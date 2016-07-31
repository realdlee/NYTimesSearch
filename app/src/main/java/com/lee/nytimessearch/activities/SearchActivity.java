package com.lee.nytimessearch.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.lee.nytimessearch.Article;
import com.lee.nytimessearch.ArticleArrayAdapter;
import com.lee.nytimessearch.EndlessScrollListener;
import com.lee.nytimessearch.Filter;
import com.lee.nytimessearch.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {
    EditText etQuery;
    GridView gvResults;
    Button btnSearch;
    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;
    Filter filter;
    private final int REQUEST_CODE = 20;
    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setupViews();
    }

    public void setupViews() {
        etQuery = (EditText) findViewById(R.id.etQuery);
        gvResults = (GridView) findViewById(R.id.gvResults);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        articles = new ArrayList<>();
        adapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(adapter);

        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //create intent
                //use getApplicationContext instead of "this" because anonymous class
                Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                //get article to display
                Article article = articles.get(position);

                //pass article in intent
                i.putExtra("article", Parcels.wrap(article));
                startActivity(i);
            }
        });

        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                customLoadMoreDataFromApi(page++);
                return true;
            }
        });

        filter = new Filter();

    }

    public void customLoadMoreDataFromApi(int offset) {
        onArticleSearch(offset);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.miFilter) {
            openFilters();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void openFilters() {
        Intent i = new Intent(getApplicationContext(), FilterActivity.class);
        i.putExtra("filter", Parcels.wrap(filter));
        startActivityForResult(i, REQUEST_CODE);
    }

    public void onNewArticleSearch(View view) {
        onNewArticleSearch();
    }

    public void onNewArticleSearch() {
        adapter.clear();
        onArticleSearch(1);
    }

    public void onArticleSearch(int page) {
        if(isNetworkAvailable() && page < 4) {
            String query = etQuery.getText().toString();

            AsyncHttpClient client = new AsyncHttpClient();
            String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";

            RequestParams params = new RequestParams();
            params.put("api-key", "49e4ef58db7f4b28a614996ed6e0e513");
            params.put("page", page);
            params.put("q", query);
            params.put("sort", filter.sort);
            if(!filter.newsDesk.isEmpty()) {
                String newDeskParams = "";
                for(int i=0;i < filter.newsDesk.size(); i++) {
                    newDeskParams += "\""+ filter.newsDesk.get(i).toString() +"\"" + ",";
                }
                params.put("fq", "news_desk:(" + newDeskParams + ")");
            }
            if(filter.beginDate != null) {
                params.put("begin_date", format.format(filter.beginDate.getTime()));
            }
            Log.e("params", params.toString());
            client.get(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    JSONArray articleJsonResults = null;

                    try {
                        articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                        //                    articles.addAll(Article.fromJSONArray(articleJsonResults));
                        //                    adapter.notifyDataSetChanged();
                        adapter.addAll(Article.fromJSONArray(articleJsonResults));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        } else {
            Log.e("error", "no internet!");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            filter = (Filter) Parcels.unwrap(data.getParcelableExtra("filter"));
            onNewArticleSearch();
        }
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
