package br.ufop.ildeir.ubspaces.activities;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
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
import br.ufop.ildeir.ubspaces.objects.Item;
import br.ufop.ildeir.ubspaces.requests.post.EditObjDataRequest;
import br.ufop.ildeir.ubspaces.requests.post.PostObjImgRequest;
import br.ufop.ildeir.ubspaces.singleton.ItemSingleton;


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
    private Spinner stateSpinner, unitSpinner;
    private int dia,mes,ano;
    private DateDialog dateDialog;
    private static int IMG_REQUEST = 1;
    private static String[] STATE_SPINNER_OPTIONS = {"Normal","Excluido","Quebrado","Consertado"};
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
    private boolean flagDateDialogOpened = false;
    private String codigoAntigo, fotoAntigo;
    private Item itemSingleton;
    private boolean imgSeted = false;

    private IntentIntegrator intentIntegrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_obj);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Editar objeto");

        etCodigo = findViewById(R.id.layoutCod);
        etNome = findViewById(R.id.layoutNome);
        etDescricao = findViewById(R.id.layoutDescricao);
        etLocal = findViewById(R.id.layoutLocal);
        etDepto = findViewById(R.id.layoutDepto);
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
                    dateDialog = new DateDialog(view);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    dateDialog.show(ft,"DatePicker");
                    flagDateDialogOpened = true;
                }
            }
        });

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
            dia = itemSingleton.getDia();
            mes = itemSingleton.getMes();
            ano = itemSingleton.getAno();
            etCodigo.getEditText().setText(itemSingleton.getCodigo());
            etNome.getEditText().setText(itemSingleton.getNome());
            etDescricao.getEditText().setText(itemSingleton.getDescricao());
            etLocal.getEditText().setText(itemSingleton.getLocal());
            etDepto.getEditText().setText(itemSingleton.getDepto());
            etData.getEditText().setText(dia + "/" + mes + "/" + ano);
            etRecebedor.getEditText().setText(itemSingleton.getRecebeu());
            etNota.getEditText().setText(itemSingleton.getNota());
            img = BitmapFactory.decodeByteArray(itemSingleton.getImg(),0,itemSingleton.getImg().length);
            fotoView.setImageBitmap(img);
            codigoAntigo = itemSingleton.getCodigo();
            fotoAntigo = itemSingleton.getFoto();

            if(itemSingleton.getEstado().equals(STATE_SPINNER_OPTIONS[0])){
                stateSpinner.setSelection(0);
            } else if(itemSingleton.getEstado().equals(STATE_SPINNER_OPTIONS[1])){
                stateSpinner.setSelection(1);
            } else if(itemSingleton.getEstado().equals(STATE_SPINNER_OPTIONS[2])){
                stateSpinner.setSelection(2);
            } else if(itemSingleton.getEstado().equals(STATE_SPINNER_OPTIONS[3])){
                stateSpinner.setSelection(3);
            }

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
                    if(flagDateDialogOpened){
                        if(dateDialog.isFlagDateSeted()){
                            dia = dateDialog.getDia();
                            mes = dateDialog.getMes();
                            ano = dateDialog.getAno();
                        }
                    }
                    itemSingleton.setCodigo(etCodigo.getEditText().getText().toString());
                    itemSingleton.setNome(etNome.getEditText().getText().toString());
                    itemSingleton.setEstado(stateSpinner.getSelectedItem().toString());
                    itemSingleton.setDescricao(etDescricao.getEditText().getText().toString());
                    itemSingleton.setLocal(etLocal.getEditText().getText().toString());
                    itemSingleton.setDepto(etDepto.getEditText().getText().toString());
                    itemSingleton.setDia(dia);
                    itemSingleton.setMes(mes);
                    itemSingleton.setAno(ano);
                    itemSingleton.setRecebeu(etRecebedor.getEditText().getText().toString());
                    itemSingleton.setNota(etNota.getEditText().getText().toString());
                    itemSingleton.setUnidade(unitSpinner.getSelectedItem().toString());
                    itemSingleton.setFoto(etNome.getEditText().getText().toString().replaceAll(" ","_") + "_" + etCodigo.getEditText().getText().toString() + ".jpg");
                    itemSingleton.setImg(bmpToByteArray());
                    JSONObject jsonObject = itemSingleton.ItemtoJSON();
                    JSONObject jsonImg = new JSONObject();
                    try {
                        jsonObject.put("codigoAntigo",codigoAntigo);
                        jsonObject.put("fotoAntigo",fotoAntigo);
                        jsonImg.put("nome",itemSingleton.getFoto());
                        jsonImg.put("img",bmptoString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        String result = new EditObjDataRequest(jsonObject.toString()).execute().get();
                        if(result.equals("401")){
                            Toast.makeText(this, R.string.invalid_operator, Toast.LENGTH_LONG).show();
                            finish();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    new PostObjImgRequest(jsonImg.toString(),this).execute();
                    Intent it = new Intent(this, VisualizarObjActivity.class);
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
                    startActivity(it);
                    finish();
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
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        intentIntegrator.setBeepEnabled(false);
        intentIntegrator.initiateScan();
    }
}
