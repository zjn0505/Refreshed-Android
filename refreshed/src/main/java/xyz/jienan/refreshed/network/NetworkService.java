package xyz.jienan.refreshed.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;

import io.reactivex.Observable;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import retrofit2.http.Url;
import xyz.jienan.refreshed.BuildConfig;
import xyz.jienan.refreshed.base.RefreshedApplication;

/**
 * Created by jienanzhang on 11/01/2018.
 */

public class NetworkService {

    private static final String HOST = "https://newsapi.org/v2/";

    private static NewsAPI newsAPI;

    private NetworkService() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder()
                        .header("X-Api-Key", "2a2736d38cc8461daf7af2a80f2e0ffe").build();
                return chain.proceed(request);
            }
        });

        httpClientBuilder.addNetworkInterceptor(new NetworkCacheInterceptor())
                .addInterceptor(new ApplicationCacheInterceptor());

        //setup cache
        File httpCacheDirectory = new File(RefreshedApplication.getInstance().getCacheDir(), "responses");
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(httpCacheDirectory, cacheSize);

        //add cache to the client
        httpClientBuilder.cache(cache);

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
            httpClientBuilder.addInterceptor(interceptor);
        }

        OkHttpClient client = httpClientBuilder.build();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(HOST)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        Retrofit retrofit = builder.build();
        newsAPI = retrofit.create(NewsAPI.class);
    }


    /**
     * Interceptor to cache data and maintain it for four weeks.
     *
     * If the device is offline, stale (at most four weeks old)
     * response is fetched from the cache.
     */
    private static class ApplicationCacheInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!isNetworkAvailable()) {
                request = request.newBuilder()
                        .removeHeader("pragma")
                        .header("Cache-Control",
                                "public, only-if-cached, max-stale=" + 2419200)
                        .build();
            }
            String cacheable = request.header("cacheable");
            if (TextUtils.isEmpty(cacheable) || cacheable.equals("0")) {
                Request.Builder builder = request.newBuilder().addHeader("Cache-Control", "no-cache");
                request =  builder.build();
            }
            return chain.proceed(request);
        }
    }

    /**
     * Interceptor to cache data and maintain it for a minute.
     *
     * If the same network request is sent within a minute,
     * the response is retrieved from cache.
     */
    private static class NetworkCacheInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            String cacheable = request.header("cacheable");
            okhttp3.Response originalResponse = chain.proceed(request);
            Response.Builder builder = originalResponse.newBuilder().removeHeader("pragma").removeHeader("cacheable");
            if (TextUtils.isEmpty(cacheable)) {
                return builder.build();
            } else {
                return builder.header("Cache-Control", "public, max-age=" + cacheable).build();
            }
        }
    }


    public static NewsAPI getNewsAPI() {
        if (newsAPI == null) {
            new NetworkService();
        }
        return newsAPI;
    }


    public static boolean isNetworkAvailable() {
        Context context = RefreshedApplication.getInstance();
        ConnectivityManager connectivity =(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo anInfo : info) {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public interface NewsAPI {

        // Headline section
        @Headers("cacheable: 86400")
        @GET("top-headlines")
        Observable<HeadlinesBean> getHeadLinesBySource(@Query("sources") String sources);

        @GET("top-headlines")
        Observable<HeadlinesBean> getHeadLinesBySourceWithoutCache(@Query("sources") String sources);

        // Topic section
        @Headers("cacheable: 60")
        @GET("top-headlines")
        Observable<ResponseBody> getHeadLinesByCountryAndCategory(@Query("country") String country, @Query("category") String category);

        @Headers("cacheable: 60")
        @GET("everything?sortBy=relevancy")
        Observable<ResponseBody> getCustomQuery(@Query("q") String query, @Query("language") String language, @Query("from") String from);

        // Source selection
        @Headers("cacheable: 86400")
        @GET("sources")
        Observable<NewsSourcesBean> getSources(@Query("language") String language, @Query("country") String country);


        @Headers("cacheable: 60")
        @GET
        Observable<ResponseBody> getFeatureImage(@Url String url);
        //"https://www.googleapis.com/customsearch/v1?key=%s&cx=%s&searchType=image&q=%s"
    }
}
