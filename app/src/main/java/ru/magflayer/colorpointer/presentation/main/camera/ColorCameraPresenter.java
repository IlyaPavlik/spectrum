package ru.magflayer.colorpointer.presentation.main.camera;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.graphics.Palette;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import ru.magflayer.colorpointer.presentation.common.BasePresenter;
import ru.magflayer.colorpointer.presentation.main.router.MainRouter;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ColorCameraPresenter extends BasePresenter<ColorCameraView, MainRouter> {

    private int previousColor = -1;

    @Inject
    public ColorCameraPresenter() {
        super();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    public void handleCameraSurface(final Bitmap bitmap) {
        Observable.create(new Observable.OnSubscribe<Palette>() {
            @Override
            public void call(Subscriber<? super Palette> subscriber) {
                try {
                    Palette palette = Palette.from(bitmap).generate();
                    subscriber.onNext(palette);
                } catch (Exception e) {
                    subscriber.onError(e);
                } finally {
                    subscriber.onCompleted();
                }
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<Palette, Boolean>() {
                    @Override
                    public Boolean call(Palette palette) {
                        return (palette.getVibrantColor(Color.BLACK) & palette.getMutedColor(Color.BLACK)) != previousColor;
                    }
                })
                .map(new Func1<Palette, List<Palette.Swatch>>() {
                    @Override
                    public List<Palette.Swatch> call(Palette palette) {
                        List<Palette.Swatch> colors = new ArrayList<>(palette.getSwatches());
                        previousColor = palette.getVibrantColor(Color.BLACK) & palette.getMutedColor(Color.BLACK);

                        Collections.sort(colors, new Comparator<Palette.Swatch>() {
                            @Override
                            public int compare(Palette.Swatch lhs, Palette.Swatch rhs) {
                                float leftValue = lhs.getHsl()[2];
                                float rightValue = rhs.getHsl()[2];

                                if (leftValue < rightValue) {
                                    return -1;
                                }

                                if (leftValue > rightValue) {
                                    return 1;
                                }

                                return 0;
                            }
                        });

                        return colors;
                    }
                })
                .subscribe(new Action1<List<Palette.Swatch>>() {
                    @Override
                    public void call(List<Palette.Swatch> colors) {
                        getView().showColors(colors);
                    }
                });
    }

    public void handleColorDetails(final Bitmap bmp) {
        int centerX = bmp.getWidth() / 2;
        int centerY = bmp.getHeight() / 2;

        Palette.Swatch color = new Palette.Swatch(bmp.getPixel(centerX, centerY), 1);
        getView().showColorDetails(color.getRgb(), color.getTitleTextColor());

    }
}
