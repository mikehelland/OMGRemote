package com.mikehelland.omgbananasremote;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * User: m
 * Date: 11/15/13
 * Time: 11:01 PM
 */
public class MixerView extends View {

    private Paint paint;
    private Paint paintBlue;
    private Paint paintRed;
    private Paint paintGreen;

    private int width = -1;
    private int height = -1;

    private Instrument mChannel;
    private BluetoothConnection mConnection;

    private Paint topPanelPaint;
    private Paint paintText;

    private static final int TOUCHING_AREA_MUTE = 1;
    private static final int TOUCHING_AREA_VOLUME = 2;
    private static final int TOUCHING_AREA_PAN = 3;
    private static final int TOUCHING_AREA_NONE = 0;

    private int touchingArea = TOUCHING_AREA_NONE;

    private String channelName = "";

    private float muteButtonWidth = -1;
    private float volumeStart = -1;
    private final static float controlMargin = 5;
    private float volumeWidth = -1;
    private float panStart = -1;
    private float panWidth = -1;

    private long touchedMuteAt = 0l;

    final static private float volumeViewWeight = 0.65f;

    public MixerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setARGB(255, 255, 255, 255);
        paint.setShadowLayer(10, 0, 0, 0xFFFFFFFF);

        paintText = new Paint();
        paintText.setARGB(255, 255, 255, 255);
        paintText.setTextSize(36);

        paintBlue = new Paint();
        paintBlue.setARGB(128, 0, 0, 255);
        paintBlue.setStyle(Paint.Style.FILL);

        paintRed = new Paint();
        paintRed.setARGB(128, 255, 0, 0);
        paintRed.setStyle(Paint.Style.FILL_AND_STROKE);

        paintGreen = new Paint();
        paintGreen.setARGB(128, 0, 255, 0);
        paintGreen.setStyle(Paint.Style.FILL_AND_STROKE);

        topPanelPaint = new Paint();
        topPanelPaint.setARGB(255, 192, 192, 255);

        setBackgroundColor(Color.BLACK);

        setChannelName("Test Trackname");

    }

    public void setChannelName(String newChannelName) {
        channelName = newChannelName;
    }

    public void onDraw(Canvas canvas) {
        if (height == -1) {
            width = getWidth();
            height = getHeight();

            muteButtonWidth = paintText.measureText(" Mute ");
            volumeStart = muteButtonWidth + controlMargin;
            volumeWidth = (width - volumeStart) * volumeViewWeight - 2 * controlMargin;
            panStart = volumeStart + volumeWidth + controlMargin;
            panWidth = width - panStart - controlMargin;
        }

        float volume = mChannel.volume;
        float pan = mChannel.pan;

        float height2 = height / 2;

        canvas.drawRect(0, 0, muteButtonWidth, height,
                mChannel.isEnabled() ? paintGreen : paintRed);
        canvas.drawText(" Mute ", 0, height2, paintText);


        canvas.drawLine(volumeStart + controlMargin, height2, volumeStart + volumeWidth - controlMargin,
                height2, paint);

        canvas.drawLine(panStart + controlMargin, height2, width - controlMargin,
                height2, paint);

        canvas.drawText("L", panStart,
                height - 5, paintText);

        canvas.drawText("R", width - 35,
                height - 5, paintText);

        canvas.drawRect(volumeStart + volumeWidth * volume - controlMargin, 0,
                volumeStart + volumeWidth * volume + controlMargin, height,
                topPanelPaint);

        canvas.drawRect(volumeStart, 0, volumeStart + volumeWidth * volume, height,
                mChannel.isEnabled() ? paintGreen : paintRed);


        canvas.drawRect(panStart + panWidth / 2 + panWidth / 2 * pan - controlMargin, 0,
                panStart + panWidth / 2 + panWidth / 2* pan + controlMargin, height,
                topPanelPaint);

        if (pan < 0) {
            canvas.drawRect(panStart + panWidth / 2 + panWidth / 2 * pan, 0,
                    panStart + panWidth / 2, height,
                    paintBlue);
        }
        else {
            canvas.drawRect(panStart + panWidth / 2, 0,
                    panStart + panWidth / 2 + panWidth / 2 * pan, height,
                    paintBlue);
        }

        canvas.drawText(channelName, volumeStart + controlMargin, height2, paintText);
    }

    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();

        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {

            if (x <= muteButtonWidth) {
                mChannel.toggleEnabled();
                mConnection.writeString("SET_CHANNEL_ENABLED=" +
                        (mChannel.isEnabled()?"1":"0") + "," + mChannel.channelNumber + ";");
                touchingArea = TOUCHING_AREA_MUTE;
                touchedMuteAt = System.currentTimeMillis();
                final long thisMute = touchedMuteAt;

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                if (touchingArea == TOUCHING_AREA_MUTE &&
                                        touchedMuteAt == thisMute) {
                                    Toast.makeText(getContext(), "Channel Cleared!", Toast.LENGTH_SHORT).show();
                                    mConnection.writeString("CLEAR_CHANNEL=" + mChannel.channelNumber + ";");
                                }
                            }
                        },
                        1200);
            }
            else if (x <= volumeStart + volumeWidth) {
                touchingArea = TOUCHING_AREA_VOLUME;
            }
            else {
                touchingArea = TOUCHING_AREA_PAN;
            }

            performTouch(x);
        }

        if (action == MotionEvent.ACTION_MOVE) {

            performTouch(x);
        }

        if (action == MotionEvent.ACTION_UP) {

            touchingArea = TOUCHING_AREA_NONE;
        }

        invalidate();
        return true;
    }

    public void setup(BluetoothConnection connection, Instrument instrument) {
        mConnection = connection;
        mChannel = instrument;

        setChannelName(instrument.name);

    }
    private void performTouch(float x) {
        if (touchingArea == TOUCHING_AREA_VOLUME) {
            float volume = Math.max(0, Math.min(1, (x - volumeStart) / volumeWidth));
            mChannel.volume = volume;
            mConnection.writeString("SET_CHANNEL_VOLUME=" + volume + "," + mChannel.channelNumber + ";");
        }
        else if (touchingArea == TOUCHING_AREA_PAN) {
            float pan = Math.max(-1.0f, Math.min(1.0f, ((x - panStart) / panWidth - 0.5f) * 2));
            mChannel.pan = pan;
            mConnection.writeString("SET_CHANNEL_PAN=" + pan + "," + mChannel.channelNumber + ";");
        }
    }
}
