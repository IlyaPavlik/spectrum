package ru.magflayer.spectrum.domain.repository

interface AnalyticsRepository {
    fun logEvent(event: String)
    fun logEvent(event: String, params: Map<String, Any>)
}
