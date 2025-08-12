# MagCode: Cross-Technology Communication for Mobile Payments

[![iOS](https://img.shields.io/badge/iOS-15.0+-blue.svg)](https://developer.apple.com/ios/)
[![Android](https://img.shields.io/badge/Android-7.0+-green.svg)](https://developer.android.com/)
[![Swift](https://img.shields.io/badge/Swift-5.0+-orange.svg)](https://swift.org/)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-purple.svg)](https://kotlinlang.org/)
[![Xcode](https://img.shields.io/badge/Xcode-15.0+-green.svg)](https://developer.apple.com/xcode/)
[![Android Studio](https://img.shields.io/badge/Android%20Studio-2023+-yellow.svg)](https://developer.android.com/studio)

> ⚠️ **DEVELOPMENT STATUS: WORK IN PROGRESS** ⚠️
> 
> **This project is currently under active development and is NOT ready for production use.**
> 
> - 🚧 **iOS Platform**: Basic structure complete, core algorithms in development
> - 🚧 **Android Platform**: Project setup complete, implementation in progress
> - 🚧 **Core Features**: Research phase complete, implementation ongoing
> - 🚧 **Testing**: Limited testing completed, comprehensive testing planned
> 
> **Expected Release**: Q2 2025 (Tentative)
> 
> *Please note that this is a research project and the current implementation is for development and testing purposes only.*

## 🎯 Project Overview

MagCode is an innovative cross-technology communication system that enables data transmission between NFC readers and smartphone cameras through magnetic field interference. This technology addresses the limitation of NFC functionality in nearly half of smartphones while maintaining the security and convenience of NFC-based payments.

> **Note**: This project is currently in the research and development phase. The technology has been proven in laboratory conditions, but the mobile applications are still under development.

## ✨ Key Features

- **Cross-Technology Communication**: Enables communication between NFC readers and camera-equipped smartphones
- **High Security**: Maintains NFC-level security standards
- **Universal Compatibility**: Works with any smartphone equipped with a camera
- **Fast Data Transfer**: Achieves maximum throughput of 2.58 kbps
- **Quick Payment Processing**: Completes data exchange in 1.8 seconds on average
- **Accessibility**: User-friendly for visually impaired users
- **Cross-Platform Support**: Native iOS and Android implementations

## 🏗️ Project Structure

```
MagCode/
├── platforms/              # Platform-specific implementations
│   ├── ios/               # iOS application (Swift/SwiftUI)
│   │   ├── MagCode/       # iOS source code
│   │   └── MagCode.xcodeproj
│   └── android/           # Android application (Kotlin/Jetpack Compose)
│       ├── app/           # Android source code
│       └── build.gradle   # Build configuration
├── shared/                 # Shared resources and configurations
│   ├── Package.swift      # Swift Package Manager configuration
│   ├── algorithms/        # Cross-platform algorithms
│   └── protocols/         # Protocol specifications
├── docs/                   # Project documentation
├── research/               # Research materials and papers
└── README.md              # This file
```

## 🔧 Technology Stack

### iOS Platform
- **Development Language**: Swift 5.0+
- **UI Framework**: SwiftUI + UIKit
- **Image Processing**: Core Image, Vision Framework
- **Camera Control**: AVFoundation
- **Payment Integration**: Apple Pay, PassKit
- **Target**: iOS 15.0+

### Android Platform
- **Development Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Image Processing**: OpenCV, ML Kit
- **Camera Control**: CameraX
- **Payment Integration**: Google Pay
- **Target**: Android API 24+ (Android 7.0+)

## 🚀 Getting Started

> ⚠️ **IMPORTANT**: This project is under development and may have incomplete features, bugs, or breaking changes. It's intended for developers and researchers who want to contribute to or study the project.

### Prerequisites

- **iOS Development**: Xcode 15.0+, macOS 12.0+
- **Android Development**: Android Studio 2023+, JDK 17+
- **Git**: For version control
- **Device**: iPhone or Android device with camera for testing
- **Patience**: This is a research project with ongoing development

### Quick Start

#### For iOS Development
```bash
git clone https://github.com/tagsysx/MagCode.git
cd MagCode/platforms/ios
open MagCode.xcodeproj
```

#### For Android Development
```bash
git clone https://github.com/tagsysx/MagCode.git
cd MagCode/platforms/android
# Open in Android Studio
./gradlew build
```

### Development Setup

1. **Clone Repository**:
```bash
git clone https://github.com/tagsysx/MagCode.git
cd MagCode
```

2. **Choose Platform**:
   - **iOS**: Navigate to `platforms/ios/` and open `MagCode.xcodeproj`
   - **Android**: Navigate to `platforms/android/` and open in Android Studio

3. **Install Dependencies**:
   - **iOS**: Dependencies managed through Xcode
   - **Android**: Dependencies managed through Gradle

4. **Configure Development Environment**:
   - Set up development team (iOS)
   - Configure signing (Android)
   - Enable payment capabilities (Apple Pay/Google Pay)

## 📱 Usage

### Core Workflow
1. **Launch App**: Open MagCode on your device
2. **Camera Access**: Grant camera permissions when prompted
3. **Position Device**: Place smartphone near NFC reader
4. **Detect Stripes**: App automatically detects magnetic interference patterns
5. **Process Payment**: Complete transaction using native payment system

### Platform-Specific Features
- **iOS**: Apple Pay integration, Core Image processing
- **Android**: Google Pay integration, OpenCV processing

## 🔬 Technology Background

Mobile payment has achieved explosive growth in recent years due to its contactless feature, which lowers the infection risk of COVID-19. In the market, near-field communication (NFC) and barcodes have become the de facto standard technologies for mobile payment.

The NFC-based payment outperforms barcode-based payment in terms of security, usability, and convenience. It is especially more user-friendly for the amblyopia group. Unfortunately, NFC functionality is unavailable in nearly half of smartphones in the market nowadays due to the shortage of NFC modules or being disabled for security reasons.

## 💡 MagCode Solution

At the heart of MagCode is the harmless magnetic interference on the CMOS image sensor of a smartphone placed nearby the NFC reader, resulting in a group of barcode-like stripes appearing on the captured images. We take advantage of these stripes to encode the data and achieve simplex communication from an NFC reader to an NFC-denied or NFC-disabled smartphone.

## 📊 Technical Specifications

- **Maximum Throughput**: 2.58 kbps (58× better than magnetometer-based solutions)
- **Data Exchange Time**: 1.8 seconds average
- **Compatibility**: Tested across 11 smart devices
- **Protocol Stack**: Complete implementation from physical to transport layer
- **Processing Time**: ≤ 1.8 seconds
- **Accuracy**: ≥ 95% stripe detection rate
- **Memory Usage**: ≤ 100MB
- **Battery Impact**: ≤ 5% per transaction

## 🛠️ Development

### Project Structure
- **Platform-Specific Code**: Each platform has its own directory with native implementations
- **Shared Resources**: Algorithms, protocols, and configurations shared across platforms
- **Documentation**: Comprehensive guides for both platforms
- **Research Integration**: Scientific background directly connected to implementation

### Development Workflow
1. **Algorithm Design**: Create specifications in `shared/algorithms/`
2. **Protocol Definition**: Define protocols in `shared/protocols/`
3. **Platform Implementation**: Implement on iOS and Android
4. **Testing & Validation**: Ensure consistency across platforms
5. **Documentation**: Update shared documentation

### Testing Strategy
- **Unit Testing**: Platform-specific test suites
- **Integration Testing**: Cross-platform functionality validation
- **Performance Testing**: Consistent benchmarks across platforms
- **User Acceptance Testing**: Real-world scenario validation

## 🤝 Contributing

This is an international research project. We welcome contributions from researchers and developers worldwide.

### How to Contribute
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

### Development Areas
- **iOS Development**: Swift, SwiftUI, Core Image, Vision
- **Android Development**: Kotlin, Jetpack Compose, CameraX, ML Kit
- **Algorithm Research**: Computer vision, pattern recognition
- **Payment Integration**: Apple Pay, Google Pay, security
- **Documentation**: User guides, technical documentation

## 📚 Documentation

- **[Project Structure](PROJECT_STRUCTURE.md)**: Comprehensive project organization guide
- **[Detailed Project Overview](docs/README.md)**: In-depth project information
- **[Development Plan](docs/DEVELOPMENT_PLAN.md)**: 13-week development roadmap
- **[Quick Start Guide](docs/QUICK_START.md)**: Developer setup instructions
- **[Project Summary](docs/PROJECT_SUMMARY.md)**: Current status and achievements
- **[Research Background](research/RESEARCH_BACKGROUND.md)**: Scientific foundation
- **[Stripe Detection Spec](shared/algorithms/STRIPE_DETECTION_SPEC.md)**: Algorithm specifications
- **[Payment Protocol Spec](shared/protocols/PAYMENT_PROTOCOL_SPEC.md)**: Protocol specifications

## 🔗 Links

- **GitHub Repository**: [https://github.com/tagsysx/MagCode](https://github.com/tagsysx/MagCode)
- **Research Paper**: [MagCode: NFC-Enabled Barcodes for
NFC-Disabled Smartphones](https://web.comp.polyu.edu.hk/csyanglei/data/files/magcode-mobicom23.pdf)
- **iOS App**: Available in `platforms/ios/`
- **Android App**: Available in `platforms/android/`

## 📄 License

[License information to be determined]

## 👥 Research Team

This project is developed as part of ongoing research in cross-technology communication and mobile payment systems.

## 📞 Contact

For research collaboration and technical questions, please contact the development team.

## 🎯 Development Status

> 🚧 **CURRENT STATUS**: This project is actively being developed and is NOT production-ready.

- **Phase 1**: ✅ Foundation & Architecture (Complete)
- **Phase 2**: 🔄 Core Image Processing (In Progress - ~30% Complete)
- **Phase 3**: ⏳ Data Decoding System (Planned - Not Started)
- **Phase 4**: ⏳ Payment Integration (Planned - Not Started)
- **Phase 5**: ⏳ Optimization & Testing (Planned - Not Started)

**Overall Progress**: ~15% Complete
**Next Milestone**: Basic stripe detection working on both platforms
**Estimated Completion**: Q2 2025 (Research and Development Phase)

## 🌟 Innovation Highlights

### Technical Innovation
1. **Cross-Technology Communication**: Bridges NFC and camera technologies
2. **Magnetic Field Interference**: Novel use of CMOS sensor interference
3. **Real-time Processing**: Sub-2-second data exchange capability
4. **Universal Compatibility**: Works with any camera-equipped smartphone

### Research Impact
- Addresses real-world NFC limitations
- Maintains NFC-level security standards
- Improves accessibility for visually impaired users
- Enables new payment scenarios and use cases

---

*MagCode: Bridging the gap between NFC and camera-based mobile payments through innovative magnetic field interference technology.*

*Last Updated: January 2025*
*Project Status: Cross-Platform Development Ready*
