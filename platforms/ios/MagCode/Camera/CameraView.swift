import SwiftUI
import AVFoundation

struct CameraView: UIViewControllerRepresentable {
    let cameraManager: CameraManager
    let stripeDetector: StripeDetector
    let onDataDetected: (String) -> Void
    
    func makeUIViewController(context: Context) -> CameraViewController {
        let controller = CameraViewController()
        controller.cameraManager = cameraManager
        controller.stripeDetector = stripeDetector
        controller.onDataDetected = onDataDetected
        return controller
    }
    
    func updateUIViewController(_ uiViewController: CameraViewController, context: Context) {
        // Updates handled by the view controller
    }
}

class CameraViewController: UIViewController {
    var cameraManager: CameraManager!
    var stripeDetector: StripeDetector!
    var onDataDetected: ((String) -> Void)!
    
    private var previewLayer: AVCaptureVideoPreviewLayer!
    private var captureButton: UIButton!
    private var statusLabel: UILabel!
    private var isProcessing = false
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
        setupCamera()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        cameraManager.startSession()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        cameraManager.stopSession()
    }
    
    private func setupUI() {
        view.backgroundColor = .black
        
        // Status Label
        statusLabel = UILabel()
        statusLabel.text = "Position your camera near the NFC reader"
        statusLabel.textColor = .white
        statusLabel.textAlignment = .center
        statusLabel.font = UIFont.systemFont(ofSize: 16)
        statusLabel.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(statusLabel)
        
        // Capture Button
        captureButton = UIButton(type: .system)
        captureButton.setTitle("Capture & Analyze", for: .normal)
        captureButton.setTitleColor(.white, for: .normal)
        captureButton.backgroundColor = .blue
        captureButton.layer.cornerRadius = 25
        captureButton.titleLabel?.font = UIFont.systemFont(ofSize: 18, weight: .semibold)
        captureButton.addTarget(self, action: #selector(captureButtonTapped), for: .touchUpInside)
        captureButton.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(captureButton)
        
        // Close Button
        let closeButton = UIButton(type: .system)
        closeButton.setTitle("✕", for: .normal)
        closeButton.setTitleColor(.white, for: .normal)
        closeButton.titleLabel?.font = UIFont.systemFont(ofSize: 24)
        closeButton.addTarget(self, action: #selector(closeButtonTapped), for: .touchUpInside)
        closeButton.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(closeButton)
        
        NSLayoutConstraint.activate([
            statusLabel.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor, constant: 20),
            statusLabel.leadingAnchor.constraint(equalTo: view.leadingAnchor, constant: 20),
            statusLabel.trailingAnchor.constraint(equalTo: view.trailingAnchor, constant: -20),
            
            closeButton.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor, constant: 20),
            closeButton.trailingAnchor.constraint(equalTo: view.trailingAnchor, constant: -20),
            closeButton.widthAnchor.constraint(equalToConstant: 44),
            closeButton.heightAnchor.constraint(equalToConstant: 44),
            
            captureButton.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor, constant: -40),
            captureButton.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            captureButton.widthAnchor.constraint(equalToConstant: 200),
            captureButton.heightAnchor.constraint(equalToConstant: 50)
        ])
    }
    
    private func setupCamera() {
        previewLayer = cameraManager.previewLayer
        previewLayer.frame = view.bounds
        view.layer.insertSublayer(previewLayer, at: 0)
    }
    
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        previewLayer?.frame = view.bounds
    }
    
    @objc private func captureButtonTapped() {
        guard !isProcessing else { return }
        
        isProcessing = true
        statusLabel.text = "Analyzing image for magnetic stripes..."
        captureButton.isEnabled = false
        
        // Simulate stripe detection (this will be replaced with actual implementation)
        DispatchQueue.main.asyncAfter(deadline: .now() + 2.0) {
            self.processCapturedImage()
        }
    }
    
    @objc private func closeButtonTapped() {
        dismiss(animated: true)
    }
    
    private func processCapturedImage() {
        // This is where the actual stripe detection will happen
        // For now, we'll simulate the detection process
        
        let simulatedData = "PAYMENT_DATA_12345_AMOUNT_29.99"
        
        DispatchQueue.main.async {
            self.statusLabel.text = "Data detected successfully!"
            self.captureButton.isEnabled = true
            self.isProcessing = false
            
            DispatchQueue.main.asyncAfter(deadline: .now() + 1.0) {
                self.onDataDetected(simulatedData)
            }
        }
    }
}
