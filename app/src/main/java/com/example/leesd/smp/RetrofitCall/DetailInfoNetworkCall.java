package com.example.leesd.smp.RetrofitCall;

import android.os.AsyncTask;
import android.util.Log;

import com.example.leesd.smp.DetailSearch.JsonDetail;
import com.example.leesd.smp.googlemaps.JsonMaps;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by leesd on 2018-05-06.
 */

public class DetailInfoNetworkCall extends AsyncTask<Call, Void, Response<JsonDetail>> {
    public AsyncResponseMaps delegate = null;

    @Override
    protected Response<JsonDetail> doInBackground(Call... params) {
        // execute thread for background http call
        try {

            Call<JsonDetail> call = params[0]; // get call params
            Response<JsonDetail> response = call.execute(); // execute call

            return response;

        } catch (Exception e) {

            Log.e("AsyncTask", e.getMessage());
        }

        return null;
    }


    @Override
    protected void onPostExecute(Response<JsonDetail> response) {
        // call if doInBackground returns response or null

        // send a response object to activity
        if(response!=null)
            delegate.processDetailFinish(response);
    }
}