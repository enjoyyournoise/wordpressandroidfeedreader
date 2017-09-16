package com.wpfeedreader.android;

import com.wpfeedreader.android.Database.ReaderDbHelper;
import com.wpfeedreader.android.Database.ReaderFavourites;
import com.wpfeedreader.android.Models.Post;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Favourites extends AppCompatActivity {

    private ReaderDbHelper dbHelper;

    private ProgressDialog progressDialog;
    private List<Post> postsList;
    private ListView postList;

    private TextView txtNoFavourites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        dbHelper = new ReaderDbHelper(this);

        postsList = new ArrayList<Post>();
        postList = (ListView) findViewById(R.id.postList);

        txtNoFavourites = (TextView) findViewById(R.id.txtNoFavourites);

        progressDialog = new ProgressDialog(Favourites.this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();


        // Get datas from database
        fillPostsListFromDatabase();

        progressDialog.dismiss();

        if (postsList.size() <= 0) {
            showTextMessage();
        } else {
            postList.setAdapter(new PostsListAdapter(Favourites.this, postsList));
            showList();
        }

        postList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Post post = postsList.get(position);

                Intent intent = new Intent(getApplicationContext(), PostDetails.class);
                intent.putExtra("postObject", post);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        postsList.clear();
        fillPostsListFromDatabase();
        if (postsList.size() <= 0) {
            showTextMessage();

        } else {
            postList.setAdapter(new PostsListAdapter(Favourites.this, postsList));
            showList();
        }
        super.onResume();
    }

    private void fillPostsListFromDatabase() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * from " + ReaderFavourites.Favourites.TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Post post = new Post();
                post.setID(cursor.getInt(cursor.getColumnIndex(ReaderFavourites.Favourites.COLUMN_NAME_ID)));
                post.setTitle(cursor.getString(cursor.getColumnIndex(ReaderFavourites.Favourites.COLUMN_NAME_TITLE)));
                post.setAuthor(cursor.getString(cursor.getColumnIndex(ReaderFavourites.Favourites.COLUMN_NAME_AUTHOR)));
                post.setContent(cursor.getString(cursor.getColumnIndex(ReaderFavourites.Favourites.COLUMN_NAME_CONTENT)));
                post.setShareUrl(cursor.getString(cursor.getColumnIndex(ReaderFavourites.Favourites.COLUMN_NAME_SHAREURL)));
                post.setThumbUrl(cursor.getString(cursor.getColumnIndex(ReaderFavourites.Favourites.COLUMN_NAME_THUMBURL)));
                post.setImageUrl(cursor.getString(cursor.getColumnIndex(ReaderFavourites.Favourites.COLUMN_NAME_IMAGEURL)));

                postsList.add(post);
                cursor.moveToNext();
            }
        }
    }

    private void showList() {
        txtNoFavourites.setVisibility(View.GONE);
        postList.setVisibility(View.VISIBLE);
    }

    private void showTextMessage() {
        txtNoFavourites.setVisibility(View.VISIBLE);
        postList.setVisibility(View.GONE);
    }
}
