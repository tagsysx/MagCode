# MagCode-plus (Android)

MagCode+ is an extension of MagCode. It converts the camera-produced magnetic barcode into a standard EAN‑13 code that can be scanned and decoded by conventional barcode scanners.


- Platform path: `platforms/android/`
- Only source and required configs are kept; build outputs and large model files are ignored via the root `.gitignore`
- UI is built with Jetpack Compose (no XML)

## Directory Structure
```
MagCode-plus/
├── platforms/
│   └── android/
│       ├── app/
│       │   ├── src/main/
│       │   │   ├── java/…               # Kotlin sources (Compose)
│       │   │   ├── res/…                # Resources
│       │   │   └── AndroidManifest.xml
│       │   ├── build.gradle.kts
│       │   └── proguard-rules.pro
│       ├── gradle/…                      # Gradle wrapper (optional but recommended)
│       ├── settings.gradle.kts
│       ├── build.gradle.kts
│       ├── gradle.properties
│       ├── gradlew / gradlew.bat
│       └── (build artifacts and large assets are ignored by root .gitignore)
├── docs/
│   ├── README.md
│   └── QUICK_START.md
├── shared/
│   ├── algorithms/
│   │   └── ALGORITHMS_OVERVIEW.md
│   └── protocols/
│       └── PAYMENT_PROTOCOL_SPEC.md
├── .gitignore
└── README.md (this file)
```

## Build & Run
1) Open `MagCode-plus/platforms/android` in Android Studio
2) Sync Gradle and select the `app` configuration
3) Or build via command line:
```bat
cd platforms\android
gradlew.bat assembleDebug
```
APK output: `platforms/android/app/build/outputs/apk/debug/app-debug.apk`

## Notes
- Build outputs and large model files are not committed (see root `.gitignore`)
- For local testing, place models under `app/src/main/assets/` but do not commit them
- All UI is Jetpack Compose; no XML layouts

## License
MIT License. See `LICENSE` at the repository root.
