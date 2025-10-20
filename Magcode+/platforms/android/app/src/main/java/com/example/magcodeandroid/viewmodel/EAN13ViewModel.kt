package com.example.magcodeandroid.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.magcodeandroid.algorithm.FrameAnalyzer
import com.example.magcodeandroid.algorithm.ImageConcatenator
import com.example.magcodeandroid.algorithm.ImageDecodeAlgorithm
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.min

/**
 * EAN13 提取状态
 */
data class EAN13UiState(
    val isDetecting: Boolean = false,          // Whether detecting
    val headFrameDetected: Boolean = false,    // Head frame detected
    val tailFrameDetected: Boolean = false,    // Tail frame detected
    val imageA: Bitmap? = null,                // image_A
    val imageB: Bitmap? = null,                // image_B
    val statusMessage: String = "Pending",     // Status message
    val processingComplete: Boolean = false,   // Processing complete
    val barcodeResult: String = ""             // Barcode decoding result
)

class EAN13ViewModel : ViewModel(), ViewModelInterface {
    
    companion object {
        private const val TAG = "EAN13ViewModel"
    }
    
    private val _uiState = MutableStateFlow(EAN13UiState())
    val uiState: StateFlow<EAN13UiState> = _uiState.asStateFlow()
    
    // 保存检测到的帧
    private var headFrameBitmap: Bitmap? = null
    private var tailFrameBitmap: Bitmap? = null
    private var headDecodeResult: ImageDecodeAlgorithm.DecodeResult? = null
    private var tailDecodeResult: ImageDecodeAlgorithm.DecodeResult? = null
    
    private var lastProcessTime = 0L
    private val processInterval = 33L // 30fps，快速检测
    
    /**
     * 开始检测
     */
    fun startDetection() {
        Log.d(TAG, "Start detection")
        
        // Reset state
        headFrameBitmap = null
        tailFrameBitmap = null
        headDecodeResult = null
        tailDecodeResult = null
        
        _uiState.value = EAN13UiState(
            isDetecting = true,
            headFrameDetected = false,
            tailFrameDetected = false,
            imageA = null,
            imageB = null,
            statusMessage = "Detecting...",
            processingComplete = false
        )
    }
    
    /**
     * 停止检测
     */
    fun stopDetection() {
        Log.d(TAG, "Stop detection")
        _uiState.value = _uiState.value.copy(
            isDetecting = false,
            statusMessage = "Detection Stopped"
        )
    }
    
    override fun processFrame(imageProxy: ImageProxy) {
        // 如果不在检测状态或已经完成，直接关闭
        if (!_uiState.value.isDetecting || _uiState.value.processingComplete) {
            imageProxy.close()
            return
        }
        
        // 如果已经检测到头帧和尾帧，跳过处理
        if (headFrameBitmap != null && tailFrameBitmap != null) {
            imageProxy.close()
            return
        }
        
        // 帧率控制
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastProcessTime < processInterval) {
            imageProxy.close()
            return
        }
        lastProcessTime = currentTime
        
        viewModelScope.launch {
            try {
                // 转换为Bitmap
                var bitmap = imageProxyToBitmap(imageProxy)
                
                // 检查宽度是否为1080，如果不是则旋转90度
                if (bitmap.width != 1080) {
                    Log.d(TAG, "检测到帧宽度为 ${bitmap.width}，旋转90度")
                    bitmap = rotateBitmap90Degrees(bitmap)
                }
                
                // 确保是1080x1920
                if (bitmap.width != 1080) {
                    Log.w(TAG, "旋转后仍然不是1080宽度: ${bitmap.width}x${bitmap.height}")
                    return@launch
                }
                
                // 解码图像
                val decodeResult = ImageDecodeAlgorithm.processImage(bitmap)
                if (decodeResult == null || decodeResult.decodedBits.isEmpty()) {
                    Log.d(TAG, "解码失败或无有效bit")
                    return@launch
                }
                
                // 分析帧类型
                val analysisResult = FrameAnalyzer.analyzeFrameForPatterns(decodeResult.decodedBits)
                val frameType = FrameAnalyzer.classifyFrameType(analysisResult)
                
                when (frameType) {
                    FrameAnalyzer.FrameType.HEAD -> {
                        if (headFrameBitmap == null) {
                            Log.d(TAG, "Head frame detected")
                            headFrameBitmap = bitmap
                            headDecodeResult = decodeResult
                            
                            _uiState.value = _uiState.value.copy(
                                headFrameDetected = true,
                                statusMessage = "Head frame detected, ${if (tailFrameBitmap != null) "generating results..." else "detecting tail frame..."}"
                            )
                            
                            // If both frames detected, generate results
                            if (tailFrameBitmap != null && tailDecodeResult != null) {
                                generateFinalImages()
                            }
                        }
                    }
                    
                    FrameAnalyzer.FrameType.TAIL -> {
                        if (tailFrameBitmap == null) {
                            Log.d(TAG, "Tail frame detected")
                            tailFrameBitmap = bitmap
                            tailDecodeResult = decodeResult
                            
                            _uiState.value = _uiState.value.copy(
                                tailFrameDetected = true,
                                statusMessage = "Tail frame detected, ${if (headFrameBitmap != null) "generating results..." else "detecting head frame..."}"
                            )
                            
                            // If both frames detected, generate results
                            if (headFrameBitmap != null && headDecodeResult != null) {
                                generateFinalImages()
                            }
                        }
                    }
                    
                    FrameAnalyzer.FrameType.SKIP -> {
                        // Skip this frame
                    }
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Error processing frame", e)
                _uiState.value = _uiState.value.copy(
                    statusMessage = "Processing error: ${e.message}"
                )
            } finally {
                imageProxy.close()
            }
        }
    }
    
