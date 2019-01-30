package com.prosolstech.sugartrade.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.prosolstech.sugartrade.R;

public class ContactUsActivity extends AppCompatActivity {

    Context context;
    String strFlag;
    Toolbar toolbar;

    ProgressBar progressBar;
    private WebView webView;
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        context = ContactUsActivity.this;
        getIntentData();
        intitializeUI();
        setToolBar();
    }

    private void getIntentData() {

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            try {
                strFlag = extras.getString("flag");
                setUrl(extras.getString("url"));
                Log.e("FLAG", " " + strFlag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void intitializeUI() {

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        webView = (WebView) findViewById(R.id.webViewActivity);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        showPrograssBar();
        webView.loadUrl(getUrl());
    }

    private void setToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (strFlag.equalsIgnoreCase("contact_us")) {
            toolbar.setTitle("Contact Us");
        } else if (strFlag.equalsIgnoreCase("terms_and_condition")) {
            toolbar.setTitle("Terms And Conditions");
        } else if (strFlag.equalsIgnoreCase("help")) {
            toolbar.setTitle("Help");
        }else if (strFlag.equalsIgnoreCase("privacy_policy")) {
            toolbar.setTitle("Privacy Policy");
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    private void showPrograssBar() {
        progressBar.setVisibility(View.VISIBLE);
        new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                progressBar.setVisibility(View.GONE);
            }
        }.start();
    }
}
