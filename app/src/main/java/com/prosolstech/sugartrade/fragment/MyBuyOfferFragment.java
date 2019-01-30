package com.prosolstech.sugartrade.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
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
import com.prosolstech.sugartrade.adapter.MyBuyOfferAdapter;
import com.prosolstech.sugartrade.util.ACU;
import com.prosolstech.sugartrade.util.ItemAnimation;
import com.prosolstech.sugartrade.util.VU;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

@SuppressLint("ValidFragment")
public class MyBuyOfferFragment extends Fragment {


    Context context;
    View rootView;
    private RecyclerView recyclerView;
    private MyBuyOfferAdapter buyOfferAdapter;
    private int animation_type = ItemAnimation.BOTTOM_UP;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean _hasLoadedOnce = false; // your boolean field
    TabLayout tabLayout;
    private int counter = 1;

    public MyBuyOfferFragment(TabLayout tabLayout) {
        this.tabLayout = tabLayout;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        rootView = inflater.inflate(R.layout.fragment_my_all_sell_bid, container, false);
        initializeUI(rootView);
//        Log.e("MyBuyOfferFragment", " : ");
//        Log.e("BuyBidFragment", tabLayout.getSelectedTabPosition()+"");
        final FloatingActionButton fab_add = (FloatingActionButton) rootView.findViewById(R.id.fab_add);

                fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ACU.MySP.getFromSP(context, ACU.MySP.ROLE, "").equals("Seller")) {


                    if (!ACU.MySP.getSPBoolean(context, ACU.MySP.IS_FIRST_TIME_LAUNCH, false)) {
                        Toast.makeText(context, "Create Sell offer", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(context, PlaceSellBidActivity.class));
                        ACU.MySP.setSPBoolean(context, ACU.MySP.IS_FIRST_TIME_LAUNCH, true);
                    } else {
                        startActivity(new Intent(context, PlaceSellBidActivity.class));
                    }

                } else if (ACU.MySP.getFromSP(context, ACU.MySP.ROLE, "").equals("Buyer")) {

                    if (!ACU.MySP.getSPBoolean(context, ACU.MySP.IS_FIRST_TIME_LAUNCH, false)) {
                        Toast.makeText(context, "Create Buyer offer", Toast.LENGTH_SHORT).show();
                       // startActivity(new Intent(context, PlaceBuyBidActivity.class));
                        Intent in = new Intent(context, PlaceBuyBidActivity.class);
                        in.putExtra("flag", "insert");
                        in.putExtra("data", "");
                        startActivity(in);
                        ACU.MySP.setSPBoolean(context, ACU.MySP.IS_FIRST_TIME_LAUNCH, true);
                    } else {
                       // startActivity(new Intent(context, PlaceBuyBidActivity.class));
                        Intent in = new Intent(context, PlaceBuyBidActivity.class);
                        in.putExtra("flag", "insert");
                        in.putExtra("data", "");
                        startActivity(in);
                    }

                }
            }
        });
        if (VU.isConnectingToInternet(context)) {
            MyBuyOffer();

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
                    MyBuyOffer();
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

    }


    private void MyBuyOffer() {

        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait while data fetch from server...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(context);

        String url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/buyBidsById";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Log.e("MyBuyOffer", " RESPONSE " + response);
                        pDialog.dismiss();
                        recyclerView.setVisibility(View.VISIBLE);
                        setListAdapter(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("No Response", "FOUND");
                pDialog.dismiss();
                Toast.makeText(context, "No Data Found", Toast.LENGTH_SHORT).show();
                recyclerView.setVisibility(View.GONE);
                tabLayout.getTabAt(1).setText("My Post");

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

            Log.e("setListAdapter", " RESULT " + result.trim());
            array = new JSONArray(result);
            if (array != null && array.length() > 0) {
                recyclerView.setVisibility(View.VISIBLE);
                buyOfferAdapter = new MyBuyOfferAdapter(getActivity(), array, animation_type, tabLayout);
                recyclerView.setAdapter(buyOfferAdapter);
            } else {
                recyclerView.setVisibility(View.GONE);

                tabLayout.getTabAt(1).setText("My Post");
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
                tabLayout.getTabAt(1).setText("My Post");
                Log.e("MyBuyOfferFragment", "setUserVisibleHint");
                //   if (isFragmentVisible_ && !_hasLoadedOnce) {
                if (VU.isConnectingToInternet(context)) {
                    MyBuyOffer();
                }
                _hasLoadedOnce = true;
            }
        }
    }
}
