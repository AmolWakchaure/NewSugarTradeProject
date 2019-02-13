package com.prosolstech.sugartrade.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.prosolstech.sugartrade.LoginActiveDialog;
import com.prosolstech.sugartrade.R;
import com.prosolstech.sugartrade.adapter.MyRequestAdapter;
import com.prosolstech.sugartrade.util.ACU;
import com.prosolstech.sugartrade.util.Constant;
import com.prosolstech.sugartrade.util.ItemAnimation;
import com.prosolstech.sugartrade.util.VU;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MyRequestedActivity extends AppCompatActivity {

    Context context;
    private RecyclerView recyclerView;
    private MyRequestAdapter myRequestAdapter;
    private int animation_type = ItemAnimation.BOTTOM_UP;
    String strRecordID = "", strCheckFlag = "", strCheckValue = "";
    String str;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_requested);
        context = MyRequestedActivity.this;
        setToolBar();
//        getDataFromOffer();
        intitializeUI();
        strRecordID = ACU.MySP.getFromSP(context, ACU.MySP.NOTIFY_ID, "");
        Log.e("strRecordID", " : " + strRecordID);
        if (strRecordID.equalsIgnoreCase("")) {
            getDataFromOffer();
        }

        if (VU.isConnectingToInternet(context)) {
            MyRequest();
        }
    }

    private void intitializeUI() {
        recyclerView = (RecyclerView) findViewById(R.id.MyRequestActivityRecyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 1));
        recyclerView.setHasFixedSize(true);
        animation_type = ItemAnimation.FADE_IN;
    }

    Toolbar toolbar;

    private void setToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
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

    private void getDataFromOffer() {                              // this data is comes from MySellTenderOfferAdapter or MyBuyOfferAdapter

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            try {
                strCheckFlag = extras.getString("check_flag");
                strCheckValue = extras.getString("value");
                Log.e("MyRequested_Flag", " " + strCheckFlag);
                Log.e("MyRequested_Value", " " + strCheckValue);

                JSONObject jsonobj = new JSONObject(strCheckValue);
                strRecordID = jsonobj.getString("id");

                if (ACU.MySP.getFromSP(context, ACU.MySP.ROLE, "").equalsIgnoreCase("Buyer")) {
                    toolbar.setTitle("Buy Post Id:" + strRecordID);
                } else {
                    toolbar.setTitle("Sell Post Id:" + strRecordID);
                }
                setSupportActionBar(toolbar);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void MyRequest() {

        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait while data fetch from server...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/getMyOfferDetails";      //for server

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Log.e("MyRequested", " RESPONSE " + response);
                        pDialog.dismiss();
                        setListAdapter(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("No_Response", "FOUND");
                pDialog.dismiss();
                Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", strRecordID);
                params.put("role", ACU.MySP.getFromSP(MyRequestedActivity.this, ACU.MySP.ROLE, ""));

                Log.e("PARAMS", " MYREQ " + params.toString());
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private void setListAdapter(String result) {
        try {
            JSONArray array;
            Log.e("MyRequested_LISTADAPTER", " RESULT " + result.trim());
            array = new JSONArray(result);
            if (array != null && array.length() > 0) {
                ACU.MySP.saveSP(context, ACU.MySP.NOTIFY_ID, "");
                myRequestAdapter = new MyRequestAdapter(context, array, animation_type, ACU.MySP.getFromSP(MyRequestedActivity.this, ACU.MySP.ROLE, ""), listner,strRecordID);
                recyclerView.setAdapter(myRequestAdapter);
            } else {
                Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    MyRequestAdapter.RefreshListner listner = new MyRequestAdapter.RefreshListner() {
        @Override
        public void refresh(Context context) {
            if (VU.isConnectingToInternet(context)) {
                MyRequest();
            }
        }
    };


    @Override
    public void onBackPressed() {
        if (strRecordID != null) {
            Intent intent = new Intent(this, BuySellDashBoardActivity.class);
            intent.putExtra("MyRequest", "val");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(this, BuySellDashBoardActivity.class);
            intent.putExtra("MyRequest", "val");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
}
