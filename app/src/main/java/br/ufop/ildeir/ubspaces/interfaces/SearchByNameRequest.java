package br.ufop.ildeir.ubspaces.interfaces;

import java.util.ArrayList;

import br.ufop.ildeir.ubspaces.objects.RecyclerViewItem;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Ildeir on 11/09/2018.
 */

public interface SearchByNameRequest {

    @GET("searchName/")
    Call<ArrayList<RecyclerViewItem>> searchByName(@Query("substring") String substring,
                                                   @Query("mode") String mode);

}