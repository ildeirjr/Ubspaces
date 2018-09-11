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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import br.ufop.ildeir.ubspaces.R;
import br.ufop.ildeir.ubspaces.activities.VisualizarObjActivity;
import br.ufop.ildeir.ubspaces.interfaces.OnLoadMoreListener;
import br.ufop.ildeir.ubspaces.miscellaneous.CircleTransform;
import br.ufop.ildeir.ubspaces.miscellaneous.FlipAnimator;
import br.ufop.ildeir.ubspaces.objects.Item;
import br.ufop.ildeir.ubspaces.objects.RecyclerViewItem;
import br.ufop.ildeir.ubspaces.singleton.ItemSingleton;

/**
 * Created by Ildeir on 15/06/2018.
 */

public class RecyclerListAdapter extends RecyclerView.Adapter {

//    SortedList.Callback<RecyclerViewItem> sortedListCallback = new SortedList.Callback<RecyclerViewItem>() {
//        @Override
//        public int compare(RecyclerViewItem o1, RecyclerViewItem o2) {
//            return o1.getNome().compareTo(o2.getNome());
//        }
//
//        @Override
//        public void onChanged(int position, int count) {
//            notifyItemRangeChanged(position, count);
//        }
//
//        @Override
//        public boolean areContentsTheSame(RecyclerViewItem oldItem, RecyclerViewItem newItem) {
//            return oldItem.equals(newItem);
//        }
//
//        @Override
//        public boolean areItemsTheSame(RecyclerViewItem item1, RecyclerViewItem item2) {
//            return item1.getCodigo() == item2.getCodigo();
//        }
//
//        @Override
//        public void onInserted(int position, int count) {
//            notifyItemRangeInserted(position, count);
//        }
//
//        @Override
//        public void onRemoved(int position, int count) {
//            notifyItemRangeRemoved(position, count);
//        }
//
//        @Override
//        public void onMoved(int fromPosition, int toPosition) {
//            notifyItemMoved(fromPosition, toPosition);
//        }
//    };
//
//    private final SortedList<RecyclerViewItem> sortedList = new SortedList<RecyclerViewItem>(RecyclerViewItem.class, sortedListCallback);
//    private SortedList<RecyclerViewItem> sortedListBackup = new SortedList<RecyclerViewItem>(RecyclerViewItem.class, sortedListCallback);
//    private SortedList<RecyclerViewItem> filteredItemsByDate = new SortedList<RecyclerViewItem>(RecyclerViewItem.class, sortedListCallback);
//    private SortedList<RecyclerViewItem> filteredItemsByName = new SortedList<RecyclerViewItem>(RecyclerViewItem.class, sortedListCallback);
    private List<RecyclerViewItem> itemList;
    private List<RecyclerViewItem> itemListBackup;
    private List<RecyclerViewItem> filteredItemsByDate;
    private List<RecyclerViewItem> filteredItemsByName;
    private Context context;
    private MessageAdapterListener listener;
    private SparseBooleanArray selectedItems;
    private OnLoadMoreListener onLoadMoreListener;

    private final int VIEW_ITEM = 0;
    private final int VIEW_PROG = 1;

    // array used to perform multiple animation at once
    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;

    // index is used to animate only the selected row
    // dirty fix, find a better solution
    private static int currentSelectedIndex = -1;

    // Boolean to track loading status
    private boolean loading = false;

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


    public RecyclerListAdapter(Context context, ArrayList<RecyclerViewItem> itemList, MessageAdapterListener listener, OnLoadMoreListener onLoadMoreListener) {
        this.context = context;
        this.itemList = itemList;
        itemListBackup = new ArrayList<>();
        filteredItemsByDate = new ArrayList<>();
        filteredItemsByName = new ArrayList<>();
        this.listener = listener;
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
        this.onLoadMoreListener = onLoadMoreListener;
    }

