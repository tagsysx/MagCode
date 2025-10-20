package com.example.magcodeandroid.algorithm

/**
 * 帧分析器 - 对应vedio_selection.py
 */
object FrameAnalyzer {
    
    /**
     * 帧类型
     */
    enum class FrameType {
        HEAD,    // 头帧
        TAIL,    // 尾帧
        SKIP     // 跳过
    }
    
    data class PatternAnalysisResult(
        val startLeft: Int,      // 从左向右查找起始符位置
        val startRight: Int,     // 从右向左查找起始符位置
        val endLeft: Int,        // 从左向右查找终止符位置
        val endRight: Int,       // 从右向左查找终止符位置
        val bits: List<Int>
    )
    
    /**
     * 在bit序列中查找模式
     */
    private fun findPatternInSequence(bits: List<Int>, pattern: List<Int>, fromLeft: Boolean = true): Int {
        if (bits.size < pattern.size) return -1
        
        if (fromLeft) {
            // 从左向右查找
            for (i in 0..(bits.size - pattern.size)) {
                var match = true
                for (j in pattern.indices) {
                    if (bits[i + j] != pattern[j]) {
                        match = false
                        break
                    }
                }
                if (match) return i
            }
        } else {
            // 从右向左查找
            for (i in (bits.size - pattern.size) downTo 0) {
                var match = true
                for (j in pattern.indices) {
                    if (bits[i + j] != pattern[j]) {
                        match = false
                        break
                    }
                }
                if (match) return i
            }
        }
        
        return -1
    }
    
    /**
     * 分析帧的bit序列，查找起始符和终止符
     */
    fun analyzeFrameForPatterns(bits: List<Int>): PatternAnalysisResult {
        // 起始符：左侧（尾帧）用00101，右侧（头帧）用10100
        val startPatternLeft = listOf(0, 0, 1, 0, 1)   // 尾帧起始符
        val startPatternRight = listOf(1, 0, 1, 0, 0)  // 头帧起始符
        val endPattern = listOf(0, 1, 0, 1, 0)         // 终止符
        
        // 从左向右查找起始符00101（尾帧）
        val startLeft = findPatternInSequence(bits, startPatternLeft, fromLeft = true)
        
        // 从右向左查找起始符10100（头帧）
        val startRight = findPatternInSequence(bits, startPatternRight, fromLeft = false)
        
        // 从左向右和从右向左查找终止符
        val endLeft = findPatternInSequence(bits, endPattern, fromLeft = true)
        val endRight = findPatternInSequence(bits, endPattern, fromLeft = false)
        
        return PatternAnalysisResult(
            startLeft = startLeft,
            startRight = startRight,
            endLeft = endLeft,
            endRight = endRight,
            bits = bits
        )
    }
    
    /**
     * 根据起始符和终止符的位置分类帧类型
     * 按照优先级顺序判断：先判断头帧（右侧起始符），再判断尾帧（左侧起始符）
     */
    fun classifyFrameType(analysisResult: PatternAnalysisResult): FrameType {
        val startLeft = analysisResult.startLeft
        val startRight = analysisResult.startRight
        val bits = analysisResult.bits
        
        // 起始符长度：5个bit（00101或10100）
        // 需要的bit数：42个数据bit + 5个终止符bit = 47个bit（不包括起始符的5个bit）
        val startPatternLen = 5
        val totalBitsNeeded = 47
        val endPattern = listOf(0, 1, 0, 1, 0)
        
        // 优先判断头帧：从右向左扫描起始符10100
        // 头帧结构：[终止符01010] + [有效数据42bit] + [起始符10100]
        if (startRight != -1) {
            // 检查起始符前面是否有足够的47个bit
            if (startRight >= totalBitsNeeded) {
                // 提取起始符前面的47个bit
                val dataSection = bits.subList(startRight - totalBitsNeeded, startRight)
                // 检查前5个bit是否为终止符01010
                if (dataSection.size >= 5) {
                    val potentialEnd = dataSection.subList(0, 5)
                    if (potentialEnd == endPattern) {
                        return FrameType.HEAD
                    }
                }
            }
        }
        
        // 如果不是头帧，再判断尾帧：从左向右扫描起始符00101
        // 尾帧结构：[起始符00101] + [有效数据42bit] + [终止符01010]
        if (startLeft != -1) {
            // 起始符后面需要有47个bit
            val startEnd = startLeft + startPatternLen
            if (startEnd + totalBitsNeeded <= bits.size) {
                // 提取起始符后面的47个bit
                val dataSection = bits.subList(startEnd, startEnd + totalBitsNeeded)
                // 检查后5个bit是否为终止符01010
                if (dataSection.size >= 5) {
                    val potentialEnd = dataSection.subList(dataSection.size - 5, dataSection.size)
                    if (potentialEnd == endPattern) {
                        return FrameType.TAIL
                    }
                }
            }
        }
        
        // 都不符合则跳过此帧
        return FrameType.SKIP
    }
}


