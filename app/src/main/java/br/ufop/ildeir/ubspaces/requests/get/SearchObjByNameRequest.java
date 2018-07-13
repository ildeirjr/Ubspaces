package br.ufop.ildeir.ubspaces.requests.get;

import android.os.AsyncTask;
import android.util.Log;

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

import br.ufop.ildeir.ubspaces.miscellaneous.UbspaceURL;
import br.ufop.ildeir.ubspaces.objects.Item;
import br.ufop.ildeir.ubspaces.objects.RecyclerViewItem;
import br.ufop.ildeir.ubspaces.singleton.SessionManager;

/**
 * Created by Ildeir on 05/07/2018.
 */

public class SearchObjByNameRequest extends AsyncTask<String,Void,ArrayList<RecyclerViewItem>>{

//    private String searchKey;
//
//    public SearchObjByNameRequest(String searchKey) {
//        this.searchKey = searchKey;
//    }

    @Override
    protected ArrayList<RecyclerViewItem> doInBackground(String... strings) {
        try{
            RecyclerViewItem item;
            ArrayList<RecyclerViewItem> objectArrayList = new ArrayList<>();
            URL url = new URL(new UbspaceURL().getUrl() + "searchName/?substring=" + strings[0]);
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
            JSONArray objectJSONArray = new JSONArray(result);
            if(!objectJSONArray.isNull(0)){
                for(int i=0 ; i<objectJSONArray.length() ; i++){
                    item = new RecyclerViewItem();
                    item.JSONtoItem((JSONObject) objectJSONArray.get(i));
                    objectArrayList.add(item);
                    Log.e("if","laço");
                }
            } else Log.e("if","ELSE");
            conn.disconnect();
            return objectArrayList;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
