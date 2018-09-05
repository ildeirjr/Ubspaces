package br.ufop.ildeir.ubspaces.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
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
    private ArrayAdapter<String> spinnerAdapter;
    private Spinner stateSpinner, unitSpinner, deptSpinner;
    private int dia,mes,ano;
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

    private static String[] DEPT_ICEA_OPTIONS = {"DECEA - Departamento de Ciências Exatas e Aplicadas",
                                                 "DECSI - Departamento de Computação e Sistemas",
                                                 "DEELT - Departamento de Engenharia Elétrica",
                                                 "DEENP - Departamento de Engenharia de Produção"};

    private static String[] DEPT_ICSA_OPTIONS = {"DECEG - Departamento de Ciências Econômicas e Gerenciais",
                                                 "DECSO - Departamento de Ciências Sociais, Jornalismo e Serviço Social"};

    private static String[] DEPT_ICHS_OPTIONS = {"DEEDU - Departamento de Educação",
                                                 "DEHIS - Departamento de História",
                                                 "DELET - Departamento de Letras"};

    private static String[] DEPT_IFAC_OPTIONS = {"DEART - Departamento de Artes",
                                                 "DEFIL - Departamento de Filosofia",
                                                 "DEMUS - Departamento de Música"};

    private static String[] DEPT_ICEB_OPTIONS = {"DEBIO - Departamento de Biodiversidade, Evolução e Meio Ambiente",
                                                 "DECBI - Departamento de Ciências Biológicas",
                                                 "DECOM - Departamento de Computação",
                                                 "DEEMA - Departamento de Educação Matemática",
                                                 "DEEST - Departamento de Estatística",
                                                 "DEFIS - Departamento de Física",
                                                 "DEMAT - Departamento de Matemática",
                                                 "DEQUI - Departamento de Química"};

    private static String[] DEPT_NUTRICAO_OPTIONS = {" "};

    private static String[] DEPT_MEDICINA_OPTIONS = {"DECGP - Departamento de Cirurgia, Ginecologia e Obstetrícia e Propedêutica",
                                                     "DECPA - Departamento de Clínica Pediátrica e do Adulto",
                                                     "DEMSC - Departamento de Medicina de Família, Saúde Mental e Saúde Coletiva"};

    private static String[] DEPT_MINAS_OPTIONS = {"DEAMB - Departamento de Engenharia Ambiental",
                                                  "DEARQ - Departamento de Arquitetura e Urbanismo",
                                                  "DECAT - Departamento do Curso de Engenharia de Controle e Automação e Técnicas Fundamentais",
                                                  "DEMEC - Departamento do Curso de Engenharia Mecânica",
                                                  "DECIV - Departamento de Engenharia Civil",
                                                  "DEGEO - Departamento de Geologia",
                                                  "DEMET - Departamento de Engenharia Metalúrgica e de Materiais",
                                                  "DEMIN - Departamento de Engenharia de Minas",
                                                  "DEPRO - Departamento de Engenharia de Produção",
                                                  "DEURB - Departamento de Engenharia Urbana"};

    private static String[] DEPT_FARMACIA_OPTIONS = {"Departamento de Análises Clínicas",
                                                     "Departamento de Farmácia"};

    private static String[] DEPT_EDTM_OPTIONS = {" "};

    private static String[] DEPT_CEDUFOP_OPTIONS = {" "};

    private static String[] DEPT_CEAD_OPTIONS = {"DEETE - Departamento de Educação e Tecnologias",
                                                 "DEGEP - Departamento de Gestão Pública"};

    Bitmap img;
    byte[] b;
    boolean imgSeted = false;

    IntentIntegrator intentIntegrator;

    private SimpleDateFormat simpleDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_obj);

        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        if(savedInstanceState != null){
            calendar.set(Calendar.DAY_OF_MONTH, savedInstanceState.getInt("day"));
            calendar.set(Calendar.MONTH, savedInstanceState.getInt("month"));
            calendar.set(Calendar.YEAR, savedInstanceState.getInt("year"));
        }

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
        //etDepto = findViewById(R.id.layoutDepto);
        etData = findViewById(R.id.layoutData);
        etRecebedor = findViewById(R.id.layoutRecebedor);
        etNota = findViewById(R.id.layoutNota);
        fotoView = findViewById(R.id.addImg);
        stateSpinner = findViewById(R.id.stateSpinner);
        unitSpinner = findViewById(R.id.unitSpinner);
        deptSpinner = findViewById(R.id.deptSpinner);

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
                            etData.getEditText().setText(simpleDateFormat.format(calendar.getTime()));
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
        unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(UNIT_SPINNER_OPTIONS[i].equals(UNIT_SPINNER_OPTIONS[0])){
                    initDeptSpinner(DEPT_CEAD_OPTIONS);
                } else if(UNIT_SPINNER_OPTIONS[i].equals(UNIT_SPINNER_OPTIONS[1])){
                    initDeptSpinner(DEPT_CEDUFOP_OPTIONS);
                } else if(UNIT_SPINNER_OPTIONS[i].equals(UNIT_SPINNER_OPTIONS[2])){
                    initDeptSpinner(DEPT_EDTM_OPTIONS);
                } else if(UNIT_SPINNER_OPTIONS[i].equals(UNIT_SPINNER_OPTIONS[3])){
                    initDeptSpinner(DEPT_FARMACIA_OPTIONS);
                } else if(UNIT_SPINNER_OPTIONS[i].equals(UNIT_SPINNER_OPTIONS[4])){
                    initDeptSpinner(DEPT_MINAS_OPTIONS);
                } else if(UNIT_SPINNER_OPTIONS[i].equals(UNIT_SPINNER_OPTIONS[5])){
                    initDeptSpinner(DEPT_MEDICINA_OPTIONS);
                } else if(UNIT_SPINNER_OPTIONS[i].equals(UNIT_SPINNER_OPTIONS[6])){
                    initDeptSpinner(DEPT_NUTRICAO_OPTIONS);
                } else if(UNIT_SPINNER_OPTIONS[i].equals(UNIT_SPINNER_OPTIONS[7])){
                    initDeptSpinner(DEPT_ICEA_OPTIONS);
                } else if(UNIT_SPINNER_OPTIONS[i].equals(UNIT_SPINNER_OPTIONS[8])){
                    initDeptSpinner(DEPT_ICEB_OPTIONS);
                } else if(UNIT_SPINNER_OPTIONS[i].equals(UNIT_SPINNER_OPTIONS[9])){
                    initDeptSpinner(DEPT_ICHS_OPTIONS);
                } else if(UNIT_SPINNER_OPTIONS[i].equals(UNIT_SPINNER_OPTIONS[10])){
                    initDeptSpinner(DEPT_ICSA_OPTIONS);
                } else if(UNIT_SPINNER_OPTIONS[i].equals(UNIT_SPINNER_OPTIONS[11])){
                    initDeptSpinner(DEPT_IFAC_OPTIONS);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void initDeptSpinner(String[] depts){
        ArrayAdapter<String> adapterDeptSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,depts);
        deptSpinner.setAdapter(adapterDeptSpinner);
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
                   !validarCampo(etNota)|
                   !validarCampo(etRecebedor)){
                    return true;
                }else {
                    dia = calendar.get(Calendar.DAY_OF_MONTH);
                    mes = calendar.get(Calendar.MONTH) + 1;
                    ano = calendar.get(Calendar.YEAR);
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("codigo",etCodigo.getEditText().getText().toString());
                        jsonObject.put("nome",etNome.getEditText().getText().toString());
                        jsonObject.put("estado",stateSpinner.getSelectedItem().toString());
                        jsonObject.put("descricao",etDescricao.getEditText().getText().toString());
                        jsonObject.put("local",etLocal.getEditText().getText().toString());
                        jsonObject.put("depto",deptSpinner.getSelectedItem().toString());
                        jsonObject.put("dia",dia);
                        jsonObject.put("mes",mes);
                        jsonObject.put("ano",ano);
                        jsonObject.put("recebeu",etRecebedor.getEditText().getText().toString());
                        jsonObject.put("nota",etNota.getEditText().getText().toString());
                        jsonObject.put("unidade",unitSpinner.getSelectedItem().toString());
                        jsonObject.put("nome_usuario",Integer.parseInt(SessionManager.getInstance().getUserId()));
                        Log.e("user_id",SessionManager.getInstance().getUserId());
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
        intentIntegrator.setCaptureActivity(ScanActivity.class);
        intentIntegrator.initiateScan();
    }



}
