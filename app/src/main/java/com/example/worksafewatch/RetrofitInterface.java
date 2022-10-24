package com.example.worksafewatch;

import retrofit2.Call;
import retrofit2.http.GET;

import java.util.List;

public interface RetrofitInterface {

        @GET("/beacons")
        Call<List<BeaconsResult>> getBeacons ();

}
