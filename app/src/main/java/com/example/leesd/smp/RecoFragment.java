package com.example.leesd.smp;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by leesd on 2018-03-15.
 * Modified by Leo Park on 2018-05-06.
 *
 */

public class RecoFragment extends Fragment{
    @Nullable
    
    private ArrayList<LatLng> positionList; // list of user positions
    
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recommend, container, false);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        
        // reference to button ui
        Button buttonDistance = (Button) getView().findViewById(R.id.button_distance);
        Button buttonHotplace = (Button) getView().findViewById(R.id.button_hotplace);
        
        // get position list bundle in fragment
        if(getArguments() != null){
            positionList = getArguments().getParcelableArrayList("positions");
        }
        
        // search places that nearby median position of lists
        buttonDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positionList == null) {
                    return;
                }
                
                LatLng medianPosition = getMidPoint(positionList);
                Context context = getActivity().getApplicationContext();
                CharSequence text = new String(medianPosition.latitude + ", " + medianPosition.longitude);
                int duration = Toast.LENGTH_LONG;
    
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });
        
        
    }
    
    // calculate median position of list
    private LatLng getMidPoint(Iterable<LatLng> list){
        double sumLat = 0.0D;
        double sumLng = 0.0D;
        
        int length = 0;
        for(LatLng position : list){
            sumLat += position.latitude;
            sumLng += position.longitude;
            
            length++;
        }
        
        return new LatLng((double) sumLat / length, (double) sumLng / length);
        
    }
}
