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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.gturedi.views.StatefulLayout;

import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import xyz.jienan.refreshed.R;
import xyz.jienan.refreshed.TimeUtils;
import xyz.jienan.refreshed.WebUtils;
import xyz.jienan.refreshed.base.AdsManager;
import xyz.jienan.refreshed.base.AnalyticsManager;
import xyz.jienan.refreshed.base.RefreshedApplication;
import xyz.jienan.refreshed.network.entity.ArticleBean;
import xyz.jienan.refreshed.network.entity.ArticlesBean;
import xyz.jienan.refreshed.ui.FaceCenterCrop;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static xyz.jienan.refreshed.MetaUtils.ADMOD_LIST_INSERT_ADS_UNIT_ID;
import static xyz.jienan.refreshed.base.Const.EVENT_LIST_EMPTY;
import static xyz.jienan.refreshed.base.Const.EVENT_LIST_ERROR;
import static xyz.jienan.refreshed.base.Const.EVENT_LIST_RELOAD;
import static xyz.jienan.refreshed.network.NetworkService.BYPASS_CACHE;
import static xyz.jienan.refreshed.network.NetworkService.USE_CACHE;

public class NewsListFragment extends Fragment implements NewsListContract.View, SwipeRefreshLayout.OnRefreshListener, INewsListFragmentListener {

    private SwipeRefreshLayout refreshLayout;
    private String newsSource;
    private String title;
    private StatefulLayout stateful;
    private NewsAdapter mAdapter;
    private RecyclerView rvNews;
    private NewsListContract.Presenter mPresenter;
    private int type;
    private static boolean isGoogleServiceAvailable = false;
    private boolean forceBypassCache = false;
    private int newsDays = 60;

    public static NewsListFragment newInstance(String source, String name, int type){
        NewsListFragment newsListFragment = new NewsListFragment();
        Bundle args = new Bundle();
        args.putString("source", source);
        args.putString("name", name);
        args.putInt("type", type);
        newsListFragment.setArguments(args);
        isGoogleServiceAvailable = RefreshedApplication.getInstance().isGoogleServiceAvailable;
        return newsListFragment;
    }

    public static NewsListFragment newInstance(String source, String name, int type, boolean forceBypassCache){
        NewsListFragment newsListFragment = newInstance(source, name, type);
        Bundle args = newsListFragment.getArguments();
        args.putBoolean("forceBypassCache", forceBypassCache);
        newsListFragment.setArguments(args);
        return newsListFragment;
    }

