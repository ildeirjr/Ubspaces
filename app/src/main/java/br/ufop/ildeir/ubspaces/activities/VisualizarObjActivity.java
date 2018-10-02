package br.ufop.ildeir.ubspaces.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import br.ufop.ildeir.ubspaces.R;
import br.ufop.ildeir.ubspaces.miscellaneous.DateHandler;
import br.ufop.ildeir.ubspaces.network.RetrofitConfig;
import br.ufop.ildeir.ubspaces.objects.Item;
import br.ufop.ildeir.ubspaces.singleton.ItemSingleton;
import br.ufop.ildeir.ubspaces.singleton.UserSingleton;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VisualizarObjActivity extends AppCompatActivity {

    private TextView textNome;
    private TextView textCodigo;
    private TextView textEstado;
    private TextView textDescricao;
    private TextView textSala;
    private TextView textData;
    private TextView textBloco;
    private TextView textRecebedor;
    private TextView textNota;
    private TextView textUnidade;
    private ImageView foto;
    private Bitmap img;

    private MenuItem deleteItem;
    private MenuItem editItem;

    private ProgressDialog progressDialog;
    private ProgressBar progressBar;
    private ScrollView scrollView;

    private SimpleDateFormat simpleDateFormat;
    private SimpleDateFormat sqlDateFormat;

    private int position;

    private static int EDIT_OBJ_CODE = 1;

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
        textBloco = findViewById(R.id.textBloco);
        textSala = findViewById(R.id.textSala);
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
        position = intent.getExtras().getInt("index");


        loadObject(code, imgPath);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Carregando");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

    }

    public void loadObject(String code, final String imgPath){
        scrollView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
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
                                textBloco.setText(itemSingleton.getBloco());
                                textSala.setText(itemSingleton.getSala());
                                textData.setText(DateHandler.sqlDateToString(itemSingleton.getDataEntrada()));
                                textRecebedor.setText(itemSingleton.getRecebeu());
                                textNota.setText(itemSingleton.getNota());
                                textUnidade.setText(itemSingleton.getUnidade());
                                img = BitmapFactory.decodeByteArray(itemSingleton.getImg(),0, itemSingleton.getImg().length);
                                foto.setImageBitmap(img);
                            }
                            progressBar.setVisibility(View.GONE);
                            scrollView.setVisibility(View.VISIBLE);
                            deleteItem.setVisible(true);
                            editItem.setVisible(true);
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
                                        textBloco.setText(itemSingleton.getBloco());
                                        textSala.setText(itemSingleton.getSala());
                                        textData.setText(DateHandler.sqlDateToString(itemSingleton.getDataEntrada()));
                                        textRecebedor.setText(itemSingleton.getRecebeu());
                                        textNota.setText(itemSingleton.getNota());
                                        textUnidade.setText(itemSingleton.getUnidade());
                                        img = BitmapFactory.decodeByteArray(itemSingleton.getImg(),0, itemSingleton.getImg().length);
                                        foto.setImageBitmap(img);
                                    }
                                    progressBar.setVisibility(View.GONE);
                                    scrollView.setVisibility(View.VISIBLE);
                                    deleteItem.setVisible(true);
                                    editItem.setVisible(true);
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
        deleteItem = menu.findItem(R.id.btnDelete);
        editItem = menu.findItem(R.id.btnEdit);
        deleteItem.setVisible(false);
        editItem.setVisible(false);
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
                startActivityForResult(it, EDIT_OBJ_CODE);
                return true;
            case R.id.btnDelete:
                progressDialog.show();
                String itemCode = ItemSingleton.getInstance().getItemSingleton().getCodigo();
                String itemImgPath = ItemSingleton.getInstance().getItemSingleton().getFoto();
                String deleteUser = UserSingleton.getInstance().getNome();

                final Intent intent = new Intent();
                intent.putExtra("deleted",true);
                intent.putExtra("index", position);
                intent.putExtra("codigo", ItemSingleton.getInstance().getItemSingleton().getCodigo());

                Call<ResponseBody> call = new RetrofitConfig().deleteObjRequest().deleteObj(itemCode, itemImgPath, deleteUser);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        progressDialog.dismiss();
                        Toast.makeText(VisualizarObjActivity.this, "Objeto excluído!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
//                try {
//                    String itemCode = ItemSingleton.getInstance().getItemSingleton().getCodigo();
//                    String itemImgPath = ItemSingleton.getInstance().getItemSingleton().getFoto();
//                    String deleteUser = UserSingleton.getInstance().getNome();
//                    String result = new DeleteObjRequest().execute(itemCode,itemImgPath,deleteUser).get();
//                    if(result.equals("401")){
//                        Toast.makeText(this, R.string.invalid_operator, Toast.LENGTH_LONG).show();
//                        SessionManager.getInstance().toLoginActivity();
//                        finish();
//                    }
//                    Log.e("delete",result);
//                    finish();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == EDIT_OBJ_CODE && resultCode == RESULT_OK){
            String code = ItemSingleton.getInstance().getItemSingleton().getCodigo();
            String imgPath = ItemSingleton.getInstance().getItemSingleton().getFoto();
            loadObject(code, imgPath);
            Intent intent = new Intent();
            intent.putExtra("edited", data.getBooleanExtra("edited", true));
            intent.putExtra("index", position);
            intent.putExtra("codigo", data.getStringExtra("codigo"));
            setResult(RESULT_OK, intent);
        }
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
