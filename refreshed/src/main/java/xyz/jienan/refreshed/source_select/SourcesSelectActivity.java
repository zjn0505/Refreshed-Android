package xyz.jienan.refreshed.source_select;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.gturedi.views.StatefulLayout;

import java.util.List;

import io.realm.Realm;
import xyz.jienan.refreshed.R;
import xyz.jienan.refreshed.network.NewsSourceBean;

/**
 * Created by Jienan on 2017/7/24.
 */

public class SourcesSelectActivity extends AppCompatActivity implements SourceSelectContract.View {

    private final static String TAG = SourcesSelectActivity.class.getSimpleName();

    private NewsSourceBean bean;
    private RecyclerView rvSources;
    private StatefulLayout stateful;
    private SourcesAdapter mAdapter;
    private Realm realm;
    private SourceSelectContract.Presenter mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_select);
        setTitle("Select Sources");
        mPresenter = new SourceSelectPresenter(this);
        rvSources = findViewById(R.id.rv_source_list);
        stateful = findViewById(R.id.stateful);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvSources.getContext(),
                RecyclerView.VERTICAL);
        rvSources.addItemDecoration(dividerItemDecoration);
        mAdapter = new SourcesAdapter(null);
        rvSources.setLayoutManager(layoutManager);
        rvSources.setAdapter(mAdapter);
        realm = Realm.getDefaultInstance();
//        RealmQuery<NewsSourceBean> query = realm.where(NewsSourceBean.class);
//        RealmResults<NewsSourceBean> results = query.greaterThan("index", -1).findAll().sort("index");
//        addSourcesToAdapter(results.subList(0, results.size()));


//        if (!TextUtils.isEmpty(jsonString)) {
//            selectedSet = sharedPreferences.getStringSet("selected_sources", null);
//            bean = new Gson().fromJson(jsonString, NewsSourceBean.class);
//            Log.d(TAG, "sources stored : " + jsonString);
//            mAdapter.updateList(bean.getSources());
//            pbLoading.setVisibility(GONE);
//        } else {
//            querySources();
//        }
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


    private class SourcesAdapter extends RecyclerView.Adapter<SourcesAdapter.ViewHolder> {

        public class ViewHolder extends RecyclerView.ViewHolder {

            public final View mView;
            public final TextView mTvName;
            public final TextView mTvDescription;
            public final CheckBox mCkbSelect;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mTvName = (TextView) view.findViewById(R.id.tv_source_name);
                mTvDescription = (TextView) view.findViewById(R.id.tv_source_description);
                mCkbSelect = (CheckBox) view.findViewById(R.id.cbx_selected);
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

        @Override
        public SourcesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(SourcesSelectActivity.this).inflate(R.layout.list_item_source, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final SourcesAdapter.ViewHolder holder, final int position) {
            final NewsSourceBean bean = sourceList.get(position);
            holder.mTvName.setText(bean.getName());
            holder.mTvDescription.setText(bean.getDescription());

            holder.mCkbSelect.setChecked(bean.getIndex() > -1);

            final NewsSourceBean beanDB = realm.where(NewsSourceBean.class).equalTo("id", bean.getId()).findFirst();
            if (beanDB != null && beanDB.getIndex() > -1) {

            }
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final boolean wasChecked = holder.mCkbSelect.isChecked();
                    holder.mCkbSelect.setChecked(!wasChecked);
                    mPresenter.changeSelection(bean.getId(), wasChecked, position);
                }
            });

        }

        @Override
        public int getItemCount() {
            return sourceList == null ? 0 : sourceList.size();
        }
    }
}
