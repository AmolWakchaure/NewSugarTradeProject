package com.prosolstech.sugartrade.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.prosolstech.sugartrade.ConfirmationDialog;
import com.prosolstech.sugartrade.DialogListner;
import com.prosolstech.sugartrade.R;
import com.prosolstech.sugartrade.activity.BuySellDashBoardActivity;
import com.prosolstech.sugartrade.adapter.BuyBidAdapter;
import com.prosolstech.sugartrade.adapter.BuyBidAdapterTestTimer;
import com.prosolstech.sugartrade.adapter.BuyerListAdapter;
import com.prosolstech.sugartrade.classes.Constants;
import com.prosolstech.sugartrade.classes.T;
import com.prosolstech.sugartrade.database.DataBaseConstants;
import com.prosolstech.sugartrade.database.DataBaseHelper;
import com.prosolstech.sugartrade.model.BuyBidModel;
import com.prosolstech.sugartrade.model.BuyerInfoDetails;
import com.prosolstech.sugartrade.model.SellBidModel;
import com.prosolstech.sugartrade.util.ACU;
import com.prosolstech.sugartrade.util.ItemAnimation;
import com.prosolstech.sugartrade.util.VU;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SellBidFragment extends Fragment {

    Context context;
    private RecyclerView recyclerView;
    EditText autoTxtSearch;
    ImageButton imgSearch, imgFilter;
    private int animation_type = ItemAnimation.BOTTOM_UP;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ArrayList<String> listBuyerName;
    ArrayList<String> listCategory;
    ArrayList<String> listCatId;
    ArrayList<String> listSeason;
    ArrayList<String> listSeasonId;
    ArrayList<String> listDistrictName;
    ArrayList<String> listDistrictID;
    String strCatID = "", strSeason = "", strRadioButton = "", strDistrictName = "", strDistrictId = "";
    RadioButton rbOne, rbTwo, rbThree, rbFour, rbFive;
    Spinner spnGrade, spnSeason, spnState, spnDistrict;
    Button btnSave, btnCancel;
    Dialog dialog;
    LinearLayout llState,hideLayout;

    String gradeCategory;
    private boolean _hasLoadedOnce = false; // your boolean field

    ArrayList<BuyBidModel> listBuyModel = new ArrayList<>();
    ArrayList<BuyBidModel> listBuyModelFilter = new ArrayList<>();
    private long counter = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        View rootView = inflater.inflate(R.layout.fragment_all_buy_sell_bid, container, false);
        initializeUI(rootView);


        autoTxtSearch.addTextChangedListener(new TextWatcher()
        {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence newText, int start, int before, int count) {
                //M.t(""+s);

                if(!listBuyModel.isEmpty())
                {
                    searchDetails(newText.toString());
                }


            }
        });

        if (!ACU.MySP.getSPBoolean(context, ACU.MySP.FLAG_ACCEPT, false))
        {
            ConfirmationDialog confirmationDialog = new ConfirmationDialog(getContext(), dialogListner);
            confirmationDialog.show();
        }
        else
        {

            if (VU.isConnectingToInternet(context))
            {
                Log.e("SELL_BID_FRAGMENT", " : ");
                BuySellDashBoardActivity.checkStatus = "view";
                sellBidData(context);
            }
        }

        return rootView;
    }
    private void searchDetails(String newText)
    {
        final ArrayList<BuyBidModel> filteredModelList = filter(listBuyModel, "" + newText);
        buyBidAdapter.setFilter(filteredModelList);
    }

    private ArrayList<BuyBidModel> filter(ArrayList<BuyBidModel> models, String query) {
       // query = query.toLowerCase().replace(" ","");
        final ArrayList<BuyBidModel> filteredModelList = new ArrayList<>();
        filteredModelList.clear();

        if (query.length() == 0)
        {
            filteredModelList.addAll(models);
        }
        else
        {
            for (BuyBidModel model : models)
            {


                final String id = model.getId();
               // final String bid_start_time = model.getBid_start_time();
                //final String validity_time = model.getValidity_time();
                final String category = model.getCategory().toLowerCase().replace(" ","");
                final String company_name = model.getCompany_name().toLowerCase();
                final String type = model.getType().toLowerCase().replace(" ","");
               // final String date = model.getDate();
               // final String time = model.getEndTime();
               // final String end_bid_time = model.getBidEndTime();
               // final String production_year = model.getProduction_year();


                //id
                if (id.contains(query)) {
                    filteredModelList.add(model);
                }

                //category
                else if (category.contains(query))
                {
                    filteredModelList.add(model);
                }
                //company_name
                else if (company_name.contains(query))
                {
                    filteredModelList.add(model);
                }
                //type
                else if (type.contains(query))
                {
                    filteredModelList.add(model);
                }

            }
        }


        return filteredModelList;
    }


    DialogListner dialogListner = new DialogListner()
    {
        @Override
        public void click()
        {
            ACU.MySP.setSPBoolean(context, ACU.MySP.FLAG_ACCEPT, true);
            if (VU.isConnectingToInternet(context))
            {
                sellBidData(context);
            }
        }
    };


    public void initializeUI(View rootView) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeToRefresh);

        imgFilter = (ImageButton) rootView.findViewById(R.id.AllBidFragmentImgFilter);
        imgSearch = (ImageButton) rootView.findViewById(R.id.AllBidFragmentImgSearch);
        autoTxtSearch = (EditText) rootView.findViewById(R.id.AllBidFragmentAutoTexViewSearch);
        hideLayout = (LinearLayout) rootView.findViewById(R.id.hideLayout);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.AllBidFragmentRecyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 1));
        recyclerView.setHasFixedSize(true);
        animation_type = ItemAnimation.FADE_IN;

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (VU.isConnectingToInternet(context)) {
                    sellBidData(context);
                }
                mSwipeRefreshLayout.setRefreshing(false);
                autoTxtSearch.setText("");
            }
        });

        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (Validate())
                {
                    searchData();
                }
            }
        });
        imgFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog();
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

    private void sellBidData(Context ctx) {
        String url = "";
        final ProgressDialog pDialog = new ProgressDialog(ctx);
        pDialog.setMessage("Please wait while data fetch from server...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(context);


        url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/getSellBidsByRole";      //for server


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Log.e("sellBidData", " RESPONSE " + response);
                        pDialog.dismiss();
                        setListAdapter(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("No Response", "FOUND");
                pDialog.dismiss();
                if (listBuyModel.size() > 0) {
                    listBuyModel.clear();
                    recyclerView.setVisibility(View.GONE);
                }
                Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("role", ACU.MySP.getFromSP(context, ACU.MySP.ROLE, ""));
                params.put("userby", ACU.MySP.getFromSP(context, ACU.MySP.ID, ""));
                T.e("params : "+params);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    JSONArray array;
    BuyBidAdapterTestTimer buyBidAdapter;
    private void setListAdapter(String result) {

        String id = Constants.NA;
        String bid_start_time = Constants.NA;
        String CategoryName = Constants.NA;
        String company_name = Constants.NA;
        String required_qty = Constants.NA;
        String price_per_qtl = Constants.NA;
        String is_favorite = Constants.NA;
        String created_date = Constants.NA;
        String validity_time = Constants.NA;
        String bid_end_time = Constants.NA;
        String curr_req_qty = Constants.NA;
        String acquired_qty = Constants.NA;
        String type = Constants.NA;
        String production_year = Constants.NA;

        try {
            //JSONArray array;
            Log.e("setListAdapter", " RESULT " + result.trim());
            array = new JSONArray(result);



            if (array != null && array.length() > 0)
            {


                if (listBuyModel.size() > 0) {
                    listBuyModel.clear();
                }

                for (int i = 0; i < array.length(); i++)
                {
                    JSONObject jsonObject = array.getJSONObject(i);

                    if(jsonObject.has("id") && !jsonObject.isNull("id"))
                    {
                        id = jsonObject.getString("id");
                    }
                    if(jsonObject.has("bid_start_time") && !jsonObject.isNull("bid_start_time"))
                    {
                        bid_start_time = jsonObject.getString("bid_start_time");
                    }
                    if(jsonObject.has("CategoryName") && !jsonObject.isNull("CategoryName"))
                    {
                        CategoryName = jsonObject.getString("CategoryName");
                    }
                    if(jsonObject.has("company_name") && !jsonObject.isNull("company_name"))
                    {
                        company_name = jsonObject.getString("company_name");
                    }
                    if(jsonObject.has("required_qty") && !jsonObject.isNull("required_qty"))
                    {
                        required_qty = jsonObject.getString("required_qty");
                    }
                    if(jsonObject.has("price_per_qtl") && !jsonObject.isNull("price_per_qtl"))
                    {
                        price_per_qtl = jsonObject.getString("price_per_qtl");
                    }
                    if(jsonObject.has("is_favorite") && !jsonObject.isNull("is_favorite"))
                    {
                        is_favorite = jsonObject.getString("is_favorite");
                    }
                    if(jsonObject.has("created_date") && !jsonObject.isNull("created_date"))
                    {
                        created_date = jsonObject.getString("created_date");
                    }
                    if(jsonObject.has("validity_time") && !jsonObject.isNull("validity_time"))
                    {
                        validity_time = jsonObject.getString("validity_time");
                    }
                    if(jsonObject.has("bid_end_time") && !jsonObject.isNull("bid_end_time"))
                    {
                        bid_end_time = jsonObject.getString("bid_end_time");
                    }
                    if(jsonObject.has("curr_req_qty") && !jsonObject.isNull("curr_req_qty"))
                    {
                        curr_req_qty = jsonObject.getString("curr_req_qty");
                    }
                    if(jsonObject.has("acquired_qty") && !jsonObject.isNull("acquired_qty"))
                    {
                        acquired_qty = jsonObject.getString("acquired_qty");
                    }
                    if(jsonObject.has("type") && !jsonObject.isNull("type"))
                    {
                        type = jsonObject.getString("type");
                    }
                    if(jsonObject.has("production_year") && !jsonObject.isNull("production_year"))
                    {
                        production_year = jsonObject.getString("production_year");
                    }

                    BuyBidModel buyBidModel = new BuyBidModel();

                    buyBidModel.setId(id);
                    buyBidModel.setBid_start_time(bid_start_time);
                    buyBidModel.setCategory(CategoryName);
                    buyBidModel.setCompany_name(company_name);
                    buyBidModel.setAvailable_qty(required_qty);
                    buyBidModel.setPrice_per_qtl(price_per_qtl);
                    buyBidModel.setIs_favorite(is_favorite);
                    buyBidModel.setDate(created_date);
                    buyBidModel.setValidity_time(validity_time);
                    buyBidModel.setEndTime("0");
                    buyBidModel.setIsTimerTuuning("1");
                    buyBidModel.setBidEndTime(bid_end_time);
                    buyBidModel.setCurrent_req_quantity(curr_req_qty);
                    buyBidModel.setAcquired_quantity(acquired_qty);
                    buyBidModel.setIs_delelted("0");
                    buyBidModel.setType(type);
                    buyBidModel.setProduction_year(production_year);

                    listBuyModel.add(buyBidModel);
                    boolean is = DataBaseHelper.DBBuyBidData.BuyBidDatainsert(buyBidModel);

                    /*String status = T.returnSeconds(bid_end_time);

                    if(status.equals("start"))
                    {

                    }*/






                }
                if(listBuyModel.isEmpty())
                {

                    T.t("Oops ! Buy offers not found");

                    hideLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    buyBidAdapter = new BuyBidAdapterTestTimer(context, array, animation_type, listBuyModel, refreshListner);    // if user login as a "Seller" than show Buyer offer list.
                    recyclerView.setAdapter(buyBidAdapter);
                    buyBidAdapter.notifyDataSetChanged();
                }
                else
                {

                    hideLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    buyBidAdapter = new BuyBidAdapterTestTimer(context, array, animation_type, listBuyModel, refreshListner);    // if user login as a "Seller" than show Buyer offer list.
                    recyclerView.setAdapter(buyBidAdapter);
                    buyBidAdapter.notifyDataSetChanged();
                }

            }
            else
            {
                //recyclerView.setVisibility(View.GONE);
                Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
            }
        }
        catch (JSONException e)
        {
            T.t(""+e);
            e.printStackTrace();
        }

    }

    BuyBidAdapterTestTimer.RefreshListner refreshListner = new BuyBidAdapterTestTimer.RefreshListner() {


        @Override
        public void refresh(Context ctx) {

            if (VU.isConnectingToInternet(ctx)) {
                Log.e("SELL_BID_FRAGMENT", " : ");
                BuySellDashBoardActivity.checkStatus = "view";
                sellBidData(ctx);
            }
        }
    };


    /*private void BuyerNames() {                  // if login as seller than all buyer names list show
        String url = "";
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait while data fetch from server...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(context);


        url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/getCompanyNames";      //for server
        Log.e("BuyerNamesURL", " ....." + url);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Log.e("BuyerNames", " RESPONSE " + response);
                        pDialog.dismiss();
                        setBuyerNames(response);
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
                return params;
            }
        };
        queue.add(stringRequest);
    }*/

    /*private void setBuyerNames(String result) {
        try {
            JSONArray array;
            ;
            Log.e("setBuyerNames", " RESULT " + result.trim());
            array = new JSONArray(result);
            if (array != null && array.length() > 0) {
                try {
                    listBuyerName = new ArrayList<>();

                    if (array != null && array.length() > 0) {
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            listBuyerName.add(object.getString("company_name"));
                            Log.e("company_name", " " + object.getString("company_name"));
                        }
                        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, listBuyerName);
                        autoTxtSearch.setThreshold(1);
                        autoTxtSearch.setAdapter(adapter);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/

    private void searchData() {                  // if login as seller than all buyer offer show
        String url = "";
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait while data fetch from server...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(context);


        url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/getBidByCompany";      //for server
        Log.e("searchDataURL", " ....." + url);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Log.e("searchData", " RESPONSE " + response);
                        pDialog.dismiss();
                        recyclerView.setVisibility(View.VISIBLE);
                        setListAdapter(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("No Response", "FOUND");
                pDialog.dismiss();
                //recyclerView.setVisibility(View.GONE);
                Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("role", ACU.MySP.getFromSP(context, ACU.MySP.ROLE, ""));
                params.put("company", autoTxtSearch.getText().toString().trim());

                Log.e("searchData_PARAM", " : " + params.toString());
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
        dialog.setContentView(R.layout.filter_list);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        // if button is clicked, close the custom dialog

        rbOne = (RadioButton) dialog.findViewById(R.id.RadioButtonOne);
        rbTwo = (RadioButton) dialog.findViewById(R.id.RadioButtonTwo);
        rbThree = (RadioButton) dialog.findViewById(R.id.RadioButtonThree);
        rbFour = (RadioButton) dialog.findViewById(R.id.RadioButtonFour);
        rbFive = (RadioButton) dialog.findViewById(R.id.RadioButtonFive);

        spnGrade = (Spinner) dialog.findViewById(R.id.FilterDialogSpnGrade);
        spnSeason = (Spinner) dialog.findViewById(R.id.FilterDialogSpnSeason);
        spnState = (Spinner) dialog.findViewById(R.id.FilterDialogSpnState);
        spnDistrict = (Spinner) dialog.findViewById(R.id.FilterDialogSpnDistrict);
        llState = (LinearLayout) dialog.findViewById(R.id.FilterDialogLinearState);


        btnSave = (Button) dialog.findViewById(R.id.FilterDialogBtnSave);
        btnCancel = (Button) dialog.findViewById(R.id.FilterDialogBtnCancel);

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
        adpCategory = new ArrayAdapter<String>(context, R.layout.simple_item, listCategory);
        spnGrade.setAdapter(adpCategory);

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
        adpSeason = new ArrayAdapter<String>(context, R.layout.simple_item, listSeason);
        spnSeason.setAdapter(adpSeason);

        Cursor curDistrict = DataBaseHelper.DistrictDB.getDistrictName();
        Log.e("curDistrict_SELL", " : " + curDistrict.getCount());
        try {
            listDistrictName.add(0, "Select District");
            Log.e("Dis_DBDistrict_SELL", " : " + listDistrictName.toString());
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
        spnGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strCatID = listCatId.get(position);
                Log.e("strCatID", " " + strCatID);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spnSeason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strSeason = listSeason.get(position);
                Log.e("strSeason", " " + strSeason);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!strRadioButton.equalsIgnoreCase(""))
                {
                    if (strRadioButton.equalsIgnoreCase("Grade"))
                    {
                       // T.t(strCatID);
                        gradeCategory = spnGrade.getSelectedItem().toString();
                        filterData("Grade");
                        //FilterData("grade", strCatID);
                    }
                    else if (strRadioButton.equalsIgnoreCase("Season"))
                    {
                        gradeCategory = spnSeason.getSelectedItem().toString();
                        filterData("Season");
                        //FilterData("season", strSeason);
                    }
                    else if (strRadioButton.equalsIgnoreCase("State And District"))
                    {
                        FilterData("state_and_district", strDistrictId);
                    }
                    else if (strRadioButton.equalsIgnoreCase("Price Low to high"))
                    {
                        Collections.sort(listBuyModel, BuyBidModel.priceLowTohigh);
                        recyclerView.setAdapter(buyBidAdapter);
                        buyBidAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                        //FilterData("low_to_high", "");
                    }
                    else if (strRadioButton.equalsIgnoreCase("Price High to Low"))
                    {
                        Collections.sort(listBuyModel, BuyBidModel.priceHighTolow);
                        recyclerView.setAdapter(buyBidAdapter);
                        buyBidAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                        //FilterData("high_to_low", "");
                    }
                }
                else
                {
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
                rbFive.setChecked(false);
                spnSeason.setVisibility(View.GONE);
                spnGrade.setVisibility(View.VISIBLE);
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
                rbFive.setChecked(false);
                spnSeason.setVisibility(View.VISIBLE);
                spnGrade.setVisibility(View.GONE);
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
                rbFive.setChecked(false);
                spnGrade.setVisibility(View.GONE);
                spnSeason.setVisibility(View.GONE);
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
                rbFive.setChecked(false);
                spnGrade.setVisibility(View.GONE);
                spnSeason.setVisibility(View.GONE);
                llState.setVisibility(View.GONE);
            }
        });
        rbFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strRadioButton = rbFive.getText().toString();
                rbOne.setChecked(false);
                rbTwo.setChecked(false);
                rbThree.setChecked(false);
                rbFour.setChecked(false);
                spnGrade.setVisibility(View.GONE);
                spnSeason.setVisibility(View.GONE);
                llState.setVisibility(View.GONE);
            }
        });
        dialog.show();
    }
    private void filterData(String searchStatus) {

        try
        {
            JSONArray jsonArray = null;
            //Grade
            if(searchStatus.equals("Grade"))
            {
                listBuyModelFilter.clear();

                jsonArray = new JSONArray();
                for(int i = 0; i < listBuyModel.size(); i++)
                {
                    BuyBidModel buyerInfoDetails = listBuyModel.get(i);

                    String gradeCat = buyerInfoDetails.getCategory();

                    if(gradeCat.equals(gradeCategory))
                    {
                        listBuyModelFilter.add(buyerInfoDetails);
                        jsonArray.put(array.getJSONObject(i));
                    }
                }
                T.e("jsonArray : "+jsonArray.toString());
                buyBidAdapter = new BuyBidAdapterTestTimer(context,jsonArray, animation_type,listBuyModelFilter,refreshListner);
                recyclerView.setAdapter(buyBidAdapter);
                dialog.dismiss();
            }
            //Season
            else if(searchStatus.equals("Season"))
            {
                listBuyModelFilter.clear();
                jsonArray = new JSONArray();
                int arrayListPos = 0;
                for(int i = 0; i < listBuyModel.size(); i++)
                {
                    BuyBidModel buyerInfoDetails = listBuyModel.get(i);

                    String production_year = buyerInfoDetails.getProduction_year();

                    if(production_year.equals(gradeCategory))
                    {
                        listBuyModelFilter.add(buyerInfoDetails);
                        jsonArray.put(array.getJSONObject(i));
                    }
                }
                buyBidAdapter = new BuyBidAdapterTestTimer(context,jsonArray, animation_type,listBuyModelFilter,refreshListner);
                recyclerView.setAdapter(buyBidAdapter);
                dialog.dismiss();
            }
            else
            {
                buyBidAdapter = new BuyBidAdapterTestTimer(context,array, animation_type,listBuyModelFilter,refreshListner);
                recyclerView.setAdapter(buyBidAdapter);
                dialog.dismiss();
            }
        }
        catch (Exception e)
        {
            T.e("Exception : "+e);
            e.printStackTrace();
        }
    }

    private void FilterData(final String strType, final String strFilter) {                  // in this "strType" and "strFilter" variable user select filter type that value set in this variable
        String url = "";
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait while data fetch from server...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(context);


        url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/getFilteredData";      //for server
        Log.e("FilterData", " ....." + url);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Log.e("FilterData", " RESPONSE " + response);
                        pDialog.dismiss();
                        dialog.dismiss();
                        recyclerView.setVisibility(View.VISIBLE);
                        setListAdapter(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("No Response", "FOUND");
                pDialog.dismiss();
                dialog.dismiss();
                //recyclerView.setVisibility(View.GONE);
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

    @Override
    public void setUserVisibleHint(boolean isFragmentVisible_) {
        super.setUserVisibleHint(true);
        if (this.isVisible()) {
            if (isFragmentVisible_ && isResumed()) {


                if (VU.isConnectingToInternet(context)) {
                    Log.e("SELL_BID_FRAGMENT", " : ");
                    BuySellDashBoardActivity.checkStatus = "view";
                    sellBidData(context);
                }
                _hasLoadedOnce = true;


            }
        }
    }
}

