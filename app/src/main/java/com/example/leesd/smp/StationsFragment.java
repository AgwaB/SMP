package com.example.leesd.smp;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.leesd.smp.RetrofitCall.AsyncResponseMaps;
import com.example.leesd.smp.RetrofitCall.GoogleMapsNetworkCall;
import com.example.leesd.smp.RetrofitCall.GooglePlaceService;
import com.example.leesd.smp.dummy.DummyContent;
import com.example.leesd.smp.dummy.DummyContent.DummyItem;
import com.example.leesd.smp.googlemaps.JsonMaps;
import com.example.leesd.smp.googlemaps.Result;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.HttpUrl;
import retrofit2.Call;
import retrofit2.Response;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnStationsFragmentListener}
 * interface.
 */
public class StationsFragment extends Fragment implements AsyncResponseMaps {

    // TODO: Customize parameters
    private int mColumnCount = 1;

    private OnStationsFragmentListener mListener;

    public interface OnStationsFragmentListener {
        // TODO: Update argument type and name
        void onReceivedResults(ArrayList<Result> results);
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StationsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

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

    private String senderTag;
    private ListView stationsView;          // listView for stations
    private StationsListViewAdapter stationsListViewAdapter;    // listview adapter
    private ArrayList<Result> stations;     // stations list
    private ArrayList<LatLng> positions;
    private LatLng medianPosition;
    private String placeType;

    private ArrayList<ArrayList<JsonMaps>> jsonMapsResults;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_station_list, container, false);

        // setting up the ListView
        stationsListViewAdapter = new StationsListViewAdapter();	// create listview adapter
        stations	 = stationsListViewAdapter.stations;			// set station list for listview
        stationsView = view.findViewById(R.id.stations_list);	// get listview
        stationsView.setAdapter(stationsListViewAdapter);			// set listview adapter

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        // ARRAYLIST_LATLNG_POSITIONS
        // ARRAYLIST_RESULT_STATIONS
        // MEDIAN_LATLNG_POSITION

        // get arguments from previous fragment
        if (getArguments() != null) {
            senderTag = getArguments().getString("SENDER");
            if(senderTag.equals(RecoFragment.class.getSimpleName())){
                positions = getArguments().getParcelableArrayList("ARRAYLIST_LATLNG_POSITIONS");
                medianPosition = getArguments().getParcelable("LATLNG_MEDIAN_POSITION");
                for(Result station : (ArrayList<Result>) getArguments().getSerializable("ARRAYLIST_RESULT_STATIONS")){
                    stations.add(station);
                }
            }
            if(senderTag.equals(HotPlaceButtonFragment.class.getSimpleName())){
                placeType = getArguments().getString("HOTPLACE_TYPE");
                for(Result station : (ArrayList<Result>) getArguments().getSerializable("ARRAYLIST_RESULT_STATIONS")){
                    stations.add(station);
                }
            }
            stationsListViewAdapter.notifyDataSetChanged();

            mListener.onReceivedResults(stations);
        }


        // search the stations
        if(senderTag.equals(RecoFragment.class.getSimpleName())){
//            String location = medianPosition.latitude+","+medianPosition.longitude;
            for(int index = 0; index < stations.size(); index++){
                Result station = stations.get(index);
                String location = station.getGeometry().getLocation().getLat() + "," +
                        station.getGeometry().getLocation().getLng();	// string with location
                int stationId = index;
                getNearByPlacesWithType(stationId, location, STATION);
            }

        }

        if(senderTag.equals(HotPlaceButtonFragment.class.getSimpleName())){
            for (int index = 0; index < stations.size(); index++) {
                Result station = stations.get(index);
                String location = station.getGeometry().getLocation().getLat() + "," +
                        station.getGeometry().getLocation().getLng();	// string with location
                int stationId = index;

                if(placeType.equals("QUIET")){
                    getNearByPlacesWithType(stationId, location, CAFE);
                    getNearByPlacesWithType(stationId, location, LIBRARY);

                } else if(placeType.equals("ACTIVITY")){
                    getNearByPlacesWithType(stationId, location, PARK);
                    getNearByPlacesWithType(stationId, location, BOWLING_ALLEY);
                    getNearByPlacesWithType(stationId, location, ART_GALLERY);

                } else if(placeType.equals("PLAY")){
                    getNearByPlacesWithType(stationId, location, BAR);
                    getNearByPlacesWithType(stationId, location, DEPARTMENT_STORE);
                    getNearByPlacesWithType(stationId, location, MOVIE_THEATER);

                }

            }
        }

        stationsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // When clicked, show a toast with the selectedItem's name
                Result selectedItem = stations.get(position);
                Toast.makeText(getActivity().getApplicationContext(), selectedItem.getName(),
                        Toast.LENGTH_SHORT).show();

                // pass the selected station to detail fragment
                Bundle bundle = new Bundle();
                bundle.putSerializable("RESULT_STATION", selectedItem);		// stations list
                bundle.putSerializable("ARRAYLIST_RESULT_PLACE", jsonMapsResults.get((int)id));	// search result of item (station)

                Fragment fr = new DetailFragment();
                fr.setArguments(bundle); // transmit to DetailFragment

                //change fragment, switchFragment is 'static method' (in MapsActivity)
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction()//;
                //tx.replace(R.id.fragment_view, fr)
                        .detach(StationsFragment.this)
                        .addToBackStack(null)
                        .commit();
            }
        });


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnStationsFragmentListener) {
            mListener = (OnStationsFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnStationsFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


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
        searchParams.put("key", getString(R.string.api));   // api key

        googlePlaceSearch(searchParams);    // search network call
    }

    private void getNextResultPage(Integer stationId, String pageToken){

        HashMap<String, String> searchParams = new HashMap<>();
        if(stationId != null){
            searchParams.put("station_id", stationId + ""); 		// station index in list
        }
        searchParams.put("pagetoken", pageToken);
        searchParams.put("key", getString(R.string.api));

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

        if(jsonMapsResults == null){
            jsonMapsResults = new ArrayList<>();
            for(int i=0;i<stations.size(); i++){
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
            throw new IllegalArgumentException("stationId is null value. ");
        }

        sortStations(stations);
        stationsListViewAdapter.notifyDataSetChanged(); // refresh the stations listview


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
