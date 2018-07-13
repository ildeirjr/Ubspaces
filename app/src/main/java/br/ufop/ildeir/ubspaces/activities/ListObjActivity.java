package br.ufop.ildeir.ubspaces.activities;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.util.SortedList;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import br.ufop.ildeir.ubspaces.R;
import br.ufop.ildeir.ubspaces.adapters.RecyclerListAdapter;
import br.ufop.ildeir.ubspaces.miscellaneous.RecyclerItemTouchHelper;
import br.ufop.ildeir.ubspaces.miscellaneous.WrapContentLinearLayoutManager;
import br.ufop.ildeir.ubspaces.objects.Item;
import br.ufop.ildeir.ubspaces.objects.RecyclerViewItem;
import br.ufop.ildeir.ubspaces.requests.delete.DeleteObjRequest;
import br.ufop.ildeir.ubspaces.requests.get.GetAllObjRequest;
import br.ufop.ildeir.ubspaces.requests.get.GetObjDataRequest;
import br.ufop.ildeir.ubspaces.requests.get.GetObjImgRequest;
import br.ufop.ildeir.ubspaces.requests.get.GetObjThumbRequest;
import br.ufop.ildeir.ubspaces.requests.get.GetUserRequest;
import br.ufop.ildeir.ubspaces.requests.get.SearchObjByNameRequest;
import br.ufop.ildeir.ubspaces.singleton.ItemSingleton;
import br.ufop.ildeir.ubspaces.singleton.ObjectListSingleton;
import br.ufop.ildeir.ubspaces.singleton.SessionManager;

public class ListObjActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, android.support.v7.widget.SearchView.OnQueryTextListener, SearchView.OnCloseListener, RecyclerListAdapter.MessageAdapterListener {

    private RecyclerView recyclerView;
    private RecyclerListAdapter recyclerListAdapter;
    private CoordinatorLayout coordinatorLayout;
    private ArrayList<RecyclerViewItem> deletedItems;
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;
    private int statusBarColor;

    private TextView dateStart;
    private TextView dateEnd;
    private Calendar calendarStart = Calendar.getInstance();
    private Calendar calendarEnd = Calendar.getInstance();

    private FloatingActionButton fabDate;
    private SortedList<RecyclerViewItem> itemList;
    private boolean isFabSeted = false;
    private boolean isNameFilterActivated = false;

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

        deletedItems = new ArrayList<>();

        actionModeCallback = new ActionModeCallback();

