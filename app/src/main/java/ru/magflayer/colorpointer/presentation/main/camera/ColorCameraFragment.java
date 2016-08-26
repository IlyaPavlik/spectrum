package ru.magflayer.colorpointer.presentation.main.camera;

import android.graphics.SurfaceTexture;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.TextureView;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import ru.magflayer.colorpointer.R;
import ru.magflayer.colorpointer.presentation.common.BaseFragment;
import ru.magflayer.colorpointer.presentation.common.BasePresenter;
import ru.magflayer.colorpointer.presentation.common.Layout;
import ru.magflayer.colorpointer.presentation.injection.InjectorManager;
import ru.magflayer.colorpointer.presentation.manager.CameraManager;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

@Layout(id = R.layout.fragment_color_camera)
public class ColorCameraFragment extends BaseFragment implements TextureView.SurfaceTextureListener {

    @BindView(R.id.camera)
    protected TextureView cameraView;
    @BindView(R.id.color_recycler)
    protected RecyclerView colorRecycler;

    @Inject
    protected ColorCameraPresenter presenter;

    @Inject
    protected CameraManager cameraManager;

    private Subscription subscription;
    private ColorCameraAdapter adapter;

    public static ColorCameraFragment newInstance() {
        return new ColorCameraFragment();
    }

    @NonNull
    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected void inject() {
        InjectorManager.getAppComponent().inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        adapter = new ColorCameraAdapter();
        colorRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        colorRecycler.setAdapter(adapter);

        cameraManager.open();
        cameraView.setSurfaceTextureListener(this);
        startColorMonitoring();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopColorMonitoring();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cameraManager.close();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        try {
            cameraManager.startCamera(surface);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private void startColorMonitoring() {
        subscription = Observable.interval(5, 2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Long>() {
                            @Override
                            public void call(Long aLong) {
                                Palette p = Palette.from(cameraView.getBitmap()).generate();
                                adapter.setColors(p.getSwatches());
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        });
    }

    private void stopColorMonitoring() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }
}
