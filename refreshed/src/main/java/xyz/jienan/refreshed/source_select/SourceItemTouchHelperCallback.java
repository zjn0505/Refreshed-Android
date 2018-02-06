package xyz.jienan.refreshed.source_select;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.HapticFeedbackConstants;

/**
 * Created by Jienan on 2017/10/13.
 */

public class SourceItemTouchHelperCallback extends ItemTouchHelper.Callback {

    public interface ItemMoveListener {
        boolean movable(int position);
        void onItemMove(int from, int to);
    }

    private ItemMoveListener itemMoveListener;

    public SourceItemTouchHelperCallback(ItemMoveListener listener){
        itemMoveListener = listener;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = 0;
        if (itemMoveListener.movable(viewHolder.getAdapterPosition())) {
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        }
        int swipeFlags = 0;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        itemMoveListener.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
    }
}
