package com.prosolstech.sugartrade.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
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
import android.widget.TextView;
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
import com.prosolstech.sugartrade.activity.BuyerListActivity;
import com.prosolstech.sugartrade.activity.PlaceBuyBidActivity;
import com.prosolstech.sugartrade.activity.PlaceSellBidActivity;
import com.prosolstech.sugartrade.adapter.BuyBidAdapter;
import com.prosolstech.sugartrade.adapter.BuyBidAdapterTestTimer;
import com.prosolstech.sugartrade.adapter.SellBidAdapter;
import com.prosolstech.sugartrade.adapter.SellBidAdapterTest;
import com.prosolstech.sugartrade.adapter.SellBidAdapterTestTimer;
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
import java.util.Timer;


public class BuyBidFragment extends Fragment {

    private Context context;
    private View rootView;
    private RecyclerView recyclerView;
    private int animation_type = ItemAnimation.BOTTOM_UP;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private EditText autoTxtSearch;
    private ImageButton imgSearch, imgFilter;
    private ArrayList<String> listSellerName;
    private ArrayList<String> listCategory;
    private ArrayList<String> listCatId;
    private ArrayList<String> listSeason;
    private ArrayList<String> listSeasonId;
    private ArrayList<String> listDistrictName;
    private ArrayList<String> listDistrictID;
    private String gradeCategory,strCatID = "", strSeason = "", strRadioButton = "", strDistrictName = "", strDistrictId = "";
    private RadioButton rbOne, rbTwo, rbThree, rbFour, rbFive;
    private Spinner spnGrade, spnSeason, spnState, spnDistrict;
    private Button btnSave, btnCancel;
    private Dialog dialog;
    private LinearLayout llState,hideLayout;
    private boolean _hasLoadedOnce = false; // your boolean field
    private ArrayList<SellBidModel> listSellModel = new ArrayList<>();
    private ArrayList<SellBidModel> listSellModelFilter = new ArrayList<>();
    private int counter = 1;
    private Timer timer;

    private TabLayout tabLayout;
    public BuyBidFragment(TabLayout tabLayout) {
        this.tabLayout=tabLayout;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        rootView = inflater.inflate(R.layout.fragment_all_buy_sell_bid, container, false);
        initializeUI(rootView);


        getSellOffersData();

        autoTxtSearch.addTextChangedListener(new TextWatcher()
        {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence newText, int start, int before, int count) {
                //M.t(""+s);

                if(!listSellModel.isEmpty())
                {
                    final ArrayList<SellBidModel> filteredModelList = filter(listSellModel, "" + newText);
                    sellBidAdapter.setFilter(filteredModelList);
                }


            }
        });

