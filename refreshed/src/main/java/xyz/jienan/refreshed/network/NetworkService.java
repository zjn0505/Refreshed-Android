package xyz.jienan.refreshed.network;

import java.io.IOException;


import io.reactivex.Observable;
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
import retrofit2.http.Query;
import retrofit2.http.Url;
import xyz.jienan.refreshed.BuildConfig;

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

    public static NewsAPI getNewsAPI() {
        if (newsAPI == null) {
            new NetworkService();
        }
        return newsAPI;
    }

    public interface NewsAPI {

        // Headline section
        @GET("top-headlines")
        Observable<HeadlinesBean> getHeadLinesBySource(@Query("sources") String sources);


        // Topic section
        @GET("top-headlines")
        Observable<ResponseBody> getHeadLinesByCountryAndCategory(@Query("country") String country, @Query("category") String category);

        @GET("everything?sortBy=relevancy")
        Observable<ResponseBody> getCustomQuery(@Query("q") String query, @Query("language") String language, @Query("from") String from);

        // Source selection
        @GET("sources")
        Observable<NewsSourceBean> getSources(@Query("language") String language, @Query("country") String country);


        @GET
        Observable<ResponseBody> getFeatureImage(@Url String url);
        //"https://www.googleapis.com/customsearch/v1?key=%s&cx=%s&searchType=image&q=%s"
    }
}
