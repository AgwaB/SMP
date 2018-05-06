package com.example.leesd.smp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    private ArrayList<LocateInfo> listViewItemList = new ArrayList<LocateInfo>();

    public ListViewAdapter() {

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
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_search, parent, false);
        }

        TextView titleTextView = (TextView) convertView.findViewById(R.id.place_name);
        TextView locationTextView = (TextView) convertView.findViewById(R.id.place_location);

        LocateInfo listViewItem = listViewItemList.get(position);

        titleTextView.setText(listViewItem.getTitle());
        locationTextView.setText(listViewItem.getLocation());

        return convertView;
    }

    public void addItem(String title, String location) {
        LocateInfo item = new LocateInfo();

        item.setTitle(title);
        item.setLocation(location);

        listViewItemList.add(item);
    }
    public ArrayList<LocateInfo> getListViewItemList() {
        return this.listViewItemList;
    }

}
