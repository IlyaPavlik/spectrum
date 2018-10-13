package ru.magflayer.spectrum.domain.entity

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ListType<T>(wrapper: Class<T>) : ParameterizedType {
    private val wrapped: Class<*>

    init {
        this.wrapped = wrapper
    }

    override fun getActualTypeArguments(): Array<Type> {
        return arrayOf(wrapped)
    }

    override fun getRawType(): Type {
        return List::class.java
    }

    override fun getOwnerType(): Type? {
        return null
    }
}
