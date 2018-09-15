package br.ufop.ildeir.ubspaces.network.requests.post;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Ildeir on 14/09/2018.
 */

public interface PostObjImgRequest {

    @POST("uploadImg/")
    Call<RequestBody> postObjImg(@Body RequestBody object);

}
