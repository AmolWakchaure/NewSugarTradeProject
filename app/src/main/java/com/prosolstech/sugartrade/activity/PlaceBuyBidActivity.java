package com.prosolstech.sugartrade.activity;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.prosolstech.sugartrade.DialogListner;
import com.prosolstech.sugartrade.MandatoryFieldsDialog;
import com.prosolstech.sugartrade.R;
import com.prosolstech.sugartrade.classes.T;
import com.prosolstech.sugartrade.database.DataBaseConstants;
import com.prosolstech.sugartrade.database.DataBaseHelper;
import com.prosolstech.sugartrade.model.CategoryModel;
import com.prosolstech.sugartrade.model.SeasonModel;
import com.prosolstech.sugartrade.util.ACU;
import com.prosolstech.sugartrade.util.DTU;
import com.prosolstech.sugartrade.util.VU;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PlaceBuyBidActivity extends AppCompatActivity implements View.OnClickListener {
    Context context;
    EditText edtValidityTime, edtStartTime, edtPriQty, edtDeliveryDate, edtRemark;
    Spinner spnCategory, spnProdYear;
    Button btnPlaceBid;
    ArrayList<String> listCategory;
    ArrayList<String> listSeason;
    ArrayList<String> listSeasonId;
    ArrayList<String> listCatId;
    String strFlag = "", strCatID = "", strUserId = "", strSendTo = "";
    DataBaseHelper db;
    RadioButton rbSendToFav, rbSendToAll;
    JSONObject jsonObject;
    double totalGst;
    TextView txtGstValue;
    ImageView imgSeasonRefresh, imgCategoryRefresh;
    TextView original_required_tv, totoal_required_tv;
    EditText original_required_et,totalAquiredQtyEt,currentRequiredQtyEt;
    LinearLayout hideLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_buy_bid);
        context = PlaceBuyBidActivity.this;
        db = new DataBaseHelper(context);
        setToolBar();
        intitializeUI();
        getIntentData();
    }

    private void intitializeUI() {
        start_time_error = findViewById(R.id.start_time_error);

        original_required_tv = findViewById(R.id.original_required_tv);
        totoal_required_tv = findViewById(R.id.totoal_required_tv);
        original_required_et = findViewById(R.id.original_required_et);


        imgSeasonRefresh = (ImageView) findViewById(R.id.PlaceBuyBidActivityImageSeasonRefresh);
        imgCategoryRefresh = (ImageView) findViewById(R.id.PlaceBuyBidActivityImageCategoryRefresh);
        hideLayout = (LinearLayout) findViewById(R.id.hideLayout);

        txtGstValue = (TextView) findViewById(R.id.PlaceBuyBidActivityTxtGstValue);

        edtValidityTime = (EditText) findViewById(R.id.PlaceBuyBidActivityValidityTime);
        edtStartTime = (EditText) findViewById(R.id.PlaceBuyBidActivityBidStartTime);
        totalAquiredQtyEt = (EditText) findViewById(R.id.totalAquiredQtyEt);
        currentRequiredQtyEt = (EditText) findViewById(R.id.currentRequiredQtyEt);


        spnProdYear = (Spinner) findViewById(R.id.PlaceBuyBidActivitySpnProductionYear);
        edtPriQty = (EditText) findViewById(R.id.PlaceBuyBidActivityPriceQuantity);
      //  edtRequiredQty = (EditText) findViewById(R.id.PlaceBuyBidActivityRequiredQuantity);
        edtDeliveryDate = (EditText) findViewById(R.id.PlaceBuyBidActivityDeliveryDate);
        edtRemark = (EditText) findViewById(R.id.PlaceBuyBidActivityRemark);

        spnCategory = (Spinner) findViewById(R.id.PlaceBuyBidActivitySpnCategory);

        rbSendToFav = (RadioButton) findViewById(R.id.PlaceBuyBidActivityRadioButtonSendToFav);
        rbSendToAll = (RadioButton) findViewById(R.id.PlaceBuyBidActivityRadioButtonSendToAll);


        btnPlaceBid = (Button) findViewById(R.id.PlaceBuyBidActivityButtonPlaceBid);


        btnPlaceBid.setOnClickListener(this);
        edtValidityTime.setOnClickListener(this);
        edtStartTime.setOnClickListener(this);
        edtDeliveryDate.setOnClickListener(this);
        imgSeasonRefresh.setOnClickListener(this);
        imgCategoryRefresh.setOnClickListener(this);

        edtPriQty.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!edtPriQty.getText().toString().equals("")) {
                    double amount = Double.parseDouble(edtPriQty.getText().toString());
                    double res = (amount / 100.0f) * 5;
                    totalGst = amount + res;
                    txtGstValue.setVisibility(View.VISIBLE);
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
        seasonData();
        categoryData();

    }

    private TextView start_time_error;


    DialogListner dialogListner = new DialogListner() {
        @Override
        public void click() {

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.PlaceBuyBidActivityButtonPlaceBid:
                if (VU.isConnectingToInternet(context)) {
                    if (Validate()) {
                        AddBuyBid();
                    } else {


                        if (TextUtils.isEmpty(original_required_et.getText().toString().trim())
                                || TextUtils.isEmpty(edtPriQty.getText().toString().trim())
                                || TextUtils.isEmpty(edtDeliveryDate.getText().toString().trim())
                                || TextUtils.isEmpty(edtStartTime.getText().toString().trim())
                                || TextUtils.isEmpty(edtValidityTime.getText().toString().trim())) {


                            original_required_et.setError(null);
                            edtPriQty.setError(null);
                            edtDeliveryDate.setError(null);
                            edtStartTime.setError(null);
                            edtValidityTime.setError(null);

                            MandatoryFieldsDialog mandatoryFieldsDialog = new MandatoryFieldsDialog(PlaceBuyBidActivity.this, dialogListner);
                            mandatoryFieldsDialog.show();


                        }


                    }


                }
                break;
            case R.id.PlaceBuyBidActivityValidityTime:
                showTime24HourPickerDialogWithMinute(context, DTU.getCurrentDateTimeStamp(DTU.HM), edtValidityTime);
                edtValidityTime.setError(null);
                break;
            case R.id.PlaceBuyBidActivityBidStartTime:
//                DTU.showTime24HourPickerDialog(context, DTU.getCurrentDateTimeStamp(DTU.HM), edtStartTime);
                showTime(context, DTU.getCurrentDateTimeStamp(DTU.HM), edtStartTime);
                edtStartTime.setError(null);

                break;
            case R.id.PlaceBuyBidActivityDeliveryDate:
                DTU.showDatePickerDialog(context, DTU.FLAG_OLD_AND_NEW, edtDeliveryDate);
                edtDeliveryDate.setError(null);

                break;
            case R.id.PlaceBuyBidActivityImageSeasonRefresh:
                if (VU.isConnectingToInternet(context)) {
                    fetchSeasonYear();
                }
            case R.id.PlaceBuyBidActivityImageCategoryRefresh:
                if (VU.isConnectingToInternet(context)) {
                    fetchCategoryName();
                }
                break;
        }
    }


    private boolean checkStartValidtySum() {
        boolean isValid = true;


        long min1 = toMins(edtValidityTime.getText().toString().trim()) + toMins(edtStartTime.getText().toString().trim());


        long hours = min1 / 60; //since both are ints, you get an int
        long minutes = min1 % 60;


        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);

        SimpleDateFormat parser = new SimpleDateFormat("HH:mm");


        Log.e("checkStartValidtySum: ", timeLeftFormatted);

        try {
            Date currentDate = parser.parse(hours + ":" + minutes);
            Date startTime = parser.parse("23" + ":" + "59");


            if (currentDate.after(startTime)) {
                isValid = false;
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }

        return isValid;

    }


    private boolean checkStartTime() {
        boolean isValid = true;


        if (edtStartTime.getText().toString().equalsIgnoreCase("23:59")) {
            isValid = false;
        }
        return isValid;
    }


    private boolean checkTime(String time) {
        boolean isValid = true;
        Calendar calendar = Calendar.getInstance();
        int hours1 = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes1 = calendar.get(Calendar.MINUTE);

        String ho = "";
        String mi = "";
        try {
            String[] p = time.split(":");
            ho = p[0];
            mi = p[1];
        } catch (Exception e) {

        }
        SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
        try {
            Date currentDate = parser.parse(hours1 + ":" + minutes1);
            Date startTime = parser.parse(ho + ":" + mi);


            if (startTime.before(currentDate)) {
                isValid = false;
            }

        } catch (ParseException e) {
            // Invalid date was entered
        }


        return isValid;
    }


    public static void showTime(final Context appContext, String inputTime, final EditText eStartTime) {

        final Calendar c = Calendar.getInstance();
        String[] spiltedTime = inputTime.split(":");

        currentHour = Integer.parseInt(spiltedTime[0]);
        currentMinute = Integer.parseInt(spiltedTime[1]);
        currentSeconds = 00;

        TimePickerDialog timePickerFragment = new TimePickerDialog(appContext,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Calendar datetime = Calendar.getInstance();
                        Calendar c = Calendar.getInstance();
                        datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        datetime.set(Calendar.MINUTE, minute);
                        if (datetime.getTimeInMillis() >= c.getTimeInMillis()) {
                            //it's after current
                            int hour = hourOfDay % 24;
                            eStartTime.setText(String.format("%02d:%02d", hour == 0 ? 12 : hour,
                                    minute));
                        } else {
                            //it's before current'
                            eStartTime.setText("");
                            Toast.makeText(appContext, "Invalid Time", Toast.LENGTH_LONG).show();
                        }
                    }
                }, currentHour, currentMinute, true);
        timePickerFragment.show();
    }


    public static int roundTo5(double n) {
        return (int) Math.round(n / 5) * 5;
    }

    public static int currentHour, currentMinute, currentSeconds;
    private int timesVal;

    public String showTime24HourPickerDialogWithMinute(final Context appContext, String inputTime,
                                                       final EditText eStartTime) {

        final Calendar c = Calendar.getInstance();
        String[] spiltedTime = inputTime.split(":");

//        currentHour = Integer.parseInt(spiltedTime[0]);
//        currentMinute = Integer.parseInt(spiltedTime[1]);


        currentHour = 0;
        currentMinute = 0;
        currentSeconds = 00;
        TimePickerDialog tpd = new TimePickerDialog(appContext,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minutes) {
                        int hour = hourOfDay;
                        int minute = roundTo5(minutes);
                        if (minute == 60) {
                            minute = 0;
                            hour = hour + 1;
                        }
                        String time = "" + hourOfDay + "" + minutes + "00";
                        if (minute < 10) {
                            int times = toMins(hour + ":" + minute);
                            String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);

                            eStartTime.setText(timeLeftFormatted);
                            timesVal = times;
                            Log.e("onTimeSet: ", timesVal + "");
                        } else {
                            int times = toMins(hour + ":" + minute);
                            String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
                            eStartTime.setText(timeLeftFormatted);
                            timesVal = times;
                            Log.e("onTimeSet: ", timesVal + "");
                        }


                    }
                }, currentHour, currentMinute, true);
        tpd.show();

        return "";
    }


    private static int toMins(String s) {
        String[] hourMin = s.split(":");
        int hour = Integer.parseInt(hourMin[0]);
        int mins = Integer.parseInt(hourMin[1]);
        int hoursInMins = hour * 60;
        return hoursInMins + mins;
    }


    public void OnSendBid(View v) {
        switch (v.getId()) {
            case R.id.PlaceBuyBidActivityRadioButtonSendToFav:
                strSendTo = "send_to_favorites";
                break;
            case R.id.PlaceBuyBidActivityRadioButtonSendToAll:
                strSendTo = "send_to_all";
                break;
        }
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
                finish();
                onBackPressed();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isValidateZero(EditText editText) {

        if (leadingZerosCount(editText.getText().toString().trim()) > 0) {
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

    public boolean Validate() {

        if (VU.isEmpty(original_required_et) || !isValidateZero(original_required_et)) {
            original_required_et.setError("Please Enter Valid Original Required Qty");
            original_required_et.requestFocus();
            return false;
        } else if (VU.isEmpty(edtPriQty)) {
            edtPriQty.setError("Please Enter Price Per Qtl");
            edtPriQty.requestFocus();
            return false;
        } else if (VU.isEmpty(edtDeliveryDate)) {
            edtDeliveryDate.setError("Please Enter Delivery Date");
            edtDeliveryDate.requestFocus();
            return false;
        } else if (VU.isEmpty(edtStartTime)) {
            edtStartTime.setError("Please Enter Start Time");
            edtStartTime.requestFocus();
            return false;
        } else if (!checkTime(edtStartTime.getText().toString().trim())) {
            edtStartTime.setError("StartTime Should be greater than Current Time");
            edtStartTime.requestFocus();
            return false;
        } else if (!checkStartTime()) {

            edtStartTime.setError("Offer validity time must be before 12am");
            edtStartTime.requestFocus();
            return false;

        } else if (TextUtils.isEmpty(edtValidityTime.getText().toString().trim()) || edtValidityTime.getText().toString().trim().equalsIgnoreCase("")) {
            edtValidityTime.setError("Please Enter Validity Time");
            edtValidityTime.requestFocus();
            return false;

        } else if (edtValidityTime.getText().toString().equalsIgnoreCase("00:00")) {
            edtValidityTime.setError("Offer Validity Time cannot be 00:00");
            edtValidityTime.requestFocus();
            return false;

        } else if (!checkStartValidtySum()) {

            edtStartTime.setError("Offer validity time must be before 12am");
            edtValidityTime.setError("Offer validity time must be before 12am");
            edtStartTime.requestFocus();
            edtValidityTime.requestFocus();
            return false;


        } else if (strSendTo.equalsIgnoreCase("")) {
            Toast.makeText(context, "Please Select Send to all & Send to favorite", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    public void AddBuyBid() {
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait while buy offer add...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url;
        if (!strFlag.equalsIgnoreCase("update")) {                                  // for new record insert
            url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/addBuyBid";
        } else {                                                                                     // for update record
            url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/updateBuyBid";
        }
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        pDialog.dismiss();
                        try {
                            Log.e("AddBuyBid_RESPONSE", " : " + response);
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("message").equalsIgnoreCase("success")) {
                                Toast.makeText(context, "Data Saved Successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(context, BuySellDashBoardActivity.class));
                                finish();
                            } else {
                                Toast.makeText(context, "Try Again", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("No Response", " GET ");
                pDialog.dismiss();
                Toast.makeText(context, "Try Again", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                if (!strFlag.equalsIgnoreCase("update")) {                             // for new record insert
                    params.put("created_date", DTU.getCurrentDateTimeStamp(DTU.YMD_HMS));
                } else {                                                                              // for update record
                    params.put("id", strUserId);
                    params.put("modified_date", DTU.getCurrentDateTimeStamp(DTU.YMD_HMS));
                }

                params.put("userby", ACU.MySP.getFromSP(context, ACU.MySP.ID, ""));
                params.put("validity_time", timesVal + "");
                params.put("bid_start_time", edtStartTime.getText().toString().trim());


                long v = toMins(edtValidityTime.getText().toString());
                long s = toMins(edtStartTime.getText().toString().trim());

                long e = v + s;

                Log.e("getParams:a ", e + "");
                params.put("bid_end_time", e + "");

                params.put("category", strCatID);
                params.put("production_year", spnProdYear.getSelectedItem().toString().trim());
                params.put("price_per_qtl", edtPriQty.getText().toString().trim());
                params.put("required_qty", original_required_et.getText().toString().trim());
                params.put("due_delivery_date", VU.getddmmyyDate(edtDeliveryDate.getText().toString().trim()));
                params.put("remark", edtRemark.getText().toString().trim());
                params.put("transportation", "");                           // remove this field after done this page
                params.put("transport_charge", "");                         // remove this field after done this page
                params.put("send_to", strSendTo);

                Log.e("BUY_BID_PARAMS", " " + params.toString());
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);
    }

    private void getIntentData() {        // this data comes from MyBuyOfferAdapter class
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            try {
                strFlag = extras.getString("flag");

                Log.e("strFlag", " " + strFlag);
                Log.e("Buy_jsonObject", " " + jsonObject);

                //MySellOffer(jsonObjectIntent.getString("id"));
                setSupportActionBar(toolbar);

                if(strFlag.equals("insert"))
                {
                    hideLayout.setVisibility(View.GONE);
                }
                else
                {
                    jsonObject = new JSONObject(extras.getString("data"));
                    toolbar.setTitle("Buy Post Id:" + jsonObject.getString("id"));
                    setData();
                }

            } catch (Exception e) {
                T.e("getIntentData() EXCEPTION : "+e);
                e.printStackTrace();
            }
        }
    }

    public void setData() {
        try {
            btnPlaceBid.setVisibility(View.GONE);
            btnPlaceBid.setText("Update Place Buy Offer");

            strUserId = jsonObject.getString("id");

//            edtValidityTime.setText(jsonObject.getString("validity_time"));


            long hours = Long.parseLong(jsonObject.getString("validity_time")) / 60; //since both are ints, you get an int
            long minutes = Long.parseLong(jsonObject.getString("validity_time")) % 60;

            String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
            edtValidityTime.setText(timeLeftFormatted);
//            edtValidityTime.setText(hours + ":" + minutes);

            edtStartTime.setText(DTU.changeTime(jsonObject.getString("bid_start_time")));
            // edtProdYear.setText(jsonObject.getString("production_year"));
            edtPriQty.setText(jsonObject.getString("price_per_qtl"));
            //edtRequiredQty.setText(jsonObject.getString("required_qty"));
            original_required_et.setText(jsonObject.getString("required_qty"));
            totalAquiredQtyEt.setText(jsonObject.getString("acquired_qty"));
            currentRequiredQtyEt.setText(jsonObject.getString("curr_req_qty"));
            edtDeliveryDate.setText(VU.getddmmyyDate(jsonObject.getString("due_delivery_date")));
            edtRemark.setText(jsonObject.getString("remark"));

            txtGstValue.setVisibility(View.VISIBLE);
            // txtGstValue.setText("WITH GST (" + jsonObject.getString("price_per_qtl") + ")");

            if (!jsonObject.getString("category").equals("")) {
                int index = listCategory.indexOf("" + jsonObject.getString("category"));
                spnCategory.setSelection(index);
                Log.e("spnCategory", " " + index + "  " + jsonObject.getString("category"));
            }

            if (!jsonObject.getString("production_year").equals("")) {
                int index = listSeason.indexOf("" + jsonObject.getString("production_year"));
                spnProdYear.setSelection(index);
                Log.e("spnProdYear", " " + index + "  " + jsonObject.getString("production_year"));
            }

            if (!jsonObject.getString("send_to").equalsIgnoreCase("send_to_all")) {
                rbSendToFav.setChecked(true);
                strSendTo = "send_to_favorites";
            } else {
                rbSendToAll.setChecked(true);
                strSendTo = "send_to_all";
            }


            original_required_tv.setVisibility(View.VISIBLE);
           // totoal_required_tv.setVisibility(View.VISIBLE);
            original_required_et.setVisibility(View.VISIBLE);


        } catch (JSONException e) {
            T.e("setData() EXCEPTION : "+e);
            e.printStackTrace();

        }
    }

    public void fetchSeasonYear() {
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/getAllSeasons";      //for server


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            pDialog.dismiss();
                            Log.e("fetchSeasonYear", " " + response);
                            setSeasonYear(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Log.e("No Response", " GET ");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("farmer_id", ACU.MySP.getFromSP(context, ACU.MySP.ID, ""));
                return params;

            }
        };
        queue.add(stringRequest);
    }

    private void setSeasonYear(String result) {

        JSONArray jsonArray;
        SeasonModel seasonModel = new SeasonModel();
        try {
            jsonArray = new JSONArray(result);
            boolean tab = DataBaseHelper.SeasonDB.deleteSeasonName(DataBaseConstants.TableNames.TBL_SEASON);
            Log.e("COUNT", " " + tab);
            if (tab == true) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    seasonModel.setSeason_id(jsonObject.getString("id"));
                    seasonModel.setSeason_year(jsonObject.getString("season"));
                    DataBaseHelper.SeasonDB.SeasonNameInsert(seasonModel);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        seasonData();
    }

    public void categoryData() {
        listCategory = new ArrayList<>();
        listCatId = new ArrayList<>();

        Cursor cur = DataBaseHelper.CategoryDB.getCategoryName();

        try {
            while (cur.moveToNext()) {
                listCategory.add(cur.getString(cur.getColumnIndex(DataBaseConstants.CategoryName.CATEGORY)));
                listCatId.add(cur.getString(cur.getColumnIndex(DataBaseConstants.CategoryName.CATEGORY_ID)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayAdapter<String> adpCategory;
        adpCategory = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_item, listCategory);
        spnCategory.setAdapter(adpCategory);

        spnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strCatID = listCatId.get(position);
                Log.e("strCatID", " " + strCatID);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    public void seasonData() {


        listSeason = new ArrayList<>();
        listSeasonId = new ArrayList<>();

        Cursor curSeason = DataBaseHelper.SeasonDB.getSeasonName();

        try {
            while (curSeason.moveToNext()) {
                listSeason.add(curSeason.getString(curSeason.getColumnIndex(DataBaseConstants.SeasonName.SEASON_YEAR)));
                listSeasonId.add(curSeason.getString(curSeason.getColumnIndex(DataBaseConstants.SeasonName.SEASON_ID)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        ArrayAdapter<String> adpSeason;


        int year = Calendar.getInstance().get(Calendar.YEAR);
        Log.e("seasonData: ", year + "");


        ArrayList<String> arrayList = new ArrayList<>();

        for (int i = 0; i < listSeason.size(); i++) {

            if (listSeason.get(i).contains(String.valueOf(year))) {
                arrayList.add(0, listSeason.get(i));
            } else {
                arrayList.add(listSeason.get(i));
            }

        }


        adpSeason = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_item, arrayList);
        spnProdYear.setAdapter(adpSeason);
    }

    public void fetchCategoryName() {
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/categories";      //for server


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            pDialog.dismiss();
                            Log.e("Category_NAME", " " + response);
                            setCategoryName(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Log.e("No Response", " GET ");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("farmer_id", ACU.MySP.getFromSP(context, ACU.MySP.ID, ""));
                return params;

            }
        };
        queue.add(stringRequest);
    }

    private void setCategoryName(String result) {

        JSONArray jsonArray;
        CategoryModel categoryModel = new CategoryModel();
        try {
            jsonArray = new JSONArray(result);
            boolean tab = DataBaseHelper.CategoryDB.deleteCategoryName(DataBaseConstants.TableNames.TBL_CATEGORY);
            Log.e("COUNT", " " + tab);
            if (tab == true) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    categoryModel.setCategory_id(jsonObject.getString("id"));
                    categoryModel.setCategory(jsonObject.getString("category"));
                    categoryModel.setProduct_name(jsonObject.getString("product_name"));
                    DataBaseHelper.CategoryDB.CategoryNameInsert(categoryModel);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        categoryData();
    }


    /*private void MySellOffer(final String offerId) {

        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait while data fetch from server...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/getOfferDetailsByOfferId";      //for server

        Log.e("URL_MySellOffer", " : " + url);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Log.e("getOfferDetailsBy", " RESPONSE " + response);


                        JSONArray array;

                        try {
                            array = new JSONArray(response);

                            if (array != null && array.length() > 0) {

                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject object = array.getJSONObject(i);
                                    setData();

                                }


                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        pDialog.dismiss();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("No Response", "FOUND");
                pDialog.dismiss();
                Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("role", ACU.MySP.getFromSP(context, ACU.MySP.ROLE, ""));
                params.put("id", offerId);
                return params;
            }
        };
        queue.add(stringRequest);
    }*/

}
