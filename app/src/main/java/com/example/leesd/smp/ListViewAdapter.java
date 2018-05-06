package com.example.leesd.smp;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.leesd.smp.googlemaps.JsonMaps;
import com.example.leesd.smp.googlemaps.Result;

import java.util.ArrayList;

/**
 * Created by leesd on 2018-03-23.
 */

public class ListViewAdapter extends BaseAdapter{

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>() ;

    // DetailFragment에서 retrofit 통신 후 data를 받아오면 adapter에도 그 값을 넘겨 준다. (listview 내부의 button 클릭 시 data를 넘겨주기 위해)
    private Result jsonResult;

    // ListViewAdapter의 생성자
    public ListViewAdapter() {

    }
    public void setJsonResult(Result jsonResult){ // list 각각의 데이터를 가져 오려고 만듬(JsonMap는 Result의 set)
        this.jsonResult = jsonResult;
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.item_icon) ;
        TextView titleTextView = (TextView) convertView.findViewById(R.id.item_title) ;
        TextView addressTextView = (TextView) convertView.findViewById(R.id.item_address) ;
        Button detailButton = (Button) convertView.findViewById(R.id.IDdetailInfo);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        final ListViewItem listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        Glide.with(context).load(listViewItem.getIcon()).into(iconImageView); // Glide로 URI->Drawble 변형
        titleTextView.setText(listViewItem.getTitle());
        addressTextView.setText(listViewItem.getAddress());
        detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // list 클릭시 , 해당 item 정보를 detailActivity로 넘겨준다.
                Intent intent = new Intent(context, DetailInfoActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("positionItem", listViewItem);
                intent.putExtra("jsonResult", jsonResult);
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String iconUrl, String title, String address) {
        ListViewItem item = new ListViewItem();

        item.setIcon(iconUrl);
        item.setTitle(title);
        item.setAddress(address);

        listViewItemList.add(item);
    }


}
