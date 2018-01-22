package xyz.jienan.refreshed.island;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import xyz.jienan.refreshed.R;
import xyz.jienan.refreshed.news_list.NewsListFragment;

/**
 * Created by Jienan on 2018/1/22.
 */

public class NewsIslandActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frame = new FrameLayout(this);
        setContentView(frame, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        Intent intent = getIntent();
        String source = intent.getStringExtra("source");

        if (savedInstanceState == null) {
            Fragment newsFragment = NewsListFragment.newInstance(source, source, R.integer.type_topic, true);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(android.R.id.content, newsFragment).commit();
        }
    }
}
