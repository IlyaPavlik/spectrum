package ru.magflayer.spectrum.presentation.pages.main.history;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.List;

import butterknife.BindView;
import ru.magflayer.spectrum.R;
import ru.magflayer.spectrum.domain.entity.ColorPhotoEntity;
import ru.magflayer.spectrum.domain.injection.InjectorManager;
import ru.magflayer.spectrum.presentation.common.android.BaseFragment;
import ru.magflayer.spectrum.presentation.common.android.helper.SwipeToDeleteCallback;
import ru.magflayer.spectrum.presentation.common.android.layout.Layout;
import ru.magflayer.spectrum.presentation.common.utils.AppUtils;
import ru.magflayer.spectrum.presentation.common.utils.DialogUtils;

@Layout(R.layout.fragment_history)
public class HistoryFragment extends BaseFragment implements HistoryView {

    private static final int REQUEST_PICK_IMAGE = 1;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 2;

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
        historyRecycler.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        SwipeToDeleteCallback callback = new SwipeToDeleteCallback(getContext()) {
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, final int direction) {
                openAcceptDeleteColor(viewHolder.getAdapterPosition());
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(historyRecycler);
        adapter.setItemSelectListener(position -> {
            ColorPhotoEntity entity = adapter.getItem(position);
            if (entity != null) {
                presenter.handleColorSelected(entity);
            }
        });
        adapter.setItemLongClickListener(this::openAcceptDeleteColor);
    }

    @Override
    public void showHistory(final List<ColorPhotoEntity> history) {
        adapter.setData(history);
        emptyView.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void openPickPhoto() {
        logger.debug("openPickPhoto");
        if (hasStoragePermission()) {
            requestPermission();
            return;
        }

        String title = getString(R.string.select_image_title);
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, title);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, REQUEST_PICK_IMAGE);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        Context context = getContext();
        if (context == null) {
            logger.warn("Context is null");
            return;
        }

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_PICK_IMAGE) {
                Uri dataUri = data.getData();
                try {
                    if (dataUri != null) {
                        ContentResolver resolver = context.getContentResolver();
                        logger.debug("Try to build bitmap from: {}", dataUri);
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(resolver, dataUri);
                        presenter.handleSelectedImage(AppUtils.getPath(getContext(), dataUri), bitmap);
                    } else {
                        logger.warn("Data uri is null");
                    }
                } catch (Exception e) {
                    logger.warn("Cannot load bitmap: ", e);
                }
            }
        } else {
            logger.warn("Result doesn't success: {}", resultCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openPickPhoto();
                }
            }
        }
    }

    private void openAcceptDeleteColor(final int position) {
        String title = getString(R.string.history_remove_title);
        String message = getString(R.string.history_remove_description);

        Dialog dialog = DialogUtils.buildYesNoDialog(getContext(), title, message, (dialogInterface, i) -> {
            presenter.removeColor(adapter.getItem(position));
            adapter.remove(position);
            emptyView.setVisibility(adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        });
        dialog.setOnCancelListener(dialog1 -> adapter.notifyItemChanged(position));
        dialog.show();
    }

    private boolean hasStoragePermission() {
        return ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_WRITE_EXTERNAL_STORAGE);
    }
}
