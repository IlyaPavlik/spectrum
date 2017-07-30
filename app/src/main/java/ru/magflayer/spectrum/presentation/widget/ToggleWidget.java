package ru.magflayer.spectrum.presentation.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.magflayer.spectrum.R;
import ru.magflayer.spectrum.utils.ViewUtils;

public class ToggleWidget extends LinearLayout {

    private enum ModeType {SINGLE, MULTIPLE}

    public interface OnCheckChangedListener {
        void checkChanged(boolean isSingle);
    }

    @BindView(R.id.toggle_single_container)
    protected View singleContainerView;
    @BindView(R.id.toggle_multiple_container)
    protected View multipleContainerView;

    @BindView(R.id.toggle_single)
    protected View singleView;
    @BindView(R.id.toggle_multiple)
    protected View multipleView;

    private ModeType currentType = ModeType.SINGLE;
    private OnCheckChangedListener onCheckChangedListener;

    public ToggleWidget(Context context) {
        this(context, null);
    }

    public ToggleWidget(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ToggleWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @OnClick(R.id.toggle_single)
    public void onSingleClick() {
        changeType(ModeType.SINGLE);
    }

    @OnClick(R.id.toggle_multiple)
    public void onMultipleClick() {
        changeType(ModeType.MULTIPLE);
    }

    public void setOnCheckChangedListener(OnCheckChangedListener onCheckChangedListener) {
        this.onCheckChangedListener = onCheckChangedListener;
    }

    public boolean isSingle() {
        return currentType == ModeType.SINGLE;
    }

    public void rotateIcons(final int toDegrees) {
        ViewUtils.rotateView(singleView, toDegrees);
        ViewUtils.rotateView(multipleView, toDegrees);
    }

    private void init() {
        View view = inflate(getContext(), R.layout.widget_toggle, this);
        ButterKnife.bind(view);
        changeType(currentType);
    }

    private void changeType(ModeType modeType) {
        currentType = modeType;
        if (modeType == ModeType.SINGLE) {
            multipleContainerView.setActivated(false);
            singleContainerView.setActivated(true);
        } else {
            singleContainerView.setActivated(false);
            multipleContainerView.setActivated(true);
        }

        if (onCheckChangedListener != null) {
            onCheckChangedListener.checkChanged(modeType == ModeType.SINGLE);
        }
    }
}
