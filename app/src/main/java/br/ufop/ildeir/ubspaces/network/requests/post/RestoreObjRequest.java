package br.ufop.ildeir.ubspaces.network.requests.post;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Ildeir on 14/09/2018.
 */

public interface RestoreObjRequest {

    @POST("restore/")
    Call<RequestBody> restoreObj(@Query("id") String id);

}
