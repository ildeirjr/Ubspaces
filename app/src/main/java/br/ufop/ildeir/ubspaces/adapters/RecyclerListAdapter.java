package br.ufop.ildeir.ubspaces.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import br.ufop.ildeir.ubspaces.R;
import br.ufop.ildeir.ubspaces.activities.VisualizarObjActivity;
import br.ufop.ildeir.ubspaces.objects.Item;
import br.ufop.ildeir.ubspaces.singleton.ItemSingleton;

/**
 * Created by Ildeir on 15/06/2018.
 */

public class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListAdapter.MyViewHolder> {

    private final SortedList<Item> sortedList = new SortedList<Item>(Item.class, new SortedList.Callback<Item>() {
        @Override
        public int compare(Item o1, Item o2) {
            return o1.getNome().compareTo(o2.getNome());
        }

        @Override
        public void onChanged(int position, int count) {
            notifyItemRangeChanged(position, count);
        }

        @Override
        public boolean areContentsTheSame(Item oldItem, Item newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(Item item1, Item item2) {
            return item1.getCodigo() == item2.getCodigo();
        }

        @Override
        public void onInserted(int position, int count) {
            notifyItemRangeInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            notifyItemRangeRemoved(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            notifyItemMoved(fromPosition, toPosition);
        }
    });

    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName, itemCode;
        public ImageView itemThumbnail;
        public RelativeLayout viewBackground;
        public LinearLayout viewForeground;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.objectName);
            itemCode = itemView.findViewById(R.id.objectCode);
            itemThumbnail = itemView.findViewById(R.id.objectImg);
            viewBackground = itemView.findViewById(R.id.item_list_background);
            viewForeground = itemView.findViewById(R.id.item_list_foreground);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ItemSingleton.getInstance().setItemSingleton(sortedList.get(getAdapterPosition()));
                    context.startActivity(new Intent(view.getContext(),VisualizarObjActivity.class));
                }
            });

        }
    }


    public RecyclerListAdapter(Context context, ArrayList<Item> itemList) {
        this.context = context;
        sortedList.addAll(itemList);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.object_list_adapter,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Item item = sortedList.get(position);
        holder.itemName.setText(item.getNome());
        holder.itemCode.setText("Código: " + item.getCodigo());
        if(item.getFoto().equals("null.jpg")){
            holder.itemThumbnail.setImageResource( R.drawable.ic_camera);
        }else holder.itemThumbnail.setImageBitmap(item.createImgThumb());
    }

    @Override
    public int getItemCount() {
        return sortedList.size();
    }

    public void removeItem(Item item){
        sortedList.remove(item);
    }

    public void restoreItem(Item item){
        sortedList.add(item);
    }

    public void replaceAll(ArrayList<Item> models) {
        sortedList.beginBatchedUpdates();
        sortedList.clear();
        sortedList.addAll(models);
        sortedList.endBatchedUpdates();
    }

    public SortedList<Item> getSortedList() {
        return sortedList;
    }
}
