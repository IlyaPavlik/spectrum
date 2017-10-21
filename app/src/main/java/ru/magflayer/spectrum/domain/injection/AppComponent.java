package ru.magflayer.spectrum.domain.injection;

import javax.inject.Singleton;

import dagger.Component;
import ru.magflayer.spectrum.presentation.pages.main.MainActivity;
import ru.magflayer.spectrum.presentation.pages.main.camera.ColorCameraFragment;
import ru.magflayer.spectrum.presentation.pages.main.history.HistoryFragment;
import ru.magflayer.spectrum.presentation.pages.main.history.details.HistoryDetailsFragment;
import ru.magflayer.spectrum.presentation.pages.main.splash.SplashFragment;
import ru.magflayer.spectrum.presentation.pages.main.toolbar.ToolbarViewHolder;
import ru.magflayer.spectrum.presentation.pages.splash.SplashActivity;

@Singleton
@Component(modules = AppModule.class)
@SuppressWarnings("WeakerAccess")
public interface AppComponent {

    //ACTIVITY

    void inject(MainActivity activity);

    void inject(SplashActivity activity);

    //FRAGMENT

    void inject(ColorCameraFragment fragment);

    void inject(HistoryFragment fragment);

    void inject(SplashFragment fragment);

    void inject(HistoryDetailsFragment fragment);

    //VIEW HOLDER
    void inject(ToolbarViewHolder viewHolder);

}