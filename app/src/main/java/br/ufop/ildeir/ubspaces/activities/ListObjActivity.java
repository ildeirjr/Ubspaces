package br.ufop.ildeir.ubspaces.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import br.ufop.ildeir.ubspaces.R;
import br.ufop.ildeir.ubspaces.adapters.ObjectListAdapter;
import br.ufop.ildeir.ubspaces.objects.Item;
import br.ufop.ildeir.ubspaces.requests.GetAllObjRequest;
import br.ufop.ildeir.ubspaces.requests.GetObjImgRequest;
import br.ufop.ildeir.ubspaces.singleton.ItemSingleton;
import br.ufop.ildeir.ubspaces.singleton.ObjectListSingleton;

public class ListObjActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_obj);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Objetos cadastrados");

        listView = findViewById(R.id.objectListView);
        listView.setAdapter(new ObjectListAdapter(ObjectListSingleton.getInstance().getObjectList(),this));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ItemSingleton.getInstance().setItemSingleton(ObjectListSingleton.getInstance().getObjectList().get(i));
                startActivity(new Intent(view.getContext(),VisualizarObjActivity.class));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            ArrayList<Item> itemsArrayList = new GetAllObjRequest().execute().get();
            for(int i=0 ; i<itemsArrayList.size() ; i++){
                itemsArrayList.get(i).setImg(new GetObjImgRequest(itemsArrayList.get(i).getFoto()).execute().get());
            }
            ObjectListSingleton.getInstance().setObjectList(itemsArrayList);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        listView.setAdapter(new ObjectListAdapter(ObjectListSingleton.getInstance().getObjectList(),this));
    }
}
