package ru.magflayer.spectrum.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull

abstract class BaseSingleModelRepository<T : Any> {

    protected val initialValue: T? = null
    private val _modelStateFlow = MutableStateFlow(initialValue)
    val modelStateFlow: Flow<T?> = _modelStateFlow
    val modelNotNullStateFlow: Flow<T> = _modelStateFlow.filterNotNull()

    fun emitModel(model: T) {
        _modelStateFlow.value = model
    }

}