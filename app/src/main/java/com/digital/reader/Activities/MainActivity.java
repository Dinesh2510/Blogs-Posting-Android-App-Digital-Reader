package com.digital.reader.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.digital.reader.Adapter.AdapterPostList;
import com.digital.reader.Adapter.AdapterTrendingPost;
import com.digital.reader.Adapter.TopicAdapter;
import com.digital.reader.Forms.LoginActivity;
import com.digital.reader.Forms.UpdateActivity;
import com.digital.reader.Helper.AppController;
import com.digital.reader.Helper.NetworkCheck;
import com.digital.reader.Model.Post;
import com.digital.reader.Model.Topic;
import com.digital.reader.R;
import com.digital.reader.room.ListOfPosts;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.digital.reader.Forms.LoginActivity.FNAME;
import static com.digital.reader.Forms.LoginActivity.LNAME;
import static com.digital.reader.Forms.LoginActivity.PREMIUMUSER;
import static com.digital.reader.Forms.LoginActivity.SHARED_PREFERENCES_NAME;
import static com.digital.reader.Forms.LoginActivity.USER_ID;
import static com.digital.reader.Helper.API.GET_ALL_POST_URL;
import static com.digital.reader.Helper.API.GET_ALL_TOPIC_URL;
import static com.digital.reader.Helper.API.GET_ALL_TRENDLIST_URL;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUESTS = 1;
    Button failed_retry, btn_pay;
    ProgressBar progress_bar;
    TextView txt_viewmoreTopic, txt_viewmorePost;
    TextView name, login_logout, welcom_txt, settings;
    String str_email, str_pwd, str_fname, str_lname, str_userid, str_passowrd, userpremiumflag;
    SharedPreferences sharedPreferences;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RelativeLayout rel_avatar;
    LinearLayout nav_menu_rate, lyt_failed;
    LinearLayout layout_topic, layout_trendpost, layout_allpostlist;
    ImageView avatar;
    RecyclerView recyclerView_trend;
    ArrayList<Post> postArrayList1 = new ArrayList<>();
    AdapterTrendingPost adapterTrendingPost;
    RecyclerView recyclerView_topic;
    ArrayList<Topic> topicArrayList = new ArrayList<>();
    TopicAdapter topicAdapter;
    RecyclerView recyclerView_postlist;
    ArrayList<Post> postArrayList = new ArrayList<>();
    AdapterPostList adapterPostList;
    AdView mAdView;
    private ActionBar actionBar;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private long backPressedTime;
    private Toast backToast;
    private ShimmerFrameLayout shimmer;

    private static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            Log.i("TAG", "Permission granted: " + permission);
            return true;
        }
        Log.i("TAG", "Permission NOT granted: " + permission);
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradiant(this);
        setContentView(R.layout.activity_main);

        getRuntimePermissions();
        initToolbar();
        initSherf();
        initDrawerMenu();

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


        Log.d("userpremiumflag_1425", "onCreate: " + userpremiumflag);


        if (NetworkCheck.isConnect(getApplicationContext())) {
            AllMethodS();
        } else {
            Toast.makeText(getApplicationContext(), R.string.no_internet_text, Toast.LENGTH_SHORT).show();
            layout_trendpost.setVisibility(View.GONE);
            layout_allpostlist.setVisibility(View.GONE);
            layout_topic.setVisibility(View.GONE);
            btn_pay.setVisibility(View.GONE);

            lyt_failed.setVisibility(View.VISIBLE);
            shimmer.setVisibility(View.GONE);
            shimmer.stopShimmer();
            failed_retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progress_bar.setVisibility(View.VISIBLE);
                    failed_retry.setVisibility(View.GONE);
                    AllMethodS();
                }
            });
        }
        txt_viewmoreTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityTopicList.class);
                startActivity(intent);

            }
        });
        txt_viewmorePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ListOfPosts.class);
                startActivity(intent);

            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Toast.makeText(getApplicationContext(), "Refreshing Data!", Toast.LENGTH_LONG).show();

                if (NetworkCheck.isConnect(getApplicationContext())) {
                    loadTrendData();
                    postArrayList1.clear();
                    loadTopicData();
                    topicArrayList.clear();
                    loadPostAllData();
                    postArrayList.clear();

                } else if (!NetworkCheck.isConnect(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), R.string.no_internet_text, Toast.LENGTH_SHORT).show();
                    layout_trendpost.setVisibility(View.GONE);
                    layout_allpostlist.setVisibility(View.GONE);
                    layout_topic.setVisibility(View.GONE);
                    btn_pay.setVisibility(View.GONE);

                    lyt_failed.setVisibility(View.VISIBLE);
                    shimmer.setVisibility(View.GONE);
                    shimmer.stopShimmer();
                    failed_retry.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            progress_bar.setVisibility(View.VISIBLE);
                            progress_bar.setMax(10);
                            failed_retry.setVisibility(View.GONE);
                            AllMethodS();
                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(), R.string.failed_text, Toast.LENGTH_SHORT).show();
                    lyt_failed.setVisibility(View.VISIBLE);
                    shimmer.setVisibility(View.GONE);
                    shimmer.stopShimmer();
                    failed_retry.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AllMethodS();
                        }
                    });


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

    private void AllMethodS() {
        loadTrendData();
        loadPostAllData();
        loadTopicData();
    }

    private void loadTrendData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_ALL_TRENDLIST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        if (!response.equalsIgnoreCase("failure"))

                            try {
                                shimmer.setVisibility(View.GONE);
                                shimmer.stopShimmer();
                                JSONObject obj = new JSONObject(response);
                                JSONArray array = obj.getJSONArray("response");


                                Log.d("respondstrend", "" + response);
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

                                adapterTrendingPost = new AdapterTrendingPost(MainActivity.this, postArrayList1);
                                setDataAdapterTrendList();

                            } catch (JSONException e) {
                                e.printStackTrace();
                                shimmer.setVisibility(View.GONE);
                                shimmer.stopShimmer();
                                lyt_failed.setVisibility(View.VISIBLE);
                            }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void setDataAdapterTrendList() {
        if (postArrayList1.size() > 0) {

            adapterTrendingPost = new AdapterTrendingPost(MainActivity.this, postArrayList1);
            lyt_failed.setVisibility(View.GONE);
            shimmer.setVisibility(View.GONE);
            shimmer.stopShimmer();
            layout_trendpost.setVisibility(View.VISIBLE);
            recyclerView_trend.setVisibility(View.VISIBLE);
            recyclerView_trend.setAdapter(adapterTrendingPost);
        } else {
            shimmer.setVisibility(View.GONE);
            shimmer.stopShimmer();
            layout_trendpost.setVisibility(View.GONE);
            lyt_failed.setVisibility(View.VISIBLE);

        }
    }

    private void loadTopicData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_ALL_TOPIC_URL,
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

                                    topicArrayList.add(new Topic(
                                            rlist.getString("topic_id"),
                                            rlist.getString("topic_title"),
                                            rlist.getString("topic_image")
                                    ));
                                }

                                //creating adapter object and setting it to recyclerview
                                topicAdapter = new TopicAdapter(MainActivity.this, topicArrayList);
                                //  recyclerView.setAdapter(memberAdapter);
                                setDataAdapterTopic();

                            } catch (JSONException e) {
                                e.printStackTrace();
                                shimmer.setVisibility(View.GONE);
                                shimmer.stopShimmer();
                                lyt_failed.setVisibility(View.VISIBLE);
                            }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(stringRequest);
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

                                //creating adapter object and setting it to recyclerview
                                adapterPostList = new AdapterPostList(MainActivity.this, postArrayList);
                                //  recyclerView.setAdapter(memberAdapter);
                                setDataAdapter();

                            } catch (JSONException e) {
                                e.printStackTrace();
                                shimmer.setVisibility(View.GONE);
                                shimmer.stopShimmer();
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

                params.put("limit", "0");
                Log.d("parameter", "getParams: " + params);
                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void setDataAdapterTopic() {
        if (topicArrayList.size() > 0) {

            topicAdapter = new TopicAdapter(MainActivity.this, topicArrayList);
            lyt_failed.setVisibility(View.GONE);
            shimmer.setVisibility(View.GONE);
            shimmer.stopShimmer();
            layout_topic.setVisibility(View.VISIBLE);
            recyclerView_topic.setVisibility(View.VISIBLE);
            recyclerView_topic.setAdapter(topicAdapter);
        } else {
            shimmer.setVisibility(View.GONE);
            shimmer.stopShimmer();
            layout_topic.setVisibility(View.GONE);
            lyt_failed.setVisibility(View.VISIBLE);


        }
    }

    private void setDataAdapter() {
        if (postArrayList.size() > 0) {
            adapterPostList = new AdapterPostList(MainActivity.this, postArrayList);
            lyt_failed.setVisibility(View.GONE);
            shimmer.setVisibility(View.GONE);
            shimmer.stopShimmer();
            layout_allpostlist.setVisibility(View.VISIBLE);
            recyclerView_postlist.setVisibility(View.VISIBLE);
            recyclerView_postlist.setAdapter(adapterPostList);
            if (userpremiumflag.equals("1")) {
                btn_pay.setVisibility(View.GONE);
            } else {
                btn_pay.setVisibility(View.VISIBLE);
            }

        } else {
            shimmer.setVisibility(View.GONE);
            shimmer.stopShimmer();
            layout_allpostlist.setVisibility(View.GONE);
            lyt_failed.setVisibility(View.VISIBLE);

        }
    }


    private void DialogForLogout() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.confirmation);
        dialog.setMessage(R.string.logout_confirmation_text);
        dialog.setNegativeButton(R.string.CANCEL, null);
        dialog.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ClearAllSherf();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void initSherf() {
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        str_userid = sharedPreferences.getString(USER_ID, "");
        str_fname = sharedPreferences.getString(FNAME, "");
        str_lname = sharedPreferences.getString(LNAME, "");
        userpremiumflag = sharedPreferences.getString(PREMIUMUSER, "");
    }

    private void initToolbar() {

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.app_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        shimmer = findViewById(R.id.shimmer_home);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        lyt_failed = findViewById(R.id.lyt_failed);
        failed_retry = findViewById(R.id.failed_retry);
        btn_pay = findViewById(R.id.btn_pay);
        progress_bar = findViewById(R.id.progress_bar);
        txt_viewmoreTopic = findViewById(R.id.txt_viewmoreTopic);
        txt_viewmorePost = findViewById(R.id.txt_viewmorePost);


        shimmer.setVisibility(View.VISIBLE);
        shimmer.startShimmer();
        recyclerView_trend = findViewById(R.id.recyclerView_trend);
        layout_trendpost = findViewById(R.id.layout_trendpost);
        adapterTrendingPost = new AdapterTrendingPost(this, postArrayList);
        recyclerView_trend.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView_trend.setAdapter(adapterTrendingPost);
        adapterTrendingPost.notifyDataSetChanged();

        recyclerView_topic = findViewById(R.id.recyclerView_all_topics);
        layout_topic = findViewById(R.id.layout_topic);
        topicAdapter = new TopicAdapter(this, topicArrayList);
        recyclerView_topic.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView_topic.setAdapter(topicAdapter);
        topicAdapter.notifyDataSetChanged();


        recyclerView_postlist = findViewById(R.id.recyclerView_all_post);
        layout_allpostlist = findViewById(R.id.layout_allpostlist);
        adapterPostList = new AdapterPostList(this, postArrayList1);
        recyclerView_postlist.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_postlist.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView_postlist.setAdapter(adapterPostList);
        adapterPostList.notifyDataSetChanged();
    }

    private void initDrawerMenu() {
        NavigationView nav_view = findViewById(R.id.nav_view);
        drawer = findViewById(R.id.drawer_layout);
        name = nav_view.findViewById(R.id.name);
        login_logout = nav_view.findViewById(R.id.login_logout);
        settings = nav_view.findViewById(R.id.settings);
        avatar = nav_view.findViewById(R.id.avatar);
        rel_avatar = nav_view.findViewById(R.id.rel_avatar);
        nav_menu_rate = nav_view.findViewById(R.id.nav_menu_rate);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settings = new Intent(getApplicationContext(), ActivitySettings.class);
                startActivity(settings);
            }
        });
        if (!str_userid.equals("")) {

            name.setText(str_fname + " " + str_lname);
            //  Glide.with(this).load(R.drawable.ic_user).placeholder(R.drawable.ic_gradient_bg).into(avatar);
            login_logout.setText("Logout");

            //onclick goto update
            rel_avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), UpdateActivity.class);
                    startActivity(intent);
                }
            });

            btn_pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), ActivityPayment.class);
                    startActivity(intent);
                }
            });


            login_logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogForLogout();
                }
            });


        } else {
/*
            Glide.with(this).load(R.drawable.digital_logo).placeholder(R.drawable.ic_gradient_bg).into(avatar);
*/

            rel_avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }
            });

            login_logout.setText("Login");
            login_logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loginScreen();
                }
            });

            btn_pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(), "Please Login!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);

                }
            });

        }


    }

    private void loginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
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

    private void ClearAllSherf() {
        SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        Drawable drawable = toolbar.getOverflowIcon();
        drawable.mutate();
        drawable.setColorFilter(getResources().getColor(R.color.grey_90), PorterDuff.Mode.SRC_ATOP);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menu_id = item.getItemId();
        if (menu_id == android.R.id.home) {

            if (!drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.openDrawer(GravityCompat.START);
            } else {
                drawer.openDrawer(GravityCompat.END);
            }
        } else if (menu_id == R.id.action_search) {
            Intent main = new Intent(getApplicationContext(), SearchActivity.class);
            startActivity(main);
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onDrawerMenuClick(View view) {
        String title = actionBar.getTitle().toString();
        int menu_id = view.getId();
        switch (menu_id) {
            case R.id.nav_menu_home:
                Intent main = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(main);
                break;

            case R.id.nav_menu_topic:
                if (!str_userid.equals("")) {
                    Intent intentt = new Intent(getApplicationContext(), ActivityTopicList.class);
                    startActivity(intentt);
                } else {
                    Toast.makeText(getApplicationContext(), "Please Login!", Toast.LENGTH_SHORT).show();
                    Intent intentsave = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intentsave);
                }
                break;

            case R.id.nav_menu_saved:
                if (!str_userid.equals("")) {
                    Intent intentt = new Intent(getApplicationContext(), ActivityUserBookMarkList.class);
                    startActivity(intentt);
                    // Toast.makeText(this, "coming soon...", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getApplicationContext(), "Please Login!", Toast.LENGTH_SHORT).show();
                    Intent intentsave = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intentsave);
                }
                break;
         /*   case R.id.nav_menu_idea:
                if (!str_userid.equals("")) {
                    Intent intentt = new Intent(getApplicationContext(), ActivityNews.class);
                    startActivity(intentt);
                } else {
                    Toast.makeText(getApplicationContext(), "Please Login!", Toast.LENGTH_SHORT).show();
                    Intent intentsave = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intentsave);
                }
                break;*/
            case R.id.nav_menu_more_app:
                Intent intent4 = new Intent(android.content.Intent.ACTION_VIEW);

                intent4.setData(Uri.parse("https://play.google.com/store/apps/dev?id=7204380673008740984"));

                startActivity(intent4);

                break;
            case R.id.nav_menu_rate:
                Intent rate = new Intent(android.content.Intent.ACTION_VIEW);

                rate.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.digital.reader"));

                startActivity(rate);
                break;
            case R.id.nav_menu_about:
                Intent intenta = new Intent(getApplicationContext(), ActivityAbout.class);
                startActivity(intenta);

                break;
        }
        actionBar.setTitle(title);
        drawer.closeDrawers();
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }

        backPressedTime = System.currentTimeMillis();
    }


    private String[] getRequiredPermissions() {
        try {
            PackageInfo info = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (Exception e) {
            return new String[0];
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(getApplicationContext(), permission)) {
                return false;
            }
        }
        return true;
    }

    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(MainActivity.this, permission)) {
                allNeededPermissions.add(permission);
            }
        }
        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(MainActivity.this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i("TAG", "Permission granted!");
        if (allPermissionsGranted()) {
            //onLocationChanged(location);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
