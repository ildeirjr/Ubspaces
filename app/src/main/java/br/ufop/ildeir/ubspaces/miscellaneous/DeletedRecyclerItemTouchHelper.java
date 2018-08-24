package br.ufop.ildeir.ubspaces.miscellaneous;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import br.ufop.ildeir.ubspaces.adapters.DeletedRecyclerListAdapter;
import br.ufop.ildeir.ubspaces.adapters.RecyclerListAdapter;

/**
 * Created by Ildeir on 23/08/2018.
 */

public class DeletedRecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback{

    private DeletedRecyclerItemTouchHelper.RecyclerItemTouchHelperListener listener;

    /**
     * Creates a Callback for the given drag and swipe allowance. These values serve as
     * defaults
     * and if you want to customize behavior per ViewHolder, you can override
     * {@link #getSwipeDirs(RecyclerView, ViewHolder)}
     * and / or {@link #getDragDirs(RecyclerView, ViewHolder)}.
     *
     * @param dragDirs  Binary OR of direction flags in which the Views can be dragged. Must be
     *                  composed of {@link #LEFT}, {@link #RIGHT}, {@link #START}, {@link
     *                  #END},
     *                  {@link #UP} and {@link #DOWN}.
     * @param swipeDirs Binary OR of direction flags in which the Views can be swiped. Must be
     *                  composed of {@link #LEFT}, {@link #RIGHT}, {@link #START}, {@link
     *                  #END},
     *                  {@link #UP} and {@link #DOWN}.
     */
    public DeletedRecyclerItemTouchHelper(int dragDirs, int swipeDirs, DeletedRecyclerItemTouchHelper.RecyclerItemTouchHelperListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }


    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        listener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            final View foregroundView = ((DeletedRecyclerListAdapter.MyViewHolder) viewHolder).viewForeground;
            getDefaultUIUtil().onSelected(foregroundView);
        }
    }


    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {
        final View foregroundView = ((DeletedRecyclerListAdapter.MyViewHolder) viewHolder).viewForeground;
        getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final View foregroundView = ((DeletedRecyclerListAdapter.MyViewHolder) viewHolder).viewForeground;
        getDefaultUIUtil().clearView(foregroundView);
    }


    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        final View foregroundView = ((DeletedRecyclerListAdapter.MyViewHolder) viewHolder).viewForeground;

        getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive);
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    public interface RecyclerItemTouchHelperListener {
        void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }

}
