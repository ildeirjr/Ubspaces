package br.ufop.ildeir.ubspaces.interfaces;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Ildeir on 11/09/2018.
 */

public interface GetObjThumbRequest {

    @GET("photos/thumbs/{imgPath}")
    Call<ResponseBody> getObjThumb(@Path("imgPath") String imgPath);

}
