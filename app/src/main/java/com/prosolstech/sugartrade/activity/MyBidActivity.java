package com.prosolstech.sugartrade.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.prosolstech.sugartrade.R;
import com.prosolstech.sugartrade.adapter.MyBidAdapter;
import com.prosolstech.sugartrade.adapter.SellBidAdapterTestTimer;
import com.prosolstech.sugartrade.database.DataBaseHelper;
import com.prosolstech.sugartrade.model.MyBid;
import com.prosolstech.sugartrade.model.SellBidModel;
import com.prosolstech.sugartrade.util.ACU;
import com.prosolstech.sugartrade.util.ItemAnimation;
import com.prosolstech.sugartrade.util.VU;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyBidActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private int animation_type = ItemAnimation.BOTTOM_UP;
    private MyBidAdapter bidAdapter;
    List<MyBid> stringList;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bid);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);
        recyclerView = (RecyclerView) findViewById(R.id.recyler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.setHasFixedSize(true);
        animation_type = ItemAnimation.FADE_IN;
        stringList = new ArrayList<>();
        bidAdapter = new MyBidAdapter(this, stringList, animation_type);

        recyclerView.setAdapter(bidAdapter);
        bidAdapter.notifyDataSetChanged();


        if (VU.isConnectingToInternet(this)) {
            Log.e("Buy_BID_FRAGMENT", " : ");
            buyBidData(this);
        }


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (VU.isConnectingToInternet(MyBidActivity.this)) {
                    buyBidData(MyBidActivity.this);
                }
                mSwipeRefreshLayout.setRefreshing(false);

            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, BuySellDashBoardActivity.class);
        startActivity(intent);

        finish();
    }


    private void buyBidData(final Context context) {
        String url = "";
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait while data fetch from server...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(context);
        url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/getMyBids";      //for server
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
                Log.e("No Response", error.toString());
                pDialog.dismiss();
                recyclerView.setVisibility(View.GONE);
                Toast.makeText(context, "Please Try Again!", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("role", ACU.MySP.getFromSP(context, ACU.MySP.ROLE, ""));
                params.put("id", ACU.MySP.getFromSP(context, ACU.MySP.ID, ""));
                Log.e("BUYER_PARMAS", " ..... " + params.toString());

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
            Log.e("setListAdapter", " RESULT " + array.length() + "");


            if (stringList.size() > 0) {
                stringList.clear();
            }


            if (array != null && array.length() > 0) {

                for (int i = 0; i < array.length(); i++) {
                    MyBid myBid = new MyBid();
                    myBid.setId(array.getJSONObject(i).getString("offer_id"));
                    myBid.setCompanyName(array.getJSONObject(i).getString("company_name"));
                    myBid.setType(array.getJSONObject(i).getString("type"));

//                    sellBidModel.setId(array.getJSONObject(i).getString("id"));
//                    sellBidModel.setCompany_name(array.getJSONObject(i).getString("company_name"));
//
                    stringList.add(myBid);


                }

                recyclerView.setVisibility(View.VISIBLE);

                bidAdapter.notifyDataSetChanged();


            } else {
                recyclerView.setVisibility(View.GONE);
                Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
