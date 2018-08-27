package br.ufop.ildeir.ubspaces.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import br.ufop.ildeir.ubspaces.R;
import br.ufop.ildeir.ubspaces.objects.Item;
import br.ufop.ildeir.ubspaces.requests.delete.DeleteObjRequest;
import br.ufop.ildeir.ubspaces.requests.get.GetUserRequest;
import br.ufop.ildeir.ubspaces.singleton.ItemSingleton;
import br.ufop.ildeir.ubspaces.singleton.SessionManager;
import br.ufop.ildeir.ubspaces.singleton.UserSingleton;

public class DeletedObjActivity extends AppCompatActivity {

    private TextView textNome;
    private TextView textCodigo;
    private TextView textEstado;
    private TextView textDescricao;
    private TextView textDepto;
    private TextView textData;
    private TextView textLocal;
    private TextView textRecebedor;
    private TextView textNota;
    private TextView textUnidade;
    private ImageView foto;
    private TextView textUsrExclusao;
    private TextView textDataExclusao;
    private Bitmap img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deleted_obj);

        try {
            String user = new GetUserRequest(SessionManager.getInstance().getUserId()).execute().get();
            if(user == null){
                Toast.makeText(this, R.string.invalid_operator, Toast.LENGTH_LONG).show();
                SessionManager.getInstance().toLoginActivity();
                finish();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        textNome = findViewById(R.id.textNome);
        textCodigo = findViewById(R.id.textCodigo);
        textEstado = findViewById(R.id.textEstado);
        textDescricao = findViewById(R.id.textDescricao);
        textLocal = findViewById(R.id.textLocal);
        textDepto = findViewById(R.id.textDepto);
        textData = findViewById(R.id.textData);
        textRecebedor = findViewById(R.id.textRecebedor);
        textNota = findViewById(R.id.textNota);
        textUnidade = findViewById(R.id.textUnidade);
        foto = findViewById(R.id.imgView);
        textUsrExclusao = findViewById(R.id.textUsrExclusao);
        textDataExclusao = findViewById(R.id.textDataExclusao);

        if(ItemSingleton.getInstance().getItemSingleton() != null){
            textNome.setText(ItemSingleton.getInstance().getItemSingleton().getNome());
            textCodigo.setText(ItemSingleton.getInstance().getItemSingleton().getCodigo());
            textEstado.setText(ItemSingleton.getInstance().getItemSingleton().getEstado());
            textDescricao.setText(ItemSingleton.getInstance().getItemSingleton().getDescricao());
            textLocal.setText(ItemSingleton.getInstance().getItemSingleton().getLocal());
            textDepto.setText(ItemSingleton.getInstance().getItemSingleton().getDepto());
            textData.setText(ItemSingleton.getInstance().getItemSingleton().getDia() + "/" +
                    ItemSingleton.getInstance().getItemSingleton().getMes() + "/" +
                    ItemSingleton.getInstance().getItemSingleton().getAno());
            textRecebedor.setText(ItemSingleton.getInstance().getItemSingleton().getRecebeu());
            textNota.setText(ItemSingleton.getInstance().getItemSingleton().getNota());
            textUnidade.setText(ItemSingleton.getInstance().getItemSingleton().getUnidade());
            img = BitmapFactory.decodeByteArray(ItemSingleton.getInstance().getItemSingleton().getImg(),0,ItemSingleton.getInstance().getItemSingleton().getImg().length);
            foto.setImageBitmap(img);
            textUsrExclusao.setText(ItemSingleton.getInstance().getItemSingleton().getNomeUsrExclusao());

            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            DateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
            Date date = null;
            try {
                date = inputFormat.parse(ItemSingleton.getInstance().getItemSingleton().getDataExclusao());
                textDataExclusao.setText(outputFormat.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }


        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
