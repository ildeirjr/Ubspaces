package br.ufop.ildeir.ubspaces.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.concurrent.ExecutionException;

import br.ufop.ildeir.ubspaces.R;
import br.ufop.ildeir.ubspaces.objects.Item;
import br.ufop.ildeir.ubspaces.requests.get.GetObjDataRequest;
import br.ufop.ildeir.ubspaces.requests.get.GetObjImgRequest;
import br.ufop.ildeir.ubspaces.singleton.ItemSingleton;

public class ScanActivity extends AppCompatActivity implements DecoratedBarcodeView.TorchListener{

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private ImageButton switchFlashlightButton;
    private boolean torchOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Ler código");

        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);
        barcodeScannerView.setTorchListener(this);

        switchFlashlightButton = findViewById(R.id.switch_flashlight);

        // if the device does not have flashlight in its camera,
        // then remove the switch flashlight button...
        if (!hasFlash()) {
            switchFlashlightButton.setVisibility(View.GONE);
        }

        torchOn = false;

        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    /**
     * Check if the device's camera has a Flashlight.
     * @return true if there is Flashlight, otherwise false.
     */
    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public void switchFlashlight(View view) {
//        if (getString(R.string.turn_on_flashlight).equals(switchFlashlightButton.getText())) {
//            barcodeScannerView.setTorchOn();
//        } else {
//            barcodeScannerView.setTorchOff();
//        }
        if(torchOn){
            barcodeScannerView.setTorchOff();
            torchOn = false;
        } else {
            barcodeScannerView.setTorchOn();
            torchOn = true;
        }
    }

    @Override
    public void onTorchOn() {
        //switchFlashlightButton.setImageResource(R.drawable.ic_flash_off);
        switchFlashlightButton.setBackgroundResource(R.drawable.white_circle_shape);
        switchFlashlightButton.setImageResource(R.drawable.ic_flash_on_black_24dp);
    }

    @Override
    public void onTorchOff() {
        switchFlashlightButton.setBackgroundResource(R.drawable.black_circle_shape);
        switchFlashlightButton.setImageResource(R.drawable.ic_flash_on);
    }
}
