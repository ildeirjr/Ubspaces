package br.ufop.ildeir.ubspaces.network;

import java.io.IOException;

import br.ufop.ildeir.ubspaces.interfaces.GetObjListRequest;
import br.ufop.ildeir.ubspaces.interfaces.GetObjThumbRequest;
import br.ufop.ildeir.ubspaces.interfaces.SearchByDateRequest;
import br.ufop.ildeir.ubspaces.interfaces.SearchByNameRequest;
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

                Request request = original.newBuilder()
                        .header("Authorization", SessionManager.getInstance().getUserToken())
                        .method(original.method(), original.body())
                        .build();

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

    public GetObjListRequest getObjListRequest(){
        return this.retrofit.create(GetObjListRequest.class);
    }

    public GetObjThumbRequest getObjThumbRequest(){
        return this.retrofit.create(GetObjThumbRequest.class);
    }

    public SearchByNameRequest searchByNameRequest(){
        return this.retrofit.create(SearchByNameRequest.class);
    }

    public SearchByDateRequest searchByDateRequest(){
        return this.retrofit.create(SearchByDateRequest.class);
    }

}
