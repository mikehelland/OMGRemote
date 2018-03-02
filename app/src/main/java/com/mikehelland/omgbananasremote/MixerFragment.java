package com.mikehelland.omgbananasremote;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MixerFragment extends Fragment {

    private View mView;
    private ViewGroup mContainer;
    private List<View> mPanels = new CopyOnWriteArrayList<>();

    private LayoutInflater mInflater;

    Jam mJam;

    BluetoothConnection mConnection;
    BluetoothDataCallback mCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mInflater = inflater;
        mView = inflater.inflate(R.layout.mixer_fragment,
                container, false);

        setupPanels(inflater);

        mCallback = new BluetoothDataCallback() {
            @Override
            public void newData(String name, String value) {
                if ("JAMINFO_CHANNELS".equals(name)) {
                    mContainer.removeAllViewsInLayout();
                    Activity activity = getActivity();
                    if (activity != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setupPanels(mInflater);
                            }
                        });
                    }
                }
                if ("NEW_CHANNEL".equals(name)) {
                    Activity activity = getActivity();
                    if (activity != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setupPanel(mInflater, mJam.instruments.get(mJam.instruments.size() - 1));
                            }
                        });
                    }
                }
                if ("CHANNEL_ENABLED".equals(name) ||
                        "CHANNEL_VOLUME".equals(name) || "CHANNEL_PAN".equals(name)) {
                    for (View panel : mPanels)
                        panel.postInvalidate();
                }
            }
        };

        mConnection.addDataCallback(mCallback);

        return mView;
    }

    void setupPanels(LayoutInflater inflater) {


        mContainer = (ViewGroup)mView.findViewById(R.id.channel_list);
        for (Instrument instrument : mJam.instruments) {

            setupPanel(inflater, instrument);
        }

    }

    private void setupPanel(LayoutInflater inflater, Instrument instrument) {
        View controls = inflater.inflate(R.layout.mixer_panel, mContainer, false);
        mContainer.addView(controls);

        MixerView mixerView = (MixerView) controls.findViewById(R.id.mixer_view);
        mixerView.setup(mConnection, instrument);

        mPanels.add(mixerView);
    }

    public void onPause() {
        super.onPause();
        mConnection.removeDataCallback(mCallback);
    }
}

