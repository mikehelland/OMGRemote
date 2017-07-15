package com.mikehelland.omgbananasremote;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by m on 7/30/16.
 */
public class RemoteControlFragment extends Fragment {

    BluetoothConnection mConnection;
    Jam mJam;
    PlaybackThread mPlaybackThread;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.remote_controls,
                container, false);


        final ViewGroup instrumentList = (ViewGroup)view.findViewById(R.id.instrument_list);

        mJam = new Jam();
        mPlaybackThread = new PlaybackThread();
        mPlaybackThread.jam = mJam;
        mPlaybackThread.start();



        mConnection.setDataCallback(new RemoteControlBluetoothDataCallback(mJam) {
            @Override
            public void newData(String name, String value) {
                super.newData(name, value);

                if ("SET_CHANNELS".equals(name)) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Button button;
                            for (final Instrument instrument : mJam.instruments) {
                                button = new Button(getContext());
                                button.setText(instrument.name);
                                button.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
                                instrumentList.addView(button);

                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        onInstrumenClicked(instrument);
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });


        RemoteControlBluetoothHelper.getJamInfo(mConnection);


        view.findViewById(R.id.bpm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BPMFragment f = new BPMFragment();
                f.mConnection = mConnection;
                f.mJam = mJam;
                showFragment(f);
            }
        });

        view.findViewById(R.id.chordprogression_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RemoteControlBluetoothHelper.getJamInfo(mConnection);
            }
        });


        return view;
    }

    void onInstrumenClicked(Instrument instrument) {

        InstrumentFragment f = new InstrumentFragment();
        f.mInstrument = instrument;
        f.mConnection = mConnection;
        f.mJam = mJam;
        showFragment(f);
    }

    void showFragment(Fragment f) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
        );
        ft.replace(R.id.main_layout, f);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();

    }

}