package com.example.leesd.smp.RetrofitCall;

import retrofit2.Response;

/**
 * Created by Leo on 2018-05-10.
 */

public interface AsyncResponse<T> {
    void processFinish(Response <T> response);    // call if Retrofit is finished
}
