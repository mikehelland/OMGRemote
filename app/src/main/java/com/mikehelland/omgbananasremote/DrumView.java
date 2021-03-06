package com.mikehelland.omgbananasremote;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * User: m
 * Date: 11/15/13
 * Time: 11:01 PM
 */
public class DrumView extends View {

    private Paint paint;
    private Paint paintOff;

    private int width = -1;
    private int height = -1;

    private int marginX;
    private int marginY;

    private int boxWidth;
    private int boxHeight;

    private int wide = 4;
    private int tall = 8;

    private boolean[] trackData;
    private boolean[][] data;
    private Jam mJam;

    private Paint topPanelPaint;
    private Paint paintText;

    private int firstRowButton = -1;

    private String[][] captions;
    private float[][] captionWidths;
    private int captionHeight;

    private Paint paintBeat;

    private Paint blackPaint;

    private int adjustUp = 12;
    private int adjustDown = 18;

    private boolean isLive = false;
    private int lastX = -1;
    private int lastY = -1;

    private DrumChannel mChannel;

    public DrumView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setARGB(255, 255, 255, 255);
        paint.setShadowLayer(10, 0, 0, 0xFFFFFFFF);

        paintText = new Paint();
        paintText.setARGB(255, 255, 255, 255);
        paintText.setTextSize(24);

        paintBeat = new Paint();
        paintBeat.setARGB(255, 255, 0, 0);

        paintOff = new Paint();
        paintOff.setARGB(255, 128, 128, 128);
        //paintOff.setShadowLayer(10, 0, 0, 0xFFFFFFFF);
        paintOff.setStyle(Paint.Style.STROKE);

        blackPaint = new Paint();
        blackPaint.setARGB(255, 0, 0, 0);
        blackPaint.setStyle(Paint.Style.STROKE);

        paintOff.setTextSize(paintText.getTextSize());
        blackPaint.setTextSize(22);


        topPanelPaint = new Paint();
        topPanelPaint.setARGB(255, 192, 192, 255);

