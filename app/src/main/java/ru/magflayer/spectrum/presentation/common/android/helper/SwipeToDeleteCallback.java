package ru.magflayer.spectrum.presentation.common.android.helper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import ru.magflayer.spectrum.R;

public abstract class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    private final Drawable deleteIcon;
    private final int intrinsicWidth;
    private final int intrinsicHeight;
    private final int backgroundColor;
    private final ColorDrawable background = new ColorDrawable();
    private final Paint clearPaint = new Paint();

    public SwipeToDeleteCallback(final Context context) {
        super(0, ItemTouchHelper.RIGHT);
        deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete);
        intrinsicWidth = deleteIcon.getIntrinsicWidth();
        intrinsicHeight = deleteIcon.getIntrinsicHeight();

        backgroundColor = ContextCompat.getColor(context, R.color.red);
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @Override
    public boolean onMove(final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;// We don't want support moving items up/down
    }

    @Override
    public void onChildDraw(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder,
                            final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {

        View itemView = viewHolder.itemView;
        int itemHeight = itemView.getBottom() - itemView.getTop();
        boolean isCanceled = dX == 0f && !isCurrentlyActive;

        if (isCanceled) {
            clearCanvas(c, itemView.getRight() + dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            return;
        }

        // Draw the red delete background
        background.setColor(backgroundColor);
        background.setBounds(
                itemView.getLeft(),
                itemView.getTop(),
                itemView.getLeft() + (int) dX,
                itemView.getBottom()
        );
        background.draw(c);

        // Calculate position of delete icon
        int iconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
        int iconMargin = (itemHeight - intrinsicHeight) / 2;
        int iconLeft = itemView.getLeft() + iconMargin;
        int iconRight = itemView.getLeft() + iconMargin + intrinsicWidth;
        int iconBottom = iconTop + intrinsicHeight;

        // Draw the delete icon
        deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
        deleteIcon.draw(c);

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private void clearCanvas(final Canvas c, final float left, final float top, final float right, final float bottom) {
        c.drawRect(left, top, right, bottom, clearPaint);
    }
}
