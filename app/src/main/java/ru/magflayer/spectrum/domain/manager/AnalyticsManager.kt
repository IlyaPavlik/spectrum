package ru.magflayer.spectrum.domain.manager

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.qualifiers.ApplicationContext
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsManager @Inject constructor(
    @ApplicationContext context: Context
) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)

    fun logEvent(event: String) {
        log.debug("Analytics event sent: {}", event)
        analytics.logEvent(event, Bundle())
    }

    fun logEvent(event: String, bundle: Bundle) {
        log.debug("Analytics event sent: {}, data: {}", event, bundle)
        analytics.logEvent(event, bundle)
    }
}
