package br.ufop.ildeir.ubspaces.requests;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Scanner;

import br.ufop.ildeir.ubspaces.miscellaneous.UbspaceURL;
import br.ufop.ildeir.ubspaces.singleton.SessionManager;

/**
 * Created by Ildeir on 12/06/2018.
 */

public class ClearUserTokenRequest extends AsyncTask<Void,Void,String> {


    @Override
    protected String doInBackground(Void... voids) {
        try {
            URL url = new URL(new UbspaceURL().getUrl() + "clearUserToken/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            System.out.println(SessionManager.getInstance().getUserToken());
            //Log.e("clearToken",SessionManager.getInstance().getUserToken());
            conn.setRequestProperty("Authorization", SessionManager.getInstance().getUserToken());
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.connect();
            String result = new Scanner(conn.getInputStream()).nextLine();
            conn.disconnect();
            return result;
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
