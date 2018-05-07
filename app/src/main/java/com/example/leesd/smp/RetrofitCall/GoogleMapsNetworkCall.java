package com.example.leesd.smp.RetrofitCall;

import android.os.AsyncTask;
import android.util.Log;

import com.example.leesd.smp.googlemaps.JsonMaps;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by leo on 2017-07-17.
 */

public class GoogleMapsNetworkCall extends AsyncTask<Call, Void, Response<JsonMaps>> {
    public AsyncResponseMaps delegate = null;
    @Override
    protected Response<JsonMaps> doInBackground(Call... params){
        // execute thread for background http call
        try {

            Call<JsonMaps> call = params[0]; // get call params
            Response<JsonMaps> response = call.execute(); // execute call

            Log.d("asfdsafasd", Integer.toString(response.body().getResults().size()));
            return response;

        } catch (Exception e){

            Log.e("AsyncTask", e.getMessage());
        }

        return null;
    }


    @Override
    protected void onPostExecute(Response<JsonMaps> response){
        // call if doInBackground returns response or null

        // send a response object to activity
        delegate.processFinish(response);

        Log.d("end", "ddddddd");
    }
}
