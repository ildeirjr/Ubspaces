package br.ufop.ildeir.ubspaces.network.requests.get;

import java.util.ArrayList;

import br.ufop.ildeir.ubspaces.objects.Item;
import br.ufop.ildeir.ubspaces.objects.RecyclerViewItem;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Ildeir on 11/09/2018.
 */

public interface SearchByIdRequest {

    @GET("searchId/")
    Call<Item> searchById(@Query("mode") String mode,
                          @Query("id") String id);

}
