package ru.magflayer.spectrum.presentation.pages.main.history;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import ru.magflayer.spectrum.R;
import ru.magflayer.spectrum.domain.injection.InjectorManager;
import ru.magflayer.spectrum.domain.model.ColorPicture;
import ru.magflayer.spectrum.domain.model.ToolbarAppearance;
import ru.magflayer.spectrum.presentation.common.BaseFragment;
import ru.magflayer.spectrum.presentation.common.BasePresenter;
import ru.magflayer.spectrum.presentation.common.Layout;
import ru.magflayer.spectrum.utils.DialogUtils;

@Layout(R.layout.fragment_history)
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
        adapter.setItemSelectListener(position -> {
            ColorPicture colorPicture = adapter.getItem(position);
            openHistoryDetails(colorPicture);
        });
        adapter.setItemLongClickListener(this::openAcceptDeleteColor);
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

    private void openHistoryDetails(final ColorPicture colorPicture) {
        getRouter().openHistoryDetails(colorPicture);
    }

    private void openAcceptDeleteColor(int position) {
        String title = getString(R.string.history_remove_title);
        String message = getString(R.string.history_remove_description);

        DialogUtils.buildYesNoDialog(getContext(), title, message, (dialogInterface, i) -> {
            presenter.removeColor(adapter.getItem(position));
            adapter.remove(position);
            emptyView.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }).show();
    }
}
