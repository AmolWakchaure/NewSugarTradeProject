package com.prosolstech.sugartrade.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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
import com.prosolstech.sugartrade.adapter.VehicleListAdapter;
import com.prosolstech.sugartrade.classes.T;
import com.prosolstech.sugartrade.util.ACU;
import com.prosolstech.sugartrade.util.DTU;
import com.prosolstech.sugartrade.util.VU;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.Manifest.permission.CALL_PHONE;

public class BookingDetailsActivity extends AppCompatActivity {

    Context context;
    Button btnSend, btnView, btnReview, btnCancel, btnSubmit;
    EditText edtCompName, edtcategory, edtProdYear, edtQuantity, edtPriceQtl, edtContactNo,
            edtReview, edtAccountName, edtAccountNo, edtBankName, edtGSTIN, edtIFSC, post_date_time_et, confir_date_time_et,
            remark_et, due_date_of_payment_et, due_date_of_lifting_et, emd_et;
    TextView due_tv, emd_tv, due_pay_tv;
    String strFlag = "", strOfferID = "", strMobileNo = "", strUserID = "", strUserBy = "",navigate_status = "";
    JSONObject jsonObject;
    TextView txtCompName, txtRole, txtTotalAmount, required_qty;
    RatingBar ratingBar;
    Dialog ad;
    ImageView imgCall,SellerListAdapterImgUnFav;
    double totalGst;

    private TextView validity_time_tv, start_time_tv, type_tv, original_qty_tv,aquired_qty,available_qty,due_lifting_tv;
    private EditText validity_time, start_time, end_time, type, original_qty,
            BookingDetailsActivityEdtAquiredQuantity,
            BookingDetailsActivityEdtAvailableQuantity,due_lifting_et;
    private LinearLayout
            emdHideLayout;

    private String typeStatus;

