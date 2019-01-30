package com.prosolstech.sugartrade.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.prosolstech.sugartrade.R;
import com.prosolstech.sugartrade.activity.PlaceBuyBidActivity;
import com.prosolstech.sugartrade.activity.PlaceSellBidActivity;
import com.prosolstech.sugartrade.adapter.BuyBidAdapter;
import com.prosolstech.sugartrade.adapter.MySellOfferAdapter;
import com.prosolstech.sugartrade.util.ACU;
import com.prosolstech.sugartrade.util.ItemAnimation;
import com.prosolstech.sugartrade.util.VU;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class MySellOfferFragment extends Fragment {

    Context context;
    View rootView;
    private RecyclerView recyclerView;
    private MySellOfferAdapter sellOfferAdapter;
    private int animation_type = ItemAnimation.BOTTOM_UP;
    SwipeRefreshLayout mSwipeRefreshLayout;

    private TabLayout tabLayout;
    private int counter=1;


    public MySellOfferFragment(TabLayout tabLayout) {
        this.tabLayout = tabLayout;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        rootView = inflater.inflate(R.layout.fragment_my_all_sell_bid, container, false);
        initializeUI(rootView);
        Log.e("MySellOfferFragment", "oNcretaed");

        final FloatingActionButton fab_add = (FloatingActionButton) rootView.findViewById(R.id.fab_add);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ACU.MySP.getFromSP(context, ACU.MySP.ROLE, "").equals("Seller")) {

                    if (ACU.MySP.getFromSP(context, ACU.MySP.ROLE, "").equals("Seller")) {

                        if (!ACU.MySP.getSPBoolean(context, ACU.MySP.IS_FIRST_TIME_LAUNCH, false)){
                            Toast.makeText(context, "Create Sell offer", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(context, PlaceSellBidActivity.class));
                            ACU.MySP.setSPBoolean(context, ACU.MySP.IS_FIRST_TIME_LAUNCH, true);
                        }else{
                            startActivity(new Intent(context, PlaceSellBidActivity.class));
                        }


                        } else if (ACU.MySP.getFromSP(context, ACU.MySP.ROLE, "").equals("Buyer")) {

                        if (!ACU.MySP.getSPBoolean(context, ACU.MySP.IS_FIRST_TIME_LAUNCH, false)){
                            Toast.makeText(context, "Create Buyer offer", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(context, PlaceBuyBidActivity.class));
                            ACU.MySP.setSPBoolean(context, ACU.MySP.IS_FIRST_TIME_LAUNCH, true);
                        }else{
                            startActivity(new Intent(context, PlaceBuyBidActivity.class));
                        }



                    }
                }
            }
        });

        if (VU.isConnectingToInternet(context)) {
            Log.e("MySellOfferFragment", " : ");
            MySellOffer();
        }
        return rootView;
    }

    public void initializeUI(View rootView) {


        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.MyAllSellOfferSwipeToRefresh);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.AllMySellBidFragmentRecyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 1));
        recyclerView.setHasFixedSize(true);
        animation_type = ItemAnimation.FADE_IN;

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (VU.isConnectingToInternet(context)) {
                    MySellOffer();
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

    }


    private void MySellOffer() {

        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait while data fetch from server...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/sellBidsById";      //for server

        Log.e("URL_MySellOffer", " : " + url);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Log.e("DATA_MySellOffer", " RESPONSE " + response);
                        pDialog.dismiss();
                        recyclerView.setVisibility(View.VISIBLE);
                        setListAdapter(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("No Response", "FOUND");
                pDialog.dismiss();
                tabLayout.getTabAt(1).setText("My Post");
                recyclerView.setVisibility(View.GONE);
                Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", ACU.MySP.getFromSP(context, ACU.MySP.ID, ""));
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private void setListAdapter(String result) {
        try {
            JSONArray array;

            array = new JSONArray(result);
            if (array != null && array.length() > 0) {
                recyclerView.setVisibility(View.VISIBLE);

                sellOfferAdapter = new MySellOfferAdapter(context, array, animation_type, tabLayout);
                recyclerView.setAdapter(sellOfferAdapter);
            } else {
                recyclerView.setVisibility(View.GONE);

                tabLayout.getTabAt(1).setText("My Post");
                Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean _hasLoadedOnce = false; // your boolean field

    @Override
    public void setUserVisibleHint(boolean isFragmentVisible_) {
        super.setUserVisibleHint(true);
        Log.e("MySellOfferFragment", "setUserVisibleHint");
        if (this.isVisible()) {
            // we check that the fragment is becoming visible
            if (isFragmentVisible_ && isResumed()) {
//            if (isFragmentVisible_ && !_hasLoadedOnce) {
                tabLayout.getTabAt(1).setText("My Post");
                if (VU.isConnectingToInternet(context)) {
                    Log.e("MySellOfferFragment", " : ");
                    MySellOffer();
                }
                _hasLoadedOnce = true;
            }
        }
    }
}


