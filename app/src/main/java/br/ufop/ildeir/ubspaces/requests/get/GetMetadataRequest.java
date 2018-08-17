package br.ufop.ildeir.ubspaces.requests.get;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import br.ufop.ildeir.ubspaces.miscellaneous.UbspaceURL;
import br.ufop.ildeir.ubspaces.objects.RecyclerViewItem;
import br.ufop.ildeir.ubspaces.singleton.SessionManager;

/**
 * Created by Ildeir on 15/08/2018.
 */

public class GetMetadataRequest extends AsyncTask<String,Void,ArrayList<String>> {
    @Override
    protected ArrayList<String> doInBackground(String... strings) {
        try{
            RecyclerViewItem item;
            ArrayList<String> dataArray = new ArrayList<>();
            URL url = new URL(new UbspaceURL().getUrl() + "getMetadata/?user_id=" + strings[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", SessionManager.getInstance().getUserToken());
            conn.setDoInput(true);
            conn.connect();
            int responseCode = conn.getResponseCode();
            if(responseCode == 401){
                SessionManager.getInstance().toLoginActivity();
                return null;
            }
            String result = "";
            Scanner scanner = new Scanner(conn.getInputStream());
            while(scanner.hasNext()){
                result += scanner.nextLine();
            }
            JSONObject jsonObject = new JSONObject(result);
            dataArray.add(jsonObject.getString("num_obj_user"));
            dataArray.add(jsonObject.getString("num_obj"));
            dataArray.add(jsonObject.getString("num_obj_month"));
            return dataArray;
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
