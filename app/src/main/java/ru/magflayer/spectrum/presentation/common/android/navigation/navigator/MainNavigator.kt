package ru.magflayer.spectrum.presentation.common.android.navigation.navigator

import androidx.fragment.app.FragmentActivity
import ru.terrakok.cicerone.android.support.SupportAppNavigator

class MainNavigator(
        activity: FragmentActivity,
        containerId: Int
) : SupportAppNavigator(activity, containerId)
