package com.shuqixiao.newsapp.Fragment.Home;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.shuqixiao.newsapp.Adapter.MyNewsRecyclerViewAdapter;
import com.shuqixiao.newsapp.Entity.NewsItem;
import com.shuqixiao.newsapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class NewsFragment extends Fragment {
    private ArrayList<NewsItem> newsItemList;
    private MyNewsRecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private OnListFragmentInteractionListener mListener;
    private ProgressBar progressBar;
    private TextView progressBarText;

    public NewsFragment() {
    }

    @SuppressWarnings("unused")
    public static NewsFragment newInstance() {
        NewsFragment fragment = new NewsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        newsItemList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);

        Context context = view.getContext();
        recyclerView = (RecyclerView) view.findViewById(R.id.news_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL));
        recyclerViewAdapter = new MyNewsRecyclerViewAdapter(newsItemList, mListener, context);
        recyclerView.setAdapter(recyclerViewAdapter);


        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBarText = (TextView) view.findViewById(R.id.progressBar_text);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
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

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(NewsItem item);
    }

    public boolean getNewsListData() {
        newsItemList = new ArrayList<>();

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        String url = getString(R.string.homeNewsAPI);
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
        if (newsItemList.size() == 0) {
            return false;
        }

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        progressBarText.setVisibility(View.VISIBLE);

        newsItemList.clear();
        getNewsListData();
        recyclerViewAdapter.reSetNewsItemList(newsItemList);
        recyclerViewAdapter.notifyDataSetChanged();

    }
}
