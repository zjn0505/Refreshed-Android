package xyz.jienan.refreshed.source_select;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gturedi.views.StatefulLayout;

import java.util.Collections;
import java.util.List;

import xyz.jienan.refreshed.R;
import xyz.jienan.refreshed.network.entity.ITabEntity;
import xyz.jienan.refreshed.network.entity.NewsSourceBean;
import xyz.jienan.refreshed.ui.GridItemDecoration;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by Jienan on 2017/7/24.
 */

public class SourcesSelectActivity extends AppCompatActivity implements SourceSelectContract.View {

    private final static String TAG = SourcesSelectActivity.class.getSimpleName();

    private RecyclerView rvSources;
    private StatefulLayout stateful;
    private SourcesAdapter mAdapter;
    private SourceSelectContract.Presenter mPresenter;
    GridLayoutManager layoutManager;
    private Context mContext;
    private int type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_source_select);
        type = getIntent().getIntExtra("type", R.integer.type_source);
        mPresenter = new SourceSelectPresenter(this);
        rvSources = findViewById(R.id.rv_source_list);
        stateful = findViewById(R.id.stateful);
        layoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        int spacing = getResources().getDimensionPixelSize(R.dimen.grid_space);
        rvSources.addItemDecoration(new GridItemDecoration(spacing));
        rvSources.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new SourcesAdapter(null);
        rvSources.setLayoutManager(layoutManager);
        rvSources.setAdapter(mAdapter);

        SourceItemTouchHelperCallback dragCallback = new SourceItemTouchHelperCallback(mAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(dragCallback);
        itemTouchHelper.attachToRecyclerView(rvSources);
        if (R.integer.type_source == type) {
            setTitle(getString(R.string.select_sources));
            mPresenter.loadSources();
            stateful.showLoading();
        } else {
            setTitle(getString(R.string.select_topics));
            mPresenter.loadTopics();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void renderSources(List<? extends ITabEntity> sources) {
        if (sources == null) {
            stateful.showError(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.loadSources();
                    stateful.showLoading();
                }
            });
        } else if (sources.size() == 0){
            stateful.showEmpty();
        } else {
            mAdapter.updateList((List<ITabEntity>) sources);
            stateful.showContent();
        }
    }

    @Override
    public void renderSourcesWithReorder(List<? extends ITabEntity> sources, int from, int to) {
        if (sources == null) {
            stateful.showError(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.loadSources();
                    stateful.showLoading();
                }
            });
        } else if (sources.size() == 0){
            stateful.showEmpty();
        } else {
            mAdapter.updateList((List<ITabEntity>) sources, from, to);
            stateful.showContent();
        }
    }


    private class SourcesAdapter extends RecyclerView.Adapter<SourcesAdapter.ViewHolder> implements SourceItemTouchHelperCallback.ItemMoveListener {

        @Override
        public boolean movable(int position) {
            return sourceList.get(position).getIndex() != -1;
        }

        @Override
        public void onItemMove(int from, int to) {
            if (to > getMaxSelectedIndex()) {
                to = getMaxSelectedIndex();
            }
            ITabEntity source = sourceList.get(from);
            sourceList.remove(source);
            sourceList.add(to, source);
            notifyItemMoved(from, to);
            mPresenter.reorderSelected(sourceList, from, to);
            setResult(RESULT_OK);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public final View mView;
            public final TextView mTvName;
            public final TextView mTvDescription;
            public final CheckBox mCkbSelect;
            public final ImageView mIvIcon;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mTvName = (TextView) view.findViewById(R.id.tv_source_name);
                mTvDescription = (TextView) view.findViewById(R.id.tv_source_description);
                mCkbSelect = (CheckBox) view.findViewById(R.id.cbx_selected);
                mIvIcon = (ImageView) view.findViewById(R.id.iv_icon);
            }

        }

        private List<ITabEntity> sourceList;

        public SourcesAdapter(List<ITabEntity> list) {
            sourceList = list;
        }

        public void updateList(List<ITabEntity> list) {
            sourceList = list;
            notifyDataSetChanged();
        }

        public void updateList(List<ITabEntity> list, int from, int to) {
            sourceList = list;
            notifyItemMoved(from, to);
        }

        @Override
        public SourcesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(SourcesSelectActivity.this).inflate(R.layout.list_item_source_1_1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SourcesAdapter.ViewHolder holder, int position) {
            final ITabEntity bean = sourceList.get(position);
            holder.mTvName.setText(bean.getName());
            holder.mCkbSelect.setChecked(bean.getIndex() > -1);
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.getAdapterPosition() == 0) {
                        if (sourceList.size() > 1 && sourceList.get(1).getIndex() == -1) {
                            Toast.makeText(mContext, getString(R.string.keep_at_least_one_source), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    final boolean wasChecked = holder.mCkbSelect.isChecked();
                    holder.mCkbSelect.setChecked(!wasChecked);
                    mPresenter.changeSelection(sourceList, wasChecked, holder.getAdapterPosition());
                    setResult(RESULT_OK);
                }
            });
            RequestOptions myOptions = new RequestOptions()
                    .fitCenter().placeholder(R.drawable.image_placeholder);

            Glide.with(mContext)
                    .load(bean.getImgUrl())
                    .apply(myOptions)
                    .transition(withCrossFade())
                    .into(holder.mIvIcon);
        }

        @Override
        public int getItemCount() {
            return sourceList == null ? 0 : sourceList.size();
        }


        private int getMaxSelectedIndex() {
            for (int i = 0; i< sourceList.size(); i++) {
                ITabEntity bean = sourceList.get(i);
                if (bean.getIndex() == -1) {
                    return --i;
                }
            }
            return -1;
        }
    }
}
