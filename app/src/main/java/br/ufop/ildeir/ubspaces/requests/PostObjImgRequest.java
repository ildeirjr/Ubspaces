package br.ufop.ildeir.ubspaces.requests;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import br.ufop.ildeir.ubspaces.miscellaneous.UbspaceURL;
import br.ufop.ildeir.ubspaces.singleton.SessionManager;

/**
 * Created by Ildeir on 25/05/2018.
 */

public class PostObjImgRequest extends AsyncTask<Void,Void,String> {

    private String imgContent;

    public PostObjImgRequest(String imgContent){
        this.imgContent = imgContent;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            URL url = new URL(new UbspaceURL().getUrl() + "uploadImg/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-type","application/json");
            conn.setRequestProperty("Authorization", SessionManager.getInstance().getUserToken());
            conn.setDoOutput(true);
            PrintStream printStream = new PrintStream(conn.getOutputStream());
            printStream.println(imgContent);
            conn.connect();
            int responseCode = conn.getResponseCode();
            if(responseCode == 401){
                SessionManager.getInstance().toLoginActivity();
            }
            Scanner scanner = new Scanner(conn.getInputStream());
            String result = "";
            while(scanner.hasNext()){
                result = result + scanner.nextLine();
            }
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
