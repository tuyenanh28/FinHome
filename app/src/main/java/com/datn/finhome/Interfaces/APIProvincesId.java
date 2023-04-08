package com.datn.finhome.Interfaces;

import retrofit2.Call;
import retrofit2.http.GET;

public interface APIProvincesId {
    String BASE_URL = "https://provinces.open-api.vn/api/";

    @GET("p/")
    Call<String> getState();

    @GET("d/")
    Call<String> getDistrict();

}
