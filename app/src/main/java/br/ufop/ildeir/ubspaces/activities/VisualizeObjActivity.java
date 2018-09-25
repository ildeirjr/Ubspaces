package br.ufop.ildeir.ubspaces.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

import br.ufop.ildeir.ubspaces.R;
import br.ufop.ildeir.ubspaces.miscellaneous.DateHandler;
import br.ufop.ildeir.ubspaces.singleton.ItemSingleton;

/**
 * Created by Ildeir on 21/06/2018.
 */

public class VisualizeObjActivity extends AppCompatActivity {

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
        textEstado = findViewById(R.id.textEstado);
        textDescricao = findViewById(R.id.textDescricao);
        textBloco = findViewById(R.id.textBloco);
        textSala = findViewById(R.id.textSala);
        textData = findViewById(R.id.textData);
        textRecebedor = findViewById(R.id.textRecebedor);
        textNota = findViewById(R.id.textNota);
        textUnidade = findViewById(R.id.textUnidade);
        foto = findViewById(R.id.imgView);

        if(ItemSingleton.getInstance().getItemSingleton() != null){
            textNome.setText(ItemSingleton.getInstance().getItemSingleton().getNome());
            textCodigo.setText(ItemSingleton.getInstance().getItemSingleton().getCodigo());
            textEstado.setText(ItemSingleton.getInstance().getItemSingleton().getEstado());
            textDescricao.setText(ItemSingleton.getInstance().getItemSingleton().getDescricao());
            textBloco.setText(ItemSingleton.getInstance().getItemSingleton().getBloco());
            textSala.setText(ItemSingleton.getInstance().getItemSingleton().getSala());
            textData.setText(DateHandler.sqlDateToString(ItemSingleton.getInstance().getItemSingleton().getDataEntrada()));
            textRecebedor.setText(ItemSingleton.getInstance().getItemSingleton().getRecebeu());
            textNota.setText(ItemSingleton.getInstance().getItemSingleton().getNota());
            textUnidade.setText(ItemSingleton.getInstance().getItemSingleton().getUnidade());
            img = BitmapFactory.decodeByteArray(ItemSingleton.getInstance().getItemSingleton().getImg(),0,ItemSingleton.getInstance().getItemSingleton().getImg().length);
            foto.setImageBitmap(img);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
