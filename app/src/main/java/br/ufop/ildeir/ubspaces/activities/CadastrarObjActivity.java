package br.ufop.ildeir.ubspaces.activities;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import br.ufop.ildeir.ubspaces.miscellaneous.DateDialog;
import br.ufop.ildeir.ubspaces.R;
import br.ufop.ildeir.ubspaces.requests.PostObjDataRequest;
import br.ufop.ildeir.ubspaces.requests.PostObjImgRequest;


public class CadastrarObjActivity extends AppCompatActivity {


    private TextInputLayout etCodigo;
    private TextInputLayout etNome;
    private TextInputLayout etDescricao;
    private TextInputLayout etLocal;
    private TextInputLayout etDepto;
    private TextInputLayout etData;
    private TextInputLayout etRecebedor;
    private TextInputLayout etNota;
    private ImageView fotoView;
    private int dia,mes,ano;
    private DateDialog dateDialog;
    private static int IMG_REQUEST = 1;
    Bitmap img;
    byte[] b;
    boolean imgSeted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_obj);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Cadastrar objeto");

        etCodigo = findViewById(R.id.layoutCod);
        etNome = findViewById(R.id.layoutNome);
        etDescricao = findViewById(R.id.layoutDescricao);
        etLocal = findViewById(R.id.layoutLocal);
        etDepto = findViewById(R.id.layoutDepto);
        etData = findViewById(R.id.layoutData);
        etRecebedor = findViewById(R.id.layoutRecebedor);
        etNota = findViewById(R.id.layoutNota);
        fotoView = findViewById(R.id.addImg);

        etData.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    dateDialog = new DateDialog(view);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    dateDialog.show(ft,"DatePicker");
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cadastrar_obj,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.btnConfirm:
                if(!validarCampo(etCodigo)|
                   !validarCampo(etNome)|
                   !validarCampo(etLocal)|
                   !validarCampo(etData)|
                   !validarCampo(etDepto)|
                   !validarCampo(etNota)|
                   !validarCampo(etRecebedor)){
                    return true;
                }else {
                    dia = dateDialog.getDia();
                    mes = dateDialog.getMes();
                    ano = dateDialog.getAno();
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("codigo",etCodigo.getEditText().getText().toString());
                        jsonObject.put("nome",etNome.getEditText().getText().toString());
                        jsonObject.put("descricao",etDescricao.getEditText().getText().toString());
                        jsonObject.put("local",etLocal.getEditText().getText().toString());
                        jsonObject.put("depto",etDepto.getEditText().getText().toString());
                        jsonObject.put("dia",dia);
                        jsonObject.put("mes",mes);
                        jsonObject.put("ano",ano);
                        jsonObject.put("recebeu",etRecebedor.getEditText().getText().toString());
                        jsonObject.put("nota",etNota.getEditText().getText().toString());
                        Log.e("imgSeted",String.valueOf(imgSeted));
                        if(imgSeted){
                            JSONObject jsonImg = new JSONObject();
                            jsonObject.put("foto",etNome.getEditText().getText().toString().replaceAll(" ","_")+"_"+etCodigo.getEditText().getText().toString()+".jpg");
                            //Log.e("imagem_nome",jsonObject.getString("foto"));
                            jsonImg.put("nome",jsonObject.getString("foto"));
                            jsonImg.put("img",bmptoString());
                            String result = new PostObjImgRequest(jsonImg.toString()).execute().get();
                            //Log.e("result",result);
                        }else{
                            jsonObject.put("foto","null.jpg");
                        }
                        //Log.e("teste","DEPOIS DO IF");
                        new PostObjDataRequest(jsonObject.toString()).execute();
                        Snackbar.make(findViewById(R.id.btnConfirm),"Objeto cadastrado com sucesso",Snackbar.LENGTH_LONG).show();
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                }

        }
        return super.onOptionsItemSelected(item);
    }

    public void escolherImagem(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMG_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMG_REQUEST && resultCode==RESULT_OK && data!=null){
            Uri path = data.getData();
            try {
                imgSeted = true;
                img = MediaStore.Images.Media.getBitmap(getContentResolver(),path);
                fotoView.setImageBitmap(img);
                fotoView.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String bmptoString(){
        return Base64.encodeToString(bmpToByteArray(),Base64.DEFAULT);
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

    public boolean validarCampo(TextInputLayout til){
        String str = til.getEditText().getText().toString();
        if(str.isEmpty()){
            til.setError("Este campo é obrigatório");
            return false;
        }else{
            til.setError(null);
            return true;
        }
    }
}
