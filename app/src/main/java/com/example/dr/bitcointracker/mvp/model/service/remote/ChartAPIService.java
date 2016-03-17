package com.example.dr.bitcointracker.mvp.model.service.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

@Singleton
public interface ChartAPIService {
    String ENDPOINT = "https://blockchain.info/charts/";

    @GET("market-price?timespan=all&format=json")
    Observable<JsonObject> getAllBitcoinMarketPrice();

     // Helper class that sets up a new services
    class Creator {

        public static ChartAPIService newChartAPIService() {
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ChartAPIService.ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            return retrofit.create(ChartAPIService.class);
        }
    }
}
