package br.ufop.ildeir.ubspaces.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import br.ufop.ildeir.ubspaces.R;
import br.ufop.ildeir.ubspaces.requests.GetUserRequest;
import br.ufop.ildeir.ubspaces.singleton.SessionManager;
import br.ufop.ildeir.ubspaces.singleton.UserSingleton;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

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
                    try {
                        String userId = SessionManager.getInstance().getUserId();
                        String result = new GetUserRequest(userId).execute().get();
                        if(result != null) {
                            JSONObject usr = new JSONObject(result);
                            UserSingleton singleton = UserSingleton.getInstance();
                            singleton.setNome(usr.getString("nome"));
                            singleton.setEmail(usr.getString("email"));
                            singleton.setDia(usr.getInt("dia_nasc"));
                            singleton.setDia(usr.getInt("mes_nasc"));
                            singleton.setDia(usr.getInt("ano_nasc"));
                            singleton.setDepto(usr.getString("depto"));
                            System.out.println(singleton.getNome());
                            Intent it = new Intent(SplashActivity.this, HomeActivity.class);
                            startActivity(it);
                        }
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        },SPLASH_TIME_OUT);

    }
}
