package com.mikehelland.omgbananasremote;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * User: m
 * Date: 11/15/13
 * Time: 11:01 PM
 */
public class VerticalSliderView extends View {

    float value = 0.5f;
    float startY;

    OnValueChangedListener onValueChanged = null;

    private Paint paint;

    private int width = -1;
    private int height = -1;

    private int marginX;
    private int marginY;

    private Paint valuePaint;

    public VerticalSliderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setARGB(255, 0, 0, 0);

        valuePaint = new Paint();
        valuePaint.setARGB(255, 255, 0, 0);
        paint.setShadowLayer(10, 0, 0, 0xFFFFFFFF);

    }

    public void onDraw(Canvas canvas) {

        if (height != getHeight()) {
            width = getWidth();
            height = getHeight();
            marginX = width / 64;
            marginY = height / 128;

        }

        canvas.drawRect(0, 0,
                width, height,
                paint);

        canvas.drawRect(marginX, marginY + height * (1.0f - value),
                width - marginX * 2, height - marginY * 2,
                valuePaint);

    }


    public boolean onTouchEvent(MotionEvent event) {

        float y = 1 - event.getY() / height;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            startY = y;
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            value = Math.max(0.0f, Math.min(1.0f, value - (startY - y)));
            startY = y;

            if (onValueChanged != null)
                onValueChanged.onValueChanged(value);

        }

        invalidate();
        return true;
    }

    static class OnValueChangedListener {
        void onValueChanged(float f) {}
    }

}