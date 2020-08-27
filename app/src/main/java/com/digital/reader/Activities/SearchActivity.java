package com.digital.reader.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.digital.reader.Model.Post;
import com.digital.reader.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.digital.reader.Helper.API.GETSearchData;
import static com.digital.reader.Helper.API.REGISTER_URL;

public class SearchActivity extends AppCompatActivity {
    EditText et_search;
    LinearLayout lyt_search;
    String str_find;
    ImageView find, bt_reset;
    RecyclerView recyclerView_postlist;
    ArrayList<Post> postArrayList = new ArrayList<>();
    AdapterPostList adapterPostList;
    LinearLayout layout_allpostlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradiant(this);
        setContentView(R.layout.activity_search);
        et_search = findViewById(R.id.et_search);
        bt_reset = findViewById(R.id.bt_clear);
        layout_allpostlist = findViewById(R.id.layout_allpostlist);
        find = findViewById(R.id.find);
        recyclerView_postlist = findViewById(R.id.recyclerView_all_post);
        lyt_search = findViewById(R.id.search_lyt);
        adapterPostList = new AdapterPostList(this, postArrayList);
        recyclerView_postlist.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_postlist.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView_postlist.setAdapter(adapterPostList);
        adapterPostList.notifyDataSetChanged();


        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_find = et_search.getText().toString();
                if (!str_find.isEmpty()) {
                    lyt_search.setVisibility(View.GONE);
                    showProgress(true);
                   /* SearchKEY();
                    postArrayList.clear();*/

                } else {
                    Toast.makeText(SearchActivity.this, "Enter the keyword", Toast.LENGTH_SHORT).show();

                }
            }
        });
        bt_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_search.setText("");
                /*postArrayList.clear();
                recyclerView_postlist.invalidate();
                adapterPostList.notifyDataSetChanged();*/


            }
        });

    }

    private void SearchKEY() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, GETSearchData, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showProgress(true);
                showJSON(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), R.string.failed_text, Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                final Map<String, String> parameter = new HashMap<String, String>();

                parameter.put("search_key", str_find);
                Log.d("parameter", "" + parameter);

                return parameter;

            }
        };


        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(stringRequest, "Login_Module");

    }

    private void showJSON(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            Log.d("logrrd", "onResponse: " + jsonObject);
            String res = jsonObject.getString("status");
            if (res.equals("false")) {
                showProgress(false);
                recyclerView_postlist.setVisibility(View.GONE);
                lyt_search.setVisibility(View.VISIBLE);
            } else {
                showProgress(false);
                JSONArray array = jsonObject.getJSONArray("message");
                for (int i = 0; i < array.length(); i++) {

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
                adapterPostList = new AdapterPostList(getApplicationContext(), postArrayList);
                //setDataAdapter();
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }

    }

    private void setDataAdapter() {
        if (postArrayList.size() > 0) {
            adapterPostList = new AdapterPostList(getApplicationContext(), postArrayList);
            lyt_search.setVisibility(View.GONE);
//            shimmer.setVisibility(View.GONE);
//            shimmer.stopShimmer();
            layout_allpostlist.setVisibility(View.VISIBLE);
            recyclerView_postlist.setVisibility(View.VISIBLE);
            recyclerView_postlist.setAdapter(adapterPostList);


        } else {
            //  shimmer.setVisibility(View.GONE);
            //shimmer.stopShimmer();
            layout_allpostlist.setVisibility(View.GONE);
            // lyt_failed.setVisibility(View.VISIBLE);

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

    private void showProgress(final boolean show) {
        View progress_loading = findViewById(R.id.progress_loading);
        progress_loading.setVisibility(show ? View.VISIBLE : View.GONE);
    }

}
