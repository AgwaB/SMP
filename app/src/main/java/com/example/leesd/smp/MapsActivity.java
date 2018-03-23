package com.example.leesd.smp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.leesd.smp.RetrofitCall.AsyncResponseMaps;
import com.example.leesd.smp.RetrofitCall.GoogleMapsNetworkCall;
import com.example.leesd.smp.RetrofitCall.GooglePlaceService;
import com.example.leesd.smp.googlemaps.JsonMaps;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, AsyncResponseMaps {

    private GoogleMap map;
    private Button fragmentChange;
    private boolean isFragmentChange = true ;
    private HashMap<String, String> searchParams;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        fragmentChange = (Button)findViewById(R.id.fragmentChange);

        fragmentChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchFragment();
            }
        });

        //google map load
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);

        // add params to HashMap
        searchParams = new HashMap<String, String>();

        searchParams.put("location", Double.toString(37.56) + "," + Double.toString(126.97));
        searchParams.put("radius", "500");
        searchParams.put("type", "cafe");
        searchParams.put("language", "ko");
        searchParams.put("key", "AIzaSyBx7DqG04qR-hXB6wZpyW7pJhDB3OTelC4");

        // build retrofit object
        GooglePlaceService googlePlaceService = GooglePlaceService.retrofit.create(GooglePlaceService.class);

        // call GET request with category and HashMap params
        final Call<JsonMaps> call = googlePlaceService.getPlaces("nearbysearch", searchParams);

        // make a thread for http communication
        GoogleMapsNetworkCall n = new GoogleMapsNetworkCall();

        // set delegate for receiving response object
        n.delegate = MapsActivity.this;

        // execute background service
        n.execute(call);

        // fragment load
        Fragment fr = new RecoFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.view, fr);
        fragmentTransaction.commit();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        LatLng SEOUL = new LatLng(37.56, 126.97);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(SEOUL);
        markerOptions.title("서울");
        markerOptions.snippet("한국의 수도");
        map.addMarker(markerOptions);

        map.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
        map.animateCamera(CameraUpdateFactory.zoomTo(10));

    }

    public void switchFragment(){ // 버튼 클릭 시 프래그먼트 교체
        Fragment fr;
        if (isFragmentChange) {
            fr = new DetailFragment() ;
        } else {
            fr = new RecoFragment() ;
        }
        isFragmentChange = (isFragmentChange) ? false : true ;

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.view, fr);
        fragmentTransaction.commit();

    }


    public void processFinish(Response<JsonMaps> response){
        Context context = getApplicationContext();
        CharSequence text = response.body().getStatus();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
