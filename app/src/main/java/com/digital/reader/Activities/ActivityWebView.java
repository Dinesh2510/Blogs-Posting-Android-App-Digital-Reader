package com.digital.reader.Activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.digital.reader.R;
import com.google.android.material.appbar.AppBarLayout;

public class ActivityWebView extends AppCompatActivity {
    private Toolbar toolbar;
    private ActionBar actionBar;

    private WebView webView;
    private String url;
    private View lyt_parent;
    private AppBarLayout appbar_layout;
    private ProgressBar progressBar;
    private Boolean from_notif;
    private MenuItem menu_back, menu_forward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);


        lyt_parent = findViewById(R.id.lyt_parent);

        // get extra object
        url = getIntent().getStringExtra("EXTRA_OBJC");

        Log.d("url", "onCreate: " + url);
        initComponent();
        initToolbar();
        loadWebFromUrl();

    }

    private void initComponent() {
        appbar_layout = findViewById(R.id.appbar_layout);
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        progressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.grey_80), PorterDuff.Mode.SRC_IN);
        progressBar.setBackgroundColor(getResources().getColor(R.color.overlay_dark_10));
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_backspace);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.grey_80), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(null);

    }

    private void loadWebFromUrl() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                actionBar.setTitle(null);
                actionBar.setSubtitle(url);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                actionBar.setTitle(view.getTitle());
                progressBar.setVisibility(View.INVISIBLE);
                refreshMenu();
            }
        });

        webView.loadUrl(url);
        webView.setWebChromeClient(new MyChromeClient());
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_web_view, menu);
        menu_back = menu.findItem(R.id.action_back);
        menu_forward = menu.findItem(R.id.action_forward);
        Drawable drawable = toolbar.getOverflowIcon();
        drawable.mutate();
        drawable.setColorFilter(getResources().getColor(R.color.grey_90), PorterDuff.Mode.SRC_ATOP);
        refreshMenu();
        return true;
    }

    private void refreshMenu() {
        if (menu_back != null) {
            menu_back.setEnabled(webView.canGoBack());
        }
        if (menu_forward != null) {
            menu_forward.setEnabled(webView.canGoForward());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackChecker();
        } else if (item.getItemId() == R.id.action_refresh) {
            loadWebFromUrl();
        } else if (item.getItemId() == R.id.action_browser) {
            if (!URLUtil.isValidUrl(url)) {
                Toast.makeText(getApplicationContext(), "Opps!, Cannot open url", Toast.LENGTH_LONG).show();
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } else if (item.getItemId() == R.id.action_copy_link) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("clipboard", webView.getUrl());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, R.string.email_copied, Toast.LENGTH_SHORT).show();
        } else if (item.getItemId() == R.id.action_back) {
            onBackPressed();
        } else if (item.getItemId() == R.id.action_forward) {
            if (webView.canGoForward()) webView.goForward();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            onBackChecker();
        }
    }

    private void onBackChecker() {

        super.onBackPressed();

    }

    private class MyChromeClient extends WebChromeClient {

        protected FrameLayout mFullscreenContainer;
        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;


        @Override
        public void onProgressChanged(WebView view, int progress) {
            super.onProgressChanged(view, progress);
            progressBar.setProgress(progress + 10);
            if (progress >= 100) actionBar.setTitle(view.getTitle());
        }

        public Bitmap getDefaultVideoPoster() {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView() {
            ((FrameLayout) getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
            appbar_layout.setVisibility(View.VISIBLE);
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback) {
            if (this.mCustomView != null) {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mCustomView.setBackgroundColor(Color.BLACK);
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout) getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846);
            appbar_layout.setVisibility(View.GONE);
        }
    }

}
