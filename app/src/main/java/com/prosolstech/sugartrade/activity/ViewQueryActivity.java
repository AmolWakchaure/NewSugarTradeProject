package com.prosolstech.sugartrade.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.prosolstech.sugartrade.R;
import com.prosolstech.sugartrade.adapter.ViewQueryAdapter;
import com.prosolstech.sugartrade.classes.T;
import com.prosolstech.sugartrade.util.ACU;
import com.prosolstech.sugartrade.util.VU;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class ViewQueryActivity extends AppCompatActivity {

    private Context context;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_query);
        context = ViewQueryActivity.this;
        setToolBar();
        intitializeUI();
        if (VU.isConnectingToInternet(context)) {
            ViewQuery();
        }
    }

    private void intitializeUI() {

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, AskQueryActivity.class));
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.ViewQueryActivityRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

    public void ViewQuery() {
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait while data fetch from server...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestQueue queue = Volley.newRequestQueue(context);

        String url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/getMyQueries";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            pDialog.dismiss();
                            Log.e("ViewQuery_ONRESPONSE", " : " + response.trim());
                            setDataAdapter(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("No_Response", " FOUND");
                pDialog.dismiss();
                Toast.makeText(context, "You do not have any existing query", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", ACU.MySP.getFromSP(context, ACU.MySP.ID, ""));
                T.e("id : "+ACU.MySP.getFromSP(context, ACU.MySP.ID, ""));
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private void setDataAdapter(String response) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            if (jsonArray != null && jsonArray.length() > 0) {
                ViewQueryAdapter viewQueryAdapter = new ViewQueryAdapter(context, jsonArray);
                recyclerView.setAdapter(viewQueryAdapter);
            } else {
                Toast.makeText(context, "You do not have any existing query", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (VU.isConnectingToInternet(context)) {
            ViewQuery();
        }
    }
}
