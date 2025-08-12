// swift-tools-version: 5.9
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "MagCode",
    platforms: [
        .iOS(.v15),
        .macOS(.v12)
    ],
    products: [
        .library(
            name: "MagCode",
            targets: ["MagCode"]),
    ],
    dependencies: [
        // Core dependencies for image processing
        .package(url: "https://github.com/opencv/opencv_contrib.git", from: "4.8.0"),
        
        // Vision and Core ML for advanced image analysis
        .package(url: "https://github.com/apple/swift-numerics.git", from: "1.0.0"),
        
        // For testing
        .package(url: "https://github.com/apple/swift-testing.git", from: "0.5.0")
    ],
    targets: [
        .target(
            name: "MagCode",
            dependencies: [
                .product(name: "OpenCV", package: "opencv_contrib"),
                .product(name: "Numerics", package: "swift-numerics")
            ],
            path: "MagCode"),
        .testTarget(
            name: "MagCodeTests",
            dependencies: [
                "MagCode",
                .product(name: "Testing", package: "swift-testing")
            ],
            path: "Tests"),
    ]
)