    public static NewsListFragment newInstance(String source, String name, int type, int newsDays, boolean forceBypassCache){
        NewsListFragment newsListFragment = newInstance(source, name, type);
        Bundle args = newsListFragment.getArguments();
        args.putBoolean("forceBypassCache", forceBypassCache);
        args.putInt("newsDays", newsDays);
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
        newsDays = args.getInt("newsDays", 60);
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
        refreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);
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
        recyclerView.setHasFixedSize(false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.loadList(newsSource, type, newsDays, forceBypassCache ? BYPASS_CACHE : USE_CACHE);
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
        mPresenter.loadList(newsSource, type, newsDays, BYPASS_CACHE);
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
            if (articles != null && !articles.isEmpty()) {
                stateful.showContent();
                refreshLayout.setEnabled(true);
                refreshLayout.setRefreshing(false);
                mAdapter.updateList(articles);
            } else {
                stateful.showEmpty();
                AnalyticsManager.getInstance().logEvent(EVENT_LIST_EMPTY);
            }
        } else {
            stateful.showError(v -> {
                mPresenter.loadList(newsSource, type, newsDays, BYPASS_CACHE);
                stateful.showLoading();
                AnalyticsManager.getInstance().logEvent(EVENT_LIST_RELOAD);
            });
            AnalyticsManager.getInstance().logEvent(EVENT_LIST_ERROR);
        }
    }

    private static class NewsAdapter
            extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

        private List<ArticleBean> mArticles;
        private Context mContext;
        private Realm realm;
        private final static int AD_TYPE = 0;
        private final static int CONTENT_TYPE = 1;
        private final static int LIST_AD_DELTA = 10;

        public class ViewHolder extends RecyclerView.ViewHolder {
            private View view;

            public ViewHolder(View view) {
                super(view);
                this.view = view;
            }

            public void bindType(ArticleBean bean) {

            }
        }

        public class ContentViewHolder extends ViewHolder {

            final View mView;
            final ImageView mIvThumbnail;
            final TextView mTvTitle;
            final TextView mTvDescription;
            final TextView mTvPublishTime;

            ContentViewHolder(View view) {
                super(view);
                mView = view;
                mIvThumbnail = view.findViewById(R.id.iv_thumbnails);
                mTvTitle = view.findViewById(R.id.tv_title);
                mTvDescription = view.findViewById(R.id.tv_description);
                mTvPublishTime = view.findViewById(R.id.tv_publish_time);
            }

            @Override
            public void bindType(final ArticleBean article) {
                final ArticleBean articleDB = realm.where(ArticleBean.class).equalTo("url", article.getUrl()).findFirst();
                mTvTitle.setText(article.getTitle());
                mTvDescription.setText(article.getDescription());
                mTvPublishTime.setText(TimeUtils.convertTimeToString(article.getPublishedAt()));
                Resources resources = mContext.getResources();
                if (articleDB.getAccessCount() > 0) {
                    int color = resources.getColor(R.color.textColorItemRead);
                    mTvTitle.setTextColor(color);
                    mTvDescription.setTextColor(color);
                    mTvPublishTime.setTextColor(color);
                } else {
                    int color = resources.getColor(R.color.textColorPrimary);
                    mTvTitle.setTextColor(color);
                    mTvDescription.setTextColor(color);
                    int colorSecondary = resources.getColor(R.color.textColorSecondary);
                    mTvPublishTime.setTextColor(colorSecondary);
                }

                mView.setOnClickListener(v -> {
                    realm.executeTransaction(realm -> {
                        articleDB.increaseAccessCount();
                        realm.insertOrUpdate(articleDB);
                    });
                    WebUtils.openLink(v.getContext(), article.getUrl());
                });

                String imgUrl = article.getUrlToImage();
                if (TextUtils.isEmpty(imgUrl)) {
                    mIvThumbnail.setVisibility(View.GONE);
                } else {
                    mIvThumbnail.setVisibility(View.VISIBLE);
                    RequestOptions myOptions = new RequestOptions()
                            .placeholder(R.drawable.image_placeholder);

                    if (isGoogleServiceAvailable)
                        myOptions.transforms(new CenterCrop(), new FaceCenterCrop());

                    Glide.with(mIvThumbnail.getContext())
                            .load(article.getUrlToImage())
                            .apply(myOptions)
                            .transition(withCrossFade())
                            .into(mIvThumbnail);
                }
            }
        }

        NewsAdapter(Context context, List<ArticleBean> items) {

            mContext = context;
            mArticles = items;
            realm = Realm.getDefaultInstance();
        }

        void updateList(List<ArticleBean> articles) {
            mArticles = articles;
            Collections.sort(mArticles, new ArticleBean.ReleaseComparator());
            realm.executeTransaction(realm -> {
                for (ArticleBean articleBean : mArticles) {
                    if (realm.where(ArticleBean.class).equalTo("url", articleBean.getUrl()).count() == 0){
                        realm.copyToRealm(articleBean);
                    }
                }
                notifyDataSetChanged();
            });
        }

        @Override
        public int getItemViewType(int position) {
            if (position > 0 && position % LIST_AD_DELTA == 0)
                return AD_TYPE;
            return CONTENT_TYPE;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == CONTENT_TYPE) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_3_3, parent, false);
                return new ContentViewHolder(view);
            } else if (viewType == AD_TYPE) {
                AdView adView = new AdView(mContext);
                adView.setAdSize(AdSize.SMART_BANNER);
                adView.setAdUnitId(AdsManager.getAdsUnitId(ADMOD_LIST_INSERT_ADS_UNIT_ID));
                AdRequest adRequest = new AdRequest.Builder().build();
                adView.loadAd(adRequest);
                return new ViewHolder(adView);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final ArticleBean article = mArticles.get(getRealPosition(position));
            holder.bindType(article);

        }

        @Override
        public int getItemCount() {
            if (mArticles == null) {
                return 0;
            }
            return mArticles.size() + (mArticles.size() / LIST_AD_DELTA);
        }

        private int getRealPosition(int position) {
            if (LIST_AD_DELTA == 0) {
                return position;
            } else {
                return position - position / LIST_AD_DELTA;
            }
        }
    }
}
