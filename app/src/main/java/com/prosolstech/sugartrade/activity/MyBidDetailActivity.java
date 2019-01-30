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
import android.widget.LinearLayout;
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
import com.prosolstech.sugartrade.model.MyBid;
import com.prosolstech.sugartrade.util.ACU;
import com.prosolstech.sugartrade.util.DTU;
import com.prosolstech.sugartrade.util.VU;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class MyBidDetailActivity extends AppCompatActivity {

    private Context context;
    private EditText edtPaymentDate, edtCompanyName, edtValidityTime, edtStartTime, edtCategory, edtProdYear, edtPriQty, edtRequiredQty, edtLiftingDate, edtRemark, emd_et, original_et;
    private Button btnContinue;
    private EditText type_Value;
    private TextView txtQty, txtTotalQtyValue, emd_tv, due_tv_lift, type_tv;
    private String strFlag = "", strSellerId = "", strUserBy = "";
    private JSONObject jsonObject;
    private LinearLayout llPricePerQtl, llPaymentDate;
    Toolbar toolbar;
    private EditText posted_at;
    private TextView original_tv;
    private MyBid myBid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_details);
        context = MyBidDetailActivity.this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        Intent intent = getIntent();

        if (intent != null) {

            myBid = (MyBid) intent.getSerializableExtra("MyBid");
            if (ACU.MySP.getFromSP(this, ACU.MySP.ROLE, "").equalsIgnoreCase("Buyer")) {
                toolbar.setTitle("Sell Post Id :" + myBid.getId());
            } else {
                toolbar.setTitle("Buy Post Id :" + myBid.getId());
            }
        }


        setSupportActionBar(toolbar);


        intitializeUI();
        getIntentData();
        setToolBar();
        buyBidData(this);
    }


    private void buyBidData(final Context context) {
        String url = "";
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait while data fetch from server...");
        pDialog.setCancelable(false);
        pDialog.show();


        RequestQueue queue = Volley.newRequestQueue(context);
        url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/getBidDetailsById";      //for server
        Log.e("BUYER_BothURL", " ....." + url);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Log.e("dfsfs", " RESPONSE " + response);


                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            setData(jsonArray);
                            pDialog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("fdgfdgfde", error.toString());
                pDialog.dismiss();

                Toast.makeText(context, "Please Try Again!", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("role", ACU.MySP.getFromSP(context, ACU.MySP.ROLE, ""));
                params.put("offer_id", myBid.getId() + "");
                Log.e("BUYER_PARMAS", " ..... " + params.toString());

                return params;
            }
        };
        queue.add(stringRequest);
    }


    public void intitializeUI() {
        llPricePerQtl = (LinearLayout) findViewById(R.id.SellDetailsActivityLinearLayoutPricePerQtl);
        llPaymentDate = (LinearLayout) findViewById(R.id.SellDetailsActivityLinearLayoutPaymentDate);
        edtPaymentDate = (EditText) findViewById(R.id.SellDetailsActivityPaymentDate);
        btnContinue = (Button) findViewById(R.id.SellDetailsActivityButtonContinue);
        btnContinue.setVisibility(View.GONE);
        edtCompanyName = (EditText) findViewById(R.id.SellDetailsActivityCompanyName);
        edtValidityTime = (EditText) findViewById(R.id.SellDetailsActivityValidityTime);
        edtStartTime = (EditText) findViewById(R.id.SellDetailsActivityBidStartTime);
        edtCategory = (EditText) findViewById(R.id.SellDetailsActivityEdtCategory);
        edtProdYear = (EditText) findViewById(R.id.SellDetailsActivityProductionYear);
        edtPriQty = (EditText) findViewById(R.id.SellDetailsActivityPriceQuantity);
        edtRequiredQty = (EditText) findViewById(R.id.SellDetailsActivityRequiredQuantity);
        edtLiftingDate = (EditText) findViewById(R.id.SellDetailsActivityLiftingDate);
        edtRemark = (EditText) findViewById(R.id.SellDetailsActivityRemark);
        emd_et = (EditText) findViewById(R.id.emd_et);
        emd_tv = (TextView) findViewById(R.id.emd_tv);

        type_Value = (EditText) findViewById(R.id.type_Value);
        type_tv = findViewById(R.id.type_tv);
        posted_at = findViewById(R.id.posted_at);
        original_et = findViewById(R.id.original_et);
        original_tv = findViewById(R.id.original_tv);


        due_tv_lift = (TextView) findViewById(R.id.due_tv_lift);


        txtQty = (TextView) findViewById(R.id.SellDetailsActivityTxtQuantity);
        txtTotalQtyValue = (TextView) findViewById(R.id.SellDetailsActivityTotalQtyValue);

    }


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
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            myBid = (MyBid) intent.getSerializableExtra("MyBid");

        }
    }


    private void setData(JSONArray jsonArray) {

        try {


            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                setData(jsonObject);

            }

        } catch (Exception data) {
            Log.e("setData: ", data.toString());
        }
    }


    public void setData(JSONObject jsonObject) {
        try {

            if (ACU.MySP.getFromSP(context, ACU.MySP.ROLE, "").equalsIgnoreCase("Seller")) {
                Log.e("INSDIE", "SELLER " + jsonObject.toString());


                type_Value.setVisibility(View.GONE);
                type_tv.setVisibility(View.GONE);
                //  txtQty.setText("Available Quantity  (in Quintal)");
                strSellerId = jsonObject.getString("id");
                strUserBy = jsonObject.getString("userby");
                due_tv_lift.setText("Due date of Delivery");
                edtCompanyName.setText(jsonObject.getString("company_name"));

                posted_at.setText(DTU.changeDateTimeFormat(jsonObject.getString("created_date")));
                long hours = Long.parseLong(jsonObject.getString("validity_time")) / 60; //since both are ints, you get an int
                long minutes = Long.parseLong(jsonObject.getString("validity_time")) % 60;
                String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
                edtValidityTime.setText(timeLeftFormatted);


//                edtValidityTime.setText(jsonObject.getString("validity_time"));
                edtStartTime.setText(DTU.changeTime(jsonObject.getString("bid_start_time")));

                edtCategory.setText(jsonObject.getString("catName"));
                edtProdYear.setText(jsonObject.getString("production_year"));
                edtPriQty.setText(jsonObject.getString("price_per_qtl"));

                emd_et.setVisibility(View.GONE);
                emd_tv.setVisibility(View.GONE);
//                emd_et.setText(jsonObject.getString("emd"));


                if (ACU.MySP.getFromSP(context, ACU.MySP.ROLE, "").equals("Buyer")) {

                    original_tv.setVisibility(View.VISIBLE);
                    if (jsonObject.getString("original_qty").contains("-")){
                        edtRequiredQty.setText("0");

                    }else{
                        edtRequiredQty.setText(jsonObject.getString("original_qty"));
                    }
                    txtQty.setText("Available Quantity  (in Quintal)");
                    original_et.setText(jsonObject.getString("available_qty"));


                } else if (ACU.MySP.getFromSP(context, ACU.MySP.ROLE, "").equals("Seller")) {
                    original_tv.setVisibility(View.GONE);
                    original_et.setVisibility(View.GONE);

                    edtRequiredQty.setText(jsonObject.getString("required_qty"));
                    txtQty.setText("Required Quantity  (in Quintal)");
                }
                edtLiftingDate.setText(VU.getddmmyyDate(jsonObject.getString("due_delivery_date")));
                edtRemark.setText(jsonObject.getString("remark"));

                txtTotalQtyValue.setVisibility(View.GONE);
//                if (!jsonObject.getString("claimed").equalsIgnoreCase("null")) {
//                    txtTotalQtyValue.setText("Total Acquired quantity : " + jsonObject.getString("claimed"));
//                } else {
//                    txtTotalQtyValue.setText("Total Acquired quantity : (0)");
//                }


            } else if (ACU.MySP.getFromSP(context, ACU.MySP.ROLE, "").equalsIgnoreCase("Buyer")) {

                type_Value.setVisibility(View.VISIBLE);
                type_tv.setVisibility(View.VISIBLE);
                type_Value.setText(jsonObject.getString("type"));


                posted_at.setText(DTU.changeDateTimeFormat(jsonObject.getString("created_date")));

                Log.e("INSDIE", "BUYER " + jsonObject.toString());
                due_tv_lift.setText("Due date of Lifting");
                due_tv_lift.setText("Due date of Lifting");
                //txtQty.setText("Required Quantity  (in Quintal)");
                strSellerId = jsonObject.getString("id");
                strUserBy = jsonObject.getString("userby");

                edtCompanyName.setText(jsonObject.getString("company_name"));
                original_tv.setVisibility(View.VISIBLE);
                original_et.setVisibility(View.VISIBLE);
                original_et.setText(jsonObject.getString("available_qty"));

                long hours = Long.parseLong(jsonObject.getString("validity_time")) / 60; //since both are ints, you get an int
                long minutes = Long.parseLong(jsonObject.getString("validity_time")) % 60;

                String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
                edtValidityTime.setText(timeLeftFormatted);

                edtStartTime.setText(DTU.changeTime(jsonObject.getString("bid_start_time")));


                edtCategory.setText(jsonObject.getString("catName"));
                edtProdYear.setText(jsonObject.getString("production_year"));
                emd_tv.setVisibility(View.VISIBLE);
                emd_et.setVisibility(View.VISIBLE);
                emd_et.setText(jsonObject.getString("emd"));


                if (!jsonObject.getString("type").equalsIgnoreCase("Tender")) {
                    llPricePerQtl.setVisibility(View.VISIBLE);
                    edtPriQty.setText(jsonObject.getString("price_per_qtl"));
                } else {
                    llPricePerQtl.setVisibility(View.GONE);
                    edtPriQty.setText("");
                }

                if (ACU.MySP.getFromSP(context, ACU.MySP.ROLE, "").equals("Buyer")) {

                    if (jsonObject.getString("original_qty").contains("-")){
                        edtRequiredQty.setText("0");

                    }else{
                        edtRequiredQty.setText(jsonObject.getString("original_qty"));
                    }
//                    edtRequiredQty.setText(jsonObject.getString("original_qty"));
                    txtQty.setText("Current Available Quantity  (in Quintal)");
                    llPaymentDate.setVisibility(View.VISIBLE);                                              // if record is sell bid than due dat of payment field show
                    edtPaymentDate.setText(VU.getddmmyyDate(jsonObject.getString("payment_date")));
                } else if (ACU.MySP.getFromSP(context, ACU.MySP.ROLE, "").equals("Seller")) {
                    edtRequiredQty.setText(jsonObject.getString("required_qty"));
                    txtQty.setText("Required Quantity  (in Quintal)");
                    llPaymentDate.setVisibility(View.GONE);                                         // if record is sell bid than due dat of payment field hide
                }
                edtLiftingDate.setText(VU.getddmmyyDate(jsonObject.getString("due_lifting_date")));                   // in Buyer case 'due_lifting_date" as a "due_delivery_date"
                edtRemark.setText(jsonObject.getString("remark"));

                if (!jsonObject.getString("claimed").equalsIgnoreCase("null")) {
                    txtTotalQtyValue.setText("Total Acquired quantity : " + jsonObject.getString("claimed"));
                } else {
                    txtTotalQtyValue.setText("Total Acquired quantity : (0)");
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