        return rootView;
    }


    private ArrayList<SellBidModel> filter(ArrayList<SellBidModel> models, String query) {

        final ArrayList<SellBidModel> filteredModelList = new ArrayList<>();
        filteredModelList.clear();

        if (query.length() == 0)
        {
            filteredModelList.addAll(models);
        }
        else
        {
            for (SellBidModel model : models)
            {


                final String id = model.getId();
                final String category = model.getCategory().toLowerCase().replace(" ","");
                final String company_name = model.getCompany_name().toLowerCase();
                final String type = model.getType().toLowerCase().replace(" ","");

                //type
                if (type.contains(query.toLowerCase().replace(" ","")))
                {
                    filteredModelList.add(model);
                }
                //id
                else if (id.contains(query)) {
                    filteredModelList.add(model);
                }
                //category
                else if (category.contains(query.toLowerCase().replace(" ","")))
                {
                    filteredModelList.add(model);
                }
                //company_name
                else if (company_name.contains(query.toLowerCase().replace(" ","")))
                {
                    filteredModelList.add(model);
                }
            }
        }


        return filteredModelList;
    }

    private void getSellOffersData() {

        if (!ACU.MySP.getSPBoolean(context, ACU.MySP.FLAG_ACCEPT, false))
        {

            ConfirmationDialog confirmationDialog = new ConfirmationDialog(getContext(), dialogListner);
            confirmationDialog.show();
        }
        else
        {
            if (VU.isConnectingToInternet(context))
            {
                Log.e("Buy_BID_FRAGMENT", " : ");
                buyBidData(context);
            }

        }
    }

    DialogListner dialogListner = new DialogListner() {
        @Override
        public void click() {
            ACU.MySP.setSPBoolean(context, ACU.MySP.FLAG_ACCEPT, true);

            if (VU.isConnectingToInternet(context)) {
                buyBidData(context);
                //SellerNames();
            }
        }
    };


    public void initializeUI(View rootView) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeToRefresh);
        imgFilter = (ImageButton) rootView.findViewById(R.id.AllBidFragmentImgFilter);
        imgSearch = (ImageButton) rootView.findViewById(R.id.AllBidFragmentImgSearch);
        autoTxtSearch = (EditText) rootView.findViewById(R.id.AllBidFragmentAutoTexViewSearch);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.AllBidFragmentRecyclerview);
        hideLayout = (LinearLayout) rootView.findViewById(R.id.hideLayout);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 1));
        recyclerView.setHasFixedSize(true);
        animation_type = ItemAnimation.FADE_IN;

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (VU.isConnectingToInternet(context)) {
                    buyBidData(context);
                }
                mSwipeRefreshLayout.setRefreshing(false);
                autoTxtSearch.setText("");
            }
        });

        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Validate()) {
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

    private void buyBidData(final Context context) {
        String url = "";
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait while data fetch from server...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(context);
        url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/getSellBidsByRole";      //for server
        Log.e("BUYER_BothURL", " ....." + url);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Log.e("buyBidData", " RESPONSE " + response);
                        pDialog.dismiss();
                        recyclerView.setVisibility(View.VISIBLE);
                        setListAdapter(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("No Response", "FOUND");
                pDialog.dismiss();

                refreshList();
                //recyclerView.setVisibility(View.GONE);

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("role", ACU.MySP.getFromSP(context, ACU.MySP.ROLE, ""));
                params.put("userby", ACU.MySP.getFromSP(context, ACU.MySP.ID, ""));
                Log.e("BUYER_PARMAS", " ..... " + params.toString());

                return params;
            }
        };
        queue.add(stringRequest);
    }
    JSONArray array;
    private void setListAdapter(String result) {

        String id = Constants.NA;
        String bid_start_time = Constants.NA;
        String CategoryName = Constants.NA;
        String company_name = Constants.NA;
        String price_per_qtl = Constants.NA;
        String is_favorite = Constants.NA;
        String created_date = Constants.NA;
        String validity_time = Constants.NA;
        String time = Constants.NA;
        String type = Constants.NA;
        String production_year = Constants.NA;
        String available_qty = Constants.NA;
        String claimed = Constants.NA;
        String is_interested = Constants.NA;
        String original_qty = Constants.NA;
        String bid_end_time = Constants.NA;

        try {

            if(result.length() > 0 && result != null)
            {
                //JSONArray array;
                Log.e("setListAdapter", " RESULT " + result.trim());
                array = new JSONArray(result);
                Log.e("setListAdapter", " RESULT " + array.length() + "");



                if (array != null && array.length() > 0)
                {

                    hideLayout.setVisibility(View.VISIBLE);

                    if(!listSellModel.isEmpty())
                    {
                        listSellModel.clear();
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
                        if(jsonObject.has("validity_time") && !jsonObject.isNull("validity_time"))
                        {
                            validity_time = jsonObject.getString("validity_time");
                        }
                        if(jsonObject.has("CategoryName") && !jsonObject.isNull("CategoryName"))
                        {
                            CategoryName = jsonObject.getString("CategoryName");
                        }
                        if(jsonObject.has("company_name") && !jsonObject.isNull("company_name"))
                        {
                            company_name = jsonObject.getString("company_name");
                        }
                        if(jsonObject.has("available_qty") && !jsonObject.isNull("available_qty"))
                        {
                            available_qty = jsonObject.getString("available_qty");
                        }
                        if(jsonObject.has("claimed") && !jsonObject.isNull("claimed"))
                        {
                            claimed = jsonObject.getString("claimed");
                        }
                        if(jsonObject.has("price_per_qtl") && !jsonObject.isNull("price_per_qtl"))
                        {
                            price_per_qtl = jsonObject.getString("price_per_qtl");
                        }
                        if(jsonObject.has("type") && !jsonObject.isNull("type"))
                        {
                            type = jsonObject.getString("type");
                        }
                        if(jsonObject.has("is_interested") && !jsonObject.isNull("is_interested"))
                        {
                            is_interested = jsonObject.getString("is_interested");
                        }
                        if(jsonObject.has("original_qty") && !jsonObject.isNull("original_qty"))
                        {
                            original_qty = jsonObject.getString("original_qty");
                        }
                        if(jsonObject.has("is_favorite") && !jsonObject.isNull("is_favorite"))
                        {
                            is_favorite = jsonObject.getString("is_favorite");
                        }
                        if(jsonObject.has("created_date") && !jsonObject.isNull("created_date"))
                        {
                            created_date = jsonObject.getString("created_date");
                        }
                        if(jsonObject.has("time") && !jsonObject.isNull("time"))
                        {
                            time = jsonObject.getString("time");
                        }
                        if(jsonObject.has("bid_end_time") && !jsonObject.isNull("bid_end_time"))
                        {
                            bid_end_time = jsonObject.getString("bid_end_time");
                        }
                        if(jsonObject.has("production_year") && !jsonObject.isNull("production_year"))
                        {
                            production_year = jsonObject.getString("production_year");
                        }

                        SellBidModel sellBidModel = new SellBidModel();
                        sellBidModel.setId(id);
                        sellBidModel.setBid_start_time(bid_start_time);
                        sellBidModel.setValidity_time(validity_time);
                        sellBidModel.setEndtime("0");
                        sellBidModel.setIsTimerRunning("1");
                        sellBidModel.setCategory(CategoryName);
                        sellBidModel.setCompany_name(company_name);
                        sellBidModel.setAvailable_qty(available_qty);
                        sellBidModel.setClaimed(claimed);
                        sellBidModel.setPrice_per_qtl(price_per_qtl);
                        sellBidModel.setType(type);
                        sellBidModel.setIsIntrested(is_interested);
                        sellBidModel.setOriginal_qty(original_qty);
                        sellBidModel.setIs_favorite(is_favorite);
                        sellBidModel.setDate(created_date);
                        sellBidModel.setTime(time);
                        sellBidModel.setEnd_bid_time(bid_end_time);
                        sellBidModel.setIs_deleted("0");
                        sellBidModel.setProduction_year(production_year);
                        listSellModel.add(sellBidModel);

                        boolean is = DataBaseHelper.DBSellBidData.SellBidDatainsert(sellBidModel);
                        /*String status = T.returnSeconds(bid_end_time);

                        if(status.equals("start"))
                        {

                        }*/





                    }
                    //refresh sell offers list
                    refreshList();
                }
                else
                {
                    //recyclerView.setVisibility(View.GONE);
                    Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                T.t("Oops ! Sell Offers not found");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    SellBidAdapterTestTimer sellBidAdapter;

    private void refreshList()
    {

        if(listSellModel.isEmpty())
        {
            Toast.makeText(context, "Oops ! Sell Offers not found", Toast.LENGTH_SHORT).show();
            Log.e("COUNT_VALUE: ", DataBaseHelper.DBSellBidData.getCountOfSellData() + "");
            recyclerView.setVisibility(View.VISIBLE);
            sellBidAdapter = new SellBidAdapterTestTimer(getActivity(), array, animation_type, listSellModel, listner);         // if user login as a "Buyer" than show Seller offer ist
            recyclerView.setAdapter(sellBidAdapter);
            sellBidAdapter.notifyDataSetChanged();
        }
        else
        {
            Log.e("COUNT_VALUE: ", DataBaseHelper.DBSellBidData.getCountOfSellData() + "");
            recyclerView.setVisibility(View.VISIBLE);
            sellBidAdapter = new SellBidAdapterTestTimer(getActivity(), array, animation_type, listSellModel, listner);         // if user login as a "Buyer" than show Seller offer ist
            recyclerView.setAdapter(sellBidAdapter);
            sellBidAdapter.notifyDataSetChanged();
        }


    }

    SellBidAdapterTestTimer.RefreshListner listner = new SellBidAdapterTestTimer.RefreshListner() {
        @Override
        public void refresh(Context context) {

            if (VU.isConnectingToInternet(context)) {
                Log.e("Buy_BID_FRAGMENT", " : ");
                buyBidData(context);
            }

        }

        @Override
        public void refreshLoadedData(String bidId) {

            //when bid over remove specifi item from list
            T.e("Refresh static... position : "+bidId);

            if(!listSellModel.isEmpty())
            {
                for(int i = 0; i < listSellModel.size(); i++)
                {
                    SellBidModel sellBidModel = listSellModel.get(i);
                    String bidIdSearch = sellBidModel.getId();

                    if(bidIdSearch.equals(bidId))
                    {
                        listSellModel.remove(bidId);
                        refreshList();
                        break;
                    }
                }
            }
        }
    };

    private long convert(String val) {
        String arr[] = val.split(":");
        String hour = arr[0];
        String min = arr[1];
        return toMins(hour + ":" + min);


    }

    private static long toMins(String s) {
        String[] hourMin = s.split(":");
        long hour = Integer.parseInt(hourMin[0]);
        long mins = Integer.parseInt(hourMin[1]);
        long hoursInMins = hour * 60;
        return hoursInMins + mins;
    }

    private void SellerNames() {                  // if login as buyer than all seller names list show
        String url = "";
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait while data fetch from server...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(context);


        url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/getCompanyNames";      //for server
        Log.e("SellerNamesURL", " ....." + url);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Log.e("SellerNames", " RESPONSE " + response);
                        pDialog.dismiss();
                        //setSellerNames(response);
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
    }

    /*private void setSellerNames(String result) {
        try {
            JSONArray array;
            ;
            Log.e("setSellerNames", " RESULT " + result.trim());
            array = new JSONArray(result);
            if (array != null && array.length() > 0) {
                try {
                    listSellerName = new ArrayList<>();

                    if (array != null && array.length() > 0) {
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            listSellerName.add(object.getString("company_name"));
                            Log.e("company_name", " " + object.getString("company_name"));
                        }
                        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, listSellerName);
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

    private void searchData() {                  // if login as buyer than all seller offer show
        String url = "";
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait while data fetch from server...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(context);


        url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/getBidByCompany";      //for server

//        Log.e("searchDataURL", " ....." + url);


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
        spnGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                strCatID = listCatId.get(position);
                Log.e("strCatID", " " + strCatID);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        spnSeason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strSeason = listSeason.get(position);
                Log.e("strSeason", " " + strSeason);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (VU.isConnectingToInternet(context))
                {
                    if (!strRadioButton.equalsIgnoreCase(""))
                    {
                        if (strRadioButton.equalsIgnoreCase("Grade"))
                        {

                            //FilterData("grade", strCatID);
                            gradeCategory = spnGrade.getSelectedItem().toString();
                            filterData("Grade");
                        }
                        else if (strRadioButton.equalsIgnoreCase("Season"))
                        {
                            gradeCategory = spnSeason.getSelectedItem().toString();
                            filterData("Season");
                            //FilterData("season", strSeason);
                        }
                        else if (strRadioButton.equalsIgnoreCase("State And District"))
                        {
                            if (FilterValidate())
                            {
                                FilterData("state_and_district", strDistrictId);
                            }
                        }
                        else if (strRadioButton.equalsIgnoreCase("Price Low to high"))
                        {
                            Collections.sort(listSellModel, SellBidModel.priceLowTohigh);
                            recyclerView.setAdapter(sellBidAdapter);
                            sellBidAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                            //FilterData("low_to_high", "");
                        }
                        else if (strRadioButton.equalsIgnoreCase("Price High to Low"))
                        {
                            Collections.sort(listSellModel, SellBidModel.priceHighTolow);
                            recyclerView.setAdapter(sellBidAdapter);
                            sellBidAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                            //FilterData("high_to_low", "");
                        }
                    }
                    else
                    {
                        Toast.makeText(context, "Please select any filter", Toast.LENGTH_SHORT).show();
                    }
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
                listSellModelFilter.clear();
                jsonArray = new JSONArray();
                for(int i = 0; i < listSellModel.size(); i++)
                {
                    SellBidModel buyerInfoDetails = listSellModel.get(i);
                    String gradeCat = buyerInfoDetails.getCategory().replace(" ","").replace("-","");
                    if(gradeCat.equals(gradeCategory.replace(" ","").replace("-","")))
                    {

                        listSellModelFilter.add(buyerInfoDetails);
                        jsonArray.put(array.getJSONObject(i));
                    }
                }
               // T.t(""+listSellModelFilter.size());
                sellBidAdapter.setData(getActivity(),jsonArray, animation_type,listSellModelFilter,listner);
                recyclerView.setAdapter(sellBidAdapter);
                dialog.dismiss();
            }
            //Season
            else if(searchStatus.equals("Season"))
            {
                listSellModelFilter.clear();
                jsonArray = new JSONArray();
                for(int i = 0; i < listSellModel.size(); i++)
                {
                    SellBidModel buyerInfoDetails = listSellModel.get(i);

                    String production_year = buyerInfoDetails.getProduction_year();

                    if(production_year.equals(gradeCategory))
                    {
                        listSellModelFilter.add(buyerInfoDetails);
                        jsonArray.put(array.getJSONObject(i));
                    }
                }

                sellBidAdapter.setData(getActivity(),jsonArray, animation_type,listSellModelFilter,listner);
                recyclerView.setAdapter(sellBidAdapter);
                dialog.dismiss();
            }
            else
            {
                sellBidAdapter = new SellBidAdapterTestTimer(getActivity(),array, animation_type,listSellModel,listner);
                recyclerView.setAdapter(sellBidAdapter);
                dialog.dismiss();
            }
        }
        catch (Exception e)
        {
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
                Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
               // recyclerView.setVisibility(View.GONE);
            }
        })

        {
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

    public boolean FilterValidate() {
        if (spnState.getSelectedItemPosition() == 0) {
            Toast.makeText(context, "Please Select State & District", Toast.LENGTH_SHORT).show();
            return false;
        } else if (spnDistrict.getSelectedItemPosition() == 0) {
            Toast.makeText(context, "Please Select State & District", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void setUserVisibleHint(boolean isFragmentVisible_) {
        super.setUserVisibleHint(true);

        Log.e("BuyBidFragment", "setUserVisibleHint");

        if (this.isVisible()) {



            // we check that the fragment is becoming visible
            if (isFragmentVisible_ && isResumed()) {
//            if (isFragmentVisible_ && !_hasLoadedOnce) {

//                if (!ACU.MySP.getSPBoolean(context, ACU.MySP.FLAG_ACCEPT, false)) {
                if (VU.isConnectingToInternet(context)) {
                    Log.e("Buy_BID_FRAGMENT", " : ");
                    buyBidData(context);
                }
                _hasLoadedOnce = true;
//                }
            }
        }
    }
}

