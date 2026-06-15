# DermCalc

An Android app for dermatological score calculation and patient management, built with Jetpack Compose.

## Features

- **Patient profile** — onboarding flow collecting personal data (name, date of birth, sex); held in-memory for the session (Room dependency included, persistence not yet implemented)
- **BMI** — weight/height input, automatic classification (underweight / normal / overweight / obese)
- **BSA** — body surface area via body region checklist (head, trunk, limbs, genitals)
- **PASI** — multi-step calculator across four districts (head, upper limbs, trunk, lower limbs) with erythema, induration, desquamation and area scoring; severity classification (Mild <10 / Moderate 10–20 / Severe ≥20)
- **EASI** — identical structure to PASI with adjusted parameters (erythema, oedema/papulation, excoriation, lichenification; 0–3 scale)
- **History** — chronological log of all calculations with type, value, severity and date; full history screen with deletion support when entries exceed 7

## Stack

- Kotlin + Jetpack Compose
- Navigation Compose
- ViewModel + StateFlow
- Room (KSP)
- Material 3

## Architecture

### Entry point & orientation

`MainActivity` detects portrait/landscape and delegates to `MainPortraitActivity` or `MainLandscapeActivity` (landscape is a stub). Portrait side observes `OnboardingModel.hasSeenOnboarding` and gates between `OnboardingScreen` and the main `Scaffold`.

### ViewModels

| ViewModel         | Responsibility                                                                                                                         |
|-------------------|----------------------------------------------------------------------------------------------------------------------------------------|
| `OnboardingModel` | Owns `List<InputField>` state flow; all values normalised to SI (cm, kg) on write; gates the onboarding pager via `isFieldsInputValid` |
| `ToolsModel`      | In-memory `List<ToolResult>` state flow; `addResult` validates before appending; `deleteResult` removes by equality                    |
| `QuoteModel`      | Serves a single random dermatology quote; must be explicitly refreshed via `updateQuote()`                                             |

### App startup sequence

```mermaid
sequenceDiagram
    participant MA as MainActivity
    participant MPA as MainPortraitActivity
    participant OM as OnboardingModel
    participant ONB as OnboardingScreen
    participant NH as AppNavHost

    MA->>MPA: portrait detected
    MPA->>OM: observe hasSeenOnboarding
    OM-->>MPA: false
    MPA->>ONB: render onboarding pager
    ONB->>OM: updateName / updateDateOfBirth / updateHeight / updateWeight
    OM-->>ONB: isFieldsInputValid → true (gates Next button)
    ONB->>OM: finishOnboarding()
    OM-->>MPA: hasSeenOnboarding = true
    MPA->>NH: render Scaffold + AppNavHost
    NH-->>MPA: HomeRoute (initial destination)
```

### Calculator flow (addResult)

```mermaid
sequenceDiagram
    participant UI as ToolScreen
    participant TM as ToolsModel
    participant TR as ToolResult

    UI->>TR: construct PasiResult / BmiResult / …
    UI->>TM: addResult(result)
    TM->>TR: isValid()
    alt valid
        TR-->>TM: true
        TM->>TM: _results.update { it + result }
        TM-->>UI: returns true
    else invalid
        TR-->>TM: false
        TM-->>UI: returns false (not stored)
    end
    TM-->>UI: toolsResult StateFlow updated
```

### Onboarding state machine

```mermaid
stateDiagram-v2
    [*] --> AwaitingName
    AwaitingName --> AwaitingDOB : name ≥ 2 words
    AwaitingDOB --> AwaitingSex : date valid (1900 < dob ≤ today)
    AwaitingSex --> AwaitingMeasurements : sex selected (optional)
    AwaitingMeasurements --> Complete : height + weight in range
    Complete --> [*] : finishOnboarding()

    AwaitingName --> AwaitingName : invalid input (Next disabled)
    AwaitingDOB --> AwaitingDOB : future date / pre-1900 (Next disabled)
    AwaitingMeasurements --> AwaitingMeasurements : out-of-range values (Next disabled)
```

### Severity thresholds

| Tool | Mild    | Moderate | Severe         |
|------|---------|----------|----------------|
| PASI | < 10    | 10–20    | ≥ 20           |
| EASI | < 7     | 7–21     | ≥ 21           |
| BMI  | 18.5–25 | 25–30    | < 18.5 or ≥ 30 |
| BSA  | < 10 %  | 10–30 %  | ≥ 30 %         |

### Package layout

```
it.lcavagnari.pdm.dermcalc
├── MainActivity.kt
├── models/           — InputField, OnboardingModel, QuoteModel, ToolsModel/ToolResult, AppRoute
├── utils/            — DateUtils (today() via kotlinx.datetime)
└── ui/
    ├── theme/        — Color, Type, Theme (Material3), LocalDarkTheme, LocalToggleDarkTheme
    ├── landscape/    — MainLandscapeActivity (stub)
    ├── shared/
    │   ├── navigation/   — AppNavHost, BottomNavBar
    │   └── component/    — SnapWheelPicker, DatePicker, BorderedCard, TopMenu, ButtonsTray, HistoryCard
    └── portrait/
        ├── MainPortraitActivity.kt
        ├── screens/      — HomeScreen, ToolsScreen, ProfileRoute, OnboardingScreen
        └── onboarding/   — OnboardingPager, OnboardingItem
```

## API Reference

Full KDoc — classes, functions, parameters, and cross-references — is published at:

**[javacode-docsvault.vercel.app/projects/dermcalc/index.html](https://javacode-docsvault.vercel.app/projects/dermcalc/index.html)**

[![KDoc wiki preview](docs/kdoc-preview.png)](https://javacode-docsvault.vercel.app/projects/dermcalc/index.html)

Generated via Dokka. To rebuild locally: `.\gradlew dokkaHtml` → `app/build/dokka/html/`.

## Theme

DermCalc uses a custom Material 3 theme with subtle visual nods to Undertale — meaningful to those who notice, invisible to those who don't.

### Design language

- **App icon** — a red heart, readable as a medical symbol and as a soul
- **Typography** — Determination Mono used throughout the full type scale (display → label); JetBrains Mono available for clinical score readouts
- **Result cards** — white border on background, echoing the battle box aesthetic
- **Severity color mapping** — intentionally aligned with Undertale's own color language:
  - Mild → green
  - Moderate → yellow (`#ffff00` toned down for readability)
  - Severe → red

### Light / Dark mode

Both modes are supported. The theme adapts as follows:

| Element            | Light             | Dark                   |
|--------------------|-------------------|------------------------|
| Background         | off-white         | near-black (`#1a1a1a`) |
| Surface            | white             | dark grey              |
| Primary accent     | muted yellow-gold | bright yellow          |
| On-primary text    | black             | black                  |
| Severity: mild     | green             | green                  |
| Severity: moderate | amber             | yellow                 |
| Severity: severe   | red               | red                    |


Dark mode is the intended experience. Light mode is fully functional for clinical environments where a dark screen is impractical.

## Roadmap
- [X] Phase 1 — Project setup, navigation scaffold, empty screens
- [X] Phase 2 — Onboarding flow
- [X] Phase 3 — Profile screen (read + edit)
- [X] Phase 4 — Home screen (welcome, medical quote, history preview)
- [x] Phase 5 — BMI calculator end-to-end & BSA calculator
- [x] Phase 6 — PASI multi-step calculator
- [x] Phase 7 — EASI multi-step calculator
- [x] Phase 8 — Room profile & result storage
- [x] Phase 9 — Full history screen
- [x] Phase 10 — Polish (Material 3 theming, transitions, error handling)
