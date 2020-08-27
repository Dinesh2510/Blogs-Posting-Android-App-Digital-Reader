package com.digital.reader.room;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.digital.reader.Helper.AppController;
import com.digital.reader.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListOfPosts extends AppCompatActivity {
    PostViewModel postViewModel;
    List<PostData> postDataList = new ArrayList<>();
    PostAdapter adapter;
    RecyclerView recyclerView;
    TextView tv_empty;
    LinearLayout linear_empty;
    SwipeRefreshLayout swipe_refresh = null;
    ActionBar actionBar;
    Toolbar toolbar;
    Boolean alreadyExecuted = false;
    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postlist);


        initViews();
        setRecyclerView();
        setAdapters();
        setViewModels();
        setonClickListeners();

        if (!alreadyExecuted) {
            GetPostList();
            alreadyExecuted = true;
        }
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.SMART_BANNER);
        //  adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");//test
        adView.setAdUnitId("ca-app-pub-7783850843188004/8032321198");//OG
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

       /* List<String> testDeviceIds = Arrays.asList("95793537B99A9216EBEB2D783ADBF818");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);*/

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


    }


    private void setEmptyLayout(int size) {
        if (size == 0) {
            tv_empty.setText("Nothing found");
            tv_empty.setVisibility(View.VISIBLE);
            linear_empty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tv_empty.setVisibility(View.GONE);
            linear_empty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }


    private void initViews() {
        Toast.makeText(getApplicationContext(), "pull to refresh the list!", Toast.LENGTH_SHORT).show();

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_backspace);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorTextAction), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("List Of Posts");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tv_empty = findViewById(R.id.tv_empty);
        linear_empty = findViewById(R.id.linear_empty);
        tv_empty.setText("Nothing found");

        recyclerView = findViewById(R.id.RecyclerView);
        swipe_refresh = findViewById(R.id.swipe_refresh);
    }


    private void setRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
    }


    private void setAdapters() {
        adapter = new PostAdapter();
        recyclerView.setAdapter(adapter);
    }


    private void setViewModels() {
        postViewModel = ViewModelProviders.of(this).get(PostViewModel.class);
        postViewModel.GetAllPostList().observe(this, new Observer<List<PostData>>() {
            @Override
            public void onChanged(List<PostData> postData) {

                postDataList = postData;
                adapter.submitList(postData);
                setEmptyLayout(postData.size());
            }
        });
    }


    private void setonClickListeners() {

        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //tv_empty.setText("Loading...");
                Toast.makeText(ListOfPosts.this, "Loading List...", Toast.LENGTH_SHORT).show();
                GetPostList();
            }
        });


    }

    public void GetPostList() {
        String url = "https://pixeldev.in/webservices/digital_reader/GetAllPostList.php";
        String tag_json_obj = "json_obj_req";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    String serverResponse = object.getString("response");
                    Log.d("_RESPONSE", "" + response);

                    if (!serverResponse.equalsIgnoreCase("failure")) {

                        JSONArray listData = object.getJSONArray("response");

                        postViewModel.deleteAllPost();

                        JSONArray jsonArray = new JSONArray(serverResponse);

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject dataObject = jsonArray.getJSONObject(i);

                            String post_id = dataObject.getString("post_id");

                            String post_title = dataObject.getString("post_title");
                            String post_sub_title = dataObject.getString("post_sub_title");
                            String post_content = dataObject.getString("post_content");
                            String post_userid = dataObject.getString("post_userid");
                            String post_username = dataObject.getString("post_username");
                            String post_image = dataObject.getString("post_image");
                            String post_date = dataObject.getString("post_date");
                            String post_link = dataObject.getString("post_link");
                            String topics = dataObject.getString("topics");
                            String post_view = dataObject.getString("post_view");
                            String post_like = dataObject.getString("post_like");
                            String premium_flag = dataObject.getString("premium_flag");


                            PostData data = new PostData(post_id, post_title, post_sub_title, post_content, post_userid,
                                    post_username, post_date, post_image, post_link, topics, post_view, post_like, premium_flag);
                            postViewModel.insert(data);


                        }

                    }
                    handleSwipeRefresh();
                } catch (JSONException e) {
                    handleSwipeRefresh();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleSwipeRefresh();
            }
        });
        AppController.getInstance().addToRequestQueue(request, tag_json_obj);
    }

    private void handleSwipeRefresh() {
        if (swipe_refresh.isRefreshing()) {
            swipe_refresh.setRefreshing(false);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
