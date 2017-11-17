package com.mikehelland.omgbananasremote;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MixerFragment extends Fragment {

    private View mView;

    private View oscControls;

    Jam mJam;

    BluetoothConnection mConnection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.mixer_fragment,
                container, false);

        setupPanels(inflater);

        return mView;
    }

    void setupPanels(LayoutInflater inflater) {


        ViewGroup container = (ViewGroup)mView.findViewById(R.id.channel_list);
        View controls;
        for (Instrument instrument : mJam.instruments) {

            controls = inflater.inflate(R.layout.mixer_panel, container, false);
            container.addView(controls);

            ((MixerView) controls.findViewById(R.id.mixer_view)).
                    setup(mConnection, instrument);

        }

    }

}

