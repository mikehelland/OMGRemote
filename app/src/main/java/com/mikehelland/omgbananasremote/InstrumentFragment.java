package com.mikehelland.omgbananasremote;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by m on 7/30/16.
 */
public class InstrumentFragment extends Fragment {

    BluetoothConnection mConnection;
    Instrument mInstrument;
    Jam mJam;

    Fretboard mFretboard = null;

    View mView;

    private BluetoothDataCallback mDataCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final GuitarView surfaceView;
        final Channel channel;

        View view;

        if (mInstrument.surfaceType == Instrument.SurfaceType.PRESET_SEQUENCER) {

            view = inflater.inflate(R.layout.drum_fragment,
                    container, false);

            final DrumChannel drumChannel = new DrumChannel(mConnection, mInstrument);

            final DrumView drumView = (DrumView)view.findViewById(R.id.drummachine);
            drumView.setJam(mJam, drumChannel);

            setupDrumCallback(drumChannel, drumView);
            mView = drumView;
        }
        else {
            view = inflater.inflate(R.layout.instrument,
                    container, false);
            channel = new Channel(mConnection);

            surfaceView = (GuitarView) view.findViewById(R.id.drummachine);
            setupInstrumentCallback(channel, surfaceView);
            mView = surfaceView;
        }
        mConnection.addDataCallback(mDataCallback);

        RemoteControlBluetoothHelper.setChannel(mConnection, mInstrument.id);

        return view;
    }

    void setupInstrumentCallback(final Channel channel, final View view) {
        mDataCallback = new BluetoothDataCallback() {

            @Override
            public void newData(String name, String value) {

                if ("JAMINFO_CHANNELS".equals(name)) {
                    FragmentManager fm = getFragmentManager();
                    if (fm != null && fm.getBackStackEntryCount() > 1) {
                        fm.popBackStack();
                    }
                }

                if ("FRETBOARD_INFO".equals(name)) {

                    String[] dataParts = value.split("\\|");

                    if (dataParts.length > 1) {
                        channel.soundsetCaptions = dataParts[1].split(",");
                    }

                    String[] lowhigh = dataParts[0].split(",");
                    channel.lowNote = Integer.parseInt(lowhigh[0]);
                    channel.highNote = Integer.parseInt(lowhigh[1]);
                    channel.octave = Integer.parseInt(lowhigh[2]);
                    channel.chromatic = mInstrument.chromatic;

                    if (mInstrument.surfaceType == Instrument.SurfaceType.PRESET_FRETBOARD) {
                        mFretboard = new Fretboard(channel, mJam, getResources().getString(R.string.fretboard_json));
                    }

                    ((GuitarView)view).setJam(mJam, channel, mFretboard);

                }
                if ("NOTE_INFO".equals(name)) {
                    Log.d("MGH parse note info", value);
                    if (value.length() > 0) {

                        String[] noteStrings = value.split(",");

                        Note note;
                        channel.noteList = new ArrayList<>();

                        for (String noteString : noteStrings) {
                            note = new Note();
                            note.setRest(noteString.startsWith("-"));
                            String[] noteData = noteString.split("\\|");
                            note.setBeats(Math.abs(Double.parseDouble(noteData[0])));
                            note.setInstrumentNote(Integer.parseInt(noteData[1]));
                            channel.noteList.add(note);
                        }

                        ((GuitarView)view).setJam(mJam, channel, mFretboard);
                    }
                }
            }
        };
    }

    void setupDrumCallback(final DrumChannel drumChannel, final DrumView drumView) {
        mDataCallback = new BluetoothDataCallback() {
            @Override
            public void newData(String name, String value) {

                if ("JAMINFO_CHANNELS".equals(name)) {
                    FragmentManager fm = getFragmentManager();
                    if (fm != null && fm.getBackStackEntryCount() > 1) {
                        fm.popBackStack();
                    }
                }

                if ("DRUMBEAT_INFO".equals(name)) {
                    String[] tracks = value.split(",");
                    drumChannel.pattern = new boolean[tracks.length][mJam.getTotalSubbeats()];
                    drumChannel.captions = new String[tracks.length];
                    String[] data;
                    for (int i = 0; i < tracks.length; i++) {

                        data = tracks[i].split("\\|");
                        drumChannel.captions[i] = data[0];
                        for (int j = 1; j < drumChannel.pattern[i].length; j++) {
                            if (j < data.length) {
                                drumChannel.pattern[i][j - 1] = !data[j].equals("0");
                            }
                        }
                    }

                    drumView.setJam(mJam, drumChannel);
                    drumView.postInvalidate();
                }
            }
        };
    }

    public void onPause() {
        super.onPause();
        mJam.viewsToInvalidateOnBeat.remove(mView);
        mConnection.removeDataCallback(mDataCallback);
    }
}