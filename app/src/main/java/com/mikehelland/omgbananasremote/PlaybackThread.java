package com.mikehelland.omgbananasremote;

import android.view.View;

class PlaybackThread extends Thread {

    Jam jam;
    boolean cancel;

    PlaybackThread(Jam jam) {
        this.jam = jam;
    }

    public void run() {

        if (jam == null)
            return;

        long lastBeatPlayed = System.currentTimeMillis() - jam.subbeatLength;
        long now;

        cancel = false;

        jam.playing = true;


        while (!cancel) {

            now = System.currentTimeMillis();
            jam.timeSinceLast = now - lastBeatPlayed;

            if (jam.timeSinceLast < jam.subbeatLength) {
                continue;
            }

            //lastBeatPlayed = now;
            lastBeatPlayed += jam.subbeatLength;

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

