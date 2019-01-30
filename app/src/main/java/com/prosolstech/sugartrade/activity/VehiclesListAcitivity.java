package com.prosolstech.sugartrade.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.prosolstech.sugartrade.adapter.VehicleListAdapter;
import com.prosolstech.sugartrade.util.ACU;
import com.prosolstech.sugartrade.util.ItemAnimation;
import com.prosolstech.sugartrade.util.VU;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class VehiclesListAcitivity extends AppCompatActivity {

    Context context;
    String strValue = "", strOfferID = "";
    private RecyclerView recyclerView;
    private int animation_type = ItemAnimation.BOTTOM_UP;
    VehicleListAdapter vehicleListAdapter;
    private  FloatingActionButton fab_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_details);
        context = VehiclesListAcitivity.this;
        setToolBar();
        getIntentData();
        intitializeUI();
        if (VU.isConnectingToInternet(context)) {
            FetchVehicleDetail();
        }

         fab_add = (FloatingActionButton) findViewById(R.id.fab_add);

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(context, DispatchActivity.class);
                in.putExtra("offer_id", strOfferID);
                startActivity(in);
                finish();
            }
        });


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
                Log.e("strOfferID", " : " + strOfferID);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, BuySellDashBoardActivity.class);
        startActivity(intent);
        finish();
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.addvehicles, menu);
//        return true;
//    }
//
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_book_service:
//                Intent in = new Intent(context, DispatchActivity.class);
//                in.putExtra("offer_id", strOfferID);
//                startActivity(in);
//                finish();
//                break;
//
//            default:
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private void FetchVehicleDetail() {
        String url = "";
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait while data fetch from server...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(context);
//        http://www.sugarcatalog.com/anakantuser/sugar_trade/index.php/API/getVehicleById
        url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/getVehicleById";      //for server
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
                Toast.makeText(context, "Oops ! vehicle details no found.", Toast.LENGTH_SHORT).show();

//                Intent in = new Intent(context, DispatchActivity.class);
//                in.putExtra("offer_id", strOfferID);
//                startActivity(in);
//                finish();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("bid_id", strOfferID);
                params.put("userby", ACU.MySP.getFromSP(context, ACU.MySP.ID, ""));
                params.put("role", ACU.MySP.getFromSP(context, ACU.MySP.ROLE, ""));
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
                vehicleListAdapter = new VehicleListAdapter(context, array, animation_type);
                recyclerView.setAdapter(vehicleListAdapter);
            } else {

//                Intent in = new Intent(context, DispatchActivity.class);
//                in.putExtra("offer_id", strOfferID);
//                startActivity(in);
//                finish();

                Toast.makeText(context, "Oops ! vehicle details not found.", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
