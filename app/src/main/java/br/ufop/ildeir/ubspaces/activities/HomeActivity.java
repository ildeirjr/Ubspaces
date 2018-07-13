package br.ufop.ildeir.ubspaces.activities;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.concurrent.ExecutionException;

import br.ufop.ildeir.ubspaces.R;
import br.ufop.ildeir.ubspaces.objects.Item;
import br.ufop.ildeir.ubspaces.requests.ClearUserTokenRequest;
import br.ufop.ildeir.ubspaces.requests.get.GetObjDataRequest;
import br.ufop.ildeir.ubspaces.requests.get.GetObjImgRequest;
import br.ufop.ildeir.ubspaces.requests.get.GetUserRequest;
import br.ufop.ildeir.ubspaces.singleton.SessionManager;
import br.ufop.ildeir.ubspaces.singleton.ItemSingleton;
import br.ufop.ildeir.ubspaces.singleton.UserSingleton;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout dl;
    private ActionBarDrawerToggle dToggle;

    private TextView user;
    private TextView email;

    private IntentIntegrator intentIntegrator;

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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intentIntegrator = new IntentIntegrator(this);

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
        startActivity(new Intent(this,ListObjActivity.class));
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
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                intentIntegrator.setBeepEnabled(false);
                intentIntegrator.initiateScan();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent showObject = new Intent(this,VisualizarObjActivity.class);
        Intent objNotFound = new Intent(this,ObjNotFoundActivity.class);
        showObject.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(intentResult != null){
            if(intentResult.getContents() != null){
                try {
                    Item item = new GetObjDataRequest(intentResult.getContents(),this).execute().get();
                    if(item != null){
                        item.setImg(new GetObjImgRequest(item.getFoto()).execute().get());
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

    @Override
    protected void onResume() {
        if(dl.isDrawerOpen(GravityCompat.START)){
            dl.closeDrawer(GravityCompat.START);
        }
        super.onResume();
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
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
