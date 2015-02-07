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
import com.gbaldera.yts.models.YtsMovie;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MoviesAdapter extends ArrayAdapter<YtsMovie> {

    private LayoutInflater mInflater;
    private static int LAYOUT = R.layout.item_movie;


    public MoviesAdapter(Context context){
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
        YtsMovie movie = getItem(position);

        holder.title.setText(movie.title);
        holder.date.setText(String.valueOf(movie.year));

        if (!TextUtils.isEmpty(movie.medium_cover_image)) {
            Picasso.with(getContext())
                    .load(movie.medium_cover_image)
                    .into(holder.poster);
        } else {
            // clear image
            holder.poster.setImageDrawable(null);
        }

        return convertView;
    }

    public void setData(List<YtsMovie> data) {
        clear();
        if (data != null) {
            for (YtsMovie item : data) {
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
