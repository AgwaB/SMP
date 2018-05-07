package com.example.leesd.smp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
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

/**
 * Created by leesd on 2018-03-15.
 * Modified by Leo Park on 2018-05-06.
 */

public class RecoFragment extends Fragment implements AsyncResponseMaps {
	
	
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
//			ImageButton btnCancel = (ImageButton) convertView.findViewById(R.id.cancelButton);
//			btnCancel.setOnClickListener(new ImageButton.OnClickListener(){
//				public void onClick(View v){
//					listViewItemList.remove(position);
//					notifyDataSetChanged();
//				}
//			});
			
			Result station = stations.get(position);
			stationName.setText(station.getName());
			
			return convertView;
		}
	}
	
	@Nullable
	private ArrayList<LatLng> positionList; // list of user positions
	private ListView stationsView;
	private StationsListViewAdapter stationsListViewAdapter;
	private ArrayList<Result> stations;
	
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_recommend, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// Setup any handles to view objects here
		
		// reference to button ui
		Button buttonDistance = getView().findViewById(R.id.button_distance);
		Button buttonHotplace = getView().findViewById(R.id.button_hotplace);
		
		// setting up the ListView
		stationsListViewAdapter = new StationsListViewAdapter();
		stationsView = getView().findViewById(R.id.stations_list);
		stationsView.setAdapter(stationsListViewAdapter);
		
		stations = stationsListViewAdapter.stations;
		
		// when item clicked, pass the subway to detail fragment
		stationsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// When clicked, show a toast with the selectedItem's name
				Result selectedItem = (Result) stations.get(position);
				Toast.makeText(getActivity().getApplicationContext(), selectedItem.getName(),
						Toast.LENGTH_SHORT).show();
				
				
				// pass the selected station to detail fragment
				Bundle bundle = new Bundle();
				bundle.putSerializable("station", selectedItem);
				
				Fragment fr = new DetailFragment();
				fr.setArguments(bundle);
			}
		});
		
		// get position list bundle in fragment
		if (getArguments() != null) {
			positionList = getArguments().getParcelableArrayList("positions");
		}
		
		// search places that nearby median position of lists
		buttonDistance.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// hide the buttons after selected
//				getView().findViewById(R.id.reco_layout_buttons).setVisibility(View.GONE);
				
				if (positionList == null) {
					return;
				}
				
				// calculate median position
				LatLng medianPosition = getMidPoint(positionList);
				Context context = getActivity().getApplicationContext();
				CharSequence text = new String(medianPosition.latitude + ", " + medianPosition.longitude);
				int duration = Toast.LENGTH_LONG;
				
				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
				
				
				// search the subway station nearby median position
				// add params to HashMap
				HashMap<String, String> searchParams = new HashMap<String, String>();
				searchParams.put("location", medianPosition.latitude + "," + medianPosition.longitude);
				searchParams.put("radius", "1000");
				searchParams.put("type", "subway_station");
				searchParams.put("language", "ko");
				searchParams.put("key", getString(R.string.api));
				
				GooglePlaceService googlePlaceService = GooglePlaceService.retrofit.create(GooglePlaceService.class);
				final Call<JsonMaps> call = googlePlaceService.getPlaces("nearbysearch", searchParams);
				GoogleMapsNetworkCall n = new GoogleMapsNetworkCall();
				n.delegate = RecoFragment.this;
				
				// execute background service
				n.execute(call);
			}
		});
		
		buttonHotplace.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// hide the buttons after selected
				getView().findViewById(R.id.reco_layout_buttons).setVisibility(View.GONE);
				getView().findViewById(R.id.reco_layout_listview).setVisibility(View.GONE);
				
				LinearLayout categories = (LinearLayout) getActivity().findViewById(R.id.fragment_recommend);
				View child = LayoutInflater.from(getActivity()).inflate(R.layout.buttons_hotplace, null);
				categories.addView(child);
				
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
		Context context = getActivity().getApplicationContext();
		CharSequence text = response.body().getStatus();
		int duration = Toast.LENGTH_LONG;
		
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
		
		// if repeat to search, clear the previous results
		if (!stations.isEmpty()) {
			stations.clear();
		}
		
		// add the response data to arraylist
		for (Result r : response.body().getResults()) {
			stations.add(r);
		}
		stationsListViewAdapter.notifyDataSetChanged();
		
		Log.d("RETROFIT_MESSAGE", response.body().getResults().toString());
		Log.d("RETROFIT_MESSAGE", response.toString());
	}
}
