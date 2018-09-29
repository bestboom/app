package com.app.dlike.http;

import android.content.Context;

import com.app.dlike.Tools;
import com.app.dlike.api.Steem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Sanat.Shukla on 05/01/17.
 */

public class RetrofitClient {
    private Steem apiService = null;
    private static RetrofitClient retrofitClient = null;

    private static Context context;

    public static RetrofitClient getInstance(Context ctx, String url) {
        if (retrofitClient == null) {
            retrofitClient = new RetrofitClient(ctx,url);

        }
        return retrofitClient;
    }

    public RetrofitClient(final Context ctx, String url) {
        context = ctx;
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        File httpCacheDirectory = new File(context.getCacheDir(), "responses");
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(httpCacheDirectory, cacheSize);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.readTimeout(60, TimeUnit.SECONDS);
        builder.connectTimeout(60, TimeUnit.SECONDS);
        builder.cache(new Cache(context.getCacheDir(), 10 * 1024 * 1024)) // 10 MB
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    if (Tools.isNetAvail(context)) {
                        request = request.newBuilder().header("Cache-Control", "public, max-age=" + 60).build();
                    } else {
                        request = request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7).build();
                    }
                    return chain.proceed(request);
                });
        OkHttpClient client = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(url)
                .client(client)
                .build();

        apiService = retrofit.create(Steem.class);


    }

    public Steem getSteemService() {
        return apiService;
    }
}
