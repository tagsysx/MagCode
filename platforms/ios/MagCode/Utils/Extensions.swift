import Foundation
import SwiftUI
import CoreImage
import UIKit

// MARK: - Color Extensions
extension Color {
    static let primaryColor = Color("PrimaryColor")
    static let secondaryColor = Color("SecondaryColor")
    static let accentColor = Color("AccentColor")
    static let backgroundColor = Color("BackgroundColor")
    static let errorColor = Color("ErrorColor")
    static let successColor = Color("SuccessColor")
}

// MARK: - View Extensions
extension View {
    func primaryButtonStyle() -> some View {
        self
            .foregroundColor(.white)
            .frame(maxWidth: .infinity)
            .padding()
            .background(Color.blue)
            .cornerRadius(12)
    }
    
    func secondaryButtonStyle() -> some View {
        self
            .foregroundColor(.blue)
            .frame(maxWidth: .infinity)
            .padding()
            .background(Color.blue.opacity(0.1))
            .cornerRadius(12)
    }
    
    func cardStyle() -> some View {
        self
            .padding()
            .background(Color.gray.opacity(0.1))
            .cornerRadius(12)
    }
    
    func hideKeyboard() {
        UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
    }
}

// MARK: - String Extensions
extension String {
    var isValidEmail: Bool {
        let emailRegex = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}"
        let emailPredicate = NSPredicate(format: "SELF MATCHES %@", emailRegex)
        return emailPredicate.evaluate(with: self)
    }
    
    var isValidAmount: Bool {
        guard let amount = Double(self) else { return false }
        return amount > 0 && amount <= 10000
    }
    
    var isNumeric: Bool {
        return !isEmpty && rangeOfCharacter(from: CharacterSet.decimalDigits.inverted) == nil
    }
    
    func truncate(to length: Int) -> String {
        if self.count <= length {
            return self
        }
        return String(self.prefix(length)) + "..."
    }
    
    func extractAmount() -> Double? {
        let components = self.components(separatedBy: "_")
        for component in components {
            if component.hasPrefix("AMOUNT") {
                let amountString = component.replacingOccurrences(of: "AMOUNT", with: "")
                return Double(amountString)
            }
        }
        return nil
    }
    
    func extractMerchantId() -> String? {
        let components = self.components(separatedBy: "_")
        for component in components {
            if component.hasPrefix("MERCHANT") {
                return component.replacingOccurrences(of: "MERCHANT", with: "")
            }
        }
        return nil
    }
}

// MARK: - Double Extensions
extension Double {
    var formattedCurrency: String {
        return String(format: "%.2f", self)
    }
    
    var formattedPercentage: String {
        return String(format: "%.1f%%", self * 100)
    }
    
    var isPositive: Bool {
        return self > 0
    }
    
    var isNegative: Bool {
        return self < 0
    }
    
    var isZero: Bool {
        return self == 0
    }
}

// MARK: - Date Extensions
extension Date {
    var timeAgo: String {
        let formatter = RelativeDateTimeFormatter()
        formatter.unitsStyle = .abbreviated
        return formatter.localizedString(for: self, relativeTo: Date())
    }
    
    var formattedTime: String {
        let formatter = DateFormatter()
        formatter.timeStyle = .short
        return formatter.string(from: self)
    }
    
    var formattedDate: String {
        let formatter = DateFormatter()
        formatter.dateStyle = .medium
        return formatter.string(from: self)
    }
    
    var isToday: Bool {
        return Calendar.current.isDateInToday(self)
    }
    
    var isExpired: Bool {
        return self < Date()
    }
}

// MARK: - Array Extensions
extension Array where Element == StripePattern {
    var averageWidth: CGFloat {
        guard !isEmpty else { return 0 }
        let totalWidth = reduce(0) { $0 + $1.width }
        return totalWidth / CGFloat(count)
    }
    
    var averageIntensity: Double {
        guard !isEmpty else { return 0 }
        let totalIntensity = reduce(0) { $0 + $1.intensity }
        return totalIntensity / Double(count)
    }
    
    var sortedByIntensity: [StripePattern] {
        return sorted { $0.intensity > $1.intensity }
    }
    
    var sortedByWidth: [StripePattern] {
        return sorted { $0.width > $1.width }
    }
}

// MARK: - CIImage Extensions
extension CIImage {
    var averageBrightness: Double? {
        let extent = self.extent
        let inputExtent = CIVector(x: extent.origin.x, y: extent.origin.y, z: extent.size.width, w: extent.size.height)
        
        guard let filter = CIFilter(name: "CIAreaAverage", parameters: [kCIInputImageKey: self, kCIInputExtentKey: inputExtent]) else {
            return nil
        }
        
        guard let outputImage = filter.outputImage else { return nil }
        
        let context = CIContext()
        let data = context.createCGImage(outputImage, from: outputImage.extent)
        
        guard let cgImage = data else { return nil }
        
        let width = cgImage.width
        let height = cgImage.height
        let bytesPerPixel = 4
        let bytesPerRow = bytesPerPixel * width
        let bitsPerComponent = 8
        let colorSpace = CGColorSpaceCreateDeviceRGB()
        let bitmapInfo = CGImageAlphaInfo.premultipliedLast.rawValue | CGBitmapInfo.byteOrder32Big.rawValue
        
        guard let context2 = CGContext(data: nil, width: width, height: height, bitsPerComponent: bitsPerComponent, bytesPerRow: bytesPerRow, space: colorSpace, bitmapInfo: bitmapInfo) else {
            return nil
        }
        
        context2.draw(cgImage, in: CGRect(x: 0, y: 0, width: width, height: height))
        
        guard let data2 = context2.data else { return nil }
        
        let buffer = data2.bindMemory(to: UInt8.self, capacity: width * height * bytesPerPixel)
        
        var totalBrightness: Double = 0
        let pixelCount = width * height
        
        for i in stride(from: 0, to: pixelCount * bytesPerPixel, by: bytesPerPixel) {
            let r = Double(buffer[i]) / 255.0
            let g = Double(buffer[i + 1]) / 255.0
            let b = Double(buffer[i + 2]) / 255.0
            
            // Calculate brightness using luminance formula
            let brightness = 0.299 * r + 0.587 * g + 0.114 * b
            totalBrightness += brightness
        }
        
        return totalBrightness / Double(pixelCount)
    }
    
    func resize(to size: CGSize) -> CIImage {
        let scaleX = size.width / self.extent.width
        let scaleY = size.height / self.extent.height
        return self.transformed(by: CGAffineTransform(scaleX: scaleX, y: scaleY))
    }
}

// MARK: - Bundle Extensions
extension Bundle {
    var appName: String {
        return object(forInfoDictionaryKey: "CFBundleName") as? String ?? "MagCode"
    }
    
    var appVersion: String {
        return object(forInfoDictionaryKey: "CFBundleShortVersionString") as? String ?? "1.0.0"
    }
    
    var buildNumber: String {
        return object(forInfoDictionaryKey: "CFBundleVersion") as? String ?? "1"
    }
}

// MARK: - UIDevice Extensions
extension UIDevice {
    var isIPad: Bool {
        return userInterfaceIdiom == .pad
    }
    
    var isIPhone: Bool {
        return userInterfaceIdiom == .phone
    }
    
    var deviceModel: String {
        var systemInfo = utsname()
        uname(&systemInfo)
        let machineMirror = Mirror(reflecting: systemInfo.machine)
        return machineMirror.children.reduce("") { identifier, element in
            guard let value = element.value as? Int8, value != 0 else { return identifier }
            return identifier + String(UnicodeScalar(UInt8(value))!)
        }
    }
}
