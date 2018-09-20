package br.ufop.ildeir.ubspaces.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import br.ufop.ildeir.ubspaces.miscellaneous.DateDialog;
import br.ufop.ildeir.ubspaces.R;
import br.ufop.ildeir.ubspaces.miscellaneous.DateHandler;
import br.ufop.ildeir.ubspaces.network.RetrofitConfig;
import br.ufop.ildeir.ubspaces.objects.Item;
import br.ufop.ildeir.ubspaces.requests.post.EditObjDataRequest;
import br.ufop.ildeir.ubspaces.requests.post.PostObjImgRequest;
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
    private TextInputLayout etLocal;
    private TextInputLayout etDepto;
    private TextInputLayout etData;
    private TextInputLayout etRecebedor;
    private TextInputLayout etNota;
    private ImageView fotoView;
    private Spinner stateSpinner, unitSpinner, deptSpinner;
    private int dia,mes,ano;
    private Calendar calendar;
    private static int IMG_REQUEST = 1;
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
    private boolean flagDateDialogOpened = false;
    private String codigoAntigo, fotoAntigo;
    private Item itemSingleton;
    private boolean imgSeted;

    private IntentIntegrator intentIntegrator;

    private ProgressDialog progressDialog;

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
        etLocal = findViewById(R.id.layoutLocal);
        //etDepto = findViewById(R.id.layoutDepto);
        etData = findViewById(R.id.layoutData);
        etRecebedor = findViewById(R.id.layoutRecebedor);
        etNota = findViewById(R.id.layoutNota);
        fotoView = findViewById(R.id.addImg);
        stateSpinner = findViewById(R.id.stateSpinner);
        unitSpinner = findViewById(R.id.unitSpinner);
        deptSpinner = findViewById(R.id.deptSpinnerEdit);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, STATE_SPINNER_OPTIONS);
        stateSpinner.setAdapter(spinnerAdapter);

        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, UNIT_SPINNER_OPTIONS);
        unitSpinner.setAdapter(spinnerAdapter);
        unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(UNIT_SPINNER_OPTIONS[i].equals(UNIT_SPINNER_OPTIONS[0])){
                    initDeptSpinner(itemSingleton.getDepto(), DEPT_CEAD_OPTIONS);
                } else if(UNIT_SPINNER_OPTIONS[i].equals(UNIT_SPINNER_OPTIONS[1])){
                    initDeptSpinner(itemSingleton.getDepto(), DEPT_CEDUFOP_OPTIONS);
                } else if(UNIT_SPINNER_OPTIONS[i].equals(UNIT_SPINNER_OPTIONS[2])){
                    initDeptSpinner(itemSingleton.getDepto(), DEPT_EDTM_OPTIONS);
                } else if(UNIT_SPINNER_OPTIONS[i].equals(UNIT_SPINNER_OPTIONS[3])){
                    initDeptSpinner(itemSingleton.getDepto(), DEPT_FARMACIA_OPTIONS);
                } else if(UNIT_SPINNER_OPTIONS[i].equals(UNIT_SPINNER_OPTIONS[4])){
                    initDeptSpinner(itemSingleton.getDepto(), DEPT_MINAS_OPTIONS);
                } else if(UNIT_SPINNER_OPTIONS[i].equals(UNIT_SPINNER_OPTIONS[5])){
                    initDeptSpinner(itemSingleton.getDepto(), DEPT_MEDICINA_OPTIONS);
                } else if(UNIT_SPINNER_OPTIONS[i].equals(UNIT_SPINNER_OPTIONS[6])){
                    initDeptSpinner(itemSingleton.getDepto(), DEPT_NUTRICAO_OPTIONS);
                } else if(UNIT_SPINNER_OPTIONS[i].equals(UNIT_SPINNER_OPTIONS[7])){
                    initDeptSpinner(itemSingleton.getDepto(), DEPT_ICEA_OPTIONS);
                } else if(UNIT_SPINNER_OPTIONS[i].equals(UNIT_SPINNER_OPTIONS[8])){
                    initDeptSpinner(itemSingleton.getDepto(), DEPT_ICEB_OPTIONS);
                } else if(UNIT_SPINNER_OPTIONS[i].equals(UNIT_SPINNER_OPTIONS[9])){
                    initDeptSpinner(itemSingleton.getDepto(), DEPT_ICHS_OPTIONS);
                } else if(UNIT_SPINNER_OPTIONS[i].equals(UNIT_SPINNER_OPTIONS[10])){
                    initDeptSpinner(itemSingleton.getDepto(), DEPT_ICSA_OPTIONS);
                } else if(UNIT_SPINNER_OPTIONS[i].equals(UNIT_SPINNER_OPTIONS[11])){
                    initDeptSpinner(itemSingleton.getDepto(), DEPT_IFAC_OPTIONS);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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
//            etLocal.getEditText().setText(bundle.getString("local"));
//            etDepto.getEditText().setText(bundle.getString("depto"));
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
            etLocal.getEditText().setText(itemSingleton.getLocal());
            //etDepto.getEditText().setText(itemSingleton.getDepto());
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

    private void initDeptSpinner(String dept, String[] depts){
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, depts);
        deptSpinner.setAdapter(adapterSpinner);
        for(int i=0 ; i<depts.length ; i++){
            if(dept.equals(depts[i])){
                deptSpinner.setSelection(i);
                break;
            }
        }
    }

    private void onSelectDeptSpinner(String[] depts){
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, depts);
        deptSpinner.setAdapter(adapterSpinner);
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
                    showConfirmDialog();
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
                itemSingleton.setLocal(etLocal.getEditText().getText().toString());
                itemSingleton.setDepto(deptSpinner.getSelectedItem().toString());
                itemSingleton.setDataEntrada(sqlDate);
                itemSingleton.setRecebeu(etRecebedor.getEditText().getText().toString());
                itemSingleton.setNota(etNota.getEditText().getText().toString());
                itemSingleton.setUnidade(unitSpinner.getSelectedItem().toString());
                if(!itemSingleton.getFoto().equals("null.jpg") || imgSeted){
                    itemSingleton.setFoto(etNome.getEditText().getText().toString().replaceAll(" ", "_") + "_" + etCodigo.getEditText().getText().toString() + ".jpg");
                    try {
                        Log.e("teste", "ENTROU NO IMGSETED");
                        itemSingleton.setImg(bmpToByteArray());
                        jsonImg.put("nome", itemSingleton.getFoto());
                        jsonImg.put("img", bmptoString());
                        createImgThumb();
                        jsonImg.put("imgThumb", Base64.encodeToString(bmpToByteArray(), Base64.DEFAULT));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    JSONObject jsonObject = itemSingleton.ItemtoJSON();
                    jsonObject.put("codigoAntigo",codigoAntigo);
                    jsonObject.put("fotoAntigo",fotoAntigo);
                    if(!fotoAntigo.equals(itemSingleton.getFoto())){
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
                            if(!fotoAntigo.equals(itemSingleton.getFoto()) || imgSeted){
//                                    new PostObjImgRequest(jsonImg.toString(),this).execute();
                                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonImg.toString());
                                Call<ResponseBody> imgCall = new RetrofitConfig().postObjImgRequest().postObjImg(body);
                                imgCall.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        progressDialog.dismiss();
                                        Toast.makeText(EditarObjActivity.this, "Alterações realizadas com sucesso!", Toast.LENGTH_SHORT).show();
                                        startActivity(it);
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
                                startActivity(it);
                                finish();
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
//                    bundle.putString("local", etLocal.getEditText().getText().toString());
//                    bundle.putString("depto", etDepto.getEditText().getText().toString());
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
        builder.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMG_REQUEST){
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

    public String[] splitDate(String date){
        String str[] = date.split("/");
        return str;
    }

    public void code_scan(View view) {
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        intentIntegrator.setBeepEnabled(false);
        intentIntegrator.setCaptureActivity(ScanActivity.class);
        intentIntegrator.initiateScan();
    }
}
