# 快速上手（Android）

## 环境
- Android Studio 2023+
- JDK 11+
- Android SDK 合适的 API 级别

## 打开项目
1. 打开路径：`MagCode-plus/platforms/android`
2. 同步 Gradle
3. 选择 `app` 运行目标并启动

## 命令行构建（可选）
```bat
cd platforms\android
gradlew.bat assembleDebug
```

产物路径：`platforms/android/app/build/outputs/apk/debug/app-debug.apk`

## 模型文件（可选）
如需离线测试，将模型文件放入：`app/src/main/assets/`。
请勿将模型文件提交到仓库（已由 `.gitignore` 忽略）。
