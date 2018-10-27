package xyz.jienan.refreshed.island;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import xyz.jienan.refreshed.R;
import xyz.jienan.refreshed.news_list.NewsListFragment;

/**
 * Created by Jienan on 2018/1/22.
 */

public class NewsIslandActivity extends AppCompatActivity implements NewsIslandContract.View{

    private ImageView tvAddTopics;

    private NewsIslandContract.Presenter mPresenter;

    private String source;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new NewsIslandPresenter(this);
        setContentView(R.layout.activity_island);
        Intent intent = getIntent();
        source = intent.getStringExtra("source");
        mPresenter.checkNewsDays(source);
        setTitle(source);
        tvAddTopics = findViewById(R.id.tv_add_topics);
        if (mPresenter.ifTopicsExist(source)) {
            tvAddTopics.setVisibility(View.GONE);
        } else {
            tvAddTopics.setOnClickListener(v -> {
                if (mPresenter.addTopics(source)) {
                    Toast.makeText(NewsIslandActivity.this, "Topic " + source + " added", Toast.LENGTH_SHORT).show();
                    tvAddTopics.setVisibility(View.GONE);
                    setResult(RESULT_OK);
                } else {
                    Toast.makeText(NewsIslandActivity.this, "Failed to add topics", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onNewsDaysReady(int days) {
        Fragment newsFragment = NewsListFragment.newInstance(source, source, R.integer.type_topic, days, true);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.news_list_container, newsFragment).commit();
    }
}
