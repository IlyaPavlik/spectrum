package ru.pavlik.colorlive.presentation.injection;

import javax.inject.Singleton;

import dagger.Component;
import ru.pavlik.colorlive.presentation.main.camera.ColorCameraFragment;
import ru.pavlik.colorlive.presentation.main.camera.ColorCameraPresenter;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    void inject(ColorCameraFragment fragment);

    void inject(ColorCameraPresenter presenter);

}
