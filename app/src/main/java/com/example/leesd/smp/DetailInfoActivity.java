package com.example.leesd.smp;

import android.content.Intent;
import android.media.Rating;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.example.leesd.smp.DetailSearch.JsonDetail;
import com.example.leesd.smp.DetailSearch.Photo;
import com.example.leesd.smp.DetailSearch.Review;
import com.example.leesd.smp.RetrofitCall.AsyncResponseMaps;
import com.example.leesd.smp.RetrofitCall.DetailInfoNetworkCall;
import com.example.leesd.smp.RetrofitCall.DetailInfoService;
import com.example.leesd.smp.googlemaps.JsonMaps;
import com.example.leesd.smp.googlemaps.Result;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by leesd on 2018-03-23.
 */

public class DetailInfoActivity extends AppCompatActivity implements AsyncResponseMaps {
    private final String photoBaseUrl = "https://maps.googleapis.com/maps/api/place/photo?";
    private SliderLayout sliderLayout; // image slider layout :  external library
    private DefaultSliderView defaultSliderView;

    private ListView listView;
    private DetailListViewAdapter adapter;
    private TextView txt_title, txt_address, txt_noimages, txt_noreviews;
    private ImageButton btn_back;
    private RatingBar ratingBar;
    private Result jsonResult;
    private int position;
    private JsonDetail jsonDetail;

    private HashMap<String, String> searchParams;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); // makes window fullscreen
        setContentView(R.layout.activity_detail);



        sliderLayout = (SliderLayout)findViewById(R.id.slider);

        listView = (ListView)findViewById(R.id.detailListView);
        txt_title = (TextView)findViewById(R.id.IDtitle);
        btn_back = (ImageButton)findViewById(R.id.backButton);
        //txt_address = (TextView)findViewById(R.id.IDadress);
        txt_noimages = (TextView)findViewById(R.id.IDnoimages);
        txt_noreviews = (TextView)findViewById(R.id.IDnoreviews);
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


        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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

    public void setPhotos(String photoreference, String maxheight, String maxwidth){
        defaultSliderView = new DefaultSliderView(this);
        defaultSliderView.image(photoBaseUrl + "key=" + getString(R.string.placesKey) + "&photoreference=" + photoreference + "&maxheight=" + maxheight + "&maxwidth=" + maxwidth );
        sliderLayout.addSlider(defaultSliderView);
    }
    @Override
    public void processFinish(Response<JsonMaps> response) {

    }

    @Override
    public void processDetailFinish(Response<JsonDetail> response) {
        if(response!=null)
            jsonDetail = response.body();
            ArrayList<Review> review = null;
            ArrayList<Photo> photo = null;

        if(jsonDetail.getResult().getReviews()!=null) { // review data가 있으면 보여준다.
            review = (ArrayList<Review>) jsonDetail.getResult().getReviews();

            for (int i = 0; i < review.size(); i++)
                adapter.addItem(review.get(i).getProfilePhotoUrl(), review.get(i).getRelativeTimeDescription(), review.get(i).getAuthorName(), review.get(i).getText(), review.get(i).getRating());
            adapter.notifyDataSetChanged();
        }
        else{ // 없으면 리뷰 없다고 하는 textview 보여줌
            txt_noreviews.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }

        if(jsonDetail.getResult().getPhotos()!=null) { // photo data가 있으면 보여준다.
                photo = (ArrayList<Photo>) jsonDetail.getResult().getPhotos();
            for (int i = 0; i < photo.size(); i++)
                setPhotos(photo.get(i).getPhotoReference(), "300", "300");
        }
        else{ // 없으면 사진 없다고 하는 textview 보여줌
            txt_noimages.setVisibility(View.VISIBLE);
            sliderLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStop() {
        sliderLayout.startAutoCycle(); // To prevent a memory leak on rotation.
        super.onStop();
    }
}
