package br.ufop.ildeir.ubspaces.network.requests.post;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Ildeir on 14/09/2018.
 */

public interface EditObjDataRequest {

    @POST("edit/")
    Call<RequestBody> editObjData(@Body RequestBody object);

}
