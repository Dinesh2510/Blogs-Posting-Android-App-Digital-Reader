package com.digital.reader.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.digital.reader.Adapter.AdapterBookmarkList;
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

import static com.digital.reader.Forms.LoginActivity.SHARED_PREFERENCES_NAME;
import static com.digital.reader.Forms.LoginActivity.USER_ID;
import static com.digital.reader.Helper.API.GetListOfBookmark;

public class ActivityUserBookMarkList extends AppCompatActivity {
    public static ActivityUserBookMarkList aubl;

    LinearLayout lyt_failed, lyt_nopost;
    SwipeRefreshLayout mSwipeRefreshLayout;
    TextView failed_message, net_msg;
    RecyclerView recyclerView_bk;
    AdapterBookmarkList adapterBookmarkList;
    ArrayList<Post> postArrayList = new ArrayList<>();
    SharedPreferences sharedPreferences;
    String str_userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradiant(this);
        setContentView(R.layout.activity_user_book_mark_list);

        aubl = this;

        inSherf();
        initToolbar();

    }

    private void inSherf() {
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        str_userid = sharedPreferences.getString(USER_ID, "");
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
        getSupportActionBar().setTitle("Bookmark Lists");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        lyt_failed = findViewById(R.id.lyt_failed);
        lyt_nopost = findViewById(R.id.lyt_nopost);

        recyclerView_bk = findViewById(R.id.recyclerView_all_bk);
        adapterBookmarkList = new AdapterBookmarkList(this, postArrayList);
        recyclerView_bk.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_bk.setAdapter(adapterBookmarkList);
        adapterBookmarkList.notifyDataSetChanged();

        if (NetworkCheck.isConnect(getApplicationContext())) {
            showProgress(true);
            loadBkList();
        } else {
            showProgress(false);
            lyt_failed.setVisibility(View.VISIBLE);
            recyclerView_bk.setVisibility(View.GONE);
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code here
                // Toast.makeText(getApplicationContext(), "Works!", Toast.LENGTH_LONG).show();
                if (NetworkCheck.isConnect(getApplicationContext())) {

                    loadBkList();
                    postArrayList.clear();
                    lyt_failed.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.no_internet_text, Toast.LENGTH_SHORT).show();
                    lyt_failed.setVisibility(View.VISIBLE);
                    lyt_nopost.setVisibility(View.GONE);
                    recyclerView_bk.setVisibility(View.GONE);
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

    public void loadBkList() {
        postArrayList.clear();
        adapterBookmarkList = new AdapterBookmarkList(getApplicationContext(), postArrayList);
        recyclerView_bk.setAdapter(adapterBookmarkList);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, GetListOfBookmark,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.d("logrrd", "onResponse: " + jsonObject);
                            String res = jsonObject.getString("response");
                            if (res.equals("failure")) {
                                showProgress(false);
                                recyclerView_bk.setVisibility(View.GONE);
                                lyt_nopost.setVisibility(View.VISIBLE);
                            } else {
                                showProgress(false);
                                JSONArray array = jsonObject.getJSONArray("response");
                                for (int i = 0; i < array.length(); i++) {
//
                                    JSONObject rlist = array.getJSONObject(i);

                                    postArrayList.add(new Post(
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
                                }
                                adapterBookmarkList = new AdapterBookmarkList(ActivityUserBookMarkList.this, postArrayList);
                                setDataAdapterTrendList();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

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

    private void showProgress(final boolean show) {
        View progress_loading = findViewById(R.id.progress_loading);
        progress_loading.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void setDataAdapterTrendList() {
        if (postArrayList.size() > 0) {
            adapterBookmarkList = new AdapterBookmarkList(ActivityUserBookMarkList.this, postArrayList);
            recyclerView_bk.setVisibility(View.VISIBLE);
            recyclerView_bk.setAdapter(adapterBookmarkList);
            lyt_nopost.setVisibility(View.GONE);

            showProgress(false);
        } else {
            showProgress(false);
            recyclerView_bk.setVisibility(View.GONE);
            lyt_nopost.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
