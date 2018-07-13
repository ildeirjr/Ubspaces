package br.ufop.ildeir.ubspaces.requests.get;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import br.ufop.ildeir.ubspaces.R;
import br.ufop.ildeir.ubspaces.miscellaneous.UbspaceURL;
import br.ufop.ildeir.ubspaces.singleton.SessionManager;
import br.ufop.ildeir.ubspaces.utils.Utils;

/**
 * Created by Ildeir on 23/05/2018.
 */

public class GetObjImgRequest extends AsyncTask<String,Void,byte[]> {

    private String imgPath;

    public GetObjImgRequest(String imgName){
        this.imgPath = imgName;
    }

    @Override
    protected byte[] doInBackground(String... strings) {
        URL url;
        HttpURLConnection conn;
        try {
            url = new URL(new UbspaceURL().getUrl() + "photos/" + imgPath);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", SessionManager.getInstance().getUserToken());
            conn.setDoInput(true);
            conn.connect();
            int responseCode = conn.getResponseCode();
            if(responseCode == 401){
                SessionManager.getInstance().toLoginActivity();
                return null;
            }
            return Utils.StreamToByteArray.extractByteArray(conn.getInputStream());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                url = new URL(new UbspaceURL().getUrl() + "photos/default.jpg");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", SessionManager.getInstance().getUserToken());
                conn.connect();
                int responseCode = conn.getResponseCode();
                if(responseCode == 401){
                    SessionManager.getInstance().toLoginActivity();
                    return null;
                }
                return Utils.StreamToByteArray.extractByteArray(conn.getInputStream());
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return new byte[0];
    }
}
