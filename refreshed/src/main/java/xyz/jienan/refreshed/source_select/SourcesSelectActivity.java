package xyz.jienan.refreshed.source_select;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gturedi.views.StatefulLayout;

import java.util.List;

import xyz.jienan.refreshed.R;
import xyz.jienan.refreshed.network.entity.NewsSourceBean;
import xyz.jienan.refreshed.ui.GridItemDecoration;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_source_select);
        setTitle("Select Sources");
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
        mPresenter.loadSources();
        stateful.showLoading();
    }

    @Override
    public void renderSources(List<NewsSourceBean> sources) {
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
            mAdapter.updateList(sources);
            stateful.showContent();
        }
    }

    @Override
    public void renderSourcesWithReorder(List<NewsSourceBean> sources, int from, int to) {
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
            mAdapter.updateList(sources, from, to);
            stateful.showContent();
        }
    }


    private class SourcesAdapter extends RecyclerView.Adapter<SourcesAdapter.ViewHolder> {

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

        private List<NewsSourceBean> sourceList;

        public SourcesAdapter(List<NewsSourceBean> list) {
            sourceList = list;
        }

        public void updateList(List<NewsSourceBean> list) {
            sourceList = list;
            notifyDataSetChanged();
        }

        public void updateList(List<NewsSourceBean> list, int from, int to) {
            sourceList = list;
            int visible = layoutManager.findFirstVisibleItemPosition();
            View view = layoutManager.getChildAt(visible);
            if (view != null) {
                int offset = view.getTop();
                layoutManager.scrollToPositionWithOffset(visible, offset);
            }
            notifyItemMoved(from, to);

        }

        @Override
        public SourcesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(SourcesSelectActivity.this).inflate(R.layout.list_item_source_1_1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SourcesAdapter.ViewHolder holder, int position) {
            final NewsSourceBean bean = sourceList.get(position);
            holder.mTvName.setText(bean.getName());
            holder.mTvDescription.setText(bean.getDescription());
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
            Glide.with(mContext)
                    .load(bean.getImgUrl())
                    .fitCenter()
                    .placeholder(R.drawable.image_placeholder)
                    .crossFade()
                    .into(holder.mIvIcon);
        }

        @Override
        public int getItemCount() {
            return sourceList == null ? 0 : sourceList.size();
        }
    }
}
