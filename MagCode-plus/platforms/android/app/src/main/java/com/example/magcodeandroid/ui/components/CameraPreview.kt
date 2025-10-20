package com.example.magcodeandroid.ui.components

import android.content.Context
import android.util.Log
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.magcodeandroid.viewmodel.ViewModelInterface
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun CameraPreview(
    viewModel: ViewModelInterface,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val executor = remember { Executors.newSingleThreadExecutor() }
    
    DisposableEffect(Unit) {
        onDispose {
            executor.shutdown()
        }
    }
    
    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                this.scaleType = PreviewView.ScaleType.FILL_CENTER
            }
        },
        modifier = modifier.fillMaxSize(),
        update = { previewView ->
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                
                // 创建精确的分辨率选择器，强制使用16:9宽高比和1920x1080分辨率
                val resolutionSelector = ResolutionSelector.Builder()
                    .setAspectRatioStrategy(
                        AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY
                    )
                    .setResolutionStrategy(
                        ResolutionStrategy(
                            Size(1920, 1080),
                            ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER
                        )
                    )
                    .build()
                
                val preview = Preview.Builder()
                    .setResolutionSelector(resolutionSelector)
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                
                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setResolutionSelector(resolutionSelector)
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                    .build()
                    .also {
                        it.setAnalyzer(executor) { imageProxy ->
                            // 记录实际分辨率和宽高比
                            val actualWidth = imageProxy.width
                            val actualHeight = imageProxy.height
                            val aspectRatio = actualWidth.toFloat() / actualHeight.toFloat()
                            
                            Log.d("CameraPreview", "实际分辨率: ${actualWidth}x${actualHeight}, 宽高比: ${"%.2f".format(aspectRatio)}")
                            
                            if (actualWidth != 1920 || actualHeight != 1080) {
                                Log.w("CameraPreview", "⚠️ 分辨率不匹配！实际: ${actualWidth}x${actualHeight}, 期望: 1920x1080")
                            } else {
                                Log.i("CameraPreview", "✅ 分辨率正确: 1920x1080")
                            }
                            
                            viewModel.processFrame(imageProxy)
                            // imageProxy.close() 在viewModel中处理
                        }
                    }
                
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                
                try {
                    cameraProvider.unbindAll()
                    val camera = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )
                    
                    // 记录相机配置信息
                    val cameraInfo = camera.cameraInfo
                    Log.d("CameraPreview", "========== 相机配置 ==========")
                    Log.d("CameraPreview", "相机已绑定")
                    Log.d("CameraPreview", "期望分辨率: 1920x1080 (16:9)")
                    Log.d("CameraPreview", "================================")
                } catch (e: Exception) {
                    Log.e("CameraPreview", "相机绑定失败", e)
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(context))
        }
    )
}
