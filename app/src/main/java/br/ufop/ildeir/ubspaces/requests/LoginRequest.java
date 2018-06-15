package br.ufop.ildeir.ubspaces.requests;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import br.ufop.ildeir.ubspaces.miscellaneous.UbspaceURL;
import br.ufop.ildeir.ubspaces.singleton.SessionManager;

/**
 * Created by Ildeir on 17/05/2018.
 */

public class LoginRequest extends AsyncTask<String,Void,String> {

    private String content;

    public LoginRequest(String content){
        this.content = content;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            URL url = new URL(new UbspaceURL().getUrl() + "validateLogin/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-type","application/json");
            conn.setRequestProperty("Authorization", "1gdh87efuhwi");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            PrintStream printStream = new PrintStream(conn.getOutputStream());
            printStream.println(content);
            conn.connect();
            int responseCode = conn.getResponseCode();
            if(responseCode == 401){
                Log.e("teste","ENTROU NO IF 401");
                SessionManager.getInstance().toLoginActivity();
                return "";
            }
            String result = "";
            Scanner s = new Scanner(conn.getInputStream());
            while(s.hasNext()){
                result = result + s.nextLine();
            }
            //String result = "";
            //String result = new DataInputStream(conn.getInputStream()).readUTF();
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
