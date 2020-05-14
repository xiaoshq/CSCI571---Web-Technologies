package com.shuqixiao.newsapp.Fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.shuqixiao.newsapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

public class FragmentTrending extends Fragment {
    final String endpoint = "http://shuqixiao-backend.us-east-2.elasticbeanstalk.com/trend?q=";
    private EditText editText;
    private LineChart lineChart;
    private String searchTerm = "CoronaVirus";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trending, container, false);
        lineChart = (LineChart) view.findViewById(R.id.chart);
        Legend legend = lineChart.getLegend();
        legend.setTextSize(15);
        legend.setFormSize(15);
        editText = (EditText) view.findViewById(R.id.search_term_keyword);
        getCharData(searchTerm);
        editText.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                event != null &&
                                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            if (event == null || !event.isShiftPressed()) {
                                getCharData(v.getText().toString());
                                return true;
                            }
                        }
                        return false; // pass on to other listeners.
                    }
                }
        );
        return view;
    }

    private void getCharData(String searchTerm) {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        final String keyWord = searchTerm;
        String url = endpoint + keyWord;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    List<Entry> list = new ArrayList<>();
                    JSONArray results = jsonObject.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        list.add(new Entry(i, Float.valueOf(results.getString(i))));
                    }
                    drowChart(list, keyWord);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private void drowChart(List<Entry> list, String keyWord) {
        LineDataSet lineDataSet = new LineDataSet(list, "Trending Chart for " + keyWord);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawAxisLine(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setColor(getResources().getColor(R.color.trendingChart));
        lineDataSet.setCircleColor(getResources().getColor(R.color.trendingChart));
        lineDataSet.setCircleHoleColor(getResources().getColor(R.color.trendingChart));
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(lineDataSet);
        LineData data = new LineData(dataSets);
        lineChart.setData(data);
        lineChart.invalidate(); // refresh
    }

}
