package com.timaimee.retrofitdownfilewithprogress.http;


import com.timaimee.retrofitdownfilewithprogress.progress.ProgressListener;
import com.timaimee.retrofitdownfilewithprogress.progress.ProgressResponseBody;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by TimAimee on 2016/7/27.
 */
public class HttpDownUtil {
    private final static String BASE_URL = "http://shouji.360tpcdn.com/";
    private final static String TAG = HttpDownUtil.class.getSimpleName();
    private final static int CONNECT_TIME_OUT = 30;
    private volatile static HttpDownUtil instance; //声明成 volatile
    static OkHttpClient.Builder httpClient;
    static HttpLoggingInterceptor httpLoggingInterceptor;
    static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create());

    private HttpDownUtil() {

    }

    public static HttpDownUtil getInstance() {
        if (instance == null) {
            synchronized (HttpDownUtil.class) {
                if (instance == null) {
                    instance = new HttpDownUtil();
                    httpLoggingInterceptor = getHttpLoggingInterceptor();
                    getHttpClient();
                }
            }
        }
        return instance;
    }

    private static void getHttpClient() {
        httpClient = new OkHttpClient().newBuilder();
        httpClient.addInterceptor(httpLoggingInterceptor);
        httpClient.connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS);
    }


    private static HttpLoggingInterceptor getHttpLoggingInterceptor() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        return logging;
    }


    public static <S> S creatService(Class<S> serviceClass, final ProgressListener progressListener) {
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                okhttp3.Response originalResponse = chain.proceed(chain.request());
                ProgressResponseBody responseBody = new ProgressResponseBody(originalResponse.body(), progressListener);
                return originalResponse.newBuilder()
                        .body(responseBody)
                        .build();
            }
        });
        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(serviceClass);
    }

    public static void down(String url, ProgressListener progressListener, Subscriber<Response<ResponseBody>> responseBodySubscriber) {

        creatService(HttpService.class, progressListener)
                .downloadApkUseStream(url)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(responseBodySubscriber);


    }


}
