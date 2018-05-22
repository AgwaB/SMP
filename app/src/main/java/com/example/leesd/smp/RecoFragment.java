package com.example.leesd.smp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import static com.example.leesd.smp.MapsActivity.switchFragment;


/**
 * Created by leesd on 2018-03-15.
 * Modified by Leo Park on 2018-05-06.
 */

public class RecoFragment extends Fragment implements AsyncResponseMaps {

	//				 0: none
	//	 QUIET 		10: cafe	11: library
	//	 ACTIVITY 	20: park	21: art_gallery			22: bowling_alley
	//	 PLAY 		30: bar		31: department_store	32: movie_theater
	//	 STATION 	40: subway_station

	public static final int NONE = 0;
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


	@Nullable
	private ArrayList<LatLng> positions; // list of user positions
	private ArrayList<Result> stations;     // stations list


	LatLng medianLatLng;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_recommend, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) { // onCreateView 에서 return 한 view를 가지고 있다.
		// Setup any handles to view objects here

		// reference to button ui
		Button buttonDistance = getView().findViewById(R.id.button_distance);
		Button buttonHotplace = getView().findViewById(R.id.button_hotplace);

		// get position list bundle in fragment
		if (getArguments() != null) {
			positions = getArguments().getParcelableArrayList("positions");
		}

		// calculate median position
		medianLatLng = getMidPoint(positions);

		// search the subway station nearby median position
		getNearByPlacesWithType(null, medianLatLng.latitude +","+ medianLatLng.longitude, STATION);

		// search stations that nearby median position of lists
		buttonDistance.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();

				// pass the position to StationsFragment list data
				bundle.putString("SENDER", RecoFragment.class.getSimpleName());
				bundle.putParcelableArrayList("ARRAYLIST_LATLNG_POSITIONS", positions);
				bundle.putSerializable("ARRAYLIST_RESULT_STATIONS", stations);
				bundle.putParcelable("LATLNG_MEDIAN_POSITION", medianLatLng);

				// fragment load
				Fragment fr = new StationsFragment();
				fr.setArguments(bundle);
				FragmentManager fm = getFragmentManager();
				FragmentTransaction fragmentTransaction = fm.beginTransaction();
				fragmentTransaction.remove(fm.findFragmentById(R.id.fragment_view))
						.add(R.id.fragment_view, fr)
						.addToBackStack(null)
						.commit();

			}
		});

		// search places that nearby median position of lists
		buttonHotplace.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Bundle bundle = new Bundle();

				// pass the position to StationsFragment list data
				bundle.putString("SENDER", RecoFragment.class.getSimpleName());
				bundle.putParcelableArrayList("ARRAYLIST_LATLNG_POSITIONS", positions);
				bundle.putSerializable("ARRAYLIST_RESULT_STATIONS", stations);
				bundle.putParcelable("LATLNG_MEDIAN_POSITION", medianLatLng);

				// fragment load
				Fragment fr = new HotPlaceButtonFragment();
				fr.setArguments(bundle);
				FragmentManager fm = getFragmentManager();
				FragmentTransaction fragmentTransaction = fm.beginTransaction();
				fragmentTransaction.remove(fm.findFragmentById(R.id.fragment_view))
						.add(R.id.fragment_view, fr)
						.addToBackStack(null)
						.commit();

			}
		});

	}

	// calculate median position of list
	private LatLng getMidPoint(Iterable<LatLng> list) {
		double sumLat = 0.0D;
		double sumLng = 0.0D;

		int length = 0;
		for (LatLng position : list) {
			sumLat += position.latitude;
			sumLng += position.longitude;

			length++;
		}

		return new LatLng((double) sumLat / length, (double) sumLng / length);

	}

	public void processFinish(Response<JsonMaps> response) {
		if (response == null) {
			Log.e("RETROFIT_ERROR", "BAD_RESPONSE");
			return;
		}
		//		ArrayList<Result> results = (ArrayList<Result>) response.body().getResults();	// API results for places
		HttpUrl url = response.raw().request().url();	// url for request

		// if it has previous page,
		if(url.queryParameter("pagetoken") != null){
			// add current page
			stations.addAll(response.body().getResults());
		} else {
			// replace the result
			stations = (ArrayList<Result>) response.body().getResults();
		}

		for (Result station : stations){
			station.setWeight(0);
		}

		// check if it has next page
		if (response.body().getNextPageToken() != null) {
			getNextResultPage(null, response.body().getNextPageToken());
		}
	}

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

	private void googlePlaceSearch(HashMap<String, String> params) {
		GooglePlaceService googlePlaceService = GooglePlaceService.retrofit.create(GooglePlaceService.class);
		final Call<JsonMaps> call = googlePlaceService.getPlaces("nearbysearch", params);
		GoogleMapsNetworkCall n = new GoogleMapsNetworkCall();
		n.delegate = RecoFragment.this;

		// execute background service
		n.execute(call);
	}

}