    // ViewHolder for ProgressBar
    // Copy it as it is
    public static class ProgressViewHolder extends RecyclerView.ViewHolder {

        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progress_bar_bottom);
        }

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.object_list_adapter, parent, false);
            vh = new MyViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof MyViewHolder){
            final RecyclerViewItem item = itemList.get(position);
            ((MyViewHolder) holder).itemName.setText(item.getNome());
            ((MyViewHolder) holder).itemCode.setText("Código: " + item.getCodigo());
            ((MyViewHolder) holder).itemDate.setText(item.getDia() + "/" + item.getMes() + "/" + item.getAno());
            if(item.getFoto().equals("null.jpg")){
                ((MyViewHolder) holder).itemThumbnail.setImageResource( R.drawable.ic_camera);
            }else ((MyViewHolder) holder).itemThumbnail.setImageBitmap(item.createImgBitmap());

            // change the row state to activated
            holder.itemView.setActivated(selectedItems.get(position, false));

            // handle icon animation
            applyIconAnimation((MyViewHolder) holder, position);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemRowClicked(position);
                }
            });
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
            if (!loading) {
                // End has been reached
                // Do something
                if (onLoadMoreListener != null) {
                    onLoadMoreListener.onLoadMore(position);
                }
                loading = true;
            }
        }
    }

    // Method to set value of boolean variable "loading" to false
    // this method is called when data is loaded in the Activity class
    public void setLoaded() {
        loading = false;
    }

    @Override
    public int getItemViewType(int position) {
        return itemList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    // Method to add more items to the list
    public void update(List<RecyclerViewItem> newItems){
        itemList.addAll(newItems);
        notifyDataSetChanged();
    }

    // This method is used to remove ProgressBar when data is loaded
    public void removeLastItem(){
        itemList.remove(itemList.size() - 1);
        notifyDataSetChanged();
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
        return itemList.size();
    }

    public void removeItem(int position){
        itemList.remove(position);
        resetCurrentIndex();
        notifyItemRemoved(position);
    }

    public void restoreItem(RecyclerViewItem item, int position){
        itemList.add(position, item);
        notifyItemInserted(position);
    }

    public void replaceAll(ArrayList<RecyclerViewItem> models){
        itemList.clear();
        itemList.addAll(models);
        notifyDataSetChanged();
    }

//    public void replaceAll(ArrayList<RecyclerViewItem> models) {
//        sortedList.beginBatchedUpdates();
//        sortedList.clear();
//        sortedList.addAll(models);
//        sortedList.endBatchedUpdates();
//    }
//
//    public SortedList<RecyclerViewItem> getSortedList() {
//        return sortedList;
//    }
//
//    public SortedList<RecyclerViewItem> getSortedListBackup() {
//        return sortedListBackup;
//    }
//
//    public void setSortedListBackup(SortedList<RecyclerViewItem> sortedListBackup) {
//        this.sortedListBackup = sortedListBackup;
//    }
//
//    public SortedList<RecyclerViewItem> getFilteredItemsByDate() {
//        return filteredItemsByDate;
//    }
//
//    public SortedList<RecyclerViewItem> getFilteredItemsByName() {
//        return filteredItemsByName;
//    }
//
//    public void setFilteredItemsByName(SortedList<RecyclerViewItem> filteredItemsByName) {
//        this.filteredItemsByName = filteredItemsByName;
//    }


    public List<RecyclerViewItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<RecyclerViewItem> itemList) {
        this.itemList = itemList;
    }

    public List<RecyclerViewItem> getItemListBackup() {
        return itemListBackup;
    }

    public void setItemListBackup(List<RecyclerViewItem> itemListBackup) {
        this.itemListBackup = itemListBackup;
    }

    public List<RecyclerViewItem> getFilteredItemsByDate() {
        return filteredItemsByDate;
    }

    public void setFilteredItemsByDate(List<RecyclerViewItem> filteredItemsByDate) {
        this.filteredItemsByDate = filteredItemsByDate;
    }

    public List<RecyclerViewItem> getFilteredItemsByName() {
        return filteredItemsByName;
    }

    public void setFilteredItemsByName(List<RecyclerViewItem> filteredItemsByName) {
        this.filteredItemsByName = filteredItemsByName;
    }

    public interface MessageAdapterListener {

        void onItemRowClicked(int position);

        void onIconClicked(int position);

        void onRowLongClicked(int position);
    }

}
