package ru.magflayer.spectrum.presentation.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import ru.magflayer.spectrum.utils.AppUtils;

@SuppressWarnings("unused")
public class PointView extends View {

    private static final float RADIUS_DP = 4;
    private static final float STROKE_WIDTH_DP = 2;

    @SuppressWarnings("WeakerAccess")
    public interface OnPointChangeListener {
        void onPointChanged(float x, float y, int radius);
    }

    private Paint aimPaint;
    private float currentX;
    private float currentY;
    private float circleRadius;

    private RectF leftTopArc;
    private RectF leftBottomArc;
    private RectF rightTopArc;
    private RectF rightBottomArc;

    private boolean moveEnabled;
    private OnPointChangeListener onPointChangeListener;
    private boolean moveActive;

    public PointView(Context context) {
        this(context, null);
    }

    public PointView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public PointView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        circleRadius = AppUtils.convertDpToPixel(RADIUS_DP, getContext());
        float circleStrokeWidth = AppUtils.convertDpToPixel(STROKE_WIDTH_DP, getContext());

        aimPaint = new Paint();
        aimPaint.setAntiAlias(true);
        aimPaint.setStrokeWidth(circleStrokeWidth);
        aimPaint.setStyle(Paint.Style.STROKE);

        leftTopArc = new RectF();
        leftBottomArc = new RectF();
        rightTopArc = new RectF();
        rightBottomArc = new RectF();
    }

    public void setOnPointChangeListener(OnPointChangeListener onPointChangeListener) {
        this.onPointChangeListener = onPointChangeListener;
    }

    public float getCurrentX() {
        return currentX;
    }

    public float getCurrentY() {
        return currentY;
    }

    public int getRadius() {
        return (int) circleRadius;
    }

    public void setMoveActive(boolean moveActive) {
        this.moveActive = moveActive;
    }

    public void setAimColor(int color) {
        aimPaint.setColor(color);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);

        currentX = parentWidth / 2;
        currentY = parentHeight / 2;

        leftTopArc.set(currentX - 50, currentY - 50, currentX, currentY);
        leftBottomArc.set(currentX - 50, currentY, currentX, currentY + 50);
        rightTopArc.set(currentX, currentY - 50, currentX + 50, currentY);
        rightBottomArc.set(currentX, currentY, currentX + 50, currentY + 50);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(currentX, currentY, circleRadius, aimPaint);

        if (leftTopArc != null && leftBottomArc != null
                && rightTopArc != null && rightBottomArc != null) {
            canvas.drawArc(leftTopArc, 180, 90, false, aimPaint);
            canvas.drawArc(leftBottomArc, 180, -90, false, aimPaint);
            canvas.drawArc(rightTopArc, 0, -90, false, aimPaint);
            canvas.drawArc(rightBottomArc, 0, 90, false, aimPaint);
        }

        if (onPointChangeListener != null) {
            onPointChangeListener.onPointChanged(currentX, currentY, getRadius());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        if (!moveActive) {
            return super.onTouchEvent(event);
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDown(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                break;
        }

        return true;
    }

    private void touchDown(float x, float y) {
        if ((x > currentX - circleRadius && x < currentX + circleRadius)
                && (y > currentY - circleRadius && y < currentY + circleRadius)) {
            moveEnabled = true;
            currentX = x;
            currentY = y;
            invalidate();
        }
    }

    private void touchMove(float x, float y) {
        if (moveEnabled) {
            currentX = x;
            currentY = y;
            invalidate();
        }
    }

    private void touchUp() {
        moveEnabled = false;
    }
}
