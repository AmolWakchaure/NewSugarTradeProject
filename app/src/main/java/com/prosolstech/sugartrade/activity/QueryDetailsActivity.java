package com.prosolstech.sugartrade.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.prosolstech.sugartrade.R;
import com.prosolstech.sugartrade.adapter.QueryDetailsAdapter;
import com.prosolstech.sugartrade.util.ACU;
import com.prosolstech.sugartrade.util.VU;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class QueryDetailsActivity extends AppCompatActivity {

    Context context;
    String strQuery_id = "";
    ListView listview;
    EditText edtReply;
    Button btnReply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_details);
        context = QueryDetailsActivity.this;
        setToolBar();
        getIntentData();
        intitializeUI();
        if (VU.isConnectingToInternet(context)) {
            getQueryDetails();
        }
    }

    private void intitializeUI() {
        listview = (ListView) findViewById(R.id.QueryDetailsActivityListview);
        edtReply = (EditText) findViewById(R.id.QueryDetailsActivityEdtReply);
        btnReply = (Button) findViewById(R.id.QueryDetailsActivityBtnReply);

        btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (VU.isConnectingToInternet(context)) {
                    if (Validate()) {
                        addReply();
                    }
                }
            }
        });
    }

    public boolean Validate() {
        if (VU.isEmpty(edtReply)) {
            edtReply.setError("Please enter your query");
            edtReply.requestFocus();
            return false;
        }
        return true;
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
                onBackPressed();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getIntentData() {

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            try {
                strQuery_id = extras.getString("query_id");
                Log.e("strQuery_id", " " + strQuery_id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void getQueryDetails() {
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait while data fetch from server...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestQueue queue = Volley.newRequestQueue(context);

        String url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/getSubQueries";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            pDialog.dismiss();
                            Log.e("QueryDetails_ONRESPONSE", " : " + response.trim());
                            setListAdapter(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("No_Response", " FOUND");
                pDialog.dismiss();
                Toast.makeText(context, "You do not have any query", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("query_id",strQuery_id);

                Log.e("PARAMS", " : " + params.toString());
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private void setListAdapter(String result) {
        try {
            JSONArray array;
            ;
            Log.e("setListAdapter", " RESULT " + result.trim());
            array = new JSONArray(result);
            if (array != null && array.length() > 0) {
                QueryDetailsAdapter queryadapter = new QueryDetailsAdapter(context, array);
                listview.setAdapter(queryadapter);
            } else {
                Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addReply() {

        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait while data uploaded on server...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/addSubQuery";      //for server

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Log.e("OfferCheck", " RESPONSE " + response);
                        pDialog.dismiss();
                        try {
                            JSONObject jobj = new JSONObject(response);
                            if (jobj.getString("message").equalsIgnoreCase("success")) {
                                getQueryDetails();
                                edtReply.setText("");
                            } else {
                                Toast.makeText(context, "Please Try Again", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("No_Response_OfferCheck", "FOUND");
                pDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("userby", ACU.MySP.getFromSP(context, ACU.MySP.ID, ""));
                params.put("query_id", strQuery_id);
                params.put("message", edtReply.getText().toString().trim());

                Log.e("addReply_PARAMS", " " + params.toString());
                return params;
            }
        };
        queue.add(stringRequest);
    }
}
