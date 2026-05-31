# Repository Guidelines

## Project Structure & Module Organization
`shared/` contains the Kotlin Multiplatform code used by both apps. Put shared UI, state, DI, and business logic under `shared/src/commonMain/kotlin/pl/chaoticorder/boingball`, and keep platform-specific implementations in `androidMain/` and `iosMain/`.

`androidApp/` is the Android entry point, including `MainActivity`, app wiring, and Android resources under `src/main/res`. `iosApp/` contains the Xcode project, SwiftUI entry files, and iOS assets. Shared fonts, audio, strings, and images live in `shared/src/commonMain/composeResources`.

## Build, Test, and Development Commands
Use the Gradle wrapper from the repository root.

- `./gradlew :androidApp:assembleDebug` builds the Android debug APK.
- `./gradlew :shared:build` compiles the shared module and runs its tests.
- `./gradlew :shared:testAndroidHostTest` runs Android host-side tests for shared code.
- `./gradlew :shared:iosSimulatorArm64Test` runs iOS simulator tests for the shared module.
- `./gradlew :shared:check` runs the standard verification tasks.

Run the iOS app from Xcode by opening `iosApp/iosApp.xcodeproj`.

## Coding Style & Naming Conventions
Follow Kotlin defaults: 4-space indentation, no tabs, and one top-level class or screen per file when practical. Use `UpperCamelCase` for types (`BoingBallViewModel`), `lowerCamelCase` for functions and properties, and keep package names lowercase under `com.rff.boingballdemo`.

Compose screens, state, and action types already follow `FeatureScreen`, `FeatureState`, and `FeatureAction`; keep new code consistent with that pattern. No formatter or linter is configured in this repository, so keep imports tidy and match surrounding code style.

## Testing Guidelines
Tests use `kotlin.test`. Place cross-platform tests in `shared/src/commonTest`, Android-specific tests in `shared/src/androidHostTest`, and iOS-specific tests in `shared/src/iosTest`. Name test classes after the unit under test, and use descriptive test function names such as `fun savesPreferences()` instead of generic `example()`.

## Commit & Pull Request Guidelines
Git history is minimal and uses short, lowercase subjects (`initial version`). Keep commit messages brief, imperative, and focused on one change, for example `add preferences persistence`.

Pull requests should summarize behavior changes, list tested platforms, and attach screenshots for UI changes on Android and iOS. Link the relevant issue when one exists.
