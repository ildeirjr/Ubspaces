package br.ufop.ildeir.ubspaces.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import br.ufop.ildeir.ubspaces.R;
import br.ufop.ildeir.ubspaces.network.RetrofitConfig;
import br.ufop.ildeir.ubspaces.requests.get.GetUserRequest;
import br.ufop.ildeir.ubspaces.singleton.SessionManager;
import br.ufop.ildeir.ubspaces.singleton.UserSingleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 2000;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = findViewById(R.id.progress_bar);

        SessionManager.getInstance().setSession(getApplicationContext());
        //SessionManager.getInstance().logoutUser();

        new Handler().postDelayed(new Runnable(){

            @Override
            public void run(){
                if(!SessionManager.getInstance().isLoggedIn()) {
                    Intent it = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(it);
                    finish();
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                    String userId = SessionManager.getInstance().getUserId();
                    Log.e("codigo",userId);
                    Call<JsonObject> call = new RetrofitConfig().getUserRequest().getUser(userId);
                    call.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            Log.e("codigo", String.valueOf(response.code()));
                            JsonObject object = response.body();
                            System.out.println(object.toString());
                            UserSingleton userSingleton = UserSingleton.getInstance();
                            userSingleton.setNome(object.get("nome").getAsString());
                            userSingleton.setEmail(object.get("email").getAsString());
                            userSingleton.setDataNasc(object.get("data_nasc").getAsString());
                            userSingleton.setDepto(object.get("depto").getAsString());
                            Intent it = new Intent(SplashActivity.this, HomeActivity.class);
                            startActivity(it);
                            finish();
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            Toast.makeText(SplashActivity.this, "Usuário não encontrado", Toast.LENGTH_SHORT).show();
                        }
                    });
//                    try {
//                        String userId = SessionManager.getInstance().getUserId();
//                        String result = new GetUserRequest(userId).execute().get();
//                        if(result != null) {
//                            JSONObject usr = new JSONObject(result);
//                            UserSingleton singleton = UserSingleton.getInstance();
//                            singleton.setNome(usr.getString("nome"));
//                            singleton.setEmail(usr.getString("email"));
//                            singleton.setDataNasc(usr.getString("data_nasc"));
//                            singleton.setDepto(usr.getString("depto"));
//                            System.out.println(singleton.getNome());
//                            Intent it = new Intent(SplashActivity.this, HomeActivity.class);
//                            startActivity(it);
//                        }
//                        finish();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    } catch (ExecutionException e) {
//                        e.printStackTrace();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
                }
            }

        },SPLASH_TIME_OUT);

    }
}
