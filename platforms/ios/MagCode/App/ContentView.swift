import SwiftUI

struct ContentView: View {
    @StateObject private var cameraManager = CameraManager()
    @StateObject private var stripeDetector = StripeDetector()
    @StateObject private var paymentProcessor = PaymentProcessor()
    
    @State private var showingCamera = false
    @State private var showingPayment = false
    @State private var detectedData: String = ""
    
    var body: some View {
        NavigationView {
            VStack(spacing: 30) {
                // Header
                VStack(spacing: 10) {
                    Image(systemName: "wave.3.right.circle.fill")
                        .font(.system(size: 80))
                        .foregroundColor(.blue)
                    
                    Text("MagCode")
                        .font(.largeTitle)
                        .fontWeight(.bold)
                    
                    Text("Cross-Technology Mobile Payment")
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                }
                
                // Main Action Buttons
                VStack(spacing: 20) {
                    Button(action: {
                        showingCamera = true
                    }) {
                        HStack {
                            Image(systemName: "camera.fill")
                                .font(.title2)
                            Text("Scan NFC Reader")
                                .font(.headline)
                        }
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(Color.blue)
                        .cornerRadius(12)
                    }
                    
                    if !detectedData.isEmpty {
                        Button(action: {
                            showingPayment = true
                        }) {
                            HStack {
                                Image(systemName: "creditcard.fill")
                                    .font(.title2)
                                Text("Process Payment")
                                    .font(.headline)
                            }
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(Color.green)
                            .cornerRadius(12)
                        }
                    }
                }
                
                // Status Display
                if !detectedData.isEmpty {
                    VStack(alignment: .leading, spacing: 10) {
                        Text("Detected Data:")
                            .font(.headline)
                            .foregroundColor(.primary)
                        
                        Text(detectedData)
                            .font(.body)
                            .foregroundColor(.secondary)
                            .padding()
                            .background(Color.gray.opacity(0.1))
                            .cornerRadius(8)
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                }
                
                Spacer()
            }
            .padding()
            .navigationTitle("MagCode")
            .navigationBarTitleDisplayMode(.inline)
        }
        .sheet(isPresented: $showingCamera) {
            CameraView(cameraManager: cameraManager, stripeDetector: stripeDetector) { data in
                detectedData = data
                showingCamera = false
            }
        }
        .sheet(isPresented: $showingPayment) {
            PaymentView(paymentProcessor: paymentProcessor, paymentData: detectedData)
        }
        .onAppear {
            cameraManager.checkPermissions()
        }
    }
}

#Preview {
    ContentView()
}
