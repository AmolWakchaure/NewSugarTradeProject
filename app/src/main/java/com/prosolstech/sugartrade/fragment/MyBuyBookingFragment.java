package com.prosolstech.sugartrade.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.prosolstech.sugartrade.R;
import com.prosolstech.sugartrade.adapter.MyAllBookingBuyAdapter;
import com.prosolstech.sugartrade.util.ACU;
import com.prosolstech.sugartrade.util.ItemAnimation;
import com.prosolstech.sugartrade.util.VU;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class MyBuyBookingFragment extends Fragment {

    Context context;
    View rootView;
    private RecyclerView recyclerView;
    private MyAllBookingBuyAdapter allBookingBuyAdapter;
    private int animation_type = ItemAnimation.BOTTOM_UP;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean _hasLoadedOnce = false; // your boolean field
    private TabLayout tabLayout;
    RadioGroup radioGroup;
    RadioButton rbBuy, rbSell;
    public static final String BUYER = "buy";
    public static final String SELLER = "sell";
    String typeOne = "";

    public MyBuyBookingFragment(TabLayout tabLayout) {
        this.tabLayout = tabLayout;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        rootView = inflater.inflate(R.layout.fragment_my_all_booking, container, false);
        initializeUI(rootView);
//        Log.e("MyBuyBookingFragment", " : ");

        if (ACU.MySP.getFromSP(context, ACU.MySP.ROLE, "").equalsIgnoreCase("Buyer"))
        {
            if (VU.isConnectingToInternet(context))
            {
                MyBuyBooking(BUYER);
            }
            rbBuy.setChecked(true);
            rbSell.setChecked(false);
        }
        else
        {
            rbBuy.setChecked(false);
            rbSell.setChecked(true);

            if (VU.isConnectingToInternet(context))
            {
                MyBuyBooking(SELLER);
            }

        }


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {


            @Override

            public void onCheckedChanged(RadioGroup group, int checkedId) {

                // find which radio button is selected

                if (checkedId == R.id.buy_rg) {
                    typeOne = "buy";
                    if (VU.isConnectingToInternet(context)) {
                        MyBuyBooking(BUYER);
                    }
                } else if (checkedId == R.id.sell_rg) {
                    typeOne = "sell";
                    if (VU.isConnectingToInternet(context)) {
                        MyBuyBooking(SELLER);
                    }
                }

            }

        });


        return rootView;
    }


    public void initializeUI(View rootView) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.MyBookingSwipeToRefresh);

        radioGroup = (RadioGroup) rootView.findViewById(R.id.radio_grp);
        rbBuy = (RadioButton) rootView.findViewById(R.id.buy_rg);
        rbSell = (RadioButton) rootView.findViewById(R.id.sell_rg);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.MyAllBookingFragmentRecyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 1));
        recyclerView.setHasFixedSize(true);
        animation_type = ItemAnimation.FADE_IN;

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (typeOne.equalsIgnoreCase("buy")) {

                    rbBuy.setChecked(true);
                    rbSell.setChecked(false);
                    if (VU.isConnectingToInternet(context)) {
                        MyBuyBooking(BUYER);
                    }
                } else {

                    rbBuy.setChecked(false);
                    rbSell.setChecked(true);

                    if (VU.isConnectingToInternet(context)) {
                        MyBuyBooking(SELLER);
                    }
                }

                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    private void MyBuyBooking(final String type) {
        String url = "";
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait while data fetch from server...");
        pDialog.setCancelable(false);
        pDialog.show();
        RequestQueue queue = Volley.newRequestQueue(context);
        url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/getMySellBooking";
        Log.e("MY_BOOK_BUY_URL", " ....." + url);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Log.e("MyBuyBooking", " RESPONSE " + response);
                        pDialog.dismiss();
                        recyclerView.setVisibility(View.VISIBLE);
                        setListAdapter(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("No Response", "FOUND");
                pDialog.dismiss();
                recyclerView.setVisibility(View.GONE);
                Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", ACU.MySP.getFromSP(context, ACU.MySP.ID, ""));
                params.put("role", ACU.MySP.getFromSP(context, ACU.MySP.ROLE, ""));
                params.put("type", type);
                Log.e("MY_BOOK_BUY", " : " + params.toString());//{type=buy, id=11, role=Buyer}
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private void setListAdapter(String result) {
        try {
            JSONArray array;
            Log.e("My_Buy_Booking_ListAda", " RESULT " + result.trim());
            array = new JSONArray(result);
            if (array != null && array.length() > 0) {


                recyclerView.setVisibility(View.VISIBLE);
                allBookingBuyAdapter = new MyAllBookingBuyAdapter(getActivity(), array, animation_type);
                recyclerView.setAdapter(allBookingBuyAdapter);
            } else {
                recyclerView.setVisibility(View.GONE);
                Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isFragmentVisible_) {
        super.setUserVisibleHint(true);
        if (this.isVisible()) {
            // we check that the fragment is becoming visible
            if (isFragmentVisible_ && isResumed()) {
//            if (isFragmentVisible_ && !_hasLoadedOnce) {
                tabLayout.getTabAt(1).setText("My Post");

//                Log.e("BuyBidFragment", tabLayout.getSelectedTabPosition()+"");


                if (ACU.MySP.getFromSP(context, ACU.MySP.ROLE, "").equalsIgnoreCase("Buyer")) {
                    if (VU.isConnectingToInternet(context)) {
                        MyBuyBooking(BUYER);
                    }
                    rbBuy.setChecked(true);
                    rbSell.setChecked(false);
                } else {
                    rbBuy.setChecked(false);
                    rbSell.setChecked(true);

                    if (VU.isConnectingToInternet(context)) {
                        MyBuyBooking(SELLER);
                    }

                }


//                rbBuy.setChecked(false);
//                rbSell.setChecked(false);
//                if (VU.isConnectingToInternet(context)) {
//                    MyBuyBooking(BUYER);
//                }
                _hasLoadedOnce = true;
            }
        }
    }
}
