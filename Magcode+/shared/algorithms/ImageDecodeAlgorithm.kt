package com.example.magcodeandroid.algorithm

import android.graphics.Bitmap
import kotlin.math.min

/**
 * 图像解码算法 - 对应image_decode_algorithm.py
 */
object ImageDecodeAlgorithm {
    
    /**
     * 使用加权平均算法进行灰度化
     * 公式: Gray = 0.299×R + 0.587×G + 0.114×B
     */
    fun grayscaleWeightedAverage(bitmap: Bitmap): IntArray {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        
        val grayPixels = IntArray(width * height)
        
        for (i in pixels.indices) {
            val pixel = pixels[i]
            val r = (pixel shr 16) and 0xFF
            val g = (pixel shr 8) and 0xFF
            val b = pixel and 0xFF
            
            // 使用加权平均公式
            val gray = (0.299 * r + 0.587 * g + 0.114 * b).toInt()
            grayPixels[i] = gray
        }
        
        return grayPixels
    }
    
    /**
     * 对灰度图像按列求平均，得到宽度维的平均像素值
     * 注意：这里的"列"是指图像的宽度方向
     */
    fun columnAverage(grayPixels: IntArray, width: Int, height: Int): DoubleArray {
        val columnAvg = DoubleArray(width)
        
        for (x in 0 until width) {
            var sum = 0.0
            for (y in 0 until height) {
                sum += grayPixels[y * width + x]
            }
            columnAvg[x] = sum / height
        }
        
        return columnAvg
    }
    
    /**
     * 将数据分成多个区间，计算每个区间的阈值
     * 阈值 = 最小值 + 0.2 * (最大值 - 最小值)
     */
    fun calculateIntervalThresholds(columnAvg: DoubleArray, intervalSize: Int = 135): DoubleArray {
        val numIntervals = columnAvg.size / intervalSize
        val thresholds = DoubleArray(numIntervals)
        
        for (i in 0 until numIntervals) {
            val startIdx = i * intervalSize
            val endIdx = min(startIdx + intervalSize, columnAvg.size)
            
            var minVal = Double.MAX_VALUE
            var maxVal = Double.MIN_VALUE
            
            for (j in startIdx until endIdx) {
                if (columnAvg[j] < minVal) minVal = columnAvg[j]
                if (columnAvg[j] > maxVal) maxVal = columnAvg[j]
            }
            
            thresholds[i] = minVal + 0.2 * (maxVal - minVal)
        }
        
        return thresholds
    }
    
    /**
     * 按区间阈值进行二值化
     * 小于阈值的置为255（白色），大于等于阈值的置为0（黑色）
     */
    fun binarizeByIntervals(columnAvg: DoubleArray, thresholds: DoubleArray, intervalSize: Int = 135): IntArray {
        val binaryResult = IntArray(columnAvg.size)
        
        for (i in thresholds.indices) {
            val startIdx = i * intervalSize
            val endIdx = min(startIdx + intervalSize, columnAvg.size)
            val threshold = thresholds[i]
            
            for (j in startIdx until endIdx) {
                binaryResult[j] = if (columnAvg[j] < threshold) 255 else 0
            }
        }
        
        return binaryResult
    }
    
    /**
     * 预处理二值化序列：将持续时间小于等于3个像素的255区间调整为0
     */
    fun preprocessBinarySequence(binarySequence: IntArray, min255Duration: Int = 3): IntArray {
        if (binarySequence.isEmpty()) return binarySequence
        
        val newSequence = binarySequence.clone()
        
        // 找到连续区间的变化点
        val changes = mutableListOf(0)
        for (i in 1 until binarySequence.size) {
            if (binarySequence[i] != binarySequence[i - 1]) {
                changes.add(i)
            }
        }
        changes.add(binarySequence.size)
        
        // 处理每个区间
        for (i in 0 until changes.size - 1) {
            val start = changes[i]
            val end = changes[i + 1]
            val length = end - start
            val value = binarySequence[start]
            
            // 如果是255区间且长度小于等于3，则将其设为0
            if (value == 255 && length <= min255Duration) {
                for (j in start until end) {
                    newSequence[j] = 0
                }
            }
        }
        
        return newSequence
    }
    
