package xyz.jienan.refreshed.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import io.reactivex.Observable;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;
import xyz.jienan.refreshed.BuildConfig;
import xyz.jienan.refreshed.MetaUtils;
import xyz.jienan.refreshed.base.RefreshedApplication;
import xyz.jienan.refreshed.network.entity.ArticlesBean;
import xyz.jienan.refreshed.network.entity.IconsBean;
import xyz.jienan.refreshed.network.entity.NewsSourcesBean;
import xyz.jienan.refreshed.network.entity.TopicsSearchBean;

import static xyz.jienan.refreshed.MetaUtils.NEWSAPI_API_KEY;

/**
 * Created by jienanzhang on 11/01/2018.
 */

public class NetworkService {

    private static final String HOST = "https://newsapi.org/v2/";
    private static final String HOST_ALTER = "http://130.211.211.220:3002/";
    public static final String REQ_IMAGE_PROXY = HOST_ALTER + "images";
    public static final String REQ_TOPICS_SEARCH = HOST_ALTER + "topic-suggest";
    public static final String REQ_UPDATE_TOPICS = HOST_ALTER + "update-topic";
    public static final String REQ_TOPICS_DAYS = HOST_ALTER + "topic-news-days";
    public static final String BYPASS_CACHE = "1";
    public static final String USE_CACHE = "2";


    private static NewsAPI newsAPI;

    private NetworkService() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (request.url().toString().startsWith(HOST)) {
                    request = chain.request().newBuilder()
                            .header("X-Api-Key", MetaUtils.getMeta(NEWSAPI_API_KEY)).build();
                }
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
            if ("1".equals(request.header("bypass"))) {
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
        @Headers("cacheable: 3600")
        @GET("top-headlines")
        Observable<ArticlesBean> getHeadLinesBySource(@Query("sources") String sources, @Header("bypass") String bypass);

        // Topic section
        @Headers("cacheable: 3600")
        @GET("top-headlines?country=us")
        Observable<ArticlesBean> getTopics(@Query("q") String query, @Query("category") String category, @Header("bypass") String bypass);

        @Headers("cacheable: 3600")
        @GET("everything?sortBy=relevancy")
        Observable<ArticlesBean> getCustomQuery(@Query("q") String query, @Query("language") String language, @Query("from") String from,  @Header("bypass") String bypass);

        // Source selection
        @Headers("cacheable: 86400")
        @GET("sources")
        Observable<NewsSourcesBean> getSources(@Query("language") String language, @Query("country") String country);


        // TODO OKHttp doesn't support POST request cache.
        @Headers({"cacheable: 600",
                "Content-Type: application/json"})
        @POST
        Observable<IconsBean> getFeatureImage(@Url String url, @Body RequestBody body);

        @Headers("cacheable: 600")
        @GET
        Observable<List<TopicsSearchBean>> getTopicsSuggestions(@Url String url, @Query("q") String query);

        @Headers("cacheable: 3600")
        @GET
        Observable<ResponseBody> getTopicNewsDays(@Url String url, @Query("q") String query);

        @FormUrlEncoded
        @POST
        Observable<ResponseBody> updateTopicNewsDays(@Url String url, @Field("topic") String topic, @Field("newsDays") int newsDays, @Header("x-api-key") String apiKey);
    }
}
