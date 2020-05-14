package com.shuqixiao.newsapp.Activity;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.shuqixiao.newsapp.Adapter.MyNewsRecyclerViewAdapter;
import com.shuqixiao.newsapp.Fragment.Home.NewsFragment;
import com.shuqixiao.newsapp.Entity.NewsItem;
import com.shuqixiao.newsapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class SearchResult extends AppCompatActivity implements NewsFragment.OnListFragmentInteractionListener {
    String query;
    ProgressBar progressBar;
    TextView progressBarText;
    SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<NewsItem> newsItemList;
    private MyNewsRecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private NewsFragment.OnListFragmentInteractionListener mListener;

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_news_recycler_list);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBarText = (TextView) findViewById(R.id.progressBar_text);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);

        newsItemList = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.news_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerViewAdapter = new MyNewsRecyclerViewAdapter(newsItemList, mListener, this);
        recyclerView.setAdapter(recyclerViewAdapter);

        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        progressBarText.setVisibility(View.VISIBLE);

        query = getIntent().getStringExtra("q");

        getSearchResultList(query);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSearchResultList(query);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, 1000);
            }
        });

    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //Toolbar 必须在onCreate()之后设置标题文本，否则默认标签将覆盖我们的设置
        getSupportActionBar().setTitle(getString(R.string.searchToolbar) + query);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //左侧添加一个默认的返回图标
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //设置返回键可用
        getSupportActionBar().setHomeButtonEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Toolbar的事件---返回
        if(id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void getSearchResultList(final String query) {
//        newsItemList = new ArrayList<>();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = getString(R.string.searchAPI) + query;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {

                    JSONArray results = jsonObject.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) { //results.length()
                        JSONObject temp = (JSONObject) results.get(i);
                        NewsItem newsItem = new NewsItem();
                        newsItem.id = temp.getString("id");
                        newsItem.title = temp.getString("title");
                        newsItem.image = temp.getString("image");
                        newsItem.date = temp.getString("date");
                        newsItem.section = temp.getString("section");
                        newsItemList.add(newsItem);

                    }
                    recyclerView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    progressBarText.setVisibility(View.GONE);
                    recyclerViewAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    // TODO: handle error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                // TODO: handle error
                volleyError.printStackTrace();
            }
        });

        // 将创建的请求添加到请求队列中
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onListFragmentInteraction(NewsItem item) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        getSearchResultList(query);
    }
}
