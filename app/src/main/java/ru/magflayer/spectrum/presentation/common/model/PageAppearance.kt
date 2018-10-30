package ru.magflayer.spectrum.presentation.common.model

data class PageAppearance(
        var floatingButtonState: FloatingButtonState = FloatingButtonState.INVISIBLE
) {

    class Builder {
        private val pageAppearance = PageAppearance()


        fun showFloatingButton(floatingButtonState: FloatingButtonState): Builder {
            pageAppearance.floatingButtonState = floatingButtonState
            return this
        }

        fun build(): PageAppearance {
            return pageAppearance
        }

    }

    companion object {
        fun builder(): Builder {
            return Builder()
        }
    }

    enum class FloatingButtonState {
        VISIBLE, INVISIBLE, NO_INFLUENCE
    }
}
