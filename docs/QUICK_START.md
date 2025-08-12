# MagCode Quick Start Guide

## 🚀 Getting Started

This guide will help you get the MagCode project up and running on your development machine in minutes.

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

- **Xcode 15.0+** (Latest version recommended)
- **iOS 15.0+** (Minimum deployment target)
- **macOS 12.0+** (For development)
- **Git** (For version control)
- **iPhone with camera** (For testing)

## 🛠️ Installation

### 1. Clone the Repository
```bash
git clone https://github.com/tagsysx/MagCode.git
cd MagCode
```

### 2. Open the Project
```bash
open MagCode.xcodeproj
```

### 3. Build and Run
- Select your target device (iPhone simulator or physical device)
- Press `Cmd + R` to build and run the project
- Grant camera permissions when prompted

## 🏗️ Project Structure

```
MagCode/
├── App/                    # Main application files
│   ├── MagCodeApp.swift   # App entry point
│   └── ContentView.swift  # Main UI view
├── Camera/                 # Camera functionality
│   ├── CameraManager.swift # Camera control and permissions
│   └── CameraView.swift   # Camera UI and capture
├── ImageProcessing/        # Core image processing
│   ├── StripeDetector.swift    # Stripe detection algorithms
│   ├── ImagePreprocessor.swift # Image enhancement
│   └── DataDecoder.swift      # Data extraction
├── Payment/                # Payment processing
│   ├── PaymentProcessor.swift # Payment logic
│   └── PaymentView.swift     # Payment UI
├── Models/                  # Data models
│   └── StripeData.swift    # Core data structures
├── Utils/                   # Utilities and extensions
│   ├── Constants.swift     # App constants
│   └── Extensions.swift    # Swift extensions
└── Resources/               # App resources
    ├── Assets.xcassets     # Images and colors
    └── Info.plist          # App configuration
```

## 🔧 Configuration

### Camera Permissions
The app requires camera access. Add these keys to `Info.plist`:

```xml
<key>NSCameraUsageDescription</key>
<string>MagCode needs camera access to detect magnetic interference stripes from NFC readers for mobile payments.</string>
```

### Apple Pay Setup
For payment functionality, configure Apple Pay in your Apple Developer account:

1. Enable Apple Pay capability in Xcode
2. Configure merchant ID
3. Add payment processing certificates

## 🧪 Testing

### Simulator Testing
- Use iPhone simulator for basic UI testing
- Camera functionality will be limited
- Payment processing can be tested with test cards

### Device Testing
- Use physical iPhone for full functionality
- Test camera and stripe detection
- Test payment processing with test cards

### Test Data
Use these sample data formats for testing:

```
PAYMENT_DATA_12345_AMOUNT_29.99_MERCHANT_STORE123
PAYMENT_DATA_67890_AMOUNT_15.50_MERCHANT_CAFE456
PAYMENT_DATA_11111_AMOUNT_99.99_MERCHANT_SHOP789
```

## 🚦 Development Workflow

### 1. Feature Development
```bash
# Create feature branch
git checkout -b feature/stripe-detection

# Make changes
# Test changes
# Commit changes
git commit -m "Add stripe detection algorithm"

# Push and create pull request
git push origin feature/stripe-detection
```

### 2. Testing
```bash
# Run unit tests
Cmd + U

# Run UI tests
Product > Test > UI Testing Bundle

# Performance testing
Product > Profile
```

### 3. Building
```bash
# Clean build
Product > Clean Build Folder

# Archive for distribution
Product > Archive
```

## 🔍 Debugging

### Common Issues

#### Camera Not Working
- Check camera permissions in Settings
- Ensure device has camera
- Verify Info.plist configuration

#### Build Errors
- Clean build folder
- Check Xcode version compatibility
- Verify Swift version (5.0+)

#### Payment Issues
- Check Apple Pay configuration
- Verify merchant ID setup
- Test with valid test cards

### Debug Tools
- **Xcode Debugger**: Set breakpoints and inspect variables
- **Instruments**: Profile performance and memory usage
- **Console**: View system logs and app output

## 📱 Deployment

### Development
- Use development provisioning profile
- Test on development devices
- Use test payment cards

### Production
- Use distribution provisioning profile
- Test on production devices
- Configure production payment processing

## 🤝 Contributing

### Code Style
- Follow Swift style guidelines
- Use meaningful variable names
- Add comments for complex logic
- Include unit tests for new features

### Pull Request Process
1. Fork the repository
2. Create feature branch
3. Make changes and test
4. Submit pull request
5. Code review and approval
6. Merge to main branch

## 📚 Resources

### Documentation
- [Apple Developer Documentation](https://developer.apple.com/documentation/)
- [Swift Programming Language](https://swift.org/documentation/)
- [iOS Human Interface Guidelines](https://developer.apple.com/design/human-interface-guidelines/)

### Related Research
- [MagCode Research Paper](https://github.com/tagsysx/MagCode)
- [NFC Technology Overview](https://developer.apple.com/documentation/corenfc)
- [Computer Vision in iOS](https://developer.apple.com/documentation/vision)

## 🆘 Support

### Getting Help
- Check existing issues on GitHub
- Create new issue with detailed description
- Contact development team
- Review project documentation

### Reporting Bugs
When reporting bugs, include:
- Device model and iOS version
- Steps to reproduce
- Expected vs actual behavior
- Screenshots or screen recordings
- Console logs if available

## 🎯 Next Steps

After getting the project running:

1. **Explore the Code**: Review the project structure and understand the architecture
2. **Run Tests**: Execute the test suite to ensure everything works
3. **Experiment**: Try different image processing parameters
4. **Contribute**: Pick an issue or feature to work on
5. **Learn**: Study the computer vision and payment processing code

## 🎉 Success!

You're now ready to contribute to the MagCode project! This innovative technology has the potential to revolutionize mobile payments by bridging the gap between NFC and camera-based systems.

Happy coding! 🚀
