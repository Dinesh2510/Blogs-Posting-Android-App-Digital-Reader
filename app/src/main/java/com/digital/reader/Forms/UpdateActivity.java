package com.digital.reader.Forms;

import androidx.appcompat.app.AlertDialog;
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
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.digital.reader.Activities.ActivityReceipt;
import com.digital.reader.Activities.ActivityUserBookMarkList;
import com.digital.reader.Activities.ActivityUserFollowTopic;
import com.digital.reader.Activities.MainActivity;
import com.digital.reader.Helper.AppController;
import com.digital.reader.Helper.NetworkCheck;
import com.digital.reader.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.digital.reader.Forms.LoginActivity.EMAIL;
import static com.digital.reader.Forms.LoginActivity.FNAME;
import static com.digital.reader.Forms.LoginActivity.LNAME;
import static com.digital.reader.Forms.LoginActivity.PREMIUMUSER;
import static com.digital.reader.Forms.LoginActivity.PWD;
import static com.digital.reader.Forms.LoginActivity.SHARED_PREFERENCES_NAME;
import static com.digital.reader.Forms.LoginActivity.USER_ID;
import static com.digital.reader.Helper.API.Get_Count_Update;
import static com.digital.reader.Helper.API.Update_URL;

public class UpdateActivity extends AppCompatActivity {
    public static String userid = "", userfname = "", userlname = "", useremail = "", userpwd = "";
    String str_email, str_pwd, str_fname, str_lname, str_userid, str_passowrd, userpremiumflag;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    Button submit_btn, view_recp;
    ProgressBar progress_bar;

    TextView u_name, u_email, post_count, bookmark_count, following_count;
    ImageView u_image;
    LinearLayout expandableView, layt_bk, layt_post, layt_follow;
    Pattern pattern_pwd = Pattern.compile("^[a-zA-Z0-9]+$");
    String row_userid, row_post, row_follow, row_bk;
    private EditText et_fname, et_lname, et_email, etpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradiant(this);
        setContentView(R.layout.activity_update);


        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        str_userid = sharedPreferences.getString(USER_ID, "");
        str_fname = sharedPreferences.getString(FNAME, "");
        str_lname = sharedPreferences.getString(LNAME, "");
        str_email = sharedPreferences.getString(EMAIL, "");
        str_pwd = sharedPreferences.getString(PWD, "");
        userpremiumflag = sharedPreferences.getString(PREMIUMUSER, "");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_backspace);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Intent intent = new Intent(getApplicationContext(), AddPost.class);
                startActivity(intent);*/
                Toast.makeText(UpdateActivity.this, "soon...", Toast.LENGTH_SHORT).show();
            }
        });
        et_fname = (EditText) findViewById(R.id.user_fname);
        et_lname = (EditText) findViewById(R.id.user_lname);
        et_email = (EditText) findViewById(R.id.user_email);
        etpassword = (EditText) findViewById(R.id.user_pwd);
        progress_bar = findViewById(R.id.progress_bar);

        submit_btn = findViewById(R.id.update_btn);
        view_recp = findViewById(R.id.view_recp);
        expandableView = findViewById(R.id.expandableView);
        post_count = findViewById(R.id.post_count);
        bookmark_count = findViewById(R.id.bookmark_count);
        following_count = findViewById(R.id.following_count);
        layt_bk = findViewById(R.id.layt_bk);
        layt_post = findViewById(R.id.layt_post);
        layt_follow = findViewById(R.id.layt_follow);

        u_name = findViewById(R.id.u_name);
        u_image = findViewById(R.id.u_image);
        u_email = findViewById(R.id.u_email);

        et_fname.setText(str_fname);
        et_lname.setText(str_lname);
        et_email.setText(str_email);
        et_email.setEnabled(false);
        etpassword.setText(str_pwd);


        u_email.setText(str_email);
        u_name.setText(str_fname + " " + str_lname);
        GetCOunt();
        if (userpremiumflag.equals("1")) {
            view_recp.setVisibility(View.VISIBLE);
        } else {
            view_recp.setVisibility(View.GONE);
        }
        view_recp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityReceipt.class);
                startActivity(intent);
            }
        });
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_email = et_email.getText().toString().trim();
                str_fname = et_fname.getText().toString().trim();
                str_lname = et_lname.getText().toString().trim();
                str_passowrd = etpassword.getText().toString().trim();

                if (!str_email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(str_email).matches()) {

                    if (!str_passowrd.isEmpty() && pattern_pwd.matcher(str_passowrd).matches()) {

                        if (!str_fname.isEmpty()) {

                            if (!str_lname.isEmpty()) {

                                showLoading(true);
                                if (NetworkCheck.isConnect(getApplicationContext())) {

                                    updateData();

                                } else {
                                    showLoading(false);
                                    showDialogFailed(getString(R.string.no_internet_text));
                                }
                            } else {
                                Snackbar.make(expandableView, "Enter the Last Name", Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            Snackbar.make(expandableView, "Enter the First Name", Snackbar.LENGTH_SHORT).show();
                        }
                    } else {
                        Snackbar.make(expandableView, "Enter the Valid Password", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(expandableView, "Enter the Valid Email", Snackbar.LENGTH_SHORT).show();
                }

            }
        });
        layt_bk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityUserBookMarkList.class);
                startActivity(intent);

            }
        });
        layt_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), UserPostList.class);
