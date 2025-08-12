# MagCode: Cross-Technology Communication for Mobile Payments

## Project Overview

MagCode is an innovative cross-technology communication system that enables data transmission between NFC readers and smartphone cameras through magnetic field interference. This technology addresses the limitation of NFC functionality in nearly half of smartphones while maintaining the security and convenience of NFC-based payments.

## Technology Background

Mobile payment has achieved explosive growth in recent years due to its contactless feature, which lowers the infection risk of COVID-19. In the market, near-field communication (NFC) and barcodes have become the de facto standard technologies for mobile payment. The NFC-based payment outperforms barcode-based payment in terms of security, usability, and convenience. It is especially more user-friendly for the amblyopia group.

Unfortunately, NFC functionality is unavailable in nearly half of smartphones in the market nowadays due to the shortage of NFC modules or being disabled for security reasons.

## MagCode Solution

At the heart of MagCode is the harmless magnetic interference on the CMOS image sensor of a smartphone placed nearby the NFC reader, resulting in a group of barcode-like stripes appearing on the captured images. We take advantage of these stripes to encode the data and achieve simplex communication from an NFC reader to an NFC-denied or NFC-disabled smartphone.

## Key Features

- **Cross-Technology Communication**: Enables communication between NFC readers and camera-equipped smartphones
- **High Security**: Maintains NFC-level security standards
- **Universal Compatibility**: Works with any smartphone equipped with a camera
- **Fast Data Transfer**: Achieves maximum throughput of 2.58 kbps
- **Quick Payment Processing**: Completes data exchange in 1.8 seconds on average
- **Accessibility**: User-friendly for visually impaired users
- **Cross-Platform Support**: Native iOS and Android implementations
- **Unified Experience**: Consistent functionality across both platforms

## Technical Specifications

- **Maximum Throughput**: 2.58 kbps (58× better than magnetometer-based solutions)
- **Data Exchange Time**: 1.8 seconds average
- **Compatibility**: Tested across 11 smart devices
- **Protocol Stack**: Complete implementation from physical to transport layer

## Development Plan

### Phase 1: Foundation & Architecture (1-2 weeks)
- [x] Create cross-platform project structure
- [x] Set up iOS project foundation
- [x] Set up Android project foundation
- [x] Implement shared algorithms and protocols
- [x] Establish development workflow

### Phase 2: Core Image Processing (2-3 weeks)
- [ ] Implement stripe detection algorithms on iOS
- [ ] Implement stripe detection algorithms on Android
- [ ] Develop cross-platform image preprocessing
- [ ] Test stripe recognition accuracy on both platforms
- [ ] Optimize performance for both platforms

### Phase 3: Data Decoding System (2-3 weeks)
- [ ] Implement data decoding on iOS
- [ ] Implement data decoding on Android
- [ ] Develop cross-platform error correction
- [ ] Optimize decoding performance
- [ ] Ensure consistency between platforms

### Phase 4: Payment Integration (2-3 weeks)
- [ ] Integrate Apple Pay on iOS
- [ ] Integrate Google Pay on Android
- [ ] Implement unified payment protocol
- [ ] Test payment functionality on both platforms
- [ ] Implement cross-platform security

### Phase 5: Optimization & Testing (1-2 weeks)
- [ ] Cross-platform performance optimization
- [ ] Platform-specific UI/UX improvements
- [ ] Cross-platform compatibility testing
- [ ] Bug fixes and refinements
- [ ] Final validation on both platforms

## Project Structure

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
└── README.md              # Main project overview
```

### Platform-Specific Structure

#### iOS Platform (`platforms/ios/`)
```
MagCode/
├── App/                   # Application entry point
├── Camera/                # Camera functionality
├── ImageProcessing/       # Image processing algorithms
├── Payment/               # Payment processing
├── Models/                # Data models
├── Utils/                 # Utility classes
└── Resources/             # App resources
```

#### Android Platform (`platforms/android/`)
```
app/src/main/
├── java/                  # Kotlin/Java source files
├── res/                   # Android resources
└── AndroidManifest.xml    # App configuration
```

## Technical Challenges & Solutions

### 1. Stripe Detection Algorithm
- **Challenge**: Processing images under various lighting conditions and camera angles
- **Solution**: Advanced computer vision algorithms with adaptive thresholding and noise filtering

### 2. Data Decoding Accuracy
- **Challenge**: Ensuring high decoding success rate with error correction
- **Solution**: Implement robust error detection and correction mechanisms with multiple validation layers

### 3. Real-time Performance
- **Challenge**: Achieving 1.8-second data exchange target
- **Solution**: Optimized image processing pipeline with efficient memory management

## Technology Stack

- **Development Language**: Swift 5.0+
- **UI Framework**: SwiftUI + UIKit
- **Image Processing**: Core Image, Vision Framework, OpenCV
- **Camera Control**: AVFoundation
- **Payment Integration**: Apple Pay, PassKit
- **Data Processing**: Core ML (optional), Core Data
- **Testing**: XCTest, UI Testing

## Requirements

- **iOS Version**: iOS 15.0+
- **Device**: iPhone with camera
- **Xcode**: Xcode 14.0+
- **Swift**: Swift 5.0+

## Getting Started

### For iOS Development
1. Clone the repository
2. Navigate to `platforms/ios/`
3. Open `MagCode.xcodeproj` in Xcode
4. Build and run the project
5. Grant camera permissions when prompted

### For Android Development
1. Clone the repository
2. Navigate to `platforms/android/`
3. Open the project in Android Studio
4. Build and run the project
5. Grant camera permissions when prompted

### Cross-Platform Development
- **Shared Resources**: Algorithms and protocols are defined in `shared/` directory
- **Consistent Implementation**: Both platforms implement the same core functionality
- **Platform-Specific Features**: Each platform leverages its native capabilities
- **Unified Testing**: Ensure consistent behavior across both platforms

## Contributing

This is an international research project. We welcome contributions from researchers and developers worldwide. Please read our contributing guidelines before submitting pull requests.

## License

[License information to be determined]

## Research Team

This project is developed as part of ongoing research in cross-technology communication and mobile payment systems.

## Contact

For research collaboration and technical questions, please contact the development team.

---

*MagCode: Bridging the gap between NFC and camera-based mobile payments through innovative magnetic field interference technology.*
