/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import xyz.jienan.refreshed.network.bean.ArticleBean;
import xyz.jienan.refreshed.network.bean.HeadlinesBean;

public class NewsListFragment extends Fragment implements NewsListContract.View, SwipeRefreshLayout.OnRefreshListener, INewsListFragmentListener {

    private SwipeRefreshLayout refreshLayout;
    private String newsSource;
    private String title;
    private StatefulLayout stateful;
    private NewsAdapter mAdapter;
    private RecyclerView rvNews;
    private NewsListContract.Presenter mPresenter;
    private static boolean isGoogleServiceAvaliable = false;

    public static NewsListFragment newInstance(String source, String name){
        NewsListFragment newsListFragment = new NewsListFragment();
        Bundle args = new Bundle();
        args.putString("source", source);
        args.putString("name", name);
        newsListFragment.setArguments(args);
        isGoogleServiceAvaliable = RefreshedApplication.getInstance().isGoogleServiceAvaliable;
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
        mPresenter.loadList(newsSource, false);
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
        mPresenter.loadList(newsSource, true);
    }

    @Override
    public void onDetach() {
        refreshLayout.setRefreshing(false);
        super.onDetach();
    }

    @Override
    public void renderList(HeadlinesBean headlinesBean) {
        if (headlinesBean != null) {
            List<ArticleBean> articles = headlinesBean.getArticles();
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
                    mPresenter.loadList(newsSource, true);
                    stateful.showLoading();
                }
            });
        }

    }


    private static class NewsAdapter
            extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
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
//            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
//            mBackground = mTypedValue.resourceId;
            mContext = context;
            mArticles = items;
            realm = Realm.getDefaultInstance();
        }

        public void updateList(List<ArticleBean> articles) {
            mArticles = articles;
            Collections.sort(mArticles, new ArticleBean.ReleaseComparator());
            realm.beginTransaction();
            for (ArticleBean articleBean : mArticles) {
                if (realm.where(ArticleBean.class).equalTo("url", articleBean.getUrl()).count() == 0){
                    realm.copyToRealm(articleBean);
                }
            }
            realm.commitTransaction();
            notifyDataSetChanged();
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
                            .transform(new FaceCenterCrop())
                            .into(holder.mIvThumbnail);
                } else {
                    Glide.with(holder.mIvThumbnail.getContext())
                            .load(article.getUrlToImage())
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
