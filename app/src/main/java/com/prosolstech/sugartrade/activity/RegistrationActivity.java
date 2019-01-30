package com.prosolstech.sugartrade.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import com.prosolstech.sugartrade.database.DataBaseConstants;
import com.prosolstech.sugartrade.database.DataBaseHelper;
import com.prosolstech.sugartrade.model.DistrictModelClass;
import com.prosolstech.sugartrade.util.ACU;
import com.prosolstech.sugartrade.util.VU;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    Context context;
    EditText edtFullName, edtMobileNo, edtEmailId, edtCompanyName, edtAddressOne, edtAddressTwo, edtCity, edtCountry, edtPincode,
            edtAccName, edtBankName, edtAccNo, edtIFSCcode, edtGSTIN;

    RadioGroup rgType;
    RadioButton rbSeller, rbBuyer, rbBoth;
    Spinner spnDistrict, spnState;
    Button btnRegister;
    TextView txtLogin;
    ArrayList<String> listDistrictName;
    ArrayList<String> listDistrictID;
    String strDistrictName = "", strDistrictId = "", strRole = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        context = RegistrationActivity.this;
        intitializeUI();
    }

    public void intitializeUI() {
        listDistrictName = new ArrayList<>();
        listDistrictID = new ArrayList<>();

        edtFullName = (EditText) findViewById(R.id.RegistrationActivityEdtFullName);
        edtMobileNo = (EditText) findViewById(R.id.RegistrationActivityEdtMobileNo);
        edtEmailId = (EditText) findViewById(R.id.RegistrationActivityEdtEmailId);
        edtCompanyName = (EditText) findViewById(R.id.RegistrationActivityEdtCompanyName);
        edtAddressOne = (EditText) findViewById(R.id.RegistrationActivityEdtAddressOne);
        edtAddressTwo = (EditText) findViewById(R.id.RegistrationActivityEdtAddressTwo);
        edtCity = (EditText) findViewById(R.id.RegistrationActivityEdtCity);

        edtCountry = (EditText) findViewById(R.id.RegistrationActivityEdtCountry);
        edtPincode = (EditText) findViewById(R.id.RegistrationActivityEdtPincode);

        //Bank details
        edtAccName = (EditText) findViewById(R.id.RegistrationActivityEdtAccName);
        edtBankName = (EditText) findViewById(R.id.RegistrationActivityEdtBankName);
        edtAccNo = (EditText) findViewById(R.id.RegistrationActivityEdtAccNo);
        edtIFSCcode = (EditText) findViewById(R.id.RegistrationActivityEdtIFSCcode);
        edtGSTIN = (EditText) findViewById(R.id.RegistrationActivityEdtGSTIN);

        rgType = (RadioGroup) findViewById(R.id.RegistrationActivityRadGrp);
        rbSeller = (RadioButton) findViewById(R.id.RegistrationActivityRadBtnSeller);
        rbBuyer = (RadioButton) findViewById(R.id.RegistrationActivityRadBtnBuyer);
        rbBoth = (RadioButton) findViewById(R.id.RegistrationActivityRadBtnBoth);

        spnDistrict = (Spinner) findViewById(R.id.RegistrationActivitySpinnerDistrict);
        spnState = (Spinner) findViewById(R.id.RegistrationActivitySpinnerState);


        btnRegister = (Button) findViewById(R.id.RegistrationActivityBtnRegister);

        txtLogin = (TextView) findViewById(R.id.RegistrationActivityTxtLogin);


        txtLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);


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
        adpDistrict = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple, listDistrictName);
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


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.RegistrationActivityBtnRegister:
                if (VU.isConnectingToInternet(context)) {
                    if (!strRole.equalsIgnoreCase("")) {
                        if (Validate()) {
                            registration();
                        }
                    } else {
                        Toast.makeText(context, "Please select type", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.RegistrationActivityTxtLogin:
                startActivity(new Intent(context, LoginActivity.class));
                finish();
                break;
        }
    }

    public boolean Validate() {
        if (VU.isEmpty(edtFullName)) {
            edtFullName.setError("Please Enter Full Name");
            edtFullName.requestFocus();
            return false;
        }
        if (!edtMobileNo.getText().toString().matches("[0-9]{10}")) {
            Toast.makeText(context, "Please Enter 10 digits mobile number", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!isValidEmailId(edtEmailId.getText().toString().trim())) {
            edtEmailId.setError("Please Enter Valid Email Id");
            edtEmailId.requestFocus();
            return false;
        } else if (VU.isEmpty(edtCompanyName)) {
            edtCompanyName.setError("Please Enter Company Name");
            edtCompanyName.requestFocus();
            return false;
        } else if (VU.isEmpty(edtAddressOne)) {
            edtAddressOne.setError("Please Enter Address One");
            edtAddressOne.requestFocus();
            return false;
        } else if (VU.isEmpty(edtAddressTwo)) {
            edtAddressTwo.setError("Please Enter Address Two");
            edtAddressTwo.requestFocus();
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


//        else if (VU.isEmpty(edtAccName)) {
//            edtAccName.setError("Please Enter Account Name");
//            edtAccName.requestFocus();
//            return false;
//        } else if (VU.isEmpty(edtBankName)) {
//            edtBankName.setError("Please Enter Bank Name");
//            edtBankName.requestFocus();
//            return false;
//        } else if (VU.isEmpty(edtAccNo)) {
//            edtAccNo.setError("Please Enter Account Name");
//            edtAccNo.requestFocus();
//            return false;
//        } else if (VU.isEmpty(edtIFSCcode)) {
//            edtIFSCcode.setError("Please Enter IFSC CODE");
//            edtIFSCcode.requestFocus();
//            return false;
//        } else if (VU.isEmpty(edtGSTIN)) {
//            edtGSTIN.setError("Please Enter GSTIN Number");
//            edtGSTIN.requestFocus();
//            return false;
//        }


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


    public void onRadioButtonTypeClicked(View v) {

        boolean checked = ((RadioButton) v).isChecked();

        switch (v.getId()) {
            case R.id.RegistrationActivityRadBtnSeller:
                strRole = "Seller";
                break;
            case R.id.RegistrationActivityRadBtnBuyer:
                strRole = "Buyer";
                break;
            case R.id.RegistrationActivityRadBtnBoth:
                strRole = "Both";
                break;
        }
    }

    public void registration() {
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait while registration...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/addUser";      //for server

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        pDialog.dismiss();
                        try {
                            Log.e("RESPONSE_REGISTRATION", " : " + response);


                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("message").equalsIgnoreCase("success")) {
                                Toast.makeText(context, "Registration successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(context, LoginActivity.class));
                                finish();
                            } else if (jsonObject.getString("message").equalsIgnoreCase("Mobile exists")) {
                                Toast.makeText(context, "Mobile Number Already Exists!", Toast.LENGTH_SHORT).show();
                            } else if (jsonObject.getString("message").equalsIgnoreCase("Email exists")) {
                                Toast.makeText(context, "Email Id Already Exists!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(context, "Please try again", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("role", strRole);
                params.put("name", edtFullName.getText().toString().trim());
                params.put("mobile", edtMobileNo.getText().toString().trim());
                params.put("email", edtEmailId.getText().toString().trim());
                params.put("company_name", edtCompanyName.getText().toString().trim());
                params.put("address1", edtAddressOne.getText().toString().trim());
                params.put("address2", edtAddressTwo.getText().toString().trim());
                params.put("city", edtCity.getText().toString().trim());
                params.put("district_id", strDistrictId);
                params.put("country", edtCountry.getText().toString().trim());
                params.put("pincode", edtPincode.getText().toString().trim());
                params.put("state_id", "22");                           // by default Maharashtra id 22
                params.put("fcm_token", ACU.MySP.getFromSP(context, ACU.MySP.FCM_TOKEN, ""));
                params.put("created_date", VU.getCurrentDateTimeStamp(VU.YMD_HMS));
                params.put("account_name", edtAccName.getText().toString().trim());           // add this field  18-06-18
                params.put("account_no", edtAccNo.getText().toString().trim());
                params.put("bank_name", edtBankName.getText().toString().trim());
                params.put("gstin", edtGSTIN.getText().toString().trim());
                params.put("ifsc_code", edtIFSCcode.getText().toString().trim());

                Log.e("PARAMS", " " + params.toString());
                return params;
            }
        };
        queue.add(stringRequest);
    }
}
