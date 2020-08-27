package com.digital.reader.Forms;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.digital.reader.Activities.ActivityWebView;
import com.digital.reader.Activities.MainActivity;
import com.digital.reader.EmailVerification.EmailVerify;
import com.digital.reader.EmailVerification.LoginVerify;
import com.digital.reader.Helper.AppController;
import com.digital.reader.Helper.NetworkCheck;
import com.digital.reader.R;
import com.google.android.material.snackbar.Snackbar;


import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.digital.reader.Helper.API.LOGIN_URL;

public class LoginActivity extends AppCompatActivity {
    public static final String SHARED_PREFERENCES_NAME = "login_portal";
    public static final String USER_ID = "user_id";
    public static final String FNAME = "fname";
    public static final String LNAME = "lname";
    public static final String EMAIL = "email";
    public static final String PWD = "pwd";
    public static final String PREMIUMUSER = "premiumuser";
    public static final String SKIP = "skip";
    // Pattern pattern_pwd = Pattern.compile("^[a-zA-Z0-9]+$");
    public static String userid = "", userfname = "", userlname = "", useremail = "", userpwd = "", userPremiumFlag = "";
    ImageButton show_pass;
    String fcm_id;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    TextView skip_txt;
    String email, password;
    RelativeLayout rl_pwd;
    LinearLayout ll_lay;
    ProgressBar progress_bar;
    PackageInfo pakgeInfo = null;
    private EditText etUname, etPass;
    private Button btnlogin;
    private TextView tvreg, tvverify, forget_password;
    private View parent_view;
    private String version_code;
    private String version_name;
    private String device_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradiant(this);
        setContentView(R.layout.activity_login);


        initViews();

        forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://pixeldev.in/app/forget_password.php";
                Intent intent = new Intent(getApplicationContext(), ActivityWebView.class);
                intent.putExtra("EXTRA_OBJC", url);
                startActivity(intent);
            }
        });


        Log.d("fcm_id", "onCreate: " + fcm_id);
        tvverify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, LoginVerify.class);
                finish();
                startActivity(intent);
            }
        });
        skip_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                finish();
                startActivity(intent);
            }
        });
        tvreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, Register.class);
                finish();
                startActivity(intent);
            }
        });

        show_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setActivated(!v.isActivated());
                if (v.isActivated()) {
                    etPass.setTransformationMethod(null);
                } else {
                    etPass.setTransformationMethod(new PasswordTransformationMethod());
                }
                etPass.setSelection(etPass.getText().length());
            }
        });


        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    pakgeInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                version_code = String.valueOf(pakgeInfo.versionCode);
                version_name = pakgeInfo.versionName;
                email = etUname.getText().toString().trim();
                password = etPass.getText().toString().trim();

                Log.d("userdata", "onClick: " + email + password);

                if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                    if (!password.isEmpty()) {

                        showLoading(true);
                        if (NetworkCheck.isConnect(getApplicationContext())) {

                            loginUser();

                        } else {
                            showLoading(false);
                            showDialogFailed(getString(R.string.no_internet_text));
                        }

                    } else {
                        Snackbar.make(rl_pwd, "Enter the Valid Password", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(ll_lay, "Enter the Valid Email", Snackbar.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void initViews() {
        ll_lay = findViewById(R.id.ll_lay);
        rl_pwd = findViewById(R.id.rl_pwd);
        etUname = (EditText) findViewById(R.id.etemail);
        etPass = (EditText) findViewById(R.id.etpassword);
        btnlogin = (Button) findViewById(R.id.btn);
        progress_bar = findViewById(R.id.progress_bar);
        show_pass = findViewById(R.id.show_pass);
        tvreg = (TextView) findViewById(R.id.tvreg);
        tvverify = (TextView) findViewById(R.id.tvverify);
        forget_password = (TextView) findViewById(R.id.forgot_password);
        skip_txt = (TextView) findViewById(R.id.skip_txt);

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

    private void loginUser() {
        showLoading(true);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("login_response", "" + response);

                parseLoginData(response);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showLoading(false);
                Log.d("_log", "onErrorResponse: " + error.toString());
                // Toast.makeText(Login.this, R.string.cb_no_internet_error, Toast.LENGTH_LONG).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                final Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);
                params.put("version_code", version_code);
                params.put("version_name", version_name);
                Log.d("params_login", "getParams: " + params);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        AppController.getInstance().addToRequestQueue(stringRequest, "Login_Module");

    }

    private void parseLoginData(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String check = jsonObject.getString("response");
            String msg = jsonObject.getString("msg");
            Log.d("msg_server", "parseLoginData: " + msg);
            Log.d("check_server", "parseLoginData: " + check);
            Log.d("CHECKING_THIS", "parseLoginData: " + msg.equals("success"));

            if (msg.equals("success")) {
                JSONObject innerObj = new JSONObject(check);

                userid = innerObj.getString("user_id");
                userfname = innerObj.getString("first_name");
                userlname = innerObj.getString("last_name");
                useremail = innerObj.getString("email");
                userpwd = innerObj.getString("password");
                userPremiumFlag = innerObj.getString("userpremiumflag");

                sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString(USER_ID, userid);
                editor.putString(FNAME, userfname);
                editor.putString(LNAME, userlname);
                editor.putString(EMAIL, useremail);
                editor.putString(PWD, userpwd);
                editor.putString(PREMIUMUSER, userPremiumFlag);
                editor.apply();
                Toast.makeText(this, "Welcome To Digital Reader", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                finish();
                startActivity(intent);

            } else if (check.equals("failure")) {
                Toast.makeText(this, "" + msg, Toast.LENGTH_SHORT).show();
                showLoading(false);

            } else {
                Toast.makeText(this, "Something wents wrong!" + "", Toast.LENGTH_SHORT).show();
                showLoading(false);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showLoading(final boolean show) {
        btnlogin.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
        progress_bar.setVisibility(!show ? View.INVISIBLE : View.VISIBLE);
    }


    private void showDialogFailed(String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(message);
        dialog.setPositiveButton(R.string.OK, null);
        dialog.setCancelable(true);
        dialog.show();
    }


}
