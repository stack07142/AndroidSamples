package io.github.stack07142.sample_retrofit;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Super fast hello world", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        final TextView textView = (TextView) findViewById(R.id.textView);

        findViewById(R.id.fetch_button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.d("MainActivity", "Button Clicked");

                GitHubService gitHubService = GitHubService.retrofit.create(GitHubService.class);
                final Call<List<Contributor>> call = gitHubService.repoContributors("square", "retrofit");

                call.enqueue(new Callback<List<Contributor>>() {
                    @Override
                    public void onResponse(Call<List<Contributor>> call, Response<List<Contributor>> response) {

                        Log.d("MainActivity", "onResponse, " + response.body().toString());
                        textView.setText(response.body().toString());
                    }

                    @Override
                    public void onFailure(Call<List<Contributor>> call, Throwable t) {

                        Log.d("MainActivity", "onFailure, " + t.getMessage());
                        textView.setText("Something went wrong: " + t.getMessage());
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
