package ru.magflayer.spectrum.presentation.widget;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.magflayer.spectrum.R;

public class ColorInfoWidget extends CardView {

    private static final int MAX_COLUMNS = 3;

    @BindView(R.id.color_info_title)
    TextView titleView;
    @BindView(R.id.color_info_container)
    ViewGroup paramContainer;

    private LayoutInflater inflater;

    public ColorInfoWidget(Context context) {
        this(context, null);
    }

    public ColorInfoWidget(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ColorInfoWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(final Context context) {
        View view = inflate(context, R.layout.widget_color_info, this);
        ButterKnife.bind(this, view);
        inflater = LayoutInflater.from(context);
    }

    public void setTitle(final CharSequence title) {
        titleView.setText(title);
    }

    public void setParams(final List<String> params) {
        paramContainer.removeAllViews();

        TableRow tableRow = null;
        for (String param : params) {
            if (tableRow == null || tableRow.getChildCount() % MAX_COLUMNS == 0) {
                tableRow = new TableRow(getContext());
                paramContainer.addView(tableRow);
            }
            TextView paramTextView = (TextView) inflater.inflate(R.layout.widget_color_info_item, tableRow, false);
            paramTextView.setText(param);
            tableRow.addView(paramTextView);
        }
    }

}
