package com.trendpicker.repick;

import android.content.Context;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OpenApi {


     Context context;

    public OpenApi(Context context) {
        context = context;
        context.getResources();
    }
    private OkHttpClient client;
    private static OpenApi instance = new OpenApi();
    public static OpenApi getInstance() {
        return instance;
    }

    private OpenApi(){ this.client = new OkHttpClient(); }
    public void requestWebServer(String appVersion,  Callback callback) {

        String url = "https://trendpicker1.cafe24.com/proc/version-check.php?version="+appVersion;
        System.out.println(url);
        Request request = new Request.Builder()
                // .addHeader("key", "Content-Type")
                // .addHeader("value", "application/json")
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(callback);


    }
}
