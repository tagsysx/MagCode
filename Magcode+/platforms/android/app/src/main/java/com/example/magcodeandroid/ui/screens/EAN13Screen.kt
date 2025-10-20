package com.example.magcodeandroid.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.magcodeandroid.ui.components.CameraPreview
import com.example.magcodeandroid.viewmodel.EAN13ViewModel

@Composable
fun EAN13Screen(
    viewModel: EAN13ViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var fullScreenImage by remember { mutableStateOf<Bitmap?>(null) }
    var fullScreenTitle by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "EAN13 Image Extraction",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 相机预览
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ) {
            CameraPreview(
                viewModel = viewModel,
                modifier = Modifier.fillMaxSize()
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 状态卡片
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Status: ${uiState.statusMessage}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Head Frame: ${if (uiState.headFrameDetected) "✓ Detected" else "○ Not Detected"}",
                    color = if (uiState.headFrameDetected) Color(0xFF4CAF50) else Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Tail Frame: ${if (uiState.tailFrameDetected) "✓ Detected" else "○ Not Detected"}",
                    color = if (uiState.tailFrameDetected) Color(0xFF4CAF50) else Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 开始/停止检测按钮
        Button(
            onClick = {
                if (uiState.isDetecting) {
                    viewModel.stopDetection()
                } else {
                    viewModel.startDetection()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (uiState.isDetecting) 
                    MaterialTheme.colorScheme.error 
                else 
                    MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = if (uiState.isDetecting) "Stop Detection" else "Start Detection",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Image A 显示区域 - Magnetic Barcodes
        Text(
            text = "Magnetic Barcodes",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .border(2.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        enabled = uiState.imageA != null,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        fullScreenImage = uiState.imageA
                        fullScreenTitle = "Magnetic Barcodes"
                    },
                contentAlignment = Alignment.Center
            ) {
                if (uiState.imageA != null) {
                    Image(
                        bitmap = uiState.imageA!!.asImageBitmap(),
                        contentDescription = "Magnetic Barcodes",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )
                } else {
                    Text(
                        text = "Pending Detection",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Image B 显示区域 - Standardized Barcodes
        Text(
            text = "Standardized Barcodes",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp)  // 设置最大高度，防止图片太高
                .border(2.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 600.dp)
                    .clickable(
                        enabled = uiState.imageB != null,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        fullScreenImage = uiState.imageB
                        fullScreenTitle = "Standardized Barcodes"
                    },
                contentAlignment = Alignment.Center
            ) {
                if (uiState.imageB != null) {
                    Image(
                        bitmap = uiState.imageB!!.asImageBitmap(),
                        contentDescription = "Standardized Barcodes",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Pending Detection",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 条形码解码结果显示
        if (uiState.barcodeResult.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (uiState.barcodeResult.contains("failed")) {
                        MaterialTheme.colorScheme.errorContainer
                    } else {
                        MaterialTheme.colorScheme.primaryContainer
                    }
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Decoding Result:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.barcodeResult.contains("failed")) {
                            MaterialTheme.colorScheme.onErrorContainer
                        } else {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = uiState.barcodeResult,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (uiState.barcodeResult.contains("failed")) {
                            FontWeight.Normal
                        } else {
                            FontWeight.Bold
                        },
                        color = if (uiState.barcodeResult.contains("failed")) {
                            MaterialTheme.colorScheme.onErrorContainer
                        } else {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
    
    // 全屏预览Dialog
    if (fullScreenImage != null) {
        Dialog(
            onDismissRequest = { fullScreenImage = null },
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Black
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // 顶部标题和关闭按钮
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = fullScreenTitle,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            
                            IconButton(
                                onClick = { fullScreenImage = null }
                            ) {
                                Text(
                                    text = "✕",
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        
                        // 全屏图片
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                bitmap = fullScreenImage!!.asImageBitmap(),
                                contentDescription = "Full Screen Preview",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.Fit
                            )
                        }
                        
                        // 底部提示
                        Text(
                            text = "Click ✕ to close or tap outside",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

