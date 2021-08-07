package ru.magflayer.spectrum.presentation.common.model

import android.graphics.Bitmap

data class SurfaceInfo(
    val type: Type,
    val bitmap: Bitmap
) {

    enum class Type {
        SINGLE, MULTIPLE
    }

}
