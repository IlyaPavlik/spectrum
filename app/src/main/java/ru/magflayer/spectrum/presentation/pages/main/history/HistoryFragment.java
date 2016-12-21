package ru.magflayer.spectrum.presentation.pages.main.history;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import ru.magflayer.spectrum.R;
import ru.magflayer.spectrum.domain.model.ColorPicture;
import ru.magflayer.spectrum.domain.model.ToolbarAppearance;
import ru.magflayer.spectrum.injection.InjectorManager;
import ru.magflayer.spectrum.presentation.common.BaseFragment;
import ru.magflayer.spectrum.presentation.common.BasePresenter;
import ru.magflayer.spectrum.presentation.common.Layout;
import ru.magflayer.spectrum.presentation.widget.ColorSelectedWidget;

@Layout(id = R.layout.fragment_history)
public class HistoryFragment extends BaseFragment implements HistoryView {

    @BindView(R.id.history_recycler)
    RecyclerView historyRecycler;
    @BindView(R.id.empty)
    TextView emptyView;

    @Inject
    protected HistoryPresenter presenter;

    private HistoryAdapter adapter;

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new HistoryAdapter(getContext());
        historyRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        historyRecycler.setAdapter(adapter);
        adapter.setItemClickListener(position -> {
            ColorPicture colorPicture = adapter.getItem(position);
            openHistoryDetailsDialog(colorPicture.getSwatches());
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        presenter.loadHistory();
    }

    @Override
    public void showHistory(List<ColorPicture> history) {
        adapter.setData(history);

        emptyView.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public ToolbarAppearance getToolbarAppearance() {
        return ToolbarAppearance.builder()
                .visible(ToolbarAppearance.Visibility.VISIBLE)
                .title(getString(R.string.history_toolbar_title))
                .build();
    }

    private void openHistoryDetailsDialog(List<Palette.Swatch> swatches) {
        ColorSelectedWidget widget = new ColorSelectedWidget(getContext());
        widget.showDialog(swatches);
    }
}
