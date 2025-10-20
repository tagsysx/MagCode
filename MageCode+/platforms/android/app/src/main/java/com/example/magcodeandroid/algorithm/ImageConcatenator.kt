package com.example.magcodeandroid.algorithm

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import kotlin.math.max
import kotlin.math.min

/**
 * 图像拼接器 - 对应image_concate.py和EAN13_extraction.py
 */
object ImageConcatenator {
    
    private const val TAG = "ImageConcatenator"
    
    data class FramePositions(
        val startBitIndex: Int,
        val endBitIndex: Int,
        val endPixelStart: Double,
        val endPixelEnd: Double
    )
    
    /**
     * 根据帧类型找到起始符和终止符的位置
     */
    fun findStartAndEndPositions(
        decodeResult: ImageDecodeAlgorithm.DecodeResult,
        frameType: FrameAnalyzer.FrameType
    ): FramePositions? {
        val decodedBits = decodeResult.decodedBits
        val bitIntervals = decodeResult.bitIntervals
        
        return when (frameType) {
            FrameAnalyzer.FrameType.HEAD -> {
                // 头帧结构：[终止符01010] + [数据42bit] + [起始符10100]
                val startPattern = listOf(1, 0, 1, 0, 0)
                var startBitIndex = -1
                
                // 从右向左查找起始符
                for (i in (decodedBits.size - startPattern.size) downTo 0) {
                    var match = true
                    for (j in startPattern.indices) {
                        if (decodedBits[i + j] != startPattern[j]) {
                            match = false
                            break
                        }
                    }
                    if (match) {
                        startBitIndex = i
                        break
                    }
                }
                
                if (startBitIndex == -1) {
                    Log.w(TAG, "警告：未找到头帧起始符")
                    return null
                }
                
                // 起始符前面应该有42bit数据 + 5bit终止符 = 47bit
                val endBitIndex = startBitIndex - 47
                
                if (endBitIndex < 0) {
                    Log.w(TAG, "警告：起始符前bit数不足47个")
                    return null
                }
                
                // 验证是否为终止符 [0,1,0,1,0]
                val endPattern = listOf(0, 1, 0, 1, 0)
                var isValidEnd = true
                for (i in endPattern.indices) {
                    if (endBitIndex + i >= decodedBits.size || decodedBits[endBitIndex + i] != endPattern[i]) {
                        isValidEnd = false
                        break
                    }
                }
                
                if (!isValidEnd) {
                    Log.w(TAG, "警告：位置 $endBitIndex 处不是终止符")
                    return null
                }
                
                // 获取终止符的像素位置
                val endPixelStart = bitIntervals[endBitIndex].pixelStart
                val endPixelEnd = bitIntervals[min(endBitIndex + 4, bitIntervals.size - 1)].pixelEnd
                
                FramePositions(startBitIndex, endBitIndex, endPixelStart, endPixelEnd)
            }
            
            FrameAnalyzer.FrameType.TAIL -> {
                // 尾帧结构：[起始符00101] + [数据42bit] + [终止符01010]
                val startPattern = listOf(0, 0, 1, 0, 1)
                var startBitIndex = -1
                
                // 从左向右查找起始符
                for (i in 0..(decodedBits.size - startPattern.size)) {
                    var match = true
                    for (j in startPattern.indices) {
                        if (decodedBits[i + j] != startPattern[j]) {
                            match = false
                            break
                        }
                    }
                    if (match) {
                        startBitIndex = i
                        break
                    }
                }
                
                if (startBitIndex == -1) {
                    Log.w(TAG, "警告：未找到尾帧起始符")
                    return null
                }
                
                // 起始符后面应该有42bit数据 + 5bit终止符 = 47bit
                val endBitIndex = startBitIndex + 5 + 42
                
                if (endBitIndex + 5 > decodedBits.size) {
                    Log.w(TAG, "警告：起始符后bit数不足52个")
                    return null
                }
                
                // 验证是否为终止符 [0,1,0,1,0]
                val endPattern = listOf(0, 1, 0, 1, 0)
                var isValidEnd = true
                for (i in endPattern.indices) {
                    if (endBitIndex + i >= decodedBits.size || decodedBits[endBitIndex + i] != endPattern[i]) {
                        isValidEnd = false
                        break
                    }
                }
                
                if (!isValidEnd) {
                    Log.w(TAG, "警告：位置 $endBitIndex 处不是终止符")
                    return null
                }
                
                // 获取终止符的像素位置
                val endPixelStart = bitIntervals[endBitIndex].pixelStart
                val endPixelEnd = bitIntervals[min(endBitIndex + 4, bitIntervals.size - 1)].pixelEnd
                
                FramePositions(startBitIndex, endBitIndex, endPixelStart, endPixelEnd)
            }
            
            else -> null
        }
    }
    
