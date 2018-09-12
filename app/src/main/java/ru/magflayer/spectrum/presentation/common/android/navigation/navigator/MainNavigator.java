package ru.magflayer.spectrum.presentation.common.android.navigation.navigator;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import ru.magflayer.spectrum.presentation.common.android.navigation.Screens;
import ru.magflayer.spectrum.presentation.common.model.HistoryItem;
import ru.magflayer.spectrum.presentation.pages.main.camera.ColorCameraFragment;
import ru.magflayer.spectrum.presentation.pages.main.history.HistoryFragment;
import ru.magflayer.spectrum.presentation.pages.main.history.details.HistoryDetailsFragment;
import ru.terrakok.cicerone.android.SupportFragmentNavigator;

public class MainNavigator extends SupportFragmentNavigator {

    private final AppCompatActivity activity;

    /**
     * Creates SupportFragmentNavigator.
     *
     * @param rootActivity support fragment manager
     * @param containerId  id of the fragments container layout
     */
    public MainNavigator(final AppCompatActivity rootActivity, final int containerId) {
        super(rootActivity.getSupportFragmentManager(), containerId);
        this.activity = rootActivity;
    }

    @Override
    protected Fragment createFragment(final String screenKey, final Object data) {
        switch (screenKey) {
            case Screens.CAMERA_SCREEN:
                return ColorCameraFragment.newInstance();
            case Screens.HISTORY_SCREEN:
                return HistoryFragment.newInstance();
            case Screens.HISTORY_DETAILS_SCREEN:
                return HistoryDetailsFragment.newInstance((HistoryItem) data);
            default:
                return null;
        }
    }

    @Override
    protected void showSystemMessage(final String message) {
        //do nothing
    }

    @Override
    protected void exit() {
        activity.finish();
    }
}
