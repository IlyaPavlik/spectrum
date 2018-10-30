package ru.magflayer.spectrum.presentation.common.android.layout

import android.support.annotation.LayoutRes

@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
annotation class Layout(@LayoutRes val value: Int)