    data class BitInterval(
        val bitIndex: Int,
        val bitValue: Int,
        val pixelStart: Double,
        val pixelEnd: Double,
        val pixelCenter: Double,
        val pixelWidth: Double,
        val intervalIndex: Int,
        val intervalStart: Int,
        val intervalEnd: Int,
        val intervalLength: Int,
        val intervalValue: Int,
        val bitsInInterval: Int,
        val bitPositionInInterval: Int
    )
    
    data class DecodeResult(
        val decodedBits: List<Int>,
        val bitIntervals: List<BitInterval>,
        val binarySequence: IntArray
    )
    
    /**
     * 像素合并解码，并记录每个bit对应的像素区间
     * 将连续的0或255合并成区间，根据区间长度解码为bit
     */
    fun pixelMergeDecodeWithIntervals(binarySequence: IntArray): DecodeResult {
        if (binarySequence.isEmpty()) {
            return DecodeResult(emptyList(), emptyList(), binarySequence)
        }
        
        // 预处理：调整持续时间小于等于3个像素的255区间
        val processedSequence = preprocessBinarySequence(binarySequence)
        
        // 找到连续区间的变化点
        val changes = mutableListOf(0)
        for (i in 1 until processedSequence.size) {
            if (processedSequence[i] != processedSequence[i - 1]) {
                changes.add(i)
            }
        }
        changes.add(processedSequence.size)
        
        val decodedBits = mutableListOf<Int>()
        val bitIntervals = mutableListOf<BitInterval>()
        var bitIndex = 0
        
        for (i in 0 until changes.size - 1) {
            val intervalStart = changes[i]
            val intervalEnd = changes[i + 1]
            val intervalLength = intervalEnd - intervalStart
            val intervalValue = processedSequence[intervalStart]
            
            // 区间长度小于5的跳过
            if (intervalLength < 5) continue
            
            // 根据长度判断解码为几个bit
            val numBits: Int
            val bitValue: Int
            
            when {
                intervalLength in 5..29 -> {
                    numBits = 1
                    bitValue = if (intervalValue == 0) 1 else 0
                }
                intervalLength in 30..49 -> {
                    numBits = 2
                    bitValue = if (intervalValue == 0) 1 else 0
                }
                intervalLength in 50..69 -> {
                    numBits = 3
                    bitValue = if (intervalValue == 0) 1 else 0
                }
                intervalLength in 70..89 -> {
                    numBits = 4
                    bitValue = if (intervalValue == 0) 1 else 0
                }
                intervalLength >= 90 -> {
                    numBits = 5
                    bitValue = if (intervalValue == 0) 1 else 0
                }
                else -> continue
            }
            
            // 将像素区间平均分配给每个bit
            val pixelPerBit = intervalLength.toDouble() / numBits
            
            for (j in 0 until numBits) {
                val bitPixelStart = intervalStart + j * pixelPerBit
                val bitPixelEnd = intervalStart + (j + 1) * pixelPerBit
                val bitPixelCenter = (bitPixelStart + bitPixelEnd) / 2
                
                decodedBits.add(bitValue)
                
                bitIntervals.add(
                    BitInterval(
                        bitIndex = bitIndex,
                        bitValue = bitValue,
                        pixelStart = bitPixelStart,
                        pixelEnd = bitPixelEnd,
                        pixelCenter = bitPixelCenter,
                        pixelWidth = pixelPerBit,
                        intervalIndex = i,
                        intervalStart = intervalStart,
                        intervalEnd = intervalEnd,
                        intervalLength = intervalLength,
                        intervalValue = intervalValue,
                        bitsInInterval = numBits,
                        bitPositionInInterval = j
                    )
                )
                
                bitIndex++
            }
        }
        
        return DecodeResult(decodedBits, bitIntervals, binarySequence)
    }
    
    /**
     * 完整的图像处理流程
     */
    fun processImage(bitmap: Bitmap): DecodeResult? {
        try {
            // 1. 灰度化
            val grayPixels = grayscaleWeightedAverage(bitmap)
            
            // 2. 按列求平均
            val columnAvg = columnAverage(grayPixels, bitmap.width, bitmap.height)
            
            // 3. 计算区间阈值
            val thresholds = calculateIntervalThresholds(columnAvg)
            
            // 4. 二值化
            val binaryResult = binarizeByIntervals(columnAvg, thresholds)
            
            // 5. 像素合并解码
            val decodeResult = pixelMergeDecodeWithIntervals(binaryResult)
            
            return decodeResult
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}


