package com.mikehelland.omgbananasremote;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class ChordFragment extends Fragment {

    BluetoothConnection mConnection;
    Jam mJam;
    int[] chords = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.remote_controls,
                container, false);


        final ViewGroup instrumentList = (ViewGroup)view.findViewById(R.id.instrument_list);

        makeButtons(instrumentList);

        return view;
    }

    void makeButtons(ViewGroup list) {
        for (final int chord: chords) {
            makeButton(chord, list);
        }
    }

    void makeButton(final int chord, ViewGroup list) {
        Button button = new Button(getContext());
        button.setText(Integer.toString(chord));
        button.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RemoteControlBluetoothHelper.setChord(mConnection, chord);
            }
        });

        list.addView(button);
    }

}