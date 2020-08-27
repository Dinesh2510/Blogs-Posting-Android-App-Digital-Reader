package com.digital.reader.EmailVerification;

import androidx.annotation.LongDef;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.digital.reader.Forms.LoginActivity;
import com.digital.reader.Helper.AppController;
import com.digital.reader.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.digital.reader.Helper.API.Get_Verification_Code;
import static com.digital.reader.Helper.API.SendActiveFlag;

public class EmailVerify extends AppCompatActivity {
    EditText user_input;
    Button btn_submit;
    int str_number;
    Button btn_resent;
    String str_user_verification, intent_code, email;
    ImageView tick;
    TextView user_email;
    ProgressBar progress_bar;
    String New_verificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verify);

        //  str_number = getIntent().getIntExtra("number", 0);
        email = getIntent().getStringExtra("email");

        user_input = findViewById(R.id.user_input);
        tick = findViewById(R.id.tick);
        btn_submit = findViewById(R.id.btn_submit);
        btn_resent = findViewById(R.id.btn_resent);
        user_email = findViewById(R.id.user_email);
        progress_bar = findViewById(R.id.progress_bar);
        user_email.setText(email);

        if (!email.isEmpty()) {
            GetVerificationCode();
        } else {
            Log.d("error", "onCreate: oops email Not Found!");
        }


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoading(true);
                VerificationMethod();

            }
        });


    }

    /**********************************************************************************************************************/
    private void GetVerificationCode() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Get_Verification_Code, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showEmailJSON(response);
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
                parameter.put("email", email);

                Log.d("SendToTEmail", "" + parameter);

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
            String msg = jsonObject.getString("msg");
            if (msg.equals("success")) {

                Log.d("msg_ala", "showJSON: " + res);
                New_verificationCode = jsonObject.getString("response");

            } else if (msg.equals("failure")) {
                //showLoading(false);
                Toast.makeText(this, "" + res, Toast.LENGTH_SHORT).show();

            } else {
                //showLoading(false);
                Toast.makeText(this, "something went Wrong!", Toast.LENGTH_SHORT).show();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**********************************************************************************************************************/
    private void VerificationMethod() {
        str_user_verification = user_input.getText().toString();
        Log.d("code", "onCreate: " + str_user_verification + "== " + New_verificationCode);

        if (str_user_verification.equals(New_verificationCode)) {
            tick.setVisibility(View.VISIBLE);
            SendActiveFlag();

        } else {

            Toast.makeText(EmailVerify.this, "Verification Code Not Match!", Toast.LENGTH_SHORT).show();
            showLoading(false);

        }
    }

    private void SendActiveFlag() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SendActiveFlag, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showActiveJSON(response);
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
                // parameter.put("flag", "0");
                parameter.put("email", email);

                Log.d("parameter", "" + parameter);

                return parameter;

            }
        };


        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(stringRequest, "Login_Module");

    }

    private void showActiveJSON(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String res = jsonObject.getString("response");
            if (res.equals("success")) {
                showLoading(false);
                Toast.makeText(EmailVerify.this, "Email is Verified!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                finish();
                startActivity(intent);
                Log.d("msg_ala", "showJSON: " + res);

            } else if (res.equals("failure")) {
                showLoading(false);
                Toast.makeText(this, "" + res, Toast.LENGTH_SHORT).show();

            } else {
                showLoading(false);
                Toast.makeText(this, "something went Wrong!", Toast.LENGTH_SHORT).show();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();// commented this line in order to disable back press
        //Write your code here
        Toast.makeText(getApplicationContext(), "Back press disabled!", Toast.LENGTH_SHORT).show();
    }

    private void showLoading(final boolean show) {
        btn_submit.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
        progress_bar.setVisibility(!show ? View.INVISIBLE : View.VISIBLE);
    }

}
