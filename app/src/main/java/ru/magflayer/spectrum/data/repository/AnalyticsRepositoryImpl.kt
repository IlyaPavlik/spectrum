package ru.magflayer.spectrum.data.repository

import android.content.Context
import android.os.Bundle
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.magflayer.spectrum.domain.repository.AnalyticsRepository
import ru.magflayer.spectrum.presentation.common.extension.logger
import javax.inject.Inject

class AnalyticsRepositoryImpl @Inject constructor(@ApplicationContext context: Context) : AnalyticsRepository {

    private val logger by logger()
    private val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)

    override fun logEvent(event: String) {
        logger.debug("Analytics event sent: {}", event)
        analytics.logEvent(event, Bundle())
    }

    override fun logEvent(event: String, params: Map<String, Any>) {
        logger.debug("Analytics event sent: {}, data: {}", event, params)

        val bundle = bundleOf(*params.toList().toTypedArray())
        analytics.logEvent(event, bundle)
    }
}