        trackData = new boolean[256];

//        setBackgroundColor(Color.BLACK);
    }

    public void onDraw(Canvas canvas) {

        if (mJam == null || mChannel == null || tall == 0 || wide == -1) {
            return;
        }

        if (height != getHeight()) {
            width = getWidth();
            height = getHeight();
            marginX = width / 64;
            marginY = height / 128;
            boxWidth = width / (wide + 1);
            boxHeight = height / tall;

            if (boxHeight < 90) {
                blackPaint.setTextSize(12);
                adjustUp = 6;
                adjustDown = 8;
            }


            setCaptions();
        }

        canvas.drawRect(0, 0,
                boxWidth, height,
                topPanelPaint);

        if (mChannel.isEnabled())
            paintBeat.setARGB(255, 0, 255, 0);
        else
            paintBeat.setARGB(255, 255, 0, 0);


        boolean on;

        if (mJam != null && mJam.playing) {
            if (firstRowButton > -1) {
                int i = 1 + (mJam.currentSubbeat % wide);
                int j = mJam.currentSubbeat / wide;
                canvas.drawRect(boxWidth * i, j * boxHeight,
                        boxWidth * i + boxWidth, j * boxHeight + boxHeight,
                        paintBeat);
            } else {
                int i = mJam.currentSubbeat / mJam.subbeats;
                canvas.drawRect(boxWidth + boxWidth * i, 0,
                        boxWidth + boxWidth * i + boxWidth, height,
                        paintBeat);
            }
        }

        if (captions != null && captions.length > 0 && captionWidths != null && captionWidths.length > 0) {
            captionHeight = height / captions.length;
            for (int j = 0; j < captions.length; j++) {
                if (j < captions.length && j < captionWidths.length &&
                        captions[j] != null && captionWidths[j] != null) {
                    if (captionWidths[j].length == 1) {
                        canvas.drawText(captions[j][0], boxWidth / 2 - captionWidths[j][0] / 2,
                                j * captionHeight + captionHeight / 2 + 6, blackPaint);
                    } else {
                        canvas.drawText(captions[j][0], boxWidth / 2 - captionWidths[j][0] / 2,
                                j * captionHeight + captionHeight / 2 - adjustUp, blackPaint);
                        canvas.drawText(captions[j][1], boxWidth / 2 - captionWidths[j][1] / 2,
                                j * captionHeight + captionHeight / 2 + adjustDown, blackPaint);
                    }
                }
            }
        }

        for (int j = 0; j < tall; j++) {

            for (int i = 0; i < wide; i++) {
                if ((firstRowButton == -1 && (j >= data.length || j < 0)) ||
                        (firstRowButton > -1 && ((i + j * wide) >= trackData.length))) {
                    Log.e("MGH dv firstRowButton", "" + firstRowButton);
                    Log.e("MGH dv", "j=" + j + ", i=" + i + ", wide= " + wide + ", tall=" + tall);
                    Log.e("MGH dv soundname", mChannel.name);
                    Log.e("Bad DrumView", "break here");
                    break;
                }
                on = (firstRowButton == -1) ? data[j][i] : trackData[i + j * wide];

                canvas.drawRect(boxWidth + boxWidth * i + marginX, j * boxHeight + marginY,
                        boxWidth + boxWidth * i + boxWidth - marginX, j * boxHeight + boxHeight - marginY,
                        on ? paint : paintOff);

                if (firstRowButton == -1) {
                    canvas.drawText(Integer.toString(i + 1), boxWidth + i * boxWidth + boxWidth / 2 - 6,
                            boxHeight * j + boxHeight / 2 + 6, on ? paintOff : paintText);
                } else {
                    canvas.drawText(i == 0 ? Integer.toString(j + 1) : i == 1 ? "e" : i == 2 ? "+" : "a",
                            boxWidth + i * boxWidth + boxWidth / 2 - 6,
                            boxHeight * j + boxHeight / 2 + 6, on ? paintOff : paintText);
                }
            }
        }

    }


    public boolean onTouchEvent(MotionEvent event) {

        if (mJam == null || mChannel == null || boxWidth == 0 || boxHeight == 0) {
            return true;
        }

        int boxX = (int) Math.floor(event.getX() / boxWidth);
        int boxY = (int) Math.floor(event.getY() / boxHeight);

        boxX = Math.min(wide, Math.max(0, boxX));
        boxY = Math.min(tall - 1, Math.max(0, boxY));

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            if (boxX == 0) {
                if (captionHeight > 0) {
                    handleFirstColumn((int) Math.floor(event.getY() / captionHeight));
                }
            } else {
                handleTouch(boxX - 1, boxY);
                isLive = true;
            }
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (isLive && boxX > 0) {

                if (boxX != lastX || boxY != lastY) {
                    handleTouch(boxX - 1, boxY);
                }
            }
        }

        lastX = boxX;
        lastY = boxY;

        if (event.getAction() == MotionEvent.ACTION_UP) {
            isLive = false;
            lastX = -1;
            lastY = -1;
        }

        invalidate();
        return true;
    }

    private void handleTouch(int x, int y) {
        //todo
        //((Main)getContext()).onModify();

        if (firstRowButton == -1) {
            if (y > -1 && y < data.length && x > -1 && x < data[y].length) {
                data[y][x] = !data[y][x];
                mChannel.setPattern(y, x * mJam.subbeats, data[y][x]);
            }
        } else {
            int i = x % wide + y * wide;

            if (i >= 0 && trackData.length > i) {
                mChannel.setPattern(firstRowButton, i, !trackData[i]);
            }
        }

    }

    public void setJam(Jam jam, DrumChannel channel) {
        mJam = jam;
        mChannel = channel;
        Log.d("MGH DrumView setJam", "setting wide");
        firstRowButton = -1;
        wide = mJam.beats;
        tall = mChannel.pattern.length;

        data = new boolean[tall][wide];
        int subbeats = mJam.subbeats;

        for (int i = 0; i < tall; i++) {
            for (int j = 0; j < wide; j++) {
                data[i][j] = mChannel.pattern[i][j * subbeats];
            }
        }

        setCaptions();
        mJam.addInvalidateOnBeatListener(this);
    }

    void handleFirstColumn(int y) {
        if (y < 0 || y >= mChannel.pattern.length) {
            return;
        }

        Log.d("MGH DrumView handleFC", "setting wide");
        if (firstRowButton == y) {
            firstRowButton = -1;
            wide = mJam.beats;
            tall = mChannel.pattern.length;
        } else {
            trackData = mChannel.getTrack(y);
            firstRowButton = y;
            wide = mJam.subbeats;
            tall = mJam.beats;
        }

        height = -1;
        postInvalidate();
    }

    public void setCaptions() {
        try { //fails occasionally, usually nullpointer
            String[] caps = mChannel.getCaptions();
            captionWidths = new float[caps.length][];
            captions = new String[caps.length][];
            for (int i = 0; i < caps.length; i++) {
                if (caps[i] != null) {
                    if (caps[i] == null) {
                        caps[i] = "";
                    }
                    captions[i] = caps[i].split(" ");
                    captionWidths[i] = new float[captions[i].length];
                    for (int j = 0; j < captions[i].length; j++) {
                        captionWidths[i][j] = blackPaint.measureText(captions[i][j]) ;
                    }
                }
            }
        }
        catch (Exception excp) {
            Log.e("MGH DrumView", "Couldn't make captions");
        }
    }
}