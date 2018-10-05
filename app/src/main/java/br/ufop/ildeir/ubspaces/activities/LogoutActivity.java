package br.ufop.ildeir.ubspaces.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import br.ufop.ildeir.ubspaces.R;

public class LogoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
    }

    public void toLoginActivity(View view) {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
