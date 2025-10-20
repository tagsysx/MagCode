# MagCode-plus Payment Protocol Specification

## Overview
This document specifies the MagCode-plus payment protocol with an explicit bit-to-digit EAN-13 encoding pipeline and magnetic carrier modulation for transmission. It describes how raw bits are transformed into decimal digits, composed into EAN-13 symbols, mapped back to unit-width stripe bits, and finally modulated onto a carrier to produce magnetic interference for camera capture.

## Protocol Architecture

### 1. Data Encoding Format (Application Payload)
```
Format: PAYMENT_DATA_{ID}_{AMOUNT}_{MERCHANT}_{CURRENCY}_{TIMESTAMP}_{CHECKSUM}

Components:
- PAYMENT_DATA: Protocol identifier (fixed)
- ID: Transaction identifier (alphanumeric, 8–16 chars)
- AMOUNT: Decimal amount with 2 fraction digits (e.g., 29.99)
- MERCHANT: Merchant identifier (alphanumeric, 6–12 chars)
- CURRENCY: ISO 4217 currency code (3 chars), e.g., USD
- TIMESTAMP: Unix timestamp (10 digits, seconds)
- CHECKSUM: CRC32 checksum (8 hex chars, uppercase)

Example: PAYMENT_DATA_12345_29.99_STORE123_USD_1640995200_A1B2C3D4
```

### 2. Bitstream Construction (Pre-EAN Layer)
- Serialize the application payload into a binary bitstream.
- Append error detection/correction fields as needed (e.g., CRC32 for detection; optional Hamming for correction).

### 3. 3-Bit to Decimal Digit Mapping
- The raw bitstream is segmented into 3-bit groups.
- Each 3-bit group maps to one decimal digit (0–7) using a fixed lookup table, e.g.:
  - 000 → 0, 001 → 1, 010 → 2, 011 → 3, 100 → 4, 101 → 5, 110 → 6, 111 → 7
- If 10 digits (0–9) are required, define extension rules (e.g., reserved markers or grouping policy) — out of scope here; MagCode-plus uses 0–7 set by design.

### 4. Grouping into EAN-13 Symbols
- Take every 12 decimal digits to form the data portion of one EAN-13 symbol.
- Compute the 13th check digit using the EAN‑13 checksum rule:
  - Sum of digits in odd positions (1st, 3rd, …, 11th): S_odd
  - Sum of digits in even positions (2nd, 4th, …, 12th): S_even
  - Check digit = (10 − ((S_odd + 3×S_even) mod 10)) mod 10
- Result: a complete 13-digit EAN-13 code per 12 input digits block.

### 5. Per-Digit Barcode Zone Extraction and Unit-Stripe Mapping
- For each EAN-13 digit, use the standard EAN-13 left/right patterns to obtain the per-digit bar/space sequence.
- Convert the per-digit pattern to unit-width binary stripes using a lookup table:
  - Unit black stripe = 1
  - Unit white stripe = 0
- Concatenate all per-digit unit stripes in order, including EAN‑13 guards:
  - Start guard (101), left digits, center guard (01010), right digits, end guard (101)
- The resulting unit-width 0/1 sequence is the canonical stripe bitstream for modulation.

### 6. Carrier Modulation for Magnetic Interference
- The canonical stripe bitstream is modulated onto a carrier to form a time-domain signal for the magnetic transmitter:
  - Logical 1 (black unit) → high-energy/“on” pulse at carrier frequency
  - Logical 0 (white unit) → low-energy/“off” (or reduced amplitude) interval
- Carrier parameters (example):
  - Carrier frequency: implementation-defined (depends on hardware and camera sampling constraints)
  - Unit period: fixed symbol duration per unit stripe (matched to camera exposure/rolling shutter)
  - Pulse shaping: rectangular or smoothed edges to satisfy EMI constraints
- The transmitted signal induces magnetic interference stripes on the camera sensor; the receiver reconstructs unit stripes from captured frames.

