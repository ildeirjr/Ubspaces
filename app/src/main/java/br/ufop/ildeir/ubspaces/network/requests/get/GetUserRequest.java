package br.ufop.ildeir.ubspaces.network.requests.get;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Ildeir on 14/09/2018.
 */

public interface GetUserRequest {

    @GET("getOperator/")
    Call<JsonObject> getUser(@Query("id") String id);

}
