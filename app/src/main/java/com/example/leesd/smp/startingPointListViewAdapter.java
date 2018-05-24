package com.example.leesd.smp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class startingPointListViewAdapter extends BaseAdapter {

    private ArrayList<LocateInfo> listViewItemList = new ArrayList<LocateInfo>();

    public startingPointListViewAdapter() {

    }

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
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
            convertView = inflater.inflate(R.layout.listview_search, parent, false);
        }

        TextView titleTextView = (TextView) convertView.findViewById(R.id.place_name);
        TextView locationTextView = (TextView) convertView.findViewById(R.id.place_location);

        final LocateInfo listViewItem = listViewItemList.get(position);

        titleTextView.setText(listViewItem.getTitle());
        locationTextView.setText(listViewItem.getLocation());

        Button deleteBtn = (Button) convertView.findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                listViewItemList.remove(position);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    public void addItem(LocateInfo locateInfo) {
        listViewItemList.add(locateInfo);
    }
    public ArrayList<LocateInfo> getListViewItemList() {
        return this.listViewItemList;
    }

}
