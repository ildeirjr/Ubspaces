package br.ufop.ildeir.ubspaces.requests.post;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Scanner;

import br.ufop.ildeir.ubspaces.miscellaneous.UbspaceURL;
import br.ufop.ildeir.ubspaces.singleton.SessionManager;

/**
 * Created by Ildeir on 23/08/2018.
 */

public class RestoreObjRequest extends AsyncTask<String,Void,String> {
    @Override
    protected String doInBackground(String... strings) {
        URL url = null;
        try {
            url = new URL(new UbspaceURL().getUrl() + "restore/?id="+strings[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
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
