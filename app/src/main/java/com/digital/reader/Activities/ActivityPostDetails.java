package com.digital.reader.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.digital.reader.Forms.LoginActivity;
import com.digital.reader.Helper.AppController;
import com.digital.reader.Helper.NetworkCheck;
import com.digital.reader.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.digital.reader.Forms.LoginActivity.PREMIUMUSER;
import static com.digital.reader.Forms.LoginActivity.SHARED_PREFERENCES_NAME;
import static com.digital.reader.Forms.LoginActivity.USER_ID;
import static com.digital.reader.Helper.API.BookMarkPost;
import static com.digital.reader.Helper.API.CheckBookMark;
import static com.digital.reader.Helper.API.DeleteBookmarkPost;
import static com.digital.reader.Helper.API.ImageUrl;
import static com.digital.reader.Helper.API.Send_Like;

public class ActivityPostDetails extends AppCompatActivity {
    String str_id, str_username, str_title, str_content, str_userpremiumflag, str_premium, str_img, str_link, str_date, str_userId, str_like;
    TextView username, total_comment, title, date, count_like, txt_content, txt_info;
    ImageView like_btn, bkmark_btn, btn_share, image;
    LinearLayout lyt_like, lyt_bkmark, lyt_comment;
    Button link, premium_btn;
    SharedPreferences sharedPreferences;
    String str_postlike, str_postcmts;
    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradiant(this);
        setContentView(R.layout.activity_post_details);
        GetIntentData();

        initToolbar();
        CheckBookMark();
        //Patterns.WEB_URL.matcher(url).matches();
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.SMART_BANNER);
        //  adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");//test
        adView.setAdUnitId("ca-app-pub-7783850843188004/8032321198");//OG
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

