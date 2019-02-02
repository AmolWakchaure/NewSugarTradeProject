package com.prosolstech.sugartrade.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.prosolstech.sugartrade.R;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;
    private Context context;
    String url, strUrl = "";
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        context = WebViewActivity.this;
        getIntentData();
        setToolBar();
        intitializeUI();
    }

    private void intitializeUI() {
        webView = (WebView) findViewById(R.id.WebViewActivityWebView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        webView.setWebViewClient(new myWebClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.loadUrl(strUrl);
    }


    public class myWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            progressBar.setVisibility(View.VISIBLE);
            view.loadUrl(url);
            return true;

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }
    }

    // To handle "Back" key press event for WebView to go back to previous screen.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Details");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void getIntentData() {

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            try {
                strUrl = extras.getString("url");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                onBackPressed();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}