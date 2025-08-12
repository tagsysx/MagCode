import Foundation
import CoreGraphics

// MARK: - App Constants
struct AppConstants {
    static let appName = "MagCode"
    static let appVersion = "1.0.0"
    static let buildNumber = "1"
    
    // App Store identifiers
    static let appStoreId = "com.magcode.app"
    static let bundleIdentifier = "com.magcode.app"
}

// MARK: - Camera Constants
struct CameraConstants {
    static let defaultResolution = CGSize(width: 1920, height: 1080)
    static let maxResolution = CGSize(width: 3840, height: 2160)
    static let minResolution = CGSize(width: 640, height: 480)
    
    // Focus and exposure settings
    static let defaultFocusDistance: Float = 0.5
    static let defaultExposureBias: Float = 0.0
    static let defaultISO: Float = 100.0
    
    // Frame rate settings
    static let defaultFrameRate: Float = 30.0
    static let maxFrameRate: Float = 60.0
    static let minFrameRate: Float = 15.0
}

// MARK: - Image Processing Constants
struct ImageProcessingConstants {
    // Stripe detection parameters
    static let defaultStripeWidth: CGFloat = 5.0
    static let minStripeWidth: CGFloat = 2.0
    static let maxStripeWidth: CGFloat = 20.0
    
    // Image quality thresholds
    static let minBrightness: Double = 0.2
    static let maxBrightness: Double = 0.8
    static let minContrast: Double = 0.4
    static let minSharpness: Double = 0.5
    static let maxNoiseLevel: Double = 0.3
    
    // Processing timeouts
    static let imagePreprocessingTimeout: TimeInterval = 2.0
    static let stripeDetectionTimeout: TimeInterval = 3.0
    static let dataDecodingTimeout: TimeInterval = 2.0
    static let totalProcessingTimeout: TimeInterval = 8.0
    
    // Algorithm parameters
    static let edgeDetectionThreshold: Double = 0.1
    static let lineDetectionThreshold: Double = 0.8
    static let patternMatchingThreshold: Double = 0.7
}

// MARK: - Payment Constants
struct PaymentConstants {
    // Supported currencies
    static let supportedCurrencies = ["USD", "EUR", "GBP", "JPY", "CNY"]
    static let defaultCurrency = "USD"
    
    // Payment limits
    static let minPaymentAmount: Double = 0.01
    static let maxPaymentAmount: Double = 10000.0
    
    // Transaction timeouts
    static let paymentAuthorizationTimeout: TimeInterval = 60.0
    static let transactionTimeout: TimeInterval = 300.0
    
    // Apple Pay configuration
    static let applePayMerchantId = "merchant.com.magcode.app"
    static let applePaySupportedNetworks = ["visa", "masterCard", "amex", "discover"]
}

// MARK: - UI Constants
struct UIConstants {
    // Colors
    static let primaryColor = "PrimaryColor"
    static let secondaryColor = "SecondaryColor"
    static let accentColor = "AccentColor"
    static let backgroundColor = "BackgroundColor"
    static let errorColor = "ErrorColor"
    static let successColor = "SuccessColor"
    
    // Spacing
    static let smallSpacing: CGFloat = 8.0
    static let mediumSpacing: CGFloat = 16.0
    static let largeSpacing: CGFloat = 24.0
    static let extraLargeSpacing: CGFloat = 32.0
    
    // Corner radius
    static let smallCornerRadius: CGFloat = 8.0
    static let mediumCornerRadius: CGFloat = 12.0
    static let largeCornerRadius: CGFloat = 16.0
    
    // Animation durations
    static let shortAnimationDuration: Double = 0.2
    static let mediumAnimationDuration: Double = 0.3
    static let longAnimationDuration: Double = 0.5
}

// MARK: - Error Messages
struct ErrorMessages {
    static let cameraPermissionDenied = "Camera access is required for MagCode to function. Please enable camera permissions in Settings."
    static let cameraSetupFailed = "Failed to setup camera. Please try again."
    static let imageProcessingFailed = "Failed to process image. Please try again."
    static let stripeDetectionFailed = "No magnetic stripes detected. Please position your camera closer to the NFC reader."
    static let dataDecodingFailed = "Failed to decode data from detected stripes. Please try again."
    static let paymentProcessingFailed = "Payment processing failed. Please try again."
    static let networkError = "Network connection error. Please check your internet connection."
    static let unknownError = "An unknown error occurred. Please try again."
}

// MARK: - Success Messages
struct SuccessMessages {
    static let cameraReady = "Camera is ready to scan"
    static let stripesDetected = "Magnetic stripes detected successfully"
    static let dataDecoded = "Data decoded successfully"
    static let paymentAuthorized = "Payment authorized successfully"
    static let paymentCompleted = "Payment completed successfully"
}

// MARK: - Validation Constants
struct ValidationConstants {
    // Data validation
    static let minDataLength = 10
    static let maxDataLength = 1000
    static let minConfidence = 0.7
    static let maxConfidence = 1.0
    
    // Time validation
    static let maxDataAge: TimeInterval = 300.0 // 5 minutes
    static let maxProcessingTime: TimeInterval = 10.0 // 10 seconds
    
    // Image validation
    static let minImageWidth: CGFloat = 320.0
    static let minImageHeight: CGFloat = 240.0
    static let maxImageSize: Int64 = 50 * 1024 * 1024 // 50MB
}

// MARK: - Performance Constants
struct PerformanceConstants {
    // Target performance metrics
    static let targetProcessingTime: TimeInterval = 1.8 // 1.8 seconds as per research
    static let targetThroughput: Double = 2.58 // 2.58 kbps as per research
    
    // Memory limits
    static let maxMemoryUsage: Int64 = 100 * 1024 * 1024 // 100MB
    static let maxImageCacheSize: Int64 = 50 * 1024 * 1024 // 50MB
    
    // Battery optimization
    static let maxProcessingDuration: TimeInterval = 30.0 // 30 seconds max
    static let idleTimeout: TimeInterval = 60.0 // 1 minute idle timeout
}
