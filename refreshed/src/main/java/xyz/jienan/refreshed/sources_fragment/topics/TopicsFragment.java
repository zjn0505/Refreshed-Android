package xyz.jienan.refreshed.sources_fragment.topics;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.gturedi.views.StatefulLayout;

import java.util.ArrayList;
import java.util.List;

import xyz.jienan.refreshed.R;
import xyz.jienan.refreshed.base.RefreshedApplication;
import xyz.jienan.refreshed.island.NewsIslandActivity;
import xyz.jienan.refreshed.network.entity.NewsTopicsRequest;
import xyz.jienan.refreshed.network.entity.TopicsSearchBean;
import xyz.jienan.refreshed.sources_fragment.BaseSourcesFragment;
import xyz.jienan.refreshed.ui.NewsPagerAdapter;

import static android.app.Activity.RESULT_OK;

/**
 * Created by jienanzhang on 21/01/2018.
 */

public class TopicsFragment extends BaseSourcesFragment implements TopicsContract.View {

    private TopicsContract.Presenter mPresenter;
    private CursorAdapter searchAdapter;
    private List<TopicsSearchBean> searchSuggestions = new ArrayList<>();
    private final static int REQ_ISLAND_ACTIVITY = 101;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mPresenter = new TopicsPresenter(this);
        stateful = (StatefulLayout) inflater.inflate(R.layout.fragment_refreshed, container, false);
        viewPager = stateful.findViewById(R.id.viewpager);
        tabLayout = getActivity().findViewById(R.id.tabs);
        adapter = new NewsPagerAdapter(getChildFragmentManager());
        if (viewPager != null) {
            setupViewPager(viewPager);
        }
        setHasOptionsMenu(true);
        mPresenter.loadTopics(false);
//        stateful.showLoading(); // TODO don't know why
        return stateful;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.menu_topics, menu);
        final SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        final MenuItem search = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) search.getActionView();
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        if (searchAdapter == null) {
            searchAdapter = new SimpleCursorAdapter(
                    getActivity(),
                    android.R.layout.simple_list_item_2,
                    null,
                    new String[]{SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_TEXT_2},
                    new int[]{android.R.id.text1, android.R.id.text2},
                    0
            );
        }
        searchView.setSuggestionsAdapter(searchAdapter);
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                String topic = searchSuggestions.get(position).getTitle();
                searchView.setQuery(topic, true);
                searchView.clearFocus();
                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(getActivity(), NewsIslandActivity.class);
                intent.putExtra("source", query);
                startActivityForResult(intent, REQ_ISLAND_ACTIVITY);
                search.collapseActionView();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mPresenter.searchTopics(newText);
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search: {
            }
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_ISLAND_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                mPresenter.loadTopics(true);
            }
        }
    }

    private void addTopicsToAdapter(List<NewsTopicsRequest> sourceList) {
        adapter.updateSource(sourceList);
        if (landingPage != 0 && landingPage < sourceList.size()) {
            viewPager.setCurrentItem(landingPage);
            landingPage = 0;
        } else {
            viewPager.setCurrentItem(0);
        }
        ((RefreshedApplication) getActivity().getApplication()).bus().send(sourceList);
    }

    @Override
    public void renderTopics(List<NewsTopicsRequest> sourceList) {
        if (sourceList != null && sourceList.size() > 0) {
            stateful.showContent();
            tabLayout.setVisibility(View.VISIBLE);
            addTopicsToAdapter(sourceList);
        } else {
            stateful.showError(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.loadTopics(false);
                    stateful.showLoading();
                }
            });
        }
    }

    @Override
    public void renderTopicsSearch(List<TopicsSearchBean> topics) {
        searchSuggestions = topics;
        String[] columns = { BaseColumns._ID,
                SearchManager.SUGGEST_COLUMN_TEXT_1,
                SearchManager.SUGGEST_COLUMN_TEXT_2,
                SearchManager.SUGGEST_COLUMN_INTENT_DATA,
        };
        MatrixCursor cursor = new MatrixCursor(columns);
        for (int i = 0; i < searchSuggestions.size(); i++) {
            TopicsSearchBean topic = searchSuggestions.get(i);
            String[] tmp = {Integer.toString(i), topic.getTitle(),topic.getType(), topic.getTitle()};
            cursor.addRow(tmp);
        }
        searchAdapter.swapCursor(cursor);
    }


}
