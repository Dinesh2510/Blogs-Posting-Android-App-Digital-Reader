package com.digital.reader.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.digital.reader.Adapter.AdapterPostList;
import com.digital.reader.Helper.AppController;
import com.digital.reader.Helper.NetworkCheck;
import com.digital.reader.Model.Post;
import com.digital.reader.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.digital.reader.Helper.API.Get_TopicWise_Post;

public class ActivityTopicWisePostList extends AppCompatActivity {
    String str_id, str_title;
    RecyclerView recyclerView_postlist;
    ArrayList<Post> postArrayList1 = new ArrayList<>();
    AdapterPostList adapterPostList;
    LinearLayout lyt_failed, lyt_nopost;
    SwipeRefreshLayout mSwipeRefreshLayout;
    TextView failed_message, net_msg;
    Button btn_retry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradiant(this);
        setContentView(R.layout.activity_topic_wise_post_list);

        str_id = getIntent().getStringExtra("id");
        str_title = getIntent().getStringExtra("title");

        initToolbar();
        recyclerView_postlist = findViewById(R.id.recyclerView_all_post);
        adapterPostList = new AdapterPostList(this, postArrayList1);
        recyclerView_postlist.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_postlist.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView_postlist.setAdapter(adapterPostList);
        adapterPostList.notifyDataSetChanged();

        if (NetworkCheck.isConnect(getApplicationContext())) {
            showProgress(true);
            loadPostAllData();
        } else {
            showProgress(false);
            lyt_failed.setVisibility(View.VISIBLE);
            recyclerView_postlist.setVisibility(View.GONE);

        }
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code here
                // Toast.makeText(getApplicationContext(), "Works!", Toast.LENGTH_LONG).show();
                if (NetworkCheck.isConnect(getApplicationContext())) {

                    loadPostAllData();
                    postArrayList1.clear();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.no_internet_text, Toast.LENGTH_SHORT).show();
                    lyt_failed.setVisibility(View.VISIBLE);
                    recyclerView_postlist.setVisibility(View.GONE);
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

    private void loadPostAllData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Get_TopicWise_Post,
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
                                        postArrayList1.add(new Post(
                                                rlist.getString("post_id"),
                                                rlist.getString("post_title"),
                                                rlist.getString("post_sub_title"),
                                                rlist.getString("post_content"),
                                                rlist.getString("post_userid"),
                                                rlist.getString("post_username"),
                                                rlist.getString("post_date"),
                                                rlist.getString("post_image"),
                                                rlist.getString("post_link"),
                                                rlist.getString("topics"),
                                                rlist.getString("post_view"),
                                                rlist.getString("post_like"),
                                                rlist.getString("premium_flag")
                                        ));
                                        adapterPostList = new AdapterPostList(ActivityTopicWisePostList.this, postArrayList1);
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
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("topic_id", str_id);
                Log.d("parameter", "getParams: " + params);
                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(stringRequest);

    }

    private void setDataAdapterTrendList() {
        if (postArrayList1.size() > 0) {

            adapterPostList = new AdapterPostList(ActivityTopicWisePostList.this, postArrayList1);
            recyclerView_postlist.setVisibility(View.VISIBLE);
            recyclerView_postlist.setAdapter(adapterPostList);
            lyt_nopost.setVisibility(View.GONE);

            showProgress(false);
        } else {
            showProgress(false);
            recyclerView_postlist.setVisibility(View.GONE);
            lyt_nopost.setVisibility(View.VISIBLE);

        }
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
        getSupportActionBar().setTitle(str_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        lyt_failed = findViewById(R.id.lyt_failed);
        lyt_nopost = findViewById(R.id.lyt_nopost);
        failed_message = findViewById(R.id.failed_message);
        net_msg = findViewById(R.id.net_msg);
        btn_retry = findViewById(R.id.failed_retry);


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