    String viewFlag = "false";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);
        context = BookingDetailsActivity.this;

        checkPermission();
        requestPermission();
        initializeUI();
        getIntentData();
        FetchVehicleDetail();
        setToolBar();



    }

    private void initializeUI() {
        txtTotalAmount = (TextView) findViewById(R.id.BookingDetailsActivityTxtTotalAmount);         // add on 24-08-18
        aquired_qty = (TextView) findViewById(R.id.aquired_qty);
        available_qty = (TextView) findViewById(R.id.available_qty);
        BookingDetailsActivityEdtAquiredQuantity = (EditText) findViewById(R.id.BookingDetailsActivityEdtAquiredQuantity);
        BookingDetailsActivityEdtAvailableQuantity = (EditText) findViewById(R.id.BookingDetailsActivityEdtAvailableQuantity);

        edtCompName = (EditText) findViewById(R.id.BookingDetailsActivityEdtCompanyName);
        emdHideLayout = (LinearLayout) findViewById(R.id.emdHideLayout);

        due_lifting_tv = (TextView) findViewById(R.id.due_lifting_tv);
        due_lifting_et = (EditText) findViewById(R.id.due_lifting_et);


        validity_time_tv = findViewById(R.id.validity_time_tv);
        start_time_tv = findViewById(R.id.start_time_tv);
        validity_time = findViewById(R.id.validity_time);
        start_time = findViewById(R.id.start_time);
        type = findViewById(R.id.type);
        type_tv = findViewById(R.id.type_tv);
        required_qty = findViewById(R.id.required_qty);
        original_qty_tv = findViewById(R.id.original_qty_tv);
        original_qty = findViewById(R.id.original_qty);

        remark_et = (EditText) findViewById(R.id.remark_et);
        emd_et = (EditText) findViewById(R.id.emd_et);
        emd_tv = (TextView) findViewById(R.id.emd_tv);
        due_pay_tv = (TextView) findViewById(R.id.due_pay_tv);
        due_date_of_payment_et = (EditText) findViewById(R.id.due_date_of_payment_et);
        due_date_of_lifting_et = (EditText) findViewById(R.id.due_date_of_lifting_et);
        end_time = (EditText) findViewById(R.id.end_time);
        due_tv = findViewById(R.id.due_tv);


        edtcategory = (EditText) findViewById(R.id.BookingDetailsActivityEdtCategory);
        edtProdYear = (EditText) findViewById(R.id.BookingDetailsActivityEdtProductiionYear);
        edtPriceQtl = (EditText) findViewById(R.id.BookingDetailsActivityEdtPricePerQtl);
        edtQuantity = (EditText) findViewById(R.id.BookingDetailsActivityEdtQuantity);//curr req
        post_date_time_et = (EditText) findViewById(R.id.post_date_time_et);


        imgCall = (ImageView) findViewById(R.id.BookingDetailsActivityImgCall);         // add on 24-08-18
        SellerListAdapterImgUnFav = (ImageView) findViewById(R.id.SellerListAdapterImgUnFav);
        edtContactNo = (EditText) findViewById(R.id.BookingDetailsActivityEdtContactNumber);         // add on 24-08-18

        edtAccountName = (EditText) findViewById(R.id.BookingDetailsActivityEdtAccountName);
        edtAccountNo = (EditText) findViewById(R.id.BookingDetailsActivityEdtAccountNo);
        edtBankName = (EditText) findViewById(R.id.BookingDetailsActivityEdtBankName);
        edtGSTIN = (EditText) findViewById(R.id.BookingDetailsActivityEdtGstin);
        edtIFSC = (EditText) findViewById(R.id.BookingDetailsActivityEdtIFSC);
        confir_date_time_et = (EditText) findViewById(R.id.confir_date_time_et);


        btnSend = (Button) findViewById(R.id.BookingDetailsActivityBtnSend);
        btnView = (Button) findViewById(R.id.BookingDetailsActivityBtnView);

        btnReview = (Button) findViewById(R.id.BookingDetailsActivityBtnReview);


        imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + strMobileNo));

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(callIntent);

            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(context, VehiclesListAcitivity.class);
                in.putExtra("offer_id", strOfferID);
                in.putExtra("viewFlag", viewFlag);
                startActivity(in);
                finish();
            }
        });
        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent in = new Intent(context, VehicleDetailsActivity.class);
                in.putExtra("offer_id", strOfferID);
                in.putExtra("viewFlag", viewFlag);
                startActivity(in);
            }
        });

        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!strUserBy.equalsIgnoreCase(ACU.MySP.getFromSP(context, ACU.MySP.ID, ""))) {
                    ad = new Dialog(context);
                    ad.setContentView(R.layout.review);
                    ad.setCanceledOnTouchOutside(false);
                    ad.setTitle("Review");
                    ad.show();
                    txtCompName = (TextView) ad.findViewById(R.id.ReviewDialogTxtCompName);
                    txtRole = (TextView) ad.findViewById(R.id.ReviewDialogTxtRole);
                    ratingBar = (RatingBar) ad.findViewById(R.id.ReviewDialogRatingBar);
                    edtReview = (EditText) ad.findViewById(R.id.ReviewDialogEdtReview);
                    btnCancel = (Button) ad.findViewById(R.id.ReviewDialogBtnCancel);
                    btnSubmit = (Button) ad.findViewById(R.id.ReviewDialogBtnSubmit);

                    txtRole.setText(ACU.MySP.getFromSP(context, ACU.MySP.ROLE, ""));
                    txtCompName.setText(edtCompName.getText().toString().trim());

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ad.dismiss();
                        }
                    });
                    btnSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (VU.isConnectingToInternet(context)) {
                                if (validate()) {
                                    review();
                                }
                            }
                        }
                    });
                } else {
                    Toast.makeText(context, "This is your record you cannot give rating", Toast.LENGTH_SHORT).show();
                }
            }
        });


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
    public void onBackPressed() {

        if(navigate_status != null)
        {
            if(navigate_status.equals("from_my_bids"))
            {
                finish();
            }
        }
        else
        {
            Intent intent = new Intent(this, BuySellDashBoardActivity.class);
            intent.putExtra("BookingDetails", "val");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
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
                        pDialog.dismiss();
                        Log.e("FetchVehicleDetail", " RESPONSE " + response);

                        setListAdapter(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("No Response", "FOUND");
                pDialog.dismiss();


                if (ACU.MySP.getFromSP(context, ACU.MySP.ROLE, "").equalsIgnoreCase("Seller")) {


                    btnSend.setBackgroundColor(BookingDetailsActivity.this.getResources().getColor(R.color.colorAccent));
                    btnSend.setEnabled(true);
                    btnView.setBackgroundColor(BookingDetailsActivity.this.getResources().getColor(android.R.color.darker_gray));
                    btnView.setEnabled(false);


                } else {


                    btnSend.setBackgroundColor(BookingDetailsActivity.this.getResources().getColor(R.color.colorAccent));
                    btnSend.setEnabled(true);
                    btnView.setBackgroundColor(BookingDetailsActivity.this.getResources().getColor(android.R.color.darker_gray));
                    btnView.setEnabled(false);
                }


            }
        })

        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("bid_id", strOfferID);
                params.put("userby", ACU.MySP.getFromSP(context, ACU.MySP.ID, ""));

                if (ACU.MySP.getFromSP(context, ACU.MySP.ROLE, "").equalsIgnoreCase("Seller"))
                {
                    params.put("role", "Buyer");
                }
                else
                {
                    params.put("role", "Seller");

                }


                Log.e("FilterData_PARAMS", "" + params.toString());
                Log.e("FilterData_PARAMS", "prefs_role" + ACU.MySP.getFromSP(context, ACU.MySP.ROLE, ""));
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
            if (ACU.MySP.getFromSP(context, ACU.MySP.ROLE, "").equalsIgnoreCase("Seller")) {

                if (array != null && array.length() > 0)
                {

                    btnSend.setBackgroundColor(BookingDetailsActivity.this.getResources().getColor(android.R.color.darker_gray));
                    btnSend.setEnabled(false);
                    btnView.setBackgroundColor(BookingDetailsActivity.this.getResources().getColor(R.color.colorAccent));
                    btnView.setEnabled(true);
                    viewFlag = "true";

                }
                else
                {
                    btnSend.setBackgroundColor(BookingDetailsActivity.this.getResources().getColor(R.color.colorAccent));
                    btnSend.setEnabled(true);
                    btnView.setBackgroundColor(BookingDetailsActivity.this.getResources().getColor(android.R.color.darker_gray));
                    btnView.setEnabled(false);
                    viewFlag = "false";
                }

            }
            else
            {
                if (array != null && array.length() > 0) {
                    btnSend.setBackgroundColor(BookingDetailsActivity.this.getResources().getColor(android.R.color.darker_gray));
                    btnSend.setEnabled(false);
                    btnView.setBackgroundColor(BookingDetailsActivity.this.getResources().getColor(R.color.colorAccent));
                    btnView.setEnabled(true);
                    viewFlag = "true";

                } else {
                    btnSend.setBackgroundColor(BookingDetailsActivity.this.getResources().getColor(R.color.colorAccent));
                    btnSend.setEnabled(true);
                    btnView.setBackgroundColor(BookingDetailsActivity.this.getResources().getColor(android.R.color.darker_gray));
                    btnView.setEnabled(false);
                    viewFlag = "false";
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private boolean validate() {
        if (ratingBar.getRating() == 0) {
            Toast.makeText(context, "Please give rating", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void setToolBar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        try {
            if (typeStatus.equalsIgnoreCase("sell")) {
                toolbar.setTitle("Sell Post id : " + jsonObject.getString("offer_id"));
            } else {
                toolbar.setTitle("Buy Post id : " + jsonObject.getString("offer_id"));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    String typeStatusData,flag_another;

    private void getIntentData() {

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            try {
                strFlag = extras.getString("flag");
                flag_another = extras.getString("flag_another");
                typeStatus = extras.getString("type");
                typeStatusData = extras.getString("typeStatus");
                jsonObject = new JSONObject(extras.getString("data"));
                navigate_status = extras.getString("navigate_status");
                T.e("strFlag : " + jsonObject);
                T.e("typeStatus : " + typeStatus);
                T.e("typeStatusData : " + typeStatusData);
                setData(jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setData(JSONObject jsonObject) {
        try
        {



            if (strFlag.equalsIgnoreCase("Seller"))
            {

                if(flag_another.equals("bid_display"))
                {
                    /*long hours = Long.parseLong(jsonObject.getString("validity_time")) / 60; //since both are ints, you get an int
                    long minutes = Long.parseLong(jsonObject.getString("validity_time")) % 60;
                    String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
                    validity_time.setText(timeLeftFormatted);

                    String bidEndTime = T.returnEndTime(jsonObject.getString("bid_end_time"));
                    start_time.setText(DTU.changeTime(jsonObject.getString("bid_start_time")));
                    end_time.setText(bidEndTime);

                    strOfferID = jsonObject.getString("offer_id");                         // this key is in buy_bid  or sell_bid table id column key.
                    //strMobileNo = jsonObject.getString("mobile");
                    strUserID = jsonObject.getString("userId");          // in this key other user's id
                    strUserBy = jsonObject.getString("userby");              // in this key user Login id set


                    edtCompName.setText(jsonObject.getString("company_name"));
                    edtcategory.setText(jsonObject.getString("catName"));
                    edtProdYear.setText(jsonObject.getString("production_year"));
                    remark_et.setText(jsonObject.getString("remark"));


                    if(typeStatus.equals("sell"))
                    {
                        emdHideLayout.setVisibility(View.VISIBLE);
                        emd_et.setText(jsonObject.getString("emd"));
                        aquired_qty.setText("Total claimed Quantity");

                        due_tv.setText("Due Date of Delivery");
                        due_date_of_lifting_et.setText(DTU.changeDateTimeFormatForDue(jsonObject.getString("due_lifting_date")));

                    }
                    else
                    {
                        aquired_qty.setText("Total acquired Quantity");

                        due_tv.setText("Due Date of Delivery");
                        due_date_of_lifting_et.setText(DTU.changeDateTimeFormatForDue(jsonObject.getString("due_lifting_date")));
                    }

                    due_pay_tv.setVisibility(View.GONE);
                    String required_qty = jsonObject.getString("required_qty");
                    String allotted = jsonObject.getString("allotted");

                    original_qty.setText(required_qty);
                    BookingDetailsActivityEdtAquiredQuantity.setText(allotted);
                    edtQuantity.setText(jsonObject.getString("curr_req_qty"));

                    BookingDetailsActivityEdtAvailableQuantity.setText(jsonObject.getString("curr_req_qty"));


                    if (jsonObject.has("type")) {                                              // if type tender or open
                        if (jsonObject.getString("type").equalsIgnoreCase("Tender")) {            // this is use for tender case for Seller Login
                            edtPriceQtl.setText(jsonObject.getString("tender_price"));
                            int iTotalAmount = Integer.valueOf(jsonObject.getString("allotted")) * Integer.valueOf(jsonObject.getString("tender_price"));
                            Log.e("iTotalAmount", " : " + iTotalAmount);
                            double res = (iTotalAmount / 100.0f) * 5;
                            totalGst = iTotalAmount + res;
                            txtTotalAmount.setText(Double.toString(totalGst));
                            Log.e("totalGst", " : " + totalGst);
                        } else {                                                                             // this is use for open case  for Seller Login
                            edtPriceQtl.setText(jsonObject.getString("price_per_qtl"));
                            int iTotalAmount = Integer.valueOf(jsonObject.getString("allotted")) * Integer.valueOf(jsonObject.getString("price_per_qtl"));
                            Log.e("iTotalAmount", " : " + iTotalAmount);
                            double res = (iTotalAmount / 100.0f) * 5;
                            totalGst = iTotalAmount + res;
                            txtTotalAmount.setText(Double.toString(totalGst));
                            Log.e("totalGst", " : " + totalGst);
                        }
                    } else {                                                                        // this is use for open case for Buyer Login
                        edtPriceQtl.setText(jsonObject.getString("price_per_qtl"));
                        int iTotalAmount = Integer.valueOf(jsonObject.getString("allotted")) * Integer.valueOf(jsonObject.getString("price_per_qtl"));
                        Log.e("iTotalAmount", " : " + iTotalAmount);
                        double res = (iTotalAmount / 100.0f) * 5;
                        totalGst = iTotalAmount + res;
                        txtTotalAmount.setText(Double.toString(totalGst));
                        Log.e("totalGst", " : " + totalGst);
                    }*/
                }
                else
                {

                     //set favorite icon
                    if (jsonObject.getString("is_favorite").equalsIgnoreCase("Y"))
                    {
                        SellerListAdapterImgUnFav.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        SellerListAdapterImgUnFav.setVisibility(View.GONE);

                    }
                    //check type status = sell
                    if(typeStatus.equals("sell"))
                    {
                        required_qty.setText("Current Available Qty");
                        available_qty.setText("Booked Qty");
                        emdHideLayout.setVisibility(View.VISIBLE);
                        emd_et.setText(jsonObject.getString("emd"));
                        aquired_qty.setText("Total Acquired Quantity");

                        //due date of payment
                        due_tv.setText("Due Date of Payment");
                        due_date_of_lifting_et.setText(DTU.changeDateTimeFormatForDue(jsonObject.getString("payment_date")));

                        //Note :  in query selected date is lifting date but here alis is created i.e due_delivery_date
                        //due date of lifting
                        due_lifting_tv.setVisibility(View.VISIBLE);
                        due_lifting_et.setVisibility(View.VISIBLE);
                        due_lifting_tv.setText("Due Date of lifting");
                        due_lifting_et.setText(DTU.changeDateTimeFormatForDue(jsonObject.getString("due_delivery_date")));

                        long hours = Long.parseLong(jsonObject.getString("validity_time")) / 60; //since both are ints, you get an int
                        long minutes = Long.parseLong(jsonObject.getString("validity_time")) % 60;
                        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
                        validity_time.setText(timeLeftFormatted);

                        String bidEndTime = T.returnEndTime(jsonObject.getString("bid_end_time"));
                        start_time.setText(DTU.changeTime(jsonObject.getString("bid_start_time")));
                        end_time.setText(bidEndTime);

                        strOfferID = jsonObject.getString("offer_id");                         // this key is in buy_bid  or sell_bid table id column key.
                        strMobileNo = jsonObject.getString("mobile");
                        strUserID = jsonObject.getString("userId");          // in this key other user's id
                        strUserBy = jsonObject.getString("userby");              // in this key user Login id set


                        edtCompName.setText(jsonObject.getString("company_name"));
                        edtcategory.setText(jsonObject.getString("category"));
                        edtProdYear.setText(jsonObject.getString("production_year"));

                        edtContactNo.setText(strMobileNo);                  // add on 24-08-18
                        // edtQuantity.setText(jsonObject.getString("allotted"));
                        edtAccountName.setText(jsonObject.getString("account_name"));
                        edtAccountNo.setText(jsonObject.getString("account_no"));
                        edtBankName.setText(jsonObject.getString("bank_name"));
                        edtGSTIN.setText(jsonObject.getString("gstin"));
                        edtIFSC.setText(jsonObject.getString("ifsc_code"));


                        post_date_time_et.setText(DTU.changeDateTimeFormat(jsonObject.getString("created_date")));
                        confir_date_time_et.setText(DTU.changeTimeFormat(jsonObject.getString("date"), jsonObject.getString("time")));


                        remark_et.setText(jsonObject.getString("remark"));
                        due_pay_tv.setVisibility(View.GONE);

                        String required_qty = jsonObject.getString("available_qty");
                        String allotted = jsonObject.getString("allotted");
                        int curr_req_qty = Integer.valueOf(required_qty) - Integer.valueOf(allotted);


                        int total_acq_qty = Integer.valueOf(required_qty) - Integer.valueOf(curr_req_qty);

                        //original required qty
                        original_qty.setText(required_qty);
                        //total acquired qty
                        BookingDetailsActivityEdtAquiredQuantity.setText(""+total_acq_qty);

                        //current available qty
                        edtQuantity.setText(""+curr_req_qty);

                        //booked qty
                        BookingDetailsActivityEdtAvailableQuantity.setText(""+total_acq_qty);

                        if (jsonObject.has("type"))
                        {                                              // if type tender or open
                            if (jsonObject.getString("type").equalsIgnoreCase("Tender"))
                            {            // this is use for tender case for Seller Login
                                edtPriceQtl.setText(jsonObject.getString("tender_price"));
                                int iTotalAmount = Integer.valueOf(jsonObject.getString("allotted")) * Integer.valueOf(jsonObject.getString("tender_price"));
                                Log.e("iTotalAmount", " : " + iTotalAmount);
                                double res = (iTotalAmount / 100.0f) * 5;
                                totalGst = iTotalAmount + res;
                                txtTotalAmount.setText(Double.toString(totalGst));
                                Log.e("totalGst", " : " + totalGst);
                            }
                            else
                            {                                                                             // this is use for open case  for Seller Login
                                edtPriceQtl.setText(jsonObject.getString("price_per_qtl"));
                                int iTotalAmount = Integer.valueOf(jsonObject.getString("allotted")) * Integer.valueOf(jsonObject.getString("price_per_qtl"));
                                Log.e("iTotalAmount", " : " + iTotalAmount);
                                double res = (iTotalAmount / 100.0f) * 5;
                                totalGst = iTotalAmount + res;
                                txtTotalAmount.setText(Double.toString(totalGst));
                                Log.e("totalGst", " : " + totalGst);
                            }
                        }
                        else
                        {                                                                        // this is use for open case for Buyer Login
                            edtPriceQtl.setText(jsonObject.getString("price_per_qtl"));
                            int iTotalAmount = Integer.valueOf(jsonObject.getString("allotted")) * Integer.valueOf(jsonObject.getString("price_per_qtl"));
                            Log.e("iTotalAmount", " : " + iTotalAmount);
                            double res = (iTotalAmount / 100.0f) * 5;
                            totalGst = iTotalAmount + res;
                            txtTotalAmount.setText(Double.toString(totalGst));
                            Log.e("totalGst", " : " + totalGst);
                        }
                    }
                    ////check type status = buy
                    else
                    {
                        available_qty.setText("Received Qty : ");
                        aquired_qty.setText("Total acquired Quantity");
                        due_tv.setText("Due Date of Delivery");
                        due_date_of_lifting_et.setText(DTU.changeDateTimeFormatForDue(jsonObject.getString("due_delivery_date")));

                        long hours = Long.parseLong(jsonObject.getString("validity_time")) / 60; //since both are ints, you get an int
                        long minutes = Long.parseLong(jsonObject.getString("validity_time")) % 60;
                        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
                        validity_time.setText(timeLeftFormatted);

                        String bidEndTime = T.returnEndTime(jsonObject.getString("bid_end_time"));
                        start_time.setText(DTU.changeTime(jsonObject.getString("bid_start_time")));
                        end_time.setText(bidEndTime);

                        strOfferID = jsonObject.getString("offer_id");                         // this key is in buy_bid  or sell_bid table id column key.
                        strMobileNo = jsonObject.getString("mobile");
                        strUserID = jsonObject.getString("userId");          // in this key other user's id
                        strUserBy = jsonObject.getString("userby");              // in this key user Login id set


                        edtCompName.setText(jsonObject.getString("company_name"));
                        edtcategory.setText(jsonObject.getString("category"));
                        edtProdYear.setText(jsonObject.getString("production_year"));

                        edtContactNo.setText(strMobileNo);                  // add on 24-08-18
                        // edtQuantity.setText(jsonObject.getString("allotted"));
                        edtAccountName.setText(jsonObject.getString("account_name"));
                        edtAccountNo.setText(jsonObject.getString("account_no"));
                        edtBankName.setText(jsonObject.getString("bank_name"));
                        edtGSTIN.setText(jsonObject.getString("gstin"));
                        edtIFSC.setText(jsonObject.getString("ifsc_code"));

                        post_date_time_et.setText(DTU.changeDateTimeFormat(jsonObject.getString("created_date")));
                        confir_date_time_et.setText(DTU.changeTimeFormat(jsonObject.getString("date"), jsonObject.getString("time")));
                        remark_et.setText(jsonObject.getString("remark"));
                        due_pay_tv.setVisibility(View.GONE);

                        String required_qty = jsonObject.getString("required_qty");
                        String curr_req_qty = jsonObject.getString("curr_req_qty");
                        String allotted = jsonObject.getString("allotted");
                        int total_acq_qty = Integer.valueOf(required_qty) - Integer.valueOf(curr_req_qty);
                        original_qty.setText(required_qty);
                        //total acquired qty
                        BookingDetailsActivityEdtAquiredQuantity.setText(""+total_acq_qty);
                        edtQuantity.setText(""+curr_req_qty);

                        BookingDetailsActivityEdtAvailableQuantity.setText(jsonObject.getString("allotted"));

                        if (jsonObject.has("type")) {                                              // if type tender or open
                            if (jsonObject.getString("type").equalsIgnoreCase("Tender")) {            // this is use for tender case for Seller Login
                                edtPriceQtl.setText(jsonObject.getString("tender_price"));
                                int iTotalAmount = Integer.valueOf(jsonObject.getString("allotted")) * Integer.valueOf(jsonObject.getString("tender_price"));
                                Log.e("iTotalAmount", " : " + iTotalAmount);
                                double res = (iTotalAmount / 100.0f) * 5;
                                totalGst = iTotalAmount + res;
                                txtTotalAmount.setText(Double.toString(totalGst));
                                Log.e("totalGst", " : " + totalGst);
                            } else {                                                                             // this is use for open case  for Seller Login
                                edtPriceQtl.setText(jsonObject.getString("price_per_qtl"));
                                int iTotalAmount = Integer.valueOf(jsonObject.getString("allotted")) * Integer.valueOf(jsonObject.getString("price_per_qtl"));
                                Log.e("iTotalAmount", " : " + iTotalAmount);
                                double res = (iTotalAmount / 100.0f) * 5;
                                totalGst = iTotalAmount + res;
                                txtTotalAmount.setText(Double.toString(totalGst));
                                Log.e("totalGst", " : " + totalGst);
                            }
                        } else {                                                                        // this is use for open case for Buyer Login
                            edtPriceQtl.setText(jsonObject.getString("price_per_qtl"));
                            int iTotalAmount = Integer.valueOf(jsonObject.getString("allotted")) * Integer.valueOf(jsonObject.getString("price_per_qtl"));
                            Log.e("iTotalAmount", " : " + iTotalAmount);
                            double res = (iTotalAmount / 100.0f) * 5;
                            totalGst = iTotalAmount + res;
                            txtTotalAmount.setText(Double.toString(totalGst));
                            Log.e("totalGst", " : " + totalGst);
                        }
                    }

                }


            }
            else if (strFlag.equalsIgnoreCase("Buyer"))
            {


                //set favorite icon
                if (jsonObject.getString("is_favorite").equalsIgnoreCase("Y"))
                {
                    SellerListAdapterImgUnFav.setVisibility(View.VISIBLE);
                }
                else
                {
                    SellerListAdapterImgUnFav.setVisibility(View.GONE);

                }

                validity_time.setText(T.returnEndTime(jsonObject.getString("validity_time")));

                if(typeStatusData.equals("buy"))
                {
                    String required_qty = jsonObject.getString("required_qty");
                    String curr_req_qty = jsonObject.getString("curr_req_qty");
                    String allotted = jsonObject.getString("allotted");
                    available_qty.setText("Received qty");
                    due_tv.setText("Due Date of Delivery");



                    start_time.setText(DTU.changeTime(jsonObject.getString("bid_start_time")));
                    end_time.setText(T.returnEndTime(jsonObject.getString("bid_end_time")));
                    strOfferID = jsonObject.getString("offer_id");                // this key is in buy_bid  or sell_bid table id column key.
                    strMobileNo = jsonObject.getString("mobile");
                    strUserID = jsonObject.getString("userId");          // in this key other user's id
                    strUserBy = jsonObject.getString("userby");              // in this key user Login id set
                    edtCompName.setText(jsonObject.getString("company_name"));
                    edtcategory.setText(jsonObject.getString("category"));
                    edtProdYear.setText(jsonObject.getString("production_year"));
                    edtContactNo.setText(strMobileNo);                  // add on 24-08-18
                    edtAccountName.setText(jsonObject.getString("account_name"));
                    edtAccountNo.setText(jsonObject.getString("account_no"));
                    edtBankName.setText(jsonObject.getString("bank_name"));
                    edtGSTIN.setText(jsonObject.getString("gstin"));
                    edtIFSC.setText(jsonObject.getString("ifsc_code"));
                    post_date_time_et.setText(DTU.changeDateTimeFormat(jsonObject.getString("created_date")));
                    confir_date_time_et.setText(DTU.changeTimeFormat(jsonObject.getString("date"), jsonObject.getString("time")));
                    remark_et.setText(jsonObject.getString("remark"));
                    due_date_of_lifting_et.setText(DTU.changeDateTimeFormatForDue(jsonObject.getString("due_delivery_date")));
                    due_date_of_payment_et.setVisibility(View.GONE);
                    emdHideLayout.setVisibility(View.GONE);

                    //original required qty
                    original_qty.setText(required_qty);

                     //total acquired qty
                    int totalAQ = Integer.valueOf(required_qty) - Integer.valueOf(curr_req_qty);
                    BookingDetailsActivityEdtAquiredQuantity.setText(""+totalAQ);

                    //current req qty
                    edtQuantity.setText(curr_req_qty);

                    due_pay_tv.setVisibility(View.GONE);
                    BookingDetailsActivityEdtAvailableQuantity.setText(jsonObject.getString("allotted"));
                    if (jsonObject.has("type"))
                    {
                        if (jsonObject.getString("type").equalsIgnoreCase("Tender"))
                        {
                            edtPriceQtl.setText(jsonObject.getString("tender_price"));
                            int iTotalAmount = Integer.valueOf(jsonObject.getString("allotted")) * Integer.valueOf(jsonObject.getString("tender_price"));
                            Log.e("iTotalAmount", " : " + iTotalAmount);
                            double res = (iTotalAmount / 100.0f) * 5;
                            totalGst = iTotalAmount + res;
                            txtTotalAmount.setText(Double.toString(totalGst));
                            Log.e("totalGst", " : " + totalGst);
                        }
                        else
                        {
                            edtPriceQtl.setText(jsonObject.getString("price_per_qtl"));
                            int iTotalAmount = Integer.valueOf(jsonObject.getString("allotted")) * Integer.valueOf(jsonObject.getString("price_per_qtl"));
                            Log.e("iTotalAmount", " : " + iTotalAmount);
                            double res = (iTotalAmount / 100.0f) * 5;
                            totalGst = iTotalAmount + res;
                            txtTotalAmount.setText(Double.toString(totalGst));
                            Log.e("totalGst", " : " + totalGst);
                        }
                    }
                    else
                    {
                        edtPriceQtl.setText(jsonObject.getString("price_per_qtl"));
                        int iTotalAmount = Integer.valueOf(jsonObject.getString("allotted")) * Integer.valueOf(jsonObject.getString("price_per_qtl"));
                        Log.e("iTotalAmount", " : " + iTotalAmount);
                        double res = (iTotalAmount / 100.0f) * 5;
                        totalGst = iTotalAmount + res;
                        txtTotalAmount.setText(Double.toString(totalGst));
                        Log.e("totalGst", " : " + totalGst);
                    }

                }
                else
                {

                    due_tv.setText("Due Date of Lifting");
                    //edtQuantity.setText(jsonObject.getString("available_qty"));
                    // aquired_qty.setVisibility(View.GONE);
                    required_qty.setText("Current Available Qty");

                    String required_qty = jsonObject.getString("available_qty");
                    String curr_req_qty = jsonObject.getString("curr_req_qty");
                    String allotted = jsonObject.getString("allotted");


                    //original required qty
                     original_qty.setText(required_qty);
                    //total acquired qty
                    int totalAQ = Integer.valueOf(required_qty) - Integer.valueOf(curr_req_qty);
                    BookingDetailsActivityEdtAquiredQuantity.setText(""+totalAQ);
                    //current available qty
                    int current_avail_qty = Integer.valueOf(required_qty) - Integer.valueOf(totalAQ);
                    edtQuantity.setText(""+current_avail_qty);

                    //booked qty
                    available_qty.setText("Booked Qty");
                    BookingDetailsActivityEdtAvailableQuantity.setText(""+allotted);


                    start_time.setText(DTU.changeTime(jsonObject.getString("bid_start_time")));
                    end_time.setText(T.returnEndTime(jsonObject.getString("bid_end_time")));
                    strOfferID = jsonObject.getString("offer_id");                // this key is in buy_bid  or sell_bid table id column key.
                    strMobileNo = jsonObject.getString("mobile");
                    strUserID = jsonObject.getString("userId");          // in this key other user's id
                    strUserBy = jsonObject.getString("userby");              // in this key user Login id set
                    edtCompName.setText(jsonObject.getString("company_name"));
                    edtcategory.setText(jsonObject.getString("category"));
                    edtProdYear.setText(jsonObject.getString("production_year"));
                    edtContactNo.setText(strMobileNo);                  // add on 24-08-18
                    edtAccountName.setText(jsonObject.getString("account_name"));
                    edtAccountNo.setText(jsonObject.getString("account_no"));
                    edtBankName.setText(jsonObject.getString("bank_name"));
                    edtGSTIN.setText(jsonObject.getString("gstin"));
                    edtIFSC.setText(jsonObject.getString("ifsc_code"));
                    post_date_time_et.setText(DTU.changeDateTimeFormat(jsonObject.getString("created_date")));
                    confir_date_time_et.setText(DTU.changeTimeFormat(jsonObject.getString("date"), jsonObject.getString("time")));
                    remark_et.setText(jsonObject.getString("remark"));
                    due_date_of_lifting_et.setText(DTU.changeDateTimeFormatForDue(jsonObject.getString("due_delivery_date")));
                    due_date_of_payment_et.setVisibility(View.GONE);
                    emdHideLayout.setVisibility(View.GONE);




                    due_pay_tv.setVisibility(View.GONE);

                    if (jsonObject.has("type"))
                    {
                        if (jsonObject.getString("type").equalsIgnoreCase("Tender"))
                        {
                            edtPriceQtl.setText(jsonObject.getString("tender_price"));
                            int iTotalAmount = Integer.valueOf(jsonObject.getString("allotted")) * Integer.valueOf(jsonObject.getString("tender_price"));
                            Log.e("iTotalAmount", " : " + iTotalAmount);
                            double res = (iTotalAmount / 100.0f) * 5;
                            totalGst = iTotalAmount + res;
                            txtTotalAmount.setText(Double.toString(totalGst));
                            Log.e("totalGst", " : " + totalGst);
                        }
                        else
                        {
                            edtPriceQtl.setText(jsonObject.getString("price_per_qtl"));
                            int iTotalAmount = Integer.valueOf(jsonObject.getString("allotted")) * Integer.valueOf(jsonObject.getString("price_per_qtl"));
                            Log.e("iTotalAmount", " : " + iTotalAmount);
                            double res = (iTotalAmount / 100.0f) * 5;
                            totalGst = iTotalAmount + res;
                            txtTotalAmount.setText(Double.toString(totalGst));
                            Log.e("totalGst", " : " + totalGst);
                        }
                    }
                    else
                    {
                        edtPriceQtl.setText(jsonObject.getString("price_per_qtl"));
                        int iTotalAmount = Integer.valueOf(jsonObject.getString("allotted")) * Integer.valueOf(jsonObject.getString("price_per_qtl"));
                        Log.e("iTotalAmount", " : " + iTotalAmount);
                        double res = (iTotalAmount / 100.0f) * 5;
                        totalGst = iTotalAmount + res;
                        txtTotalAmount.setText(Double.toString(totalGst));
                        Log.e("totalGst", " : " + totalGst);
                    }
                }

            }
        } catch (JSONException e) {

            T.e("Exception : "+e);
            e.printStackTrace();
        }

    }


    void setData(String date, String time, TextView textView) {


        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
        //Date/time pattern of desired output date
        DateFormat outputformat1 = new SimpleDateFormat("dd-MM-yyyy");
        Date date1 = null;
        String output1 = null;
        try {
            //Conversion of input String to date
            date1 = df1.parse(date);
            //old date format to new date format
            output1 = outputformat1.format(date1);


            DateFormat df2 = new SimpleDateFormat("HH:mm:ss");
            //Date/time pattern of desired output date
            DateFormat outputformat2 = new SimpleDateFormat("HH:mm");
            Date date2 = null;
            String output2 = null;

            //Conversion of input String to date
            date2 = df2.parse(time);
            //old date format to new date format
            output2 = outputformat2.format(date2);


            textView.setText(output1 + " " + output2);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }


    }


    private void review() {
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait while data fetch from server...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/addRate";      //for server
        Log.e("review", " ....." + url);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Log.e("review_Data", " RESPONSE " + response);
                        pDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("message").equalsIgnoreCase("success")) {
                                ad.dismiss();
                                Toast.makeText(context, "Review save successfully ", Toast.LENGTH_SHORT).show();
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
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();


                params.put("bid_id", strOfferID);
                params.put("rating", String.valueOf(ratingBar.getRating()));
                params.put("comment", edtReview.getText().toString().trim());
                params.put("userby", ACU.MySP.getFromSP(context, ACU.MySP.ID, ""));
                params.put("user_id", strUserID);

                Log.e("REVIEW_PARMAS", " ..... " + params.toString());

                return params;
            }
        };
        queue.add(stringRequest);
    }

    private static final int PERMISSION_REQUEST_CODE = 200;

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE);

        return result == PackageManager.PERMISSION_GRANTED;

    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CALL_PHONE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(CALL_PHONE)) {
                            showMessageOKCancel("You need to allow access to these permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[]{CALL_PHONE}, PERMISSION_REQUEST_CODE);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", okListener)
                .create()
                .show();
    }

}