    /**
     * 裁剪头帧：精确保留【终止符+数据+起始符】
     */
    fun cropHeadFrame(
        bitmap: Bitmap,
        decodeResult: ImageDecodeAlgorithm.DecodeResult
    ): Bitmap? {
        val positions = findStartAndEndPositions(decodeResult, FrameAnalyzer.FrameType.HEAD)
            ?: return null
        
        val bitIntervals = decodeResult.bitIntervals
        
        // 终止符开始的像素位置
        val cropStart = positions.endPixelStart.toInt()
        
        // 起始符结束的像素位置
        val startBitIdx = positions.startBitIndex
        val cropEnd = bitIntervals[min(startBitIdx + 4, bitIntervals.size - 1)].pixelEnd.toInt()
        
        Log.d(TAG, "头帧裁剪：保留像素位置 $cropStart 到 $cropEnd")
        
        // 裁剪图像
        val width = cropEnd - cropStart
        if (width <= 0 || cropStart < 0 || cropEnd > bitmap.width) {
            Log.e(TAG, "头帧裁剪参数错误: cropStart=$cropStart, cropEnd=$cropEnd, width=$width")
            return null
        }
        
        return Bitmap.createBitmap(bitmap, cropStart, 0, width, bitmap.height)
    }
    
    /**
     * 裁剪尾帧：精确保留【起始符+数据】
     */
    fun cropTailFrame(
        bitmap: Bitmap,
        decodeResult: ImageDecodeAlgorithm.DecodeResult
    ): Bitmap? {
        val positions = findStartAndEndPositions(decodeResult, FrameAnalyzer.FrameType.TAIL)
            ?: return null
        
        val bitIntervals = decodeResult.bitIntervals
        
        // 起始符开始的像素位置
        val startBitIdx = positions.startBitIndex
        val cropStart = bitIntervals[startBitIdx].pixelStart.toInt()
        
        // 终止符开始的像素位置（不包括终止符）
        val cropEnd = positions.endPixelStart.toInt()
        
        Log.d(TAG, "尾帧裁剪：保留像素位置 $cropStart 到 $cropEnd")
        
        // 裁剪图像
        val width = cropEnd - cropStart
        if (width <= 0 || cropStart < 0 || cropEnd > bitmap.width) {
            Log.e(TAG, "尾帧裁剪参数错误: cropStart=$cropStart, cropEnd=$cropEnd, width=$width")
            return null
        }
        
        return Bitmap.createBitmap(bitmap, cropStart, 0, width, bitmap.height)
    }
    
    /**
     * 创建黑白条纹图
     * bit=0的区域 → 白色（255）
     * bit=1的区域 → 黑色（0）
     */
    fun createStripeImage(
        bitmap: Bitmap,
        decodeResult: ImageDecodeAlgorithm.DecodeResult,
        cropStartPixel: Int = 0,
        cropEndPixel: Int? = null
    ): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val bitIntervals = decodeResult.bitIntervals
        
        // 创建白色背景图像
        val stripeImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        stripeImage.eraseColor(Color.WHITE)
        
        val pixels = IntArray(width * height)
        for (i in pixels.indices) {
            pixels[i] = Color.WHITE
        }
        
        // 根据每个bit的值填充对应的像素区域
        for (bitInfo in bitIntervals) {
            val pixelStart = bitInfo.pixelStart.toInt()
            val pixelEnd = bitInfo.pixelEnd.toInt().coerceAtMost(width)
            val bitValue = bitInfo.bitValue
            
            // bit=0 → 白色, bit=1 → 黑色
            val color = if (bitValue == 0) Color.WHITE else Color.BLACK
            
            for (x in pixelStart until pixelEnd) {
                for (y in 0 until height) {
                    pixels[y * width + x] = color
                }
            }
        }
        
        stripeImage.setPixels(pixels, 0, width, 0, 0, width, height)
        
        // 应用裁剪
        val finalCropEnd = cropEndPixel ?: width
        val cropWidth = finalCropEnd - cropStartPixel
        
        if (cropWidth <= 0 || cropStartPixel < 0 || finalCropEnd > width) {
            Log.e(TAG, "条纹图裁剪参数错误: cropStart=$cropStartPixel, cropEnd=$finalCropEnd")
            return stripeImage
        }
        
