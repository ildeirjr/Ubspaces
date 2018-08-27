package br.ufop.ildeir.ubspaces.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import br.ufop.ildeir.ubspaces.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Sobre o App");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void ufopSite(View view) {
        String url = "https://www.ufop.br/";
        Intent it = new Intent(Intent.ACTION_VIEW);
        it.setData(Uri.parse(url));
        startActivity(it);
    }

    public void imobilisSite(View view) {
        String url = "http://www.decom.ufop.br/imobilis/";
        Intent it = new Intent(Intent.ACTION_VIEW);
        it.setData(Uri.parse(url));
        startActivity(it);
    }
}
