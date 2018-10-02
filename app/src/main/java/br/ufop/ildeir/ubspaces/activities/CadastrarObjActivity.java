package br.ufop.ildeir.ubspaces.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

import br.ufop.ildeir.ubspaces.R;
import br.ufop.ildeir.ubspaces.miscellaneous.DBResponseCodes;
import br.ufop.ildeir.ubspaces.miscellaneous.DateHandler;
import br.ufop.ildeir.ubspaces.network.RetrofitConfig;
import br.ufop.ildeir.ubspaces.singleton.SessionManager;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CadastrarObjActivity extends AppCompatActivity {


    private TextInputLayout etCodigo;
    private TextInputLayout etNome;
    private TextInputLayout etDescricao;
    private TextInputLayout etBloco;
    private TextInputLayout etSala;
    private TextInputLayout etData;
    private TextInputLayout etRecebedor;
    private TextInputLayout etNota;
    private ImageView fotoView;
    private ArrayAdapter<String> spinnerAdapter;
    private Spinner stateSpinner, unitSpinner;
    private Calendar calendar;
    private static int IMG_GALLERY = 1;
    private static int IMG_CAMERA = 2;
    private static String[] STATE_SPINNER_OPTIONS = {"Normal","Quebrado","Consertado"};
    private static String[] UNIT_SPINNER_OPTIONS = {"Centro de Educação Aberta e a Distância (CEAD)",
                                                    "Centro Desportivo da UFOP (CEDUFOP)",
                                                    "Escola de Direito, Turismo e Museologia (EDTM)",
                                                    "Escola de Farmácia",
                                                    "Escola de Minas",
                                                    "Escola de Medicina",
                                                    "Escola de Nutrição",
                                                    "Instituto de Ciências Exatas e Aplicadas (ICEA)",
                                                    "Instituto de Ciências Exatas e Biológicas",
                                                    "Instituto de Ciências Humanas e Sociais (ICHS)",
                                                    "Instituto de Ciências Sociais Aplicadas (ICSA)",
                                                    "Instituto de Filosofia, Arte e Cultura (IFAC)"};

    Bitmap img;
    byte[] b;
    boolean imgSeted = false;

    IntentIntegrator intentIntegrator;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_obj);

        calendar = Calendar.getInstance();

        if(savedInstanceState != null){
            calendar.set(Calendar.DAY_OF_MONTH, savedInstanceState.getInt("day"));
            calendar.set(Calendar.MONTH, savedInstanceState.getInt("month"));
            calendar.set(Calendar.YEAR, savedInstanceState.getInt("year"));
        }

