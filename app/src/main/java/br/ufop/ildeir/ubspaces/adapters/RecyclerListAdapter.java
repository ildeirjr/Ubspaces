package br.ufop.ildeir.ubspaces.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import br.ufop.ildeir.ubspaces.R;
import br.ufop.ildeir.ubspaces.activities.VisualizarObjActivity;
import br.ufop.ildeir.ubspaces.objects.Item;
import br.ufop.ildeir.ubspaces.singleton.ItemSingleton;
import br.ufop.ildeir.ubspaces.singleton.ObjectListSingleton;

/**
 * Created by Ildeir on 15/06/2018.
 */

public class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListAdapter.MyViewHolder> {

    private Context context;
    private List<Item> itemList;

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
                    Context context = view.getContext();
                    ItemSingleton.getInstance().setItemSingleton(ObjectListSingleton.getInstance().getObjectList().get(getAdapterPosition()));
                    context.startActivity(new Intent(view.getContext(),VisualizarObjActivity.class));
                }
            });

        }
    }

    public RecyclerListAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.object_list_adapter,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Item item = itemList.get(position);
        holder.itemName.setText(item.getNome());
        holder.itemCode.setText("Código: " + item.getCodigo());
        if(item.getFoto().equals("null.jpg")){
            holder.itemThumbnail.setImageResource( R.drawable.ic_camera);
        }else holder.itemThumbnail.setImageBitmap(item.createImgThumb());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void removeItem(int position){
        itemList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Item item, int position){
        itemList.add(position,item);
        notifyItemInserted(position);
    }

}
