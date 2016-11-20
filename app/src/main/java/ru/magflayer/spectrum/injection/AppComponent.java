package ru.magflayer.spectrum.injection;

import javax.inject.Singleton;

import dagger.Component;
import ru.magflayer.spectrum.presentation.pages.main.MainActivity;
import ru.magflayer.spectrum.presentation.pages.main.camera.ColorCameraFragment;
import ru.magflayer.spectrum.presentation.pages.main.history.HistoryFragment;
import ru.magflayer.spectrum.presentation.pages.main.splash.SplashFragment;
import ru.magflayer.spectrum.presentation.pages.splash.SplashActivity;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    //ACTIVITY

    void inject(MainActivity activity);

    void inject(SplashActivity activity);

    //FRAGMENT

    void inject(ColorCameraFragment fragment);

    void inject(HistoryFragment fragment);

    void inject(SplashFragment fragment);

    //PRESENTER

}
