package com.prosolstech.sugartrade.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.prosolstech.sugartrade.R;
import com.prosolstech.sugartrade.database.DataBaseConstants;
import com.prosolstech.sugartrade.database.DataBaseHelper;
import com.prosolstech.sugartrade.util.ACU;
import com.prosolstech.sugartrade.util.DTU;
import com.prosolstech.sugartrade.util.VU;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class MyProfileActivity extends AppCompatActivity {

    Context context;
    EditText edtUserType, edtName, edtMobileNo, edtEmail, edtCompanyName, edtAddressOne, edtAddresTwo, edtCountry,
            edtCity, edtPincode, edtAccName, edtBankName, edtAccNo, edtIFSCcode, edtGSTIN;
    Button btnUpdate;
    Spinner spnDistrict, spnState;
    ArrayList<String> listDistrictName;
    ArrayList<String> listDistrictID;
    String strUserId = "", strDistrictName = "", strDistrictId = "";
    DataBaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        context = MyProfileActivity.this;
        db = new DataBaseHelper(context);
        setToolBar();
        intitializeUI();
    }

    private void intitializeUI() {
        listDistrictName = new ArrayList<>();
        listDistrictID = new ArrayList<>();

        edtUserType = (EditText) findViewById(R.id.MyProfileActivityEdtUserType);
        edtName = (EditText) findViewById(R.id.MyProfileActivityEdtFullName);
        edtMobileNo = (EditText) findViewById(R.id.MyProfileActivityEdtMobileNo);
        edtEmail = (EditText) findViewById(R.id.MyProfileActivityEdtEmailId);
        edtCompanyName = (EditText) findViewById(R.id.MyProfileActivityEdtCompanyName);
        edtAddressOne = (EditText) findViewById(R.id.MyProfileActivityEdtAddressOne);
        edtAddresTwo = (EditText) findViewById(R.id.MyProfileActivityEdtAddressTwo);

        spnState = (Spinner) findViewById(R.id.MyProfileActivitySpinnerState);
        spnDistrict = (Spinner) findViewById(R.id.MyProfileActivitySpinnerDistrict);

        edtCountry = (EditText) findViewById(R.id.MyProfileActivityEdtCountry);
        edtCity = (EditText) findViewById(R.id.MyProfileActivityEdtCity);
        edtPincode = (EditText) findViewById(R.id.MyProfileActivityEdtPincode);
        edtAccName = (EditText) findViewById(R.id.MyProfileActivityEdtAccName);
        edtBankName = (EditText) findViewById(R.id.MyProfileActivityEdtBankName);
        edtAccNo = (EditText) findViewById(R.id.MyProfileActivityEdtAccNo);
        edtIFSCcode = (EditText) findViewById(R.id.MyProfileActivityEdtIFSCcode);
        edtGSTIN = (EditText) findViewById(R.id.MyProfileActivityEdtGSTIN);

        btnUpdate = (Button) findViewById(R.id.MyProfileActivityBtnUpdate);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (VU.isConnectingToInternet(context)) {
                    if (Validate()) {
                        updateProfile();
                    }
                }
            }
        });

        Cursor cur = DataBaseHelper.DistrictDB.getDistrictName();

        try {
            listDistrictName.add(0, "Select District");

            while (cur.moveToNext()) {
                listDistrictName.add(cur.getString(cur.getColumnIndex(DataBaseConstants.DistrictName.DISTRICT_NAME)));
                listDistrictID.add(cur.getString(cur.getColumnIndex(DataBaseConstants.DistrictName.DISTRICT_ID)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayAdapter<String> adpDistrict;
        adpDistrict = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_item, listDistrictName);
        spnDistrict.setAdapter(adpDistrict);

        spnDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    strDistrictName = listDistrictName.get(position);
                    Log.e("strDistrictName", ": " + strDistrictName);
                    strDistrictId = listDistrictID.get(position - 1);
                    Log.e("strDistrictId", " " + strDistrictId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        setProfileData();
    }


    public boolean Validate() {
        if (VU.isEmpty(edtName)) {
            edtName.setError("Please Enter Full Name");
            edtName.requestFocus();
            return false;
        }
        if (!edtMobileNo.getText().toString().matches("[0-9]{10}")) {
            Toast.makeText(context, "Please Enter 10 digits mobile number", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!isValidEmailId(edtEmail.getText().toString().trim())) {
            edtEmail.setError("Please Enter Valid Email Id");
            edtEmail.requestFocus();
            return false;
        } else if (VU.isEmpty(edtCompanyName)) {
            edtCompanyName.setError("Please Enter Company Name");
            edtCompanyName.requestFocus();
            return false;
        } else if (VU.isEmpty(edtAddressOne)) {
            edtAddressOne.setError("Please Enter Address One");
            edtAddressOne.requestFocus();
            return false;
        } else if (VU.isEmpty(edtAddresTwo)) {
            edtAddresTwo.setError("Please Enter Address Two");
            edtAddresTwo.requestFocus();
            return false;
        } else if (VU.isEmpty(edtCity)) {
            edtCity.setError("Please Enter City");
            edtCity.requestFocus();
            return false;
        } else if (VU.isEmpty(edtCountry)) {
            edtCountry.setError("Please Enter Country");
            edtCountry.requestFocus();
            return false;
        } else if (spnDistrict.getSelectedItemPosition() == 0) {
            Toast.makeText(context, "Please Select District Name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (VU.isEmpty(edtPincode)) {
            edtPincode.setError("Please Enter Pincode");
            edtPincode.requestFocus();
            return false;
        }


        return true;
    }


    private boolean isValidEmailId(String email) {

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
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
                finish();
                onBackPressed();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateProfile() {

        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait while data fetch from server...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(context);

        String url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/updateUser";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Log.e("PROFILE_UPDATE", " RESPONSE " + response);
                        pDialog.dismiss();
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            if (!jsonArray.getJSONObject(0).has("message")) {
                                Toast.makeText(context, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                                setdata(response);
                            } else if (jsonArray.getJSONObject(0).getString("message").equalsIgnoreCase("fail")) {
                                Toast.makeText(context, "Please Try Again", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("No Response", "FOUND");
                pDialog.dismiss();
                Toast.makeText(context, "Please Try Again", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", strUserId);
                params.put("role", edtUserType.getText().toString().trim());
                params.put("name", edtName.getText().toString().trim());
                params.put("mobile", edtMobileNo.getText().toString().trim());
                params.put("email", edtEmail.getText().toString().trim());
                params.put("company_name", edtCompanyName.getText().toString().trim());
                params.put("address1", edtAddressOne.getText().toString().trim());
                params.put("address2", edtAddresTwo.getText().toString().trim());
                params.put("city", edtCity.getText().toString().trim());
                params.put("district_id", strDistrictId);
                params.put("state_id", "22");                              // by default Maharashtra id 22
                params.put("country", edtCountry.getText().toString().trim());
                params.put("pincode", edtPincode.getText().toString().trim());
                params.put("account_name", edtAccName.getText().toString().trim());
                params.put("account_no", edtAccNo.getText().toString().trim());
                params.put("bank_name", edtBankName.getText().toString().trim());
                params.put("gstin", edtGSTIN.getText().toString().trim());
                params.put("ifsc_code", edtIFSCcode.getText().toString().trim());
                params.put("modified_date", DTU.getCurrentDateTimeStamp(DTU.YMD_HMS));

                Log.e("PARAMS", " : " + params.toString());
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private void setdata(String response) {
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(response);
            Log.e("jsonArray", " : " + jsonArray.toString());
            if (jsonArray.length() > 0) {
                ACU.MySP.saveSP(context, ACU.MySP.ID, jsonArray.getJSONObject(0).getString("id"));
                ACU.MySP.saveSP(context, ACU.MySP.ROLE, jsonArray.getJSONObject(0).getString("role"));
                ACU.MySP.saveSP(context, ACU.MySP.NAME, jsonArray.getJSONObject(0).getString("name"));
                ACU.MySP.saveSP(context, ACU.MySP.MOBILE_NO, jsonArray.getJSONObject(0).getString("mobile"));
                ACU.MySP.saveSP(context, ACU.MySP.EMAIL_ID, jsonArray.getJSONObject(0).getString("email"));
                ACU.MySP.saveSP(context, ACU.MySP.COMPANY_NAME, jsonArray.getJSONObject(0).getString("company_name"));
                ACU.MySP.saveSP(context, ACU.MySP.ADD_ONE, jsonArray.getJSONObject(0).getString("address1"));
                ACU.MySP.saveSP(context, ACU.MySP.ADD_TWO, jsonArray.getJSONObject(0).getString("address2"));
                ACU.MySP.saveSP(context, ACU.MySP.COUNTRY, jsonArray.getJSONObject(0).getString("country"));
                ACU.MySP.saveSP(context, ACU.MySP.STATE, jsonArray.getJSONObject(0).getString("state_id"));
                ACU.MySP.saveSP(context, ACU.MySP.DISTRICT, jsonArray.getJSONObject(0).getString("district_id"));
                ACU.MySP.saveSP(context, ACU.MySP.CITY, jsonArray.getJSONObject(0).getString("city"));
                ACU.MySP.saveSP(context, ACU.MySP.PINCODE, jsonArray.getJSONObject(0).getString("pincode"));
                ACU.MySP.saveSP(context, ACU.MySP.ACC_NAME, jsonArray.getJSONObject(0).getString("account_name"));
                ACU.MySP.saveSP(context, ACU.MySP.BANK_NAME, jsonArray.getJSONObject(0).getString("bank_name"));
                ACU.MySP.saveSP(context, ACU.MySP.ACC_NO, jsonArray.getJSONObject(0).getString("account_no"));
                ACU.MySP.saveSP(context, ACU.MySP.IFSC_CODE, jsonArray.getJSONObject(0).getString("ifsc_code"));
                ACU.MySP.saveSP(context, ACU.MySP.GSTIN, jsonArray.getJSONObject(0).getString("gstin"));
                ACU.MySP.saveSP(context, ACU.MySP.STATE_NAME, jsonArray.getJSONObject(0).getString("stateName"));
                ACU.MySP.saveSP(context, ACU.MySP.DISTRICT_NAME, jsonArray.getJSONObject(0).getString("districtName"));

                setProfileData();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setProfileData() {
        strUserId = ACU.MySP.getFromSP(context, ACU.MySP.ID, "");
        edtUserType.setText(ACU.MySP.getFromSP(context, ACU.MySP.ROLE, ""));
        edtName.setText(ACU.MySP.getFromSP(context, ACU.MySP.NAME, ""));
        edtMobileNo.setText(ACU.MySP.getFromSP(context, ACU.MySP.MOBILE_NO, ""));
        edtEmail.setText(ACU.MySP.getFromSP(context, ACU.MySP.EMAIL_ID, ""));
        edtCompanyName.setText(ACU.MySP.getFromSP(context, ACU.MySP.COMPANY_NAME, ""));
        edtAddressOne.setText(ACU.MySP.getFromSP(context, ACU.MySP.ADD_ONE, ""));
        edtAddresTwo.setText(ACU.MySP.getFromSP(context, ACU.MySP.ADD_TWO, ""));
        edtCountry.setText(ACU.MySP.getFromSP(context, ACU.MySP.COUNTRY, ""));
        edtCity.setText(ACU.MySP.getFromSP(context, ACU.MySP.CITY, ""));
        edtPincode.setText(ACU.MySP.getFromSP(context, ACU.MySP.PINCODE, ""));
        edtAccName.setText(ACU.MySP.getFromSP(context, ACU.MySP.ACC_NAME, ""));
        edtBankName.setText(ACU.MySP.getFromSP(context, ACU.MySP.BANK_NAME, ""));
        edtAccNo.setText(ACU.MySP.getFromSP(context, ACU.MySP.ACC_NO, ""));
        edtIFSCcode.setText(ACU.MySP.getFromSP(context, ACU.MySP.IFSC_CODE, ""));
        edtGSTIN.setText(ACU.MySP.getFromSP(context, ACU.MySP.GSTIN, ""));

        if (!ACU.MySP.getFromSP(context, ACU.MySP.DISTRICT_NAME, "").equals("")) {
            int index = listDistrictName.indexOf("" + ACU.MySP.getFromSP(context, ACU.MySP.DISTRICT_NAME, ""));
            spnDistrict.setSelection(index);
            Log.e("SET_spnDistrict", " " + index + "  " + ACU.MySP.getFromSP(context, ACU.MySP.DISTRICT_NAME, ""));
        }
        if (!ACU.MySP.getFromSP(context, ACU.MySP.STATE_NAME, "").equals("")) {
            spnState.setSelection(1);
            Log.e("spnState", " " + "  " + ACU.MySP.getFromSP(context, ACU.MySP.STATE_NAME, ""));
        }
    }
}
