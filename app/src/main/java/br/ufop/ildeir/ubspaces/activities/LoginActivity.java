package br.ufop.ildeir.ubspaces.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import br.ufop.ildeir.ubspaces.R;
import br.ufop.ildeir.ubspaces.network.RetrofitConfig;
import br.ufop.ildeir.ubspaces.objects.Item;
import br.ufop.ildeir.ubspaces.requests.get.GetObjDataRequestForComumUser;
import br.ufop.ildeir.ubspaces.requests.get.GetObjImgRequestForComumUser;
import br.ufop.ildeir.ubspaces.requests.get.GetUserRequest;
import br.ufop.ildeir.ubspaces.requests.post.LoginRequest;
import br.ufop.ildeir.ubspaces.singleton.ItemSingleton;
import br.ufop.ildeir.ubspaces.singleton.SessionManager;
import br.ufop.ildeir.ubspaces.singleton.UserSingleton;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText senha;
    private Button btnLogin;
    private IntentIntegrator intentIntegrator;
    private ProgressDialog progressDialog;
    private ProgressBar progressBar;
    private LinearLayout linearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //SessionManager.getInstance().setSession(getApplicationContext());

        email = findViewById(R.id.usrEmail);
        senha = findViewById(R.id.usrPass);
        btnLogin = findViewById(R.id.btnLogin);

        intentIntegrator = new IntentIntegrator(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Carregando");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        progressBar = findViewById(R.id.progress_bar);
        linearLayout = findViewById(R.id.login_buttons_layout);

        progressBar.getIndeterminateDrawable().setColorFilter(new LightingColorFilter(0xFF000000, 0xFFFFFF));

    }

    public void login(View view) {

        linearLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        JSONObject obj = new JSONObject();
        try {
            obj.put("email",email.getText().toString());
            obj.put("senha",senha.getText().toString());
            Log.e("teste",obj.getString("email"));
            Log.e("teste",obj.getString("senha"));
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), obj.toString());
            Call<JsonObject> call = new RetrofitConfig().loginRequest().login(body);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    JsonObject object = response.body();
                    String userId = object.get("idUser").getAsString();
                    String userToken = object.get("token").getAsString();
                    SessionManager.getInstance().createLoginSession(userToken, userId);

                    Call<JsonObject> userCall = new RetrofitConfig().getUserRequest().getUser(userId);
                    userCall.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            JsonObject usr = response.body();
                            UserSingleton singleton = UserSingleton.getInstance();
                            singleton.setNome(usr.get("nome").getAsString());
                            singleton.setEmail(usr.get("email").getAsString());
                            singleton.setDataNasc(usr.get("data_nasc").getAsString());
                            singleton.setDepto(usr.get("depto").getAsString());
                            Intent it = new Intent(getApplicationContext(),HomeActivity.class);
                            startActivity(it);
                            finish();
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {

                        }
                    });

                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Dados inválidos!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);
                }
            });
//            LoginRequest loginRequest = new LoginRequest(obj.toString());
//            String result = loginRequest.execute().get();
//            System.out.println(result);
//            //Log.e("teste",result);
//            if(!result.equals("0")){
//                authenticationObj = new JSONObject(result);
//                userId = authenticationObj.getString("idUser");
//                userToken = authenticationObj.getString("token");
//                SessionManager.getInstance().createLoginSession(userToken,userId);
//                result = new GetUserRequest(userId).execute().get();
//                Log.e("teste",result);
//                JSONObject usr = new JSONObject(result);
//                UserSingleton singleton = UserSingleton.getInstance();
//                singleton.setNome(usr.getString("nome"));
//                singleton.setEmail(usr.getString("email"));
//                singleton.setDataNasc(usr.getString("data_nasc"));
//                singleton.setSala(usr.getString("depto"));
//                System.out.println(singleton.getNome());
//                Intent it = new Intent(this,HomeActivity.class);
//                startActivity(it);
//                finish();
//            }else{
//                Toast.makeText(this, "Dados inválidos!", Toast.LENGTH_SHORT).show();
//            }
        } catch (JSONException e) {
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
        final Intent showObject = new Intent(this,VisualizeObjActivity.class);
        final Intent objNotFound = new Intent(this,ObjNotFoundActivity.class);
        showObject.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(intentResult != null){
            if(intentResult.getContents() != null){
//                try {
//                    Log.e("item scan",intentResult.getContents());
//                    Item item = new GetObjDataRequestForComumUser(intentResult.getContents()).execute().get();
//                    if(item != null){
//                        item.setImg(new GetObjImgRequestForComumUser(item.getFoto()).execute().get());
//                        ItemSingleton.getInstance().setItemSingleton(item);
//                        startActivity(showObject);
//                    }else{
//                        startActivity(objNotFound);
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }
                progressDialog.show();
                Call<Item> call = new RetrofitConfig().getObjDataRequestForComumUser().getObjData(intentResult.getContents());
                call.enqueue(new Callback<Item>() {
                    @Override
                    public void onResponse(Call<Item> call, Response<Item> response) {
                        final Item item = response.body();
                        Log.e("teste","nao é null");
                        Call<ResponseBody> imgCall = new RetrofitConfig().getObjImgRequestForComumUser().getObjImg(item.getFoto());
                        imgCall.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if(response.body() != null){
                                    try {
                                        item.setImg(response.body().bytes());
                                        ItemSingleton.getInstance().setItemSingleton(item);
                                        startActivity(showObject);
                                        progressDialog.dismiss();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Call<ResponseBody> imgCall = new RetrofitConfig().getObjImgRequestForComumUser().getObjImg("default.jpg");
                                    imgCall.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            if(response.body() != null){
                                                try {
                                                    item.setImg(response.body().bytes());
                                                    ItemSingleton.getInstance().setItemSingleton(item);
                                                    startActivity(showObject);
                                                    progressDialog.dismiss();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<Item> call, Throwable t) {
                        startActivity(objNotFound);
                        progressDialog.dismiss();
                    }
                });
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