## Receiver Processing (High-Level)
1) Capture frames → detect/normalize stripes (see `shared/algorithms/ALGORITHMS_OVERVIEW.md`).
2) From the reconstructed unit 0/1 sequence (black=1, white=0), parse EAN‑13 guards and per-digit zones.
3) For each digit, use the EAN‑13 per-digit lookup to recover the decimal digit sequence.
4) After every 13 digits, drop the check digit (after validation) and append the remaining 12 digits to the 3‑bit groups buffer.
5) Convert each decimal digit (0–7) back to its 3‑bit representation; concatenate 3‑bit groups to restore the raw bitstream.
6) Parse payload fields and verify CRC32; proceed to validation/authorization.

## Payment Processing Flow

### Step 1: Data Reception
```
1) Magnetic carrier → camera stripes → unit 0/1 sequence
2) EAN‑13 guard/digit parsing via lookup
3) 12-digit groups + computed check digit validation
4) Rebuild 3-bit groups (digits 0–7) → raw bitstream
5) Deframe message and verify CRC32
```

### Step 2: Payment Validation
```
1) Parse: ID, AMOUNT, MERCHANT, CURRENCY, TIMESTAMP, CHECKSUM
2) Check format & ranges:
   - ID: 8–16 alphanumeric
   - AMOUNT: 0.01–10,000.00
   - MERCHANT: 6–12 alphanumeric; optional whitelist
   - CURRENCY: ISO 4217
   - TIMESTAMP: within ±5 minutes of device time
3) CRC32: recompute and compare (uppercase 8-hex)
```

### Step 3: Authorization
```
1) Build payment request object
2) User authentication (biometric/PIN)
3) Authorize with processor (e.g., Google Pay)
4) Receive result and build receipt
```

### Step 4: Confirmation
```
1) Show confirmation (amount, merchant, result)
2) Persist record per policy
3) (Optional) Merchant confirmation callback
4) Update user account state
```

## Android Implementation Sketch
```kotlin
// Digit <-> 3-bit mapping (0..7)
val bitsToDigit: Map<String, Int> = mapOf(
    "000" to 0, "001" to 1, "010" to 2, "011" to 3,
    "100" to 4, "101" to 5, "110" to 6, "111" to 7
)

val digitToBits: Map<Int, String> = bitsToDigit.entries.associate { it.value to it.key }

// EAN-13 lookup tables (left/right encodings) should be defined here
// plus guard patterns and per-digit unit-stripe expansion/compression.

// Payment model skeleton
data class PaymentData(
    val transactionId: String,
    val amount: BigDecimal,
    val merchantId: String,
    val currency: String,
    val timestamp: Long,
    val checksum: String
)
```

## Security Requirements
- Integrity: CRC32; strict parsing; range checks; check-digit validation at EAN‑13 level
- Authentication: biometric/PIN; optional device binding
- Session: secure token handling for any network calls
- Storage/Transit: AES‑256 at rest (if stored), TLS 1.3 in transit

## Error Handling
- Protocol: malformed format, checksum mismatch, EAN‑13 check digit failure, timestamp out-of-range, invalid merchant
- Processing: insufficient funds, network/server error, authentication failure
- Recovery: clear user feedback; safe retry; backoff for network steps

## Performance Targets
- Stripe processing (receiver): ≤ 1.8 s
- Authorization: ≤ 5 s (network dependent)
- Confirmation: ≤ 2 s
- Total transaction: ≤ 10 s

## Testing & Validation
- Scenarios: lighting/contrast extremes, motion blur; valid/invalid EAN‑13 sequences; mapping edge cases
- Metrics: decode success rate, latency per stage, robustness to noise
- Security: auth paths, tamper detection, timestamp drift

## Notes
- The 3-bit→digit (0–7) mapping is fixed; if future payloads require digits 8–9, extend mapping policy explicitly.
- For image/stripe pipeline details, see `shared/algorithms/ALGORITHMS_OVERVIEW.md`.
