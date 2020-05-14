package com.shuqixiao.newsapp.Adapter;

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

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.shuqixiao.newsapp.Helper.BookmarkHelper;
import com.shuqixiao.newsapp.Activity.DetailedArticle;
import com.shuqixiao.newsapp.R;
import com.shuqixiao.newsapp.Entity.NewsItem;
import com.shuqixiao.newsapp.Fragment.FragmentBookmarks.OnListFragmentInteractionListener;
import com.squareup.picasso.Picasso;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BookmarkRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
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

    public BookmarkRecyclerViewAdapter(List<NewsItem> items, OnListFragmentInteractionListener listener, final Context context) {
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
                dialogBookmark.setImageResource(R.drawable.ic_bookmark_red_24dp);

                dialogBookmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove(mValues.get(pos).id);
                        editor.commit();
                        Toast.makeText(mContext, "\"" + mValues.get(pos).title + "\" was removed from Bookmarks", Toast.LENGTH_SHORT).show();
                        mValues.remove(pos);
                        notifyDataSetChanged(); // remove bookmark in recyclerview list
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        };
    }

    public static final int VIEW_TYPE_ITEM = 1;
    public static final int VIEW_TYPE_EMPTY = 0;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_EMPTY) {
            View emptyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmarks_empty, parent, false);
            return new RecyclerView.ViewHolder(emptyView) {};
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_card, parent, false);
        return new MyViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof MyViewHolder) {
            final MyViewHolder holder = (MyViewHolder) viewHolder;
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
            String displayTime = DateTimeFormatter.ofPattern("dd MMM").format(pubTimeAtLA);
            holder.mDateView.setText(displayTime);

            String section = mValues.get(position).section;
            if (section.length() > 14) {
                holder.mSectionView.setText(section.substring(0,15) + "...");
            } else {
                holder.mSectionView.setText(section);
            }

            // handle bookmark icon
            holder.mBookmarkView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove(holder.mItem.id);
                    Toast.makeText(mContext, "\"" + holder.mItem.title + "\" was removed from Bookmarks", Toast.LENGTH_SHORT).show();
                    editor.commit();
                    mValues.remove(position);
                    notifyDataSetChanged();
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

    }

    @Override
    public int getItemCount() {
        if (mValues.size() == 0) {
            return 1;
        }
        return mValues.size();
    }

    @Override
    public int getItemViewType(int position) {
        //在这里进行判断，如果我们的集合的长度为0时，我们就使用emptyView的布局
        if (mValues.size() == 0) {
            return VIEW_TYPE_EMPTY;
        }
        //如果有数据，则使用ITEM的布局
        return VIEW_TYPE_ITEM;
    }

    public void reSetNewsItemList(ArrayList<NewsItem> newsItemArrayList) {
        mValues = newsItemArrayList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImageView;
        public final TextView mTitleView;
        public final TextView mDateView;
        public final TextView mSectionView;
        public final ImageView mBookmarkView;
        public NewsItem mItem;

        public MyViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.bookmark_image);
            mTitleView = (TextView) view.findViewById(R.id.bookmark_title);
            mDateView = (TextView) view.findViewById(R.id.bookmark_date);
            mSectionView = (TextView) view.findViewById(R.id.bookmark_section);
            mBookmarkView = (ImageView) view.findViewById(R.id.bookmark_bookmarkCard);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }
}
