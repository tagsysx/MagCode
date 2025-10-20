package com.example.magcodeandroid.ui.screens

import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.net.NetworkInterface
import java.util.*

@Composable
fun InfoScreen() {
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "设备信息",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 设备基本信息
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "设备信息",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                InfoRow("制造商", Build.MANUFACTURER)
                InfoRow("品牌", Build.BRAND)
                InfoRow("型号", Build.MODEL)
                InfoRow("产品", Build.PRODUCT)
                InfoRow("设备", Build.DEVICE)
                InfoRow("硬件", Build.HARDWARE)
                InfoRow("指纹", Build.FINGERPRINT)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 系统信息
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "系统信息",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                InfoRow("Android版本", Build.VERSION.RELEASE)
                InfoRow("SDK版本", Build.VERSION.SDK_INT.toString())
                InfoRow("安全补丁", Build.VERSION.SECURITY_PATCH)
                InfoRow("构建ID", Build.ID)
                InfoRow("构建时间", Date(Build.TIME).toString())
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 网络信息
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "网络信息",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                val networkInfo = getNetworkInfo()
                networkInfo.forEach { (key, value) ->
                    InfoRow(key, value ?: "未知")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 应用信息
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "应用信息",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                InfoRow("包名", context.packageName)
                InfoRow("版本名", context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "未知")
                InfoRow("版本号", context.packageManager.getPackageInfo(context.packageName, 0).versionCode.toString())
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(2f)
        )
    }
}

private fun getNetworkInfo(): Map<String, String?> {
    val networkInfo = mutableMapOf<String, String?>()
    
    try {
        val networkInterfaces = NetworkInterface.getNetworkInterfaces()
        var interfaceCount = 0
        
        while (networkInterfaces.hasMoreElements()) {
            val networkInterface = networkInterfaces.nextElement()
            if (networkInterface.isUp && !networkInterface.isLoopback) {
                val displayName = networkInterface.displayName ?: "Unknown"
                val name = networkInterface.name ?: "Unknown"
                networkInfo["接口${interfaceCount + 1}"] = "$displayName ($name)"
                interfaceCount++
            }
        }
        
        if (networkInfo.isEmpty()) {
            networkInfo["状态"] = "无可用网络接口"
        }
    } catch (e: Exception) {
        networkInfo["错误"] = e.message ?: "未知错误"
    }
    
    return networkInfo
}
