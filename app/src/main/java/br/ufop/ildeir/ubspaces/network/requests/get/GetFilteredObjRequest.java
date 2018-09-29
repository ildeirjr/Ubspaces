package br.ufop.ildeir.ubspaces.network.requests.get;

import java.util.ArrayList;

import br.ufop.ildeir.ubspaces.objects.RecyclerViewItem;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Ildeir on 27/09/2018.
 */

public interface GetFilteredObjRequest {

    @GET("filter/")
    Call<ArrayList<RecyclerViewItem>> getFilteredObjects(@Query("mode") String mode,
                                                         @Query("num_page") String numPage,
                                                         @Query("window_size") String windowSize,
                                                         @Query("filter_params") String filterParams);

}
