package ru.magflayer.spectrum.presentation.common.model

data class ToolbarAppearance(
    val visible: Visibility,
    val title: String,
) {
    enum class Visibility {
        VISIBLE, INVISIBLE, NO_INFLUENCE
    }
}
