package ru.magflayer.spectrum.presentation.pages.main.history.details;

import android.graphics.Color;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import com.arellomobile.mvp.InjectViewState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import ru.magflayer.spectrum.R;
import ru.magflayer.spectrum.data.android.ResourceManager;
import ru.magflayer.spectrum.domain.injection.InjectorManager;
import ru.magflayer.spectrum.domain.interactor.ColorsInteractor;
import ru.magflayer.spectrum.domain.manager.AnalyticsManager;
import ru.magflayer.spectrum.domain.model.AnalyticsEvent;
import ru.magflayer.spectrum.domain.model.ColorPicture;
import ru.magflayer.spectrum.domain.model.NcsColor;
import ru.magflayer.spectrum.presentation.common.model.ToolbarAppearance;
import ru.magflayer.spectrum.presentation.common.mvp.BasePresenter;
import ru.magflayer.spectrum.presentation.common.utils.ColorUtils;
import rx.Observable;

@InjectViewState
public class HistoryDetailsPresenter extends BasePresenter<HistoryDetailsView> {

    @Inject
    AnalyticsManager analyticsManager;
    @Inject
    ColorsInteractor colorsInteractor;
    @Inject
    ResourceManager resourceManager;

    private final int colorQuantity;
    private final Map<String, String> colorInfoMap = new HashMap<>();
    private final List<NcsColor> ncsColors = new ArrayList<>();

    HistoryDetailsPresenter(final long pictureId, final int colorQuantity) {
        this.colorQuantity = colorQuantity;
        loadColorNames();
        loadNcsColors();
        loadPicture(pictureId);
    }

    @Override
    protected void inject() {
        InjectorManager.getAppComponent().inject(this);
    }

    @Override
    public void attachView(final HistoryDetailsView view) {
        super.attachView(view);
        analyticsManager.logEvent(AnalyticsEvent.OPEN_HISTORY_DETAILS);
    }

    private void loadPicture(final long id) {
        ColorPicture colorPicture = appRealm.loadPicture(id);
        if (colorPicture != null) {
            getViewState().showPicture(colorPicture);
        }
    }

    void handleSelectedColor(final int color) {
        getViewState().showRgb(color);
        getViewState().showRyb(color);
        getViewState().showCmyk(color);
        getViewState().showHsv(color);
        getViewState().showXyz(color);
        getViewState().showLab(color);

        execute(Observable.just(color)
                        .map(c -> ColorUtils.dec2Ncs(ncsColors, c)),
                ncsName -> getViewState().showNcs(color, ncsName));

        handleColorDetails(color);
    }

    @Override
    public ToolbarAppearance getToolbarAppearance() {
        return ToolbarAppearance.builder()
                .visible(ToolbarAppearance.Visibility.VISIBLE)
                .title(resourceManager.getQuantityString(R.plurals.history_details_title, colorQuantity))
                .build();
    }

    private void handleColorDetails(final int color) {
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
                result -> getViewState().showColorName(colorInfoMap.get(result.first)),
                throwable -> logger.error("Error occurred: ", throwable));
    }

    private void loadColorNames() {
        execute(colorsInteractor.loadColorNames()
                        .flatMap(Observable::from),
                colorInfo -> colorInfoMap.put(colorInfo.getId(), colorInfo.getName()),
                throwable -> logger.error("Error occurs: ", throwable),
                () -> getViewState().selectFirstItem());
    }

    private void loadNcsColors() {
        execute(colorsInteractor.loadNscColors(),
                colors -> {
                    ncsColors.clear();
                    ncsColors.addAll(colors);
                });
    }
}