    /**
     * 生成最终的image_A和image_B
     */
    private fun generateFinalImages() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Start generating final images")
                
                val headBitmap = headFrameBitmap ?: return@launch
                val tailBitmap = tailFrameBitmap ?: return@launch
                val headResult = headDecodeResult ?: return@launch
                val tailResult = tailDecodeResult ?: return@launch
                
                // 1. Crop head frame
                val headCropped = ImageConcatenator.cropHeadFrame(headBitmap, headResult)
                if (headCropped == null) {
                    Log.e(TAG, "Failed to crop head frame")
                    _uiState.value = _uiState.value.copy(statusMessage = "Failed to crop head frame")
                    return@launch
                }
                
                val headPositions = ImageConcatenator.findStartAndEndPositions(
                    headResult, FrameAnalyzer.FrameType.HEAD
                )
                if (headPositions == null) {
                    Log.e(TAG, "Failed to find head frame positions")
                    _uiState.value = _uiState.value.copy(statusMessage = "Failed to find head frame positions")
                    return@launch
                }
                
                // 2. Crop tail frame
                val tailCropped = ImageConcatenator.cropTailFrame(tailBitmap, tailResult)
                if (tailCropped == null) {
                    Log.e(TAG, "Failed to crop tail frame")
                    _uiState.value = _uiState.value.copy(statusMessage = "Failed to crop tail frame")
                    return@launch
                }
                
                val tailPositions = ImageConcatenator.findStartAndEndPositions(
                    tailResult, FrameAnalyzer.FrameType.TAIL
                )
                if (tailPositions == null) {
                    Log.e(TAG, "Failed to find tail frame positions")
                    _uiState.value = _uiState.value.copy(statusMessage = "Failed to find tail frame positions")
                    return@launch
                }
                
                // 3. 拼接原始图像
                val concatenated = ImageConcatenator.concatenateFrames(tailCropped, headCropped)
                
                // 4. 生成条纹图
                // 头帧条纹图
                val headCropStart = headPositions.endPixelStart.toInt()
                val headStartBitIdx = headPositions.startBitIndex
                val headCropEnd = headResult.bitIntervals[min(headStartBitIdx + 4, headResult.bitIntervals.size - 1)].pixelEnd.toInt()
                val headStripe = ImageConcatenator.createStripeImage(
                    headBitmap, headResult, headCropStart, headCropEnd
                )
                
                // 尾帧条纹图
                val tailCropStart = tailPositions.startBitIndex
                val tailCropStartPixel = tailResult.bitIntervals[tailCropStart].pixelStart.toInt()
                val tailCropEnd = tailPositions.endPixelStart.toInt()
                val tailStripe = ImageConcatenator.createStripeImage(
                    tailBitmap, tailResult, tailCropStartPixel, tailCropEnd
                )
                
                // 5. 拼接条纹图
                val concatenatedStripe = ImageConcatenator.concatenateFrames(tailStripe, headStripe)
                
                // 6. 旋转180度
                val concatenatedRotated = ImageConcatenator.rotate180(concatenated)
                val concatenatedStripeRotated = ImageConcatenator.rotate180(concatenatedStripe)
                
                // 7. 规范化条纹图
                val normalizedStripe = ImageConcatenator.normalizeStripeImage(concatenatedStripeRotated)
                
                // 8. 生成image_A和image_B
                val imageA = createImageA(concatenatedRotated, concatenatedStripeRotated)
                val imageB = createImageB(normalizedStripe)
                
                // 9. 解码Standardized Barcodes (image_B)
                val barcodeResult = decodeBarcode(imageB)
                
                // Update state
                _uiState.value = _uiState.value.copy(
                    imageA = imageA,
                    imageB = imageB,
                    barcodeResult = barcodeResult,
                    statusMessage = "Detection Complete",
                    processingComplete = true,
                    isDetecting = false
                )
                
