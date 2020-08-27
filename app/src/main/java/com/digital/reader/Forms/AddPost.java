package com.digital.reader.Forms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.digital.reader.Helper.NetworkCheck;
import com.digital.reader.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;

import static com.digital.reader.Forms.LoginActivity.SHARED_PREFERENCES_NAME;
import static com.digital.reader.Forms.LoginActivity.USER_ID;

public class AddPost extends AppCompatActivity {
    LinearLayout lyt_failed;
    SwipeRefreshLayout mSwipeRefreshLayout;
    EditText post_image_path, post_title, post_weblink, post_message;
    Button upload_img, submit;
    String str_userid, url, postData, str_post_image_path, str_post_title, str_post_weblink, str_post_message, str_topic_id;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradiant(this);
        setContentView(R.layout.activity_add_post);

        intitToolbar();

        lyt_failed = findViewById(R.id.lyt_failed);
        showProgress(true);
        if (NetworkCheck.isConnect(getApplicationContext())) {
            // mywebview = (WebView) findViewById(R.id.webView);
            //mywebview.loadUrl("https://pixeldev.in/webservices/digital_reader/admin/add_post_app.php");
            showProgress(false);

        } else {
            showProgress(false);
            lyt_failed.setVisibility(View.VISIBLE);

        }
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);

    }

    private void intitToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_backspace);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Posts");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        str_userid = sharedPreferences.getString(USER_ID, "");

        post_image_path = findViewById(R.id.post_image_path);
        post_title = findViewById(R.id.post_title);
        post_weblink = findViewById(R.id.web_link);
        post_message = findViewById(R.id.post_msg);
        upload_img = findViewById(R.id.post_image);
        submit = findViewById(R.id.btn_submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_post_message = post_message.getText().toString();
                str_post_title = post_title.getText().toString();
                str_post_weblink = post_weblink.getText().toString();
                str_post_message = post_image_path.getText().toString();
                Toast.makeText(AddPost.this, "" + str_post_image_path + str_post_title + str_post_weblink + str_post_message, Toast.LENGTH_SHORT).show();


            }
        });

    }//                https://pixeldev.in/webservices/digital_reader/admin/add_post_code.php?post_title=demo&&web_link=demo&&topic_id=5&&post_detail=demo&&post_image=55555


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

