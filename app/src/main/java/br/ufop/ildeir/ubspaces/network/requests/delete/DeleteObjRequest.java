package br.ufop.ildeir.ubspaces.network.requests.delete;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.DELETE;
import retrofit2.http.Query;

/**
 * Created by Ildeir on 14/09/2018.
 */

public interface DeleteObjRequest {

    @DELETE("delete/")
    Call<Response> deleteObj(@Query("id") String id,
                             @Query("foto") String foto,
                             @Query("delete_user") String deleteUser);

}
