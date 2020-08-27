package com.digital.reader.EmailVerification;

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
import com.digital.reader.Helper.NetworkCheck;
import com.digital.reader.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.digital.reader.Helper.API.SendActiveFlag;
import static com.digital.reader.Helper.API.SendEmailCode;

public class LoginVerify extends AppCompatActivity {

    EditText user_input_email, user_input_code;
    Button btn_submit;
    int str_number;
    Button btn_resent;
    String str_user_verification, str_user_email, intent_code, email;
    ImageView tick;
    TextView user_email;
    ProgressBar progress_bar;
    String New_verificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_verifiy);

        user_input_code = findViewById(R.id.user_input_code);
        user_input_email = findViewById(R.id.user_input_email);
        tick = findViewById(R.id.tick);

        btn_submit = findViewById(R.id.btn_submit);
        progress_bar = findViewById(R.id.progress_bar);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_user_email = user_input_email.getText().toString();
                str_user_verification = user_input_code.getText().toString();
                if (!str_user_email.isEmpty()) {
                    if (!str_user_verification.isEmpty()) {
                        if (NetworkCheck.isConnect(getApplicationContext())) {
                            Log.d("ver_1", "onClick: " + str_user_verification + str_user_email);
                            SendEmailCode();
                            showLoading(true);
                        } else {
                            Toast.makeText(LoginVerify.this, "No Internet", Toast.LENGTH_SHORT).show();
                        }
                    } else {

                        Toast.makeText(LoginVerify.this, "enter verification code is empty", Toast.LENGTH_SHORT).show();
                    }
                } else {

                    Toast.makeText(LoginVerify.this, "enter email", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    private void SendEmailCode() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SendEmailCode, new com.android.volley.Response.Listener<String>() {
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
                parameter.put("email", str_user_email);
                parameter.put("code", str_user_verification);

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
            String res = jsonObject.getString("msg");
            String response_server = jsonObject.getString("response");
            if (res.equals("success")) {
                showLoading(false);
                Toast.makeText(this, "" + response_server, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                finish();
                startActivity(intent);
                Log.d("msg_ala", "showJSON: " + res);

            } else if (res.equals("failure")) {
                showLoading(false);
                Toast.makeText(this, "" + response_server, Toast.LENGTH_SHORT).show();

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