# DermCalc

An Android app for dermatological score calculation and patient management, built with Jetpack Compose.

## Features

- **Patient profile** — onboarding flow collecting personal data (name, date of birth, sex), stored locally with Room
- **BMI** — weight/height input, automatic classification (underweight / normal / overweight / obese)
- **BSA** — body surface area via body region checklist (head, trunk, limbs, genitals)
- **PASI** — multi-step calculator across four districts (head, upper limbs, trunk, lower limbs) with erythema, induration, desquamation and area scoring; severity classification (Mild <5 / Moderate 5–10 / Severe >10)
- **EASI** — identical structure to PASI with adjusted parameters (erythema, oedema/papulation, excoriation, lichenification; 0–3 scale)
- **History** — chronological log of all calculations with type, value, severity and date; full history screen with deletion support when entries exceed 7

## Stack

- Kotlin + Jetpack Compose
- Navigation Compose
- ViewModel + StateFlow
- Room (KSP)
- Material 3

## Theme

DermCalc uses a custom Material 3 theme with subtle visual nods to Undertale — meaningful to those who notice, invisible to those who don't.

### Design language

- **App icon** — a red heart, readable as a medical symbol and as a soul
- **Typography** — Determination Mono (or equivalent pixel font) for titles and headers only; clean sans-serif for body text, inputs and clinical data
- **Result cards** — white border on background, echoing the battle box aesthetic
- **Severity color mapping** — intentionally aligned with Undertale's own color language:
    - Mild → green
    - Moderate → yellow (`#ffff00` toned down for readability)
    - Severe → red

### Light / Dark mode

Both modes are supported. The theme adapts as follows:

| Element | Light | Dark |
|---|---|---|
| Background | off-white | near-black (`#1a1a1a`) |
| Surface | white | dark grey |
| Primary accent | muted yellow-gold | bright yellow |
| On-primary text | black | black |
| Severity: mild | green | green |
| Severity: moderate | amber | yellow |
| Severity: severe | red | red |


Dark mode is the intended experience. Light mode is fully functional for clinical environments where a dark screen is impractical.

## Roadmap
- [X] Phase 1 — Project setup, navigation scaffold, empty screens
- [X] Phase 2 — Onboarding flow and Room profile storage
- [X] Phase 3 — Profile screen (read + edit)
- [ ] Phase 4 — Home screen (welcome, medical quote, history preview)
- [ ] Phase 5 — BMI calculator end-to-end
- [ ] Phase 6 — BSA calculator
- [ ] Phase 7 — PASI multi-step calculator
- [ ] Phase 8 — EASI multi-step calculator
- [ ] Phase 9 — Full history screen
- [ ] Phase 10 — Polish (Material 3 theming, transitions, error handling)