                Log.d(TAG, "Image generation complete, barcode result: $barcodeResult")
                
            } catch (e: Exception) {
                Log.e(TAG, "Error generating final images", e)
                _uiState.value = _uiState.value.copy(
                    statusMessage = "Image generation error: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 创建image_A：concatenated_result上半960 + concatenated_stripe上半960
     */
    private fun createImageA(concatenatedResult: Bitmap, concatenatedStripe: Bitmap): Bitmap {
        val cropHeight = 960
        
        // 裁剪上半部分
        val resultTop = if (concatenatedResult.height >= cropHeight) {
            Bitmap.createBitmap(concatenatedResult, 0, 0, concatenatedResult.width, cropHeight)
        } else {
            concatenatedResult
        }
        
        val stripeTop = if (concatenatedStripe.height >= cropHeight) {
            Bitmap.createBitmap(concatenatedStripe, 0, 0, concatenatedStripe.width, cropHeight)
        } else {
            concatenatedStripe
        }
        
        // 垂直拼接
        return ImageConcatenator.verticalConcatenate(resultTop, stripeTop)
    }
    
    /**
     * 创建image_B：normalized_stripe下半960
     */
    private fun createImageB(normalizedStripe: Bitmap): Bitmap {
        val cropHeight = 960
        
        return if (normalizedStripe.height >= cropHeight) {
            val startY = normalizedStripe.height - cropHeight
            Bitmap.createBitmap(normalizedStripe, 0, startY, normalizedStripe.width, cropHeight)
        } else {
            normalizedStripe
        }
    }
    
    /**
     * 旋转图像90度
     */
    private fun rotateBitmap90Degrees(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(90f)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
    
    /**
     * ImageProxy转Bitmap
     */
    private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap {
        try {
            val yPlane = imageProxy.planes[0]
            val uPlane = imageProxy.planes[1]
            val vPlane = imageProxy.planes[2]
            
            val yBuffer = yPlane.buffer
            val uBuffer = uPlane.buffer
            val vBuffer = vPlane.buffer
            
            val ySize = yBuffer.remaining()
            val uSize = uBuffer.remaining()
            val vSize = vBuffer.remaining()
            
            val yData = ByteArray(ySize)
            val uData = ByteArray(uSize)
            val vData = ByteArray(vSize)
            
            yBuffer.get(yData)
            uBuffer.get(uData)
            vBuffer.get(vData)
            
            val width = imageProxy.width
            val height = imageProxy.height
            
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val pixels = IntArray(width * height)
            
            val yRowStride = yPlane.rowStride
            val uvRowStride = uPlane.rowStride
            val uvPixelStride = uPlane.pixelStride
            
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val yIndex = y * yRowStride + x
                    val uvIndex = (y / 2) * uvRowStride + (x / 2) * uvPixelStride
                    
                    val yValue = if (yIndex < yData.size) yData[yIndex].toInt() and 0xFF else 128
                    val uValue = if (uvIndex < uData.size) uData[uvIndex].toInt() and 0xFF else 128
                    val vValue = if (uvIndex < vData.size) vData[uvIndex].toInt() and 0xFF else 128
                    
                    val rTmp = yValue + (1.402 * (vValue - 128)).toInt()
                    val gTmp = yValue - (0.344 * (uValue - 128)).toInt() - (0.714 * (vValue - 128)).toInt()
                    val bTmp = yValue + (1.772 * (uValue - 128)).toInt()
                    
                    val r = rTmp.coerceIn(0, 255)
                    val g = gTmp.coerceIn(0, 255)
                    val b = bTmp.coerceIn(0, 255)
                    
                    pixels[y * width + x] = (0xFF shl 24) or (r shl 16) or (g shl 8) or b
                }
            }
            
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            return bitmap
            
        } catch (e: Exception) {
            Log.e(TAG, "ImageProxy转Bitmap失败", e)
            // 创建一个空白图像
            return Bitmap.createBitmap(imageProxy.width, imageProxy.height, Bitmap.Config.ARGB_8888)
        }
    }
    
    /**
     * 解码条形码图片
     */
    private fun decodeBarcode(bitmap: Bitmap): String {
        return try {
            // 将Bitmap转换为int数组
            val width = bitmap.width
            val height = bitmap.height
            val pixels = IntArray(width * height)
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
            
            // 创建ZXing识别所需的对象
            val source = RGBLuminanceSource(width, height, pixels)
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
            
            // 使用MultiFormatReader解码
            val reader = MultiFormatReader()
            val result = reader.decode(binaryBitmap)
            
            // 返回解码结果
            val decodedText = result.text
            val format = result.barcodeFormat.name
            
            Log.d(TAG, "Barcode decoded successfully: Format=$format, Text=$decodedText")
            
            "$decodedText (Format: $format)"
            
        } catch (e: Exception) {
            Log.w(TAG, "Barcode decoding failed: ${e.message}")
            "Decoding failed, please retest"
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        headFrameBitmap = null
        tailFrameBitmap = null
        Log.d(TAG, "ViewModel cleared")
    }
}

