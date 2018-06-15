package br.ufop.ildeir.ubspaces.requests;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import br.ufop.ildeir.ubspaces.miscellaneous.UbspaceURL;
import br.ufop.ildeir.ubspaces.singleton.SessionManager;

/**
 * Created by Ildeir on 17/05/2018.
 */

public class GetUserRequest extends AsyncTask<String,Void,String> {

    private String idUser;

    public GetUserRequest(String idUser){
        this.idUser = idUser;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            URL url = new URL(new UbspaceURL().getUrl() + "getOperator/" + "?id=" + idUser);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            Log.e("request",SessionManager.getInstance().getUserToken());
            Log.e("request",SessionManager.getInstance().getUserId());
            conn.setRequestProperty("Authorization", SessionManager.getInstance().getUserToken());
            conn.setDoInput(true);
            conn.connect();
            int responseCode = conn.getResponseCode();
            if(responseCode == 401){
                SessionManager.getInstance().toLoginActivity();
            }
            String result = new Scanner(conn.getInputStream()).next();
            conn.disconnect();
            return result;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
