package com.prosolstech.sugartrade.activity;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.prosolstech.sugartrade.R;
import com.prosolstech.sugartrade.util.ACU;
import com.prosolstech.sugartrade.util.DTU;
import com.prosolstech.sugartrade.util.VU;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;


public class SellDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context;
    private EditText edtPaymentDate, edtCompanyName, edtValidityTime, edtStartTime, edtCategory, edtProdYear, edtPriQty, SellDetailsActivityRequiredQuantity, edtLiftingDate, edtRemark, emd_et, original_et;
    private Button btnContinue;
    private EditText type_Value;
    private TextView txtQty, emd_tv, due_tv_lift, type_tv;
    private String strFlag = "", strSellerId = "", strUserBy = "";
    private JSONObject jsonObject;
    private LinearLayout llPricePerQtl, llPaymentDate;
    Toolbar toolbar;
    private EditText posted_at,SellDetailsActivityOriginalRequiredQuantity,SellDetailsActivityTotalAquiredQuantity;
    private TextView original_tv;
    private ImageView SellerListAdapterImgUnFav;

    //private EditText original_required_et;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_details);
        context = SellDetailsActivity.this;
        intitializeUI();
        getIntentData();
        setToolBar();
    }

    public void intitializeUI() {





        //original_required_tv =  findViewById(R.id.original_required_tv);
       // totoal_required_tv =  findViewById(R.id.totoal_required_tv);
        //original_required_et =  findViewById(R.id.original_required_et);







        llPricePerQtl = (LinearLayout) findViewById(R.id.SellDetailsActivityLinearLayoutPricePerQtl);
        llPaymentDate = (LinearLayout) findViewById(R.id.SellDetailsActivityLinearLayoutPaymentDate);
        edtPaymentDate = (EditText) findViewById(R.id.SellDetailsActivityPaymentDate);
        btnContinue = (Button) findViewById(R.id.SellDetailsActivityButtonContinue);
        edtCompanyName = (EditText) findViewById(R.id.SellDetailsActivityCompanyName);
        edtValidityTime = (EditText) findViewById(R.id.SellDetailsActivityValidityTime);
        edtStartTime = (EditText) findViewById(R.id.SellDetailsActivityBidStartTime);
        edtCategory = (EditText) findViewById(R.id.SellDetailsActivityEdtCategory);
        edtProdYear = (EditText) findViewById(R.id.SellDetailsActivityProductionYear);
        edtPriQty = (EditText) findViewById(R.id.SellDetailsActivityPriceQuantity);
        SellDetailsActivityRequiredQuantity = (EditText) findViewById(R.id.SellDetailsActivityRequiredQuantity);
        edtLiftingDate = (EditText) findViewById(R.id.SellDetailsActivityLiftingDate);
        edtRemark = (EditText) findViewById(R.id.SellDetailsActivityRemark);
        emd_et = (EditText) findViewById(R.id.emd_et);
        emd_tv = (TextView) findViewById(R.id.emd_tv);

        SellDetailsActivityOriginalRequiredQuantity = (EditText) findViewById(R.id.SellDetailsActivityOriginalRequiredQuantity);
        SellDetailsActivityTotalAquiredQuantity = (EditText) findViewById(R.id.SellDetailsActivityTotalAquiredQuantity);
        type_Value = (EditText) findViewById(R.id.type_Value);
        type_tv = findViewById(R.id.type_tv);
        posted_at = findViewById(R.id.posted_at);
        original_et = findViewById(R.id.original_et);
        original_tv = findViewById(R.id.original_tv);
        SellerListAdapterImgUnFav = findViewById(R.id.SellerListAdapterImgUnFav);


        due_tv_lift = (TextView) findViewById(R.id.due_tv_lift);


        txtQty = (TextView) findViewById(R.id.SellDetailsActivityTxtQuantity);
      //  txtTotalQtyValue = (TextView) findViewById(R.id.SellDetailsActivityTotalQtyValue);

        btnContinue.setOnClickListener(this);
    }


    private void setToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (strFlag.equalsIgnoreCase("Seller")) {
            try {
                toolbar.setTitle("Buy Post id :" + jsonObject.getString("id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

//            toolbar.setTitle(getResources().getString(R.string.buy_offer));
        } else if (strFlag.equalsIgnoreCase("Buyer")) {
//            toolbar.setTitle(getResources().getString(R.string.sell_offer));

            try {
                toolbar.setTitle("Sell Post id :" + jsonObject.getString("id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.SellDetailsActivityButtonContinue:


                if (ACU.MySP.getFromSP(context, ACU.MySP.ROLE, "").equalsIgnoreCase("Buyer")) {

                    try {
                        if (jsonObject.getString("original_qty").equalsIgnoreCase("0") || jsonObject.getString("original_qty").contains("-")) {
                            Toast.makeText(context, "This Request is Fulfill", Toast.LENGTH_SHORT).show();
                        } else {


                            if (VU.isConnectingToInternet(context)) {
                                if (!strUserBy.equalsIgnoreCase(ACU.MySP.getFromSP(context, ACU.MySP.ID, ""))) {
                                    Intent in = new Intent(context, BidBookingActivity.class);                  // add on 28-07-18
                                    in.putExtra("flag", strFlag);
                                    in.putExtra("data", jsonObject.toString());
                                    startActivity(in);
                                    finish();
                                    //bookOffer();
                                    Log.e("INSIDE_IF", " : ");
                                } else {
                                    Toast.makeText(context, "This is your offer so you can not revert this", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {

                    if (VU.isConnectingToInternet(context)) {
                        if (!strUserBy.equalsIgnoreCase(ACU.MySP.getFromSP(context, ACU.MySP.ID, ""))) {
                            Intent in = new Intent(context, BidBookingActivity.class);                  // add on 28-07-18
                            in.putExtra("flag", strFlag);
                            in.putExtra("data", jsonObject.toString());
                            startActivity(in);
                            finish();
                            //bookOffer();
                            Log.e("INSIDE_IF", " : ");
                        } else {
                            Toast.makeText(context, "This is your offer so you can not revert this", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
                break;
        }
    }

    private void getIntentData() {

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            try {
                strFlag = extras.getString("flag");
                jsonObject = new JSONObject(extras.getString("data"));


                Log.e("jsonObject", " " + jsonObject);
                Log.e("strFlag", " " + strFlag);
                setData(jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setData(JSONObject jsonObject) {
        try {

            if (strFlag.equalsIgnoreCase("Seller")) {



                Log.e("INSDIE", "SELLER " + jsonObject.toString());




              //  original_required_tv.setVisibility(View.VISIBLE);
                //totoal_required_tv.setVisibility(View.VISIBLE);
                //original_required_et.setVisibility(View.VISIBLE);

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

                edtCategory.setText(jsonObject.getString("CategoryName"));
                edtProdYear.setText(jsonObject.getString("production_year"));
                edtPriQty.setText(jsonObject.getString("price_per_qtl"));
                SellDetailsActivityOriginalRequiredQuantity.setText(jsonObject.getString("required_qty"));
                SellDetailsActivityTotalAquiredQuantity.setText(jsonObject.getString("acquired_qty"));

                emd_et.setVisibility(View.GONE);
                emd_tv.setVisibility(View.GONE);
//                emd_et.setText(jsonObject.getString("emd"));


                if (ACU.MySP.getFromSP(context, ACU.MySP.ROLE, "").equals("Buyer")) {

                    original_tv.setVisibility(View.VISIBLE);
                    SellDetailsActivityRequiredQuantity.setText(jsonObject.getString("original_qty"));
                    txtQty.setText("Available Quantity  (in Quintal)");
                    original_et.setText(jsonObject.getString("available_qty"));


                } else if (ACU.MySP.getFromSP(context, ACU.MySP.ROLE, "").equals("Seller")) {
                    original_tv.setVisibility(View.GONE);
                    original_et.setVisibility(View.GONE);

                    SellDetailsActivityRequiredQuantity.setText(jsonObject.getString("required_qty"));
                    txtQty.setText("Current Required Quantity  (in Quintal)");
                }
                edtLiftingDate.setText(VU.getddmmyyDate(jsonObject.getString("due_delivery_date")));
                edtRemark.setText(jsonObject.getString("remark"));

                //txtTotalQtyValue.setVisibility(View.GONE);
//                if (!jsonObject.getString("claimed").equalsIgnoreCase("null")) {
//                    txtTotalQtyValue.setText("Total Acquired quantity : " + jsonObject.getString("claimed"));
//                } else {
//                    txtTotalQtyValue.setText("Total Acquired quantity : (0)");
//                }


                if (jsonObject.getString("is_favorite").equalsIgnoreCase("y")) {
                    SellerListAdapterImgUnFav.setVisibility(View.VISIBLE);
                } else if (jsonObject.getString("is_favorite").equalsIgnoreCase("N")) {
                    SellerListAdapterImgUnFav.setVisibility(View.GONE);
                }


            } else if (strFlag.equalsIgnoreCase("Buyer")) {

               // original_required_tv.setVisibility(View.GONE);
                //totoal_required_tv.setVisibility(View.GONE);
                //original_required_et.setVisibility(View.GONE);

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


                edtCategory.setText(jsonObject.getString("CategoryName"));
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
                    SellDetailsActivityRequiredQuantity.setText(jsonObject.getString("original_qty"));
                    txtQty.setText("Current Available Quantity  (in Quintal)");
                    llPaymentDate.setVisibility(View.VISIBLE);                                              // if record is sell bid than due dat of payment field show
                    edtPaymentDate.setText(VU.getddmmyyDate(jsonObject.getString("payment_date")));
                } else if (ACU.MySP.getFromSP(context, ACU.MySP.ROLE, "").equals("Seller")) {
                    SellDetailsActivityRequiredQuantity.setText(jsonObject.getString("required_qty"));
                    txtQty.setText("Required Quantity  (in Quintal)");
                    llPaymentDate.setVisibility(View.GONE);                                         // if record is sell bid than due dat of payment field hide
                }
                edtLiftingDate.setText(VU.getddmmyyDate(jsonObject.getString("due_lifting_date")));                   // in Buyer case 'due_lifting_date" as a "due_delivery_date"
                edtRemark.setText(jsonObject.getString("remark"));

               /* if (!jsonObject.getString("claimed").equalsIgnoreCase("null")) {
                    txtTotalQtyValue.setText("Total Acquired quantity : " + jsonObject.getString("claimed"));
                } else {
                    txtTotalQtyValue.setText("Total Acquired quantity : (0)");
                }
*/

                if (jsonObject.getString("is_favorite").equalsIgnoreCase("y")) {
                    SellerListAdapterImgUnFav.setVisibility(View.VISIBLE);
                } else if (jsonObject.getString("is_favorite").equalsIgnoreCase("N")) {
                    SellerListAdapterImgUnFav.setVisibility(View.GONE);
                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
