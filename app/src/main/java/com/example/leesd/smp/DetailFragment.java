package com.example.leesd.smp;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
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
import com.example.leesd.smp.RetrofitCall.DetailInfoNetworkCall;
import com.example.leesd.smp.RetrofitCall.DetailInfoService;
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

public class DetailFragment extends Fragment {

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
        void onReceivedData(String name); // listview의 item 클릭 시, map에서 marker를 focusing하기 위한 callback용 interface
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


        adapter      = new ListViewAdapter();
        fragmentBack = getActivity().findViewById(R.id.fragmentBack);

        listview = view.findViewById(R.id.listview_showInformation);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListViewItem listViewItem = (ListViewItem)parent.getItemAtPosition(position);
                mOnMyListener.onReceivedData(listViewItem.getTitle());
            }
        });

        fragmentBack.setVisibility(View.VISIBLE);

        fragmentBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentBack.setVisibility(View.GONE);
                adapter = new ListViewAdapter(); // reco에서 다시 detail로 넘겨올 때, 이 전 내용이 안보이도록 초기화
                listview.setAdapter(adapter);

                jsonMapsPack = null;
                mOnMyListener.onReceivedData(jsonMapsPack);

                switchFragment(null, "ToReco");
            }
        });

        nextpagetoken = null;
        jsonMaps = null;
        result = null;
        result = (Result)getArguments().getSerializable("RESULT_STATION"); // get data from RecoFragment
        jsonMapsPack = (ArrayList<JsonMaps>)getArguments().getSerializable("ARRAYLIST_RESULT_PLACE");

        if(jsonMapsPack!=null) {
            for (int x = 0; x < jsonMapsPack.size(); x++)
                for (int i = 0; i < jsonMapsPack.get(x).getResults().size(); i++) {
                    adapter.addItem(jsonMapsPack.get(x).getResults().get(i).getIcon(), jsonMapsPack.get(x).getResults().get(i).getName(), jsonMapsPack.get(x).getResults().get(i).getVicinity());
                    adapter.addJsonResult(jsonMapsPack.get(x).getResults().get(i));
                }

            if (mOnMyListener != null) {
                mOnMyListener.onReceivedData(jsonMapsPack);
            }

            adapter.notifyDataSetChanged();
        }

        return view;
    }




}
