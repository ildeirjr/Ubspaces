package br.ufop.ildeir.ubspaces.activities;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.util.SortedList;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import br.ufop.ildeir.ubspaces.R;
import br.ufop.ildeir.ubspaces.adapters.ObjectListAdapter;
import br.ufop.ildeir.ubspaces.adapters.RecyclerListAdapter;
import br.ufop.ildeir.ubspaces.miscellaneous.RecyclerItemTouchHelper;
import br.ufop.ildeir.ubspaces.miscellaneous.WrapContentLinearLayoutManager;
import br.ufop.ildeir.ubspaces.objects.Item;
import br.ufop.ildeir.ubspaces.requests.DeleteObjRequest;
import br.ufop.ildeir.ubspaces.requests.GetAllObjRequest;
import br.ufop.ildeir.ubspaces.requests.GetObjImgRequest;
import br.ufop.ildeir.ubspaces.singleton.ItemSingleton;
import br.ufop.ildeir.ubspaces.singleton.ObjectListSingleton;

public class ListObjActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, android.support.v7.widget.SearchView.OnQueryTextListener {

    private ListView listView;
    private RecyclerView recyclerView;
    private RecyclerListAdapter recyclerListAdapter;
    private CoordinatorLayout coordinatorLayout;
    private ArrayList<Item> deletedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_obj);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Objetos cadastrados");

        coordinatorLayout = findViewById(R.id.coordinator_layout);
        recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);



//        listView = findViewById(R.id.objectListView);
//        listView.setAdapter(new ObjectListAdapter(ObjectListSingleton.getInstance().getObjectList(),this));
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                ItemSingleton.getInstance().setItemSingleton(ObjectListSingleton.getInstance().getObjectList().get(i));
//                startActivity(new Intent(view.getContext(),VisualizarObjActivity.class));
//            }
//        });

        deletedItems = new ArrayList<>();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list_obj,menu);
        final MenuItem searchItem = menu.findItem(R.id.search_btn);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        return true;
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
            Log.e("tamanho do array",String.valueOf(itemsArrayList.size()));
            for(int i=0 ; i<itemsArrayList.size() ; i++){
                itemsArrayList.get(i).setImg(new GetObjImgRequest(itemsArrayList.get(i).getFoto()).execute().get());
            }
            ObjectListSingleton.getInstance().setObjectList(itemsArrayList);
            recyclerListAdapter = new RecyclerListAdapter(this, ObjectListSingleton.getInstance().getObjectList());
            recyclerView.setAdapter(recyclerListAdapter);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //listView.setAdapter(new ObjectListAdapter(ObjectListSingleton.getInstance().getObjectList(),this));
    }

    @Override
    protected void onStop() {
        super.onStop();
        for(int i=0 ; i<deletedItems.size() ; i++){
            new DeleteObjRequest(deletedItems.get(i).getCodigo(),deletedItems.get(i).getFoto()).execute();
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(viewHolder instanceof RecyclerListAdapter.MyViewHolder){
            SortedList<Item> itemList = recyclerListAdapter.getSortedList();

            //get the removed item name to display it in snack bar
            String name = itemList.get(viewHolder.getAdapterPosition()).getNome();

            //backup of removed item for undo purpose
            final Item deletedItem = itemList.get(viewHolder.getAdapterPosition());

            //remove the item from recycler view
            recyclerListAdapter.removeItem(deletedItem);
            deletedItems.add(deletedItem);

            //showing snack bar with Undo option
            Snackbar snackbar = Snackbar.make(coordinatorLayout, name + " removido da lista", Snackbar.LENGTH_LONG);
            snackbar.setAction("DESFAZER", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //restore the deleted item
                    recyclerListAdapter.restoreItem(deletedItem);
                    deletedItems.remove(deletedItems.size()-1);
                }
            });
            snackbar.show();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final ArrayList<Item> filteredModelList = filter(ObjectListSingleton.getInstance().getObjectList(), newText);
        Log.e("tamanho filtrada",String.valueOf(filteredModelList.size()));
        recyclerListAdapter.replaceAll(filteredModelList);
        recyclerView.scrollToPosition(0);
        return true;
    }

    private static ArrayList<Item> filter(ArrayList<Item> models, String query) {
        final String lowerCaseQuery = query.toLowerCase();

        final ArrayList<Item> filteredModelList = new ArrayList<>();
        for (Item model : models) {
            final String text = model.getNome().toLowerCase();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }



}
