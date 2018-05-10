package com.example.leesd.smp.RetrofitCall;


import com.example.leesd.smp.DetailSearch.JsonDetail;
import com.example.leesd.smp.googlemaps.JsonMaps;

import retrofit2.Response;

/**
 * Created by leo on 2017-07-17.
 */

// interface for getting response in activity
public interface AsyncResponseDetail extends AsyncResponse<JsonDetail> {
    void processFinish(Response<JsonDetail> response);    // call if Retrofit is finished
}
