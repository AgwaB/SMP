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

            return response;

        } catch (Exception e){

            Log.e("AsyncTask", e.getMessage());
        }

        return null;
    }
    @Override
    protected void onPostExecute(Response<JsonMaps> response){
        // call if doInBackground returns response or null

        // ListViewActivity에서 response(검색결과물)을 매개변수로 받아서 실행
        // 원래 activity에서는 background단에서 결과물을 바로 받을 수 없음
        delegate.processFinish(response);
    }
}
