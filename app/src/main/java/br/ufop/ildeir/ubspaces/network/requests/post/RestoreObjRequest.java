package br.ufop.ildeir.ubspaces.network.requests.post;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Ildeir on 14/09/2018.
 */

public interface RestoreObjRequest {

    @POST("restore/")
    Call<ResponseBody> restoreObj(@Query("id") String id);

}
