package com.wpfeedreader.android;

import com.wpfeedreader.android.Database.ReaderDbHelper;
import com.wpfeedreader.android.Database.ReaderFavourites.Favourites;
import com.wpfeedreader.android.Models.Post;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

/**
 * Created by Diego on 06/09/2017.
 */

public class PostDetails extends AppCompatActivity {

    private Menu toolbarMenu;

    private TextView title, author;
    private WebView content;
    private ImageView postImage;
    private Post post;

    private ReaderDbHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        dbHelper = new ReaderDbHelper(this);

        post = (Post) getIntent().getSerializableExtra("postObject");

        if(post != null){
            postImage = (ImageView) findViewById(R.id.img);
            Picasso.with(this)
                    .load(post.getImageUrl())
                    .error(R.drawable.place_holder)
                    .into(postImage);

            title = (TextView) findViewById(R.id.title);
            author = (TextView) findViewById(R.id.author);
            content = (WebView) findViewById(R.id.content);

            title.setText(Html.fromHtml(post.getTitle()));
            author.setText(getString(R.string.author_prefix) + " " + Html.fromHtml(post.getAuthor()));

            Document doc = Jsoup.parse(post.getContent());
            Whitelist wl = new Whitelist().basic(); // https://jsoup.org/apidocs/org/jsoup/safety/Whitelist.html#basic--
            String cleanedText = Jsoup.clean(doc.body().html(), wl);

            content.loadData(cleanedText, "text/html", "UTF-8");
        } else {
            Toast.makeText(this, getString(R.string.post_details_error), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_SUBJECT, Html.fromHtml(post.getTitle()).toString());
                share.putExtra(Intent.EXTRA_TEXT, post.getShareUrl());
                startActivity(Intent.createChooser(share, getString(R.string.share)));
                return true;

            case R.id.action_favorite:
                if(post != null){
                    if(isFavourite(post.getID()))
                        removeFavourite(post);
                    else
                        saveFavourite(post);
                }else{
                    Toast.makeText(this, getString(R.string.favourite_error), Toast.LENGTH_SHORT).show();
                }
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        toolbarMenu = menu;
        tintFavouriteIcon(isFavourite(post.getID())); // Check if as already a favourite.
        return super.onCreateOptionsMenu(menu);
    }

    private void saveFavourite(Post post){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Favourites.COLUMN_NAME_ID, post.getID());
        values.put(Favourites.COLUMN_NAME_TITLE, post.getTitle());
        values.put(Favourites.COLUMN_NAME_AUTHOR, post.getAuthor());
        values.put(Favourites.COLUMN_NAME_CONTENT, post.getContent());
        values.put(Favourites.COLUMN_NAME_SHAREURL, post.getShareUrl());
        values.put(Favourites.COLUMN_NAME_THUMBURL, post.getThumbUrl());
        values.put(Favourites.COLUMN_NAME_IMAGEURL, post.getImageUrl());

        long newRowId = db.insert(Favourites.TABLE_NAME, null, values);
        if(newRowId > 0){
            tintFavouriteIcon(true);
            Toast.makeText(this, getString(R.string.favourite_success), Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, getString(R.string.favourite_error), Toast.LENGTH_SHORT).show();

        Log.e("Added Row: ", String.valueOf(newRowId));

    }

    private void removeFavourite(Post post){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long deletedRowId = db.delete(Favourites.TABLE_NAME, Favourites.COLUMN_NAME_ID + " = " + post.getID(), null);
        if(deletedRowId > 0){
            tintFavouriteIcon(false);
            Log.e("Deleted Row: ", String.valueOf(deletedRowId));
            Toast.makeText(this, getString(R.string.favourite_delete_success), Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(this, getString(R.string.favourite_error), Toast.LENGTH_SHORT).show();
        }

    }

    private boolean isFavourite(int postId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String Query = "SELECT * from " + Favourites.TABLE_NAME + " WHERE " + Favourites.COLUMN_NAME_ID + " = " + postId;
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    private void tintFavouriteIcon(boolean tint){
        String tintColor = tint ? "#DFC33F" : "#FFFFFF";
        Drawable fav = toolbarMenu.getItem(0).getIcon();
        if (fav != null) {
            fav.mutate();
            fav.setColorFilter(Color.parseColor(tintColor), PorterDuff.Mode.SRC_ATOP);
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}