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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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
import com.prosolstech.sugartrade.adapter.BuyerListAdapter;
import com.prosolstech.sugartrade.classes.Constants;
import com.prosolstech.sugartrade.classes.T;
import com.prosolstech.sugartrade.database.DataBaseConstants;
import com.prosolstech.sugartrade.database.DataBaseHelper;
import com.prosolstech.sugartrade.model.BuyerInfoDetails;
import com.prosolstech.sugartrade.model.SellerInfoDetails;
import com.prosolstech.sugartrade.util.ACU;
import com.prosolstech.sugartrade.util.Constant;
import com.prosolstech.sugartrade.util.ItemAnimation;
import com.prosolstech.sugartrade.util.VU;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BuyerListActivity extends AppCompatActivity {

    private Context context;
    private RecyclerView recyclerView;
    private BuyerListAdapter buyerListAdapter;
    private int animation_type = ItemAnimation.BOTTOM_UP;
    private EditText autoTxtSearch;
    private ArrayList<String> listSellerName;
    private ArrayList<String> listSellerNameId;

    private ArrayList<String> listCategory;
    private  ArrayList<String> listCatId;
    private  ArrayList<String> listSeason;
    private  ArrayList<String> listSeasonId;
    private  ArrayList<String> listDistrictName;
    ArrayList<String> listDistrictID;
    String strUserId = "", strCatID = "", strSeason = "", strRadioButton = "", strDistrictName = "", strDistrictId = "";
    RadioButton favroritrb, blockedrb, stateDistrb, ratingrb,
            unfavoriterb,
            unblockrb;
    Spinner spnState, spnDistrict;
    Button btnSave, btnCancel;
    Dialog dialog;
    LinearLayout llState;


    ImageView imgFilter;
    ImageButton imgSearch;

    private ArrayList<BuyerInfoDetails> BUYER_DETAILS = new ArrayList<>();
    private ArrayList<BuyerInfoDetails> BUYER_DETAILS_FILTER = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_list);
        context = BuyerListActivity.this;
        setToolBar();
        intitializeUI();
    }

    public void refreshList(ArrayList<BuyerInfoDetails> sellerDetails, int position, String status)
    {

        BuyerInfoDetails sellerInfoDetails = sellerDetails.get(position);

        sellerInfoDetails.setBuyerFavStatus(status);
        sellerDetails.set(position,sellerInfoDetails);
        buyerListAdapter.notifyItemChanged(position);
    }

    private void intitializeUI() {


        autoTxtSearch = (EditText) findViewById(R.id.BuyerListActivityAutoTxtName);
        imgSearch = (ImageButton) findViewById(R.id.BuyerListActivityImgSearch);
        imgFilter = (ImageView) findViewById(R.id.BuyerListActivityImgFilter);

        recyclerView = (RecyclerView) findViewById(R.id.BuyerListActivityRecyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 1));
        recyclerView.setHasFixedSize(true);
        animation_type = ItemAnimation.FADE_IN;

        if (VU.isConnectingToInternet(context)) {
            BuyerlistData();
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
        /*autoTxtSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                strUserId = listSellerNameId.get(position);
                Log.e("Buyer_strUserId", " " + strUserId);
            }
        });*/

        autoTxtSearch.addTextChangedListener(new TextWatcher()
        {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence newText, int start, int before, int count) {
                //M.t(""+s);

                searchDetails(newText.toString());

            }
        });
    }

    private void searchDetails(String newText)
    {
        final ArrayList<BuyerInfoDetails> filteredModelList = filter(BUYER_DETAILS, "" + newText);
        buyerListAdapter.setFilter(filteredModelList);
    }

    private ArrayList<BuyerInfoDetails> filter(ArrayList<BuyerInfoDetails> models, String query) {
        //query = query.toLowerCase();
        final ArrayList<BuyerInfoDetails> filteredModelList = new ArrayList<>();
        filteredModelList.clear();

        if (query.length() == 0) {
            filteredModelList.addAll(models);
        } else {
            for (BuyerInfoDetails model : models) {

                final String buyerName = model.getBuyerName();
                final String buyerBlockStatus = model.getBuyerBlockStatusSearch();
                final String buyerFavStatus = model.getBuyerFavStatusSearch();
                final String buyerRatingStatus = model.getBuyerRating();


                //buyerName
                if (buyerName.toLowerCase().contains(query.toLowerCase())) {
                    filteredModelList.add(model);
                }
                //buyerBlockStatus
                else if (buyerBlockStatus.toLowerCase().contains(query.toLowerCase()))
                {
                    filteredModelList.add(model);
                }
                //buyerFavStatus
                else if (buyerFavStatus.toLowerCase().contains(query.toLowerCase()))
                {
                    filteredModelList.add(model);
                }
                //buyerRatingStatus
                else if (buyerRatingStatus.contains(query))
                {
                    filteredModelList.add(model);
                }
            }
        }


        return filteredModelList;
    }
    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public boolean Validate() {
        if (VU.isEmpty(autoTxtSearch)) {
            autoTxtSearch.setError("Please enter search field");
            autoTxtSearch.requestFocus();
            return false;
        }
        return true;
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

    private void BuyerlistData() {
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait while data fetch from server...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(context);

       // String url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/getBuyers";
        String url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/getBuyersNew";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Log.e("BuyerlistData", " RESPONSE " + response);
                        pDialog.dismiss();
                       // Names(response);
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
                params.put("userby", ACU.MySP.getFromSP(BuyerListActivity.this, ACU.MySP.ID, ""));

                return params;
            }
        };
        queue.add(stringRequest);
    }


    private void setListAdapter(String result) {


        String id = Constants.NA;
        String web_link = Constants.NA;
        String name = Constants.NA;
        String average_rating = Constants.NA;
        String FavStatus = Constants.N;
        String blkStatus = Constants.NA;

        try
        {
            JSONArray array;
            ;
            Log.e("BuyerlistData_ADAPTER", " RESULT " + result.trim());
            array = new JSONArray(result);
            if (array != null && array.length() > 0)
            {
                BUYER_DETAILS.clear();
                for(int i = 0; i < array.length(); i++)
                {
                    JSONObject buyerJsonObject = array.getJSONObject(i);

                    if(buyerJsonObject.has("id") && !buyerJsonObject.isNull("id"))
                    {
                        id = buyerJsonObject.getString("id");
                    }
                    if(buyerJsonObject.has("web_link") && !buyerJsonObject.isNull("web_link"))
                    {
                        web_link = buyerJsonObject.getString("web_link");
                    }
                    if(buyerJsonObject.has("name") && !buyerJsonObject.isNull("name"))
                    {
                        name = buyerJsonObject.getString("name");
                    }
                    if(buyerJsonObject.has("average_rating") && !buyerJsonObject.isNull("average_rating"))
                    {
                        average_rating = buyerJsonObject.getString("average_rating");
                    }
                    if(buyerJsonObject.has("is_favorite") && !buyerJsonObject.isNull("is_favorite"))
                    {
                        FavStatus = buyerJsonObject.getString("is_favorite");
                    }
                    else
                    {
                        FavStatus = Constants.N;
                    }
                    if(buyerJsonObject.has("blkStatus") && !buyerJsonObject.isNull("blkStatus"))
                    {
                        blkStatus = buyerJsonObject.getString("blkStatus");
                    }
                    else
                    {
                        blkStatus = Constants.N;
                    }

                    BuyerInfoDetails buyerInfoDetails = new BuyerInfoDetails();

                    buyerInfoDetails.setBuyerId(id);
                    buyerInfoDetails.setBuyerName(name);
                    buyerInfoDetails.setBuyerWEbLing(web_link);
                    buyerInfoDetails.setBuyerRating(average_rating);
                    buyerInfoDetails.setBuyerFavStatus(FavStatus);
                    buyerInfoDetails.setBuyerblockStatus(blkStatus);

                    //block
                    if(blkStatus.equals("Y"))
                    {
                        buyerInfoDetails.setBuyerBlockStatusSearch("block");
                    }
                    else
                    {
                        buyerInfoDetails.setBuyerBlockStatusSearch("unblock");
                    }
                    //favoraite
                    if(FavStatus.equals("Y"))
                    {
                        buyerInfoDetails.setBuyerFavStatusSearch("favorite");
                    }
                    else
                    {
                        buyerInfoDetails.setBuyerFavStatusSearch("unfavorite");
                    }

                    BUYER_DETAILS.add(buyerInfoDetails);

                }
                //order by id
                Collections.sort(BUYER_DETAILS, BuyerInfoDetails.priceLowTohigh);
                buyerListAdapter = new BuyerListAdapter(context, animation_type,BUYER_DETAILS,BuyerListActivity.this);
                recyclerView.setAdapter(buyerListAdapter);
            }
            else
            {
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


    public void showCustomDialog()
    {
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

        favroritrb = (RadioButton) dialog.findViewById(R.id.SellerFilterDialogRadioButtonOne);
        blockedrb = (RadioButton) dialog.findViewById(R.id.SellerFilterDialogRadioButtonTwo);

        unfavoriterb = (RadioButton) dialog.findViewById(R.id.SellerFilterDialogRadioButtonUnfavorite);
        unblockrb = (RadioButton) dialog.findViewById(R.id.SellerFilterDialogRadioButtonUnblocked);

        stateDistrb = (RadioButton) dialog.findViewById(R.id.SellerFilterDialogRadioButtonThree);
        ratingrb = (RadioButton) dialog.findViewById(R.id.SellerFilterDialogRadioButtonFour);

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
                if (!strRadioButton.equalsIgnoreCase(""))
                {
                    if (strRadioButton.equalsIgnoreCase("Favorite"))
                    {
                        filterData("Favorite");

                        //FilterData("favorite", "");
                    }
                    //Unfavorite,Unblocked
                    else if (strRadioButton.equalsIgnoreCase("Unfavorite"))
                    {
                        filterData("Unfavorite");
                        //FilterData("blocked", "");
                    }
                    else if (strRadioButton.equalsIgnoreCase("Blocked"))
                    {
                        filterData("blocked");
                        //FilterData("blocked", "");
                    }
                    else if (strRadioButton.equalsIgnoreCase("Unblocked"))
                    {
                        filterData("Unblocked");
                        //FilterData("blocked", "");
                    }
                    else if (strRadioButton.equalsIgnoreCase("State And District"))
                    {
                        FilterData("state_and_district", strDistrictId);
                    }
                    else if (strRadioButton.equalsIgnoreCase("rating")) {

                        filterData("rating");
                        //FilterData("rating", "");
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

        favroritrb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strRadioButton = favroritrb.getText().toString();
                favroritrb.setChecked(true);
                unfavoriterb.setChecked(false);
                blockedrb.setChecked(false);
                unblockrb.setChecked(false);
                stateDistrb.setChecked(false);
                ratingrb.setChecked(false);
                llState.setVisibility(View.GONE);
            }
        });
        unfavoriterb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strRadioButton = unfavoriterb.getText().toString();
                favroritrb.setChecked(false);
                unfavoriterb.setChecked(true);
                blockedrb.setChecked(false);
                unblockrb.setChecked(false);
                stateDistrb.setChecked(false);
                ratingrb.setChecked(false);
                llState.setVisibility(View.GONE);
            }
        });

        blockedrb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strRadioButton = blockedrb.getText().toString();

                favroritrb.setChecked(false);
                unfavoriterb.setChecked(false);
                blockedrb.setChecked(true);
                unblockrb.setChecked(false);
                stateDistrb.setChecked(false);
                ratingrb.setChecked(false);

                llState.setVisibility(View.GONE);
            }
        });
        unblockrb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strRadioButton = unblockrb.getText().toString();
                favroritrb.setChecked(false);
                unfavoriterb.setChecked(false);
                blockedrb.setChecked(false);
                unblockrb.setChecked(true);
                stateDistrb.setChecked(false);
                ratingrb.setChecked(false);
                llState.setVisibility(View.GONE);
            }
        });
        stateDistrb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strRadioButton = stateDistrb.getText().toString();
                favroritrb.setChecked(false);
                unfavoriterb.setChecked(false);
                blockedrb.setChecked(false);
                unblockrb.setChecked(false);
                stateDistrb.setChecked(true);
                ratingrb.setChecked(false);
                llState.setVisibility(View.VISIBLE);
            }
        });
        ratingrb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strRadioButton = ratingrb.getText().toString();
                favroritrb.setChecked(false);
                unfavoriterb.setChecked(false);
                blockedrb.setChecked(false);
                unblockrb.setChecked(false);
                stateDistrb.setChecked(false);
                ratingrb.setChecked(true);
                llState.setVisibility(View.GONE);
            }
        });
        dialog.show();
    }

    private void filterData(String searchStatus) {

        try
        {
            //Favorite
            if(searchStatus.equals("Favorite"))
            {
                BUYER_DETAILS_FILTER.clear();
                for(int i = 0; i < BUYER_DETAILS.size(); i++)
                {
                    BuyerInfoDetails buyerInfoDetails = BUYER_DETAILS.get(i);

                    String is_favorite = buyerInfoDetails.getBuyerFavStatus();

                    if(is_favorite.equals("Y"))
                    {
                        BUYER_DETAILS_FILTER.add(buyerInfoDetails);
                    }
                }
                buyerListAdapter = new BuyerListAdapter(context, animation_type,BUYER_DETAILS_FILTER,BuyerListActivity.this);
                recyclerView.setAdapter(buyerListAdapter);
                dialog.dismiss();
            }
            //Unfavorite
            else if(searchStatus.equals("Unfavorite"))
            {
                BUYER_DETAILS_FILTER.clear();
                for(int i = 0; i < BUYER_DETAILS.size(); i++)
                {
                    BuyerInfoDetails buyerInfoDetails = BUYER_DETAILS.get(i);

                    String is_block = buyerInfoDetails.getBuyerFavStatus();

                    if(is_block.equals("N"))
                    {
                        BUYER_DETAILS_FILTER.add(buyerInfoDetails);
                    }
                }
                buyerListAdapter = new BuyerListAdapter(context, animation_type,BUYER_DETAILS_FILTER,BuyerListActivity.this);
                recyclerView.setAdapter(buyerListAdapter);
                dialog.dismiss();
            }
            //blocked
            else if(searchStatus.equals("blocked"))
            {
                BUYER_DETAILS_FILTER.clear();
                for(int i = 0; i < BUYER_DETAILS.size(); i++)
                {
                    BuyerInfoDetails buyerInfoDetails = BUYER_DETAILS.get(i);

                    String is_block = buyerInfoDetails.getBuyerblockStatus();

                    if(is_block.equals("Y"))
                    {
                        BUYER_DETAILS_FILTER.add(buyerInfoDetails);
                    }
                }
                buyerListAdapter = new BuyerListAdapter(context, animation_type,BUYER_DETAILS_FILTER,BuyerListActivity.this);
                recyclerView.setAdapter(buyerListAdapter);
                dialog.dismiss();
            }
            //Unblocked
            else if(searchStatus.equals("Unblocked"))
            {
                BUYER_DETAILS_FILTER.clear();
                for(int i = 0; i < BUYER_DETAILS.size(); i++)
                {
                    BuyerInfoDetails buyerInfoDetails = BUYER_DETAILS.get(i);

                    String is_block = buyerInfoDetails.getBuyerblockStatus();

                    if(is_block.equals("N"))
                    {
                        BUYER_DETAILS_FILTER.add(buyerInfoDetails);
                    }
                }
                buyerListAdapter = new BuyerListAdapter(context, animation_type,BUYER_DETAILS_FILTER,BuyerListActivity.this);
                recyclerView.setAdapter(buyerListAdapter);
                dialog.dismiss();
            }
            //rating
            else if(searchStatus.equals("rating"))
            {
                BUYER_DETAILS_FILTER.clear();
                for(int i = 0; i < BUYER_DETAILS.size(); i++)
                {
                    BuyerInfoDetails buyerInfoDetails = BUYER_DETAILS.get(i);

                    String rating = buyerInfoDetails.getBuyerRating();

                    if(Double.valueOf(rating) > 0)
                    {
                        BUYER_DETAILS_FILTER.add(buyerInfoDetails);
                    }
                }

                //order by id
                Collections.sort(BUYER_DETAILS_FILTER, BuyerInfoDetails.ratingHighTolow);
                buyerListAdapter = new BuyerListAdapter(context, animation_type,BUYER_DETAILS_FILTER,BuyerListActivity.this);
                recyclerView.setAdapter(buyerListAdapter);
                dialog.dismiss();
            }
            else
            {
                buyerListAdapter = new BuyerListAdapter(context, animation_type,BUYER_DETAILS,BuyerListActivity.this);
                recyclerView.setAdapter(buyerListAdapter);
                dialog.dismiss();
            }
        }
        catch (Exception e)
        {
            T.e("Exception : "+e);
            e.printStackTrace();
        }
    }

    private void FilterData(final String strFilter, final String strType) {                // in this "strType" and "strFilter" variable user select filter type that value set in this variable
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
