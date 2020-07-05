package ru.magflayer.spectrum.presentation.common.android.layout

import androidx.annotation.LayoutRes

@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
annotation class Layout(@LayoutRes val value: Int)