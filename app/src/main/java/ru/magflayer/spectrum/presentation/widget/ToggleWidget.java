package ru.magflayer.spectrum.presentation.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.magflayer.spectrum.R;

public class ToggleWidget extends LinearLayout {

    private enum ModeType {SINGLE, MULTIPLE}

    public interface OnCheckChangedListener {
        void checkChanged(boolean isSingle);
    }

    @BindView(R.id.toggle_single)
    protected View singleView;
    @BindView(R.id.toggle_multiple)
    protected View multipleView;

    private ModeType currentType = ModeType.SINGLE;
    private OnCheckChangedListener onCheckChangedListener;

    public ToggleWidget(Context context) {
        super(context);
        init();
    }

    public ToggleWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ToggleWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ToggleWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
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

    private void init() {
        View view = inflate(getContext(), R.layout.widget_toggle, this);
        ButterKnife.bind(view);
        changeType(currentType);
    }

    private void changeType(ModeType modeType) {
        currentType = modeType;
        if (modeType == ModeType.SINGLE) {
            multipleView.setActivated(false);
            singleView.setActivated(true);
        } else {
            singleView.setActivated(false);
            multipleView.setActivated(true);
        }

        if (onCheckChangedListener != null) {
            onCheckChangedListener.checkChanged(modeType == ModeType.SINGLE);
        }
    }
}
