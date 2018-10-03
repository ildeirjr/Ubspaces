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
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
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

import com.bumptech.glide.Glide;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import br.ufop.ildeir.ubspaces.R;
import br.ufop.ildeir.ubspaces.miscellaneous.DBResponseCodes;
import br.ufop.ildeir.ubspaces.miscellaneous.DateHandler;
import br.ufop.ildeir.ubspaces.network.RetrofitConfig;
import br.ufop.ildeir.ubspaces.objects.Item;
import br.ufop.ildeir.ubspaces.singleton.ItemSingleton;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EditarObjActivity extends AppCompatActivity {


    private TextInputLayout etCodigo;
    private TextInputLayout etNome;
    private TextInputLayout etDescricao;
    private TextInputLayout etBloco;
    private TextInputLayout etSala;
    private TextInputLayout etData;
    private TextInputLayout etRecebedor;
    private TextInputLayout etNota;
    private ImageView fotoView;
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
    private boolean flagDateDialogOpened = false;
    private String codigoAntigo, fotoAntigo;
    private Item itemSingleton;
    private boolean imgSeted;

    private IntentIntegrator intentIntegrator;

    private ProgressDialog progressDialog;

    private String imageFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_obj);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Editar objeto");

        calendar = Calendar.getInstance();

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

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, STATE_SPINNER_OPTIONS);
        stateSpinner.setAdapter(spinnerAdapter);

        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, UNIT_SPINNER_OPTIONS);
        unitSpinner.setAdapter(spinnerAdapter);

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
                    flagDateDialogOpened = true;
                }
            }
        });

        imgSeted = false;

