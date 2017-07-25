package com.linkloving.dyh08.RetrofitUtils.RetrofitApi;


import com.linkloving.dyh08.RetrofitUtils.Bean.CheckFirmwareVersionReponse;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Created by Daniel.Xu on 2017/2/19.
 */

public interface OADApi {
    @POST("Firmware/checkFirmwareVersion")
    Call<CheckFirmwareVersionReponse> checkFirmwareVersion(@Body HashMap hashMap);

    @GET("download_file")
    Call<ResponseBody> download_file(@Header("Connection") String Authorization,@QueryMap HashMap<String, Object> hashMap);

}
