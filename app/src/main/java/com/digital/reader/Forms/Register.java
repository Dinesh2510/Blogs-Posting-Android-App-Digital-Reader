package com.digital.reader.Forms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
import com.digital.reader.Activities.MainActivity;
import com.digital.reader.EmailVerification.EmailVerify;
import com.digital.reader.Helper.AppController;
import com.digital.reader.Helper.NetworkCheck;
import com.digital.reader.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import static com.digital.reader.Helper.API.Email_Templete;
import static com.digital.reader.Helper.API.REGISTER_URL;
import static com.digital.reader.Helper.API.Send_EMAIL;

public class Register extends AppCompatActivity {
    //Pattern pattern_pwd = Pattern.compile("^[a-zA-Z0-9]+$");
    private static final int REQUEST_CODE_PICTURE = 500;
    String firstName;
    String lastName;
    String email;
    String password;
    LinearLayout lyt_linear;
    ImageView layt_avatar;
    PackageInfo pakgeInfo = null;
    JSONObject user_data;
    String token;
    private EditText et_fname, et_lname, et_email, etpassword;
    private Button btnregister;
    private TextView tvlogin;
    private ProgressBar progress_bar;
    private String fcm_id;
    private String version_code;
    private String version_name;
    private String device_type;
    private int number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradiant(this);
        setContentView(R.layout.activity_register);


        try {
            pakgeInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        getRandomNumberString();
        version_code = String.valueOf(pakgeInfo.versionCode);
        version_name = pakgeInfo.versionName;
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG_fc", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        token = task.getResult().getToken();

                        // Log and toast
//                        String msg = getString("", token);
//                        Log.d(TAG, msg);
                        Log.d("TAG_got", "onComplete: " + token);
                        //Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });
        Log.d("imp_code", "onCreate: " + "version_code" + version_code + "name:" + version_name);
        initViews();

        tvlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, LoginActivity.class);
                finish();
                startActivity(intent);

            }
        });
        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstName = et_fname.getText().toString();
                lastName = et_lname.getText().toString();
                email = et_email.getText().toString();
                password = etpassword.getText().toString();

                Log.d("userdata", "onClick: " + email + password);

                if (!firstName.isEmpty()) {
                    if (!lastName.isEmpty()) {
                        if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            if (!password.isEmpty()) {
                                if (NetworkCheck.isConnect(getApplicationContext())) {
                                    registerMe(firstName, lastName, email, password, version_code, version_name);
                                } else {
                                    Snackbar.make(lyt_linear, "Your Are Offline", Snackbar.LENGTH_SHORT).show();
                                    showLoading(false);

                                }

                            } else {
                                Snackbar.make(lyt_linear, "Enter the Valid Password", Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            Snackbar.make(lyt_linear, "Enter the Valid Email", Snackbar.LENGTH_SHORT).show();
                        }
                    } else {
                        Snackbar.make(lyt_linear, "Enter the Valid Last name", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(lyt_linear, "Enter the Valid First name", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void getRandomNumberString() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        String.format("%06d", number);
        Log.d("randdom", "getRandomNumberString: " + number);
    }

    private void initViews() {
        progress_bar = findViewById(R.id.progress_bar);
        et_fname = (EditText) findViewById(R.id.et_fname);
        et_lname = (EditText) findViewById(R.id.et_lname);
        et_email = (EditText) findViewById(R.id.et_email);
        etpassword = (EditText) findViewById(R.id.etpassword);
        btnregister = findViewById(R.id.btn_reg);
        layt_avatar = findViewById(R.id.lyt_avatar);
        lyt_linear = findViewById(R.id.lyt_linear);
        tvlogin = (TextView) findViewById(R.id.tvlogin);
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

    private void registerMe(final String firstName, final String lastName, final String email, final String password, final String version_code, final String version_name) {
        showLoading(true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showJSON(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), R.string.failed_save, Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                final Map<String, String> parameter = new HashMap<String, String>();

                parameter.put("first_name", firstName);
                parameter.put("last_name", lastName);
                parameter.put("email", email);
                parameter.put("password", password);
                parameter.put("fcm_id", token);
                parameter.put("email_code", String.valueOf(number));
                parameter.put("version_code", version_code);
                parameter.put("version_name", version_name);


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
            String res = jsonObject.getString("response");
            String msg = jsonObject.getString("msg");
            if (res.equals("success")) {
                Log.d("msg_ala", "showJSON: " + res);
                Toast.makeText(Register.this, " Registration Successful!", Toast.LENGTH_SHORT).show();
                SendToTEmail();
            } else if (res.equals("failure")) {
                showLoading(false);
                Toast.makeText(this, "" + msg, Toast.LENGTH_SHORT).show();

            } else {
                showLoading(false);
                Toast.makeText(Register.this, "something went Wrong!", Toast.LENGTH_SHORT).show();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void SendToTEmail() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Send_EMAIL, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showEmailJSON(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Opps! We are't able sent verification Mail.", Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                final Map<String, String> parameter = new HashMap<String, String>();
                parameter.put("email", email);
                parameter.put("name", firstName);

                Log.d("SendToT_Email", "" + parameter);

                return parameter;

            }
        };


        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(stringRequest, "Email_Module");

    }

    private void showEmailJSON(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String res = jsonObject.getString("response");
            String status = jsonObject.getString("status");
            if (status.equals("success")) {

                Log.d("msg_ala", "showJSON: " + res);
                Toast.makeText(Register.this, "" + res, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Register.this, EmailVerify.class);
                intent.putExtra("email", email);
                finish();
                startActivity(intent);

            } else if (res.equals("failure")) {
                showLoading(false);
                Toast.makeText(this, "" + res, Toast.LENGTH_SHORT).show();

            } else {
                showLoading(false);
                Toast.makeText(Register.this, "something went Wrong!", Toast.LENGTH_SHORT).show();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void showLoading(final boolean show) {
        btnregister.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
        progress_bar.setVisibility(!show ? View.INVISIBLE : View.VISIBLE);
    }

}
