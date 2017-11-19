package ru.magflayer.spectrum.presentation.pages.main.history.details;

import android.graphics.Color;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import ru.magflayer.spectrum.data.local.ColorInfo;
import ru.magflayer.spectrum.domain.manager.AnalyticsManager;
import ru.magflayer.spectrum.domain.model.AnalyticsEvent;
import ru.magflayer.spectrum.presentation.common.BasePresenter;
import ru.magflayer.spectrum.presentation.pages.main.router.MainRouter;
import rx.Observable;


public class HistoryDetailsPresenter extends BasePresenter<HistoryDetailsView, MainRouter> {

    @Inject
    AnalyticsManager analyticsManager;

    private Map<String, String> colorInfoMap = new HashMap<>();

    @Inject
    HistoryDetailsPresenter() {
    }

    void loadColors(String colorInfoJson) {
        Gson gson = new Gson();
        List<ColorInfo> colorInfoList = gson.fromJson(colorInfoJson, new TypeToken<List<ColorInfo>>() {
        }.getType());

        execute(Observable.from(colorInfoList),
                colorInfo -> colorInfoMap.put(colorInfo.getId(), colorInfo.getName()),
                throwable -> logger.error("Error occurs: ", throwable),
                () -> getView().colorLoaded());
    }

    void handleColorDetails(final int color) {
        final int red = Color.red(color);
        final int green = Color.green(color);
        final int blue = Color.blue(color);

        execute(Observable.from(colorInfoMap.keySet())
                        .reduce(Pair.create("", Integer.MAX_VALUE), (currentMin, s) -> {
                            if (TextUtils.isEmpty(s)) s = "#000000";
                            int otherColor = Color.parseColor(s);

                            int otherRed = Color.red(otherColor);
                            int otherGreen = Color.green(otherColor);
                            int otherBlue = Color.blue(otherColor);
                            int newFi = (int) (Math.pow(otherRed - red, 2)
                                    + Math.pow(otherGreen - green, 2)
                                    + Math.pow(otherBlue - blue, 2));

                            int result = currentMin.second > newFi ? newFi : currentMin.second;
                            String color1 = currentMin.second > newFi ? s : currentMin.first;
                            return Pair.create(color1, result);
                        })
                        .filter(aDouble -> aDouble.second != Integer.MAX_VALUE),
                result -> getView().showColorName(colorInfoMap.get(result.first)),
                throwable -> logger.error("Error occurred: ", throwable));
    }

    void sendAnalytics() {
        analyticsManager.logEvent(AnalyticsEvent.OPEN_HISTORY_DETAILS);
    }

}
