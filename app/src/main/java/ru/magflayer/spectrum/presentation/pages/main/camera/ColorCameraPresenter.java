package ru.magflayer.spectrum.presentation.pages.main.camera;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.util.Pair;
import android.support.v7.graphics.Palette;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import ru.magflayer.spectrum.data.local.ColorInfo;
import ru.magflayer.spectrum.domain.event.PictureSavedEvent;
import ru.magflayer.spectrum.domain.model.ColorPicture;
import ru.magflayer.spectrum.presentation.common.BasePresenter;
import ru.magflayer.spectrum.presentation.pages.main.router.MainRouter;
import ru.magflayer.spectrum.utils.Base64Utils;
import ru.magflayer.spectrum.utils.RxUtils;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class ColorCameraPresenter extends BasePresenter<ColorCameraView, MainRouter> {

    private int previousColor = -1;
    private Map<String, String> colorInfoMap = new HashMap<>();

    @Inject
    ColorCameraPresenter() {
        super();
    }

    void handleColorInfo(String colorInfoJson) {
        Gson gson = new Gson();
        List<ColorInfo> colorInfoList = gson.fromJson(colorInfoJson, new TypeToken<List<ColorInfo>>() {
        }.getType());
        execute(Observable.from(colorInfoList),
                new Action1<ColorInfo>() {
                    @Override
                    public void call(ColorInfo colorInfo) {
                        colorInfoMap.put(colorInfo.getId(), colorInfo.getName());
                    }
                });
    }

    void handleCameraSurface(final Bitmap bitmap) {
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

    void handleColorDetails(final Bitmap bmp) {
        int centerX = bmp.getWidth() / 2;
        int centerY = bmp.getHeight() / 2;

        Palette.Swatch color = new Palette.Swatch(bmp.getPixel(centerX, centerY), 1);
        getView().showColorDetails(color.getRgb(), color.getTitleTextColor());

        final String hexColor = String.format("#%06X", (0xFFFFFF & color.getRgb()));
        int newColor = Color.parseColor(hexColor);
        final int red = Color.red(newColor);
        final int green = Color.green(newColor);
        final int blue = Color.blue(newColor);

        execute(Observable.from(colorInfoMap.keySet())
                        .reduce(Pair.create("", Integer.MAX_VALUE), new Func2<Pair<String, Integer>, String, Pair<String, Integer>>() {
                            @Override
                            public Pair<String, Integer> call(Pair<String, Integer> currentMin, String s) {
                                int otherColor = Color.parseColor(s);

                                int otherRed = Color.red(otherColor);
                                int otherGreen = Color.green(otherColor);
                                int otherBlue = Color.blue(otherColor);
                                int newFi = (int) (Math.pow(otherRed - red, 2)
                                        + Math.pow(otherGreen - green, 2)
                                        + Math.pow(otherBlue - blue, 2));

                                int result = currentMin.second > newFi ? newFi : currentMin.second;
                                String color = currentMin.second > newFi ? s : currentMin.first;
                                return Pair.create(color, result);
                            }
                        })
                        .filter(new Func1<Pair<String, Integer>, Boolean>() {
                            @Override
                            public Boolean call(Pair<String, Integer> aDouble) {
                                return aDouble.second != Integer.MAX_VALUE;
                            }
                        })
                .debounce(500, TimeUnit.MILLISECONDS)
                , new Action1<Pair<String, Integer>>() {
                    @Override
                    public void call(Pair<String, Integer> result) {
                        logger.debug("Show color: {}", result.first);
                        getView().showColorName(colorInfoMap.get(result.first));
                    }
                });
    }

    void saveColorPicture(final Bitmap bitmap, final List<Palette.Swatch> swatches) {
        Observable.create(new Observable.OnSubscribe<ColorPicture>() {
            @Override
            public void call(Subscriber<? super ColorPicture> subscriber) {
                try {
                    String pictureBase64 = Base64Utils.bitmapToBase64(bitmap);

                    ColorPicture colorPicture = new ColorPicture();
                    colorPicture.setDateInMillis(new Date().getTime());
                    colorPicture.setPictureBase64(pictureBase64);
                    colorPicture.setSwatches(swatches);
                    subscriber.onNext(colorPicture);
                } catch (Exception e) {
                    subscriber.onError(e);
                }
                subscriber.onCompleted();
            }
        })
                .compose(RxUtils.<ColorPicture>applySchedulers())
                .subscribe(new Action1<ColorPicture>() {
                               @Override
                               public void call(ColorPicture colorPicture) {
                                   appRealm.savePicture(colorPicture);
                               }
                           },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                logger.error("Error while save picture: ", throwable);
                            }
                        });
    }

    void openHistory() {
        getRouter().openHistory();
    }

    @Subscribe
    public void onPictureSaved(PictureSavedEvent event) {
        getView().showPictureSaved();
    }
}
