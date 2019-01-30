package com.prosolstech.sugartrade.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.prosolstech.sugartrade.database.DataBaseConstants;
import com.prosolstech.sugartrade.database.DataBaseHelper;
import com.prosolstech.sugartrade.model.DistrictModelClass;
import com.prosolstech.sugartrade.util.ACU;
import com.prosolstech.sugartrade.util.VU;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.prosolstech.sugartrade.activity.BuySellDashBoardActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    Context context;
    EditText edtUserName, edtPassword;
    Button btnLogin;
    TextView txtForgotPassword, txtNewUser;
    DataBaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = LoginActivity.this;
        db = new DataBaseHelper(context);
        intitializeUI();
        if (VU.isConnectingToInternet(context)) {
            DistrictData();
        }
    }


    public void intitializeUI() {
        edtUserName = (EditText) findViewById(R.id.LoginActivityEdtUsername);
        edtPassword = (EditText) findViewById(R.id.LoginActivityEdtPassword);

        btnLogin = (Button) findViewById(R.id.LoginActivityBtnLogin);

        txtForgotPassword = (TextView) findViewById(R.id.LoginActivityTxtForgotPassword);
        txtNewUser = (TextView) findViewById(R.id.LoginActivityTxtNewUser);

        btnLogin.setOnClickListener(this);
        txtForgotPassword.setOnClickListener(this);
        txtNewUser.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.LoginActivityBtnLogin:
                if (VU.isConnectingToInternet(context)) {
                    if (Validate()) {
                        login();
                    }
                }
                break;
            case R.id.LoginActivityTxtForgotPassword:
                Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT).show();
                break;
            case R.id.LoginActivityTxtNewUser:
                startActivity(new Intent(context, RegistrationActivity.class));
                finish();
                break;
        }
    }

    public boolean Validate() {
        if (VU.isEmpty(edtUserName)) {
            edtUserName.setError("Please Enter UserName");
            edtUserName.requestFocus();
            return false;
        } else if (VU.isEmpty(edtPassword)) {
            edtPassword.setError("Please Enter Password");
            edtPassword.requestFocus();
            return false;
        }
        return true;
    }

    public void login() {
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait while data upload on server...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/login";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Log.e("Login_Response", " POST_METHOD " + response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            if (!jsonArray.getJSONObject(0).has("message")) {
                                pDialog.dismiss();
                                setdata(response);
                                Toast.makeText(context, "Login successfully", Toast.LENGTH_SHORT).show();

                                ACU.MySP.setSPBoolean(context, ACU.MySP.LOGIN_STATUS, true);
                                startActivity(new Intent(context, BuySellDashBoardActivity.class));
                                finish();

                            } else if (jsonArray.getJSONObject(0).getString("message").equalsIgnoreCase("fail")) {
                                pDialog.dismiss();
                                Log.e("INSIDE_ELSE", " " + response);
                                Toast.makeText(context, "Please Check Username and Password", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Log.e("No_Response", " GET ");
                Toast.makeText(context, "Please Try Again", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", edtUserName.getText().toString().trim());
                params.put("password", edtPassword.getText().toString().trim());
                params.put("fcm_token", ACU.MySP.getFromSP(context, ACU.MySP.FCM_TOKEN, ""));
                Log.e("PARAM", " : " + params.toString());
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
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void DistrictData() {
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please Wait......");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/getDistricts";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        pDialog.dismiss();
                        Log.e("DISTRICT_Response", " POST_METHOD " + response);
                        setDistrictName(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Log.e("No_Response", " DATA ");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private void setDistrictName(String result) {

        JSONArray jsonArray;
        DistrictModelClass districtModelClass = new DistrictModelClass();
        try {
            jsonArray = new JSONArray(result);
            boolean tab = DataBaseHelper.DistrictDB.deleteDistrictData(DataBaseConstants.TableNames.TBL_DISTRICT);
            if (tab == true) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    districtModelClass.setdistrict_id(jsonObject.getString("id"));
                    districtModelClass.setDistrict_name(jsonObject.getString("name"));
                    DataBaseHelper.DistrictDB.districtNameInsert(districtModelClass);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
