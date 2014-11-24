package com.gbaldera.yts.adapters;


import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gbaldera.yts.models.Movie;
import com.gbaldera.yts.R;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.List;

public class MoviesAdapter extends ArrayAdapter<Movie> {

    private LayoutInflater mInflater;
    private static int LAYOUT = R.layout.intem_movie;
    private DateFormat dateFormatMovieReleaseDate = DateFormat.getDateInstance(DateFormat.MEDIUM);


    public MoviesAdapter(Context context){
        super(context, LAYOUT);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // A ViewHolder keeps references to children views to avoid
        // unnecessary calls to findViewById() on each row.
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(LAYOUT, null);

            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.movie_title);
            holder.date = (TextView) convertView.findViewById(R.id.movie_date);
            holder.poster = (ImageView) convertView.findViewById(R.id.movie_psoter);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Bind the data efficiently with the holder.
        Movie movie = getItem(position);

        holder.title.setText(movie.ytsMovie.MovieTitleClean);

        if (movie.ytsMovie.DateUploaded != null) {
            //holder.date.setText(dateFormatMovieReleaseDate.format(movie.ytsMovie.DateUploaded));
            holder.date.setText(movie.ytsMovie.DateUploaded);
        } else {
            holder.date.setText("");
        }

        //String poster = movie.getPosterImageBaseUrl(getContext());
        String poster = movie.ytsMovie.CoverImage;

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

    static class ViewHolder {

        TextView title;

        TextView date;

        ImageView poster;
    }
}
