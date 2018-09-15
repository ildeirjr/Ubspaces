package br.ufop.ildeir.ubspaces.network.requests.post;

import com.google.gson.JsonObject;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by Ildeir on 14/09/2018.
 */

public interface LoginRequest {

    @POST("validateLogin/")
    Call<JsonObject> login(@Body RequestBody object);

}
