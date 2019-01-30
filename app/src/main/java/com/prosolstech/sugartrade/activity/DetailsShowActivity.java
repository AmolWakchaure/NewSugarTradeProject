package com.prosolstech.sugartrade.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.prosolstech.sugartrade.R;
import com.prosolstech.sugartrade.util.VU;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailsShowActivity extends AppCompatActivity {

   private Context context;
   private JSONObject jsonObject;
    private EditText edtVehicleNo, edtDriverNo, edtLicenseNo, edtQuantity, edtArrivalDate,
            edtBillName, edtBillAddress, edtBillGST, edtShipName, edtShipAddress, edtShipGST;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_show);
        context = DetailsShowActivity.this;
        setToolBar();
        intitializeUI();
        getIntentData();
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
        edtArrivalDate = (EditText) findViewById(R.id.DetailsShowActivityEdtDateArrival);
        edtQuantity = (EditText) findViewById(R.id.DetailsShowActivityEdtQuantity);
        edtBillName = (EditText) findViewById(R.id.DetailsShowActivityEdtBillName);
        edtBillAddress = (EditText) findViewById(R.id.DetailsShowActivityEdtBillAddress);
        edtBillGST = (EditText) findViewById(R.id.DetailsShowActivityEdtBillGST);
        edtShipName = (EditText) findViewById(R.id.DetailsShowActivityEdtShipName);
        edtShipAddress = (EditText) findViewById(R.id.DetailsShowActivityEdtShipAddress);
        edtShipGST = (EditText) findViewById(R.id.DetailsShowActivityEdtShipGST);
    }


    public void setData(JSONObject jsonObject) {
        try {
            edtVehicleNo.setText(jsonObject.getString("vehicle_number"));
            edtDriverNo.setText(jsonObject.getString("driver_contact"));
            edtArrivalDate.setText(VU.getddmmyyDate(jsonObject.getString("date_of_arrival")));
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
