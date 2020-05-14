package com.shuqixiao.newsapp.Adapter;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shuqixiao.newsapp.Helper.BookmarkHelper;
import com.shuqixiao.newsapp.Activity.DetailedArticle;
import com.shuqixiao.newsapp.Fragment.Home.NewsFragment.OnListFragmentInteractionListener;
import com.shuqixiao.newsapp.R;
import com.shuqixiao.newsapp.Entity.NewsItem;
import com.squareup.picasso.Picasso;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display an Item and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */

public class MyNewsRecyclerViewAdapter extends RecyclerView.Adapter<MyNewsRecyclerViewAdapter.ViewHolder> {
    public static final String PREFERENCE_NAME = "MyPreference";
    private SharedPreferences sharedPreferences;

    private List<NewsItem> mValues;
    private final OnListFragmentInteractionListener mListener;
    private OnItemClickListener mOnItemClickListener;
    private Context mContext;
    private BookmarkHelper bookmarkHelper = new BookmarkHelper();

    public interface OnItemClickListener {
        void onClick(int pos);
        void onLongClick(int pos);
    }

    public MyNewsRecyclerViewAdapter(List<NewsItem> items, OnListFragmentInteractionListener listener, final Context context) {
        mValues = items;
        mListener = listener;
        mContext = context;
        sharedPreferences = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);

        mOnItemClickListener = new OnItemClickListener() {
            @Override
            public void onClick(int pos) {
                Intent intent = new Intent();
                intent.setClass(mContext, DetailedArticle.class);
                intent.putExtra("itemId", mValues.get(pos).id);
                intent.putExtra("pos", pos);
                mContext.startActivity(intent);
            }

            @Override
            public void onLongClick(final int pos) {
                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.custom_dialog);

                ImageView dialogImage = (ImageView) dialog.findViewById(R.id.dialog_image);
                Picasso.with(context)
                        .load(mValues.get(pos).image)
                        .resize(2048, 1600)
                        .onlyScaleDown()
                        .into(dialogImage);

                TextView dialogTitle = (TextView) dialog.findViewById(R.id.dialog_title);
                dialogTitle.setText(mValues.get(pos).title);

                ImageButton dialogShare = (ImageButton) dialog.findViewById(R.id.dialog_share);
                dialogShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String twitterUrl = "https://twitter.com/intent/tweet?text=Check%20out%20this%20link:%20" +
                                "https://www.theguardian.com/" + mValues.get(pos).id +
                                "&hashtags=CSCI571NewsSearch";
                        Uri uri = Uri.parse(twitterUrl);
                        Intent twitterIntent = new Intent(Intent.ACTION_VIEW, uri);
                        mContext.startActivity(twitterIntent);
                    }
                });

                final ImageButton dialogBookmark = (ImageButton) dialog.findViewById(R.id.dialog_bookmark);
                if (sharedPreferences.contains(mValues.get(pos).id)) {
                    dialogBookmark.setImageResource(R.drawable.ic_bookmark_red_24dp);
                } else {
                    dialogBookmark.setImageResource(R.drawable.ic_bookmark_border_red_24dp);
                }
                dialogBookmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        boolean isBookmarked = sharedPreferences.contains(mValues.get(pos).id);
                        if (!isBookmarked) {
                            // 收藏
                            String json = bookmarkHelper.newsItem2String(mValues.get(pos));
                            editor.putString(mValues.get(pos).id, json);
                            dialogBookmark.setImageResource(R.drawable.ic_bookmark_red_24dp);
                            notifyItemChanged(pos); // change bookmark in recyclerview list
                            Toast.makeText(mContext, "\"" + mValues.get(pos).title + "\" was added to Bookmarks", Toast.LENGTH_SHORT).show();
                        } else {
                            // 移除收藏
                            editor.remove(mValues.get(pos).id);
                            dialogBookmark.setImageResource(R.drawable.ic_bookmark_border_red_24dp);
                            notifyItemChanged(pos); // change bookmark in recyclerview list
                            Toast.makeText(mContext, "\"" + mValues.get(pos).title + "\" was removed from Bookmarks", Toast.LENGTH_SHORT).show();
                        }
                        editor.commit();
                    }
                });

                dialog.show();
            }
        };
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_news, parent, false); // fragment_news
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mImageView.setImageResource(R.drawable.icon_news);
        Picasso.with(holder.itemView.getContext())
                .load(mValues.get(position).image)
                .resize(2048, 1600)
                .onlyScaleDown()
                .into(holder.mImageView);
        holder.mTitleView.setText(mValues.get(position).title);
        // handle time convert
        String pubTime = mValues.get(position).date;
        Instant timestamp = Instant.parse(pubTime);
        ZonedDateTime pubTimeAtLA = timestamp.atZone(ZoneId.of("America/Los_Angeles"));
        ZonedDateTime nowTime = ZonedDateTime.now(ZoneId.of("America/Los_Angeles"));
        Duration duration = Duration.between(pubTimeAtLA, nowTime);
        String displayTime;
        if (duration.toHours() >= 1) {
            displayTime = duration.toHours() + "h ago";
        } else if (duration.toMinutes() >= 1) {
            displayTime = duration.toMinutes() + "m ago";
        } else {
            displayTime = duration.getSeconds() + "s ago";
        }
        holder.mDateView.setText(displayTime);

        holder.mSectionView.setText(mValues.get(position).section);
        // handle bookmark icon
        if (sharedPreferences.contains(holder.mItem.id)) {
            holder.mBookmarkView.setImageResource(R.drawable.ic_bookmark_red_24dp);
        } else {
            holder.mBookmarkView.setImageResource(R.drawable.ic_bookmark_border_red_24dp);
        }
        holder.mBookmarkView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获得sharedPreferences的编辑器
                SharedPreferences.Editor editor = sharedPreferences.edit();
                boolean isBookmarked = sharedPreferences.contains(holder.mItem.id);
                if (!isBookmarked) {
                    // 收藏
                    String json = bookmarkHelper.newsItem2String(holder.mItem);
                    editor.putString(holder.mItem.id, json);
                    holder.mBookmarkView.setImageResource(R.drawable.ic_bookmark_red_24dp);
                    Toast.makeText(mContext, "\"" + holder.mItem.title + "\" was added to Bookmarks", Toast.LENGTH_SHORT).show();
                } else {
                    // 移除收藏
                    editor.remove(holder.mItem.id);
                    holder.mBookmarkView.setImageResource(R.drawable.ic_bookmark_border_red_24dp);
                    Toast.makeText(mContext, "\"" + holder.mItem.title + "\" was removed from Bookmarks", Toast.LENGTH_SHORT).show();
                }
                editor.commit();
            }
        });

        if (mOnItemClickListener != null) {
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onClick(holder.getAdapterPosition());
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onListFragmentInteraction(holder.mItem);
                    }
                }
            });
            holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mOnItemClickListener.onLongClick(holder.getAdapterPosition());
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void reSetNewsItemList(ArrayList<NewsItem> newsItemArrayList) {
        mValues = newsItemArrayList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImageView;
        public final TextView mTitleView;
        public final TextView mDateView;
        public final TextView mSectionView;
        public final ImageView mBookmarkView;
        public NewsItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.newsImage);
            mTitleView = (TextView) view.findViewById(R.id.newsTitle);
            mDateView = (TextView) view.findViewById(R.id.newsDate);
            mSectionView = (TextView) view.findViewById(R.id.newsSection);
            mBookmarkView = (ImageView) view.findViewById(R.id.newsCardBookmark);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }
}
