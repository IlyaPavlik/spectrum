package ru.magflayer.spectrum.presentation.widget;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lombok.Getter;
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

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        State state = new State(super.onSaveInstanceState(), currentType.ordinal());
        bundle.putParcelable(State.STATE, state);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            State customState = bundle.getParcelable(State.STATE);

            if (customState == null) {
                super.onRestoreInstanceState(BaseSavedState.EMPTY_STATE);
                return;
            }

            currentType = ModeType.values()[customState.getModeType()];
            changeType(currentType);

            super.onRestoreInstanceState(customState.getSuperState());
            return;
        }
        // Stops a bug with the wrong state being passed to the super
        super.onRestoreInstanceState(BaseSavedState.EMPTY_STATE);
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

    protected static class State extends BaseSavedState {

        private static final String STATE = "toggle.state";

        @Getter
        private final int modeType;

        public State(Parcelable superState, int modeType) {
            super(superState);
            this.modeType = modeType;
        }
    }
}
