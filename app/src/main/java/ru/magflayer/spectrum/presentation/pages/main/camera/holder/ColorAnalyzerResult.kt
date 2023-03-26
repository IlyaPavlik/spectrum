package ru.magflayer.spectrum.presentation.pages.main.camera.holder

import androidx.annotation.ColorInt

sealed class ColorAnalyzerResult

data class CenterColorResult(@ColorInt val color: Int) : ColorAnalyzerResult()

data class SwatchesResult(val swatches: List<AnalyzerSwatch>) : ColorAnalyzerResult()

data class AnalyzerSwatch(@ColorInt val color: Int, val population: Int)
