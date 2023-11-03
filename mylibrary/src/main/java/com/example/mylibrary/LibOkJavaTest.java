package com.example.mylibrary;

import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LibOkJavaTest {

    private static final String TAG = "LibOkJavaTest";

    public static final String GET_URL = "https://www.baidu.com/";
    public static final String TYPE = "application/octet-stream";
    public static final String POST_URL = "http://zhushou.72g.com/app/gift/gift_list/";

    public static void asyncGetRequest() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                OkHttpClient okHttpClient = new OkHttpClient().newBuilder().build();
                Request request = new Request.Builder()
                        .url(GET_URL)
                        .build();

                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String string = response.body().string();
                        Log.i(TAG, "asyncGetRequest onResponse111: ");
                    }
                });
            }
        }.start();
    }
}
