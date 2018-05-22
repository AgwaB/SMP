package com.example.leesd.smp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.leesd.smp.DetailSearch.JsonDetail;
import com.example.leesd.smp.RetrofitCall.AsyncResponseDetail;
import com.example.leesd.smp.RetrofitCall.AsyncResponseMaps;
import com.example.leesd.smp.RetrofitCall.DetailInfoNetworkCall;
import com.example.leesd.smp.RetrofitCall.GoogleMapsNetworkCall;
import com.example.leesd.smp.RetrofitCall.GooglePlaceService;
import com.example.leesd.smp.googlemaps.JsonMaps;

import com.example.leesd.smp.googlemaps.Result;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, DetailFragment.OnMyListener, StationsFragment.OnStationsFragmentListener {

    private GoogleMap map;
    private Button fragmentChange;
    private ArrayList<MarkerOptions> nearbyMarker = new ArrayList<MarkerOptions>();
    private boolean isFragmentChange = true ;
    private HashMap<String, String> searchParams;
    private ArrayList<JsonMaps> jsonMapsPack; // DetailFragment에서 주변 정보들을 받아 온 뒤, callback method를 통해 이 변수로 넣어준다.
	private ArrayList<LatLng> positionList; // list of user positions
	private LatLng medianLatLng;            // median latlng (received from RecoFragment)


	// static public void switchFragment(Fragment _fr, String fromTo) 에서 다룬다.
	static FrameLayout recoFrameLayout; // RecoFragment를 담는 FrameLayout
	static FrameLayout detailFrameLayout; // DetailFragment를 담는 FrameLayout
	static Fragment fr;
	static FragmentManager fm ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        fragmentChange = (Button)findViewById(R.id.fragmentBack);

//		recoFrameLayout = (FrameLayout)findViewById(R.id.recoView);
//		detailFrameLayout = (FrameLayout)findViewById(R.id.detailView);


		// fragment load
		fr = new RecoFragment();
		fm = getFragmentManager();
		FragmentTransaction fragmentTransaction = fm.beginTransaction();
		fragmentTransaction.add(R.id.fragment_view, fr);
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

	// 프래그먼트 교체는 여기서 다 다룬다. RecoFragmnet와 DetailFragment에서 쓴다.
	// 프래그먼트 전환 메커니즘 : Reco -> Detail 시 새로운 Fragment를 만들어줌
	//							  Detail -> Reco 시 기존에 있던 Fragment를 show
	// 기존 프래그먼트 보존 메커니즘 : Fragment를 show 하는 frameLayout을 각각 만든 후, Visibility를 이용한다.
	static public void switchFragment(Fragment _fr, String fromTo) {
//		if(fromTo.equals("ToReco")){ // 기존에 있던 fragment 정보를 사용한다 ( new를 통한 새로운 생성 X)
//			recoFrameLayout.setVisibility(View.VISIBLE);
//			detailFrameLayout.setVisibility(View.GONE);
//		}
//		else if(fromTo.equals("ToDetail")){
//			FragmentTransaction fragmentTransaction = fm.beginTransaction();
//			fragmentTransaction.replace(R.id.detailView, _fr);
//			fragmentTransaction.commit();
//
//			recoFrameLayout.setVisibility(View.GONE);
//			detailFrameLayout.setVisibility(View.VISIBLE);
//		}W

		FragmentTransaction tx = fm.beginTransaction();
		tx.replace(R.id.fragment_view, _fr).addToBackStack(null).commit();
	}

	@Override
    public void onReceivedData(ArrayList<JsonMaps> data) { // DetailFragment에서 retrofit 통신 후 listview를 뿌려주고 나서, 해당 정보에 대한 marker를 찍어준다.
		MarkerOptions markerOptions;
		LatLng latLng; // marker 위치
		jsonMapsPack = data;
		map.clear();

		if(data != null) { // only mark when data exist
			for (int x = 0; x < jsonMapsPack.size(); x++) {
				markerOptions = new MarkerOptions();
				for (int i = 0; i < jsonMapsPack.get(x).getResults().size(); i++) { // marker정보 받아와서 nearbyMarker 에 넣어주기
					latLng = new LatLng(jsonMapsPack.get(x).getResults().get(i).getGeometry().getLocation().getLat(), jsonMapsPack.get(x).getResults().get(i).getGeometry().getLocation().getLng());
					markerOptions.position(latLng) // 위치 set
							.title(jsonMapsPack.get(x).getResults().get(i).getName()); // 이름 set
					map.addMarker(markerOptions); // 지도에 marker 추가

					nearbyMarker.add(markerOptions); // marker 저장
				}
			}
		}
	}

	@Override
	public void onReceivedData(String name) { // DetailFragment에서 listview 클릭 시, 해당 item에 대한 marker를 focusing
        for(int i = 0 ; i < nearbyMarker.size() ; i ++){
            if(name == nearbyMarker.get(i).getTitle()){
                map.addMarker(nearbyMarker.get(i)).showInfoWindow(); // marker 찾아서 focusing 해준다 (사실 다시 찍어줌 ㅋ)
            }
        }
    }

	// receive median latlng from StationsFragment
	public void onReceivedResults(ArrayList<Result> results) {
		MarkerOptions markerOptions;
		LatLng latLng; // marker 위치map.clear();
		if(results != null) { // only mark when data exist
			for (int x = 0; x < results.size(); x++) {
				markerOptions = new MarkerOptions();
				for (int i = 0; i < results.size(); i++) { // marker정보 받아와서 nearbyMarker 에 넣어주기
					latLng = new LatLng(results.get(i).getGeometry().getLocation().getLat(), results.get(i).getGeometry().getLocation().getLng());
					markerOptions.position(latLng) // 위치 set
							.title(results.get(i).getName()); // 이름 set
					map.addMarker(markerOptions); // 지도에 marker 추가

					nearbyMarker.add(markerOptions); // marker 저장
				}
			}
		}
	}

}
