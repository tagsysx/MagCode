import Foundation
import PassKit

class PaymentProcessor: NSObject, ObservableObject {
    @Published var isProcessing = false
    @Published var paymentStatus: PaymentStatus = .idle
    @Published var lastTransaction: Transaction?
    
    private var paymentController: PKPaymentAuthorizationController?
    
    func processPayment(amount: Double, merchantId: String, transactionId: String) async throws -> Transaction {
        await MainActor.run {
            isProcessing = true
            paymentStatus = .processing
        }
        
        defer {
            Task { @MainActor in
                isProcessing = false
            }
        }
        
        // Validate payment data
        guard amount > 0 else {
            throw PaymentError.invalidAmount
        }
        
        guard !merchantId.isEmpty else {
            throw PaymentError.invalidMerchant
        }
        
        // Create payment request
        let paymentRequest = createPaymentRequest(amount: amount, merchantId: merchantId)
        
        // Process payment
        let transaction = try await authorizePayment(paymentRequest)
        
        // Update transaction with additional info
        var updatedTransaction = transaction
        updatedTransaction.merchantId = merchantId
        updatedTransaction.transactionId = transactionId
        updatedTransaction.timestamp = Date()
        
        await MainActor.run {
            self.lastTransaction = updatedTransaction
            self.paymentStatus = .completed
        }
        
        return updatedTransaction
    }
    
    private func createPaymentRequest(amount: Double, merchantId: String) -> PKPaymentRequest {
        let request = PKPaymentRequest()
        
        // Configure payment request
        request.merchantIdentifier = merchantId
        request.supportedNetworks = [.visa, .masterCard, .amex, .discover]
        request.merchantCapabilities = .capability3DS
        request.countryCode = "US"
        request.currencyCode = "USD"
        
        // Set payment amount
        let paymentItem = PKPaymentSummaryItem(label: "MagCode Payment", amount: NSDecimalNumber(value: amount))
        request.paymentSummaryItems = [paymentItem]
        
        return request
    }
    
    private func authorizePayment(_ request: PKPaymentRequest) async throws -> Transaction {
        return try await withCheckedThrowingContinuation { continuation in
            DispatchQueue.main.async {
                self.paymentController = PKPaymentAuthorizationController(paymentRequest: request)
                self.paymentController?.delegate = self
                
                self.paymentController?.present { presented in
                    if !presented {
                        continuation.resume(throwing: PaymentError.presentationFailed)
                    }
                }
                
                // Store continuation for later use
                self.pendingContinuation = continuation
            }
        }
    }
    
    private var pendingContinuation: CheckedContinuation<Transaction, Error>?
    
    func cancelPayment() {
        paymentController?.dismiss()
        paymentStatus = .cancelled
        isProcessing = false
    }
    
    func validatePaymentData(_ data: String) -> PaymentValidationResult {
        let result = PaymentValidationResult()
        
        // Parse payment data
        let components = data.components(separatedBy: "_")
        
        for component in components {
            if component.hasPrefix("AMOUNT") {
                let amountString = component.replacingOccurrences(of: "AMOUNT", with: "")
                if let amount = Double(amountString), amount > 0 {
                    result.amount = amount
                } else {
                    result.addError(.invalidAmount)
                }
            } else if component.hasPrefix("MERCHANT") {
                let merchantString = component.replacingOccurrences(of: "MERCHANT", with: "")
                if !merchantString.isEmpty {
                    result.merchantId = merchantString
                } else {
                    result.addError(.invalidMerchant)
                }
            }
        }
        
        // Check required fields
        if result.amount == nil {
            result.addError(.missingAmount)
        }
        
        if result.merchantId.isEmpty {
            result.addError(.missingMerchant)
        }
        
        return result
    }
}

extension PaymentProcessor: PKPaymentAuthorizationControllerDelegate {
    func paymentAuthorizationController(_ controller: PKPaymentAuthorizationController, didAuthorizePayment payment: PKPayment, handler completion: @escaping (PKPaymentAuthorizationResult) -> Void) {
        // Payment was authorized
        let transaction = Transaction(
            id: UUID().uuidString,
            amount: 0.0, // Will be updated
            status: .authorized,
            paymentMethod: payment.paymentMethod.type.description,
            timestamp: Date()
        )
        
        pendingContinuation?.resume(returning: transaction)
        completion(PKPaymentAuthorizationResult(status: .success, errors: nil))
    }
    
    func paymentAuthorizationControllerDidFinish(_ controller: PKPaymentAuthorizationController) {
        controller.dismiss()
        pendingContinuation = nil
    }
}

struct Transaction {
    let id: String
    var amount: Double
    let status: TransactionStatus
    let paymentMethod: String
    var merchantId: String = ""
    var transactionId: String = ""
    var timestamp: Date
}

enum TransactionStatus {
    case pending
    case authorized
    case completed
    case failed
    case cancelled
    
    var description: String {
        switch self {
        case .pending: return "Pending"
        case .authorized: return "Authorized"
        case .completed: return "Completed"
        case .failed: return "Failed"
        case .cancelled: return "Cancelled"
        }
    }
}

enum PaymentStatus {
    case idle
    case processing
    case completed
    case failed
    case cancelled
}

struct PaymentValidationResult {
    var amount: Double?
    var merchantId: String = ""
    private(set) var errors: [PaymentError] = []
    
    var isValid: Bool {
        return errors.isEmpty && amount != nil && !merchantId.isEmpty
    }
    
    mutating func addError(_ error: PaymentError) {
        errors.append(error)
    }
}

enum PaymentError: Error, LocalizedError {
    case invalidAmount
    case invalidMerchant
    case missingAmount
    case missingMerchant
    case presentationFailed
    case authorizationFailed
    case networkError
    case unknown
    
    var errorDescription: String? {
        switch self {
        case .invalidAmount:
            return "Invalid payment amount"
        case .invalidMerchant:
            return "Invalid merchant information"
        case .missingAmount:
            return "Payment amount is required"
        case .missingMerchant:
            return "Merchant information is required"
        case .presentationFailed:
            return "Failed to present payment interface"
        case .authorizationFailed:
            return "Payment authorization failed"
        case .networkError:
            return "Network connection error"
        case .unknown:
            return "Unknown payment error"
        }
    }
}
