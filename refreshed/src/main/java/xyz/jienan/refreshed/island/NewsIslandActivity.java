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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new NewsIslandPresenter(this);
        setContentView(R.layout.activity_island);
        Intent intent = getIntent();
        final String source = intent.getStringExtra("source");
        setTitle(source);
        if (savedInstanceState == null) {
            Fragment newsFragment = NewsListFragment.newInstance(source, source, R.integer.type_topic, true);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.news_list_container, newsFragment).commit();
        }
        tvAddTopics = findViewById(R.id.tv_add_topics);
        if (mPresenter.ifTopicsExist(source)) {
            tvAddTopics.setVisibility(View.GONE);
        } else {
            tvAddTopics.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPresenter.addTopics(source)) {
                        Toast.makeText(NewsIslandActivity.this, "Topic " + source + " added", Toast.LENGTH_SHORT).show();
                        tvAddTopics.setVisibility(View.GONE);
                        setResult(RESULT_OK);
                    } else {
                        Toast.makeText(NewsIslandActivity.this, "Failed to add topics", Toast.LENGTH_SHORT).show();
                    }
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
        }
        return super.onOptionsItemSelected(item);
    }
}
