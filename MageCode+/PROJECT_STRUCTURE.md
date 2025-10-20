# Project Structure (MageCode+)

This is a lightweight Android-only structure modeled after `MagCode-main`. It keeps only essential configuration and source files, avoiding large build artifacts and models.

```
MageCode+/
├── platforms/
│   └── android/
│       ├── app/
│       │   ├── src/main/
│       │   │   ├── java/…            # Kotlin sources (Compose)
│       │   │   ├── res/…             # Resources
│       │   │   └── AndroidManifest.xml
│       │   ├── build.gradle.kts
│       │   └── proguard-rules.pro
│       ├── settings.gradle.kts
│       ├── build.gradle.kts
│       ├── gradle.properties
│       ├── gradle/…, gradlew, gradlew.bat (optional for CLI builds)
│       └── (build directories and large assets are ignored by root .gitignore)
├── docs/
│   ├── README.md
│   └── QUICK_START.md
├── shared/
│   ├── algorithms/
│   │   └── ALGORITHMS_OVERVIEW.md
│   └── protocols/
│       └── PAYMENT_PROTOCOL_SPEC.md
├── .gitignore
└── LICENSE / README.md
```


