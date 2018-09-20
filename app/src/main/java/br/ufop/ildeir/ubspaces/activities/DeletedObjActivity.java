package br.ufop.ildeir.ubspaces.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.ufop.ildeir.ubspaces.R;
import br.ufop.ildeir.ubspaces.network.RetrofitConfig;
import br.ufop.ildeir.ubspaces.objects.Item;
import br.ufop.ildeir.ubspaces.singleton.ItemSingleton;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeletedObjActivity extends AppCompatActivity {

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
    private TextView textUsrExclusao;
    private TextView textDataExclusao;
    private Bitmap img;

    private ScrollView scrollView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deleted_obj);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        scrollView = findViewById(R.id.nestedScrollView);
        progressBar = findViewById(R.id.progress_bar);

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
        textUsrExclusao = findViewById(R.id.textUsrExclusao);
        textDataExclusao = findViewById(R.id.textDataExclusao);

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
                            if(itemSingleton != null) {
                                textNome.setText(itemSingleton.getNome());
                                textCodigo.setText(itemSingleton.getCodigo());
                                textEstado.setText(itemSingleton.getEstado());
                                textDescricao.setText(itemSingleton.getDescricao());
                                textLocal.setText(itemSingleton.getLocal());
                                textDepto.setText(itemSingleton.getDepto());
                                textData.setText(itemSingleton.getDataEntrada());
                                textRecebedor.setText(itemSingleton.getRecebeu());
                                textNota.setText(itemSingleton.getNota());
                                textUnidade.setText(itemSingleton.getUnidade());
                                img = BitmapFactory.decodeByteArray(itemSingleton.getImg(), 0, itemSingleton.getImg().length);
                                foto.setImageBitmap(img);
                                textUsrExclusao.setText(itemSingleton.getNomeUsrExclusao());

                                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
                                Date date = null;
                                try {
                                    date = inputFormat.parse(itemSingleton.getDataExclusao());
                                    textDataExclusao.setText(outputFormat.format(date));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                progressBar.setVisibility(View.GONE);
                                scrollView.setVisibility(View.VISIBLE);
                            }

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
                                    if(itemSingleton != null) {
                                        textNome.setText(itemSingleton.getNome());
                                        textCodigo.setText(itemSingleton.getCodigo());
                                        textEstado.setText(itemSingleton.getEstado());
                                        textDescricao.setText(itemSingleton.getDescricao());
                                        textLocal.setText(itemSingleton.getLocal());
                                        textDepto.setText(itemSingleton.getDepto());
                                        textData.setText(itemSingleton.getDataEntrada());
                                        textRecebedor.setText(itemSingleton.getRecebeu());
                                        textNota.setText(itemSingleton.getNota());
                                        textUnidade.setText(itemSingleton.getUnidade());
                                        img = BitmapFactory.decodeByteArray(itemSingleton.getImg(), 0, itemSingleton.getImg().length);
                                        foto.setImageBitmap(img);
                                        textUsrExclusao.setText(itemSingleton.getNomeUsrExclusao());

                                        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
                                        Date date = null;
                                        try {
                                            date = inputFormat.parse(itemSingleton.getDataExclusao());
                                            textDataExclusao.setText(outputFormat.format(date));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        progressBar.setVisibility(View.GONE);
                                        scrollView.setVisibility(View.VISIBLE);
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

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
