package com.digital.reader.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.widget.TextView;

import com.digital.reader.R;

import static com.digital.reader.Forms.LoginActivity.EMAIL;
import static com.digital.reader.Forms.LoginActivity.FNAME;
import static com.digital.reader.Forms.LoginActivity.LNAME;
import static com.digital.reader.Forms.LoginActivity.PREMIUMUSER;
import static com.digital.reader.Forms.LoginActivity.PWD;
import static com.digital.reader.Forms.LoginActivity.SHARED_PREFERENCES_NAME;
import static com.digital.reader.Forms.LoginActivity.USER_ID;

public class ActivityReceipt extends AppCompatActivity {
    TextView tv_fname, tv_lname, tv_name, tv_email, tv_txnid, tv_razorpayid;
    String str_email, str_pwd, str_fname, str_lname, str_userid, str_passowrd, userpremiumflag;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        toolbar.setNavigationIcon(R.drawable.ic_backspace);
//        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("Payment Receipt");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        str_userid = sharedPreferences.getString(USER_ID, "");
        str_fname = sharedPreferences.getString(FNAME, "");
        str_lname = sharedPreferences.getString(LNAME, "");
        str_email = sharedPreferences.getString(EMAIL, "");
        str_pwd = sharedPreferences.getString(PWD, "");
        userpremiumflag = sharedPreferences.getString(PREMIUMUSER, "");
        tv_name = findViewById(R.id.tv_name);
        tv_email = findViewById(R.id.tv_email);
        tv_txnid = findViewById(R.id.tv_txnid);
        tv_razorpayid = findViewById(R.id.tv_razorpayid);

        tv_name.setText(str_fname + " " + str_lname);
        tv_email.setText(str_email);
        tv_razorpayid.setText("demo");
        tv_txnid.setText("demo");

    }
}
