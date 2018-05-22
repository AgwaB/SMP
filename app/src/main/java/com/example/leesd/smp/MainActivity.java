package com.example.leesd.smp;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button nextButton;
    PlaceAutocompleteFragment autocompleteFragment;
    LocationManager locationManager;
    LocationListener locationListener;
    startingPointListViewAdapter listViewAdapter;
    ListView listview;
    ArrayList<LocateInfo> listViewArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listViewAdapter = new startingPointListViewAdapter();
        listview = (ListView) findViewById(R.id.placeList);
        listview.setAdapter(listViewAdapter);
        listViewArrayList = listViewAdapter.getListViewItemList();

        //Google autosearch Fragment

        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.


                LocateInfo locationInfo = new LocateInfo();
                locationInfo.setTitle(String.valueOf(place.getName()));
                locationInfo.setLatitude(place.getLatLng().latitude);
                locationInfo.setLongitude(place.getLatLng().longitude);
                locationInfo.setLocation(place.getAddress());
                locationInfo.setID(place.getId());

                listViewAdapter.addItem(locationInfo);

                Log.i("TAG", "Place: " + place.getName());
                listViewAdapter.notifyDataSetChanged();



            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("TAG", "An error occurred: " + status);
            }
        });

        // 다음화면으로 넘어가기

        nextButton = (Button) findViewById(R.id.nextBtn);

        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listViewArrayList.size() < 1) {
                    Context context = getApplicationContext();
                    CharSequence text = "Set at least two meeting places.";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                } else {

                    double latSum = 0.0;
                    double lonSum = 0.0;

                    for(int i = 0 ; i < listViewArrayList.size() ; i ++) {
                        latSum += listViewArrayList.get(i).getLatitude();
                        lonSum += listViewArrayList.get(i).getLongitude();
                    }

                    LocateInfo finalPlace = new LocateInfo();
                    finalPlace.setLatitude(latSum / listViewArrayList.size());
                    finalPlace.setLongitude(lonSum / listViewArrayList.size());


                        /////////////////////////////////////INTENT 추가!

                    
//                    Intent intent = new intent(getApplicationContext(), MapsActivity.class);
//
//
//
//                    startActivity(intent);

                }
            }
        });



        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                double radiusDegrees = 0.05;
                LatLng center = new LatLng(location.getLatitude(), location.getLongitude());
                Log.d("UserLocation", center.latitude + ", "  + center.longitude);
                LatLng northEast = new LatLng(center.latitude + radiusDegrees, center.longitude + radiusDegrees);
                LatLng southWest = new LatLng(center.latitude - radiusDegrees, center.longitude - radiusDegrees);
                LatLngBounds bounds = LatLngBounds.builder()
                        .include(northEast)
                        .include(southWest)
                        .build();

                autocompleteFragment.setBoundsBias(bounds);
                toastAcceptedReadLocation();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("LoactionProvider", "Status : " + status);
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d("LoactionProvider", "Status : " + provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
                toastProviderDisabled();

                autocompleteFragment.setBoundsBias(new LatLngBounds( // 서울 ~ 부산으로 예상 검색내용 좁혀주기
                        new LatLng(35.114828, 129.041519), //부산, 일단 서울 중심으로 검색결과를 구현할 것이므로..
                        new LatLng(37.554690, 126.970702))); //서울
                Log.d("RandomLocation", "TRUE");
            }

        };
    }

    // GPS 이용 가능할 때
    public void toastAcceptedReadLocation(){
        Context context = getApplicationContext();
        CharSequence text = "Successfully got your location.";
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    // GPS 꺼져있을 때
    public void toastProviderDisabled(){
        Context context = getApplicationContext();
        CharSequence text = "Your GPS is Disabled.";
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    //    // GPS에 문제 있을 때
//    public void toastCannotReadLocation(){
//        Context context = getApplicationContext();
//        CharSequence text = "Problem occured";
//        int duration = Toast.LENGTH_LONG;
//        Toast toast = Toast.makeText(context, text, duration);
//        toast.show();
//    }
//
//    // GPS 퍼미션 거부됐을 때
//    public void toastRejectedReadLocation(){
//        Context context = getApplicationContext();
//        CharSequence text = "Access Denied";
//        int duration = Toast.LENGTH_LONG;
//        Toast toast = Toast.makeText(context, text, duration);
//        toast.show();
//    }
//
}
