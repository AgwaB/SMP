package com.example.leesd.smp;

import android.content.Intent;
import android.media.Rating;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.leesd.smp.DetailSearch.JsonDetail;
import com.example.leesd.smp.DetailSearch.Review;
import com.example.leesd.smp.RetrofitCall.AsyncResponseMaps;
import com.example.leesd.smp.RetrofitCall.DetailInfoNetworkCall;
import com.example.leesd.smp.RetrofitCall.DetailInfoService;
import com.example.leesd.smp.googlemaps.JsonMaps;
import com.example.leesd.smp.googlemaps.Result;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by leesd on 2018-03-23.
 */

public class DetailInfoActivity extends AppCompatActivity implements AsyncResponseMaps {
    private ListView listView;
    private DetailListViewAdapter adapter;
    private TextView txt_title, txt_address;
    private RatingBar ratingBar;
    private Result jsonResult;
    private int position;
    private JsonDetail jsonDetail;

    private HashMap<String, String> searchParams;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        listView = (ListView)findViewById(R.id.detailListView);
        txt_title = (TextView)findViewById(R.id.IDtitle);
        //txt_address = (TextView)findViewById(R.id.IDadress);
        ratingBar = (RatingBar)findViewById(R.id.ratingBar);

        adapter = new DetailListViewAdapter();
        listView.setAdapter(adapter);

        Intent intent = getIntent();

        jsonResult = (Result) intent.getSerializableExtra("jsonResult");
        position = intent.getIntExtra("position", 0);

        getDetailData(jsonResult.getPlaceId() , jsonResult.getReference());

        if(jsonResult.getRating() != null)
            ratingBar.setRating(Float.valueOf(Double.toString(jsonResult.getRating()))); // (float)의 명시적 형변환 안됨. double->string->float로 변환
        txt_title.setText(jsonResult.getName());
        //txt_address.setText(jsonResult.getVicinity());
    }

    public void getDetailData(String placeid, String reference){
        // add params to HashMap
        searchParams = new HashMap<String, String>();
        searchParams.put("placeid", placeid);
        //searchParams.put("reference", reference);
        searchParams.put("language", "ko");
        searchParams.put("key", getString(R.string.placesKey));


        // build retrofit object
        DetailInfoService detailInfoService = DetailInfoService.retrofit.create(DetailInfoService.class);

        // call GET request with category and HashMap params
        final Call<JsonDetail> call = detailInfoService.getPlaces("details", searchParams);

        // make a thread for http communication
        DetailInfoNetworkCall n = new DetailInfoNetworkCall();

        // set delegate for receiving response object
        n.delegate = DetailInfoActivity.this;

        // execute background service
        n.execute(call);
    }

    @Override
    public void processFinish(Response<JsonMaps> response) {

    }

    @Override
    public void processDetailFinish(Response<JsonDetail> response) {
        if(response!=null)
            jsonDetail = response.body();
            ArrayList<Review> review = null;
        if(jsonDetail.getResult().getReviews()!=null) {
            review = (ArrayList<Review>) jsonDetail.getResult().getReviews();

            for (int i = 0; i < review.size(); i++)
                adapter.addItem(review.get(i).getProfilePhotoUrl(), review.get(i).getRelativeTimeDescription(), review.get(i).getAuthorName(), review.get(i).getText(), review.get(i).getRating());

            adapter.notifyDataSetChanged();
        }
    }
}
