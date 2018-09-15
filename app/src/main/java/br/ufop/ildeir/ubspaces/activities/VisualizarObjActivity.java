package br.ufop.ildeir.ubspaces.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutionException;

import br.ufop.ildeir.ubspaces.R;
import br.ufop.ildeir.ubspaces.miscellaneous.DateHandler;
import br.ufop.ildeir.ubspaces.network.RetrofitConfig;
import br.ufop.ildeir.ubspaces.objects.Item;
import br.ufop.ildeir.ubspaces.requests.delete.DeleteObjRequest;
import br.ufop.ildeir.ubspaces.requests.get.GetObjImgRequest;
import br.ufop.ildeir.ubspaces.requests.get.GetUserRequest;
import br.ufop.ildeir.ubspaces.singleton.ItemSingleton;
import br.ufop.ildeir.ubspaces.singleton.SessionManager;
import br.ufop.ildeir.ubspaces.singleton.UserSingleton;
import br.ufop.ildeir.ubspaces.utils.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VisualizarObjActivity extends AppCompatActivity {

    private TextView textNome;
    private TextView textCodigo;
    private TextView textEstado;
    private TextView textDescricao;
    private TextView textDepto;
    private TextView textData;
    private TextView textLocal;
    private TextView textRecebedor;
    private TextView textNota;
    private TextView textUnidade;
    private ImageView foto;
    private Bitmap img;

    private ProgressBar progressBar;
    private ScrollView scrollView;

    private SimpleDateFormat simpleDateFormat;
    private SimpleDateFormat sqlDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_obj);

//        try {
//            String user = new GetUserRequest(SessionManager.getInstance().getUserId()).execute().get();
//            if(user == null){
//                Toast.makeText(this, R.string.invalid_operator, Toast.LENGTH_LONG).show();
//                SessionManager.getInstance().toLoginActivity();
//                finish();
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        textNome = findViewById(R.id.textNome);
        textCodigo = findViewById(R.id.textCodigo);
        textEstado = findViewById(R.id.textEstado);
        textDescricao = findViewById(R.id.textDescricao);
        textLocal = findViewById(R.id.textLocal);
        textDepto = findViewById(R.id.textDepto);
        textData = findViewById(R.id.textData);
        textRecebedor = findViewById(R.id.textRecebedor);
        textNota = findViewById(R.id.textNota);
        textUnidade = findViewById(R.id.textUnidade);
        foto = findViewById(R.id.imgView);

        progressBar = findViewById(R.id.progress_bar);
        scrollView = findViewById(R.id.nestedScrollView);

        Intent intent = getIntent();
        String code = intent.getExtras().getString("codigo");
        String imgPath = intent.getExtras().getString("foto");

        scrollView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        loadObject(code, imgPath);

    }

    public void loadObject(String code, final String imgPath){
        Call<Item> call = new RetrofitConfig().getObjDataRequest().getObjData(code);
        System.out.println(call.request().url().toString());
        call.enqueue(new Callback<Item>() {
            @Override
            public void onResponse(Call<Item> call, Response<Item> response) {
                final Item item = response.body();
                Call<ResponseBody> imgCall = new RetrofitConfig().getObjImgRequest().getObjImg(imgPath);
                imgCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.body() != null){
                            try {
                                item.setImg(response.body().bytes());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            ItemSingleton.getInstance().setItemSingleton(item);
                            Item itemSingleton = ItemSingleton.getInstance().getItemSingleton();
                            if(itemSingleton != null){
                                textNome.setText(itemSingleton.getNome());
                                textCodigo.setText(itemSingleton.getCodigo());
                                textEstado.setText(itemSingleton.getEstado());
                                textDescricao.setText(itemSingleton.getDescricao());
                                textLocal.setText(itemSingleton.getLocal());
                                textDepto.setText(itemSingleton.getDepto());
                                textData.setText(DateHandler.sqlDateToString(itemSingleton.getDataEntrada()));
                                textRecebedor.setText(itemSingleton.getRecebeu());
                                textNota.setText(itemSingleton.getNota());
                                textUnidade.setText(itemSingleton.getUnidade());
                                img = BitmapFactory.decodeByteArray(itemSingleton.getImg(),0, itemSingleton.getImg().length);
                                foto.setImageBitmap(img);
                            }
                            progressBar.setVisibility(View.GONE);
                            scrollView.setVisibility(View.VISIBLE);

                        } else {
                            Call<ResponseBody> imgCall = new RetrofitConfig().getObjImgRequest().getObjImg("default.jpg");
                            imgCall.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    try {
                                        item.setImg(response.body().bytes());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    ItemSingleton.getInstance().setItemSingleton(item);
                                    Item itemSingleton = ItemSingleton.getInstance().getItemSingleton();
                                    if(itemSingleton != null){
                                        textNome.setText(itemSingleton.getNome());
                                        textCodigo.setText(itemSingleton.getCodigo());
                                        textEstado.setText(itemSingleton.getEstado());
                                        textDescricao.setText(itemSingleton.getDescricao());
                                        textLocal.setText(itemSingleton.getLocal());
                                        textDepto.setText(itemSingleton.getDepto());
                                        textData.setText(DateHandler.sqlDateToString(itemSingleton.getDataEntrada()));
                                        textRecebedor.setText(itemSingleton.getRecebeu());
                                        textNota.setText(itemSingleton.getNota());
                                        textUnidade.setText(itemSingleton.getUnidade());
                                        img = BitmapFactory.decodeByteArray(itemSingleton.getImg(),0, itemSingleton.getImg().length);
                                        foto.setImageBitmap(img);
                                    }
                                    progressBar.setVisibility(View.GONE);
                                    scrollView.setVisibility(View.VISIBLE);
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

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_visualizar_obj,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
            case R.id.btnEdit:
                Intent it = new Intent(this, EditarObjActivity.class);
                it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(it);
                finish();
                return true;
            case R.id.btnDelete:
                try {
                    String itemCode = ItemSingleton.getInstance().getItemSingleton().getCodigo();
                    String itemImgPath = ItemSingleton.getInstance().getItemSingleton().getFoto();
                    String deleteUser = UserSingleton.getInstance().getNome();
                    String result = new DeleteObjRequest().execute(itemCode,itemImgPath,deleteUser).get();
                    if(result.equals("401")){
                        Toast.makeText(this, R.string.invalid_operator, Toast.LENGTH_LONG).show();
                        SessionManager.getInstance().toLoginActivity();
                        finish();
                    }
                    Log.e("delete",result);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public byte[] bmpToByteArray(){
        if(img == null){
            img = BitmapFactory.decodeResource(getResources(),R.drawable.no_foto);
        }
        ByteArrayOutputStream b_stream = new ByteArrayOutputStream();
        if(img.getHeight() > 2000){
            img = Bitmap.createScaledBitmap(img,(int)(img.getWidth()*0.25),(int)(img.getHeight()*0.25),true);
        }else{
            if(img.getHeight() > 1000){
                img = Bitmap.createScaledBitmap(img,(int)(img.getWidth()*0.5),(int)(img.getHeight()*0.5),true);
            }
        }
        img.compress(Bitmap.CompressFormat.JPEG,50,b_stream);
        return b_stream.toByteArray();
    }

}
