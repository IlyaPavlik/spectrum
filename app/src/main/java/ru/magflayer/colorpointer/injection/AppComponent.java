package ru.magflayer.colorpointer.injection;

import javax.inject.Singleton;

import dagger.Component;
import ru.magflayer.colorpointer.presentation.main.MainActivity;
import ru.magflayer.colorpointer.presentation.main.camera.ColorCameraFragment;
import ru.magflayer.colorpointer.presentation.main.history.HistoryFragment;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    //ACTIVITY

    void inject(MainActivity activity);

    //FRAGMENT

    void inject(ColorCameraFragment fragment);

    void inject(HistoryFragment fragment);

    //PRESENTER

}
