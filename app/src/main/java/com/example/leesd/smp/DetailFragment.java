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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.example.leesd.smp.DetailSearch.JsonDetail;
import com.example.leesd.smp.RetrofitCall.AsyncResponse;
import com.example.leesd.smp.RetrofitCall.AsyncResponseDetail;
import com.example.leesd.smp.RetrofitCall.AsyncResponseMaps;
import com.example.leesd.smp.RetrofitCall.GoogleMapsNetworkCall;
import com.example.leesd.smp.RetrofitCall.GooglePlaceService;
import com.example.leesd.smp.googlemaps.JsonMaps;
import com.example.leesd.smp.googlemaps.Result;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;

import static com.example.leesd.smp.MapsActivity.switchFragment;

/**
 * Created by leesd on 2018-03-16.
 */

public class DetailFragment extends Fragment implements AsyncResponseMaps {

    private int request_count = 0;
    private String nextpagetoken = null;
    private JsonMaps jsonMaps;
    private Result result;

    private Button fragmentBack;
    private ListView listview ;
    private ListViewAdapter adapter;
    private ArrayList<JsonMaps> jsonMapsPack = new ArrayList<JsonMaps>();
    private HashMap<String, String> searchParams;
    private OnMyListener mOnMyListener;

    public interface OnMyListener{ // fragment -> activity 통신을 위한 callback용 interface
        void onReceivedData(ArrayList<JsonMaps> data); // retrofit 통신 끝난 후, activity의 google map에 market를 찍어주기 위한 callback용 interface
        void onReceiveData(String name); // listview의 item 클릭 시, map에서 marker를 focusing하기 위한 callback용 interface
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(getActivity() != null && getActivity() instanceof OnMyListener){
            mOnMyListener = (OnMyListener)getActivity();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);


        adapter = new ListViewAdapter();
        fragmentBack = (Button)getActivity().findViewById(R.id.fragmentBack);

        listview = (ListView)view.findViewById(R.id.listview_showInformation);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListViewItem listViewItem = (ListViewItem)parent.getItemAtPosition(position);
                mOnMyListener.onReceiveData(listViewItem.getTitle());
            }
        });

        fragmentBack.setVisibility(View.VISIBLE);

        fragmentBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentBack.setVisibility(View.GONE);
                switchFragment(null, "ToReco");
            }
        });

        nextpagetoken = null;
        jsonMaps = null;
        result = null;

        result = (Result)getArguments().getSerializable("station"); // get data from RecoFragment

        getData(result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng(), null);

        return view;
    }


    public void getData(Double latitue, Double longitude, String nextToken) { // RecoFragment에서 위도값 받아온 뒤 hashmap에 key-value로 넣어준다.
        // add params to HashMap
        searchParams = new HashMap<String, String>();

        searchParams.put("location",latitue + "," + longitude);
        searchParams.put("radius", "500");
        searchParams.put("type", "cafe");
        searchParams.put("language", "ko");
        searchParams.put("request_count", Integer.toString(request_count));
        searchParams.put("key", getString(R.string.api));

        if(nextToken!=null)
            searchParams.put("pagetoken", nextToken);

        // build retrofit object
        GooglePlaceService googlePlaceService = GooglePlaceService.retrofit.create(GooglePlaceService.class);

        // call GET request with category and HashMap params
        Call<JsonMaps> call = googlePlaceService.getPlaces("nearbysearch", searchParams);

        // make a thread for http communication
        GoogleMapsNetworkCall n = new GoogleMapsNetworkCall();

        // set delegate for receiving response object
        n.delegate = DetailFragment.this;

        // execute background service
        n.execute(call);
    }

    @Override
    public void processFinish(Response<JsonMaps> response) { // getData()의 retrofit 요청이 완료되면 이 함수 실행
        if(response!=null) {
            jsonMaps = response.body();
            jsonMapsPack.add(jsonMaps);
            if(jsonMaps.getNextPageToken()!=null){
                getData(result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng(), jsonMaps.getNextPageToken());
                nextpagetoken = jsonMaps.getNextPageToken();
                request_count++;
            }
            else if(nextpagetoken!=null && jsonMaps.getStatus().equals("INVALID_REQUEST")) {
                getData(result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng(), nextpagetoken);
                request_count++;
            }
            else {
                for(int x = 0 ; x < jsonMapsPack.size() ; x++)
                    for(int i = 0 ; i < jsonMapsPack.get(x).getResults().size() ; i ++){
                        adapter.addItem(jsonMapsPack.get(x).getResults().get(i).getIcon(), jsonMapsPack.get(x).getResults().get(i).getName(), jsonMapsPack.get(x).getResults().get(i).getVicinity());
                        adapter.addJsonResult(jsonMapsPack.get(x).getResults().get(i));
                    }

                if(mOnMyListener != null){
                    mOnMyListener.onReceivedData(jsonMapsPack);
                }

                adapter.notifyDataSetChanged();
            }
        }
    }


}
