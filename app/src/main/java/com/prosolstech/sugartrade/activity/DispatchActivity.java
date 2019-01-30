package com.prosolstech.sugartrade.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.prosolstech.sugartrade.R;
import com.prosolstech.sugartrade.database.DataBaseHelper;
import com.prosolstech.sugartrade.model.VehicleClassModel;
import com.prosolstech.sugartrade.util.ACU;
import com.prosolstech.sugartrade.util.DTU;
import com.prosolstech.sugartrade.util.VU;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DispatchActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context;
    private EditText edtVehicleNo, edtDriverNo, edtLicenseNo, edtQuantity, edtArrivalDate,
            edtBillName, edtBillAddress, edtBillGST, edtShipName, edtShipAddress, edtShipGST;     // add on 28-28-18
    private Button btnSubmit, btnSaveMore;
    private String strOfferID = "";
    private JSONArray array;
    private JSONObject object;
    private TextView addMore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch);
        context = DispatchActivity.this;
        addMore = findViewById(R.id.addMore);
        intitializeUI();
        addMore.setMovementMethod(LinkMovementMethod.getInstance());
        addMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                edtVehicleNo.setText("");
                edtDriverNo.setText("");
                edtQuantity.setText("");
                edtArrivalDate.setText("");
                edtBillName.setText("");
                edtBillAddress.setText("");
                edtBillGST.setText("");
                edtShipName.setText("");
                edtShipAddress.setText("");
                edtShipGST.setText("");
            }
        });
        setToolBar();
        getIntentData();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, BuySellDashBoardActivity.class);
        startActivity(intent);
        finish();
    }

    private void intitializeUI() {

        array = new JSONArray();

        edtVehicleNo = (EditText) findViewById(R.id.DispatchActivityEdtVehicleNo);
        edtDriverNo = (EditText) findViewById(R.id.DispatchActivityEdtDriverNo);
        edtArrivalDate = (EditText) findViewById(R.id.DispatchActivityEdtDateArrival);
        edtQuantity = (EditText) findViewById(R.id.DispatchActivityEdtQuantity);              // add on 28-08-18
        edtBillName = (EditText) findViewById(R.id.DispatchActivityEdtBillName);              // add on 28-08-18
        edtBillAddress = (EditText) findViewById(R.id.DispatchActivityEdtBillAddress);              // add on 28-08-18
        edtBillGST = (EditText) findViewById(R.id.DispatchActivityEdtBillGST);              // add on 28-08-18
        edtShipName = (EditText) findViewById(R.id.DispatchActivityEdtShipName);              // add on 28-08-18
        edtShipAddress = (EditText) findViewById(R.id.DispatchActivityEdtShipAddress);              // add on 28-08-18
        edtShipGST = (EditText) findViewById(R.id.DispatchActivityEdtShipGST);              // add on 28-08-18


        btnSubmit = (Button) findViewById(R.id.DispatchActivityBtnSubmit);
        btnSaveMore = (Button) findViewById(R.id.BookingDetailsActivityBtnSaveMore);


        edtArrivalDate.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        btnSaveMore.setOnClickListener(this);
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // perform whatever you want on back arrow click

                onBackPressed();
            }
        });
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

    public boolean Validate() {
        if (VU.isEmpty(edtVehicleNo)) {
            edtVehicleNo.setError("Please Enter Vehicle No");
            edtVehicleNo.requestFocus();
            return false;
        } else if (!edtDriverNo.getText().toString().matches("[0-9]{10}")) {
            Toast.makeText(context, "Please Enter 10 digits mobile number", Toast.LENGTH_SHORT).show();
            return false;
        } else if (VU.isEmpty(edtArrivalDate)) {
            edtArrivalDate.setError("Please Enter Arrival Date");
            edtArrivalDate.requestFocus();
            return false;
        }
        return true;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.DispatchActivityBtnSubmit:
                if (VU.isConnectingToInternet(context)) {
                    if (Validate()) {
                        dispatchData();
                    }
                }
                break;
            case R.id.BookingDetailsActivityBtnSaveMore:
                if (VU.isConnectingToInternet(context)) {
                    if (Validate()) {
                        dispatchData();
                    }
                }
                break;


            case R.id.DispatchActivityEdtDateArrival:
                DTU.showDatePickerDialog(context, DTU.FLAG_ONLY_NEW, edtArrivalDate);
                break;
        }
    }

    private void dispatchData() {

        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait while data upload on server...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/addDispatch";
        Log.e("dispatchData_URL", " ....." + url);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Log.e("dispatchData", " RESPONSE " + response);
                        pDialog.dismiss();
                        try {
                            JSONObject jobj = new JSONObject(response);
                            if (jobj.getString("message").equalsIgnoreCase("success")) {
                                Toast.makeText(context, "Data saved sucessfully", Toast.LENGTH_SHORT).show();
                                edtVehicleNo.setText("");
                                edtDriverNo.setText("");
                                edtQuantity.setText("");
                                edtArrivalDate.setText("");
                                edtBillName.setText("");
                                edtBillAddress.setText("");
                                edtBillGST.setText("");
                                edtShipName.setText("");
                                edtShipAddress.setText("");
                                edtShipGST.setText("");


                                Intent intent = new Intent(DispatchActivity.this, BuySellDashBoardActivity.class);
                                intent.putExtra("viewPager","0");
                                startActivity(intent);
                                finish();

                            } else {
                                Toast.makeText(context, "Please try again", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("No Response", "FOUND");
                pDialog.dismiss();
                Toast.makeText(context, "Please try again", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("bid_id", strOfferID);
                params.put("userby", ACU.MySP.getFromSP(context, ACU.MySP.ID, ""));
                params.put("vehicle_number", edtVehicleNo.getText().toString().trim());
                params.put("driver_contact", edtDriverNo.getText().toString().trim());
                params.put("quantity", edtQuantity.getText().toString().trim());
                params.put("date_of_arrival", VU.getddmmyyDate(edtArrivalDate.getText().toString().trim()));
                params.put("bill_to_name", edtBillName.getText().toString().trim());
                params.put("bill_to_address", edtBillAddress.getText().toString().trim());
                params.put("bill_to_gst", edtBillGST.getText().toString().trim());
                params.put("ship_to_name", edtShipName.getText().toString().trim());
                params.put("ship_to_address", edtShipAddress.getText().toString().trim());
                params.put("ship_to_gst", edtShipGST.getText().toString().trim());


                Log.e("dispatchData_PARMAS", " ..... " + params.toString());

                return params;
            }
        };
        queue.add(stringRequest);
    }
}
