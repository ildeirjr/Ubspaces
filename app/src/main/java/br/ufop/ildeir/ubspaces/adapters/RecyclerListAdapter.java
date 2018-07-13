package br.ufop.ildeir.ubspaces.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import br.ufop.ildeir.ubspaces.R;
import br.ufop.ildeir.ubspaces.activities.VisualizarObjActivity;
import br.ufop.ildeir.ubspaces.miscellaneous.CircleTransform;
import br.ufop.ildeir.ubspaces.miscellaneous.FlipAnimator;
import br.ufop.ildeir.ubspaces.objects.Item;
import br.ufop.ildeir.ubspaces.singleton.ItemSingleton;

/**
 * Created by Ildeir on 15/06/2018.
 */

public class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListAdapter.MyViewHolder> {

    SortedList.Callback<Item> sortedListCallback = new SortedList.Callback<Item>() {
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
    };

    private final SortedList<Item> sortedList = new SortedList<Item>(Item.class, sortedListCallback);
    private SortedList<Item> sortedListBackup = new SortedList<Item>(Item.class, sortedListCallback);
    private SortedList<Item> filteredItemsByDate = new SortedList<Item>(Item.class, sortedListCallback);
    private SortedList<Item> filteredItemsByName = new SortedList<Item>(Item.class, sortedListCallback);
    private Context context;
    private MessageAdapterListener listener;
    private SparseBooleanArray selectedItems;
    private OnLoadMoreListener onLoadMoreListener;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    // array used to perform multiple animation at once
    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;

    // index is used to animate only the selected row
    // dirty fix, find a better solution
    private static int currentSelectedIndex = -1;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView itemName, itemCode, itemDate;
        public ImageView itemThumbnail;
        public RelativeLayout viewBackground, iconFront, iconBack, iconContainer;
        public LinearLayout viewForeground;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.objectName);
            itemCode = itemView.findViewById(R.id.objectCode);
            itemDate = itemView.findViewById(R.id.objectDate);
            itemThumbnail = itemView.findViewById(R.id.objectImg);
            viewBackground = itemView.findViewById(R.id.item_list_background);
            viewForeground = itemView.findViewById(R.id.item_list_foreground);
            iconContainer = itemView.findViewById(R.id.icon_container);
            iconFront = itemView.findViewById(R.id.icon_front);
            iconBack = itemView.findViewById(R.id.icon_back);
            itemView.setOnLongClickListener(this);

            iconContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onIconClicked(getAdapterPosition());
                }
            });

        }

        @Override
        public boolean onLongClick(View view) {
            listener.onRowLongClicked(getAdapterPosition());
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }


    public RecyclerListAdapter(Context context, ArrayList<Item> itemList, MessageAdapterListener listener) {
        this.context = context;
        sortedList.addAll(itemList);
        this.listener = listener;
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.object_list_adapter,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Item item = sortedList.get(position);
        holder.itemName.setText(item.getNome());
        holder.itemCode.setText("Código: " + item.getCodigo());
        holder.itemDate.setText(item.getDia() + "/" + item.getMes() + "/" + item.getAno());
        if(item.getFoto().equals("null.jpg")){
            holder.itemThumbnail.setImageResource( R.drawable.ic_camera);
        }else holder.itemThumbnail.setImageBitmap(item.createImgThumb());

        // change the row state to activated
        holder.itemView.setActivated(selectedItems.get(position, false));

        // handle icon animation
        applyIconAnimation(holder, position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemRowClicked(position);
            }
        });

    }

//    private void applyProfilePicture(MyViewHolder holder, Item item) {
//        if (item.getImg() != null) {
//            Glide.with(context).load(item.getImg())
//                    .thumbnail(0.5f)
//                    .transform(new CircleTransform(context))
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(holder.itemThumbnail);
//            holder.itemThumbnail.setColorFilter(null);
//        }
//        else{
//            Glide.with(context).load(R.drawable.ic_camera)
//                    .thumbnail(0.5f)
//                    .transform(new CircleTransform(context))
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(holder.itemThumbnail);
//            holder.itemThumbnail.setColorFilter(null);
//        }
//    }

    private void applyIconAnimation(MyViewHolder holder, int position) {
        if (selectedItems.get(position, false)) {
            holder.iconFront.setVisibility(View.GONE);
            resetIconYAxis(holder.iconBack);
            holder.iconBack.setVisibility(View.VISIBLE);
            holder.iconBack.setAlpha(1);
            if (currentSelectedIndex == position) {
                FlipAnimator.flipView(context, holder.iconBack, holder.iconFront, true);
                resetCurrentIndex();
            }
        } else {
            holder.iconBack.setVisibility(View.GONE);
            resetIconYAxis(holder.iconFront);
            holder.iconFront.setVisibility(View.VISIBLE);
            holder.iconFront.setAlpha(1);
            if ((reverseAllAnimations && animationItemsIndex.get(position, false)) || currentSelectedIndex == position) {
                FlipAnimator.flipView(context, holder.iconBack, holder.iconFront, false);
                resetCurrentIndex();
            }
        }
    }

    // As the views will be reused, sometimes the icon appears as
    // flipped because older view is reused. Reset the Y-axis to 0
    private void resetIconYAxis(View view) {
        if (view.getRotationY() != 0) {
            view.setRotationY(0);
        }
    }

    public void resetAnimationIndex() {
        reverseAllAnimations = false;
        animationItemsIndex.clear();
    }

    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }

    public void toggleSelection(int pos) {
        currentSelectedIndex = pos;
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
            animationItemsIndex.delete(pos);
        } else {
            selectedItems.put(pos, true);
            animationItemsIndex.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        reverseAllAnimations = true;
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    @Override
    public int getItemCount() {
        return sortedList.size();
    }

    public void removeItem(int position){
        sortedList.removeItemAt(position);
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

    public SortedList<Item> getSortedListBackup() {
        return sortedListBackup;
    }

    public void setSortedListBackup(SortedList<Item> sortedListBackup) {
        this.sortedListBackup = sortedListBackup;
    }

    public SortedList<Item> getFilteredItemsByDate() {
        return filteredItemsByDate;
    }

    public SortedList<Item> getFilteredItemsByName() {
        return filteredItemsByName;
    }

    public void setFilteredItemsByName(SortedList<Item> filteredItemsByName) {
        this.filteredItemsByName = filteredItemsByName;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public interface MessageAdapterListener {

        void onItemRowClicked(int position);

        void onIconClicked(int position);

        void onRowLongClicked(int position);
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

}
