package com.mikehelland.omgbananasremote;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

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
        final Button keyButton = (Button)view.findViewById(R.id.key_button);
        final Button bpmButton = (Button)view.findViewById(R.id.bpm_button);

        final Button loadButton = (Button)view.findViewById(R.id.load_button);
        final Button addChannelButton = (Button)view.findViewById(R.id.add_channel_button);
        final Button playButton = (Button)view.findViewById(R.id.play_button);

        mPlaybackThread = new PlaybackThread();
        mPlaybackThread.jam = mJam;
        mPlaybackThread.start();

        makeInstrumentButtons(instrumentList);

        mConnection.setDataCallback(new RemoteControlBluetoothDataCallback(mJam) {
            @Override
            public void newData(String name, String value) {
                super.newData(name, value);

                if ("JAMINFO_CHANNELS".equals(name)) {

                    if (getFragmentManager().getBackStackEntryCount() > 1) {
                        getFragmentManager().popBackStack();
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            instrumentList.removeAllViewsInLayout();
                            makeInstrumentButtons(instrumentList);
                        }
                    });
                }
                else if ("JAMINFO_SUBBEATLENGTH".equals(name)) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bpmButton.setText(mJam.getBPM() + " bpm");
                        }
                    });
                }
                else if ("JAMINFO_KEY".equals(name) || "JAMINFO_SCALE".equals(name)) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            keyButton.setText(mJam.getKeyName());
                        }
                    });
                }
                else if ("NEW_CHANNEL".equals(name)) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            makeInstrumentButton(
                                    mJam.instruments.get(mJam.instruments.size() - 1),
                                    instrumentList);
                        }
                    });
                }
                else if ("PLAY".equals(name)) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            playButton.setText("Stop");
                            playButton.setBackgroundColor(Color.GREEN);
                        }
                    });
                }
                else if ("STOP".equals(name)) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            playButton.setText("Play");
                            playButton.setBackgroundColor(Color.RED);
                        }
                    });
                }
                else if ("SAVED_JAMS".equals(name)) {
                    chooseSavedJam(value);
                }
                else if ("SOUNDSETS".equals(name)) {
                    chooseSoundSet(value);
                }

            }
        });

        bpmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BPMFragment f = new BPMFragment();
                f.mConnection = mConnection;
                f.mJam = mJam;
                showFragment(f);
            }
        });
        bpmButton.setText(mJam.getBPM() + " bpm");

        view.findViewById(R.id.chordprogression_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChordFragment f = new ChordFragment();
                f.mConnection = mConnection;
                f.mJam = mJam;
                showFragment(f);
            }
        });

        keyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyFragment f = new KeyFragment();
                f.mConnection = mConnection;
                f.mJam = mJam;
                showFragment(f);
            }
        });
        keyButton.setText(mJam.getKeyName());

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mJam.playing) {
                    RemoteControlBluetoothHelper.setStop(mConnection);
                }
                else {
                    RemoteControlBluetoothHelper.setPlay(mConnection);
                }
            }
        });

        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RemoteControlBluetoothHelper.getSavedJams(mConnection);
            }
        });

        addChannelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RemoteControlBluetoothHelper.getSoundSets(mConnection);
            }
        });

        view.findViewById(R.id.mixer_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MixerFragment f = new MixerFragment();
                f.mConnection = mConnection;
                f.mJam = mJam;
                showFragment(f);
            }
        });

        RemoteControlBluetoothHelper.getJamInfo(mConnection);
        return view;
    }

    void makeInstrumentButtons(ViewGroup instrumentList) {
        synchronized (mJam.instruments) {
            for (final Instrument instrument : mJam.instruments) {
                makeInstrumentButton(instrument, instrumentList);
            }
        }
    }

    void makeInstrumentButton(final Instrument instrument, ViewGroup instrumentList) {
        Button button = new Button(getContext());
        button.setText(instrument.name);
        button.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onInstrumenClicked(instrument);
            }
        });

        instrumentList.addView(button);
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

    private void chooseSavedJam(final String jams) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

            SavedJamsFragment f = new SavedJamsFragment();
            f.mSavedJamsString = jams;
            f.mConnection = mConnection;
            f.mJam = mJam;
            showFragment(f);

            }
        });
    }
    private void chooseSoundSet(final String data) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                SoundSetsFragment f = new SoundSetsFragment();
                f.mSoundSetsString = data;
                f.mConnection = mConnection;
                f.mJam = mJam;
                showFragment(f);

            }
        });
    }
}