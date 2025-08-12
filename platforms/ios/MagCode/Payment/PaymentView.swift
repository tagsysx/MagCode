import SwiftUI
import PassKit

struct PaymentView: View {
    let paymentProcessor: PaymentProcessor
    let paymentData: String
    
    @State private var paymentInfo: PaymentInfo?
    @State private var showingPaymentSheet = false
    @State private var paymentError: String?
    
    var body: some View {
        NavigationView {
            VStack(spacing: 30) {
                // Header
                VStack(spacing: 10) {
                    Image(systemName: "creditcard.fill")
                        .font(.system(size: 60))
                        .foregroundColor(.green)
                    
                    Text("Payment Details")
                        .font(.largeTitle)
                        .fontWeight(.bold)
                }
                
                // Payment Information
                if let info = paymentInfo {
                    VStack(spacing: 20) {
                        PaymentInfoCard(info: info)
                        
                        Button(action: {
                            showingPaymentSheet = true
                        }) {
                            HStack {
                                Image(systemName: "applelogo")
                                    .font(.title2)
                                Text("Pay with Apple Pay")
                                    .font(.headline)
                            }
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(Color.black)
                            .cornerRadius(12)
                        }
                    }
                } else {
                    // Loading or error state
                    if let error = paymentError {
                        VStack(spacing: 15) {
                            Image(systemName: "exclamationmark.triangle.fill")
                                .font(.system(size: 50))
                                .foregroundColor(.red)
                            
                            Text("Payment Error")
                                .font(.headline)
                                .foregroundColor(.red)
                            
                            Text(error)
                                .font(.body)
                                .foregroundColor(.secondary)
                                .multilineTextAlignment(.center)
                        }
                    } else {
                        ProgressView("Processing payment data...")
                    }
                }
                
                Spacer()
            }
            .padding()
            .navigationTitle("Payment")
            .navigationBarTitleDisplayMode(.inline)
            .navigationBarItems(trailing: Button("Close") {
                // Dismiss the view
            })
        }
        .onAppear {
            processPaymentData()
        }
        .sheet(isPresented: $showingPaymentSheet) {
            ApplePaySheet(paymentProcessor: paymentProcessor, paymentInfo: paymentInfo!)
        }
    }
    
    private func processPaymentData() {
        // Parse the payment data to extract payment information
        let components = paymentData.components(separatedBy: "_")
        
        var info = PaymentInfo()
        
        for component in components {
            if component.hasPrefix("AMOUNT") {
                let amountString = component.replacingOccurrences(of: "AMOUNT", with: "")
                info.amount = Double(amountString) ?? 0.0
            } else if component.hasPrefix("MERCHANT") {
                let merchantString = component.replacingOccurrences(of: "MERCHANT", with: "")
                info.merchantId = merchantString
            } else if component.hasPrefix("ID") {
                let idString = component.replacingOccurrences(of: "ID", with: "")
                info.transactionId = idString
            }
        }
        
        // Validate the payment information
        if info.amount > 0 && !info.merchantId.isEmpty {
            paymentInfo = info
        } else {
            paymentError = "Invalid payment data format. Please try scanning again."
        }
    }
}

struct PaymentInfoCard: View {
    let info: PaymentInfo
    
    var body: some View {
        VStack(spacing: 15) {
            HStack {
                Text("Amount")
                    .font(.headline)
                    .foregroundColor(.primary)
                Spacer()
                Text("$\(String(format: "%.2f", info.amount))")
                    .font(.title)
                    .fontWeight(.bold)
                    .foregroundColor(.green)
            }
            
            Divider()
            
            HStack {
                Text("Merchant")
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                Spacer()
                Text(info.merchantId)
                    .font(.subheadline)
                    .foregroundColor(.primary)
            }
            
            if !info.transactionId.isEmpty {
                HStack {
                    Text("Transaction ID")
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                    Spacer()
                    Text(info.transactionId)
                        .font(.subheadline)
                        .foregroundColor(.primary)
                }
            }
            
            HStack {
                Text("Currency")
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                Spacer()
                Text(info.currency)
                    .font(.subheadline)
                    .foregroundColor(.primary)
            }
        }
        .padding()
        .background(Color.gray.opacity(0.1))
        .cornerRadius(12)
    }
}

struct ApplePaySheet: View {
    let paymentProcessor: PaymentProcessor
    let paymentInfo: PaymentInfo
    
    @Environment(\.presentationMode) var presentationMode
    @State private var isProcessing = false
    @State private var showingError = false
    @State private var errorMessage = ""
    
    var body: some View {
        VStack(spacing: 20) {
            if isProcessing {
                VStack(spacing: 15) {
                    ProgressView()
                        .scaleEffect(1.5)
                    
                    Text("Processing Payment...")
                        .font(.headline)
                    
                    Text("Please complete the payment on your device")
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                        .multilineTextAlignment(.center)
                }
            } else {
                VStack(spacing: 15) {
                    Image(systemName: "checkmark.circle.fill")
                        .font(.system(size: 60))
                        .foregroundColor(.green)
                    
                    Text("Payment Successful!")
                        .font(.title)
                        .fontWeight(.bold)
                    
                    Text("Your payment of $\(String(format: "%.2f", paymentInfo.amount)) has been processed successfully.")
                        .font(.body)
                        .foregroundColor(.secondary)
                        .multilineTextAlignment(.center)
                    
                    Button("Done") {
                        presentationMode.wrappedValue.dismiss()
                    }
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color.blue)
                    .cornerRadius(12)
                }
            }
        }
        .padding()
        .onAppear {
            processPayment()
        }
        .alert("Payment Error", isPresented: $showingError) {
            Button("OK") {
                presentationMode.wrappedValue.dismiss()
            }
        } message: {
            Text(errorMessage)
        }
    }
    
    private func processPayment() {
        isProcessing = true
        
        Task {
            do {
                let transaction = try await paymentProcessor.processPayment(
                    amount: paymentInfo.amount,
                    merchantId: paymentInfo.merchantId,
                    transactionId: paymentInfo.transactionId
                )
                
                await MainActor.run {
                    isProcessing = false
                }
            } catch {
                await MainActor.run {
                    isProcessing = false
                    errorMessage = error.localizedDescription
                    showingError = true
                }
            }
        }
    }
}

#Preview {
    PaymentView(
        paymentProcessor: PaymentProcessor(),
        paymentData: "PAYMENT_DATA_12345_AMOUNT_29.99_MERCHANT_STORE123"
    )
}
