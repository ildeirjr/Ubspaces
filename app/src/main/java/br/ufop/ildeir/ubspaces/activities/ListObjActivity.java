package br.ufop.ildeir.ubspaces.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.ufop.ildeir.ubspaces.R;
import br.ufop.ildeir.ubspaces.adapters.RecyclerListAdapter;
import br.ufop.ildeir.ubspaces.listeners.OnLoadMoreListener;
import br.ufop.ildeir.ubspaces.miscellaneous.DateHandler;
import br.ufop.ildeir.ubspaces.miscellaneous.RecyclerItemTouchHelper;
import br.ufop.ildeir.ubspaces.miscellaneous.WrapContentLinearLayoutManager;
import br.ufop.ildeir.ubspaces.network.RetrofitConfig;
import br.ufop.ildeir.ubspaces.objects.Item;
import br.ufop.ildeir.ubspaces.objects.RecyclerViewItem;
import br.ufop.ildeir.ubspaces.singleton.ItemSingleton;
import br.ufop.ildeir.ubspaces.singleton.UserSingleton;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListObjActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, android.support.v7.widget.SearchView.OnQueryTextListener, SearchView.OnCloseListener, RecyclerListAdapter.MessageAdapterListener {

    private RecyclerView recyclerView;
    private RecyclerListAdapter recyclerListAdapter;
    private CoordinatorLayout coordinatorLayout;
    private ArrayList<RecyclerViewItem> deletedItems;
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;
    private int statusBarColor;

    private MenuItem searchItem;
    private TextView dateStart;
    private TextView dateEnd;
    private Calendar calendarStart = Calendar.getInstance();
    private Calendar calendarEnd = Calendar.getInstance();

    private FloatingActionButton fabDate;
    private List<RecyclerViewItem> itemList;
    private boolean isFabSeted = false;
    private boolean isNameFilterActivated = false;

    private ProgressBar progressBar;
    private ProgressDialog progressDialog;

    // variable to define how many items you want to load in RecyclerView at a time
    private final static int limit = 10;

    private int totalObjNum;

    private Call<ArrayList<RecyclerViewItem>> call;
    private Call<ArrayList<RecyclerViewItem>> searchNameCall;
    private Call<ArrayList<RecyclerViewItem>> searchDateCall;

    private CheckBox nameCheckbox;
    private CheckBox localCheckbox;
    private CheckBox dateCheckbox;
    private CheckBox stateCheckbox;
    private EditText etFilterName;
    private EditText etFilterBloco;
    private EditText etFilterSala;
    private EditText etFilterStartDate;
    private EditText etFilterEndDate;
    private Spinner unityFilterSpinner;
    private Spinner stateFilterSpinner;

    private static String[] STATE_SPINNER_OPTIONS = {"Normal","Quebrado","Consertado"};
    private static String[] UNIT_SPINNER_OPTIONS = {"Centro de Educação Aberta e a Distância (CEAD)",
            "Centro Desportivo da UFOP (CEDUFOP)",
            "Escola de Direito, Turismo e Museologia (EDTM)",
            "Escola de Farmácia",
            "Escola de Minas",
            "Escola de Medicina",
            "Escola de Nutrição",
            "Instituto de Ciências Exatas e Aplicadas (ICEA)",
            "Instituto de Ciências Exatas e Biológicas",
            "Instituto de Ciências Humanas e Sociais (ICHS)",
            "Instituto de Ciências Sociais Aplicadas (ICSA)",
            "Instituto de Filosofia, Arte e Cultura (IFAC)"};

    private static int DELETE_OBJ_REQUEST_CODE = 1;
    private static int SEARCH_OBJ_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_obj);

        Intent it = getIntent();
        totalObjNum = Integer.parseInt(it.getStringExtra("totalObjNum"));

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

        itemList = new ArrayList<>();

        deletedItems = new ArrayList<>();

        actionModeCallback = new ActionModeCallback();

        fabDate = findViewById(R.id.fabDateSearch);

        progressBar = findViewById(R.id.progress_bar);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Carregando");

        loadData();
    }

    public void loadData(){
        recyclerView.setVisibility(View.GONE);
        fabDate.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        Call<ArrayList<RecyclerViewItem>> call = new RetrofitConfig().getObjListRequest().getObjList("non_deleted","0",String.valueOf(limit));
        call.enqueue(new Callback<ArrayList<RecyclerViewItem>>() {
            @Override
            public void onResponse(Call<ArrayList<RecyclerViewItem>> call, Response<ArrayList<RecyclerViewItem>> response) {
                final ArrayList<RecyclerViewItem> itemsList = response.body();
                for(int i=0 ; i<itemsList.size() ; i++){
                    Call<ResponseBody> imgCall = new RetrofitConfig().getObjThumbRequest().getObjThumb(itemsList.get(i).getFoto());
                    final int finalI = i;
                    imgCall.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if(response.body() != null){
                                    itemsList.get(finalI).setImg(BitmapFactory.decodeStream(response.body().byteStream()));
                            } else {
                                call = new RetrofitConfig().getObjThumbRequest().getObjThumb("default.jpg");
                                call.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            itemsList.get(finalI).setImg(BitmapFactory.decodeStream(response.body().byteStream()));
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
                }
//                ObjectListSingleton.getInstance().setObjectList(new ArrayList<RecyclerViewItem>());
//                ObjectListSingleton.getInstance().getObjectList().addAll(itemsList);
                itemList.clear();
                itemList.addAll(itemsList);
                if(itemsList.size() == limit){
                    Log.e("teste","NULL");
                    itemList.add(null);
                }
                recyclerListAdapter = new RecyclerListAdapter(getApplicationContext(), (ArrayList<RecyclerViewItem>)  itemList, ListObjActivity.this, new OnLoadMoreListener() {
                    @Override
                    public void onLoadMore(int position) {
                        loadMoreData(position);
                    }
                });
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                progressBar.setVisibility(View.GONE);
                recyclerView.setAdapter(recyclerListAdapter);
                recyclerView.setVisibility(View.VISIBLE);
//                recyclerListAdapter.getItemListBackup().addAll(ObjectListSingleton.getInstance().getObjectList());
                fabDate.setVisibility(View.VISIBLE);
                searchItem.setVisible(true);
            }

            @Override
            public void onFailure(Call<ArrayList<RecyclerViewItem>> call, Throwable t) {

            }
        });
    }

    public void loadMoreData(int skip){
        Call<ArrayList<RecyclerViewItem>> call = new RetrofitConfig().getObjListRequest().getObjList("non_deleted",String.valueOf(skip),String.valueOf(limit));
        call.enqueue(new Callback<ArrayList<RecyclerViewItem>>() {
            @Override
            public void onResponse(Call<ArrayList<RecyclerViewItem>> call, Response<ArrayList<RecyclerViewItem>> response) {
                final ArrayList<RecyclerViewItem> itemsList = response.body();
                for(int i=0 ; i<itemsList.size() ; i++){
                    Call<ResponseBody> imgCall = new RetrofitConfig().getObjThumbRequest().getObjThumb(itemsList.get(i).getFoto());
                    final int finalI = i;
                    imgCall.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if(response.body() != null){
                                    itemsList.get(finalI).setImg(BitmapFactory.decodeStream(response.body().byteStream()));
                            } else {
                                call = new RetrofitConfig().getObjThumbRequest().getObjThumb("default.jpg");
                                call.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            itemsList.get(finalI).setImg(BitmapFactory.decodeStream(response.body().byteStream()));
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
                }
//                itemList.clear();
//                itemList.addAll(itemsList);
                if(itemsList.size() == limit){
                    itemsList.add(null);
                }

                recyclerListAdapter.removeLastItem();
                recyclerListAdapter.setLoaded();
                recyclerListAdapter.update(itemsList);

            }

            @Override
            public void onFailure(Call<ArrayList<RecyclerViewItem>> call, Throwable t) {

            }
        });
    }

//    public void loadDataName(final String query){
//        searchNameCall = new RetrofitConfig().searchByIdRequest().searchById(query, "non_deleted","0", String.valueOf(limit));
//        searchNameCall.enqueue(new Callback<ArrayList<RecyclerViewItem>>() {
//            @Override
//            public void onResponse(Call<ArrayList<RecyclerViewItem>> call, Response<ArrayList<RecyclerViewItem>> response) {
//                final ArrayList<RecyclerViewItem> itemsList = response.body();
//                for(int i=0 ; i<itemsList.size() ; i++){
//                    Call<ResponseBody> imgCall = new RetrofitConfig().getObjThumbRequest().getObjThumb(itemsList.get(i).getFoto());
//                    final int finalI = i;
//                    imgCall.enqueue(new Callback<ResponseBody>() {
//                        @Override
//                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                            if(response.body() != null){
//                                itemsList.get(finalI).setImg(BitmapFactory.decodeStream(response.body().byteStream()));
//                            } else {
//                                call = new RetrofitConfig().getObjThumbRequest().getObjThumb("default.jpg");
//                                call.enqueue(new Callback<ResponseBody>() {
//                                    @Override
//                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                                        itemsList.get(finalI).setImg(BitmapFactory.decodeStream(response.body().byteStream()));
//                                    }
//
//                                    @Override
//                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//                                    }
//                                });
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//                        }
//                    });
//                }
//                itemList.clear();
//                itemList.addAll(itemsList);
//                if(itemsList.size() == limit){
//                    Log.e("teste","NULL");
//                    itemList.add(null);
//                }
//                recyclerListAdapter = new RecyclerListAdapter(getApplicationContext(), (ArrayList<RecyclerViewItem>)  itemList, ListObjActivity.this, new OnLoadMoreListener() {
//                    @Override
//                    public void onLoadMore(int position) {
//                        loadMoreDataName(query, position);
//                    }
//                });
//                recyclerView.setItemAnimator(new DefaultItemAnimator());
//                recyclerView.setAdapter(recyclerListAdapter);
//                recyclerView.setVisibility(View.VISIBLE);
//                progressBar.setVisibility(View.GONE);
//                recyclerView.scrollToPosition(0);
//            }
//
//            @Override
//            public void onFailure(Call<ArrayList<RecyclerViewItem>> call, Throwable t) {
//
//            }
//        });
//    }
//
//    public void loadMoreDataName(String query, int skip){
//        Call<ArrayList<RecyclerViewItem>> call = new RetrofitConfig().searchByIdRequest().searchById(query, "non_deleted",String.valueOf(skip), String.valueOf(limit));
//        call.enqueue(new Callback<ArrayList<RecyclerViewItem>>() {
//            @Override
//            public void onResponse(Call<ArrayList<RecyclerViewItem>> call, Response<ArrayList<RecyclerViewItem>> response) {
//                final ArrayList<RecyclerViewItem> itemsList = response.body();
//                for(int i=0 ; i<itemsList.size() ; i++){
//                    Call<ResponseBody> imgCall = new RetrofitConfig().getObjThumbRequest().getObjThumb(itemsList.get(i).getFoto());
//                    final int finalI = i;
//                    imgCall.enqueue(new Callback<ResponseBody>() {
//                        @Override
//                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                            if(response.body() != null){
//                                itemsList.get(finalI).setImg(BitmapFactory.decodeStream(response.body().byteStream()));
//                            } else {
//                                call = new RetrofitConfig().getObjThumbRequest().getObjThumb("default.jpg");
//                                call.enqueue(new Callback<ResponseBody>() {
//                                    @Override
//                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                                        itemsList.get(finalI).setImg(BitmapFactory.decodeStream(response.body().byteStream()));
//                                    }
//
//                                    @Override
//                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//                                    }
//                                });
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//                        }
//                    });
//                }
////                itemList.clear();
////                itemList.addAll(itemsList);
//                if(itemsList.size() == limit){
//                    itemsList.add(null);
//                }
//
//                recyclerListAdapter.removeLastItem();
//                recyclerListAdapter.setLoaded();
//                recyclerListAdapter.update(itemsList);
//
//            }
//
//            @Override
//            public void onFailure(Call<ArrayList<RecyclerViewItem>> call, Throwable t) {
//
//            }
//        });
//    }

    public void loadDataDate(final String dateStart, final String dateEnd){
        searchDateCall = new RetrofitConfig().searchByDateRequest().searchByDate("non_deleted",dateStart,dateEnd,"0", String.valueOf(limit));
        searchDateCall.enqueue(new Callback<ArrayList<RecyclerViewItem>>() {
            @Override
            public void onResponse(Call<ArrayList<RecyclerViewItem>> call, Response<ArrayList<RecyclerViewItem>> response) {
                final ArrayList<RecyclerViewItem> filteredItems = response.body();
                for(int i=0 ; i<filteredItems.size() ; i++){
                    Call<ResponseBody> imgCall = new RetrofitConfig().getObjThumbRequest().getObjThumb(filteredItems.get(i).getFoto());
                    final int finalI = i;
                    imgCall.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if(response.body() != null){
                                filteredItems.get(finalI).setImg(BitmapFactory.decodeStream(response.body().byteStream()));
                            } else {
                                call = new RetrofitConfig().getObjThumbRequest().getObjThumb("default.jpg");
                                call.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        filteredItems.get(finalI).setImg(BitmapFactory.decodeStream(response.body().byteStream()));
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
                }
                itemList.clear();
                itemList.addAll(filteredItems);
                if(filteredItems.size() == limit){
                    Log.e("teste","NULL");
                    itemList.add(null);
                }
                recyclerListAdapter = new RecyclerListAdapter(getApplicationContext(), (ArrayList<RecyclerViewItem>)  itemList, ListObjActivity.this, new OnLoadMoreListener() {
                    @Override
                    public void onLoadMore(int position) {
                        loadMoreDataDate(dateStart, dateEnd, position);
                    }
                });
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(recyclerListAdapter);
                progressBar.setVisibility(View.GONE);
                Log.e("tamanho lista filtro", ""+filteredItems.size());
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<ArrayList<RecyclerViewItem>> call, Throwable t) {

            }
        });
    }

    public void loadMoreDataDate(String dateStart, String dateEnd, int skip){
        searchDateCall = new RetrofitConfig().searchByDateRequest().searchByDate("non_deleted",dateStart,dateEnd,String.valueOf(skip),String.valueOf(limit));
        searchDateCall.enqueue(new Callback<ArrayList<RecyclerViewItem>>() {
            @Override
            public void onResponse(Call<ArrayList<RecyclerViewItem>> call, Response<ArrayList<RecyclerViewItem>> response) {
                final ArrayList<RecyclerViewItem> itemsList = response.body();
                for(int i=0 ; i<itemsList.size() ; i++){
                    Call<ResponseBody> imgCall = new RetrofitConfig().getObjThumbRequest().getObjThumb(itemsList.get(i).getFoto());
                    final int finalI = i;
                    imgCall.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if(response.body() != null){
                                itemsList.get(finalI).setImg(BitmapFactory.decodeStream(response.body().byteStream()));
                            } else {
                                call = new RetrofitConfig().getObjThumbRequest().getObjThumb("default.jpg");
                                call.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        itemsList.get(finalI).setImg(BitmapFactory.decodeStream(response.body().byteStream()));
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
                }
//                itemList.clear();
//                itemList.addAll(itemsList);
                if(itemsList.size() == limit){
                    itemsList.add(null);
                }

                recyclerListAdapter.removeLastItem();
                recyclerListAdapter.setLoaded();
                recyclerListAdapter.update(itemsList);

            }

            @Override
            public void onFailure(Call<ArrayList<RecyclerViewItem>> call, Throwable t) {

            }
        });
    }

    public void loadFilteredData(final String jsonParams){
        call = new RetrofitConfig().getFilteredObjRequest().getFilteredObjects("non_deleted", "0", String.valueOf(limit), jsonParams.toString());
        call.enqueue(new Callback<ArrayList<RecyclerViewItem>>() {
            @Override
            public void onResponse(Call<ArrayList<RecyclerViewItem>> call, Response<ArrayList<RecyclerViewItem>> response) {
                final ArrayList<RecyclerViewItem> itemsList = response.body();
                for (int i = 0; i < itemsList.size(); i++) {
                    Call<ResponseBody> imgCall = new RetrofitConfig().getObjThumbRequest().getObjThumb(itemsList.get(i).getFoto());
                    final int finalI = i;
                    imgCall.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.body() != null) {
                                itemsList.get(finalI).setImg(BitmapFactory.decodeStream(response.body().byteStream()));
                            } else {
                                call = new RetrofitConfig().getObjThumbRequest().getObjThumb("default.jpg");
                                call.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        itemsList.get(finalI).setImg(BitmapFactory.decodeStream(response.body().byteStream()));
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
                }
                itemList.clear();
                itemList.addAll(itemsList);
                if(itemsList.size() == limit){
                    Log.e("teste","NULL");
                    itemList.add(null);
                }
                recyclerListAdapter = new RecyclerListAdapter(getApplicationContext(), (ArrayList<RecyclerViewItem>)  itemList, ListObjActivity.this, new OnLoadMoreListener() {
                    @Override
                    public void onLoadMore(int position) {
                        loadMoreFilteredData(jsonParams,position);
                    }
                });
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                progressBar.setVisibility(View.GONE);
                recyclerView.setAdapter(recyclerListAdapter);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<ArrayList<RecyclerViewItem>> call, Throwable t) {

            }
        });
    }

    public void loadMoreFilteredData(String jsonParams, int skip){
        searchDateCall = new RetrofitConfig().getFilteredObjRequest().getFilteredObjects("non_deleted", String.valueOf(skip), String.valueOf(limit), jsonParams.toString());
        searchDateCall.enqueue(new Callback<ArrayList<RecyclerViewItem>>() {
            @Override
            public void onResponse(Call<ArrayList<RecyclerViewItem>> call, Response<ArrayList<RecyclerViewItem>> response) {
                final ArrayList<RecyclerViewItem> itemsList = response.body();
                for(int i=0 ; i<itemsList.size() ; i++){
                    Call<ResponseBody> imgCall = new RetrofitConfig().getObjThumbRequest().getObjThumb(itemsList.get(i).getFoto());
                    final int finalI = i;
                    imgCall.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if(response.body() != null){
                                itemsList.get(finalI).setImg(BitmapFactory.decodeStream(response.body().byteStream()));
                            } else {
                                call = new RetrofitConfig().getObjThumbRequest().getObjThumb("default.jpg");
                                call.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        itemsList.get(finalI).setImg(BitmapFactory.decodeStream(response.body().byteStream()));
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
                }
//                itemList.clear();
//                itemList.addAll(itemsList);
                if(itemsList.size() == limit){
                    itemsList.add(null);
                }

                recyclerListAdapter.removeLastItem();
                recyclerListAdapter.setLoaded();
                recyclerListAdapter.update(itemsList);

            }

            @Override
            public void onFailure(Call<ArrayList<RecyclerViewItem>> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list_obj,menu);
        searchItem = menu.findItem(R.id.search_btn);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setInputType(InputType.TYPE_CLASS_NUMBER);
        searchView.setQueryHint("Pesquisar por código");
        searchItem.setVisible(false);

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                fabDate.setVisibility(View.GONE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if(searchNameCall != null){
                    if(searchNameCall.isExecuted()){
                        searchNameCall.cancel();
                    }
                }
                fabDate.setVisibility(View.VISIBLE);

//                List<RecyclerViewItem> backupListCopy = new ArrayList<RecyclerViewItem>();
//                backupListCopy.addAll(recyclerListAdapter.getItemListBackup());
//                recyclerListAdapter.setItemList(backupListCopy);
//                if(recyclerListAdapter.getItemList().size() < totalObjNum){
//                    recyclerListAdapter.getItemList().add(null);
//                }
//                recyclerListAdapter.notifyDataSetChanged();
//                if(progressBar.getVisibility() == View.VISIBLE){
//                    progressBar.setVisibility(View.GONE);
//                    recyclerView.setVisibility(View.VISIBLE);
//                }
//                fabDate.setVisibility(View.VISIBLE);
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
//        fabDate.setImageResource(R.drawable.ic_calendar);
//        fabDate.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
//        isFabSeted = false;
    }

    @Override
    public void onBackPressed() {
        if(isFabSeted){
            closeFilter();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        String deleteOperator = UserSingleton.getInstance().getNome();
        for (int i = 0; i < deletedItems.size(); i++) {
            Call<ResponseBody> call = new RetrofitConfig().deleteObjRequest().deleteObj(deletedItems.get(i).getCodigo(), deletedItems.get(i).getFoto(), deleteOperator);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, final int position) {
        if(viewHolder instanceof RecyclerListAdapter.MyViewHolder){
            List<RecyclerViewItem> itemList = recyclerListAdapter.getItemList();

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
                    recyclerListAdapter.restoreItem(deletedItem, position);
                    deletedItems.remove(deletedItems.size()-1);
                }
            });
            snackbar.show();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        progressDialog.show();
        final Intent showObject = new Intent(this, VisualizarObjActivity.class);
        final Intent objNotFound = new Intent(this, ObjNotFoundActivity.class);
        final Bundle bundle = new Bundle();
        Call<Item> call = new RetrofitConfig().searchByIdRequest().searchById("non_deleted",query);
        call.enqueue(new Callback<Item>() {
            @Override
            public void onResponse(Call<Item> call, Response<Item> response) {
                Item item = response.body();
                bundle.putString("codigo", item.getCodigo());
                bundle.putString("foto", item.getFoto());
                showObject.putExtras(bundle);
                startActivityForResult(showObject, SEARCH_OBJ_REQUEST_CODE);
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Item> call, Throwable t) {
                startActivity(objNotFound);
                progressDialog.dismiss();
            }
        });
//        recyclerView.setVisibility(View.GONE);
//        progressBar.setVisibility(View.VISIBLE);
//        fabDate.setVisibility(View.GONE);
//        loadDataName(query);
//        try {
//            final ArrayList<RecyclerViewItem> filteredModelList = new SearchObjByNameRequest().execute(query,"non_deleted").get();
//            if(filteredModelList != null){
//                for (int i=0 ; i<filteredModelList.size() ; i++){
//                    filteredModelList.get(i).setImg(new GetObjImgRequest(filteredModelList.get(i).getFoto()).execute().get());
//                }
//            }
//            recyclerListAdapter.replaceAll(filteredModelList);
//            recyclerListAdapter.getFilteredItemsByName().addAll(filteredModelList);
//            recyclerView.scrollToPosition(0);
//            return true;
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
        return true;
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
//            try {
//                Item item = new GetObjDataRequest(recyclerListAdapter.getItemList().get(position).getCodigo(),this).execute().get();
//                item.setImg(new GetObjImgRequest(recyclerListAdapter.getItemList().get(position).getFoto()).execute().get());
//                ItemSingleton.getInstance().setItemSingleton(item);
//                startActivity(new Intent(this,VisualizarObjActivity.class));
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            }
            Bundle bundle = new Bundle();
            bundle.putString("codigo", recyclerListAdapter.getItemList().get(position).getCodigo());
            bundle.putString("foto", recyclerListAdapter.getItemList().get(position).getFoto());
            bundle.putInt("index", position);
            Intent intent = new Intent(this, VisualizarObjActivity.class);
            intent.putExtras(bundle);
            startActivityForResult(intent, DELETE_OBJ_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == DELETE_OBJ_REQUEST_CODE && resultCode == RESULT_OK){
            if(data.getBooleanExtra("deleted",false)){
                recyclerListAdapter.removeItem(data.getIntExtra("index",0));
            } else {
                if (data.getBooleanExtra("edited", false)) {
                    Item itemSingleton = ItemSingleton.getInstance().getItemSingleton();
                    RecyclerViewItem recyclerViewItem = new RecyclerViewItem();
                    recyclerViewItem.setCodigo(itemSingleton.getCodigo());
                    recyclerViewItem.setDataEntrada(itemSingleton.getDataEntrada());
                    recyclerViewItem.setNome(itemSingleton.getNome());
                    recyclerViewItem.setFoto(itemSingleton.getFoto());

                    Bitmap bitmap = BitmapFactory.decodeByteArray(itemSingleton.getImg(), 0, itemSingleton.getImg().length);
                    bitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 0.1), (int) (bitmap.getHeight() * 0.1), true);
                    recyclerViewItem.setImg(bitmap);

                    int position = data.getIntExtra("index", 0);

                    recyclerListAdapter.getItemList().set(position, recyclerViewItem);
                    recyclerListAdapter.notifyItemChanged(position);
                }
            }
        } else if(requestCode == SEARCH_OBJ_REQUEST_CODE && resultCode == RESULT_OK){
            for(int i=0 ; i<recyclerListAdapter.getItemList().size() ; i++){
                if(recyclerListAdapter.getItemList().get(i).getCodigo().equals(data.getStringExtra("codigo"))){
                    if(data.getBooleanExtra("deleted",false)){
                        recyclerListAdapter.removeItem(i);
                    } else {
                        if (data.getBooleanExtra("edited", false)) {
                            Item itemSingleton = ItemSingleton.getInstance().getItemSingleton();
                            RecyclerViewItem recyclerViewItem = new RecyclerViewItem();
                            recyclerViewItem.setCodigo(itemSingleton.getCodigo());
                            recyclerViewItem.setDataEntrada(itemSingleton.getDataEntrada());
                            recyclerViewItem.setNome(itemSingleton.getNome());
                            recyclerViewItem.setFoto(itemSingleton.getFoto());

                            Bitmap bitmap = BitmapFactory.decodeByteArray(itemSingleton.getImg(), 0, itemSingleton.getImg().length);
                            bitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 0.1), (int) (bitmap.getHeight() * 0.1), true);
                            recyclerViewItem.setImg(bitmap);

                            recyclerListAdapter.getItemList().set(i, recyclerViewItem);
                            recyclerListAdapter.notifyItemChanged(i);
                        }
                    }
                    break;
                }
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
            final List<Integer> selectedItemPositions =
                    recyclerListAdapter.getSelectedItems();
            for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
                deletedItems.add(recyclerListAdapter.getItemList().get(selectedItemPositions.get(i)));
                recyclerListAdapter.removeItem(selectedItemPositions.get(i));
            }

            Snackbar snackbar = Snackbar.make(coordinatorLayout, selectedItemPositions.size() + " itens removidos.", Snackbar.LENGTH_LONG);
            snackbar.setAction("DESFAZER", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int j = deletedItems.size() - 1;
                    for(int i=0 ; i<selectedItemPositions.size() ; i++){
                        recyclerListAdapter.restoreItem(deletedItems.get(j), selectedItemPositions.get(i));
                        j--;
                    }
//                    for (RecyclerViewItem item : deletedItems) {
//                        recyclerListAdapter.restoreItem(item);
//                    }
                    deletedItems.clear();
                }
            });
            snackbar.show();

            recyclerListAdapter.notifyDataSetChanged();
    }

    public void filter(View view){
        if(!isFabSeted){
            LayoutInflater layoutInflater = getLayoutInflater();
            View v = layoutInflater.inflate(R.layout.filter_dialog, null);
            final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            final JSONObject jsonObject = new JSONObject();

            nameCheckbox = v.findViewById(R.id.nameCheckbox);
            localCheckbox = v.findViewById(R.id.localCheckbox);
            dateCheckbox = v.findViewById(R.id.dateCheckbox);
            stateCheckbox = v.findViewById(R.id.stateCheckbox);
            etFilterName = v.findViewById(R.id.etFilterName);
            etFilterBloco = v.findViewById(R.id.etFilterBloco);
            etFilterSala = v.findViewById(R.id.etFilterSala);
            etFilterStartDate = v.findViewById(R.id.etFilterStartDate);
            etFilterEndDate = v.findViewById(R.id.etFilterEndDate);
            unityFilterSpinner = v.findViewById(R.id.unityFilterSpinner);
            stateFilterSpinner = v.findViewById(R.id.stateFilterSpinner);

            ArrayAdapter<String> unityFilterAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, UNIT_SPINNER_OPTIONS);
            unityFilterSpinner.setAdapter(unityFilterAdapter);

            ArrayAdapter<String> stateFilterAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, STATE_SPINNER_OPTIONS);
            stateFilterSpinner.setAdapter(stateFilterAdapter);

            final LinearLayout nameLayout = v.findViewById(R.id.nameLayout);
            final LinearLayout localLayout = v.findViewById(R.id.localLayout);
            final LinearLayout dateLayout = v.findViewById(R.id.dateLayout);
            final LinearLayout stateLayout = v.findViewById(R.id.stateLayout);

            etFilterStartDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            calendarStart.set(Calendar.YEAR, i);
                            calendarStart.set(Calendar.MONTH, i1);
                            calendarStart.set(Calendar.DAY_OF_MONTH, i2);
                            etFilterStartDate.setText(dateFormat.format(calendarStart.getTime()));
                        }
                    }, calendarStart.get(Calendar.YEAR), calendarStart.get(Calendar.MONTH), calendarStart.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.show();
                }
            });

            etFilterEndDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            calendarEnd.set(Calendar.YEAR, i);
                            calendarEnd.set(Calendar.MONTH, i1);
                            calendarEnd.set(Calendar.DAY_OF_MONTH, i2);
                            etFilterEndDate.setText(dateFormat.format(calendarEnd.getTime()));
                        }
                    }, calendarEnd.get(Calendar.YEAR), calendarEnd.get(Calendar.MONTH), calendarEnd.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.show();
                }
            });

            nameCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(nameCheckbox.isChecked()){
                        nameLayout.setVisibility(View.VISIBLE);
                    } else {
                        nameLayout.setVisibility(View.GONE);
                    }
                }
            });

            localCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(localCheckbox.isChecked()){
                        localLayout.setVisibility(View.VISIBLE);
                    } else {
                        localLayout.setVisibility(View.GONE);
                    }
                }
            });

            dateCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(dateCheckbox.isChecked()){
                        dateLayout.setVisibility(View.VISIBLE);
                    } else {
                        dateLayout.setVisibility(View.GONE);
                    }
                }
            });

            stateCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(stateCheckbox.isChecked()){
                        stateLayout.setVisibility(View.VISIBLE);
                    } else {
                        stateLayout.setVisibility(View.GONE);
                    }
                }
            });

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Filtrar objetos por");
            builder.setPositiveButton("OK", null);
            builder.setNegativeButton("CANCELAR", null);
            builder.setView(v);
            final AlertDialog alertDialog = builder.create();
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    Button button = ((AlertDialog) alertDialog).getButton(AlertDialog.BUTTON_POSITIVE);
                    button.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            if(!nameCheckbox.isChecked() && !localCheckbox.isChecked() && !dateCheckbox.isChecked() && !stateCheckbox.isChecked()){
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(ListObjActivity.this);
                                builder1.setMessage("Selecione pelo menos uma opção de filtro");
                                builder1.setPositiveButton("OK", null);
                                AlertDialog dialog = builder1.create();
                                dialog.show();
                            } else {
                                fabDate.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.dark_red)));
                                fabDate.setImageResource(R.drawable.ic_close);
                                isFabSeted = true;

                                searchItem.setVisible(false);
                                recyclerView.setVisibility(View.GONE);
                                progressBar.setVisibility(View.VISIBLE);
                                searchItem.setVisible(false);
                                try {
                                    if (!nameCheckbox.isChecked() && !localCheckbox.isChecked() && !dateCheckbox.isChecked() && stateCheckbox.isChecked()) {
                                        jsonObject.put("name", "");
                                        jsonObject.put("unity", "");
                                        jsonObject.put("bloco", "");
                                        jsonObject.put("sala", "");
                                        jsonObject.put("start_date", "");
                                        jsonObject.put("end_date", "");
                                        jsonObject.put("state", stateFilterSpinner.getSelectedItem().toString());
                                    } else if (!nameCheckbox.isChecked() && !localCheckbox.isChecked() && dateCheckbox.isChecked() && !stateCheckbox.isChecked()) {
                                        jsonObject.put("name", "");
                                        jsonObject.put("unity", "");
                                        jsonObject.put("bloco", "");
                                        jsonObject.put("sala", "");
                                        jsonObject.put("start_date", DateHandler.toSqlDate(calendarStart.getTime()));
                                        jsonObject.put("end_date", DateHandler.toSqlDate(calendarEnd.getTime()));
                                        jsonObject.put("state", "");
                                    } else if (!nameCheckbox.isChecked() && !localCheckbox.isChecked() && dateCheckbox.isChecked() && stateCheckbox.isChecked()) {
                                        jsonObject.put("name", "");
                                        jsonObject.put("unity", "");
                                        jsonObject.put("bloco", "");
                                        jsonObject.put("sala", "");
                                        jsonObject.put("start_date", DateHandler.toSqlDate(calendarStart.getTime()));
                                        jsonObject.put("end_date", DateHandler.toSqlDate(calendarEnd.getTime()));
                                        jsonObject.put("state", stateFilterSpinner.getSelectedItem().toString());
                                    } else if (!nameCheckbox.isChecked() && localCheckbox.isChecked() && !dateCheckbox.isChecked() && !stateCheckbox.isChecked()) {
                                        jsonObject.put("name", "");
                                        jsonObject.put("unity", unityFilterSpinner.getSelectedItem().toString());
                                        jsonObject.put("bloco", etFilterBloco.getText().toString());
                                        jsonObject.put("sala", etFilterSala.getText().toString());
                                        jsonObject.put("start_date", "");
                                        jsonObject.put("end_date", "");
                                        jsonObject.put("state", "");
                                    } else if (!nameCheckbox.isChecked() && localCheckbox.isChecked() && !dateCheckbox.isChecked() && stateCheckbox.isChecked()) {
                                        jsonObject.put("name", "");
                                        jsonObject.put("unity", unityFilterSpinner.getSelectedItem().toString());
                                        jsonObject.put("bloco", etFilterBloco.getText().toString());
                                        jsonObject.put("sala", etFilterSala.getText().toString());
                                        jsonObject.put("start_date", "");
                                        jsonObject.put("end_date", "");
                                        jsonObject.put("state", stateFilterSpinner.getSelectedItem().toString());
                                    } else if (!nameCheckbox.isChecked() && localCheckbox.isChecked() && dateCheckbox.isChecked() && !stateCheckbox.isChecked()) {
                                        jsonObject.put("name", "");
                                        jsonObject.put("unity", unityFilterSpinner.getSelectedItem().toString());
                                        jsonObject.put("bloco", etFilterBloco.getText().toString());
                                        jsonObject.put("sala", etFilterSala.getText().toString());
                                        jsonObject.put("start_date", DateHandler.toSqlDate(calendarStart.getTime()));
                                        jsonObject.put("end_date", DateHandler.toSqlDate(calendarEnd.getTime()));
                                        jsonObject.put("state", "");
                                    } else if (!nameCheckbox.isChecked() && localCheckbox.isChecked() && dateCheckbox.isChecked() && stateCheckbox.isChecked()) {
                                        jsonObject.put("name", "");
                                        jsonObject.put("unity", unityFilterSpinner.getSelectedItem().toString());
                                        jsonObject.put("bloco", etFilterBloco.getText().toString());
                                        jsonObject.put("sala", etFilterSala.getText().toString());
                                        jsonObject.put("start_date", DateHandler.toSqlDate(calendarStart.getTime()));
                                        jsonObject.put("end_date", DateHandler.toSqlDate(calendarEnd.getTime()));
                                        jsonObject.put("state", stateFilterSpinner.getSelectedItem().toString());
                                    } else if (nameCheckbox.isChecked() && !localCheckbox.isChecked() && !dateCheckbox.isChecked() && !stateCheckbox.isChecked()) {
                                        jsonObject.put("name", etFilterName.getText().toString());
                                        jsonObject.put("unity", "");
                                        jsonObject.put("bloco", "");
                                        jsonObject.put("sala", "");
                                        jsonObject.put("start_date", "");
                                        jsonObject.put("end_date", "");
                                        jsonObject.put("state", "");
                                    } else if (nameCheckbox.isChecked() && !localCheckbox.isChecked() && !dateCheckbox.isChecked() && stateCheckbox.isChecked()) {
                                        jsonObject.put("name", etFilterName.getText().toString());
                                        jsonObject.put("unity", "");
                                        jsonObject.put("bloco", "");
                                        jsonObject.put("sala", "");
                                        jsonObject.put("start_date", "");
                                        jsonObject.put("end_date", "");
                                        jsonObject.put("state", stateFilterSpinner.getSelectedItem().toString());
                                    } else if (nameCheckbox.isChecked() && !localCheckbox.isChecked() && dateCheckbox.isChecked() && !stateCheckbox.isChecked()) {
                                        jsonObject.put("name", etFilterName.getText().toString());
                                        jsonObject.put("unity", "");
                                        jsonObject.put("bloco", "");
                                        jsonObject.put("sala", "");
                                        jsonObject.put("start_date", DateHandler.toSqlDate(calendarStart.getTime()));
                                        jsonObject.put("end_date", DateHandler.toSqlDate(calendarEnd.getTime()));
                                        jsonObject.put("state", "");
                                    } else if (nameCheckbox.isChecked() && !localCheckbox.isChecked() && dateCheckbox.isChecked() && stateCheckbox.isChecked()) {
                                        jsonObject.put("name", etFilterName.getText().toString());
                                        jsonObject.put("unity", "");
                                        jsonObject.put("bloco", "");
                                        jsonObject.put("sala", "");
                                        jsonObject.put("start_date", DateHandler.toSqlDate(calendarStart.getTime()));
                                        jsonObject.put("end_date", DateHandler.toSqlDate(calendarEnd.getTime()));
                                        jsonObject.put("state", stateFilterSpinner.getSelectedItem().toString());
                                    } else if (nameCheckbox.isChecked() && localCheckbox.isChecked() && !dateCheckbox.isChecked() && !stateCheckbox.isChecked()) {
                                        jsonObject.put("name", etFilterName.getText().toString());
                                        jsonObject.put("unity", unityFilterSpinner.getSelectedItem().toString());
                                        jsonObject.put("bloco", etFilterBloco.getText().toString());
                                        jsonObject.put("sala", etFilterSala.getText().toString());
                                        jsonObject.put("start_date", "");
                                        jsonObject.put("end_date", "");
                                        jsonObject.put("state", "");
                                    } else if (nameCheckbox.isChecked() && localCheckbox.isChecked() && !dateCheckbox.isChecked() && stateCheckbox.isChecked()) {
                                        jsonObject.put("name", etFilterName.getText().toString());
                                        jsonObject.put("unity", unityFilterSpinner.getSelectedItem().toString());
                                        jsonObject.put("bloco", etFilterBloco.getText().toString());
                                        jsonObject.put("sala", etFilterSala.getText().toString());
                                        jsonObject.put("start_date", "");
                                        jsonObject.put("end_date", "");
                                        jsonObject.put("state", stateFilterSpinner.getSelectedItem().toString());
                                    } else if (nameCheckbox.isChecked() && localCheckbox.isChecked() && dateCheckbox.isChecked() && !stateCheckbox.isChecked()) {
                                        jsonObject.put("name", etFilterName.getText().toString());
                                        jsonObject.put("unity", unityFilterSpinner.getSelectedItem().toString());
                                        jsonObject.put("bloco", etFilterBloco.getText().toString());
                                        jsonObject.put("sala", etFilterSala.getText().toString());
                                        jsonObject.put("start_date", DateHandler.toSqlDate(calendarStart.getTime()));
                                        jsonObject.put("end_date", DateHandler.toSqlDate(calendarEnd.getTime()));
                                        jsonObject.put("state", "");
                                    } else if (nameCheckbox.isChecked() && localCheckbox.isChecked() && dateCheckbox.isChecked() && stateCheckbox.isChecked()) {
                                        jsonObject.put("name", etFilterName.getText().toString());
                                        jsonObject.put("unity", unityFilterSpinner.getSelectedItem().toString());
                                        jsonObject.put("bloco", etFilterBloco.getText().toString());
                                        jsonObject.put("sala", etFilterSala.getText().toString());
                                        jsonObject.put("start_date", DateHandler.toSqlDate(calendarStart.getTime()));
                                        jsonObject.put("end_date", DateHandler.toSqlDate(calendarEnd.getTime()));
                                        jsonObject.put("state", stateFilterSpinner.getSelectedItem().toString());
                                    } else {

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                Log.e("data", jsonObject.toString());
                                loadFilteredData(jsonObject.toString());
                                alertDialog.dismiss();
                            }
                        }
                    });
                }
            });
            alertDialog.show();
        } else {
            closeFilter();
        }
    }

    public void closeFilter(){
        if(call.isExecuted()){
            call.cancel();
        }
        fabDate.setImageResource(R.drawable.ic_filter);
        fabDate.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        isFabSeted = false;
        searchItem.setVisible(true);
        loadData();
    }

