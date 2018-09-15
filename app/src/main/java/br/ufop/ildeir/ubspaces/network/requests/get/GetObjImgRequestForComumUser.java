package br.ufop.ildeir.ubspaces.network.requests.get;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

/**
 * Created by Ildeir on 14/09/2018.
 */

public interface GetObjImgRequestForComumUser {

    @Headers("Authorization: 1gdh87efuhwi")
    @GET("photos/{img_path}")
    Call<ResponseBody> getObjImg(@Path("img_path") String imgPath);

}
