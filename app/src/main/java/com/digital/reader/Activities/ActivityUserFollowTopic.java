package com.digital.reader.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.digital.reader.Adapter.AdapterBookmarkList;
import com.digital.reader.Adapter.AdapterTopicList;
import com.digital.reader.Adapter.AdapterUserTopicList;
import com.digital.reader.Helper.AppController;
import com.digital.reader.Helper.NetworkCheck;
import com.digital.reader.Model.Post;
import com.digital.reader.Model.Topic;
import com.digital.reader.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.digital.reader.Forms.LoginActivity.SHARED_PREFERENCES_NAME;
import static com.digital.reader.Forms.LoginActivity.USER_ID;
import static com.digital.reader.Helper.API.GET_ALL_TOPIC_URL;
import static com.digital.reader.Helper.API.GET_ALL_User_TOPIC_URL;

public class ActivityUserFollowTopic extends AppCompatActivity {
    public static ActivityUserFollowTopic auft;
    TextView failed_message, net_msg;
    SharedPreferences sharedPreferences;
    String str_userid;

    RecyclerView recyclerView_topic;
    ArrayList<Topic> topicArrayList = new ArrayList<>();
    AdapterUserTopicList adapterUserTopicList;
    LinearLayout lyt_failed, lyt_nopost;
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_follow_topic);
        inSherf();
        initToolbar();
        auft = this;
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
        adapterUserTopicList = new AdapterUserTopicList(this, topicArrayList);
        recyclerView_topic.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_topic.setAdapter(adapterUserTopicList);
        adapterUserTopicList.notifyDataSetChanged();

        if (NetworkCheck.isConnect(getApplicationContext())) {
            showProgress(true);
            loadTopicData();
        } else {
            showProgress(false);
            lyt_failed.setVisibility(View.VISIBLE);
            lyt_nopost.setVisibility(View.GONE);
            recyclerView_topic.setVisibility(View.GONE);
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

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

    public void loadTopicData() {
        topicArrayList.clear();
        adapterUserTopicList = new AdapterUserTopicList(getApplicationContext(), topicArrayList);
        recyclerView_topic.setAdapter(adapterUserTopicList);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_ALL_User_TOPIC_URL,
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
                                        adapterUserTopicList = new AdapterUserTopicList(getApplicationContext(), topicArrayList);
                                        setDataAdapterList();

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
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("user_id", str_userid);
                Log.d("parameter", "getParams: " + params);
                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(stringRequest);

    }

    private void setDataAdapterList() {
        if (topicArrayList.size() > 0) {

            adapterUserTopicList = new AdapterUserTopicList(getApplicationContext(), topicArrayList);
            recyclerView_topic.setVisibility(View.VISIBLE);
            recyclerView_topic.setAdapter(adapterUserTopicList);
            lyt_nopost.setVisibility(View.GONE);

            showProgress(false);
        } else {
            showProgress(false);
            recyclerView_topic.setVisibility(View.GONE);
            lyt_nopost.setVisibility(View.VISIBLE);

        }
    }

    private void inSherf() {
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        str_userid = sharedPreferences.getString(USER_ID, "");
    }

    private void showProgress(final boolean show) {
        View progress_loading = findViewById(R.id.progress_loading);
        progress_loading.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
