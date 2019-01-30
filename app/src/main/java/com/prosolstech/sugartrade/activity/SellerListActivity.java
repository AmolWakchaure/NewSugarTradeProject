package com.prosolstech.sugartrade.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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
import com.prosolstech.sugartrade.adapter.SellerListAdapter;
import com.prosolstech.sugartrade.database.DataBaseConstants;
import com.prosolstech.sugartrade.database.DataBaseHelper;
import com.prosolstech.sugartrade.util.ACU;
import com.prosolstech.sugartrade.util.ItemAnimation;
import com.prosolstech.sugartrade.util.VU;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SellerListActivity extends AppCompatActivity {

    Context context;
    private RecyclerView recyclerView;
    private SellerListAdapter sellerListAdapter;
    private int animation_type = ItemAnimation.BOTTOM_UP;
    AutoCompleteTextView autoTxtSearch;
    ArrayList<String> listSellerName;
    ArrayList<String> listSellerNameId;
    ArrayList<String> listCategory;
    ArrayList<String> listCatId;
    ArrayList<String> listSeason;
    ArrayList<String> listSeasonId;
    ArrayList<String> listDistrictName;
    ArrayList<String> listDistrictID;
    String strUserId = "", strCatID = "", strSeason = "", strRadioButton = "", strDistrictName = "", strDistrictId = "";
    RadioButton rbOne, rbTwo, rbThree, rbFour;
    Spinner spnState, spnDistrict;
    Button btnSave, btnCancel;
    Dialog dialog;
    LinearLayout llState;
    ImageView imgFilter;
    ImageButton imgSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_list);
        context = SellerListActivity.this;
        setToolBar();
        intitializeUI();
    }

    private void intitializeUI() {

        autoTxtSearch = (AutoCompleteTextView) findViewById(R.id.SellerListActivityAutoTxtName);

        imgSearch = (ImageButton) findViewById(R.id.SellerListActivityImgSearch);
        imgFilter = (ImageView) findViewById(R.id.SellerListActivityImgFilter);

        recyclerView = (RecyclerView) findViewById(R.id.SellerListActivityRecyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 1));
        recyclerView.setHasFixedSize(true);
        animation_type = ItemAnimation.FADE_IN;

        if (VU.isConnectingToInternet(context)) {
            listData();
        }

        imgFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog();
            }
        });

        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Validate()) {
                    searchByName(strUserId);
                }
            }
        });
        autoTxtSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                strUserId = listSellerNameId.get(position);
                Log.e("Seller_strUserId", " " + strUserId);
            }
        });
    }

    public boolean Validate() {
        if (VU.isEmpty(autoTxtSearch)) {
            autoTxtSearch.setError("Please enter search field");
            autoTxtSearch.requestFocus();
            return false;
        }
        return true;
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

    private void listData() {

        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait while data fetch from server...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(context);

//        String url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/getSellers";
        String url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/getBuyers";


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Log.e("listData", " RESPONSE " + response);
                        pDialog.dismiss();
                        Names(response);
                        setListAdapter(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("No_Response", "FOUND");
                pDialog.dismiss();
                Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                Log.e("PARAMS", " MYREQ " + params.toString());

                params.put("userby", ACU.MySP.getFromSP(SellerListActivity.this, ACU.MySP.ID, ""));

                return params;
            }
        };
        queue.add(stringRequest);
    }

    private void setListAdapter(String result) {
        try {
            JSONArray array;
            Log.e("MyRequested_LISTADAPTER", " RESULT " + result.trim());
            array = new JSONArray(result);
            if (array != null && array.length() > 0) {
                sellerListAdapter = new SellerListAdapter(context, array, animation_type);
                recyclerView.setAdapter(sellerListAdapter);
            } else {
                Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void Names(String result) {
        try {
            JSONArray jsonArray;
            Log.e("Names_LISTADAPTER", " RESULT " + result.trim());
            jsonArray = new JSONArray(result);
            listSellerName = new ArrayList<>();
            listSellerNameId = new ArrayList<>();
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    listSellerNameId.add(object.getString("id"));
                    listSellerName.add(object.getString("name"));
                    Log.e("NAME_", " " + object.getString("name"));
                    Log.e("BY_ID", " " + object.getString("id"));
                }
                ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, listSellerName);
                autoTxtSearch.setThreshold(1);
                autoTxtSearch.setAdapter(adapter);
            } else {
                Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void searchByName(final String strUserId) {

        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait while data fetch from server...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(context);

        String url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/searchByName";


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Log.e("searchByName", " RESPONSE " + response);
                        pDialog.dismiss();
                        setListAdapter(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("No_Response", "FOUND");
                pDialog.dismiss();
                Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", strUserId);
                Log.e("searchByNamePARAMS", " MYREQ " + params.toString());
                return params;
            }
        };
        queue.add(stringRequest);
    }

    public void showCustomDialog() {
        listSeason = new ArrayList<>();
        listSeasonId = new ArrayList<>();
        listCategory = new ArrayList<>();
        listCatId = new ArrayList<>();
        listDistrictName = new ArrayList<>();
        listDistrictID = new ArrayList<>();
        strRadioButton = "";

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.seller_filter_list);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        // if button is clicked, close the custom dialog

        rbOne = (RadioButton) dialog.findViewById(R.id.SellerFilterDialogRadioButtonOne);
        rbTwo = (RadioButton) dialog.findViewById(R.id.SellerFilterDialogRadioButtonTwo);
        rbThree = (RadioButton) dialog.findViewById(R.id.SellerFilterDialogRadioButtonThree);
        rbFour = (RadioButton) dialog.findViewById(R.id.SellerFilterDialogRadioButtonFour);


        spnState = (Spinner) dialog.findViewById(R.id.SellerFilterDialogSpnState);
        spnDistrict = (Spinner) dialog.findViewById(R.id.SellerFilterDialogSpnDistrict);

        llState = (LinearLayout) dialog.findViewById(R.id.SellerFilterDialogLinearState);

        btnSave = (Button) dialog.findViewById(R.id.SellerFilterDialogBtnSave);
        btnCancel = (Button) dialog.findViewById(R.id.SellerFilterDialogBtnCancel);


        Cursor curDistrict = DataBaseHelper.DistrictDB.getDistrictName();
        Log.e("curDistrict", " : " + curDistrict.getCount());
        try {
            listDistrictName.add(0, "Select District");
            Log.e("DistrictDB_listDistrict", " : " + listDistrictName.toString());
            while (curDistrict.moveToNext()) {
                listDistrictName.add(curDistrict.getString(curDistrict.getColumnIndex(DataBaseConstants.DistrictName.DISTRICT_NAME)));
                listDistrictID.add(curDistrict.getString(curDistrict.getColumnIndex(DataBaseConstants.DistrictName.DISTRICT_ID)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayAdapter<String> adpDistrict;
        adpDistrict = new ArrayAdapter<String>(context, R.layout.simple, listDistrictName);
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

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!strRadioButton.equalsIgnoreCase("")) {
                    if (strRadioButton.equalsIgnoreCase("Favorite")) {
                        FilterData("favorite", "");
                    } else if (strRadioButton.equalsIgnoreCase("Blocked")) {
                        FilterData("blocked", "");
                    } else if (strRadioButton.equalsIgnoreCase("State And District")) {
                        FilterData("state_and_district", strDistrictId);
                    } else if (strRadioButton.equalsIgnoreCase("rating")) {
                        FilterData("rating", "");
                    }
                } else {
                    Toast.makeText(context, "Please select any filter", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        rbOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strRadioButton = rbOne.getText().toString();
                rbTwo.setChecked(false);
                rbThree.setChecked(false);
                rbFour.setChecked(false);
                llState.setVisibility(View.GONE);
            }
        });
        rbTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strRadioButton = rbTwo.getText().toString();
                rbOne.setChecked(false);
                rbThree.setChecked(false);
                rbFour.setChecked(false);
                llState.setVisibility(View.GONE);
            }
        });
        rbThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strRadioButton = rbThree.getText().toString();
                rbOne.setChecked(false);
                rbTwo.setChecked(false);
                rbFour.setChecked(false);
                llState.setVisibility(View.VISIBLE);
            }
        });
        rbFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strRadioButton = rbFour.getText().toString();
                rbOne.setChecked(false);
                rbTwo.setChecked(false);
                rbThree.setChecked(false);
                llState.setVisibility(View.GONE);
            }
        });
        dialog.show();
    }

    private void FilterData(final String strFilter, final String strType) {                  // in this "strType" and "strFilter" variable user select filter type that value set in this variable
        String url = "";
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait while data fetch from server...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(context);


        url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/getSellerBuyerFilter";      //for server
        Log.e("FilterData", " ....." + url);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Log.e("FilterData", " RESPONSE " + response);
                        pDialog.dismiss();
                        dialog.dismiss();
                        setListAdapter(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("No Response", "FOUND");
                pDialog.dismiss();
                dialog.dismiss();
                Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("role", ACU.MySP.getFromSP(context, ACU.MySP.ROLE, ""));
                params.put("type", strType);
                params.put("filter", strFilter);

                Log.e("FilterData_PARAMS", " : " + params.toString());

                return params;
            }
        };
        queue.add(stringRequest);
    }

}
