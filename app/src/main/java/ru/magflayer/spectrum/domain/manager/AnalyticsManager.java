package ru.magflayer.spectrum.domain.manager;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AnalyticsManager {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private FirebaseAnalytics analytics;

    @Inject
    AnalyticsManager(final Context context) {
        analytics = FirebaseAnalytics.getInstance(context);
    }

    public void logEvent(final String event) {
        log.debug("Analytics event sent: {}", event);
        analytics.logEvent(event, new Bundle());
    }

    public void logEvent(final String event, final Bundle bundle) {
        log.debug("Analytics event sent: {}, data: {}", event, bundle);
        analytics.logEvent(event, bundle);
    }
}
