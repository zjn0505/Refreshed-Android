package xyz.jienan.refreshed.sources_fragment.headlines;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.gturedi.views.StatefulLayout;

import java.util.List;

import xyz.jienan.refreshed.R;
import xyz.jienan.refreshed.base.AnalyticsManager;
import xyz.jienan.refreshed.base.RefreshedApplication;
import xyz.jienan.refreshed.network.entity.NewsSourceBean;
import xyz.jienan.refreshed.source_select.SourcesSelectActivity;
import xyz.jienan.refreshed.sources_fragment.BaseSourcesFragment;
import xyz.jienan.refreshed.ui.NewsPagerAdapter;

import static android.app.Activity.RESULT_OK;
import static xyz.jienan.refreshed.base.Const.EVENT_SELECT_SOURCES;

/**
 * Created by jienanzhang on 17/07/2017.
 */

public class HeadlinesFragment extends BaseSourcesFragment implements HeadlinesContract.View {
    private HeadlinesContract.Presenter mPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mPresenter = new HeadlinesPresenter(this);
        stateful = (StatefulLayout) inflater.inflate(R.layout.fragment_refreshed, container, false);
        viewPager = stateful.findViewById(R.id.viewpager);
        tabLayout = getActivity().findViewById(R.id.tabs);
        adapter = new NewsPagerAdapter(getChildFragmentManager());
        if (viewPager != null) {
            setupViewPager(viewPager);
        }
        setHasOptionsMenu(true);
        mPresenter.loadSources();
        stateful.showLoading();
        return stateful;
    }

    private void addSourcesToAdapter(List<NewsSourceBean> sourceList) {
        adapter.updateSource(sourceList);
        if (landingPage != 0 && landingPage < sourceList.size()) {
            viewPager.setCurrentItem(landingPage);
            landingPage = 0;
        } else {
            viewPager.setCurrentItem(0);
        }
        if (getActivity() != null)
            ((RefreshedApplication) getActivity().getApplication()).bus().send(sourceList);
    }

    @Override
    public void renderSources(List<NewsSourceBean> sourceList) {
        if (sourceList != null && sourceList.size() > 0) {
            stateful.showContent();
            tabLayout.setVisibility(View.VISIBLE);
            addSourcesToAdapter(sourceList);
        } else {
            stateful.showError(v -> {
                mPresenter.loadSources();
                stateful.showLoading();
            });
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_headlines, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sources: {
                Intent intent = new Intent(getActivity(), SourcesSelectActivity.class);
                intent.putExtra("type", R.integer.type_source);
                startActivityForResult(intent, 1);
                AnalyticsManager.getInstance().logEvent(EVENT_SELECT_SOURCES);
            }
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            mPresenter.loadSources();
        }
    }
}