        return Bitmap.createBitmap(stripeImage, cropStartPixel, 0, cropWidth, height)
    }
    
    /**
     * 拼接两个图像：尾帧（左）+ 头帧（右）
     */
    fun concatenateFrames(tailFrame: Bitmap, headFrame: Bitmap): Bitmap {
        val height = max(tailFrame.height, headFrame.height)
        val width = tailFrame.width + headFrame.width
        
        val concatenated = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        concatenated.eraseColor(Color.BLACK)
        
        val pixels = IntArray(width * height)
        
        // 复制尾帧（左侧）
        val tailPixels = IntArray(tailFrame.width * tailFrame.height)
        tailFrame.getPixels(tailPixels, 0, tailFrame.width, 0, 0, tailFrame.width, tailFrame.height)
        
        for (y in 0 until tailFrame.height) {
            for (x in 0 until tailFrame.width) {
                pixels[y * width + x] = tailPixels[y * tailFrame.width + x]
            }
        }
        
        // 复制头帧（右侧）
        val headPixels = IntArray(headFrame.width * headFrame.height)
        headFrame.getPixels(headPixels, 0, headFrame.width, 0, 0, headFrame.width, headFrame.height)
        
        for (y in 0 until headFrame.height) {
            for (x in 0 until headFrame.width) {
                pixels[y * width + (tailFrame.width + x)] = headPixels[y * headFrame.width + x]
            }
        }
        
        concatenated.setPixels(pixels, 0, width, 0, 0, width, height)
        
        Log.d(TAG, "拼接完成: 尾帧=${tailFrame.width}x${tailFrame.height}, 头帧=${headFrame.width}x${headFrame.height}, 结果=${width}x${height}")
        
        return concatenated
    }
    
    /**
     * 规范化黑白条纹图
     */
    fun normalizeStripeImage(stripeImage: Bitmap): Bitmap {
        Log.d(TAG, "规范化条纹图...")
        Log.d(TAG, "原始图像尺寸: ${stripeImage.width}x${stripeImage.height}")
        
        val width = stripeImage.width
        val height = stripeImage.height
        
        // 分析第一行的条纹
        val firstRowPixels = IntArray(width)
        stripeImage.getPixels(firstRowPixels, 0, width, 0, 0, width, 1)
        
        // 找到条纹的变化点
        data class StripeInfo(val start: Int, val end: Int, val width: Int, val value: Int)
        val changes = mutableListOf<StripeInfo>()
        
        var currentValue = firstRowPixels[0]
        var start = 0
        
        for (i in 1 until width) {
            if (firstRowPixels[i] != currentValue) {
                changes.add(StripeInfo(start, i, i - start, currentValue))
                currentValue = firstRowPixels[i]
                start = i
            }
        }
        changes.add(StripeInfo(start, width, width - start, currentValue))
        
        Log.d(TAG, "找到 ${changes.size} 个条纹")
        
        // 离散化条纹宽度并过滤
        val normalizedStripes = mutableListOf<Pair<Int, Int>>() // width, color
        
        for (stripe in changes) {
            val w = stripe.width
            
            // 删除≤5像素的条纹
            if (w <= 5) {
                Log.d(TAG, "删除条纹: 宽度${w}像素 (≤5)")
                continue
            }
            
            // 离散化宽度
            val newWidth = when {
                w < 30 -> 20
                w < 50 -> 40
                w < 70 -> 60
                w < 90 -> 80
                else -> 100
            }
            
            normalizedStripes.add(Pair(newWidth, stripe.value))
        }
        
        // 创建规范化后的图像
        val leftPadding = 9 * 20  // 180像素白条纹
        val rightPadding = 5 * 20  // 100像素白条纹
        val stripesWidth = normalizedStripes.sumOf { it.first }
        val newWidth = leftPadding + stripesWidth + rightPadding
        
        val normalizedImage = Bitmap.createBitmap(newWidth, height, Bitmap.Config.ARGB_8888)
        normalizedImage.eraseColor(Color.WHITE)
        
        val pixels = IntArray(newWidth * height)
        for (i in pixels.indices) {
            pixels[i] = Color.WHITE
        }
        
        // 填充规范化的条纹
        var currentX = leftPadding
        for ((stripeWidth, color) in normalizedStripes) {
            for (x in currentX until (currentX + stripeWidth).coerceAtMost(newWidth)) {
                for (y in 0 until height) {
                    pixels[y * newWidth + x] = color
                }
            }
            currentX += stripeWidth
        }
        
        normalizedImage.setPixels(pixels, 0, newWidth, 0, 0, newWidth, height)
        
        Log.d(TAG, "规范化后尺寸: ${newWidth}x${height}")
        
        return normalizedImage
    }
    
    /**
     * 旋转图像180度
     */
    fun rotate180(bitmap: Bitmap): Bitmap {
        val matrix = android.graphics.Matrix()
        matrix.postRotate(180f)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
    
    /**
     * 垂直拼接两个图像（上下拼接）
     */
    fun verticalConcatenate(topBitmap: Bitmap, bottomBitmap: Bitmap): Bitmap {
        val width = max(topBitmap.width, bottomBitmap.width)
        val height = topBitmap.height + bottomBitmap.height
        
        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        result.eraseColor(Color.BLACK)
        
        val pixels = IntArray(width * height)
        
        // 复制上半部分
        val topPixels = IntArray(topBitmap.width * topBitmap.height)
        topBitmap.getPixels(topPixels, 0, topBitmap.width, 0, 0, topBitmap.width, topBitmap.height)
        
        for (y in 0 until topBitmap.height) {
            for (x in 0 until topBitmap.width) {
                pixels[y * width + x] = topPixels[y * topBitmap.width + x]
            }
        }
        
        // 复制下半部分
        val bottomPixels = IntArray(bottomBitmap.width * bottomBitmap.height)
        bottomBitmap.getPixels(bottomPixels, 0, bottomBitmap.width, 0, 0, bottomBitmap.width, bottomBitmap.height)
        
        for (y in 0 until bottomBitmap.height) {
            for (x in 0 until bottomBitmap.width) {
                pixels[(topBitmap.height + y) * width + x] = bottomPixels[y * bottomBitmap.width + x]
            }
        }
        
        result.setPixels(pixels, 0, width, 0, 0, width, height)
        
        return result
    }
}


