package com.digital.reader.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.digital.reader.Adapter.AdapterTopicList;
import com.digital.reader.Helper.AppController;
import com.digital.reader.Helper.NetworkCheck;
import com.digital.reader.Model.Topic;
import com.digital.reader.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.digital.reader.Helper.API.GET_ALL_TOPIC_URL;

public class ActivityTopicList extends AppCompatActivity {
    RecyclerView recyclerView_topic;
    ArrayList<Topic> topicArrayList = new ArrayList<>();
    AdapterTopicList topicAdapter;
    LinearLayout lyt_failed, lyt_nopost;
    SwipeRefreshLayout mSwipeRefreshLayout;
    TextView failed_message, net_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradiant(this);
        setContentView(R.layout.activity_topic_list);

        initToolbar();

    }

    protected void setStatusBarGradiant(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            Drawable background = activity.getResources().getDrawable(R.drawable.ic_gradient_bg);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_backspace);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Topic Lists");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        lyt_failed = findViewById(R.id.lyt_failed);
        lyt_nopost = findViewById(R.id.lyt_nopost);

        recyclerView_topic = findViewById(R.id.recyclerView_all_TopicsList);
        topicAdapter = new AdapterTopicList(this, topicArrayList);
        recyclerView_topic.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_topic.setAdapter(topicAdapter);
        topicAdapter.notifyDataSetChanged();

        if (NetworkCheck.isConnect(getApplicationContext())) {
            showProgress(true);
            loadTopicData();
        } else {
            showProgress(false);
            lyt_failed.setVisibility(View.VISIBLE);
            recyclerView_topic.setVisibility(View.GONE);
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code here
                // Toast.makeText(getApplicationContext(), "Works!", Toast.LENGTH_LONG).show();
                if (NetworkCheck.isConnect(getApplicationContext())) {

                    loadTopicData();
                    topicArrayList.clear();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.no_internet_text, Toast.LENGTH_SHORT).show();
                    lyt_failed.setVisibility(View.VISIBLE);
                    recyclerView_topic.setVisibility(View.GONE);
                }
                // To keep animation for 4 seconds
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Stop animation (This will be after 3 seconds)
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 4000); // Delay in millis
            }
        });

    }

    private void loadTopicData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_ALL_TOPIC_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!response.equalsIgnoreCase("failure"))
                            try {
                                JSONObject jo = new JSONObject(response);
                                String res = jo.getString("response");
                                Log.d("up_res", "parseUpdateData: " + res);

                                if (res.equals("failure")) {
                                    showProgress(false);
                                    lyt_nopost.setVisibility(View.VISIBLE);


                                } else {
                                    JSONObject obj = new JSONObject(response);
                                    JSONArray array = obj.getJSONArray("response");


                                    Log.d("respondsf", "" + response);
                                    // JSONArray array = new JSONArray("response");

                                    for (int i = 0; i < array.length(); i++) {

                                        JSONObject rlist = array.getJSONObject(i);
                                        topicArrayList.add(new Topic(
                                                rlist.getString("topic_id"),
                                                rlist.getString("topic_title"),
                                                rlist.getString("topic_image")
                                        ));
                                        topicAdapter = new AdapterTopicList(ActivityTopicList.this, topicArrayList);
                                        setDataAdapterTrendList();

                                    }
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showProgress(false);
                    }
                });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(stringRequest);

    }

    private void showProgress(final boolean show) {
        View progress_loading = findViewById(R.id.progress_loading);
        progress_loading.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void setDataAdapterTrendList() {
        if (topicArrayList.size() > 0) {

            topicAdapter = new AdapterTopicList(ActivityTopicList.this, topicArrayList);
            recyclerView_topic.setVisibility(View.VISIBLE);
            recyclerView_topic.setAdapter(topicAdapter);
            lyt_nopost.setVisibility(View.GONE);

            showProgress(false);
        } else {
            showProgress(false);
            recyclerView_topic.setVisibility(View.GONE);
            lyt_nopost.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
