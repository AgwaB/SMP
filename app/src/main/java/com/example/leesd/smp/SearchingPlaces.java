package com.example.leesd.smp;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.widget.ListView;

import com.example.leesd.smp.RetrofitCall.AsyncResponseMaps;
import com.example.leesd.smp.RetrofitCall.GoogleMapsNetworkCall;
import com.example.leesd.smp.RetrofitCall.GooglePlaceService;
import com.example.leesd.smp.googlemaps.JsonMaps;
import com.example.leesd.smp.googlemaps.Result;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.HttpUrl;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Leo on 2018-05-10.
 */

public abstract class SearchingPlaces implements AsyncResponseMaps{
    public static final int CAFE = 10;
    public static final int LIBRARY = 11;
    public static final int PARK = 20;
    public static final int ART_GALLERY = 21;
    public static final int BOWLING_ALLEY = 22;
    public static final int BAR = 30;
    public static final int DEPARTMENT_STORE = 31;
    public static final int MOVIE_THEATER = 32;
    public static final int STATION = 40;

    public static final Integer STATION_RADIUS = 1000;
    public static final Integer NEARBYPLACES_RADIUS = 500;

    public static final String apiKey = Resources.getSystem().getString(R.string.api);

    private ArrayList<Result> stations;     // stations list
    private ArrayList<ArrayList<JsonMaps>> jsonMapsResults;

    private void getNearByPlacesWithType(Integer stationId, String location, int placeType) {
        HashMap<String, String> searchParams = new HashMap<>(); // Hash arguments for search
        String queryType = getPlaceTypeName(placeType);

        // if search the places nearby station, need station id on params
        if(stationId != null){
            searchParams.put("station_id", stationId + ""); 		// station index in list
            searchParams.put("radius", NEARBYPLACES_RADIUS.toString());
        }
        else {
            searchParams.put("radius", STATION_RADIUS.toString());  // limitation radius for search
        }

        searchParams.put("location", location);         	// location string pWarameter (lat, lng)
        searchParams.put("type", queryType);           		// type of places
        searchParams.put("language", "ko");             	// language
        searchParams.put("key", apiKey);   // api key

        googlePlaceSearch(searchParams);    // search network call
    }

    private void getNextResultPage(Integer stationId, String pageToken){

        HashMap<String, String> searchParams = new HashMap<>();
        searchParams.put("station_id", stationId + "");
        searchParams.put("pagetoken", pageToken);
        searchParams.put("key", apiKey);

        googlePlaceSearch(searchParams);

    }

    // convert place state (integer) to place type name (string)
    private String getPlaceTypeName(int placeType) {
        switch (placeType) {
            case CAFE:
                return "cafe";
            case LIBRARY:
                return "library";
            case PARK:
                return "park";
            case BOWLING_ALLEY:
                return "bowling_alley";
            case ART_GALLERY:
                return "art_gallery";
            case BAR:
                return "bar";
            case DEPARTMENT_STORE:
                return "department_store";
            case MOVIE_THEATER:
                return "movie_theater";
            case STATION:
                return "subway_station";
            default:
                throw new IllegalArgumentException("Place type number is illegal. ");
        }
    }

    private void googlePlaceSearch(HashMap<String, String> params) {
        GooglePlaceService googlePlaceService = GooglePlaceService.retrofit.create(GooglePlaceService.class);
        final Call<JsonMaps> call = googlePlaceService.getPlaces("nearbysearch", params);
        GoogleMapsNetworkCall n = new GoogleMapsNetworkCall();
        n.delegate = this;

        // execute background service
        n.execute(call);
    }

    public void processFinish(Response<JsonMaps> response) {
        if (response == null) {
            Log.e("RETROFIT_ERROR", "BAD_RESPONSE");
            return;
        }

        ArrayList<Result> results = (ArrayList<Result>) response.body().getResults();	// API results for places
        HttpUrl url = response.raw().request().url();	// url for request

        // add the result to jsonMapsResults list
        if (stations != null) {
            jsonMapsResults = new ArrayList<>();
            for (int i = 0; i < stations.size(); i++) {
                jsonMapsResults.add(new ArrayList<JsonMaps>());
            }
        }

        // if search place that near by station
        if (url.queryParameter("station_id") != null) {
            // station_id is exist on query
            int stationId = Integer.valueOf(url.queryParameter("station_id"));	// stations index
            Result station = stations.get(stationId);
            int nextValue = station.getWeight() + results.size();	// each station's results length

            jsonMapsResults.get(stationId).add(response.body());

            // set # of places on result element (weight for the order)
            station.setWeight(nextValue);

            int last = jsonMapsResults.get(stationId).size() - 1;
            if (jsonMapsResults.get(stationId).get(last).getNextPageToken() != null) {
                getNextResultPage(stationId, jsonMapsResults.get(stationId).get(last).getNextPageToken());
            }
        } else {
            // STATION
            // if already results are exist, clear the previous results
            if (!stations.isEmpty()) {
                stations.clear();
            }

            // add the stations to arraylist
            for (Result r : results) {
                r.setWeight(0);
                stations.add(r);
            }

//            stationsListViewAdapter.notifyDataSetChanged();	// refresh the stations listview
        }

        sortStations(stations);
//        stationsListViewAdapter.notifyDataSetChanged();

        Log.d("RETROFIT_RESULTS", results.toString());
        Log.d("RETROFIT_METADATA", response.toString());
        Log.d("RETROFIT_RESULT_LENGTH", results.size() + "");
    }


    private void sortStations(ArrayList<Result> stations) {
        for (int i = 0; i < stations.size(); i++) {
            int max = i;
            for (int j = i; j < stations.size(); j++) {
                if (stations.get(max).getWeight() <= stations.get(j).getWeight()) {
                    max = j;
                }
            }

            Result r = stations.get(i);
            stations.set(i, stations.get(max));
            stations.set(max, r);
        }
    }

    private void initJsonMapsResults() {
        for (ArrayList<JsonMaps> results : jsonMapsResults) {
            results.clear();
        }
        jsonMapsResults.clear();
    }


}