package br.ufop.ildeir.ubspaces.requests;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import br.ufop.ildeir.ubspaces.miscellaneous.UbspaceURL;
import br.ufop.ildeir.ubspaces.objects.Item;
import br.ufop.ildeir.ubspaces.singleton.SessionManager;

/**
 * Created by Ildeir on 12/06/2018.
 */

public class GetAllObjRequest extends AsyncTask<String,Void,ArrayList<Item>> {

    @Override
    protected ArrayList<Item> doInBackground(String... strings) {
        try {
            Item item;
            ArrayList<Item> objectArrayList = new ArrayList<>();
            URL url = new URL(new UbspaceURL().getUrl() + "listall/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", SessionManager.getInstance().getUserToken());
            conn.setDoInput(true);
            conn.connect();
            int responseCode = conn.getResponseCode();
            if(responseCode == 401){
                SessionManager.getInstance().toLoginActivity();
            }
            String result = new Scanner(conn.getInputStream()).nextLine();
            JSONArray objectJSONArray = new JSONArray(result);
            if(!objectJSONArray.isNull(0)){
                for(int i=0 ; i<objectJSONArray.length() ; i++){
                    item = new Item();
                    item.JSONtoItem((JSONObject) objectJSONArray.get(i));
                    objectArrayList.add(item);
                }
            }
            conn.disconnect();
            return objectArrayList;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
