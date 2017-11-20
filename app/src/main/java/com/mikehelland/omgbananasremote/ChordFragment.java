package com.mikehelland.omgbananasremote;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ChordFragment extends Fragment {

    BluetoothConnection mConnection;
    Jam mJam;
    private View mActiveButton;
    private View[] mButtons;
    private BluetoothDataCallback mCallback;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chords,
                container, false);

        int[] buttonIds = {R.id.chordI_button, R.id.chordII_button, R.id.chordIII_button, R.id.chordIV_button,
                R.id.chordV_button, R.id.chordVI_button, R.id.chordVII_button, R.id.chordVb_button};
        mButtons = new View[buttonIds.length];

        for (int i = 0; i < buttonIds.length; i++) {
            mButtons[i] = view.findViewById(buttonIds[i]);
        }

        mCallback = new BluetoothDataCallback() {
            @Override
            public void newData(String name, String value) {
                if (name.equals("JAMINFO_SCALE")) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setup();
                        }
                    });
                }
            }
        };

        mConnection.addDataCallback(mCallback);

        setup();

        return view;
    }

    private void setup() {

        int[] scale = mJam.getScale();

        for (View mButton : mButtons) {
            mButton.setEnabled(false);
            mButton.setBackgroundColor(Color.rgb(128, 128, 128));
        }

        int chord;
        int buttonI;
        for (int i = 0; i < scale.length; i++) {
            chord = scale[i];
            buttonI = getButtonForChord(chord);
            if (buttonI > -1) {
                setupButton(mButtons[buttonI], i);
            }
        }
    }

    void setupButton(final View button, final int i) {
        button.setEnabled(true);
        button.setBackgroundColor(Color.WHITE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mActiveButton != null) {
                    mActiveButton.setBackgroundColor(Color.WHITE);
                }
                RemoteControlBluetoothHelper.setChord(mConnection, i);
                button.setBackgroundColor(Color.GREEN);
                mActiveButton = button;
            }
        });
    }

    private int getButtonForChord(int chord) {
        if (chord == 0) {
            return 0;
        }
        if (chord == 2) {
            return 1;
        }
        if (chord == 3 || chord == 4) {
            return 2;
        }
        if (chord == 5) {
            return 3;
        }
        if (chord == 6) {
            return 7;
        }
        if (chord == 7) {
            return 4;
        }
        if (chord == 8 || chord == 9) {
            return 5;
        }
        if (chord == 10 || chord == 11) {
            return 6;
        }
        return -1;
    }

    @Override
    public void onPause() {
        super.onPause();
        mConnection.removeDataCallback(mCallback);
    }
}