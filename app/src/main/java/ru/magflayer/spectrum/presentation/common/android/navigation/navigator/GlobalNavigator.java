package ru.magflayer.spectrum.presentation.common.android.navigation.navigator;

import android.app.Activity;
import android.content.Intent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.magflayer.spectrum.presentation.common.android.navigation.Screens;
import ru.magflayer.spectrum.presentation.pages.main.MainActivity;
import ru.magflayer.spectrum.presentation.pages.splash.SplashActivity;
import ru.terrakok.cicerone.Navigator;
import ru.terrakok.cicerone.commands.Back;
import ru.terrakok.cicerone.commands.Command;
import ru.terrakok.cicerone.commands.Forward;
import ru.terrakok.cicerone.commands.Replace;

public class GlobalNavigator implements Navigator {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Activity activity;

    public GlobalNavigator(final Activity activity) {
        this.activity = activity;
    }

    @Override
    public void applyCommands(final Command[] commands) {
        for (Command command : commands) applyCommand(command);
    }

    private void applyCommand(final Command command) {
        if (command instanceof Forward) {
            forward((Forward) command);
        } else if (command instanceof Replace) {
            replace((Replace) command);
        } else if (command instanceof Back) {
            back();
        } else {
            log.warn("Cannot handle command: {}", command);
        }
    }

    private void forward(final Forward command) {
        switch (command.getScreenKey()) {
            case Screens.MAIN_ACTIVITY_SCREEN:
                startActivity(new Intent(activity, MainActivity.class));
                break;
            case Screens.SPLASH_ACTIVITY_SCREEN:
                startActivity(new Intent(activity, SplashActivity.class));
                break;
            default:
                log.warn("Unknown screen: {}", command.getScreenKey());
        }
    }

    private void replace(final Replace command) {
        switch (command.getScreenKey()) {
            case Screens.MAIN_ACTIVITY_SCREEN:
            case Screens.SPLASH_ACTIVITY_SCREEN:
                forward(new Forward(command.getScreenKey(), command.getTransitionData()));
                activity.finish();
                break;
            default:
                log.warn("Unknown screen: {}", command.getScreenKey());
        }
    }

    private void back() {
        activity.finish();
    }

    private void startActivity(final Intent intent) {
        activity.startActivity(intent);
    }
}
