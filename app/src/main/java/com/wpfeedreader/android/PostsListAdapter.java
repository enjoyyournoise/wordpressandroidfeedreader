package com.wpfeedreader.android;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.wpfeedreader.android.Models.Post;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Diego on 06/09/2017.
 */

public class PostsListAdapter extends ArrayAdapter<Post> {

    private final Activity context;
    private final List<Post> postsList;

    public PostsListAdapter(Activity context, List<Post> postsList) {
        super(context, R.layout.item_posts_list, postsList);
        this.context = context;
        this.postsList = postsList;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.item_posts_list, null, true);

        Post post = postsList.get(position);

        TextView txtTitle = rowView.findViewById(R.id.txt);
        txtTitle.setText(Html.fromHtml(post.getTitle()));

        ImageView img = rowView.findViewById(R.id.img);
        Picasso.with(context)
                .load(post.getThumbUrl())
                .placeholder(R.drawable.logo_pallone)
                .error(R.drawable.logo_pallone)
                .into(img);

        return rowView;
    }
}