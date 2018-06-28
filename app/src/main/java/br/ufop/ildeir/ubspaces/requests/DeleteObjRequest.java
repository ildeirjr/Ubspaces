package br.ufop.ildeir.ubspaces.requests;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.zxing.client.android.Intents;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Scanner;

import br.ufop.ildeir.ubspaces.R;
import br.ufop.ildeir.ubspaces.miscellaneous.UbspaceURL;
import br.ufop.ildeir.ubspaces.singleton.SessionManager;

/**
 * Created by Ildeir on 14/06/2018.
 */

public class DeleteObjRequest extends AsyncTask<String,Void,String> {

    private String itemCode;
    private String itemImgPath;

    public DeleteObjRequest(String itemCode, String itemImgPath) {
        this.itemCode = itemCode;
        this.itemImgPath = itemImgPath;
    }

    @Override
    protected String doInBackground(String... strings) {
        URL url = null;
        try {
            url = new URL(new UbspaceURL().getUrl() + "delete/?id="+itemCode+"&foto="+itemImgPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Authorization", SessionManager.getInstance().getUserToken());
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.connect();
            int responseCode = conn.getResponseCode();
            if(responseCode == 401){
                SessionManager.getInstance().toLoginActivity();
                return "401";
            }
            Scanner scanner = new Scanner(conn.getInputStream());
            String result = "";
            while(scanner.hasNext()){
                result += scanner.nextLine();
            }
            conn.disconnect();
            return result;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
