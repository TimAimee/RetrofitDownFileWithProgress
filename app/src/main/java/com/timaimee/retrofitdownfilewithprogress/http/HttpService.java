package com.timaimee.retrofitdownfilewithprogress.http;


import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;


/**
 * Created by TimAimee on 2016/7/20.
 */
public interface HttpService {

    @Streaming
    @GET
    Observable<Response<ResponseBody>> downloadApkUseStream(@Url String url);

}