//        Intent it = getIntent();
//        Bundle bundle = it.getExtras();
//        if(bundle != null){
//            etCodigo.getEditText().setText(bundle.getString("cod"));
//            etNome.getEditText().setText(bundle.getString("nome"));
//            etDescricao.getEditText().setText(bundle.getString("descricao"));
//            etBloco.getEditText().setText(bundle.getString("local"));
//            etSala.getEditText().setText(bundle.getString("depto"));
//            etData.getEditText().setText(bundle.getString("data"));
//            etRecebedor.getEditText().setText(bundle.getString("recebedor"));
//            etNota.getEditText().setText(bundle.getString("nota"));
//            img = BitmapFactory.decodeByteArray(bundle.getByteArray("img"),0,bundle.getByteArray("img").length);
//            fotoView.setImageBitmap(img);
//            dia = Integer.parseInt(splitDate(bundle.getString("data"))[0]);
//            mes = Integer.parseInt(splitDate(bundle.getString("data"))[1]);
//            ano = Integer.parseInt(splitDate(bundle.getString("data"))[2]);
//            codigoAntigo = bundle.getString("cod");
//        }

        itemSingleton = ItemSingleton.getInstance().getItemSingleton();
        if(itemSingleton != null){
            calendar.setTime(DateHandler.sqlToDate(itemSingleton.getDataEntrada()));
            etCodigo.getEditText().setText(itemSingleton.getCodigo());
            etNome.getEditText().setText(itemSingleton.getNome());
            etDescricao.getEditText().setText(itemSingleton.getDescricao());
            etBloco.getEditText().setText(itemSingleton.getBloco());
            etSala.getEditText().setText(itemSingleton.getSala());
            etData.getEditText().setText(DateHandler.toStringDate(calendar.getTime()));
            etRecebedor.getEditText().setText(itemSingleton.getRecebeu());
            etNota.getEditText().setText(itemSingleton.getNota());
            img = BitmapFactory.decodeByteArray(itemSingleton.getImg(),0,itemSingleton.getImg().length);
            fotoView.setImageBitmap(img);
            codigoAntigo = itemSingleton.getCodigo();
            fotoAntigo = itemSingleton.getFoto();

            for(int i=0 ; i<STATE_SPINNER_OPTIONS.length ; i++){
                if(itemSingleton.getEstado().equals(STATE_SPINNER_OPTIONS[i])){
                    stateSpinner.setSelection(i);
                    break;
                }
            }

//            if(itemSingleton.getEstado().equals(STATE_SPINNER_OPTIONS[0])){
//                stateSpinner.setSelection(0);
//            } else if(itemSingleton.getEstado().equals(STATE_SPINNER_OPTIONS[1])){
//                stateSpinner.setSelection(1);
//            } else if(itemSingleton.getEstado().equals(STATE_SPINNER_OPTIONS[2])){
//                stateSpinner.setSelection(2);
//            } else if(itemSingleton.getEstado().equals(STATE_SPINNER_OPTIONS[3])){
//                stateSpinner.setSelection(3);
//            }

            if(itemSingleton.getUnidade().equals(UNIT_SPINNER_OPTIONS[0])){
                unitSpinner.setSelection(0);
            } else if(itemSingleton.getUnidade().equals(UNIT_SPINNER_OPTIONS[1])) {
                unitSpinner.setSelection(1);
            } else if(itemSingleton.getUnidade().equals(UNIT_SPINNER_OPTIONS[2])) {
                unitSpinner.setSelection(2);
            } else if(itemSingleton.getUnidade().equals(UNIT_SPINNER_OPTIONS[3])) {
                unitSpinner.setSelection(3);
            } else if(itemSingleton.getUnidade().equals(UNIT_SPINNER_OPTIONS[4])) {
                unitSpinner.setSelection(4);
            } else if(itemSingleton.getUnidade().equals(UNIT_SPINNER_OPTIONS[5])) {
                unitSpinner.setSelection(5);
            } else if(itemSingleton.getUnidade().equals(UNIT_SPINNER_OPTIONS[6])) {
                unitSpinner.setSelection(6);
            } else if(itemSingleton.getUnidade().equals(UNIT_SPINNER_OPTIONS[7])) {
                unitSpinner.setSelection(7);
            } else if(itemSingleton.getUnidade().equals(UNIT_SPINNER_OPTIONS[8])) {
                unitSpinner.setSelection(8);
            } else if(itemSingleton.getUnidade().equals(UNIT_SPINNER_OPTIONS[9])) {
                unitSpinner.setSelection(9);
            } else if(itemSingleton.getUnidade().equals(UNIT_SPINNER_OPTIONS[10])) {
                unitSpinner.setSelection(10);
            } else if(itemSingleton.getUnidade().equals(UNIT_SPINNER_OPTIONS[11])) {
                unitSpinner.setSelection(11);
            }

        }

        intentIntegrator = new IntentIntegrator(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Carregando");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
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
                    showConfirmDialog();
                }

        }
        return super.onOptionsItemSelected(item);
    }

    public void showConfirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Confirma as alterações?");
        builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                progressDialog.show();
                final JSONObject jsonImg = new JSONObject();
                String sqlDate = DateHandler.toSqlDate(calendar.getTime());
                Log.e("data",sqlDate);
                itemSingleton.setCodigo(etCodigo.getEditText().getText().toString());
                itemSingleton.setNome(etNome.getEditText().getText().toString());
                itemSingleton.setEstado(stateSpinner.getSelectedItem().toString());
                itemSingleton.setDescricao(etDescricao.getEditText().getText().toString());
                itemSingleton.setBloco(etBloco.getEditText().getText().toString());
                itemSingleton.setSala(etSala.getEditText().getText().toString());
                itemSingleton.setDataEntrada(sqlDate);
                itemSingleton.setRecebeu(etRecebedor.getEditText().getText().toString());
                itemSingleton.setNota(etNota.getEditText().getText().toString());
                itemSingleton.setUnidade(unitSpinner.getSelectedItem().toString());
                if(!itemSingleton.getFoto().equals("null.jpg")){
                    itemSingleton.setFoto(etNome.getEditText().getText().toString().replaceAll(" ", "_") + "_" + etCodigo.getEditText().getText().toString() + ".jpg");
                    try {
                        jsonImg.put("nome", itemSingleton.getFoto());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if(imgSeted){
                    try {
                        itemSingleton.setFoto(etNome.getEditText().getText().toString().replaceAll(" ", "_") + "_" + etCodigo.getEditText().getText().toString() + ".jpg");
                        itemSingleton.setImg(bmpToByteArray(img));
                        jsonImg.put("nome", itemSingleton.getFoto());
                        jsonImg.put("img", bmptoString(img));
                        Bitmap bmp = createImgThumb();
                        jsonImg.put("imgThumb", Base64.encodeToString(bmpToByteArray(bmp), Base64.DEFAULT));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    JSONObject jsonObject = itemSingleton.ItemtoJSON();
                    jsonObject.put("codigoAntigo",codigoAntigo);
                    jsonObject.put("fotoAntigo",fotoAntigo);
                    if(!fotoAntigo.equals("null.jpg") && !imgSeted){
                        jsonObject.put("imgRename","true");
                    } else {
                        jsonObject.put("imgRename","false");
                    }
                    if(imgSeted && !fotoAntigo.equals("null.jpg")){
                        jsonObject.put("imgDelete","true");
                    } else {
                        jsonObject.put("imgDelete","false");
                    }
                    final Intent it = new Intent(getApplicationContext(), VisualizarObjActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("codigo",itemSingleton.getCodigo());
                    bundle.putString("foto",itemSingleton.getFoto());
                    it.putExtras(bundle);
//                        System.out.println(jsonObject.toString());
//                        String result = new EditObjDataRequest(jsonObject.toString()).execute().get();
//                        if(result.equals("401")){
//                            Toast.makeText(this, R.string.invalid_operator, Toast.LENGTH_LONG).show();
//                            finish();
//                        }
                    RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
                    Call<ResponseBody> call = new RetrofitConfig().editObjDataRequest().editObjData(body);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                String result = response.body().string();
                                Log.e("result",result);
                                if (result.equals(DBResponseCodes.RESULT_OK)) {
                                    if (imgSeted) {
//                                    new PostObjImgRequest(jsonImg.toString(),this).execute();
                                        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonImg.toString());
                                        Call<ResponseBody> imgCall = new RetrofitConfig().postObjImgRequest().postObjImg(body);
                                        imgCall.enqueue(new Callback<ResponseBody>() {
                                            @Override
                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                progressDialog.dismiss();
                                                Toast.makeText(EditarObjActivity.this, "Alterações realizadas com sucesso!", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent();
                                                intent.putExtra("edited", true);
                                                intent.putExtra("codigo", codigoAntigo);
                                                setResult(RESULT_OK, intent);
                                                finish();
                                            }

                                            @Override
                                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                t.printStackTrace();
                                            }
                                        });
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(EditarObjActivity.this, "Alterações realizadas com sucesso!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent();
                                        intent.putExtra("edited", true);
                                        intent.putExtra("codigo", codigoAntigo);
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    }
                                } else if(result.equals(DBResponseCodes.DUPLICATE_ENTRY)){
                                    progressDialog.dismiss();
                                    Toast.makeText(EditarObjActivity.this, "Este código já existe. Informe outro.", Toast.LENGTH_SHORT).show();
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(EditarObjActivity.this, "Erro ao editar objeto. Tente novamente.", Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                    Bundle bundle = new Bundle();
//                    bundle.putString("cod", etCodigo.getEditText().getText().toString());
//                    bundle.putString("nome", etNome.getEditText().getText().toString());
//                    bundle.putString("descricao", etDescricao.getEditText().getText().toString());
//                    bundle.putString("local", etBloco.getEditText().getText().toString());
//                    bundle.putString("depto", etSala.getEditText().getText().toString());
//                    bundle.putString("dia", String.valueOf(dia));
//                    bundle.putString("mes", String.valueOf(mes));
//                    bundle.putString("ano", String.valueOf(ano));
//                    bundle.putString("recebedor", etRecebedor.getEditText().getText().toString());
//                    bundle.putString("nota", etNota.getEditText().getText().toString());
//                    bundle.putByteArray("img", bmpToByteArray());
//                    it.putExtras(bundle);
//                    startActivity(it);
//                    finish();
            }
        });
        builder.setNegativeButton("NÃO", null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    public void imageFromGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMG_GALLERY);
    }

    public void imageFromCamera(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            Intent pictureIntent = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE);
            if(pictureIntent.resolveActivity(getPackageManager()) != null){
                //Create a file to store the image
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                }
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,"br.ufop.ildeir.ubspaces.fileprovider", photoFile);
                    pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            photoURI);
                    startActivityForResult(pictureIntent,
                            IMG_CAMERA);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 1){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent pictureIntent = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE);
                if(pictureIntent.resolveActivity(getPackageManager()) != null){
                    //Create a file to store the image
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                    }
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(this,"br.ufop.ildeir.ubspaces.fileprovider", photoFile);
                        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                photoURI);
                        startActivityForResult(pictureIntent,
                                IMG_CAMERA);
                    }
                }
            }
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
            if(resultCode==RESULT_OK) {
//                Log.e("camera", "IF CAMERA");
//                imgSeted = true;
//                img = (Bitmap) data.getExtras().get("data");
//                fotoView.setImageBitmap(img);
//                fotoView.setVisibility(View.VISIBLE);
                imgSeted = true;

                new AsyncTask<Void,Void,Void>(){

                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
                            img = resizeBitmap(Glide.with(EditarObjActivity.this).load(imageFilePath).asBitmap().into(-1,-1).get());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        if(img != null){
                            fotoView.setImageBitmap(img);
                            fotoView.setVisibility(View.VISIBLE);
                        }
                    }
                }.execute();

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

    public Bitmap resizeBitmap(Bitmap img){
        if(img.getHeight() > 2000){
            img = Bitmap.createScaledBitmap(img,(int)(img.getWidth()*0.25),(int)(img.getHeight()*0.25),true);
        }else{
            if(img.getHeight() > 1000){
                img = Bitmap.createScaledBitmap(img,(int)(img.getWidth()*0.5),(int)(img.getHeight()*0.5),true);
            }
        }
        return img;
    }

    public byte[] bmpToByteArray(Bitmap img){
        if(img == null){
            img = BitmapFactory.decodeResource(getResources(),R.drawable.no_foto);
        }
        ByteArrayOutputStream b_stream = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.JPEG,100,b_stream);
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
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        intentIntegrator.setBeepEnabled(false);
        intentIntegrator.setCaptureActivity(ScanActivity.class);
        intentIntegrator.initiateScan();
    }
}
