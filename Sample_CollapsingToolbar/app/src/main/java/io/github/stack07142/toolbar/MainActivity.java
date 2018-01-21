package io.github.stack07142.toolbar;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import java.util.ArrayList;

import io.github.stack07142.toolbar.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private RVAdapter rvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // Toolbar
        setSupportActionBar(binding.appBarView.toolbar);

        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) actionbar.setTitle("");

        binding.appBarView.toolbarLayout.setTitle("Collapsing Toolbar");

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*
         * Disable collapsing
         */
        // binding.myRecyclerView.setNestedScrollingEnabled(false);
        // binding.appBarView.appBar.setExpanded(false);

        // Recycler View
        binding.myRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        rvAdapter = new RVAdapter((Context) this);
        binding.myRecyclerView.setAdapter(rvAdapter);

        ArrayList<Card> items = new ArrayList<>();

        items.add(new Card("1"));
        items.add(new Card("2"));
        items.add(new Card("3"));
        items.add(new Card("4"));
        items.add(new Card("5"));
        items.add(new Card("6"));
        items.add(new Card("7"));
        items.add(new Card("8"));
        items.add(new Card("9"));
        items.add(new Card("10"));
        items.add(new Card("11"));
        items.add(new Card("12"));
        items.add(new Card("13"));

        rvAdapter.setItemsAndRefresh(items);
    }
}
