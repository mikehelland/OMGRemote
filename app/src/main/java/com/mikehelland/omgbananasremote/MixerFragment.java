package com.mikehelland.omgbananasremote;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class MixerFragment extends Fragment {

    private View mView;

    private List<View> mPanels = new ArrayList<>();

    Jam mJam;

    BluetoothConnection mConnection;
    BluetoothDataCallback mCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.mixer_fragment,
                container, false);

        setupPanels(inflater);

        mCallback = new BluetoothDataCallback() {
            @Override
            public void newData(String name, String value) {
                if ("CHANNEL_ENABLED".equals(name)) {
                    //String[] data  = value.split(",");
                    //boolean enabled = !data[0].equals("0");
                    //int channelNumber = Integer.parseInt(data[1]);
                    //mJam.instruments.get(channelNumber).enabled = enabled;

                    for (View panel : mPanels)
                        panel.postInvalidate();
                }
            }
        };

        mConnection.addDataCallback(mCallback);

        return mView;
    }

    void setupPanels(LayoutInflater inflater) {


        ViewGroup container = (ViewGroup)mView.findViewById(R.id.channel_list);
        View controls;
        for (Instrument instrument : mJam.instruments) {

            controls = inflater.inflate(R.layout.mixer_panel, container, false);
            container.addView(controls);

            MixerView mixerView = (MixerView) controls.findViewById(R.id.mixer_view);
            mixerView.setup(mConnection, instrument);

            mPanels.add(mixerView);
        }

    }

    public void onPause() {
        super.onPause();
        mConnection.removeDataCallback(mCallback);
    }
}

