package ru.magflayer.spectrum.presentation.common.extension

import org.slf4j.LoggerFactory

fun Any.logger(tag: String? = null) = lazy { LoggerFactory.getLogger(tag ?: javaClass.simpleName) }
