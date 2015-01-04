package com.gbaldera.yts.adapters;


import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gbaldera.yts.R;
import com.gbaldera.yts.helpers.TraktHelper;
import com.jakewharton.trakt.entities.Movie;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SearchMoviesAdapter extends ArrayAdapter<Movie> {

    private LayoutInflater mInflater;
    private static int LAYOUT = R.layout.item_search_movie;
    private DateFormat dateFormatMovieReleaseDate = DateFormat.getDateInstance(DateFormat.MEDIUM);


    public SearchMoviesAdapter(Context context){
        super(context, LAYOUT);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(LAYOUT, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Bind the data efficiently with the holder.
        Movie movie = getItem(position);

        holder.title.setText(movie.title);

        if (movie.released != null) {
            holder.date.setText(dateFormatMovieReleaseDate.format(movie.released));
        } else {
            holder.date.setText("");
        }

        String poster = TraktHelper.resizeImage(movie, TraktHelper.TraktImageType.POSTER,
                TraktHelper.TraktImageSize.THUMB);

        if (!TextUtils.isEmpty(poster)) {
            Picasso.with(getContext())
                    .load(poster)
                    .into(holder.poster);
        } else {
            // clear image
            holder.poster.setImageDrawable(null);
        }

        return convertView;
    }

    public void setData(List<Movie> data) {
        clear();
        if (data != null) {
            for (Movie item : data) {
                if (item != null) {
                    add(item);
                }
            }
        }
    }

    public static class ViewHolder {

        @InjectView(R.id.movie_title)
        TextView title;

        @InjectView(R.id.movie_date)
        TextView date;

        @InjectView(R.id.movie_poster)
        ImageView poster;

        public ViewHolder(View v){
            ButterKnife.inject(this, v);
        }
    }
}
