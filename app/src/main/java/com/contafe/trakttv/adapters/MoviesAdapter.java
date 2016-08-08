package com.contafe.trakttv.adapters;

/**
 * Created by Natig on 7/27/16.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.contafe.trakttv.R;
import com.contafe.trakttv.activities.MovieDetailsActivity;
import com.contafe.trakttv.models.Movie;
import com.squareup.picasso.Picasso;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private List<Movie> feedItemList;
    private ArrayList<Movie> arraylist;
    private Context mContext;

    public MoviesAdapter(Context context, List<Movie> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
        this.arraylist = new ArrayList<Movie>();
        this.arraylist.addAll(feedItemList);
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_movie, viewGroup, false);
        MovieViewHolder viewHolder = new MovieViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder customViewHolder, int i) {
        Movie feedItem = feedItemList.get(i);
        // Download image using picasso library
        Picasso.with(mContext).load(feedItem.getThumbnail())
                .error(R.drawable.default_movie_thumb)
                .placeholder(R.drawable.default_movie_thumb)
                .into(customViewHolder.movieThumbIV);

        customViewHolder.movieLayout.setOnClickListener(clickListener);
        customViewHolder.movieLayout.setTag(customViewHolder);

        customViewHolder.movieTitleTV.setText(feedItem.getTitle());
        customViewHolder.movieTitleTV.setOnClickListener(clickListener);
        customViewHolder.movieTitleTV.setTag(customViewHolder);

        customViewHolder.movieYear.setText(feedItem.getYear());
        customViewHolder.movieYear.setOnClickListener(clickListener);
        customViewHolder.movieYear.setTag(customViewHolder);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MovieViewHolder holder = (MovieViewHolder) view.getTag();
            int position = holder.getAdapterPosition();
            Movie feedItem = feedItemList.get(position);
            Intent movieDetailsIntent = new Intent(view.getContext(), MovieDetailsActivity.class);
            movieDetailsIntent.putExtra("slug", feedItem.getSlug());
            movieDetailsIntent.putExtra("title", feedItem.getTitle());
            movieDetailsIntent.putExtra("thumb", feedItem.getThumbnail());
            view.getContext().startActivity(movieDetailsIntent);
        }
    };


    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        protected TextView movieTitleTV, movieYear;
        protected ImageView movieThumbIV;
        protected RelativeLayout movieLayout;

        public MovieViewHolder(View view) {
            super(view);
            this.movieTitleTV = (TextView) view.findViewById(R.id.rowTitle);
            this.movieYear = (TextView) view.findViewById(R.id.rowYear);
            this.movieLayout = (RelativeLayout) view.findViewById(R.id.layoutMovieRow);
            this.movieThumbIV = (ImageView) view.findViewById(R.id.rowThumb);

        }
    }

}