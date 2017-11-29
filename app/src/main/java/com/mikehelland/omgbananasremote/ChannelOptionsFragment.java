package com.mikehelland.omgbananasremote;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ChannelOptionsFragment extends Fragment {

    Instrument mInstrument;
    BluetoothConnection mConnection;
    Jam mJam;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.channel_options,
                container, false);

        ListView list = (ListView)view.findViewById(R.id.surface_list);

        final String[] array = {"Vertical", "Fretboard", "Sequencer"};

        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, array);

        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i) {
                    case 0: mInstrument.surfaceType = Instrument.SurfaceType.PRESET_VERTICAL;
                        break;
                    case 1: mInstrument.surfaceType = Instrument.SurfaceType.PRESET_FRETBOARD;
                        break;
                    case 2: mInstrument.surfaceType = Instrument.SurfaceType.PRESET_SEQUENCER;
                        break;
                }

                mInstrument.defaultSurface = false;
                getFragmentManager().popBackStack();

            }
        });

        view.findViewById(R.id.clear_channel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RemoteControlBluetoothHelper.clearChannel(mConnection, mInstrument.channelNumber);
                getFragmentManager().popBackStack();
            }
        });

        return view;
    }

}