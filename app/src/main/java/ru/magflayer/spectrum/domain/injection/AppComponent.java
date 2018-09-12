package ru.magflayer.spectrum.domain.injection;

import javax.inject.Singleton;

import dagger.Component;
import ru.magflayer.spectrum.presentation.pages.main.MainActivity;
import ru.magflayer.spectrum.presentation.pages.main.MainPresenter;
import ru.magflayer.spectrum.presentation.pages.main.camera.ColorCameraFragment;
import ru.magflayer.spectrum.presentation.pages.main.camera.ColorCameraPresenter;
import ru.magflayer.spectrum.presentation.pages.main.history.HistoryFragment;
import ru.magflayer.spectrum.presentation.pages.main.history.HistoryPresenter;
import ru.magflayer.spectrum.presentation.pages.main.history.details.HistoryDetailsFragment;
import ru.magflayer.spectrum.presentation.pages.main.history.details.HistoryDetailsPresenter;
import ru.magflayer.spectrum.presentation.pages.main.toolbar.ToolbarPresenter;
import ru.magflayer.spectrum.presentation.pages.main.toolbar.ToolbarViewHolder;
import ru.magflayer.spectrum.presentation.pages.splash.SplashActivity;
import ru.magflayer.spectrum.presentation.pages.splash.SplashPresenter;

@Singleton
@Component(modules = {AppModule.class, NavigationModule.class})
@SuppressWarnings("WeakerAccess")
public interface AppComponent {

    //ACTIVITY

    void inject(MainActivity activity);

    void inject(SplashActivity activity);

    //FRAGMENT

    void inject(ColorCameraFragment fragment);

    void inject(HistoryFragment fragment);

    void inject(HistoryDetailsFragment fragment);

    //VIEW HOLDER
    void inject(ToolbarViewHolder viewHolder);

    //PRESENTER
    void inject(SplashPresenter presenter);

    void inject(MainPresenter presenter);

    void inject(ToolbarPresenter presenter);

    void inject(ColorCameraPresenter presenter);

    void inject(HistoryPresenter presenter);

    void inject(HistoryDetailsPresenter presenter);

}
