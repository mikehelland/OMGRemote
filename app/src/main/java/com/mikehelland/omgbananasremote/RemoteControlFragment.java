package com.mikehelland.omgbananasremote;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class RemoteControlFragment extends Fragment {

    BluetoothConnection mConnection;
    Jam mJam;
    BluetoothDataCallback mDataCallback;

    private ViewGroup instrumentList;
    private Button keyButton;
    private Button bpmButton;

    private Button playButton;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.remote_controls,
                container, false);

        instrumentList = (ViewGroup)view.findViewById(R.id.instrument_list);
        keyButton = (Button)view.findViewById(R.id.key_button);
        bpmButton = (Button)view.findViewById(R.id.bpm_button);

        Button loadButton = (Button) view.findViewById(R.id.load_button);
        Button addChannelButton = (Button) view.findViewById(R.id.add_channel_button);
        playButton = (Button)view.findViewById(R.id.play_button);

        Log.d("MGH RemoteControlF", "on create");

        makeInstrumentButtons(instrumentList);

        mDataCallback = new BluetoothDataCallback() {
            @Override
            public void newData(final String name, final String value) {

                final Activity activity = getActivity();
                if (activity == null) {
                    return;
                }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUI(name, value);
                    }
                });
            }
        };
        mConnection.addDataCallback(mDataCallback);

        bpmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BPMFragment f = new BPMFragment();
                f.mConnection = mConnection;
                f.mJam = mJam;
                showFragment(f);
            }
        });
        bpmButton.setText(mJam.getBPM() + " BPM");

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
        if (mJam.playing) {
            playButton.setBackgroundColor(Color.GREEN);
            playButton.setText(R.string.stop);
        }

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

        //RemoteControlBluetoothHelper.getJamInfo(mConnection);
        return view;
    }

    @SuppressLint("SetTextI18n")
    void updateUI(String name, String value) {
        switch (name) {
            case "JAMINFO_CHANNELS":
                instrumentList.removeAllViews();
                Log.d("MGH JAMINFO", "should be removing stuff now");
                makeInstrumentButtons(instrumentList);
                break;
            case "JAMINFO_SUBBEATLENGTH":
                bpmButton.setText(mJam.getBPM() + " BPM");
                break;
            case "JAMINFO_KEY":
            case "JAMINFO_SCALE":
                keyButton.setText(mJam.getKeyName());
                break;
            case "NEW_CHANNEL":
                try { //a jaminfo_channels command might have removed the new_channel already
                    makeInstrumentButton(
                            mJam.instruments.get(mJam.instruments.size() - 1),
                            instrumentList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "PLAY":
                playButton.setText(R.string.stop);
                playButton.setBackgroundColor(Color.GREEN);
                break;
            case "STOP":
                playButton.setText(R.string.play);
                playButton.setBackgroundColor(Color.RED);
                break;
            case "SAVED_JAMS":
                chooseSavedJam(value);
                break;
            case "SOUNDSETS":
                chooseSoundSet(value);
                break;
        }
    }

    void makeInstrumentButtons(ViewGroup instrumentList) {
        if (mJam == null) {
            return;
        }
        for (final Instrument instrument : mJam.instruments) {
            makeInstrumentButton(instrument, instrumentList);
        }
    }

    void makeInstrumentButton(final Instrument instrument, ViewGroup instrumentList) {
        try {
            Button button = new Button(getContext());
            button.setTextSize(22);
            button.setText(instrument.name);
            button.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onInstrumentClicked(instrument);
                }
            });

            button.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showChannelOptions(instrument);
                    return true;
                }
            });
            instrumentList.addView(button);
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
    }

    private void onInstrumentClicked(Instrument instrument) {
        InstrumentFragment f = new InstrumentFragment();
        f.mInstrument = instrument;
        f.mConnection = mConnection;
        f.mJam = mJam;
        showFragment(f);
    }

    private void showChannelOptions(Instrument instrument) {
        ChannelOptionsFragment f = new ChannelOptionsFragment();
        f.mInstrument = instrument;
        f.mConnection = mConnection;
        f.mJam = mJam;
        showFragment(f);
    }

    void showFragment(Fragment f) {
        FragmentManager fm = getFragmentManager();
        if (fm == null) {
            return;
        }

        FragmentTransaction ft = fm.beginTransaction();
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

    @Override
    public void onPause() {
        super.onPause();
        mConnection.removeDataCallback(mDataCallback);
    }
}