package com.example.magcodeandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.magcodeandroid.ui.screens.InfoScreen
import com.example.magcodeandroid.ui.screens.EAN13Screen
import com.example.magcodeandroid.ui.theme.MagCodeAndroidTheme
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            MagCodeAndroidTheme {
                MagCodeApp()
            }
        }
    }
}

@Composable
fun MagCodeApp() {
    var hasCameraPermission by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    // 检查相机权限
    LaunchedEffect(Unit) {
        hasCameraPermission = ContextCompat.checkSelfPermission(
            context, 
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    if (!hasCameraPermission) {
        PermissionRequestScreen(
            onPermissionGranted = { hasCameraPermission = true }
        )
    } else {
        val navController = rememberNavController()
        
        Scaffold(
            bottomBar = {
                BottomNavigationBar(navController)
            }
        ) { paddingValues ->
            NavigationHost(
                navController = navController,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
fun PermissionRequestScreen(onPermissionGranted: () -> Unit) {
    val context = LocalContext.current
    var permissionRequested by remember { mutableStateOf(false) }
    
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionGranted()
        }
    }
    
    LaunchedEffect(Unit) {
        if (!permissionRequested) {
            permissionRequested = true
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        Text(
            text = "Camera Permission Required",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "This app requires camera permission to capture images and perform processing",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { 
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        ) {
            Text("Grant Permission")
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar {
        NavigationBarItem(
            icon = { Text("📷") },
            label = { Text("EAN13") },
            selected = navController.currentDestination?.route == "ean13",
            onClick = { navController.navigate("ean13") }
        )
        NavigationBarItem(
            icon = { Text("ℹ️") },
            label = { Text("Info") },
            selected = navController.currentDestination?.route == "info",
            onClick = { navController.navigate("info") }
        )
    }
}

@Composable
fun NavigationHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "ean13",
        modifier = modifier
    ) {
        composable("ean13") {
            EAN13Screen()
        }
        composable("info") {
            InfoScreen()
        }
    }
}


