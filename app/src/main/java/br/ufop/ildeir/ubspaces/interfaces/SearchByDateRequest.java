package br.ufop.ildeir.ubspaces.interfaces;

import java.util.ArrayList;

import br.ufop.ildeir.ubspaces.objects.RecyclerViewItem;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Ildeir on 12/09/2018.
 */

public interface SearchByDateRequest {

    @GET("searchDate/")
    Call<ArrayList<RecyclerViewItem>> searchByDate(@Query("mode") String mode,
                                                   @Query("date_start") String dateStart,
                                                   @Query("date_end") String dateEnd,
                                                   @Query("num_page") String num_page,
                                                   @Query("window_size") String window_size);

}
