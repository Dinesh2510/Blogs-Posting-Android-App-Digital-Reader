package com.digital.reader.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
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
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.digital.reader.Helper.API.GET_ALL_POST_URL;

public class Activity_ViewMorePost extends AppCompatActivity {
    RecyclerView recyclerView_postlist;
    ArrayList<Post> postArrayList1 = new ArrayList<>();
    AdapterPostList adapterPostList;
    LinearLayout layout_allpostlist, lyt_failed;
    ShimmerFrameLayout shimmer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__view_more_post);

        initToolbar();
        shimmer.startShimmer();
        shimmer.setVisibility(View.VISIBLE);

        recyclerView_postlist = findViewById(R.id.recyclerView_all_post);
        layout_allpostlist = findViewById(R.id.layout_allpostlist);
        lyt_failed = findViewById(R.id.lyt_failed);
        adapterPostList = new AdapterPostList(this, postArrayList1);
        recyclerView_postlist.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_postlist.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView_postlist.setAdapter(adapterPostList);
        adapterPostList.notifyDataSetChanged();
        if (NetworkCheck.isConnect(getApplicationContext())) {
            loadPostAllData();
        } else {
            Toast.makeText(this, "You'r offline!", Toast.LENGTH_SHORT).show();
        }

    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_backspace);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Posts List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        shimmer = findViewById(R.id.shimmer_home);

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

    private void loadPostAllData() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_ALL_POST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        if (!response.equalsIgnoreCase("failure"))

                            try {
                                shimmer.setVisibility(View.GONE);
                                shimmer.stopShimmer();
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
                                }

                                //creating adapter object and setting it to recyclerview
                                adapterPostList = new AdapterPostList(Activity_ViewMorePost.this, postArrayList1);
                                //  recyclerView.setAdapter(memberAdapter);
                                setDataAdapter();

                            } catch (JSONException e) {
                                e.printStackTrace();

                                lyt_failed.setVisibility(View.VISIBLE);
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

                params.put("limit", "1");
                Log.d("parameter", "getParams: " + params);
                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void setDataAdapter() {
        if (postArrayList1.size() > 0) {
            adapterPostList = new AdapterPostList(Activity_ViewMorePost.this, postArrayList1);
            lyt_failed.setVisibility(View.GONE);
            shimmer.stopShimmer();
            shimmer.setVisibility(View.GONE);
            layout_allpostlist.setVisibility(View.VISIBLE);
            recyclerView_postlist.setVisibility(View.VISIBLE);
            recyclerView_postlist.setAdapter(adapterPostList);


        } else {
            shimmer.stopShimmer();
            shimmer.setVisibility(View.GONE);
            layout_allpostlist.setVisibility(View.GONE);
            lyt_failed.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
