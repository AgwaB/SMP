package com.example.leesd.smp.RetrofitCall;


import com.example.leesd.smp.googlemaps.JsonMaps;
import retrofit2.Response;

/**
 * Created by leo on 2017-07-17.
 */

// interface for getting response in activity
public interface AsyncResponseMaps {
    void processFinish(Response<JsonMaps> response); // call if Retrofit is finished
}
