package com.mikehelland.omgbananasremote;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class KeyFragment extends Fragment {

    BluetoothConnection mConnection;
    Jam mJam;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.key_fragment,
                container, false);

        ViewGroup keysLayout = (ViewGroup)view.findViewById(R.id.list_of_keys);
        ViewGroup scalesLayout = (ViewGroup)view.findViewById(R.id.list_of_scales);

        makeKeyButtons(keysLayout);
        makeScaleButtons(scalesLayout);

        return view;
    }

    private void makeKeyButtons(ViewGroup list) {
        for (int i = 0; i <  Jam.KEY_CAPTIONS.length; i++) {
            list.addView(makeKeyButton(i, Jam.KEY_CAPTIONS[i]));
        }
    }
    private void makeScaleButtons(ViewGroup list) {
        for (int i = 0; i <  Jam.SCALE_CAPTIONS.length; i++) {
            list.addView(makeScaleButton(i, Jam.SCALE_CAPTIONS[i]));
        }
    }
    private View makeKeyButton(final int i, String caption) {
        Button button = new Button(getContext());
        button.setText(caption);
        button.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RemoteControlBluetoothHelper.setKey(mConnection, i);
            }
        });

        return button;
    }
    private View makeScaleButton(final int i, String caption) {
        Button button = new Button(getContext());
        button.setText(caption);
        button.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RemoteControlBluetoothHelper.setScale(mConnection, Jam.SCALES[i]);
            }
        });

        return button;
    }
}