//        try {
//            String user = new GetUserRequest(SessionManager.getInstance().getUserId()).execute().get();
//            if(user == null){
//                Toast.makeText(this, R.string.invalid_operator, Toast.LENGTH_SHORT).show();
//                SessionManager.getInstance().toLoginActivity();
//                finish();
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Cadastrar objeto");

        etCodigo = findViewById(R.id.layoutCod);
        etNome = findViewById(R.id.layoutNome);
        etDescricao = findViewById(R.id.layoutDescricao);
        etBloco = findViewById(R.id.layoutBloco);
        etSala = findViewById(R.id.layoutSala);
        etData = findViewById(R.id.layoutData);
        etRecebedor = findViewById(R.id.layoutRecebedor);
        etNota = findViewById(R.id.layoutNota);
        fotoView = findViewById(R.id.addImg);
        stateSpinner = findViewById(R.id.stateSpinner);
        unitSpinner = findViewById(R.id.unitSpinner);

        intentIntegrator = new IntentIntegrator(this);

        etData.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            calendar.set(Calendar.YEAR, i);
                            calendar.set(Calendar.MONTH, i1);
                            calendar.set(Calendar.DAY_OF_MONTH, i2);
                            etData.getEditText().setText(DateHandler.toStringDate(calendar.getTime()));
                        }
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.show();
                }
            }
        });

        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,STATE_SPINNER_OPTIONS);
        stateSpinner.setAdapter(spinnerAdapter);

        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,UNIT_SPINNER_OPTIONS);
        unitSpinner.setAdapter(spinnerAdapter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Carregando");
        progressDialog.setCancelable(false);

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
                   !validarCampo(etSala)|
                   !validarCampo(etData)|
                   !validarCampo(etNota)|
                   !validarCampo(etRecebedor)){
                    return true;
                }else {
                    confirmAdd();
                }

        }
        return super.onOptionsItemSelected(item);
    }

    public void confirmAdd(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Confirma os dados?");
        builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                progressDialog.show();
                String sqlDate = DateHandler.toSqlDate(calendar.getTime());
                try {
                    final JSONObject jsonObject = new JSONObject();
                    jsonObject.put("codigo",etCodigo.getEditText().getText().toString());
                    jsonObject.put("nome",etNome.getEditText().getText().toString());
                    jsonObject.put("estado",stateSpinner.getSelectedItem().toString());
                    jsonObject.put("descricao",etDescricao.getEditText().getText().toString());
                    jsonObject.put("bloco", etBloco.getEditText().getText().toString());
                    jsonObject.put("sala",etSala.getEditText().getText().toString());
                    jsonObject.put("data_entrada",sqlDate);
                    jsonObject.put("recebeu",etRecebedor.getEditText().getText().toString());
                    jsonObject.put("nota",etNota.getEditText().getText().toString());
                    jsonObject.put("unidade",unitSpinner.getSelectedItem().toString());
                    jsonObject.put("nome_usuario",Integer.parseInt(SessionManager.getInstance().getUserId()));
                    Log.e("user_id",SessionManager.getInstance().getUserId());
                    Log.e("imgSeted",String.valueOf(imgSeted));
                    final JSONObject jsonImg = new JSONObject();
                    if(imgSeted){
                        jsonObject.put("foto",etNome.getEditText().getText().toString().replaceAll(" ","_")+"_"+etCodigo.getEditText().getText().toString()+".jpg");
                    }else{
                        jsonObject.put("foto","null.jpg");
                    }
                    RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
                    Call<ResponseBody> call = new RetrofitConfig().postObjDataRequest().postObjData(body);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                String result = response.body().string();
                                if(result.equals(DBResponseCodes.RESULT_OK)){
                                    if(imgSeted){
                                        jsonImg.put("nome",jsonObject.getString("foto"));
                                        jsonImg.put("img",bmptoString(img));
                                        Bitmap bmp = createImgThumb();
                                        jsonImg.put("imgThumb", Base64.encodeToString(bmpToByteArray(bmp),Base64.DEFAULT));
                                        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonImg.toString());
                                        Call<ResponseBody> imgCall = new RetrofitConfig().postObjImgRequest().postObjImg(body);
                                        imgCall.enqueue(new Callback<ResponseBody>() {
                                            @Override
                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                            }

                                            @Override
                                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                t.printStackTrace();
                                                Toast.makeText(CadastrarObjActivity.this, "Erro ao enviar a imagem.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    progressDialog.dismiss();
                                    Toast.makeText(CadastrarObjActivity.this, "Objeto cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    if(result.equals(DBResponseCodes.DUPLICATE_ENTRY)){
                                        progressDialog.dismiss();
                                        Toast.makeText(CadastrarObjActivity.this, "Este código já existe. Informe outro.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(CadastrarObjActivity.this, "Erro ao cadastrar objeto. Tente novamente.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            t.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(CadastrarObjActivity.this, "Erro ao cadastrar o objeto. Tente novamente.", Toast.LENGTH_SHORT).show();
                        }
                    });



                    //Log.e("teste","DEPOIS DO IF");
//                    String result = new PostObjDataRequest(jsonObject.toString()).execute().get();
//                    if(result.equals("401")){
//                        Toast.makeText(this, R.string.invalid_operator, Toast.LENGTH_SHORT).show();
//                        finish();
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("day", calendar.get(Calendar.DAY_OF_MONTH));
        outState.putInt("month", calendar.get(Calendar.MONTH));
        outState.putInt("year", calendar.get(Calendar.YEAR));
        super.onSaveInstanceState(outState);
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED){
            startActivityForResult(it, IMG_CAMERA);
        }

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

    public String bmptoString(Bitmap img){
        return Base64.encodeToString(bmpToByteArray(img),Base64.DEFAULT);
    }

    public byte[] bmpToByteArray(Bitmap img){
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

    public Bitmap createImgThumb(){
        return Bitmap.createScaledBitmap(img,(int) (img.getWidth()*0.1),(int) (img.getHeight()*0.1),true);
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
        intentIntegrator.setCaptureActivity(ScanActivity.class);
        intentIntegrator.initiateScan();
    }



}
