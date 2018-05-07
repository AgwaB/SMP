package com.example.leesd.smp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.leesd.smp.googlemaps.Result;

import java.util.ArrayList;

/**
 * Created by leesd on 2018-05-06.
 */

public class DetailListViewAdapter extends BaseAdapter {

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ReviewListItem> listViewItemList = new ArrayList<ReviewListItem>() ;


    // ListViewAdapter의 생성자
    public DetailListViewAdapter() {

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
            convertView = inflater.inflate(R.layout.listview_detail, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView profileImageView = (ImageView) convertView.findViewById(R.id.userProfile) ;
        TextView whenTextView = (TextView) convertView.findViewById(R.id.IDwhen) ;
        TextView nameTextView = (TextView) convertView.findViewById(R.id.userName) ;
        TextView reviewTextView = (TextView) convertView.findViewById(R.id.IDreview) ;
        RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.IDratingBar);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        final ReviewListItem reviewListItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        Glide.with(context).load(reviewListItem.getProfileUrl()).into(profileImageView); // Glide로 URI->Drawble 변형
        whenTextView.setText(reviewListItem.getWhen());
        nameTextView.setText(reviewListItem.getName());
        reviewTextView.setText(reviewListItem.getReview());
        ratingBar.setRating(reviewListItem.getRating());

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
    public void addItem(String profileUrl, String when, String name, String review, int rating) {
        ReviewListItem item = new ReviewListItem();

        item.setProfileUrl(profileUrl);
        item.setWhen(when);
        item.setName(name);
        item.setReview(review);
        item.setRating(rating);

        listViewItemList.add(item);
    }
}
