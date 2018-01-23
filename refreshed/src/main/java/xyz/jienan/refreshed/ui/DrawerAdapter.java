package xyz.jienan.refreshed.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import xyz.jienan.refreshed.network.entity.ITabEntity;

/**
 * Created by jienanzhang on 24/01/2018.
 */

public class DrawerAdapter extends RecyclerView.Adapter {

    private List<ITabEntity> mList;

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
        TextView textView = new TextView(context);
        return new ViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((TextView)holder.itemView).setText(mList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }
}
