package com.example.leesd.smp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.leesd.smp.RetrofitCall.AsyncResponseMaps;
import com.example.leesd.smp.RetrofitCall.GoogleMapsNetworkCall;
import com.example.leesd.smp.RetrofitCall.GooglePlaceService;
import com.example.leesd.smp.googlemaps.JsonMaps;
import com.example.leesd.smp.googlemaps.Result;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.HttpUrl;
import retrofit2.Call;
import retrofit2.Response;


public class HotPlaceButtonFragment extends Fragment {// implements AsyncResponseMaps {



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private ArrayList<LatLng> positions; // list of user positions
    private ArrayList<Result> stations;     // stations list
    private LatLng medianPosition;

    private ArrayList<ArrayList<JsonMaps>> jsonMapsResults;

    private String senderTag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_hot_place_button, container, false);

        // Inflate the layout for this fragment
        return view;
    }




    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        // ARRAYLIST_LATLNG_POSITIONS
        // ARRAYLIST_RESULT_STATIONS
        // MEDIAN_LATLNG_POSITION

        if (getArguments() != null) {
            senderTag = getArguments().getString("SENDER");
            positions = getArguments().getParcelableArrayList("ARRAYLIST_LATLNG_POSITIONS");
            stations = (ArrayList<Result>) getArguments().getSerializable("ARRAYLIST_RESULT_STATIONS");
            medianPosition = getArguments().getParcelable("LATLNG_MEDIAN_POSITION");
        }

        Button buttonQuiet = getView().findViewById(R.id.button_quiet);
        Button buttonPlay = getView().findViewById(R.id.button_play);
        Button buttonActivity = getView().findViewById(R.id.button_activity);

        buttonQuiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // search on cafe, library
                Bundle bundle = new Bundle();
                bundle.putString("SENDER", HotPlaceButtonFragment.class.getSimpleName());
                bundle.putString("HOTPLACE_TYPE", "QUIET");
                bundle.putSerializable("ARRAYLIST_RESULT_STATIONS", stations);

                // fragment load
                Fragment fr = new StationsFragment();
                fr.setArguments(bundle);
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.add(R.id.fragment_view, fr).addToBackStack(null).commit();

            }
        });

        buttonActivity.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // search on park, bowling alley, art gallery

                Bundle bundle = new Bundle();
                bundle.putString("SENDER", HotPlaceButtonFragment.class.getSimpleName());
                bundle.putString("HOTPLACE_TYPE", "ACTIVITY");
                bundle.putSerializable("ARRAYLIST_RESULT_STATIONS", stations);

                // fragment load
                Fragment fr = new StationsFragment();
                fr.setArguments(bundle);
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.add(R.id.fragment_view, fr).addToBackStack(null).commit();
            }
        });

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // search on bar, movie theater, department store
                Bundle bundle = new Bundle();
                bundle.putString("SENDER", HotPlaceButtonFragment.class.getSimpleName());
                bundle.putString("HOTPLACE_TYPE", "PLAY");
                bundle.putSerializable("ARRAYLIST_RESULT_STATIONS", stations);

                // fragment load
                Fragment fr = new StationsFragment();
                fr.setArguments(bundle);
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.add(R.id.fragment_view, fr).addToBackStack(null).commit();

            }
        });
    }
}
