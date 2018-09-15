package br.ufop.ildeir.ubspaces.network.requests.post;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.POST;

/**
 * Created by Ildeir on 14/09/2018.
 */

public interface ClearUserTokenRequest {

    @POST("clearUserToken/")
    Call<Response> clearUserToken();

}
