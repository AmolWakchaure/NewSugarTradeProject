package com.prosolstech.sugartrade.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import com.prosolstech.sugartrade.classes.T;
import com.prosolstech.sugartrade.util.ACU;
import com.prosolstech.sugartrade.util.DTU;
import com.prosolstech.sugartrade.util.VU;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class DetailsShowActivity extends AppCompatActivity {

   private Context context;
   private JSONObject jsonObject;
    private EditText edtVehicleNo, edtDriverNo, edtLicenseNo, edtQuantity,
            edtBillName, edtBillAddress, edtBillGST, edtShipName, edtShipAddress, edtShipGST;

    private Button updateDetails_btn,edtArrivalDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_show);
        context = DetailsShowActivity.this;
        setToolBar();
        intitializeUI();





        getIntentData();

        edtArrivalDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DTU.showDatePickerDialog(context, DTU.FLAG_OLD_AND_NEW, edtArrivalDate);
            }
        });

        updateDetails_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (VU.isConnectingToInternet(context))
                {

                    if(!vaslidateEmptyField(edtVehicleNo,"Enter vehicle number"))
                    {
                        return;
                    }
                    if(!vaslidateEmptyField(edtDriverNo,"Enter driver number"))
                    {
                        return;
                    }
                    if(!vaslidateEmptyField(edtArrivalDate,"Select arrive date"))
                    {
                        return;
                    }
                    if(!vaslidateEmptyField(edtQuantity,"Enter quantity"))
                    {
                        return;
                    }
                    if(!vaslidateEmptyField(edtBillName,"Enter bill name"))
                    {
                        return;
                    }
                    if(!vaslidateEmptyField(edtBillAddress,"Enter bill address"))
                    {
                        return;
                    }
                    if(!vaslidateEmptyField(edtBillGST,"Enter bill GST"))
                    {
                        return;
                    }
                    if(!vaslidateEmptyField(edtShipName,"Enter ship name"))
                    {
                        return;
                    }
                    if(!vaslidateEmptyField(edtShipAddress,"Enter ship address"))
                    {
                        return;
                    }
                    if(!vaslidateEmptyField(edtShipGST,"Enter ship GST"))
                    {
                        return;
                    }

                    updateVehicleDetails();
                }
                else
                {
                    T.t("Oops ! internet connection off");
                }

            }
        });
    }

    public Boolean vaslidateEmptyField(EditText editText, String message)
    {
        if(editText.getText().toString().trim().isEmpty())
        {
            editText.setError(message);
            return false;
        }
        else
        {
            return true;
        }
    }
    public Boolean vaslidateEmptyField(Button editText, String message)
    {
        if(editText.getText().toString().trim().isEmpty())
        {
            editText.setError(message);
            return false;
        }
        else
        {
            return true;
        }
    }

    private void updateVehicleDetails()
    {

        try {


            String url = "";
            final ProgressDialog pDialog = new ProgressDialog(context);
            pDialog.setMessage("Please wait, updating vehicle details...");
            pDialog.setCancelable(false);
            pDialog.show();
            RequestQueue queue = Volley.newRequestQueue(context);
            url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/updateVehicleDetails";
            Log.e("MY_BOOK_BUY_URL", " ....." + url);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Display the response string.
                            Log.e("MyBuyBooking", " RESPONSE " + response);
                            pDialog.dismiss();

                           try
                           {

                               JSONObject jsonObject = new JSONObject(response);

                               String message = jsonObject.getString("message");
                               if(message.equals("success"))
                               {
                                    T.t("Vehicle details successfully updated");
                                    finish();
                               }
                               else
                               {
                                   T.t("Problem to update Vehicle details.");
                                   finish();
                               }
                           }
                           catch (Exception e)
                           {

                           }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("No Response", "FOUND");
                    pDialog.dismiss();

                    Toast.makeText(context, "Problem to update vehicle details", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();

                    try {

                        params.put("dispatchId", ""+jsonObject.getString("dispatchId"));
                        params.put("bid_id", ""+jsonObject.getString("bid_id"));
                        params.put("vehicle_number", edtVehicleNo.getText().toString());
                        params.put("driver_contact", edtDriverNo.getText().toString());
                        params.put("date_of_arrival", edtArrivalDate.getText().toString());
                        params.put("quantity", edtQuantity.getText().toString());
                        params.put("bill_to_name", edtBillName.getText().toString());
                        params.put("bill_to_address", edtBillAddress.getText().toString());
                        params.put("bill_to_gst", edtBillGST.getText().toString());
                        params.put("ship_to_name", edtShipName.getText().toString());
                        params.put("ship_to_address", edtShipAddress.getText().toString());
                        params.put("ship_to_gst", edtShipGST.getText().toString());
                        Log.e("MY_BOOK_BUY", " : " + params.toString());//{type=buy, id=11, role=Buyer}

                    }
                    catch (JSONException e)
                    {

                    }

                    return params;
                }
            };
            queue.add(stringRequest);

        }
        catch (Exception e)
        {

        }
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void getIntentData() {

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            try {
                jsonObject = new JSONObject(extras.getString("data"));
                Log.e("Sell_jsonObject", " " + jsonObject);
                setData(jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void intitializeUI() {


        edtVehicleNo = (EditText) findViewById(R.id.DetailsShowActivityEdtVehicleNo);
        edtDriverNo = (EditText) findViewById(R.id.DetailsShowActivityEdtDriverNo);
        edtArrivalDate = (Button) findViewById(R.id.DetailsShowActivityEdtDateArrival);
        edtQuantity = (EditText) findViewById(R.id.DetailsShowActivityEdtQuantity);
        edtBillName = (EditText) findViewById(R.id.DetailsShowActivityEdtBillName);
        edtBillAddress = (EditText) findViewById(R.id.DetailsShowActivityEdtBillAddress);
        edtBillGST = (EditText) findViewById(R.id.DetailsShowActivityEdtBillGST);
        edtShipName = (EditText) findViewById(R.id.DetailsShowActivityEdtShipName);
        edtShipAddress = (EditText) findViewById(R.id.DetailsShowActivityEdtShipAddress);
        edtShipGST = (EditText) findViewById(R.id.DetailsShowActivityEdtShipGST);
        updateDetails_btn = (Button) findViewById(R.id.updateDetails_btn);
    }


    public void setData(JSONObject jsonObject) {
        try {
            edtVehicleNo.setText(jsonObject.getString("vehicle_number"));
            edtDriverNo.setText(jsonObject.getString("driver_contact"));
            edtArrivalDate.setText(VU.getddmmyyDate(VU.formatDate(jsonObject.getString("date_of_arrival"))));
            edtQuantity.setText(jsonObject.getString("quantity"));
            edtBillName.setText(jsonObject.getString("bill_to_name"));
            edtBillAddress.setText(jsonObject.getString("bill_to_address"));
            edtBillGST.setText(jsonObject.getString("bill_to_gst"));
            edtShipName.setText(jsonObject.getString("ship_to_name"));
            edtShipAddress.setText(jsonObject.getString("ship_to_address"));
            edtShipGST.setText(jsonObject.getString("ship_to_gst"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
}
