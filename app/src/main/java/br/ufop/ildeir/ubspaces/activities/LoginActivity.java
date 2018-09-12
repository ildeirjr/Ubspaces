package br.ufop.ildeir.ubspaces.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import br.ufop.ildeir.ubspaces.R;
import br.ufop.ildeir.ubspaces.objects.Item;
import br.ufop.ildeir.ubspaces.requests.get.GetObjDataRequestForComumUser;
import br.ufop.ildeir.ubspaces.requests.get.GetObjImgRequestForComumUser;
import br.ufop.ildeir.ubspaces.requests.get.GetUserRequest;
import br.ufop.ildeir.ubspaces.requests.post.LoginRequest;
import br.ufop.ildeir.ubspaces.singleton.ItemSingleton;
import br.ufop.ildeir.ubspaces.singleton.SessionManager;
import br.ufop.ildeir.ubspaces.singleton.UserSingleton;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText senha;
    private Button btnLogin;
    private IntentIntegrator intentIntegrator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //SessionManager.getInstance().setSession(getApplicationContext());

        email = findViewById(R.id.usrEmail);
        senha = findViewById(R.id.usrPass);
        btnLogin = findViewById(R.id.btnLogin);

        intentIntegrator = new IntentIntegrator(this);
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
                singleton.setDataNasc(usr.getString("data_nasc"));
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }

    public void scan(View view) {
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        intentIntegrator.setBeepEnabled(false);
        intentIntegrator.setCaptureActivity(ScanActivity.class);
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent showObject = new Intent(this,VisualizeObjActivity.class);
        Intent objNotFound = new Intent(this,ObjNotFoundActivity.class);
        showObject.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(intentResult != null){
            if(intentResult.getContents() != null){
                try {
                    Log.e("item scan",intentResult.getContents());
                    Item item = new GetObjDataRequestForComumUser(intentResult.getContents()).execute().get();
                    if(item != null){
                        item.setImg(new GetObjImgRequestForComumUser(item.getFoto()).execute().get());
                        ItemSingleton.getInstance().setItemSingleton(item);
                        startActivity(showObject);
                    }else{
                        startActivity(objNotFound);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
