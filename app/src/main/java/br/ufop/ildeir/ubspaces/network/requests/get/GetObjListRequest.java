package br.ufop.ildeir.ubspaces.network.requests.get;

import java.util.ArrayList;

import br.ufop.ildeir.ubspaces.objects.RecyclerViewItem;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Ildeir on 11/09/2018.
 */

public interface GetObjListRequest {

    @GET("listall/")
    Call<ArrayList<RecyclerViewItem>> getObjList(@Query("mode") String mode,
                                                 @Query("num_page") String num_page,
                                                 @Query("window_size") String window_size);

}
