# DermCalc — Technical Documentation

## Table of Contents

1. [System Overview](#1-system-overview)
2. [Data Layer](#2-data-layer)
3. [State Management](#3-state-management)
4. [Navigation](#4-navigation)
5. [UI Architecture](#5-ui-architecture)
6. [Theme System](#6-theme-system)
7. [Testing](#7-testing)
8. [CI/CD Pipeline](#8-cicd-pipeline)
9. [Build Configuration](#9-build-configuration)

---

## 1. System Overview

DermCalc is a single-Activity Jetpack Compose application. `MainActivity` owns the entire UI surface; orientation handling and feature routing are done entirely in Compose via state-driven rendering in `AppMain`. There are no Fragments.

### Rendering Layers

```
MainActivity
└── DermCalcTheme
    └── AppMain
        ├── [isOnboardingLoading = true]  → LoadingScreen
        ├── [!hasSeenOnboarding]          → OnboardingScreen  (no Scaffold chrome)
        └── [hasSeenOnboarding]           → Box
                                              ├── Background Image (dark/light variant)
                                              └── Scaffold (transparent container)
                                                  ├── TopMenu        (top bar)
                                                  ├── NavigationBar  (bottom bar)
                                                  └── AppNavHost     (content)
```

### ViewModel Wiring

All four ViewModels are created in `MainActivity` via `ViewModelProvider` + `DermCalcViewModelFactory` and passed into the composable tree as parameters. There is no Hilt or other DI framework.

```
DermCalcViewModelFactory(database, context)
├── OnboardingModel(userProfileDao, appSettingsDao)
├── ToolsModel(toolResultDao)
├── QuoteModel(application)
└── BodyScanModel(application)
```

`QuoteModel` and `BodyScanModel` extend `AndroidViewModel` to access the `Application` context for string resources. `OnboardingModel` and `ToolsModel` extend plain `ViewModel`; they receive DAOs directly.

---

## 2. Data Layer

### Room Schema (v1)

Three tables. The schema is exported to `app/schemas/` and version-tracked.

#### `app_settings`

Singleton row (`id = 1`), seeded via `RoomDatabase.Callback.onCreate`.

| Column             | Type    | Notes              |
|--------------------|---------|--------------------|
| `id`               | INTEGER | PK, always 1       |
| `isDarkTheme`      | INTEGER | Boolean (0/1)      |
| `hasSeenOnboarding`| INTEGER | Boolean (0/1)      |

#### `user_profile`

Singleton row (`id = 1`), seeded via `RoomDatabase.Callback.onCreate` with all-null/zero defaults so the profile always exists.

| Column        | Type    | Notes                                   |
|---------------|---------|-----------------------------------------|
| `id`          | INTEGER | PK, always 1                           |
| `fullName`    | TEXT    | nullable                                |
| `dateOfBirth` | INTEGER | epoch-ms via `Converters`; nullable     |
| `sex`         | TEXT    | `Sex` enum name; defaults to `"Other"`  |
| `heightCm`    | REAL    | stored in cm regardless of display unit |
| `weightKg`    | REAL    | stored in kg regardless of display unit |

#### `tool_results`

Append-only log. Ordered `DESC` by auto-generated `id`.

| Column       | Type    | Notes                                            |
|--------------|---------|--------------------------------------------------|
| `id`         | INTEGER | PK, autoincrement                                |
| `toolName`   | TEXT    | `"BMI"`, `"BSA"`, `"PASI"`, `"EASI"`           |
| `score`      | REAL    | the top-level computed score                     |
| `detailsJson`| TEXT    | full `ToolResult` serialized via `kotlinx.serialization` |

`ToolResult` subtypes are polymorphic sealed interface; `Json.encodeToString(ToolResult.serializer(), result)` produces a tagged JSON object (e.g. `{"type":"bmi","weightKg":...}`). On read, `Json.decodeFromString<ToolResult>(entity.detailsJson)` reconstructs the correct subtype. Deserialization failures are caught and the row is silently skipped.

### Type Converters

`Converters` handles `LocalDate ↔ Long` (epoch-ms, UTC midnight). Room's `@TypeConverters` annotation applies it to the full database.

### Database Access Pattern

DAOs expose `Flow<T>` for reactive reads and `suspend fun` for writes. The database uses `.fallbackToDestructiveMigration()` — schema changes in development drop and recreate all tables rather than running migration scripts.

---

## 3. State Management

### OnboardingModel

Holds a `MutableStateFlow<List<InputField>>` as the single source of truth for all five profile fields (name, DOB, sex, height, weight). Each `InputField` is an immutable data class; updates produce a new list via `map { ... copy(...) }`.

On init, `viewModelScope.launch` loads the persisted profile using `flow.first()` (a single emission, not `collect`) to avoid a Room→update→Room feedback loop.

`isFieldsInputValid(fieldIds)` gates the Next button per onboarding page. It filters the field list to the IDs declared on the current `OnboardingScreen` descriptor and asserts `!field.isRequired || field.isValid` for each.

`persistFields()` is called explicitly at meaningful boundaries (onboarding completion, profile save) — not on every keystroke.

### ToolsModel — Draft State

PASI and EASI calculations span four pages; draft state must survive navigation within the tool (back/forward paging) without being saved to the database.

```kotlin
data class IndexToolDraft<Tool : ToolResult>(
    var result: Tool? = null,
    var startPage: Int = 0,
    val values: MutableMap<Int, RegionScore> = mutableMapOf()
)
```

`_pasiDraft` and `_easiDraft` are `MutableStateFlow<IndexToolDraft<*>>`. Each `updatePasiDraft(region, score, page)` call rebuilds the full result from all four regions and stores it in `draft.result`, making the running score reactive via:

```kotlin
val pasiScore: StateFlow<Double> = _pasiDraft
    .map { it.result?.score ?: 0.0 }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0.0)
```

`savePasiDraft()` delegates to `addResult(draft.result)` which writes to Room. `resetPasiDraft()` replaces the draft with a fresh `IndexToolDraft()`.

### BodyScanModel

`BodyScanState` holds a `Map<BodyRegion, Int>` (affected percentage 0–100 per region) and a `selectedRegion`. `BsaResult` is derived via a computed property on `BodyScanState.result`, not stored separately.

`LifecycleEventEffect(ON_STOP)` in `BSAScreen` calls `vm.reset()`, clearing the diagram when the user leaves the screen.

### Theme State

Dark mode preference is held in two places: a `var isDarkTheme by remember { mutableStateOf(systemDark) }` in `MainActivity` for immediate UI response, and `app_settings.isDarkTheme` in Room for persistence. A `LaunchedEffect` collects the Room flow to sync on launch; the toggle callback writes back to Room via a coroutine.

---

## 4. Navigation

### Route Definitions

All routes are objects that implement the `AppRoute` sealed interface, annotated with `@Serializable` and `@SerialName`. Type-safe navigation uses Compose Navigation's `composable<T>()` overload.

```
AppRoute (sealed interface)
├── HomeRoute       "home"
├── ToolsRoute      "tools"
├── ProfileRoute    "profile"
├── BMIToolRoute    "bmitool"
├── BSAToolRoute    "bsatool"
├── PASIToolRoute   "pasitool"
└── EASIToolRoute   "easitool"
```

### AppNavHost

`AppNavHost` wraps `NavHost` and provides two `CompositionLocal` values to the subtree:

- `LocalNavigate` — a throttled `(AppRoute) -> Unit` that checks `lifecycleState == RESUMED` before calling `navController.navigate()`, preventing double-taps from pushing duplicate destinations.
- `LocalIsIdle` — `Boolean` exposing the same lifecycle gate to child composables (used by picker `IconButton`s to prevent opening dialogs mid-transition).

Tool screens receive dependencies as parameters, not by reading from a global. `BMIScreen` takes `heightCm`, `weightKg`, `isMetric`, `isKilos` from `onboardingModel` accessors at the call site; the screen itself is stateless with respect to the ViewModel.

### Back Navigation

The `TopMenu` back arrow calls `navController.popBackStack(ToolsRoute.route, inclusive = false)` for all calculator routes, which pops the entire tool stack to the Tools tab root rather than stepping back one page.

### `saveWithFeedback`

Extension on `NavHostController` used by BSA, PASI, and EASI:

```kotlin
private fun NavHostController.saveWithFeedback(
    onSave: () -> Boolean,
    onReset: () -> Unit
)
```

On success: calls `onReset()`, pops the back stack, shows a "Saved" Toast. On failure: shows an "Error" Toast. BMI uses its own `onSaveResult` callback that pops immediately without the toast path.

---

## 5. UI Architecture

### Screen Categories

**Quick screens** (`BMIScreen`, `BSAScreen`) follow a simple `Column` → `Card` → inputs + `AnimatedVisibility(result card)` + `ToolSaveButton` layout. State is fully local (`var heightField by remember { ... }`).

**Index screens** (`PASIScreen`, `EASIScreen`) delegate all layout concerns to `IndexToolScaffold`, passing only a `pageContent` lambda that renders the per-district sign sliders and area picker for that page index.

**`IndexToolScaffold`** owns:
- `ScafoldHeader` — district name, mini body diagram, segmented progress bar, score display, reset button
- `HorizontalPager` — user-scrollable, each page rendered by the caller's `pageContent` lambda
- Navigation row (Back / Next or Save buttons)
- `AnimatedVisibility` result card (last page only)

### Key Shared Components

**`BorderedCard`** — `Card` with a single-sided colored stroke drawn via `Modifier.drawBehind`. Supports `BorderSide.{Left, Right, Top, Bottom}`. Used for result cards, history entries, onboarding text blocks, and the quote card.

**`SnapWheelPickerDialog`** — generic `LazyColumn`-based wheel picker supporting one or more columns, optional infinite scroll, and a keyboard text-input fallback mode (toggled by a keyboard icon). Used for height and weight selection throughout onboarding, profile editing, and BMI.

**`ConfirmIconButton`** — 3-tap confirmation pattern: tap 1 → armed label, tap 2 → execute label, tap 3 → fires callback and resets. Armed state clears on `ON_STOP`. Used by `ToolSaveButton`, `ResetButton` (via `ActionConfirmDialog`), and the history row trash icon.

**`BodyScan`** — Canvas-based interactive body diagram. Hit regions are defined as `RegionDef` entries authored against a 160×290dp reference canvas and scaled at runtime via `density`. Tap detection uses `detectTapGestures`; ellipse regions (head) test containment via the ellipse equation `(dx/rx)² + (dy/ry)² ≤ 1`.

**`DermCalcPreview`** — preview wrapper composable that creates stub DAOs returning empty `MutableStateFlow`s, wires them into real ViewModels, and renders inside `DermCalcTheme`. Accepts setup lambdas for each ViewModel. Eliminates the need for `@PreviewParameter` or separate preview data classes.

---

## 6. Theme System

### Color Tokens

Two schemes: **Underground** (dark, primary experience) and **Overworld** (light, clinical environments). Token names map directly to Material 3 semantic roles; raw hex values are never used outside `Color.kt`.

| Role | Dark | Light |
|------|------|-------|
| `primary` | `#E8C547` (gold) | `#8C6B00` (muted gold) |
| `tertiary` | `#E04848` (soul red) | `#B83838` (muted red) |
| `background` | `#0E0E10` | `#FAF8F2` |
| `surface` | `#0E0E10` | `#FFFFFF` |
| `surfaceVariant` | `#18181B` | `#F1EEE5` |

### Soul Color System

Each calculator and primary destination is assigned one of the seven Undertale soul colors. A soul color claims the top-bar chrome, the active nav indicator, and the primary accent on all interactive elements on that screen.

| Soul | Color | Destination |
|------|-------|-------------|
| Determination | `#E04848` | Home, brand mark |
| Justice | `#FFC107` | Tools tab, save actions |
| Patience | `#4FB8B8` | BMI |
| Bravery | `#E5853A` | BSA |
| Integrity | `#8296E6` | PASI |
| Perseverance | `#D26CED` | EASI |
| Kindness | `#5FB85F` | Profile |

Severity badges use three of the same souls as a universal clinical scale regardless of the active screen: Kindness (mild), Justice (moderate), Determination (severe).

### Local Composition Providers

`DermCalcTheme` provides four `CompositionLocal` values:

- `LocalDarkTheme: Boolean` — consumed by components that need to branch on theme (e.g. background image selection, severity color choice)
- `LocalToggleDarkTheme: () -> Unit` — consumed by `ThemeToggleButton`
- `LocalNavigate: (AppRoute) -> Unit` — throttled nav callback, avoids prop-drilling through the screen tree
- `LocalIsIdle: Boolean` — lifecycle gate for interactive controls
- `LocalBarAlpha: Float` — 0.70f (dark) / 0.90f (light), applied to top and bottom bar container colors for a frosted-glass effect over the background image

### `onSoul` / `onSoulContainer`

Helper functions in `Theme.kt` return a contrasting surface color for content placed directly on a soul-colored background, using a luminance threshold of 0.18.

---

## 7. Testing

### Unit Tests (`app/src/test/`)

Run on JVM via Robolectric (`@RunWith(RobolectricTestRunner::class)`, `@Config(sdk = [34])`).

| File | Coverage |
|------|----------|
| `BmiResultTest` | `BmiResult.compute` boundary values; all four severity thresholds including exact boundaries |
| `InputFieldConversionTest` | `HeightInput.cmToFeetInches`, `feetInchesToCm`, `WeightInput.kilosToPounds`; `LocalDate` epoch roundtrip |
| `OnboardingModelTest` | All update methods (name, DOB, sex, height imperial/metric, weight kg/lb, unit system toggles); `isFieldsInputValid` including custom snapshot; `finishOnboarding` |
| `ToolsModelTest` | `BmiResult.compute` edge cases; `BsaResult.severity` boundaries; `formattedScore` formatting |

Fake DAOs return `MutableStateFlow` instances; tests mutate fields synchronously via the public update methods without needing `runTest` coroutine scaffolding.

### Instrumented Tests (`app/src/androidTest/`)

Run on device/emulator. Use `createAndroidComposeRule<MainActivity>` or `createComposeRule` for isolated component tests.

| File | Coverage |
|------|----------|
| `OnboardingHappyPathTest` | End-to-end: all 5 pages → bottom nav visible |
| `OnboardingValidationTest` | Next button disabled for: blank name, single-word name, valid name + no DOB, neither height nor weight, only height, only weight |
| `ToolSaveButtonTest` | 3-tap confirm sequence; disabled state; enable/disable state change |
| `AppSettingsDaoTest` | Room upsert + read; upsert updates existing row |
| `ToolResultDaoTest` | Insert, delete by id, delete all, `ORDER BY id DESC` |
| `UserProfileDaoTest` | Upsert + read; upsert updates existing row |

Test tags (`testTag`) are placed on interactive elements in production code specifically to support these tests (e.g. `"btn_next"`, `"btn_start"`, `"input_full_name"`, `"btn_open_date_picker"`, `"btn_confirm_date"`, `"btn_open_height_picker"`, `"btn_open_weight_picker"`, `"btn_confirm_picker"`, `"bottom_nav_bar"`, `"tool_btn_save"`).

---

## 8. CI/CD Pipeline

Four reusable workflow files plus one composite action, triggered by `android-pr-ci.yml` (PRs + master push) and `android-release-pipeline.yml` (nightly cron + manual dispatch).

### Shared Action: `setup-android-env`

Composite action that sets up JDK 17 (Temurin), Gradle (with build cache), Android SDK, and `chmod +x gradlew`. Invoked by all four workflows to avoid duplication.

### `android-compile.yml`

Builds the debug APK and uploads it as an artifact. Accepts `run_tests_candidate: Boolean` and `base_sha: String`; outputs `run_tests: Boolean`.

**Bytecode comparison optimization**: when `run_tests_candidate` is true and a `base_sha` is provided, the workflow checks out the base versions of changed source files, recompiles, computes a SHA-256 of the `javap -c` disassembly of all `.class` files, and compares to the HEAD hash. If the bytecode is identical, `run_tests=false` skips the test job. New files (no base equivalent) always trigger tests.

### `android-tests.yml`

Runs `testDebugUnitTest` (when `run_tests=true`) and `lintDebug` (when `run_lint=true`) in parallel jobs. Reports are uploaded as artifacts on both success and failure.

### `android-security-review.yml`

Runs OWASP Dependency-Check (`dependencyCheckAnalyze`) with `failBuildOnCVSS=7.0`. Accepts an optional `NVD_API_KEY` secret to avoid rate limiting on the NVD data feed. A `classify-failure` step distinguishes CVE failures (block the build) from NVD network errors or pre-report build issues (emit a warning, don't block).

### `android-signed-release.yml`

Triggered by `android-release-pipeline.yml` after compile + checks + security all pass. Steps:
1. Reads version string from `VERSION` file (validated as `MAJOR.MINOR.PATCH`)
2. Decodes the base64 keystore from `ANDROID_SIGNING_KEYSTORE_BASE64` secret into `$RUNNER_TEMP/signing/`
3. Builds the release APK via `assembleRelease`
4. Verifies the APK signature with `apksigner verify`
5. Uploads signed APK as artifact and publishes a GitHub Release tagged `v{VERSION}-{RUN_NUMBER}`

### Change Classification

Both CI workflows start with a `classify` job that uses `dorny/paths-filter` to determine which subsequent jobs are needed:

| Changed paths | Jobs triggered |
|---|---|
| `app/src/main/java/**`, `app/src/main/kotlin/**` | compile, tests, lint |
| `app/src/main/res/**` | compile |
| `app/src/test/**`, `app/src/androidTest/**` | compile, tests |
| `gradle/**`, `**/*.gradle.kts`, `settings.gradle.kts` | compile, security |
| `app/lint.xml`, `app/lint-baseline.xml` | compile, lint |

The release pipeline classifies changes against the last `v*` git tag; if nothing changed since the last release, `should_run=false` exits the pipeline early.

---

## 9. Build Configuration

### Versioning

The canonical version string lives in the `VERSION` file at the repo root (`MAJOR.MINOR.PATCH`). `app/build.gradle.kts` reads and validates it with a regex at configuration time; a malformed version is a Gradle build error. `versionCode` is sourced from `GITHUB_RUN_NUMBER` (CI) or defaults to `1` (local).

### APK Naming

Output filenames are overridden via `applicationVariants.all`:
```
DermCalc-{versionName}-{buildType}.apk
```

### Release Signing Guard

`assembleRelease` throws a `GradleException` at task graph evaluation time if any of the four signing environment variables are absent. This prevents accidental unsigned release builds.

### Signing Config

Signing keys are consumed via environment variables only — never hardcoded or committed. CI injects them as GitHub Actions secrets; local release builds require them to be set in the shell environment or `gradle.properties`.

### Lint Configuration

`app/lint.xml` elevates seven security-relevant rules to `error` severity, combined with `abortOnError = true` in `build.gradle.kts`:

- `HardcodedDebugMode`, `SetWorldReadable`, `SetWorldWritable`, `UnsafeNativeCodeLocation`
- `AllowBackup`, `ExportedReceiver`, `ExportedContentProvider`

`allowBackup="false"` is set in `AndroidManifest.xml` to satisfy the `AllowBackup` rule.

### OWASP Dependency-Check

Configured via the `dependencyCheck { }` block in `app/build.gradle.kts`. Suppressions go in `app/dependency-check-suppressions.xml`; each suppression entry must include a comment with the review rationale and a revisit date.
