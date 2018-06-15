package br.ufop.ildeir.ubspaces.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutionException;

import br.ufop.ildeir.ubspaces.R;
import br.ufop.ildeir.ubspaces.requests.DeleteObjRequest;
import br.ufop.ildeir.ubspaces.singleton.ItemSingleton;

public class VisualizarObjActivity extends AppCompatActivity {

    private TextView textNome;
    private TextView textCodigo;
    private TextView textDescricao;
    private TextView textDepto;
    private TextView textData;
    private TextView textLocal;
    private TextView textRecebedor;
    private TextView textNota;
    private ImageView foto;
    private Bitmap img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_obj);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        textNome = findViewById(R.id.textNome);
        textCodigo = findViewById(R.id.textCodigo);
        textDescricao = findViewById(R.id.textDescricao);
        textLocal = findViewById(R.id.textLocal);
        textDepto = findViewById(R.id.textDepto);
        textData = findViewById(R.id.textData);
        textRecebedor = findViewById(R.id.textRecebedor);
        textNota = findViewById(R.id.textNota);
        foto = findViewById(R.id.imgView);

        if(ItemSingleton.getInstance().getItemSingleton() != null){
            textNome.setText(ItemSingleton.getInstance().getItemSingleton().getNome());
            textCodigo.setText(ItemSingleton.getInstance().getItemSingleton().getCodigo());
            textDescricao.setText(ItemSingleton.getInstance().getItemSingleton().getDescricao());
            textLocal.setText(ItemSingleton.getInstance().getItemSingleton().getLocal());
            textDepto.setText(ItemSingleton.getInstance().getItemSingleton().getDepto());
            textData.setText(ItemSingleton.getInstance().getItemSingleton().getDia() + "/" +
                             ItemSingleton.getInstance().getItemSingleton().getMes() + "/" +
                             ItemSingleton.getInstance().getItemSingleton().getAno());
            textRecebedor.setText(ItemSingleton.getInstance().getItemSingleton().getRecebeu());
            textNota.setText(ItemSingleton.getInstance().getItemSingleton().getNota());
            img = BitmapFactory.decodeByteArray(ItemSingleton.getInstance().getItemSingleton().getImg(),0,ItemSingleton.getInstance().getItemSingleton().getImg().length);
            foto.setImageBitmap(img);
        }

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
                    String result = new DeleteObjRequest(itemCode,itemImgPath).execute().get();
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
