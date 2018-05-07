package com.example.leesd.smp.RetrofitCall;

import com.example.leesd.smp.DetailSearch.JsonDetail;
import com.example.leesd.smp.googlemaps.JsonMaps;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Created by leesd on 2018-05-06.
 */

public interface DetailInfoService {

    // uri that receives GET request
    @GET("maps/api/place/{category}/json")


    Call<JsonDetail> getPlaces(
            @Path("category") String category, @QueryMap Map<String, String> options
    );

    // base url for request
    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

}
