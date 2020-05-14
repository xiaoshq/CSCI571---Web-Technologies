package com.shuqixiao.newsapp.Fragment.Headlines;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.shuqixiao.newsapp.R;
import com.shuqixiao.newsapp.Adapter.MyNewsRecyclerViewAdapter;
import com.shuqixiao.newsapp.Fragment.Home.NewsFragment;
import com.shuqixiao.newsapp.Entity.NewsItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentTechnology extends Fragment {
    static final String SECTION = "technology";
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private NewsFragment.OnListFragmentInteractionListener mListener;
    private List<NewsItem> mNewsItems;
    private ProgressBar progressBar;
    private TextView progressBarText;
    private SwipeRefreshLayout swipeRefreshLayout;

    public FragmentTechnology() {}

    @SuppressWarnings("unused")
    public static FragmentTechnology newInstance() {
        FragmentTechnology fragment = new FragmentTechnology();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNewsItems = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_recycler_list, container, false);

        Context context = view.getContext();
        recyclerView = (RecyclerView) view.findViewById(R.id.news_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewAdapter = new MyNewsRecyclerViewAdapter(mNewsItems, mListener, context);
        recyclerView.setAdapter(recyclerViewAdapter);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBarText = (TextView) view.findViewById(R.id.progressBar_text);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getHeadlinesNews(getString(R.string.sectionAPI) + SECTION);

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

        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        progressBarText.setVisibility(View.VISIBLE);

        getHeadlinesNews(getString(R.string.sectionAPI) + SECTION);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NewsFragment.OnListFragmentInteractionListener) {
            mListener = (NewsFragment.OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void getHeadlinesNews(String url) {
        mNewsItems.clear();
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject temp = (JSONObject) results.get(i);
                        NewsItem newsItem = new NewsItem();
                        newsItem.id = temp.getString("id");
                        newsItem.title = temp.getString("articleTitle");
                        newsItem.image = temp.getString("articleImage");
                        newsItem.date = temp.getString("articleDate");
                        newsItem.section = temp.getString("articleSection");
                        mNewsItems.add(newsItem);
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
    public void onResume() {
        super.onResume();
        getHeadlinesNews(getString(R.string.sectionAPI) + SECTION);
    }
}
