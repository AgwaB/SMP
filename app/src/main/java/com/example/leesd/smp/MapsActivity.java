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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, AsyncResponseMaps, DetailFragment.OnMyListener {

    private GoogleMap map;
    private ArrayList<MarkerOptions> nearbyMarker = new ArrayList<MarkerOptions>();
    private Button fragmentChange;
    private boolean isFragmentChange = true ;
    private HashMap<String, String> searchParams;
    private JsonMaps jsonMaps; // DetailFragment에서 주변 정보들을 받아 온 뒤, callback method를 통해 이 변수로 넣어준다.


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
        map.animateCamera(CameraUpdateFactory.zoomTo(15));

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
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    @Override
    public void onReceivedData(Object data) { // DetailFragment에서 retrofit 통신 후 listview를 뿌려주고 나서, 해당 정보에 대한 marker를 찍어준다.
        MarkerOptions markerOptions;
        LatLng latLng ; // marker 위치
        jsonMaps = (JsonMaps)data;
        map.clear();

        for(int i = 0 ; i < jsonMaps.getResults().size() ; i++){ // marker정보 받아와서 nearbyMarker 에 넣어주기
            markerOptions = new MarkerOptions();
            latLng = new LatLng(jsonMaps.getResults().get(i).getGeometry().getLocation().getLat(), jsonMaps.getResults().get(i).getGeometry().getLocation().getLng());
            markerOptions.position(latLng) // 위치 set
                    .title(jsonMaps.getResults().get(i).getName()); // 이름 set
            map.addMarker(markerOptions); // 지도에 marker 추가

            nearbyMarker.add(markerOptions); // marker 저장
        }
    }

    @Override
    public void onReceiveData(String name) { // DetailFragment에서 listview 클릭 시, 해당 item에 대한 marker를 focusing
        for(int i = 0 ; i < nearbyMarker.size() ; i ++){
            if(name == nearbyMarker.get(i).getTitle()){
                map.addMarker(nearbyMarker.get(i)).showInfoWindow(); // marker 찾아서 focusing 해준다 (사실 다시 찍어줌 ㅋ)
            }
        }
    }
}
