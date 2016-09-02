package ru.magflayer.colorpointer.presentation.main.history;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import ru.magflayer.colorpointer.R;
import ru.magflayer.colorpointer.domain.model.ColorPicture;
import ru.magflayer.colorpointer.injection.InjectorManager;
import ru.magflayer.colorpointer.presentation.common.BaseFragment;
import ru.magflayer.colorpointer.presentation.common.BasePresenter;
import ru.magflayer.colorpointer.presentation.common.Layout;

@Layout(id = R.layout.fragment_history)
public class HistoryFragment extends BaseFragment implements HistoryView {

    @BindView(R.id.history_recycler)
    protected RecyclerView historyRecycler;

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

        adapter = new HistoryAdapter();
        historyRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        historyRecycler.setAdapter(adapter);

        presenter.loadHistory();
    }

    @Override
    public void showHistory(List<ColorPicture> history) {
        adapter.setHistory(history);
    }
}
