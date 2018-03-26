package com.example.leesd.smp;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.leesd.smp.RetrofitCall.GoogleMapsNetworkCall;
import com.example.leesd.smp.RetrofitCall.GooglePlaceService;
import com.example.leesd.smp.googlemaps.JsonMaps;

import java.util.HashMap;

import retrofit2.Call;

/**
 * Created by leesd on 2018-03-16.
 */

public class DetailFragment extends Fragment {
    ListView listview ;
    ListViewAdapter adapter;
    private HashMap<String, String> searchParams;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);


        adapter = new ListViewAdapter();

        listview = (ListView)view.findViewById(R.id.listview_showInformation);
        listview.setAdapter(adapter);



        adapter.addItem(null, "박준영", "교촌치킨");

        return view;
    }


    public void getData(){ // RecoFragment에서 위도값 받아온 뒤 hashmap에 key-value로 넣어준다.
        // add params to HashMap
        searchParams = new HashMap<String, String>();

        searchParams.put("location", Double.toString(37.56) + "," + Double.toString(126.97));
        searchParams.put("radius", "500");
        searchParams.put("type", "cafe");
        searchParams.put("language", "ko");
        searchParams.put("key", getString(R.string.placesKey));

        // build retrofit object
        GooglePlaceService googlePlaceService = GooglePlaceService.retrofit.create(GooglePlaceService.class);

        // call GET request with category and HashMap params
        final Call<JsonMaps> call = googlePlaceService.getPlaces("nearbysearch", searchParams);

        // make a thread for http communication
        GoogleMapsNetworkCall n = new GoogleMapsNetworkCall();

        // set delegate for receiving response object
        //n.delegate = DetailFragment.this;

        // execute background service
        n.execute(call);

    }
}
