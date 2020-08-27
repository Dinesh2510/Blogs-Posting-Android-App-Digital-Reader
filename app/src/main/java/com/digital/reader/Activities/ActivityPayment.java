package com.digital.reader.Activities;

import androidx.appcompat.app.ActionBar;
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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.digital.reader.Forms.UpdateActivity;
import com.digital.reader.Helper.AppController;
import com.digital.reader.R;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.digital.reader.Forms.LoginActivity.EMAIL;
import static com.digital.reader.Forms.LoginActivity.FNAME;
import static com.digital.reader.Forms.LoginActivity.LNAME;
import static com.digital.reader.Forms.LoginActivity.PREMIUMUSER;
import static com.digital.reader.Forms.LoginActivity.SHARED_PREFERENCES_NAME;
import static com.digital.reader.Forms.LoginActivity.USER_ID;
import static com.digital.reader.Helper.API.Add_Payment_Data;
import static com.digital.reader.Helper.API.GET_COST;

public class ActivityPayment extends AppCompatActivity implements PaymentResultListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    TextView btn_pay;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor = null;
    String str_email, str_fname, str_lname, str_userid, str_premiumflag;
    String userpremiumflag;
    String price;
    private ActionBar actionBar;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradiant(this);

        setContentView(R.layout.activity_payment);

        Checkout.preload(getApplicationContext());
        initToolbar();
        SharedPref();
        GetCost();
        btn_pay = findViewById(R.id.btn_pay);
        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPayment();
            }
        });


    }

    private void GetCost() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_COST,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        if (!response.equalsIgnoreCase("failure"))
                            try {

                                JSONObject obj = new JSONObject(response);
                                JSONArray array = obj.getJSONArray("response");


                                Log.d("respondsf", "" + response);
                                // JSONArray array = new JSONArray("response");

                                for (int i = 0; i < array.length(); i++) {

                                    JSONObject rlist = array.getJSONObject(i);
                                    price = rlist.getString("price");
                                }
                                Log.d("String_price", "onResponse: " + price);

                            } catch (JSONException e) {
                                e.printStackTrace();
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
        toolbar = findViewById(R.id.toolbar);
        btn_pay = findViewById(R.id.btn_pay);
        toolbar.setNavigationIcon(R.drawable.ic_backspace);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Digital Reader Payment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void SharedPref() {
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        str_userid = sharedPreferences.getString(USER_ID, "");
        str_fname = sharedPreferences.getString(FNAME, "");
        str_lname = sharedPreferences.getString(LNAME, "");
        str_premiumflag = sharedPreferences.getString(PREMIUMUSER, "");
        str_email = sharedPreferences.getString(EMAIL, "");
    }

    public void startPayment() {
        Checkout checkout = new Checkout();
        //     checkout.setKeyID("rzp_test_Lw4MeCsvDdPX73");
        checkout.setImage(R.drawable.digital_logo);

        final Activity activity = this;
        try {
            JSONObject options = new JSONObject();
            options.put("name", "Digital Reader");
            options.put("key", "rzp_test_Lw4MeCsvDdPX73");
            options.put("description", "Subscription");
            options.put("currency", "INR");
            options.put("amount", price);

            JSONObject preFill = new JSONObject();
            preFill.put("email", str_email);
            // preFill.put("contact", "");
            options.put("prefill", preFill);

            checkout.open(activity, options);
        } catch (Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
        }
    }

    public void onPaymentSuccess(String s) {
        try {
            Log.d("paymentSuccess", s);
            // Toast.makeText(this, "Payment Successful: " + s, Toast.LENGTH_SHORT).show();
            Calendar date = Calendar.getInstance();
            date.setTime(new Date());
            Format f = new SimpleDateFormat("yyyy-MM-dd");
            date.add(Calendar.YEAR, 1);
            String expiry_date = f.format(date.getTime());
            Random objGenerator = new Random();
            String txn_id = "razordr-" + objGenerator.nextInt(10000000);
            String razorpay_id = s.toString();
            String user_id = str_userid;

            Log.d("Succesful_data", "onPaymentSuccess: " + txn_id + "razorpay_id = " + razorpay_id + "expiry_date=" + expiry_date + "===" + user_id);

            SendPaymentData(txn_id, razorpay_id, expiry_date, user_id);

        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentSuccess", e);
        }
    }

    private void SendPaymentData(final String txn_id, final String razorpay_id, final String expiry_date, final String user_id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Add_Payment_Data,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        if (!response.equalsIgnoreCase("failure"))
                            try {

                                JSONObject obj = new JSONObject(response);
                                JSONArray array = obj.getJSONArray("response");


                                Log.d("respondsf", "" + response);
                                // JSONArray array = new JSONArray("response");

                                for (int i = 0; i < array.length(); i++) {

                                    JSONObject rlist = array.getJSONObject(i);
                                    userpremiumflag = rlist.getString("user_premiumflag");

                                }
                                if (userpremiumflag.equals("1")) {
                                    Toast.makeText(getApplicationContext(), "Payment Successfully.", Toast.LENGTH_SHORT).show();
                                    Intent intent1 = new Intent(getApplicationContext(), UpdateActivity.class);
                                    finish();
                                    startActivity(intent1);
                                }
                                Log.d("userpremiumflag", "onResponse: " + userpremiumflag);
                                sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
                                editor = sharedPreferences.edit();
                                editor.putString(PREMIUMUSER, userpremiumflag);
                                editor.apply();
                                Log.d("userpremiumflag", userpremiumflag + "Sherf" + sharedPreferences.getString(PREMIUMUSER, ""));

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

                params.put("txn_id", txn_id);
                params.put("razorpay_id", razorpay_id);
                params.put("user_id", user_id);
                params.put("expiry_date", expiry_date);
                Log.d("parameter", "getParams: " + params);
                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(stringRequest);

    }

    @Override
    public void onPaymentError(int i, String s) {
        try {
            //Toast.makeText(this, "Payment failed: " + i + " " + s, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Payment Cancelled", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onPaymentError: " + i + s.toString());


        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentError", e);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
