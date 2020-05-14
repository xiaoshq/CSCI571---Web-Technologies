package com.shuqixiao.newsapp.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shuqixiao.newsapp.Adapter.BookmarkRecyclerViewAdapter;
import com.shuqixiao.newsapp.Helper.BookmarkHelper;
import com.shuqixiao.newsapp.Entity.NewsItem;
import com.shuqixiao.newsapp.R;

import java.util.ArrayList;
import java.util.Map;


public class FragmentBookmarks extends Fragment {
    public static final String PREFERENCE_NAME = "MyPreference";
    private SharedPreferences sharedPreferences;

    private BookmarkRecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private ArrayList<NewsItem> newsItemList;
    private OnListFragmentInteractionListener mListener;
    private BookmarkHelper bookmarkHelper = new BookmarkHelper();
    private boolean hasBookmarkedArticles;

    public FragmentBookmarks() {}

    @SuppressWarnings("unused")
    public static FragmentBookmarks newInstance() {
        FragmentBookmarks fragment = new FragmentBookmarks();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        hasBookmarkedArticles = getBookmarkedArticles();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_bookmarks, container, false);

        Context context = view.getContext();
        recyclerView = (RecyclerView) view.findViewById(R.id.bookmark_recyclerview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
        gridLayoutManager.setSpanSizeLookup(onSpanSizeLookup);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        recyclerViewAdapter = new BookmarkRecyclerViewAdapter(newsItemList, mListener, context);
        recyclerView.setAdapter(recyclerViewAdapter);

        return view;
    }

    GridLayoutManager.SpanSizeLookup onSpanSizeLookup = new GridLayoutManager.SpanSizeLookup() {
        private static final int VIEW_TYPE_EMPTY = 1;

        @Override
        public int getSpanSize(int position) {
            return recyclerViewAdapter.getItemViewType(position) == VIEW_TYPE_EMPTY ? 1 : 2;
        }
    };

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

    private boolean getBookmarkedArticles() {
        newsItemList = new ArrayList<>();

        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String json = (String) entry.getValue();
            NewsItem newsItem = bookmarkHelper.string2NewsItem(json);
            newsItemList.add(newsItem);
        }

        if (newsItemList == null || newsItemList.size() == 0) {
            return false;
        }

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        newsItemList.clear();
        hasBookmarkedArticles = getBookmarkedArticles();
        recyclerViewAdapter.reSetNewsItemList(newsItemList);
        recyclerViewAdapter.notifyDataSetChanged();
    }
}
