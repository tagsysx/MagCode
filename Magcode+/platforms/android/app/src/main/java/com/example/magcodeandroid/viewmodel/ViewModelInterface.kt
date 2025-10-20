package com.example.magcodeandroid.viewmodel

import androidx.camera.core.ImageProxy

interface ViewModelInterface {
    fun processFrame(imageProxy: ImageProxy)
}


