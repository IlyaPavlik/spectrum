package ru.magflayer.spectrum.presentation.pages.main.history;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.List;

import butterknife.BindView;
import ru.magflayer.spectrum.R;
import ru.magflayer.spectrum.domain.injection.InjectorManager;
import ru.magflayer.spectrum.domain.model.ColorPicture;
import ru.magflayer.spectrum.presentation.common.android.BaseFragment;
import ru.magflayer.spectrum.presentation.common.android.layout.Layout;
import ru.magflayer.spectrum.presentation.common.utils.DialogUtils;

@Layout(R.layout.fragment_history)
public class HistoryFragment extends BaseFragment implements HistoryView {

    @InjectPresenter
    HistoryPresenter presenter;

    @BindView(R.id.history_recycler)
    RecyclerView historyRecycler;
    @BindView(R.id.empty)
    TextView emptyView;

    private HistoryAdapter adapter;

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    protected void inject() {
        InjectorManager.getAppComponent().inject(this);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new HistoryAdapter(getContext());
        historyRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        historyRecycler.setAdapter(adapter);
        adapter.setItemSelectListener(position -> {
            ColorPicture colorPicture = adapter.getItem(position);
            if (colorPicture != null) {
                presenter.handleColorSelected(colorPicture);
            }
        });
        adapter.setItemLongClickListener(this::openAcceptDeleteColor);
    }

    @Override
    public void showHistory(final List<ColorPicture> history) {
        adapter.setData(history);
        emptyView.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    private void openAcceptDeleteColor(final int position) {
        String title = getString(R.string.history_remove_title);
        String message = getString(R.string.history_remove_description);

        DialogUtils.buildYesNoDialog(getContext(), title, message, (dialogInterface, i) -> {
            presenter.removeColor(adapter.getItem(position));
            adapter.remove(position);
            emptyView.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }).show();
    }
}
