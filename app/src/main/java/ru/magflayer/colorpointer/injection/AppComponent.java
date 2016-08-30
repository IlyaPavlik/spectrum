package ru.magflayer.colorpointer.injection;

import javax.inject.Singleton;

import dagger.Component;
import ru.magflayer.colorpointer.presentation.main.camera.ColorCameraFragment;
import ru.magflayer.colorpointer.presentation.main.camera.ColorCameraPresenter;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    void inject(ColorCameraFragment fragment);

    void inject(ColorCameraPresenter presenter);

}
