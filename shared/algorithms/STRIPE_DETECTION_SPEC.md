# Stripe Detection Algorithm Specification

## Overview
This document specifies the stripe detection algorithm for MagCode, designed to be implemented consistently across iOS and Android platforms. The algorithm detects magnetic interference stripes on CMOS image sensors caused by nearby NFC readers.

## Algorithm Flow

### 1. Image Preprocessing
```
Input: Raw camera image (RGB/BGR format)
Output: Preprocessed grayscale image

Steps:
1. Convert to grayscale using luminance formula: Y = 0.299R + 0.587G + 0.114B
2. Apply Gaussian blur (σ = 1.0) to reduce noise
3. Normalize brightness to range [0.2, 0.8]
4. Enhance contrast using histogram equalization
5. Apply edge-preserving filter (bilateral filter)
```

### 2. Edge Detection
```
Input: Preprocessed grayscale image
Output: Edge map

Steps:
1. Apply Sobel operator for gradient calculation
2. Compute gradient magnitude: G = √(Gx² + Gy²)
3. Apply non-maximum suppression
4. Use adaptive thresholding (Otsu's method)
5. Apply morphological operations (closing) to connect edges
```

### 3. Line Detection
```
Input: Edge map
Output: Detected line segments

Steps:
1. Apply Hough Transform for line detection
2. Filter lines by:
   - Minimum length: 50 pixels
   - Maximum angle deviation: ±15° from horizontal
   - Minimum line strength: 0.6
3. Group parallel lines within 5° tolerance
4. Sort lines by position (left to right)
```

### 4. Stripe Pattern Analysis
```
Input: Detected line segments
Output: Stripe patterns with metadata

Steps:
1. Calculate stripe properties:
   - Width (distance between parallel lines)
   - Intensity (average pixel value)
   - Orientation (angle from horizontal)
   - Position (center coordinates)
2. Validate stripe patterns:
   - Width range: [2.0, 20.0] pixels
   - Intensity range: [0.3, 0.9]
   - Minimum stripe count: 3
   - Maximum stripe count: 50
3. Apply pattern consistency checks
```

### 5. Data Extraction
```
Input: Validated stripe patterns
Output: Decoded data string

Steps:
1. Sort stripes by horizontal position
2. Convert stripe properties to binary data:
   - Width > threshold → 1, else → 0
   - Intensity > threshold → 1, else → 0
3. Apply error correction (Hamming code)
4. Convert binary to ASCII string
5. Validate data format and checksum
```

## Implementation Requirements

### Platform-Specific Considerations

#### iOS Implementation
- Use Core Image framework for image processing
- Implement using Metal Performance Shaders for GPU acceleration
- Use Vision framework for line detection
- Target processing time: ≤ 1.8 seconds

#### Android Implementation
- Use CameraX for camera operations
- Implement using OpenCV for image processing
- Use ML Kit for line detection
- Target processing time: ≤ 1.8 seconds

### Performance Targets
- **Processing Time**: ≤ 1.8 seconds
- **Accuracy**: ≥ 95% stripe detection rate
- **Memory Usage**: ≤ 100MB
- **Battery Impact**: ≤ 5% per transaction

### Error Handling
- Invalid image format
- Insufficient image quality
- No stripes detected
- Data corruption
- Processing timeout

## Configuration Parameters

### Image Processing
```yaml
preprocessing:
  gaussian_blur_sigma: 1.0
  brightness_range: [0.2, 0.8]
  contrast_enhancement: true
  noise_reduction: true

edge_detection:
  sobel_kernel_size: 3
  gradient_threshold: 0.1
  morphological_kernel_size: 3

line_detection:
  hough_rho_resolution: 1.0
  hough_theta_resolution: 0.0174533  # 1 degree in radians
  min_line_length: 50
  max_line_gap: 10
  angle_tolerance: 15

stripe_validation:
  width_range: [2.0, 20.0]
  intensity_range: [0.3, 0.9]
  min_stripe_count: 3
  max_stripe_count: 50
  pattern_consistency_threshold: 0.8
```

## Testing and Validation

### Test Images
- High contrast stripes on uniform background
- Low contrast stripes in varying lighting
- Noisy images with interference
- Images with multiple stripe patterns
- Edge cases (single stripe, overlapping stripes)

### Validation Metrics
- True Positive Rate (TPR)
- False Positive Rate (FPR)
- Precision and Recall
- F1 Score
- Processing time distribution

### Performance Benchmarks
- Average processing time per image
- Memory usage during processing
- CPU/GPU utilization
- Battery consumption per transaction

## Future Enhancements

### Machine Learning Integration
- CNN-based stripe detection
- Transfer learning from synthetic data
- Adaptive thresholding based on image characteristics

### Advanced Algorithms
- Multi-scale stripe detection
- Temporal analysis for video streams
- 3D reconstruction from multiple views

### Optimization Techniques
- Parallel processing on multiple cores
- GPU acceleration for image operations
- Incremental processing for real-time applications

## References
- [OpenCV Documentation](https://docs.opencv.org/)
- [Core Image Programming Guide](https://developer.apple.com/documentation/coreimage)
- [CameraX Overview](https://developer.android.com/training/camerax)
- [ML Kit Documentation](https://developers.google.com/ml-kit)
