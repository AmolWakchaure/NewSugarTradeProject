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
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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

public class PlaceSellBidActivity extends AppCompatActivity implements View.OnClickListener {
    /*
    Seller Login - My Post - Create Sell Offer on Fab Button tap
    1. Type - Tender or Open
    2. Sell Quantity
    3. Grade/Category
    4. Season Year
    5. Price/qtl (If type is Open)
    6. Due Date of Payment
    7. Due Date of Lifting
    8. EMD/Qtl
    9. Offer Start Time
    10. Offer Validity time
    11. Remark
    12. Send to favourites/all radio button
    13. 'Place Sell Offer' button
     */
    Context context;
    EditText  edtProdYear, edtPriQty, edtAvailableQty, edtRemark, edtEMD;
    Spinner spnCategory, spnProdYear;
    Button btnPlaceBid,edtPaymentDate,edtLiftingDate,selectStart_time_btn,select_val_time_btn;
    EditText edtValidityTime,edtStartTime;
    String strFlag = "", strType = "", strCatID = "", strUserId = "", strSendTo = "";
    ArrayList<String> listCategory;
    ArrayList<String> listCatId;
    ArrayList<String> listSeason;
    ArrayList<String> listSeasonId;
    JSONObject jsonObject;
    RadioGroup rgType;
    RadioButton rbTendor, rbOpen, rbSendToFav, rbSendToAll;
    LinearLayout llPerQtl,hideLayout_li,hideLayout_emd;
    TextView txtGstValue,total_aquired_qty;
    double totalGst;
    ImageView imgSeasonRefresh, imgCategoryRefresh;
    private TextView start_time_error, avail_id_tv, original_tv, SellDetailsActivityTotalQtyValue;
    private EditText original_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_sell_bid);
        context = PlaceSellBidActivity.this;
        setToolBar();
        intitializeUI();
        getIntentData();
    }

    private void intitializeUI() {
        imgSeasonRefresh = (ImageView) findViewById(R.id.PlaceSellBidActivityImageSeasonRefresh);
        imgCategoryRefresh = (ImageView) findViewById(R.id.PlaceSellBidActivityImageCategoryRefresh);

        llPerQtl = (LinearLayout) findViewById(R.id.PlaceSellBidActivityLinearLayoutPerQtl);
        hideLayout_li = (LinearLayout) findViewById(R.id.hideLayout_li);
        hideLayout_emd = (LinearLayout) findViewById(R.id.hideLayout_emd);
        txtGstValue = (TextView) findViewById(R.id.PlaceSellBidActivityTxtGstValue);
        avail_id_tv = (TextView) findViewById(R.id.avail_id_tv);
        total_aquired_qty = (TextView) findViewById(R.id.total_aquired_qty);


        original_tv = (TextView) findViewById(R.id.original_tv);
        SellDetailsActivityTotalQtyValue = (TextView) findViewById(R.id.SellDetailsActivityTotalQtyValue);
        original_et = findViewById(R.id.original_et);


        edtValidityTime = (EditText) findViewById(R.id.PlaceSellBidActivityValidityTime);
        edtStartTime = (EditText) findViewById(R.id.PlaceSellBidActivityBidStartTime);
        spnProdYear = (Spinner) findViewById(R.id.PlaceSellBidActivitySpnProductionYear);


        edtPriQty = (EditText) findViewById(R.id.PlaceSellBidActivityPriceQuantity);
        edtAvailableQty = (EditText) findViewById(R.id.PlaceSellBidActivityAvailableQuantity);

        edtPaymentDate = (Button) findViewById(R.id.PlaceSellBidActivityPaymentDate);         // add on 24-08-18

        selectStart_time_btn= (Button) findViewById(R.id.selectStart_time_btn);
                select_val_time_btn= (Button) findViewById(R.id.select_val_time_btn);

        edtLiftingDate = (Button) findViewById(R.id.PlaceSellBidActivityLiftingDate);
        edtRemark = (EditText) findViewById(R.id.PlaceSellBidActivityRemark);
        edtEMD = (EditText) findViewById(R.id.PlaceSellBidActivityEMD);

        rgType = (RadioGroup) findViewById(R.id.PlaceSellBidActivityRadioGroupType);
        rbTendor = (RadioButton) findViewById(R.id.PlaceSellBidActivityRadioButtonTendor);
        rbOpen = (RadioButton) findViewById(R.id.PlaceSellBidActivityRadioButtonOpen);


        rbSendToFav = (RadioButton) findViewById(R.id.PlaceSellBidActivityRadioButtonSendToFav);
        rbSendToAll = (RadioButton) findViewById(R.id.PlaceSellBidActivityRadioButtonSendToAll);

        spnCategory = (Spinner) findViewById(R.id.PlaceSellBidActivitySpnCategory);


        btnPlaceBid = (Button) findViewById(R.id.PlaceSellBidActivityButtonPlaceBid);
        start_time_error = findViewById(R.id.start_time_error);


        btnPlaceBid.setOnClickListener(this);
        edtValidityTime.setOnClickListener(this);
        edtStartTime.setOnClickListener(this);
        edtLiftingDate.setOnClickListener(this);
        edtPaymentDate.setOnClickListener(this);
        imgSeasonRefresh.setOnClickListener(this);
        imgCategoryRefresh.setOnClickListener(this);
        selectStart_time_btn.setOnClickListener(this);
        select_val_time_btn.setOnClickListener(this);

        edtPriQty.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!edtPriQty.getText().toString().equals("")) {
                    double amount = Double.parseDouble(edtPriQty.getText().toString());
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

        seasonData();
        categoryData();
    }


    DialogListner dialogListner = new DialogListner() {
        @Override
        public void click() {

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.PlaceSellBidActivityButtonPlaceBid:
                if (VU.isConnectingToInternet(context)) {
                    if (testVal()) {
//                        if (isValidateZero(edtAvailableQty)) {

                       // T.t("Success");
                            AddSellBid();
                        }

//                        else {
//                            Toast.makeText(context, "Available Cannot be set to Zero", Toast.LENGTH_SHORT).show();
//                        }

                    else {


                        if (strType.equalsIgnoreCase("Tender")) {

                            if (TextUtils.isEmpty(edtAvailableQty.getText().toString().trim())
                                    || TextUtils.isEmpty(edtPaymentDate.getText().toString().trim())
                                    || TextUtils.isEmpty(edtLiftingDate.getText().toString().trim())
                                    || TextUtils.isEmpty(edtEMD.getText().toString().trim())
                                    || TextUtils.isEmpty(edtValidityTime.getText().toString().trim())) {


                                edtAvailableQty.setError(null);
                                edtPriQty.setError(null);
                                edtPaymentDate.setError(null);
                                edtStartTime.setError(null);
                                edtValidityTime.setError(null);
                                edtEMD.setError(null);


                                MandatoryFieldsDialog mandatoryFieldsDialog = new MandatoryFieldsDialog(PlaceSellBidActivity.this, dialogListner);
                                mandatoryFieldsDialog.show();


                            }


                        } else if (strType.equalsIgnoreCase("Open")) {


                            if (TextUtils.isEmpty(edtAvailableQty.getText().toString().trim())
                                    || TextUtils.isEmpty(edtPaymentDate.getText().toString().trim())
                                    || TextUtils.isEmpty(edtLiftingDate.getText().toString().trim())
                                    || TextUtils.isEmpty(edtEMD.getText().toString().trim())
                                    || TextUtils.isEmpty(edtPriQty.getText().toString().trim())
                                    || TextUtils.isEmpty(edtValidityTime.getText().toString().trim())) {


                                edtAvailableQty.setError(null);
                                edtPriQty.setError(null);
                                edtPaymentDate.setError(null);
                                edtStartTime.setError(null);
                                edtValidityTime.setError(null);
                                edtEMD.setError(null);


                                MandatoryFieldsDialog mandatoryFieldsDialog = new MandatoryFieldsDialog(PlaceSellBidActivity.this, dialogListner);
                                mandatoryFieldsDialog.show();


                            }


                        }
                    }
                }
                break;

            case R.id.select_val_time_btn:
                showTime24HourPickerDialogWithMinute(context, DTU.getCurrentDateTimeStamp(DTU.HM), edtValidityTime);
                edtValidityTime.setError(null);
                edtStartTime.setError(null);
                break;

            case R.id.selectStart_time_btn:
//                DTU.showTime24HourPickerDialog(context, DTU.getCurrentDateTimeStamp(DTU.HM), edtStartTime);

                showTime(context, DTU.getCurrentDateTimeStamp(DTU.HM), edtStartTime);
                edtStartTime.setError(null);
                break;

            case R.id.PlaceSellBidActivityLiftingDate:
                DTU.showDatePickerDialog(context, DTU.FLAG_OLD_AND_NEW, edtLiftingDate);
                edtLiftingDate.setError(null);
                break;

            case R.id.PlaceSellBidActivityPaymentDate:
                DTU.showDatePickerDialog(context, DTU.FLAG_OLD_AND_NEW, edtPaymentDate);
                edtPaymentDate.setError(null);


                break;

            case R.id.PlaceSellBidActivityImageSeasonRefresh:
                if (VU.isConnectingToInternet(context)) {
                    fetchSeasonYear();
                }
                break;

            case R.id.PlaceSellBidActivityImageCategoryRefresh:
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
                            eStartTime.setError(null);
                            eStartTime.requestFocus();
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


    public boolean testVal() {
        boolean isValid = true;
        if (!strType.equalsIgnoreCase("")) {
            if (strType.equalsIgnoreCase("Tender")) {
                if (TextUtils.isEmpty(edtAvailableQty.getText().toString().trim()) || edtAvailableQty.getText().toString().equalsIgnoreCase("") || !isValidateZero(edtAvailableQty)) {
                    edtAvailableQty.setError("Please Enter Valid Available Qty");
                    edtAvailableQty.requestFocus();
                    isValid = false;
                } else if (TextUtils.isEmpty(edtPaymentDate.getText().toString().trim()) || edtPaymentDate.getText().toString().trim().equalsIgnoreCase("")) {
                    edtPaymentDate.setError("Please Enter Payment Date");
                    edtPaymentDate.requestFocus();
                    isValid = false;

                } else if (TextUtils.isEmpty(edtLiftingDate.getText().toString().trim()) || edtLiftingDate.getText().toString().equalsIgnoreCase("")) {
                    edtLiftingDate.setError("Please Enter Lifting Date");
                    edtLiftingDate.requestFocus();
                    isValid = false;

                } else if (TextUtils.isEmpty(edtEMD.getText().toString().trim()) || edtEMD.getText().toString().trim().equalsIgnoreCase("")) {
                    edtEMD.setError("Please Enter EMD");
                    edtEMD.requestFocus();
                    isValid = false;

                }else if (TextUtils.isEmpty(edtStartTime.getText().toString().trim()) || edtStartTime.getText().toString().trim().equalsIgnoreCase("")) {
                    edtStartTime.setError("Please Enter Start Time");
                    edtStartTime.requestFocus();
                    isValid = false;

                } else if (!checkTime(edtStartTime.getText().toString().trim())) {
                    edtStartTime.setError("StartTime Should be equal to or greater than Current Time");
                    edtStartTime.requestFocus();
                    isValid = false;
                } else if (!checkStartTime()) {

                    edtStartTime.setError("Offer validity time must be before 12am");
                    edtStartTime.requestFocus();
                    isValid = false;

                } else if (TextUtils.isEmpty(edtValidityTime.getText().toString().trim()) || edtValidityTime.getText().toString().trim().equalsIgnoreCase("")) {
                    edtValidityTime.setError("Please Enter Validity Time");
                    edtValidityTime.requestFocus();
                    isValid = false;

                } else if (edtValidityTime.getText().toString().equalsIgnoreCase("00:00")) {
                    edtValidityTime.setError("Offer Validity Time cannot be 00:00");
                    edtValidityTime.requestFocus();
                    isValid = false;

                } else if (!checkStartValidtySum()) {

                    edtStartTime.setError("Offer validity time must be before 12am");
                    edtValidityTime.setError("Offer validity time must be before 12am");
                    edtStartTime.requestFocus();
                    edtValidityTime.requestFocus();
                    isValid = false;


                } else if (strSendTo.equalsIgnoreCase("")) {
                    edtStartTime.setError(null);
                    edtValidityTime.setError(null);
                    Toast.makeText(context, "Please Select Send to all & Send to favorite", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else if (strType.equalsIgnoreCase("Open")) {

                if (TextUtils.isEmpty(edtAvailableQty.getText().toString().trim()) || edtAvailableQty.getText().toString().equalsIgnoreCase("") || !isValidateZero(edtAvailableQty)) {
                    edtAvailableQty.setError("Please Enter Valid Available Qty");
                    edtAvailableQty.requestFocus();
                    isValid = false;

                } else if (TextUtils.isEmpty(edtPriQty.getText().toString().trim())) {
                    edtPriQty.setError("Please Enter Price Per Qtl");
                    edtPriQty.requestFocus();
                    isValid = false;
                } else if (TextUtils.isEmpty(edtPaymentDate.getText().toString().trim()) || edtPaymentDate.getText().toString().trim().equalsIgnoreCase("")) {
                    edtPaymentDate.setError("Please Enter Payment Date");
                    edtPaymentDate.requestFocus();
                    isValid = false;

                } else if (TextUtils.isEmpty(edtLiftingDate.getText().toString().trim()) || edtLiftingDate.getText().toString().equalsIgnoreCase("")) {
                    edtLiftingDate.setError("Please Enter Lifting Date");
                    edtLiftingDate.requestFocus();
                    isValid = false;

                } else if (TextUtils.isEmpty(edtEMD.getText().toString().trim()) || edtEMD.getText().toString().trim().equalsIgnoreCase("")) {
                    edtEMD.setError("Please Enter EMD");
                    edtEMD.requestFocus();
                    isValid = false;

                } else if (TextUtils.isEmpty(edtStartTime.getText().toString().trim()) || edtStartTime.getText().toString().trim().equalsIgnoreCase("")) {
                    edtStartTime.setError("Please Enter Start Time");
                    edtStartTime.requestFocus();
                    isValid = false;

                } else if (!checkTime(edtStartTime.getText().toString().trim())) {
                    edtStartTime.setError("StartTime Should be greater than Current Time");
                    edtStartTime.requestFocus();
                    isValid = false;
                } else if (!checkStartTime()) {

                    edtStartTime.setError("Offer validity time must be before 12am");
                    edtStartTime.requestFocus();
                    isValid = false;

                } else if (TextUtils.isEmpty(edtValidityTime.getText().toString().trim()) || edtValidityTime.getText().toString().trim().equalsIgnoreCase("")) {
                    edtValidityTime.setError("Please Enter Validity Time");
                    edtValidityTime.requestFocus();
                    isValid = false;

                } else if (edtValidityTime.getText().toString().equalsIgnoreCase("00:00")) {
                    edtValidityTime.setError("Validity Time Should Be not Equal to 00:00");
                    edtValidityTime.requestFocus();
                    isValid = false;

                } else if (!checkStartValidtySum()) {

                    edtStartTime.setError("Offer validity time must be before 12am");
                    edtValidityTime.setError("Offer validity time must be before 12am");
                    edtStartTime.requestFocus();
                    edtValidityTime.requestFocus();
                    isValid = false;


                } else if (strSendTo.equalsIgnoreCase("")) {
                    edtStartTime.setError(null);
                    edtValidityTime.setError(null);
                    Toast.makeText(context, "Please Select Send to all & Send to favorite", Toast.LENGTH_SHORT).show();
                    return false;
                }


            }
        } else {
            Toast.makeText(context, "Please Select Type", Toast.LENGTH_SHORT).show();
            isValid = false;
        }


        return isValid;
    }

    public boolean Validate() {
        if (strType.equalsIgnoreCase("")) {
            Toast.makeText(context, "Please Select Type", Toast.LENGTH_SHORT).show();
            return false;
        } else if (VU.isEmpty(edtValidityTime)) {
            edtValidityTime.setError("Please Enter Time");
            edtValidityTime.requestFocus();
            return false;
        } else if (VU.isEmpty(edtStartTime)) {
            edtStartTime.setError("Please Enter Start Time");
            edtStartTime.requestFocus();
            return false;
        } else if ((llPerQtl.getVisibility() == View.VISIBLE)) {
            if (VU.isEmpty(edtPriQty)) {
                edtPriQty.setError("Please Enter Price Per Qtl");
                edtPriQty.requestFocus();
                return false;
            }
        } else if (VU.isEmpty(edtAvailableQty)) {
            edtAvailableQty.setError("Please Enter Available Qty");
            edtAvailableQty.requestFocus();
            return false;
        } else if (VU.isEmpty(edtPaymentDate)) {
            edtPaymentDate.setError("Please Enter Payment Date");
            edtPaymentDate.requestFocus();
            return false;
        } else if (VU.isEmpty(edtLiftingDate)) {
            edtLiftingDate.setError("Please Enter Lifting Date");
            edtLiftingDate.requestFocus();
            return false;
        } else if (strSendTo.equalsIgnoreCase("")) {
            Toast.makeText(context, "Please Select Send to all & Send to favorite", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    public void onRadioButtonSetType(View v) {
        switch (v.getId()) {
            case R.id.PlaceSellBidActivityRadioButtonTendor:
                strType = "Tender";
                llPerQtl.setVisibility(View.GONE);
                edtPriQty.setText("");
                txtGstValue.setText("");
                break;
            case R.id.PlaceSellBidActivityRadioButtonOpen:
                strType = "Open";
                llPerQtl.setVisibility(View.VISIBLE);
                txtGstValue.setText("WITH GST");
                break;
        }
    }

    public void OnSendBid(View v) {
        switch (v.getId()) {
            case R.id.PlaceSellBidActivityRadioButtonSendToFav:
                strSendTo = "send_to_favorites";
                break;
            case R.id.PlaceSellBidActivityRadioButtonSendToAll:
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


    public void AddSellBid() {
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait while sell offer add...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url;
        if (!strFlag.equalsIgnoreCase("update")) {                                  // for new record insert
            url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/addSellBid";
        } else {                                                                               // for update record
            url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/updateSellBid";
        }
        Log.e("URL_FOR_CHECK", " : " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        pDialog.dismiss();
                        try {
                            Log.e("AddSellBid_RESPONSE", " : " + response);
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
                Toast.makeText(context, "Please Try Again", Toast.LENGTH_SHORT).show();
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
                params.put("available_qty", edtAvailableQty.getText().toString().trim());
                params.put("payment_date", VU.getddmmyyDate(edtPaymentDate.getText().toString().trim()));              //add on 24-08-18
                params.put("due_lifting_date", VU.getddmmyyDate(edtLiftingDate.getText().toString().trim()));
                params.put("remark", edtRemark.getText().toString().trim());
                params.put("transportation", "");                                // remove this field after done this page
                params.put("transport_charge", "");                               // remove this field after done this page
                params.put("type", strType);
                params.put("send_to", strSendTo);
                params.put("emd", edtEMD.getText().toString().trim());

                Log.e("SELL_BID_PARAMS", " " + params.toString());
                return params;
            }
        };


        queue.add(stringRequest);
    }

    long offerId;

    private void getIntentData()
    {

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {

            try
            {
                strFlag = extras.getString("flag");


                if(strFlag.equals("update"))
                {
                    //display data
                    jsonObject = new JSONObject(extras.getString("data"));
                    toolbar.setTitle("Sell Post Id:" + jsonObject.getString("id"));
                    setSupportActionBar(toolbar);

                    btnPlaceBid.setVisibility(View.GONE);
                    btnPlaceBid.setText("Update Place Sell Offer");
                    llPerQtl.setVisibility(View.GONE);

                    String post_status = extras.getString("post_status");

                    if(post_status.equals("single_post"))
                    {
                        rbSendToFav.setVisibility(View.GONE);
                        rbSendToAll.setVisibility(View.GONE);
                    }

                    rbSendToFav.setEnabled(false);
                    rbSendToAll.setEnabled(false);

                    rbOpen.setEnabled(false);
                    rbTendor.setEnabled(false);

                    select_val_time_btn.setVisibility(View.GONE);
                    selectStart_time_btn.setVisibility(View.GONE);

                    setData(jsonObject);
                }
                else
                {
                    hideLayout_li.setVisibility(View.GONE);
                    avail_id_tv.setText("Sell Quantity (In Qtl)");
                    //set current year
                    spnProdYear.setSelection(listSeason.size() - 1);
                }


            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void setData(JSONObject jsonObject)
    {
        try
        {

                //rbSendToAll.setVisibility(View.GONE);
                //rbSendToFav.setVisibility(View.GONE);

                //set seller data

                //set type data
                if (!jsonObject.getString("type").equalsIgnoreCase("Tender"))
                {
                    rbOpen.setChecked(true);
                    rbTendor.setVisibility(View.GONE);
                    strType = rbOpen.getText().toString();
                    Log.e("strType_IF", ": " + strType);
                    llPerQtl.setVisibility(View.VISIBLE);
                    edtPriQty.setText(jsonObject.getString("price_per_qtl"));
                    //txtGstValue.setText("WITH GST (" + jsonObject.getString("price_per_qtl") + ")");
                }
                else
                {
                    rbTendor.setChecked(true);
                    rbOpen.setVisibility(View.GONE);
                    strType = rbTendor.getText().toString();
                    llPerQtl.setVisibility(View.GONE);
                    edtPriQty.setText("");
                    txtGstValue.setText("");
                    Log.e("strType_ELSE", ": " + strType);
                }

                //set original sell quantity
                original_et.setText(jsonObject.getString("available_qty"));

                //

                if (ACU.MySP.getFromSP(context, ACU.MySP.ROLE, "").equals("Seller"))
                {
                    if (jsonObject.getString("claimed").equalsIgnoreCase("null"))
                    {
                        //set total aquired qty
                        total_aquired_qty.setText("0");
                    }
                    else
                    {
                        //set total aquired qty
                        total_aquired_qty.setText(jsonObject.getString("claimed"));
                    }


                }
                else
                {
                    //total_aquired_qty.setVisibility(View.GONE);
                    //SellDetailsActivityTotalQtyValue.setVisibility(View.GONE);
                    //set total aquired qty
                    total_aquired_qty.setText(jsonObject.getString("claimed"));
                }


                if (jsonObject.getString("current_available_qty").equalsIgnoreCase("null"))
                {
                    //set currentavailable qty
                    edtAvailableQty.setText(jsonObject.getString("original_qty"));
                }
                else
                {
                    //set currentavailable qty
                    edtAvailableQty.setText(jsonObject.getString("current_available_qty"));
                }


                //set grade / category
                if (!jsonObject.getString("category").equals(""))
                {
                    int index = listCategory.indexOf("" + jsonObject.getString("category"));
                    spnCategory.setSelection(index);

                }

                //season year
                if (!jsonObject.getString("production_year").equals(""))
                {
                    int index = listSeason.indexOf("" + jsonObject.getString("production_year"));
                    spnProdYear.setSelection(index);

                }

                //due date of payment
                edtPaymentDate.setText(VU.getddmmyyDate(jsonObject.getString("payment_date")));

                //set due date of lifting
                edtLiftingDate.setText(VU.getddmmyyDate(jsonObject.getString("due_lifting_date")));

                //set emd / qtl
                edtEMD.setText(jsonObject.getString("emd"));

                //offer start time
                edtStartTime.setText(DTU.changeTime(jsonObject.getString("bid_start_time")));

                //offer validity time
                edtValidityTime.setText(T.returnEndTime(jsonObject.getString("validity_time")));

                //set remark
                edtRemark.setText(jsonObject.getString("remark"));



                strUserId = jsonObject.getString("id");

                if (!jsonObject.getString("send_to").equalsIgnoreCase("send_to_all"))
                {
                    rbSendToFav.setChecked(true);
                    strSendTo = "send_to_favorites";
                }
                else
                {
                    rbSendToAll.setChecked(true);
                    strSendTo = "send_to_all";
                }




        } catch (JSONException e) {
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

        /*int year = Calendar.getInstance().get(Calendar.YEAR);


        ArrayList<String> arrayList = new ArrayList<>();

        for (int i = 0; i < listSeason.size(); i++) {

            if (listSeason.get(i).contains(String.valueOf(year))) {
                arrayList.add(0, listSeason.get(i));
            } else {
                arrayList.add(listSeason.get(i));
            }

        }*/


        ArrayAdapter<String> adpSeason;
        adpSeason = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_item, listSeason);
        spnProdYear.setAdapter(adpSeason);
    }


    private void MySellOffer(final String offerId) {

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
                                    original_et.setVisibility(View.VISIBLE);
                                    original_tv.setVisibility(View.VISIBLE);
                                    SellDetailsActivityTotalQtyValue.setVisibility(View.VISIBLE);

                                    original_et.setText(object.getString("available_qty"));

                                    if (!jsonObject.getString("claimed").equalsIgnoreCase("null")) {
                                        SellDetailsActivityTotalQtyValue.setText("Total Acquired quantity : " + object.getString("claimed"));
                                    } else {
                                        SellDetailsActivityTotalQtyValue.setText("Total Acquired quantity : (0)");
                                    }




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
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("role", ACU.MySP.getFromSP(context, ACU.MySP.ROLE, ""));
                params.put("id", offerId);
                return params;
            }
        };
        queue.add(stringRequest);
    }


}







