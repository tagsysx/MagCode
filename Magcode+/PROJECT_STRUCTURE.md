# 项目结构说明（MagCode-plus）

参照 `MagCode-main` 的目录模式，对 Android 子项目进行轻量化整理，便于托管：

```
MagCode-plus/
├── platforms/
│   └── android/
│       ├── app/
│       │   ├── src/main/
│       │   │   ├── java/…            # Kotlin 源码（Compose）
│       │   │   ├── res/…             # 资源
│       │   │   └── AndroidManifest.xml
│       │   ├── build.gradle.kts
│       │   └── proguard-rules.pro
│       ├── settings.gradle.kts
│       ├── build.gradle.kts
│       ├── gradle.properties
│       ├── gradle/…、gradlew、gradlew.bat（可选，便于命令行构建）
│       └── （build 目录等产物与大文件由根 .gitignore 忽略）
├── docs/
│   ├── README.md
│   └── QUICK_START.md
├── shared/
│   └── algorithms/（算法规范/伪代码/实验记录）
├── .gitignore
└── LICENSE / README.md
```

对比 `MagCode-main/platforms/android`：仅保留必要的小文件与配置，避免提交体积巨大的构建产物与模型文件。
