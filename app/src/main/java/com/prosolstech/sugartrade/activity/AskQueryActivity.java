package com.prosolstech.sugartrade.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.prosolstech.sugartrade.R;
import com.prosolstech.sugartrade.util.ACU;
import com.prosolstech.sugartrade.util.VU;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AskQueryActivity extends AppCompatActivity {

    Context context;
    EditText edtSubject, edtDescription;
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_query);
        context = AskQueryActivity.this;
        setToolBar();
        intitializeUI();
    }

    private void intitializeUI() {
        edtSubject = (EditText) findViewById(R.id.AskQueryActivityEdtSubject);
        edtDescription = (EditText) findViewById(R.id.AskQueryActivityEdtDescription);
        btnSubmit = (Button) findViewById(R.id.AskQueryActivityBtnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (VU.isConnectingToInternet(context)) {
                    if (Validate()) {
                        askQuery();
                    }
                }
            }
        });


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

    public boolean Validate() {
        if (VU.isEmpty(edtSubject)) {
            edtSubject.setError("Please enter subject");
            edtSubject.requestFocus();
            return false;
        } else if (VU.isEmpty(edtDescription)) {
            edtDescription.setError("Please enter description");
            edtDescription.requestFocus();
            return false;
        }
        return true;
    }

    public void askQuery() {
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait while data upload on server...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/addQuery";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        try {
                            pDialog.dismiss();
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("message").equalsIgnoreCase("success")) {
                                Toast.makeText(context, "Query done successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(context, "Query not submitted. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                            Log.e("askQuery", " RESPONSE  " + response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Log.e("No_Response", " GET ");
                Toast.makeText(context, "Please Try Again", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("userby", ACU.MySP.getFromSP(context, ACU.MySP.ID, ""));
                params.put("subject", edtSubject.getText().toString().trim());
                params.put("query", edtDescription.getText().toString().trim());
                params.put("role", ACU.MySP.getFromSP(context, ACU.MySP.ROLE, ""));

                Log.e("PARAM", " : " + params.toString());
                return params;
            }
        };
        queue.add(stringRequest);
    }
}


