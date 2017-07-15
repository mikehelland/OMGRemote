package com.mikehelland.omgbananasremote;

import android.view.View;

/**
 * Created by m on 7/14/17.
 */

class PlaybackThread extends Thread {

    Jam jam;

    public void run() {

        if (jam == null)
            return;

        long lastBeatPlayed = 0;
        long now;

        boolean cancel = false;

        jam.playing = true;


        while (!cancel) {

            now = System.currentTimeMillis();
            jam.timeSinceLast = now - lastBeatPlayed;

            if (jam.timeSinceLast < jam.subbeatLength) {
                continue;
            }

            lastBeatPlayed = now;

            jam.currentSubbeat++;

            if (jam.currentSubbeat == jam.beats * jam.subbeats) {
                jam.currentSubbeat = 0;

                for (View iv : jam.viewsToInvalidateOnNewMeasure) {
                    iv.postInvalidate();
                }

            }

            for (View iv : jam.viewsToInvalidateOnBeat) {
                iv.postInvalidate();
            }

        }

        jam.playing = false;

    }

}

