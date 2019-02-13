package com.prosolstech.sugartrade.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.prosolstech.sugartrade.adapter.BuyerListAdapter;
import com.prosolstech.sugartrade.adapter.VehicleListAdapter;
import com.prosolstech.sugartrade.util.ACU;
import com.prosolstech.sugartrade.util.DTU;
import com.prosolstech.sugartrade.util.ItemAnimation;
import com.prosolstech.sugartrade.util.VU;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VehicleDetailsActivity extends AppCompatActivity {
    Context context;
   private String strValue = "", strOfferID = "",viewFlag = "";
    private RecyclerView recyclerView;
    private int animation_type = ItemAnimation.BOTTOM_UP;
    VehicleListAdapter vehicleListAdapter;
    FloatingActionButton fab_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_details);
        context = VehicleDetailsActivity.this;
        fab_add=findViewById(R.id.fab_add);
        fab_add.setVisibility(View.GONE);
        setToolBar();
        getIntentData();
        intitializeUI();
        if (VU.isConnectingToInternet(context)) {
            FetchVehicleDetail();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (VU.isConnectingToInternet(context)) {
            FetchVehicleDetail();
        }
    }

    private void intitializeUI() {
        recyclerView = (RecyclerView) findViewById(R.id.VehicleDetailsActivtyRecyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 1));
        recyclerView.setHasFixedSize(true);
        animation_type = ItemAnimation.FADE_IN;
    }


    private void getIntentData() {

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            try {
                strOfferID = extras.getString("offer_id");
                viewFlag = extras.getString("viewFlag");
                Log.e("strOfferID", " : " + strOfferID);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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

    private void FetchVehicleDetail() {
        String url = "";
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait while data fetch from server...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(context);

        url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/getVehicles";      //for server
        Log.e("FetchVehicleDetail_URL", " ....." + url);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Log.e("FetchVehicleDetail", " RESPONSE " + response);
                        pDialog.dismiss();
                        setListAdapter(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("No Response", "FOUND");
                pDialog.dismiss();
                Toast.makeText(context, "Oops ! vehicle details no found", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("bid_id", strOfferID);
                params.put("userby", ACU.MySP.getFromSP(context, ACU.MySP.ID, ""));
                if (ACU.MySP.getFromSP(context, ACU.MySP.ROLE, "").equalsIgnoreCase("Seller")) {
                    params.put("role", "Buyer");
                } else {
                    params.put("role", "Seller");

                }
                Log.e("FilterData_PARAMS", " : " + params.toString());
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private void setListAdapter(String result) {
        try {
            JSONArray array;
            Log.e("FetchVeDetail_ADAPTER", " RESULT " + result.trim());
            array = new JSONArray(result);
            if (array != null && array.length() > 0) {
                vehicleListAdapter = new VehicleListAdapter(context, array, animation_type,viewFlag);
                recyclerView.setAdapter(vehicleListAdapter);
            } else {
                Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
