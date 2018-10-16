package xyz.jienan.refreshed.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import xyz.jienan.refreshed.R;
import xyz.jienan.refreshed.network.entity.ITabEntity;

/**
 * Created by jienanzhang on 24/01/2018.
 */

public class DrawerAdapter extends RecyclerView.Adapter {

    public final static int TYPE_HEADLINES = 1;
    public final static int TYPE_TOPICS = 2;
    public IDrawerItemClickListener mListener;

    private int mType;
    private List<ITabEntity> mList;

    public interface IDrawerItemClickListener {
        void onDrawerItemClicked(int type, String sourceName);
    }

    public DrawerAdapter(IDrawerItemClickListener listener, int type) {
        mListener = listener;
        mType = type;
    }

    public void updateList(List<ITabEntity> list) {
        mList = list;
        notifyDataSetChanged();
    }


    private class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.drawer_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final String sourceName = mList.get(position).getName();
        ((TextView)holder.itemView).setText(sourceName);
        holder.itemView.setOnClickListener(v -> {
            if (mListener != null)
                mListener.onDrawerItemClicked(mType, sourceName);
        });
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }
}
