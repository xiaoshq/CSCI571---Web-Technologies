package com.shuqixiao.newsapp.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.shuqixiao.newsapp.Helper.BookmarkHelper;
import com.shuqixiao.newsapp.Entity.NewsItem;
import com.shuqixiao.newsapp.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class DetailedArticle extends AppCompatActivity {
    public static final String PREFERENCE_NAME = "MyPreference";
    private SharedPreferences sharedPreferences;

    String articleId;
    boolean isBookmarked;
    ImageView articleImage;
    Uri articleImageURL;
    TextView articleTitle;
    TextView articleSection;
    TextView articleDate;
    TextView articleDescription;
    TextView articleLink;
    ProgressBar progressBar;
    TextView progressBarText;
    CardView cardView;
    NewsItem newsItem;
    BookmarkHelper bookmarkHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_article);

        bookmarkHelper = new BookmarkHelper();
        sharedPreferences = this.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_detailed);
        progressBarText = (TextView) findViewById(R.id.progressBar_text_detailed);
        cardView = (CardView) findViewById(R.id.cardView_detailed);

        progressBar.setVisibility(View.VISIBLE);
        progressBarText.setVisibility(View.VISIBLE);
        cardView.setVisibility(View.GONE);

        articleId = getIntent().getStringExtra("itemId");
        isBookmarked = sharedPreferences.contains(articleId);
        newsItem = new NewsItem();

        articleImage = (ImageView) findViewById(R.id.detailed_image);
        articleTitle = (TextView) findViewById(R.id.detailed_title);
        articleSection = (TextView) findViewById(R.id.detailed_section);
        articleDate = (TextView) findViewById(R.id.detailed_date);
        articleDescription = (TextView) findViewById(R.id.detailed_description);
        articleLink = (TextView) findViewById(R.id.detailed_link);

        articleRequest(getString(R.string.articleAPI) + articleId);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //Toolbar 必须在onCreate()之后设置标题文本，否则默认标签将覆盖我们的设置
        getSupportActionBar().setTitle("");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detailed, menu);
        //左侧添加一个默认的返回图标
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //设置返回键可用
        getSupportActionBar().setHomeButtonEnabled(true);
        // 设置bookmark图标
        MenuItem bookmark = menu.findItem(R.id.detailed_bookmark);
        if (isBookmarked) {
            bookmark.setIcon(R.drawable.ic_bookmark_red_24dp);
        } else {
            bookmark.setIcon(R.drawable.ic_bookmark_border_red_24dp);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Toolbar的事件---返回
        if(id == android.R.id.home){
//            if (isBookmarked != sharedPreferences.contains(articleId)) {
//                Intent returnIntent = new Intent();
//                int pos = getIntent().getIntExtra("pos", 0);
//                returnIntent.putExtra("pos", pos);
//                returnIntent.putExtra("result", sharedPreferences.contains(articleId));
//                setResult(RESULT_OK,returnIntent);
//            } else {
//                Intent returnIntent = new Intent();
//                setResult(RESULT_CANCELED, returnIntent);
//            }

            finish();
        }

        // 更新Bookmark
        if (id == R.id.detailed_bookmark) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            boolean isBookmarked = sharedPreferences.contains(articleId);
            if (!isBookmarked) {
                // 收藏
                String json = bookmarkHelper.newsItem2String(newsItem);
                editor.putString(articleId, json);

                item.setIcon(R.drawable.ic_bookmark_red_24dp);
                Toast.makeText(this, "\"" + newsItem.title + "\" was added to Bookmarks", Toast.LENGTH_SHORT).show();
            } else {
                // 移除收藏
                editor.remove(articleId);
                item.setIcon(R.drawable.ic_bookmark_border_red_24dp);
                Toast.makeText(this, "\"" + newsItem.title + "\" was removed from Bookmarks", Toast.LENGTH_SHORT).show();
            }
            editor.commit();
        }

        // twitter share
        if (id == R.id.detailed_twitter) {
            String twitterUrl = "https://twitter.com/intent/tweet?text=Check%20out%20this%20link:%20" +
                    "https://www.theguardian.com/" + articleId +
                    "&hashtags=CSCI571NewsSearch";
            Uri uri = Uri.parse(twitterUrl);
            Intent twitterIntent = new Intent(Intent.ACTION_VIEW, uri);
            this.startActivity(twitterIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void articleRequest(String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    newsItem.id = articleId;
                    newsItem.image = jsonObject.getString("image");
                    newsItem.title = jsonObject.getString("title");
                    newsItem.section = jsonObject.getString("section");
                    newsItem.date = jsonObject.getString("date");

                    articleImageURL = Uri.parse(jsonObject.getString("image"));
                    Picasso.with(getBaseContext()).load(articleImageURL).resize(2048, 1600).onlyScaleDown().into(articleImage);
                    articleTitle.setText(jsonObject.getString("title"));
                    articleSection.setText(jsonObject.getString("section"));

                    // handle time convert
                    String pubTime = jsonObject.getString("date");
                    Instant timestamp = Instant.parse(pubTime);
                    ZonedDateTime pubTimeAtLA = timestamp.atZone(ZoneId.of("America/Los_Angeles"));
                    String displayTime = DateTimeFormatter.ofPattern("dd MMM YYYY").format(pubTimeAtLA);
                    articleDate.setText(displayTime);

                    String description = jsonObject.getString("description").replace("\\", "");
                    articleDescription.setText( Html.fromHtml(description) );
                    articleLink.setText(Html.fromHtml("<a href=\"" + jsonObject.getString("shareUrl") + "\">View Full Article</a>"));
                    articleLink.setMovementMethod(LinkMovementMethod.getInstance());

                    progressBar.setVisibility(View.GONE);
                    progressBarText.setVisibility(View.GONE);
                    cardView.setVisibility(View.VISIBLE);
                    getSupportActionBar().setTitle(newsItem.title);
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
}
