package com.wpfeedreader.android;

import com.wpfeedreader.android.Models.Post;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private List<Post> postsList;
    private ListView postList;
    private RelativeLayout layoutNoPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        postList = (ListView)findViewById(R.id.postList);
        layoutNoPosts = (RelativeLayout) findViewById(R.id.layoutNoPosts);
        layoutNoPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initContent();
            }
        });

        initContent();
    }

    private void initContent(){
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        // Request

        StringRequest request = new StringRequest(Request.Method.GET, Config.URL_POSTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String source) {
                JsonParser parser = new JsonParser();
                JsonArray postsArray = (JsonArray)parser.parse(source);
                postsList = new ArrayList<Post>();

                for(int i = 0; i < postsArray.size(); i ++){
                    JsonObject jsonPost = (JsonObject) postsArray.get(i);
                    Post post = new Post();
                    post.setID(jsonPost.get("id").getAsInt());
                    post.setTitle(jsonPost.get("title").getAsJsonObject().get("rendered").getAsString());
                    post.setContent(jsonPost.get("content").getAsJsonObject().get("rendered").getAsString());

                    // Thumb
                    String thumbUrl = jsonPost.get("_embedded").getAsJsonObject()
                            .get("wp:featuredmedia").getAsJsonArray().get(0).getAsJsonObject().get("media_details").getAsJsonObject()
                            .get("sizes").getAsJsonObject().get("thumbnail").getAsJsonObject().get("source_url").getAsString();
                    post.setThumbUrl(thumbUrl);

                    // Image
                    String imageUrl = jsonPost.get("_embedded").getAsJsonObject()
                            .get("wp:featuredmedia").getAsJsonArray().get(0).getAsJsonObject().get("media_details").getAsJsonObject()
                            .get("sizes").getAsJsonObject().get("full").getAsJsonObject().get("source_url").getAsString();
                    post.setImageUrl(imageUrl);

                    post.setShareUrl(jsonPost.get("link").getAsString());

                    // Author
                    String author = jsonPost.get("_embedded").getAsJsonObject()
                            .get("author").getAsJsonArray().get(0).getAsJsonObject().get("name").getAsString();
                    post.setAuthor(author);

                    postsList.add(post);
                }

                postList.setAdapter(new PostsListAdapter(MainActivity.this, postsList));
                progressDialog.dismiss();
                showList();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressDialog.dismiss();
                showTextMessage();
                Toast.makeText(MainActivity.this, "Errore durante il download degli articoli.", Toast.LENGTH_LONG).show();
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(MainActivity.this);
        rQueue.add(request);

        postList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Post post = postsList.get(position);

                Intent intent = new Intent(getApplicationContext(),PostDetails.class);
                intent.putExtra("postObject", post);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_contact:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL  , new String[]{"gurnari.dev@gmail.com"});
                try {
                    startActivity(Intent.createChooser(intent, getString(R.string.contact)));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this, getString(R.string.contact_error), Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.action_favorite:
                startActivity(new Intent(getApplicationContext(),Favourites.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void showList() {
        layoutNoPosts.setVisibility(View.GONE);
        postList.setVisibility(View.VISIBLE);
    }

    private void showTextMessage() {
        layoutNoPosts.setVisibility(View.VISIBLE);
        postList.setVisibility(View.GONE);
    }
}
