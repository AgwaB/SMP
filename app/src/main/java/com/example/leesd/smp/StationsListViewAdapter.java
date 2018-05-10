package com.example.leesd.smp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.leesd.smp.googlemaps.Result;

import java.util.ArrayList;

public class StationsListViewAdapter extends BaseAdapter {
    public ArrayList<Result> stations = new ArrayList<>();

    @Override
    public int getCount() {
        return stations.size();
    }

    @Override
    public Object getItem(int position) {
        return stations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.fragment_station_item, parent, false);
        }

        TextView stationName = convertView.findViewById(R.id.station_name);
        TextView stationWeight = convertView.findViewById(R.id.station_weight);

        Result station = stations.get(position);
        stationName.setText(station.getName());
        stationWeight.setText(String.valueOf(station.getWeight()));

        return convertView;
    }
}
