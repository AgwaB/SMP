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

import com.example.leesd.smp.DetailSearch.JsonDetail;
import com.example.leesd.smp.RetrofitCall.AsyncResponseMaps;
import com.example.leesd.smp.RetrofitCall.GoogleMapsNetworkCall;
import com.example.leesd.smp.RetrofitCall.GooglePlaceService;
import com.example.leesd.smp.googlemaps.JsonMaps;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, AsyncResponseMaps, DetailFragment.OnMyListener {

    private GoogleMap map;
    private Button fragmentChange;
    private ArrayList<MarkerOptions> nearbyMarker = new ArrayList<MarkerOptions>();
    private boolean isFragmentChange = true ;
    private HashMap<String, String> searchParams;
    private ArrayList<JsonMaps> jsonMapsPack; // DetailFragment에서 주변 정보들을 받아 온 뒤, callback method를 통해 이 변수로 넣어준다.
	private ArrayList<LatLng> positionList; // list of user positions
	private LatLng medianLatLng;            // median latlng (received from RecoFragment)
	Fragment fr;
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
		searchParams = new HashMap<String, String>();
		
		searchParams.put("location", Double.toString(37.56) + "," + Double.toString(126.97));
		searchParams.put("radius", "500");
		searchParams.put("language", "ko");
		searchParams.put("type", "cafe");
		searchParams.put("key", getString(R.string.api));
		// build retrofit object
		GooglePlaceService googlePlaceService = GooglePlaceService.retrofit.create(GooglePlaceService.class);


		// set delegate for receiving response object
		GoogleMapsNetworkCall n = new GoogleMapsNetworkCall();
		// execute background service
		
		n.delegate = MapsActivity.this;

		
		// make a thread for http communication
		final Call<JsonMaps> call = googlePlaceService.getPlaces("nearbysearch", searchParams);
		// call GET request with category and HashMap params
		
		n.execute(call);
		// fragment load
		fr = new RecoFragment();
		
		FragmentManager fm = getFragmentManager();
		FragmentTransaction fragmentTransaction = fm.beginTransaction();
		fragmentTransaction.add(R.id.view, fr);
		fragmentTransaction.commit();
		
		/*
		 *  if user choose that three points
		 *  TEMPERATURE PARAMETERS
		 */
		positionList = new ArrayList<>();
		positionList.add(new LatLng(37.475486, 126.933380));    // 관악
		positionList.add(new LatLng(37.593227, 127.074668));    // 중랑
		positionList.add(new LatLng(37.575260, 126.893325));    // 상암
		
		Bundle bundle = new Bundle();
		// pass the position to recommendation fragment list data
		bundle.putParcelableArrayList("positions", positionList);
		
		fr.setArguments(bundle);
		//google map load
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
		mapFragment.getMapAsync(this);

	}
	
	@Override
	public void onMapReady(GoogleMap googleMap) {
		map = googleMap;
		
		setUpMap();
	}
	private void setUpMap() {
		
		LatLng SEOUL = new LatLng(37.56, 126.97);
		
		for (LatLng position : positionList) {
			MarkerOptions markerOptions = new MarkerOptions();
			markerOptions.position(position);
			map.addMarker(markerOptions);
		}
		map.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
		
		map.animateCamera(CameraUpdateFactory.zoomTo(10));
	}
	
	
	public void switchFragment() { // 버튼 클릭 시 프래그먼트 교체
		Fragment fr;
		if (isFragmentChange) {
			fr = new DetailFragment();
		} else {
			fr = new RecoFragment();
		}
		isFragmentChange = (isFragmentChange) ? false : true;
		

		FragmentManager fm = getFragmentManager();
		FragmentTransaction fragmentTransaction = fm.beginTransaction();
		fragmentTransaction.replace(R.id.view, fr);
		fragmentTransaction.commit();
		
	}
	public void processFinish(Response<JsonMaps> response) {
		Context context = getApplicationContext();
		CharSequence text = response.body().getStatus();
		int duration = Toast.LENGTH_LONG;
		
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}

	@Override
	public void processDetailFinish(Response<JsonDetail> response) {

	}
	@Override
	public void onReceivedData(ArrayList<JsonMaps> data) { // DetailFragment에서 retrofit 통신 후 listview를 뿌려주고 나서, 해당 정보에 대한 marker를 찍어준다.
        MarkerOptions markerOptions = null;
        LatLng latLng ; // marker 위치
        jsonMapsPack = data;
        map.clear();

        for (int x = 0 ; x < jsonMapsPack.size() ; x++)
            for(int i = 0 ; i < jsonMapsPack.get(x).getResults().size() ; i++){ // marker정보 받아와서 nearbyMarker 에 넣어주기
				markerOptions = new MarkerOptions();
                latLng = new LatLng(jsonMapsPack.get(x).getResults().get(i).getGeometry().getLocation().getLat(), jsonMapsPack.get(x).getResults().get(i).getGeometry().getLocation().getLng());
                markerOptions.position(latLng) // 위치 set
                        .title(jsonMapsPack.get(x).getResults().get(i).getName()); // 이름 set
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
	// receive median latlng from RecoFragment
	public void onReceivedData(Object data) {
		//DETERMINE WHO STARTED THIS ACTIVITY
//			Toast.makeText(this, "Received", Toast.LENGTH_SHORT).show();
		medianLatLng = (LatLng) data;
		
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(medianLatLng);
		map.addMarker(markerOptions);
	}
	
}