//                startActivity(intent);
                Toast.makeText(UpdateActivity.this, "Coming Soon...", Toast.LENGTH_SHORT).show();

            }
        });
        layt_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityUserFollowTopic.class);
                startActivity(intent);


            }
        });
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

    private void updateData() {

        final String json_obj = "tag_json_obj";

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Update_URL, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                parseUpdateData(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error_long", "onErrorResponse: " + error.toString());
            }
        }) {
            //ur params
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", str_userid);
                params.put("first_name", str_fname);
                params.put("last_name", str_lname);
                params.put("email", str_email);
                params.put("password", str_passowrd);
                Log.d("user_flag", "" + params);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(stringRequest, json_obj);


    }

    private void parseUpdateData(String response) {

        try {
            JSONObject jo = new JSONObject(response);
            String res = jo.getString("response");
            Log.d("up_res", "parseUpdateData: " + res);
            if (!res.equalsIgnoreCase("failure")) {

                Log.d("sherf", "called" + useremail);
                sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString(USER_ID, str_userid);
                editor.putString(FNAME, str_fname);
                editor.putString(LNAME, str_lname);
                editor.putString(EMAIL, str_email);
                editor.putString(PWD, str_passowrd);
                editor.apply();

                Toast.makeText(this, "Data Updated!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(this, "Fail", Toast.LENGTH_SHORT).show();
            }
        } catch (
                JSONException e) {
            e.printStackTrace();
        }
    }

    private void showLoading(final boolean show) {
        submit_btn.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
        progress_bar.setVisibility(!show ? View.INVISIBLE : View.VISIBLE);
    }

    private void showDialogFailed(String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(message);
        dialog.setPositiveButton(R.string.OK, null);
        dialog.setCancelable(true);
        dialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void GetCOunt() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Get_Count_Update, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showCountJSON(response);
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
                parameter.put("user_id", str_userid);

                Log.d("SendToTEmail", "" + parameter);

                return parameter;

            }
        };


        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(stringRequest, "Count_Module");

    }

    private void showCountJSON(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject phone = jsonObject.getJSONObject("response");
            row_userid = phone.getString("user_id");
            row_post = phone.getString("row_post");
            row_follow = phone.getString("row_follow");
            row_bk = phone.getString("row_bk");

            post_count.setText(row_post);
            bookmark_count.setText(row_bk);
            following_count.setText(row_follow);
            Log.d("_oo_oo_oo", "showCountJSON: " + row_follow + row_bk + row_post);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
