package com.digital.reader.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.digital.reader.R;

public class ActivityAbout extends AppCompatActivity {
    TextView tc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        tc = findViewById(R.id.t_c);
        final String url = "https://pixeldev.in/app/privacypolicy.html";
        tc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityWebView.class);
                intent.putExtra("EXTRA_OBJC", url);
                startActivity(intent);
            }
        });
    }
}
