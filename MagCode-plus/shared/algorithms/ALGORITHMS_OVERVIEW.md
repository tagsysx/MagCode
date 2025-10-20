# Magnetic Stripe Recognition & EAN-13 Generation — Algorithms Overview (MagCode-plus)

This document describes the core algorithm design and module responsibilities for the Android implementation (Jetpack Compose + CameraX). It is informed by `MagCode-main/shared/algorithms/STRIPE_DETECTION_SPEC.md` and tailored to the current codebase.

## What the App Does
- Real-time camera capture at 1080p (~30 FPS)
- Automatic detection of two key frame types: Head frame and Tail frame
- Generation of two result images from the detected frames:
  - Image A: Concatenated magnetic stripe image (cropped from original frames and concatenated in order)
  - Image B: Standardized/normalized stripe image (optimized for barcode readers)
- Automatic barcode decoding with ZXing to read EAN-13 (and other common formats)
- Clear visualization and interaction: dual-image display, full-screen preview on tap, status and error messages
- All processing is on-device and offline (no network or storage permissions)

## End-to-End Pipeline
```
Camera Frame (1920×1080)
    ↓
Orientation check (rotate if needed)
    ↓
Grayscale + column-mean projection (reduce 2D to 1D signal)
    ↓
Interval thresholding (adaptive, per 135-pixel segment)
    ↓
Binary sequence denoising (suppress regions < 3 px)
    ↓
Run-length to bits mapping (pixel-run length → bit sequence)
    ↓
Frame type classification (HEAD | TAIL | SKIP)
    ↓
When HEAD & TAIL are both available:
    - Generate Image A: crop valid regions → concatenate → rotate 180°
    - Generate Image B: normalize stripes → standardized barcode-like image
    ↓
ZXing decoding (prefer Image B)
```

## Frame Patterns (Protocol Constraints)
- Head frame start marker: `10100`
- Tail frame start marker: `00101`
- Terminator: `01010`
- Data section: 42 bits

## Modules & Responsibilities

### 1) `FrameAnalyzer.kt`
- Goal: Determine the frame type for each incoming frame — `HEAD`, `TAIL`, or `SKIP`.
- Key steps:
  - Width/height/orientation check to ensure correct decoding axis
  - Pattern analysis using the bit sequence produced by `ImageDecodeAlgorithm`:
    - Head pattern: `[Terminator 01010] + [42-bit Data] + [Start 10100]`
    - Tail pattern: `[Start 00101] + [42-bit Data] + [Terminator 01010]`
  - Robustness: return `SKIP` when sequence is unstable/incomplete; continue to next frame
- Input: camera frame (YUV/RGB converted to bitmap or pixel buffer)
- Output: `FrameType` and optional positions for downstream cropping/concatenation

### 2) `ImageDecodeAlgorithm.kt`
- Goal: Convert a single frame into a robust 1D binary/bit sequence.
- Key steps:
  - Grayscale: `gray = 0.299R + 0.587G + 0.114B`
  - Column-mean projection: average per column to obtain a 1D signal
  - Interval thresholding: every 135 pixels, threshold = `min + 0.2*(max - min)`
  - Binarization & denoising: remove short segments of length ≤ 3 px
  - Run-length to bits mapping:
    - 5–29 px → 1 bit (0 or 1)
    - 30–49 px → 2 bits (00 or 11)
    - 50–69 px → 3 bits (000 or 111)
    - 70–89 px → 4 bits (0000 or 1111)
    - ≥90 px → 5 bits (00000 or 11111)
- Input: single frame image
- Output: denoised binary/bit sequence for `FrameAnalyzer`

### 3) `ImageConcatenator.kt`
- Goal: After both HEAD and TAIL are found, generate two images for visualization and decoding.
- Key steps:
  - Valid region cropping: locate the effective stripe region via frame type and pattern positions
  - Image A (magnetic stripe concatenation): concatenate cropped regions in linear order, then rotate the final result by 180°
  - Image B (standardized stripes): normalize stripe width/intensity to form a clean "standardized barcode" image
- Input: classified HEAD/TAIL frames and their cropping positions
- Output: Bitmap images A and B

## ZXing Decoding
- Prefer decoding on Image B (standardized stripes)
- Supported formats include EAN-13, UPC-A, Code-128, QR, etc.
- Success: decoded text + format info; Failure: user-facing error messages

## Parameters (Defaults from Implementation)
- Frame processing interval: `processInterval = 33 ms` (~30 FPS)
- Interval size for thresholding: `135 px`
- Denoise threshold: connected run length `≤ 3 px` is considered noise
- Bit mapping thresholds: see run-length mapping table above

## Performance Targets (Observed/Expected)
- End-to-end: usually < 2 s for a complete detection/generation cycle (depends on motion and lighting)
- Per-frame processing: real-time (33 ms/frame)
- Image generation: < 500 ms
- Decoding: < 100 ms (depends on image quality)
- Memory: ~100–150 MB suitable for on-device real-time

## Errors & Recovery
- Head/Tail not found: keep sampling and classifying; UI shows clear status indicators
- Poor image quality: unstable binary sequence → skip the frame
- Decoding failed: show reason and suggest retry; user can restart detection

## Testing Suggestions
- Lighting: high/low contrast, complex lighting, noisy scenes
- Motion: static, slight shake, rapid movement
- Angles: slight tilt, horizontal offset
- Regression: unit/integration tests for key parameters (interval size, denoise threshold, run-length mapping)

## Dependencies
- CameraX: camera input
- ZXing: barcode decoding
- Kotlin / Jetpack Compose: UI

## File Responsibility Map
- `FrameAnalyzer.kt`: frame type classification (HEAD/TAIL/SKIP) and pattern validation
- `ImageDecodeAlgorithm.kt`: grayscale/projection/thresholding/denoising/run-length-to-bits
- `ImageConcatenator.kt`: valid region cropping, magnetic stripe concatenation (Image A), stripe normalization (Image B)
