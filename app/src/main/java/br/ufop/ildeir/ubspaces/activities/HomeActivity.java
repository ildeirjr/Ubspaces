package br.ufop.ildeir.ubspaces.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import br.ufop.ildeir.ubspaces.R;
import br.ufop.ildeir.ubspaces.network.RetrofitConfig;
import br.ufop.ildeir.ubspaces.objects.Item;
import br.ufop.ildeir.ubspaces.requests.ClearUserTokenRequest;
import br.ufop.ildeir.ubspaces.requests.get.GetMetadataRequest;
import br.ufop.ildeir.ubspaces.requests.get.GetObjDataRequest;
import br.ufop.ildeir.ubspaces.requests.get.GetObjImgRequest;
import br.ufop.ildeir.ubspaces.requests.get.GetUserRequest;
import br.ufop.ildeir.ubspaces.singleton.SessionManager;
import br.ufop.ildeir.ubspaces.singleton.ItemSingleton;
import br.ufop.ildeir.ubspaces.singleton.UserSingleton;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout dl;
    private ActionBarDrawerToggle dToggle;

    private TextView user;
    private TextView email;
    private TextView userObjNum;
    private TextView totalObjNum;
    private TextView monthObjNum;

    private IntentIntegrator intentIntegrator;

    private ProgressBar progressBar;
    private LinearLayout linearLayout;

    private ProgressDialog progressDialog;

    private static int SCAN_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("create","CREATE");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dl = findViewById(R.id.drawerLayout);
        dToggle = new ActionBarDrawerToggle(this, dl, R.string.open, R.string.close);

        dl.addDrawerListener(dToggle);
        dToggle.syncState();

        NavigationView navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);

        user = navigationView.getHeaderView(0).findViewById(R.id.textUser);
        email = navigationView.getHeaderView(0).findViewById(R.id.textEmail);
        UserSingleton.getInstance();
        user.setText(UserSingleton.getInstance().getNome());
        email.setText(UserSingleton.getInstance().getEmail());

        userObjNum = findViewById(R.id.userObjNum);
        totalObjNum = findViewById(R.id.totalObjNum);
        monthObjNum = findViewById(R.id.monthObjNum);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intentIntegrator = new IntentIntegrator(this);

        progressBar = findViewById(R.id.progress_bar);
        linearLayout = findViewById(R.id.card_layout);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Carregando");
        progressDialog.setCancelable(false);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (dToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void cadastra() {
        startActivity(new Intent(this,CadastrarObjActivity.class));
    }

    public void lista(){
        Intent it = new Intent(this,ListObjActivity.class);
        it.putExtra("totalObjNum",totalObjNum.getText());
        startActivity(it);
    }

    public void listaExcluidos(){
        Intent it = new Intent(this,DeletedObjListActivity.class);
        it.putExtra("totalObjNum",totalObjNum.getText());
        startActivity(it);
    }

    public void exibeSobre(){
        startActivity(new Intent(this, AboutActivity.class));
    }


    public void logOut() {
        try {
            UserSingleton.getInstance().setNull();
            new ClearUserTokenRequest().execute().get();
            SessionManager.getInstance().logoutUser();
            finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void scan(View view) {
        try {
            String user = new GetUserRequest(SessionManager.getInstance().getUserId()).execute().get();
            if(user.equals("401")){
                Toast.makeText(this, R.string.invalid_operator, Toast.LENGTH_SHORT).show();
                SessionManager.getInstance().toLoginActivity();
                finish();
            } else {
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                intentIntegrator.setBeepEnabled(false);
                intentIntegrator.setCaptureActivity(ScanActivity.class);
                intentIntegrator.initiateScan();
//                Intent it = new Intent(this, ScanActivity.class);
//                startActivityForResult(it, SCAN_REQUEST_CODE);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final Intent showObject = new Intent(this,VisualizarObjActivity.class);
        final Intent objNotFound = new Intent(this,ObjNotFoundActivity.class);
        final Intent showDeletedObject = new Intent(this, DeletedObjActivity.class);
        showObject.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(intentResult != null){
            if(intentResult.getContents() != null){
                progressDialog.show();
                Call<Item> call = new RetrofitConfig().getObjDataRequest().getObjData(intentResult.getContents());
                call.enqueue(new Callback<Item>() {
                    @Override
                    public void onResponse(Call<Item> call, Response<Item> response) {
                        final Item item = response.body();
                        Bundle bundle = new Bundle();
                        bundle.putString("codigo", item.getCodigo());
                        bundle.putString("foto", item.getFoto());
                        showObject.putExtras(bundle);
                        showDeletedObject.putExtras(bundle);
                        Log.e("teste","nao é null");
                        Call<ResponseBody> imgCall = new RetrofitConfig().getObjImgRequestForComumUser().getObjImg(item.getFoto());
                        imgCall.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if(response.body() != null){
                                    try {
                                        item.setImg(response.body().bytes());
                                        ItemSingleton.getInstance().setItemSingleton(item);
                                        if(item.getEstado().equals("Excluido")){
                                            startActivity(showDeletedObject);
                                        } else {
                                            startActivity(showObject);
                                        }
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
                                                    if(item.getEstado().equals("Excluido")){
                                                        startActivity(showDeletedObject);
                                                    } else {
                                                        startActivity(showObject);
                                                    }
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

    @Override
    protected void onResume() {
        if(dl.isDrawerOpen(GravityCompat.START)){
            dl.closeDrawer(GravityCompat.START);
        }
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        linearLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        String userId = SessionManager.getInstance().getUserId();
        Call<JsonObject> call = new RetrofitConfig().getMetadataRequest().getMetadata(userId);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject object = response.body();
                userObjNum.setText(object.get("num_obj_user").getAsString());
                totalObjNum.setText(object.get("num_obj").getAsString());
                monthObjNum.setText(object.get("num_obj_month").getAsString());
                progressBar.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case R.id.cadastrar:
                cadastra();
                break;
            case R.id.listar:
                lista();
                break;
            case R.id.sair:
                logOut();
                break;
            case R.id.listarExcluidos:
                listaExcluidos();
                break;
            case R.id.sobre:
                exibeSobre();
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
