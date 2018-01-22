package xyz.jienan.refreshed.news_list;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gturedi.views.StatefulLayout;
import com.rohitarya.glide.facedetection.transformation.FaceCenterCrop;

import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import xyz.jienan.refreshed.R;
import xyz.jienan.refreshed.TimeUtils;
import xyz.jienan.refreshed.WebUtils;
import xyz.jienan.refreshed.base.RefreshedApplication;
import xyz.jienan.refreshed.network.entity.ArticleBean;
import xyz.jienan.refreshed.network.entity.ArticlesBean;

public class NewsListFragment extends Fragment implements NewsListContract.View, SwipeRefreshLayout.OnRefreshListener, INewsListFragmentListener {

    private SwipeRefreshLayout refreshLayout;
    private String newsSource;
    private String title;
    private StatefulLayout stateful;
    private NewsAdapter mAdapter;
    private RecyclerView rvNews;
    private NewsListContract.Presenter mPresenter;
    private int type;
    private static boolean isGoogleServiceAvaliable = false;
    private boolean forceBypassCache = false;

    public static NewsListFragment newInstance(String source, String name, int type){
        NewsListFragment newsListFragment = new NewsListFragment();
        Bundle args = new Bundle();
        args.putString("source", source);
        args.putString("name", name);
        args.putInt("type", type);
        newsListFragment.setArguments(args);
        isGoogleServiceAvaliable = RefreshedApplication.getInstance().isGoogleServiceAvaliable;
        return newsListFragment;
    }

    public static NewsListFragment newInstance(String source, String name, int type, boolean forceBypassCache){
        NewsListFragment newsListFragment = newInstance(source, name, type);
        Bundle args = newsListFragment.getArguments();
        args.putBoolean("forceBypassCache", forceBypassCache);
        newsListFragment.setArguments(args);
        return newsListFragment;
    }

    @Override
    public String getFragmentName() {
        return title;
    }

    @Override
    public void scrollTo(int position) {
        if (position >=0 && rvNews != null) {
            rvNews.smoothScrollToPosition(position);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        newsSource = args.getString("source");
        title = args.getString("name");
        type = args.getInt("type");
        forceBypassCache = args.getBoolean("forceBypassCache", false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        stateful = (StatefulLayout) inflater.inflate(
                R.layout.fragment_news_list, container, false);
        newsSource = getArguments().getString("source");
        refreshLayout = stateful.findViewById(R.id.swipe_refresh_list);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setEnabled(false);
        rvNews = stateful.findViewById(R.id.recyclerview);
        setupRecyclerView(rvNews);
        mPresenter = new NewsListPresenter(this);
        return stateful;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putString("source", newsSource);
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        mAdapter = new NewsAdapter(getActivity(), null);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.loadList(newsSource, type, false || forceBypassCache);
        stateful.showLoading();
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onRefresh() {
        refreshLayout.setRefreshing(true);
        mPresenter.loadList(newsSource, type, true);
    }

    @Override
    public void onDetach() {
        refreshLayout.setRefreshing(false);
        super.onDetach();
    }

    @Override
    public void renderList(ArticlesBean articlesBean) {
        if (articlesBean != null) {
            List<ArticleBean> articles = articlesBean.getArticles();
            if (articles != null && articles.size() > 0) {
                stateful.showContent();
                refreshLayout.setEnabled(true);
                refreshLayout.setRefreshing(false);
                mAdapter.updateList(articles);
            } else {
                stateful.showEmpty();
            }
        } else {
            stateful.showError(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.loadList(newsSource, type, true);
                    stateful.showLoading();
                }
            });
        }

    }


    private static class NewsAdapter
            extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

        private List<ArticleBean> mArticles;
        private Context mContext;
        private Realm realm;

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public final View mView;
            public final ImageView mIvThumbnail;
            public final TextView mTvTitle;
            public final TextView mTvDescription;
            public final TextView mTvPublishTime;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIvThumbnail = (ImageView) view.findViewById(R.id.iv_thumbnails);
                mTvTitle = (TextView) view.findViewById(R.id.tv_title);
                mTvDescription = (TextView) view.findViewById(R.id.tv_description);
                mTvPublishTime = (TextView) view.findViewById(R.id.tv_publish_time);
            }
        }

        public NewsAdapter(Context context, List<ArticleBean> items) {

            mContext = context;
            mArticles = items;
            realm = Realm.getDefaultInstance();
        }

        public void updateList(List<ArticleBean> articles) {
            mArticles = articles;
            Collections.sort(mArticles, new ArticleBean.ReleaseComparator());
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for (ArticleBean articleBean : mArticles) {
                        if (realm.where(ArticleBean.class).equalTo("url", articleBean.getUrl()).count() == 0){
                            realm.copyToRealm(articleBean);
                        }
                    }
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_3_3, parent, false);
//            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final ArticleBean article = mArticles.get(position);
            final ArticleBean articleDB = realm.where(ArticleBean.class).equalTo("url", article.getUrl()).findFirst();
            holder.mTvTitle.setText(article.getTitle());
            holder.mTvDescription.setText(article.getDescription());
            holder.mTvPublishTime.setText(TimeUtils.convertTimeToString(article.getPublishedAt()));
            Resources resources = mContext.getResources();
            if (articleDB.getAccessCount() > 0) {
                int color = resources.getColor(R.color.textColorItemRead);
                holder.mTvTitle.setTextColor(color);
                holder.mTvDescription.setTextColor(color);
                holder.mTvPublishTime.setTextColor(color);
            } else {
                int color = resources.getColor(R.color.textColorPrimary);
                holder.mTvTitle.setTextColor(color);
                holder.mTvDescription.setTextColor(color);
                holder.mTvPublishTime.setTextColor(color);
            }

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            articleDB.increaseAccessCount();
                            realm.insertOrUpdate(articleDB);
                        }
                    });
                    WebUtils.openLink(v.getContext(), article.getUrl());
                }
            });

            String imgUrl = article.getUrlToImage();
            if (TextUtils.isEmpty(imgUrl)) {
                holder.mIvThumbnail.setVisibility(View.GONE);
            } else {
                holder.mIvThumbnail.setVisibility(View.VISIBLE);
                if (isGoogleServiceAvaliable) {
                    Glide.with(holder.mIvThumbnail.getContext())
                            .load(article.getUrlToImage())
                            .placeholder(R.drawable.image_placeholder)
                            .transform(new FaceCenterCrop())
                            .crossFade()
                            .into(holder.mIvThumbnail);
                } else {
                    Glide.with(holder.mIvThumbnail.getContext())
                            .load(article.getUrlToImage())
                            .placeholder(R.drawable.image_placeholder)
                            .crossFade()
                            .into(holder.mIvThumbnail);
                }
            }

        }

        @Override
        public int getItemCount() {
            return mArticles == null ? 0 : mArticles.size();
        }
    }
}
