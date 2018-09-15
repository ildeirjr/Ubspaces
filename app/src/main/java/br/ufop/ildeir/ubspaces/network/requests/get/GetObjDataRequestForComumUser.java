package br.ufop.ildeir.ubspaces.network.requests.get;

import br.ufop.ildeir.ubspaces.objects.Item;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * Created by Ildeir on 14/09/2018.
 */

public interface GetObjDataRequestForComumUser {

    @GET("getObject/")
    Call<Item> getObjData(@Query("id") String id);

}
