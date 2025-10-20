# Quick Start (Android)

## Requirements
- Android Studio 2023+
- JDK 11+
- Appropriate Android SDK API level

## Open the Project
1. Open `MagCode-plus/platforms/android` in Android Studio
2. Let Gradle sync
3. Select the `app` run configuration and run

## Command Line Build (optional)
```bat
cd platforms\android
gradlew.bat assembleDebug
```

Output: `platforms/android/app/build/outputs/apk/debug/app-debug.apk`

## Models (optional)
For local tests, place model files under: `app/src/main/assets/`.
Do not commit models to the repository (root `.gitignore` already ignores them).
