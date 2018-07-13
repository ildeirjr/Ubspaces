package br.ufop.ildeir.ubspaces.activities;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import br.ufop.ildeir.ubspaces.miscellaneous.DateDialog;
import br.ufop.ildeir.ubspaces.R;
import br.ufop.ildeir.ubspaces.requests.get.GetUserRequest;
import br.ufop.ildeir.ubspaces.requests.post.PostObjDataRequest;
import br.ufop.ildeir.ubspaces.requests.post.PostObjImgRequest;
import br.ufop.ildeir.ubspaces.singleton.SessionManager;


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
    private static int IMG_GALLERY = 1;
    private static int IMG_CAMERA = 2;
    Bitmap img;
    byte[] b;
    boolean imgSeted = false;

    IntentIntegrator intentIntegrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_obj);

        try {
            String user = new GetUserRequest(SessionManager.getInstance().getUserId()).execute().get();
            if(user == null){
                Toast.makeText(this, R.string.invalid_operator, Toast.LENGTH_SHORT).show();
                SessionManager.getInstance().toLoginActivity();
                finish();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

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

        intentIntegrator = new IntentIntegrator(this);

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
                            createImgThumb();
                            jsonImg.put("imgThumb", Base64.encodeToString(bmpToByteArray(),Base64.DEFAULT));
                            String result = new PostObjImgRequest(jsonImg.toString(),this).execute().get();
                            if(result.equals("401")){
                                Toast.makeText(this, R.string.invalid_operator, Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            //Log.e("result",result);
                        }else{
                            jsonObject.put("foto","null.jpg");
                        }
                        //Log.e("teste","DEPOIS DO IF");
                        String result = new PostObjDataRequest(jsonObject.toString()).execute().get();
                        if(result.equals("401")){
                            Toast.makeText(this, R.string.invalid_operator, Toast.LENGTH_SHORT).show();
                            finish();
                        }
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
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Selecione uma opção");
        String[] pictureDialogItems = {"Galeria","Camera"};
        pictureDialog.setCancelable(true);
        pictureDialog.setItems(pictureDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i){
                    case 0:
                        imageFromGallery();
                        break;
                    case 1:
                        imageFromCamera();
                        break;
                }
            }
        });
        pictureDialog.show();
    }

    public void imageFromGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMG_GALLERY);
    }

    public void imageFromCamera(){
        Intent it = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(it, IMG_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== IMG_GALLERY){
            if(resultCode==RESULT_OK && data!=null) {
                Uri path = data.getData();
                try {
                    imgSeted = true;
                    img = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                    fotoView.setImageBitmap(img);
                    fotoView.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else if(requestCode == IMG_CAMERA) {
            if(resultCode==RESULT_OK && data != null) {
                Log.e("camera", "IF CAMERA");
                imgSeted = true;
                img = (Bitmap) data.getExtras().get("data");
                fotoView.setImageBitmap(img);
                fotoView.setVisibility(View.VISIBLE);
            }
        }else{
            IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if(intentResult != null){
                String scannedCode = intentResult.getContents();
                if(scannedCode != null){
                    etCodigo.getEditText().setText(scannedCode);
                }
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

    public void createImgThumb(){
        img = Bitmap.createScaledBitmap(img,(int) (img.getWidth()*0.1),(int) (img.getHeight()*0.1),true);
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

    public void code_scan(View view) {
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        intentIntegrator.setBeepEnabled(false);
        intentIntegrator.initiateScan();
    }



}