        fabDate = findViewById(R.id.fabDateSearch);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list_obj,menu);
        final MenuItem searchItem = menu.findItem(R.id.search_btn);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                recyclerListAdapter.getSortedList().clear();
                for(int k=0 ; k<recyclerListAdapter.getSortedListBackup().size() ; k++){
                    recyclerListAdapter.restoreItem(recyclerListAdapter.getSortedListBackup().get(k));
                }
                return true;
            }
        });

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
            ArrayList<RecyclerViewItem> itemsArrayList = new GetAllObjRequest().execute().get();
            if(itemsArrayList != null) {
                Log.e("tamanho do array", String.valueOf(itemsArrayList.size()));
                for (int i = 0; i < itemsArrayList.size(); i++) {
                    itemsArrayList.get(i).setImg(new GetObjImgRequest(itemsArrayList.get(i).getFoto()).execute().get());
                }
                ObjectListSingleton.getInstance().setObjectList(itemsArrayList);
                recyclerListAdapter = new RecyclerListAdapter(this, ObjectListSingleton.getInstance().getObjectList(), this);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(recyclerListAdapter);

                fabDate.setImageResource(R.drawable.ic_calendar);
                fabDate.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                isFabSeted = false;

                for (int k = 0; k < recyclerListAdapter.getSortedList().size(); k++) {
                    recyclerListAdapter.getSortedListBackup().add(recyclerListAdapter.getSortedList().get(k));
                }
            }else{
                Toast.makeText(this, R.string.invalid_operator, Toast.LENGTH_SHORT).show();
                finish();
            }

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
        String user = null;
        try {
            user = new GetUserRequest(SessionManager.getInstance().getUserId()).execute().get();
            if(user.equals("401")){
                Toast.makeText(this, R.string.invalid_operator, Toast.LENGTH_SHORT).show();
                SessionManager.getInstance().toLoginActivity();
                finish();
            } else {
                for (int i = 0; i < deletedItems.size(); i++) {
                    new DeleteObjRequest(deletedItems.get(i).getCodigo(), deletedItems.get(i).getFoto()).execute();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(viewHolder instanceof RecyclerListAdapter.MyViewHolder){
            SortedList<RecyclerViewItem> itemList = recyclerListAdapter.getSortedList();

            //get the removed item name to display it in snack bar
            String name = itemList.get(viewHolder.getAdapterPosition()).getNome();

            //backup of removed item for undo purpose
            final RecyclerViewItem deletedItem = itemList.get(viewHolder.getAdapterPosition());

            //remove the item from recycler view
            recyclerListAdapter.removeItem(viewHolder.getAdapterPosition());
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
        try {
            final ArrayList<RecyclerViewItem> filteredModelList = new SearchObjByNameRequest().execute(query).get();
            if(filteredModelList != null){
                for (int i=0 ; i<filteredModelList.size() ; i++){
                    filteredModelList.get(i).setImg(new GetObjImgRequest(filteredModelList.get(i).getFoto()).execute().get());
                }
            }
            recyclerListAdapter.replaceAll(filteredModelList);
            recyclerListAdapter.getFilteredItemsByName().addAll(filteredModelList);
            recyclerView.scrollToPosition(0);
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
//        isNameFilterActivated = true;
//        recyclerListAdapter.getFilteredItemsByName().clear();
//        if(isFabSeted){
//            final ArrayList<Item> filteredModelList = filter(recyclerListAdapter.getFilteredItemsByDate(), newText);
//            Log.e("tamanho filtrada",String.valueOf(filteredModelList.size()));
//            recyclerListAdapter.replaceAll(filteredModelList);
//            recyclerListAdapter.getFilteredItemsByName().addAll(filteredModelList);
//            recyclerView.scrollToPosition(0);
//            return true;
//        } else{
//            final ArrayList<Item> filteredModelList = filter(recyclerListAdapter.getSortedListBackup(), newText);
//            Log.e("tamanho filtrada",String.valueOf(filteredModelList.size()));
//            recyclerListAdapter.replaceAll(filteredModelList);
//            recyclerListAdapter.getFilteredItemsByName().addAll(filteredModelList);
//            recyclerView.scrollToPosition(0);
//            return true;
//        }
        return true;
    }

//    private static ArrayList<Item> filter(SortedList<Item> models, String query) {
//        final String lowerCaseQuery = query.toLowerCase();
//
//        final ArrayList<Item> filteredModelList = new ArrayList<>();
//        for (int i=0 ; i<models.size() ; i++) {
//            final Item item = models.get(i);
//            final String text = item.getNome().toLowerCase();
//            if (text.contains(lowerCaseQuery)) {
//                filteredModelList.add(item);
//            }
//        }
//        return filteredModelList;
//    }


    @Override
    public void onItemRowClicked(int position) {
        if(recyclerListAdapter.getSelectedItemCount() > 0){
            enableActionMode(position);
        }else{
            try {
                Item item = new GetObjDataRequest(recyclerListAdapter.getSortedList().get(position).getCodigo(),this).execute().get();
                item.setImg(new GetObjImgRequest(recyclerListAdapter.getSortedList().get(position).getFoto()).execute().get());
                ItemSingleton.getInstance().setItemSingleton(item);
                startActivity(new Intent(this,VisualizarObjActivity.class));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onIconClicked(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }

        toggleSelection(position);
    }

    @Override
    public void onRowLongClicked(int position) {// long press is performed, enable action mode
        enableActionMode(position);
    }

    private void enableActionMode(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        recyclerListAdapter.toggleSelection(position);
        int count = recyclerListAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    @Override
    public boolean onClose() {
        isNameFilterActivated = false;
        Log.e("pesquisa","FECHOU A PESQUISA");
        return true;
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                statusBarColor = getWindow().getStatusBarColor();
                getWindow().setStatusBarColor(getResources().getColor(R.color.bg_action_mode_statusbar));
            }
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    deleteItems();
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            recyclerListAdapter.clearSelections();
            actionMode = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(statusBarColor);
            }
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    recyclerListAdapter.resetAnimationIndex();
                    // mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    // deleting the messages from recycler view
    private void deleteItems() {
            recyclerListAdapter.resetAnimationIndex();
            List<Integer> selectedItemPositions =
                    recyclerListAdapter.getSelectedItems();
            for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
                deletedItems.add(recyclerListAdapter.getSortedList().get(selectedItemPositions.get(i)));
                recyclerListAdapter.removeItem(selectedItemPositions.get(i));
            }

            Snackbar snackbar = Snackbar.make(coordinatorLayout, selectedItemPositions.size() + " itens removidos.", Snackbar.LENGTH_LONG);
            snackbar.setAction("DESFAZER", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (RecyclerViewItem item : deletedItems) {
                        recyclerListAdapter.restoreItem(item);
                    }
                    deletedItems.clear();
                }
            });
            snackbar.show();

            recyclerListAdapter.notifyDataSetChanged();
        }

    public void filterDate(View view) {
        if (!isFabSeted) {
            LayoutInflater layoutInflater = getLayoutInflater();
            View v = layoutInflater.inflate(R.layout.date_dialog, null);

            final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            dateStart = v.findViewById(R.id.startDate);
            dateEnd = v.findViewById(R.id.endDate);
            dateStart.setText(dateFormat.format(calendarStart.getTime()));
            dateEnd.setText(dateFormat.format(calendarEnd.getTime()));

            dateStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            calendarStart.set(Calendar.YEAR, i);
                            calendarStart.set(Calendar.MONTH, i1);
                            calendarStart.set(Calendar.DAY_OF_MONTH, i2);
                            dateStart.setText(dateFormat.format(calendarStart.getTime()));
                        }
                    }, calendarStart.get(Calendar.YEAR), calendarStart.get(Calendar.MONTH), calendarStart.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.show();
                }
            });

            dateEnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            calendarEnd.set(Calendar.YEAR, i);
                            calendarEnd.set(Calendar.MONTH, i1);
                            calendarEnd.set(Calendar.DAY_OF_MONTH, i2);
                            dateEnd.setText(dateFormat.format(calendarEnd.getTime()));
                        }
                    }, calendarEnd.get(Calendar.YEAR), calendarEnd.get(Calendar.MONTH), calendarEnd.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.show();
                }
            });

            AlertDialog alertDialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Pesquisar por data");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ArrayList<RecyclerViewItem> filteredItems = new ArrayList<>();
                    Item dateStart = new Item();
                    Item dateEnd = new Item();
                    dateStart.setDia(calendarStart.get(Calendar.DAY_OF_MONTH));
                    dateStart.setMes(calendarStart.get(Calendar.MONTH) + 1);
                    dateStart.setAno(calendarStart.get(Calendar.YEAR));
                    dateEnd.setDia(calendarEnd.get(Calendar.DAY_OF_MONTH));
                    dateEnd.setMes(calendarEnd.get(Calendar.MONTH) + 1);
                    dateEnd.setAno(calendarEnd.get(Calendar.YEAR));
                    itemList = recyclerListAdapter.getSortedListBackup();
                    for (int k = 0; k < itemList.size(); k++) {
                        Log.e("data Inicio",dateStart.getDia() + "/" + dateStart.getMes() + "/" + dateStart.getAno());
                        Log.e("data Item",itemList.get(k).getDia() + "/" + itemList.get(k).getMes() + "/" + itemList.get(k).getAno());
                        Log.e("data Final",dateEnd.getDia() + "/" + dateEnd.getMes() + "/" + dateEnd.getAno());
                        if ((itemList.get(k).compareItemDate(dateStart) >= 0) && (itemList.get(k).compareItemDate(dateEnd) <= 0)) {
                            Log.e("if",""+itemList.get(k).compareItemDate(dateStart) + " - " + itemList.get(k).compareItemDate(dateEnd));
                            filteredItems.add(itemList.get(k));
                            recyclerListAdapter.getFilteredItemsByDate().add(itemList.get(k));
                        }
                    }
                    fabDate.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.dark_red)));
                    fabDate.setImageResource(R.drawable.ic_close);
                    isFabSeted = true;
                    Log.e("tamanho lista filtro", ""+filteredItems.size());
                    recyclerListAdapter.replaceAll(filteredItems);
                }
            });
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            builder.setView(v);
            alertDialog = builder.create();
            alertDialog.show();
        } else {
            recyclerListAdapter.getSortedList().clear();
            for(int k=0 ; k<recyclerListAdapter.getSortedListBackup().size() ; k++){
                recyclerListAdapter.restoreItem(recyclerListAdapter.getSortedListBackup().get(k));
            }
            recyclerListAdapter.getFilteredItemsByDate().clear();
            fabDate.setImageResource(R.drawable.ic_calendar);
            fabDate.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
            isFabSeted = false;
        }
    }


}
