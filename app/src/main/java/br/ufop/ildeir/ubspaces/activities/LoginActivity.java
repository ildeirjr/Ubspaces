package br.ufop.ildeir.ubspaces.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import br.ufop.ildeir.ubspaces.R;
import br.ufop.ildeir.ubspaces.requests.GetUserRequest;
import br.ufop.ildeir.ubspaces.requests.LoginRequest;
import br.ufop.ildeir.ubspaces.singleton.SessionManager;
import br.ufop.ildeir.ubspaces.singleton.UserSingleton;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText senha;
    private Button btnLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //SessionManager.getInstance().setSession(getApplicationContext());

        email = findViewById(R.id.usrEmail);
        senha = findViewById(R.id.usrPass);
        btnLogin = findViewById(R.id.btnLogin);
    }

    public void login(View view) {

        JSONObject obj = new JSONObject();
        JSONObject authenticationObj;
        String userId, userToken;
        try {
            obj.put("email",email.getText().toString());
            obj.put("senha",senha.getText().toString());
            Log.e("teste",obj.getString("email"));
            Log.e("teste",obj.getString("senha"));
            LoginRequest loginRequest = new LoginRequest(obj.toString());
            String result = loginRequest.execute().get();
            System.out.println(result);
            //Log.e("teste",result);
            if(!result.equals("0")){
                authenticationObj = new JSONObject(result);
                userId = authenticationObj.getString("idUser");
                userToken = authenticationObj.getString("token");
                SessionManager.getInstance().createLoginSession(userToken,userId);
                result = new GetUserRequest(userId).execute().get();
                Log.e("teste",result);
                JSONObject usr = new JSONObject(result);
                UserSingleton singleton = UserSingleton.getInstance();
                singleton.setNome(usr.getString("nome"));
                singleton.setEmail(usr.getString("email"));
                singleton.setDia(usr.getInt("dia_nasc"));
                singleton.setDia(usr.getInt("mes_nasc"));
                singleton.setDia(usr.getInt("ano_nasc"));
                singleton.setDepto(usr.getString("depto"));
                System.out.println(singleton.getNome());
                Intent it = new Intent(this,HomeActivity.class);
                startActivity(it);
                finish();
            }else{
                Toast.makeText(this, "Dados inválidos!", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }



}
