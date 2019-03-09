package br.ufop.ildeir.ubspaces.network.requests.post;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Ildeir on 14/09/2018.
 */

public interface DeleteObjRequest {

    @POST("delete/")
    Call<ResponseBody> deleteObj(@Query("id") String id,
                                 @Query("foto") String foto,
                                 @Query("delete_user") String deleteUser);

}
