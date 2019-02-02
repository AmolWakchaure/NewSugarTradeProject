package com.prosolstech.sugartrade.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.prosolstech.sugartrade.classes.T;
import com.prosolstech.sugartrade.util.ACU;
import com.prosolstech.sugartrade.util.VU;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class BidBookingActivity extends AppCompatActivity implements View.OnClickListener {

    Context context;
    EditText edtCompanyName, edtBookingQty, edtBookingRate, edtReqQty;
    Button btnOffer;
    String strFlag = "", strValue = "", strOfferId = "", strType = "";
    TextView txtAvailableQty, txtRequiredQty, txtGstValue;
    double totalGst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bid_booking);
        context = BidBookingActivity.this;
        setToolBar();
        intitializeUI();
        getIntentData();
    }

    private void intitializeUI() {
        edtCompanyName = (EditText) findViewById(R.id.BidBookingActivityCompanyName);
        edtBookingQty = (EditText) findViewById(R.id.BidBookingActivityBookingQty);
        edtBookingRate = (EditText) findViewById(R.id.BidBookingActivityBookingRate);
        edtReqQty = (EditText) findViewById(R.id.BidBookingActivityRequiredQty);

        txtAvailableQty = (TextView) findViewById(R.id.BidBookingActivityTxtAvailableQty);
        txtRequiredQty = (TextView) findViewById(R.id.BidBookingActivityTxtRequiredQty);
        txtGstValue = (TextView) findViewById(R.id.BidBookingActivityTxtGstValue);
        btnOffer = (Button) findViewById(R.id.BidBookingActivityBtnOffer);


        //jsonObject.getString("original_qty")



        btnOffer.setOnClickListener(this);

        edtBookingRate.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!edtBookingRate.getText().toString().equals("")) {
                    double amount = Double.parseDouble(edtBookingRate.getText().toString());
                    double res = (amount / 100.0f) * 5;
                    totalGst = amount + res;
                    txtGstValue.setText("WITH GST (" + totalGst + ")");
                } else {
                    totalGst = 0;
                    txtGstValue.setText("WITH GST (" + totalGst + ")");
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void afterTextChanged(Editable s) {
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
            case R.id.BidBookingActivityBtnOffer:
                if (VU.isConnectingToInternet(context)) {
                    if (Validate()) {

                        if (checkValueFor())
                        {
                            if (checkValue()) {

                                String available_qty = edtReqQty.getText().toString().trim();

                                if(Integer.valueOf(available_qty) > Integer.valueOf(current_required_qty))
                                {
                                    if (ACU.MySP.getFromSP(context, ACU.MySP.ROLE, "").equalsIgnoreCase("Buyer"))
                                    {
                                        T.t("Required qty can not greater than current available qty.");
                                    }
                                    else
                                    {
                                        T.t("Required qty can not greater than current available qty.");
                                    }

                                }
                                else
                                {
                                    bookOffer();
                                }



                            }
                            else
                            {
                                Toast.makeText(context, "Price/Qtl cannot be zero", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            if (ACU.MySP.getFromSP(context, ACU.MySP.ROLE, "").equalsIgnoreCase("Buyer"))
                            {
                                Toast.makeText(context, "Required Qty cannot be zero", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(context, "Available Qty cannot be zero", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
                }
                break;
        }
    }


    public boolean checkValue() {

        if (leadingZerosCount(edtBookingRate.getText().toString().trim()) > 0) {
            return false;
        }

        return true;
    }
    public boolean checkValueFor() {

        if (leadingZerosCount(edtReqQty.getText().toString().trim()) > 0) {
            return false;
        }

        return true;
    }


    public static int leadingZerosCount(String s) {
        int zeros = 0;
        for (int i = 0; i < 3 && i < s.length(); i++) {
            if (s.charAt(i) == '0')
                zeros++;
            else
                break;
        }
        return zeros;
    }

    public boolean checkQuantitiy() {
        boolean isValid = true;

        long avialbleQty = Long.parseLong(edtBookingQty.getText().toString().trim());


        if (Long.parseLong(edtReqQty.getText().toString().trim()) > avialbleQty) {
            isValid = false;
        }

        return isValid;
    }

    public boolean Validate() {
        if (VU.isEmpty(edtReqQty)) {
            edtReqQty.setError("Please enter quantity");
            edtReqQty.requestFocus();
            return false;
        }
        return true;
    }

    private void getIntentData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            try {
                strFlag = extras.getString("flag");
                strValue = extras.getString("data");
                Log.e("BidBookingActivity_Flag", " " + strFlag);
                Log.e("BidBookingActi_Value", " " + strValue);

                setData(strValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    String current_required_qty;
    private void setData(String strValue) {
        try {
            JSONObject jobj = new JSONObject(strValue);
            edtCompanyName.setText(jobj.getString("company_name"));

            if (strFlag.equalsIgnoreCase("Seller"))
            {
                current_required_qty = jobj.getString("curr_req_qty");
                // if seller login than data set on this condition
                Log.e("setData_SELLER", " : " + jobj.toString());
                strOfferId = jobj.getString("id");
                edtBookingRate.setBackgroundDrawable(getResources().getDrawable(R.drawable.new_style_container_grey));
                edtBookingRate.setText(jobj.getString("price_per_qtl"));
                edtBookingRate.setEnabled(false);
                totalGst = 0;
                if (ACU.MySP.getFromSP(context, ACU.MySP.ROLE, "").equals("Buyer")) {
                    edtBookingQty.setText(jobj.getString("original_qty"));
                } else {
                    //edtBookingQty.setText(jobj.getString("required_qty"));
                    txtRequiredQty.setText("Available Quantity");                       // here text change if buyer login
                    txtAvailableQty.setText("Current Required Quantity");
                    edtBookingQty.setText(current_required_qty);// here text change if buyer login
                }
            }
            else if (strFlag.equalsIgnoreCase("Buyer"))
            {                        // if buyer login than data set on this condition
                strOfferId = jobj.getString("id");
                strType = jobj.getString("type");
                Log.e("strType", ": " + strType);

                current_required_qty = jobj.getString("original_qty");

                Log.e("setData_BUYER", " : " + jobj.toString());
                if (!jobj.getString("type").equalsIgnoreCase("Tender"))
                {

                    edtBookingRate.setBackgroundDrawable(getResources().getDrawable(R.drawable.new_style_container_grey));
                    edtBookingRate.setText(jobj.getString("price_per_qtl"));
                    edtBookingRate.setEnabled(false);
                    txtGstValue.setVisibility(View.GONE);
                    totalGst = 0;
                } else {
                    edtBookingRate.setEnabled(true);
                    edtBookingRate.setBackgroundDrawable(getResources().getDrawable(R.drawable.new_style_container));
                    txtGstValue.setVisibility(View.VISIBLE);
                }
                if (ACU.MySP.getFromSP(context, ACU.MySP.ROLE, "").equals("Buyer")) {
                    edtBookingQty.setText(jobj.getString("original_qty"));


                } else {
                    edtBookingQty.setText(jobj.getString("required_qty"));
                }
            }
        } catch (JSONException e) {
            T.e(""+e);
            e.printStackTrace();
        }
    }

    private void bookOffer() {
        String url = "";
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait while data uploaded on server...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(context);

        url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/addSellBook";                          // this service is call for user login as Buyer or Seller anyone.
        Log.e("URL_FOR_ROLE_WISE", " ....." + url);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Log.e("bookOffer", " RESPONSE " + response);
                        pDialog.dismiss();
                        try {
                            JSONObject jobj = new JSONObject(response);
                            if (jobj.getString("message").equalsIgnoreCase("success")) {
                                Toast.makeText(context, "Your requested is registered", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(context, "Please Try Again", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Please Try Again!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("No_Response_bookOffer", "FOUND");
                pDialog.dismiss();
                Toast.makeText(context, "Please Try Again", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("role", ACU.MySP.getFromSP(context, ACU.MySP.ROLE, ""));
                params.put("id", ACU.MySP.getFromSP(BidBookingActivity.this, ACU.MySP.ID, ""));
                params.put("type", strType);
                params.put("offer_id", strOfferId);
                params.put("required_qty", edtReqQty.getText().toString().trim());
                params.put("tender_price", edtBookingRate.getText().toString().trim());
                params.put("reqd_qty_bid_placed", edtBookingQty.getText().toString().trim());
                params.put("required_qty_id", ACU.MySP.getFromSP(context, ACU.MySP.ID, ""));

                Log.e("bookOffer_PARAMS", " " + params.toString());

                return params;
            }
        };
        queue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Do you want to back from this form?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
