package com.example.leesd.smp;

import android.app.Fragment;
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

import retrofit2.Call;
import retrofit2.Response;

import static com.example.leesd.smp.MapsActivity.switchFragment;


/**
 * Created by leesd on 2018-03-15.
 * Modified by Leo Park on 2018-05-06.
 */

public class RecoFragment extends Fragment implements AsyncResponseMaps {

	public interface OnMyListener {
		void onReceivedData(Object data);
	}

	private OnMyListener mOnMyListener;

	private class StationsListViewAdapter extends BaseAdapter {

		private ArrayList<Result> stations = new ArrayList<>();

		@Override
		public int getCount() {
			return stations.size();
		}

		@Override
		public Object getItem(int position) {
			return stations.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final Context context = parent.getContext();

			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.listview_station, parent, false);
			}

			TextView stationName = convertView.findViewById(R.id.station_name);
			TextView stationWeight = convertView.findViewById(R.id.station_weight);

			Result station = stations.get(position);
			stationName.setText(station.getName());
			stationWeight.setText(String.valueOf(station.getWeight()));

			return convertView;
		}
	}

	@Nullable
	private ArrayList<LatLng> positionList; // list of user positions
	private ListView stationsView;          // listView for stations
	private StationsListViewAdapter stationsListViewAdapter;    // listview adapter
	private ArrayList<Result> stations;     // stations list

	LatLng medianLatlng;

	private ArrayList<ArrayList<JsonMaps>> jsonMapsResults;


	//[0 : none,
	// QUIET 10: cafe, 11: library
	// ACTIVITY 20: park, 21: art_gallery, 22: bowling_alley
	// PLAY 30: bar, 31: department_store, 32: movie_theater
	// STATION 40: subway_station ]
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

	private int CURRENT_SEARCH_STATE;       // search state


	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (getActivity() != null && getActivity() instanceof OnMyListener) {
			mOnMyListener = (OnMyListener) getActivity();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_recommend, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) { // onCreateView 에서 return 한 view를 가지고 있다.
		// Setup any handles to view objects here
		CURRENT_SEARCH_STATE = 0;

		// reference to button ui
		Button buttonDistance = getView().findViewById(R.id.button_distance);
		Button buttonHotplace = getView().findViewById(R.id.button_hotplace);

		// setting up the ListView
		stationsListViewAdapter = new StationsListViewAdapter();	// create listview adapter
		stationsView = getView().findViewById(R.id.stations_list);	// get listview
		stationsView.setAdapter(stationsListViewAdapter);			// set listview adapter
		stations = stationsListViewAdapter.stations;				// set station list for listview

		getView().findViewById(R.id.reco_layout_listview).setVisibility(View.INVISIBLE);

		// get position list bundle in fragment
		if (getArguments() != null) {
			positionList = getArguments().getParcelableArrayList("positions");
		}


		// calculate median position
		medianLatlng = getMidPoint(positionList);
		Context context = getActivity().getApplicationContext();
		CharSequence text = new String(medianLatlng.latitude + ", " + medianLatlng.longitude);
		int duration = Toast.LENGTH_LONG;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();

		// search the subway station nearby median position
		CURRENT_SEARCH_STATE = STATION;
		getNearByPlacesWithType(null, medianLatlng.latitude +","+ medianLatlng.longitude, STATION);

		// when item clicked, pass the subway to detail fragment
		stationsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// When clicked, show a toast with the selectedItem's name
				Result selectedItem = stations.get(position);
				Toast.makeText(getActivity().getApplicationContext(), selectedItem.getName(),
						Toast.LENGTH_SHORT).show();

				// pass the selected station to detail fragment
				Bundle bundle = new Bundle();
				bundle.putSerializable("station", selectedItem);

				Fragment fr = new DetailFragment();
				fr.setArguments(bundle); // transmit to DetailFragment

				//change fragment, switchFragment is 'static method' (in MapsActivity)
				switchFragment(fr, "ToDetail");
			}
		});


		// search places that nearby median position of lists
		buttonDistance.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// hide the buttons after selected
				getView().findViewById(R.id.reco_layout_buttons).setVisibility(View.GONE);
				getView().findViewById(R.id.reco_layout_listview).setVisibility(View.VISIBLE);

				if (positionList == null) {
					return;
				}
				// send the median position to MapsActivity
				{
					if (mOnMyListener != null) {
						mOnMyListener.onReceivedData(medianLatlng);
					}
				}

			}
		});

		buttonHotplace.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// hide the buttons after selected
				getView().findViewById(R.id.reco_layout_buttons).setVisibility(View.GONE);
				getView().findViewById(R.id.reco_layout_listview).setVisibility(View.GONE);

				LinearLayout categories = getActivity().findViewById(R.id.fragment_recommend);
				View child = LayoutInflater.from(getActivity()).inflate(R.layout.buttons_hotplace, null);
				categories.addView(child);

				Button buttonQuiet = getView().findViewById(R.id.button_quiet);
				Button buttonPlay = getView().findViewById(R.id.button_play);
				Button buttonActivity = getView().findViewById(R.id.button_activity);


				buttonQuiet.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// search on cafe, library
						getView().findViewById(R.id.reco_layout_buttons).setVisibility(View.GONE);
						getView().findViewById(R.id.reco_layout_listview).setVisibility(View.VISIBLE);

						initJsonMapsResults();  // clear the previous results

						for (int index = 0; index < stations.size(); index++) {
							Result station = stations.get(index);
							double lat = station.getGeometry().getLocation().getLat();
							double lng = station.getGeometry().getLocation().getLng();
							int stationId = index;

							getNearByPlacesWithType(stationId, lat + ","+ lng, CAFE);

							CURRENT_SEARCH_STATE = CAFE;
						}

					}
				});

				buttonActivity.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// search on park, bowling alley, art gallery
						getView().findViewById(R.id.reco_layout_buttons).setVisibility(View.GONE);
						getView().findViewById(R.id.reco_layout_listview).setVisibility(View.VISIBLE);

						for (int index = 0; index < stations.size(); index++) {
							Result station = stations.get(index);
							double lat = station.getGeometry().getLocation().getLat();
							double lng = station.getGeometry().getLocation().getLng();
							int stationId = index;

							getNearByPlacesWithType(stationId, lat + ","+ lng, PARK);

							CURRENT_SEARCH_STATE = PARK;
						}

					}
				});

				buttonPlay.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// search on bar, movie theater, department store
						getView().findViewById(R.id.reco_layout_buttons).setVisibility(View.GONE);
						getView().findViewById(R.id.reco_layout_listview).setVisibility(View.VISIBLE);

						for (int index = 0; index < stations.size(); index++) {
							Result station = stations.get(index);
							double lat = station.getGeometry().getLocation().getLat();
							double lng = station.getGeometry().getLocation().getLng();
							int stationId = index;

							getNearByPlacesWithType(stationId, lat + ","+ lng, BAR);

							CURRENT_SEARCH_STATE = BAR;
						}

					}
				});

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

		String queryLocation = response.raw().request().url().queryParameter("location");

		// add the result to jsonMapsResults list
		if (stations != null) {
			jsonMapsResults = new ArrayList<>();
			for (int i = 0; i < stations.size(); i++) {
				jsonMapsResults.add(new ArrayList<JsonMaps>());
			}
		}



		// if search place that near by station
		if (response.raw().request().url().queryParameter("station_id") != null) {
			// station_id is exist on query
			int stationId = Integer.valueOf(response.raw().request().url().queryParameter("station_id"));	// stations index
			int nextValue = stations.get(stationId).getWeight() + response.body().getResults().size();	// each station's results length

			jsonMapsResults.get(stationId).add(response.body());

			int last = jsonMapsResults.get(stationId).size() - 1;
			if (jsonMapsResults.get(stationId).get(last).getNextPageToken() != null) {
				// set # of places on result element
				stations.get(stationId).setWeight(nextValue);
				getNextResultPage(stationId, jsonMapsResults.get(stationId).get(last).getNextPageToken());
			} else {
				for (JsonMaps j : jsonMapsResults.get(stationId)) {
					if (j.getNextPageToken() != null) {
//						nextValue = stations.get(stationId).getWeight() + response.body().getResults().size();
						stations.get(stationId).setWeight(nextValue);
					}
				}
				switch (CURRENT_SEARCH_STATE) {
					case CAFE:
//						nextValue = stations.get(stationId).getWeight() + response.body().getResults().size();
						stations.get(stationId).setWeight(nextValue);
						getNearByPlacesWithType(stationId, queryLocation, LIBRARY);
						CURRENT_SEARCH_STATE = LIBRARY;
						break;
					case LIBRARY:
//						nextValue = stations.get(stationId).getWeight() + response.body().getResults().size();
						stations.get(stationId).setWeight(nextValue);
						CURRENT_SEARCH_STATE = NONE;
						break;

					case PARK:
//						nextValue = stations.get(stationId).getWeight() + response.body().getResults().size();
						stations.get(stationId).setWeight(nextValue);
						getNearByPlacesWithType(stationId, queryLocation, BOWLING_ALLEY);
						CURRENT_SEARCH_STATE = BOWLING_ALLEY;
						break;
					case BOWLING_ALLEY:
//						nextValue = stations.get(stationId).getWeight() + response.body().getResults().size();
						stations.get(stationId).setWeight(nextValue);
						getNearByPlacesWithType(stationId, queryLocation, ART_GALLERY);
						CURRENT_SEARCH_STATE = ART_GALLERY;
						break;

					case ART_GALLERY:
//						nextValue = stations.get(stationId).getWeight() + response.body().getResults().size();
						stations.get(stationId).setWeight(nextValue);
						CURRENT_SEARCH_STATE = NONE;
						break;

					case BAR:
//						nextValue = stations.get(stationId).getWeight() + response.body().getResults().size();
						stations.get(stationId).setWeight(nextValue);
						getNearByPlacesWithType(stationId, queryLocation, DEPARTMENT_STORE);
						CURRENT_SEARCH_STATE = DEPARTMENT_STORE;
						break;

					case DEPARTMENT_STORE:
//						nextValue = stations.get(stationId).getWeight() + response.body().getResults().size();
						stations.get(stationId).setWeight(nextValue);
						getNearByPlacesWithType(stationId, queryLocation, MOVIE_THEATER);
						CURRENT_SEARCH_STATE = MOVIE_THEATER;
						break;
					case MOVIE_THEATER:
//						nextValue = stations.get(stationId).getWeight() + response.body().getResults().size();
						stations.get(stationId).setWeight(nextValue);
						CURRENT_SEARCH_STATE = NONE;
						break;

					case STATION:
						// nothing to do
						break;

					default:
						sortStations(stations);
						stationsListViewAdapter.notifyDataSetChanged();
				}
			}
		} else {
			// STATION
			// if repeat to search, clear the previous results
			if (!stations.isEmpty()) {
				stations.clear();
			}

			// add the stations to arraylist
			for (Result r : response.body().getResults()) {
				r.setWeight(0);
				stations.add(r);
			}
			stationsListViewAdapter.notifyDataSetChanged();
		}


		Log.d("RETROFIT_RESULTS", response.body().getResults().toString());
		Log.d("RETROFIT_METADATA", response.toString());
	}

	private void getNearByPlacesWithType(Integer stationId, String location, int placeType) {
		HashMap<String, String> searchParams = new HashMap<>(); // Hash arguments for search
		String queryType = getPlaceTypeName(placeType);

		if(stationId != null){
			searchParams.put("station_id", stationId + ""); // station index in list
			searchParams.put("radius", NEARBYPLACES_RADIUS.toString());
		}
		else {
			searchParams.put("radius", STATION_RADIUS.toString());  // limitation radius for search
		}

		searchParams.put("location", location);         // location string parameter (lat, lng)
		searchParams.put("type", queryType);            // type of places
		searchParams.put("language", "ko");             // language
		searchParams.put("key", getString(R.string.api));   // api key

		googlePlaceSearch(searchParams);    // search network call
	}

	private void getNextResultPage(Integer stationId, String pageToken){

		HashMap<String, String> searchParams = new HashMap<>();
		searchParams.put("station_id", stationId + "");
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
		n.delegate = RecoFragment.this;

		// execute background service
		n.execute(call);
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
