package com.prosolstech.sugartrade.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.prosolstech.sugartrade.R;
import com.prosolstech.sugartrade.util.ACU;


public class LinkUrlActivity extends AppCompatActivity {

   private Context context;
   private String strFlag;
   private Toolbar toolbar;

   private ProgressBar progressBar;
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
        context = LinkUrlActivity.this;
        intitializeUI();
        setToolBar();
    }


    private void intitializeUI() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        webView = (WebView) findViewById(R.id.webViewActivity);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        showPrograssBar();
        webView.loadUrl("http://www.sugarcatalog.com/anakantuser/sugar_trade/privacy_policy.html");
    }


    private void setToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
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