//    public void filterDate(View view) {
//        if (!isFabSeted) {
//            LayoutInflater layoutInflater = getLayoutInflater();
//            View v = layoutInflater.inflate(R.layout.date_dialog, null);
//
//            final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//
//            dateStart = v.findViewById(R.id.startDate);
//            dateEnd = v.findViewById(R.id.endDate);
//            dateStart.setText(dateFormat.format(calendarStart.getTime()));
//            dateEnd.setText(dateFormat.format(calendarEnd.getTime()));
//
//            dateStart.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
//                        @Override
//                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
//                            calendarStart.set(Calendar.YEAR, i);
//                            calendarStart.set(Calendar.MONTH, i1);
//                            calendarStart.set(Calendar.DAY_OF_MONTH, i2);
//                            dateStart.setText(dateFormat.format(calendarStart.getTime()));
//                        }
//                    }, calendarStart.get(Calendar.YEAR), calendarStart.get(Calendar.MONTH), calendarStart.get(Calendar.DAY_OF_MONTH));
//                    datePickerDialog.show();
//                }
//            });
//
//            dateEnd.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
//                        @Override
//                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
//                            calendarEnd.set(Calendar.YEAR, i);
//                            calendarEnd.set(Calendar.MONTH, i1);
//                            calendarEnd.set(Calendar.DAY_OF_MONTH, i2);
//                            dateEnd.setText(dateFormat.format(calendarEnd.getTime()));
//                        }
//                    }, calendarEnd.get(Calendar.YEAR), calendarEnd.get(Calendar.MONTH), calendarEnd.get(Calendar.DAY_OF_MONTH));
//                    datePickerDialog.show();
//                }
//            });
//
//            AlertDialog alertDialog;
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("Pesquisar por data");
//            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    fabDate.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.dark_red)));
//                    fabDate.setImageResource(R.drawable.ic_close);
//                    isFabSeted = true;
//
//                    String dateStart = DateHandler.toSqlDate(calendarStart.getTime());
//                    String dateEnd = DateHandler.toSqlDate(calendarEnd.getTime());
//
//                    recyclerView.setVisibility(View.GONE);
//                    progressBar.setVisibility(View.VISIBLE);
//                    searchItem.setVisible(false);
//
//                    loadDataDate(dateStart,dateEnd);
//
//
//                }
//            });
//            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                }
//            });
//            builder.setView(v);
//            alertDialog = builder.create();
//            alertDialog.show();
//        } else {
//            if(searchDateCall.isExecuted()){
//                searchDateCall.cancel();
//            }
////            recyclerListAdapter.replaceAll((ArrayList<RecyclerViewItem>) recyclerListAdapter.getItemListBackup());
////            if(recyclerListAdapter.getItemList().size() < totalObjNum){
////                recyclerListAdapter.getItemList().add(null);
////            }
////            recyclerListAdapter.getFilteredItemsByDate().clear();
//            fabDate.setImageResource(R.drawable.ic_calendar);
//            fabDate.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
//            searchItem.setVisible(true);
//            isFabSeted = false;
////            if(recyclerView.getVisibility() == View.GONE){
////                progressBar.setVisibility(View.GONE);
////                recyclerView.setVisibility(View.VISIBLE);
////            }
//            loadData();
//        }
//    }


}
