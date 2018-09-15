package br.ufop.ildeir.ubspaces.network;

import android.util.Log;

import java.io.IOException;

import br.ufop.ildeir.ubspaces.network.requests.post.ClearUserTokenRequest;
import br.ufop.ildeir.ubspaces.network.requests.delete.DeleteObjRequest;
import br.ufop.ildeir.ubspaces.network.requests.get.GetMetadataRequest;
import br.ufop.ildeir.ubspaces.network.requests.get.GetObjDataRequest;
import br.ufop.ildeir.ubspaces.network.requests.get.GetObjDataRequestForComumUser;
import br.ufop.ildeir.ubspaces.network.requests.get.GetObjImgRequest;
import br.ufop.ildeir.ubspaces.network.requests.get.GetObjImgRequestForComumUser;
import br.ufop.ildeir.ubspaces.network.requests.get.GetObjListRequest;
import br.ufop.ildeir.ubspaces.network.requests.get.GetObjThumbRequest;
import br.ufop.ildeir.ubspaces.network.requests.get.GetUserRequest;
import br.ufop.ildeir.ubspaces.network.requests.get.SearchByDateRequest;
import br.ufop.ildeir.ubspaces.network.requests.get.SearchByNameRequest;
import br.ufop.ildeir.ubspaces.network.requests.post.EditObjDataRequest;
import br.ufop.ildeir.ubspaces.network.requests.post.LoginRequest;
import br.ufop.ildeir.ubspaces.network.requests.post.PostObjDataRequest;
import br.ufop.ildeir.ubspaces.network.requests.post.PostObjImgRequest;
import br.ufop.ildeir.ubspaces.network.requests.post.RestoreObjRequest;
import br.ufop.ildeir.ubspaces.miscellaneous.UbspaceURL;
import br.ufop.ildeir.ubspaces.singleton.SessionManager;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Ildeir on 11/09/2018.
 */

public class RetrofitConfig {

    private final Retrofit retrofit;
    OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    public RetrofitConfig() {

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request request;

                String token = SessionManager.getInstance().getUserToken();

                if(token != null) {
                    request = original.newBuilder()
                            .header("Authorization", SessionManager.getInstance().getUserToken())
                            .method(original.method(), original.body())
                            .build();
                } else {
                    request = original.newBuilder()
                            .header("Authorization", "1gdh87efuhwi")
                            .method(original.method(), original.body())
                            .build();
                }

                Response response = chain.proceed(request);

                if(response.code() == 401){
                    SessionManager.getInstance().toLoginActivity();
                    response.close();
                }

                return response;
            }
        });

        OkHttpClient client = httpClient.build();

        this.retrofit = new Retrofit.Builder()
                .baseUrl(new UbspaceURL().getUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    /** GET REQUESTS **/

    public GetMetadataRequest getMetadataRequest(){
        return this.retrofit.create(GetMetadataRequest.class);
    }

    public GetObjDataRequest getObjDataRequest(){
        return this.retrofit.create(GetObjDataRequest.class);
    }

    public GetObjDataRequestForComumUser getObjDataRequestForComumUser(){
        return this.retrofit.create(GetObjDataRequestForComumUser.class);
    }

    public GetObjImgRequest getObjImgRequest(){
        return this.retrofit.create(GetObjImgRequest.class);
    }

    public GetObjImgRequestForComumUser getObjImgRequestForComumUser(){
        return this.retrofit.create(GetObjImgRequestForComumUser.class);
    }

    public GetObjListRequest getObjListRequest(){
        return this.retrofit.create(GetObjListRequest.class);
    }

    public GetObjThumbRequest getObjThumbRequest(){
        return this.retrofit.create(GetObjThumbRequest.class);
    }

    public GetUserRequest getUserRequest(){
        return this.retrofit.create(GetUserRequest.class);
    }

    public SearchByDateRequest searchByDateRequest(){
        return this.retrofit.create(SearchByDateRequest.class);
    }

    public SearchByNameRequest searchByNameRequest(){
        return this.retrofit.create(SearchByNameRequest.class);
    }

    /** POST REQUESTS **/

    public ClearUserTokenRequest clearUserTokenRequest(){
        return this.retrofit.create(ClearUserTokenRequest.class);
    }

    public EditObjDataRequest editObjDataRequest(){
        return this.retrofit.create(EditObjDataRequest.class);
    }

    public LoginRequest loginRequest(){
        return this.retrofit.create(LoginRequest.class);
    }

    public PostObjDataRequest postObjDataRequest(){
        return this.retrofit.create(PostObjDataRequest.class);
    }

    public PostObjImgRequest postObjImgRequest(){
        return this.retrofit.create(PostObjImgRequest.class);
    }

    public RestoreObjRequest restoreObjRequest(){
        return this.retrofit.create(RestoreObjRequest.class);
    }

    /** DELETE REQUESTS **/

    public DeleteObjRequest deleteObjRequest(){
        return this.retrofit.create(DeleteObjRequest.class);
    }




}
