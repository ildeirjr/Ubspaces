package br.ufop.ildeir.ubspaces.requests.get;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import br.ufop.ildeir.ubspaces.R;
import br.ufop.ildeir.ubspaces.miscellaneous.UbspaceURL;
import br.ufop.ildeir.ubspaces.objects.Item;
import br.ufop.ildeir.ubspaces.singleton.SessionManager;

/**
 * Created by Ildeir on 22/05/2018.
 */

public class GetObjDataRequest extends AsyncTask<String,Void,Item> {

    private String idObject;

    public GetObjDataRequest(String idObject, Context context){
        this.idObject = idObject;
    }

    @Override
    protected Item doInBackground(String... strings) {
        try {
            JSONObject jsonItem;
            URL url = new URL(new UbspaceURL().getUrl() + "?id=" + idObject);
            //Log.e("teste",new UbspaceURL().getUrl() + "?id=" + idObject);
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
            String result = new Scanner(conn.getInputStream()).nextLine();
            jsonItem = new JSONObject(result);
            conn.disconnect();
            if(!jsonItem.isNull("codigo")){
                Item item = new Item();
                item.JSONtoItem(jsonItem);
                return item;
            }else return null;
            //Log.e("teste",result);
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
