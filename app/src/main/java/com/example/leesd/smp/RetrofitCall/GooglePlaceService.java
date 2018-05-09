package com.example.leesd.smp.RetrofitCall;

import com.example.leesd.smp.googlemaps.JsonMaps;

import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Created by leo on 2017-07-16.
 */

// interface for building retrofit object
public interface GooglePlaceService {

    // uri that receives GET request
    @GET("maps/api/place/{category}/json")


    // category is setting of result format whether many places in rough or one place in detail.
    // options are query values for GET request
    Call<JsonMaps> getPlaces(
            @Path("category") String category, @QueryMap Map<String, String> options
    );

    // base url for request
    OkHttpClient okHttpClient = new OkHttpClient();
    
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

}