//        List<String> testDeviceIds = Arrays.asList("95793537B99A9216EBEB2D783ADBF818");
//        RequestConfiguration configuration =
//                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
//        MobileAds.setRequestConfiguration(configuration);

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    //check if bookmark or not also get value for like and comment count
    private void CheckBookMark() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CheckBookMark, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String check = jsonObject.getString("response");
                    JSONObject msg = jsonObject.getJSONObject("msg");

                    if (check.equals("success")) {
                        bkmark_btn.setImageResource(R.drawable.ic_bookmark_in);
                        lyt_bkmark.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (NetworkCheck.isConnect(getApplicationContext())) {
                                    RemovedBookMark();
                                } else {
                                    Toast.makeText(ActivityPostDetails.this, "You'r offline!", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    } else {
                        bkmark_btn.setImageResource(R.drawable.ic_bookmark_b);
                    }
                    str_postcmts = msg.getString("post_comment");
                    str_postlike = msg.getString("post_like");
                    count_like.setText(str_postlike);
                    total_comment.setText(str_postcmts);
                    Log.d("login_response", "" + response + "check" + check + "A=" + str_postcmts + "C=" + str_postlike);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("_log", "onErrorResponse: " + error.toString());
                // Toast.makeText(Login.this, R.string.cb_no_internet_error, Toast.LENGTH_LONG).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                final Map<String, String> params = new HashMap<String, String>();
                params.put("post_id", str_id);
                params.put("user_id", str_userId);
                Log.d("params_login", "getParams: " + params);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        AppController.getInstance().addToRequestQueue(stringRequest, "Login_Module");

    }

    //removing form boookmark
    private void RemovedBookMark() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DeleteBookmarkPost,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.d("logrrd", "onResponse: " + jsonObject);
                            String res = jsonObject.getString("response");
                            if (res.equals("success")) {
                                Toast.makeText(getApplicationContext(), "removing from bookmark...", Toast.LENGTH_SHORT).show();
                                bkmark_btn.setImageResource(R.drawable.ic_bookmark_b);
                            } else {
                                bkmark_btn.setImageResource(R.drawable.ic_bookmark_in);

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

                params.put("user_id", str_userId);
                params.put("post_id", str_id);
                Log.d("parameter_removed", "getParams: " + params);
                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(stringRequest);


    }

    private void GetIntentData() {

        str_id = getIntent().getStringExtra("id");
        str_username = getIntent().getStringExtra("username");
        str_title = getIntent().getStringExtra("title");
        str_content = getIntent().getStringExtra("content");
        str_img = getIntent().getStringExtra("image");
        str_date = getIntent().getStringExtra("date");
        str_link = getIntent().getStringExtra("link");
        str_like = getIntent().getStringExtra("like");
        str_premium = getIntent().getStringExtra("premium");

        Log.d("bhau", "GetIntentData: " + str_premium);

        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        str_userId = sharedPreferences.getString(USER_ID, "");
        str_userpremiumflag = sharedPreferences.getString(PREMIUMUSER, "");
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
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorTextAction), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        date = findViewById(R.id.post_date);
        username = findViewById(R.id.post_username);
        title = findViewById(R.id.title);
        lyt_like = findViewById(R.id.lyt_like);
        lyt_bkmark = findViewById(R.id.lyt_bkmark);
        txt_content = findViewById(R.id.txt_content);
        image = findViewById(R.id.image);
        link = findViewById(R.id.link);
        txt_info = findViewById(R.id.txt_info);
        premium_btn = findViewById(R.id.premium_btn);
        btn_share = findViewById(R.id.btn_share);
        lyt_comment = findViewById(R.id.lyt_comment);
        bkmark_btn = findViewById(R.id.bkmark_btn);
        count_like = findViewById(R.id.count_like);
        total_comment = findViewById(R.id.total_comment);


        date.setText(str_date);
        username.setText(str_username);
        title.setText(str_title);

        premium_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityPayment.class);
                startActivity(intent);
            }
        });

        Glide.with(getApplicationContext())
                .load(ImageUrl + str_img)
                .placeholder(R.drawable.ic_gradient_bg)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(image);

        Log.d("Matcher_tag", "initToolbar: " + str_premium.equals(str_userpremiumflag));
        Log.d("Matcher_tag_2", "initToolbar: " + str_premium + str_userpremiumflag);

        if (str_premium.equals("1") && str_userpremiumflag.equals("1")) {
            // Toast.makeText(this, "Its premium ! match id ", Toast.LENGTH_SHORT).show();
            txt_content.setText(str_content);
//            txt_content.setWidth(100);
//            premium_btn.setVisibility(View.VISIBLE);
//            txt_info.setVisibility(View.VISIBLE);

        } else if (str_premium.equals("1")) {
            txt_content.setWidth(100);
            txt_content.setMaxLines(5);
            txt_content.setEllipsize(TextUtils.TruncateAt.END);
            txt_content.setMaxWidth(100);
            txt_content.setText(str_content);
            // Toast.makeText(this, "post is premium! user not ", Toast.LENGTH_SHORT).show();
            premium_btn.setVisibility(View.VISIBLE);
            txt_info.setVisibility(View.VISIBLE);


        } else {
            //  Toast.makeText(this, "for all free!", Toast.LENGTH_SHORT).show();
            txt_content.setText(str_content);

        }


        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Hey! There is an Interesting Post About* " + str_title + "*" + "To Read More about the this Post Download the  '*Digital Reader*'   Link: " + "https://play.google.com/store/apps/details?id=com.digital.reader";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Download Digital Reader");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });
        //if user not login goto login
        if (!str_userId.equals("")) {

            lyt_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Likeit();
                }
            });

            lyt_bkmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SendBookMarkData();
                }
            });
            lyt_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityPostDetails.this.openDialog();
                }
            });

        } else {
            lyt_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoginPlease();
                }
            });

            lyt_bkmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoginPlease();
                }
            });

            lyt_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoginPlease();
                }
            });

        }

        if (str_link != null) {
            if (!str_link.equals("") && !str_link.equals("-") && !str_link.equals("null")) {

                link.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String url = str_link;

                        if (!url.startsWith("http://") && !url.startsWith("https://")) {
                            url = "http://" + url;
                        }
//                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                        startActivity(browserIntent);
                        Intent intent = new Intent(getApplicationContext(), ActivityWebView.class);
                        intent.putExtra("EXTRA_OBJC", url);
                        startActivity(intent);

                    }
                });
            } else {
                link.setVisibility(View.GONE);
            }
        } else {
            link.setVisibility(View.GONE);
        }


    }

    private void openDialog() {
        ExampleDialog.display(getSupportFragmentManager());

    }

    private void Likeit() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Send_Like, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String check = jsonObject.getString("response");
                    if (check.equals("success")) {
                        Toast.makeText(ActivityPostDetails.this, "Thank you!", Toast.LENGTH_SHORT).show();

                    } else {
                        Log.d("TAG", "onResponse: Response");
                    }

                    Log.d("login_response", "" + response + "check" + check);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("_log", "onErrorResponse: " + error.toString());
                // Toast.makeText(Login.this, R.string.cb_no_internet_error, Toast.LENGTH_LONG).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                final Map<String, String> params = new HashMap<String, String>();
                params.put("post_id", str_id);
                params.put("count", "1");
                params.put("user_id", str_userId);
                Log.d("params_login", "getParams: " + params);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        AppController.getInstance().addToRequestQueue(stringRequest, "Login_Module");

    }

    //Bookmark the post
    private void SendBookMarkData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, BookMarkPost, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String check = jsonObject.getString("response");

                    Toast.makeText(ActivityPostDetails.this, "Adding to BookmarkList", Toast.LENGTH_SHORT).show();


                    if (check.equals("success")) {
                        bkmark_btn.setImageResource(R.drawable.ic_bookmark_in);
                        lyt_bkmark.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (NetworkCheck.isConnect(getApplicationContext())) {
                                    RemovedBookMark();
                                } else {
                                    Toast.makeText(ActivityPostDetails.this, "You'r offline!", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                        //lyt_bkmark.setEnabled(false); //disable to click
                    }


                    Log.d("login_response", "" + response + "check" + check);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("_log", "onErrorResponse: " + error.toString());
                // Toast.makeText(Login.this, R.string.cb_no_internet_error, Toast.LENGTH_LONG).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                final Map<String, String> params = new HashMap<String, String>();
                params.put("post_id", str_id);
                params.put("user_id", str_userId);
                Log.d("params_login", "getParams: " + params);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        AppController.getInstance().addToRequestQueue(stringRequest, "Login_Module");

    }

    private void LoginPlease() {
        Toast.makeText(ActivityPostDetails.this, "Please Login!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
