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
import android.view.MenuItem;
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
import com.prosolstech.sugartrade.adapter.MyBidListRequestedAdapter;
import com.prosolstech.sugartrade.classes.T;
import com.prosolstech.sugartrade.model.MyBid;
import com.prosolstech.sugartrade.model.MyBidRequestedList;
import com.prosolstech.sugartrade.util.ACU;
import com.prosolstech.sugartrade.util.ItemAnimation;
import com.prosolstech.sugartrade.util.VU;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyBidRequestedListActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private int animation_type = ItemAnimation.BOTTOM_UP;
    private MyBidListRequestedAdapter bidAdapter;
    List<MyBidRequestedList> stringList = new ArrayList<>();
    private MyBid myBid;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bid_requested_list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        Intent intent = getIntent();

        if (intent != null) {

            myBid = (MyBid) intent.getSerializableExtra("MyBid");
            if (ACU.MySP.getFromSP(this, ACU.MySP.ROLE, "").equalsIgnoreCase("Buyer")) {
                toolbar.setTitle("Sell Post Id :" + myBid.getId());
            } else {
                toolbar.setTitle("Buy Post Id :" + myBid.getId());
            }
        }


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.recyler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.setHasFixedSize(true);
        animation_type = ItemAnimation.FADE_IN;


        bidAdapter = new MyBidListRequestedAdapter(this, stringList, animation_type);

        recyclerView.setAdapter(bidAdapter);
        bidAdapter.notifyDataSetChanged();

        if (VU.isConnectingToInternet(this)) {
            buyBidData(this);
        } else {
            Toast.makeText(this, "No Internet present Please try again!", Toast.LENGTH_SHORT).show();
        }


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (VU.isConnectingToInternet(MyBidRequestedListActivity.this)) {
                    buyBidData(MyBidRequestedListActivity.this);
                }
                mSwipeRefreshLayout.setRefreshing(false);

            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void buyBidData(final Context context) {
        String url = "";
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait while data fetch from server...");
        pDialog.setCancelable(false);
        pDialog.show();


        RequestQueue queue = Volley.newRequestQueue(context);
        url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/getOfferDetailsById";      //for server
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
                params.put("offer_id", myBid.getId() + "");
                params.put("id", ACU.MySP.getFromSP(context, ACU.MySP.ID, ""));
                Log.e("BUYER_PARMAS", " ..... " + params.toString());

                return params;
            }
        };
        queue.add(stringRequest);
    }

    //
    private void setListAdapter(String result) {
        try {
            JSONArray array;
            Log.e("setListAdapter", " RESULT " + result.trim());
            array = new JSONArray(result);
            Log.e("setListAdapter", " RESULT " + array.length() + "");
            String reqQty,allotedQty;


            if (stringList.size() > 0) {
                stringList.clear();
            }


            if (array != null && array.length() > 0) {

                for (int i = 0; i < array.length(); i++) {
                    MyBidRequestedList myBid = new MyBidRequestedList();


                    String loginStatus = ACU.MySP.getFromSP(MyBidRequestedListActivity.this, ACU.MySP.ROLE,"");

                    if(loginStatus.equals("Seller"))
                    {
                        myBid.setAvailQty(array.getJSONObject(i).getString("required_qty"));
                        myBid.setReqty(array.getJSONObject(i).getString("allotted"));


                        if(array.getJSONObject(i).has("type"))
                        {
                            myBid.setType(array.getJSONObject(i).getString("type"));
                            myBid.setTenderPrice(array.getJSONObject(i).getString("tender_price"));
                        }
                        else
                        {
                            myBid.setType("NA");
                        }
                    }
                    else  if(loginStatus.equals("Buyer"))
                    {



                        if(array.getJSONObject(i).isNull("allotted"))
                        {
                            allotedQty = "0";
                        }
                        else
                        {
                            allotedQty = array.getJSONObject(i).getString("allotted");
                        }

                        if(array.getJSONObject(i).isNull("required_qty"))
                        {
                            reqQty = "0";
                        }
                        else
                        {
                            reqQty = array.getJSONObject(i).getString("required_qty");
                        }

                        if(array.getJSONObject(i).has("type"))
                        {
                            myBid.setType(array.getJSONObject(i).getString("type"));
                            myBid.setTenderPrice(array.getJSONObject(i).getString("tender_price"));
                        }
                        else
                        {
                            myBid.setType("NA");
                        }

                        int availQty = Integer.valueOf(reqQty) - Integer.valueOf(allotedQty);
                        myBid.setAvailQty(""+availQty);
                        myBid.setReqty(reqQty);
                    }
                    else
                    {
                        myBid.setAvailQty("NA");
                        myBid.setReqty("NA");
                    }
                  //  myBid.setTenderPrice(array.getJSONObject(i).getString("tender_price"));
                    //myBid.setType(array.getJSONObject(i).getString("type"));

                    myBid.setGrade(array.getJSONObject(i).getString("catName"));
                    myBid.setPriceQty(array.getJSONObject(i).getString("price_per_qtl"));

                    myBid.setRequestedTime(array.getJSONObject(i).getString("requestedAT"));
                    myBid.setSeason(array.getJSONObject(i).getString("production_year"));

                    myBid.setIsinteredstre(array.getJSONObject(i).getString("is_interested"));

//                    sellBidModel.setCompany_name(array.getJSONObject(i).getString("company_name"));
//
                    stringList.add(myBid);


                }

                recyclerView.setVisibility(View.VISIBLE);
                bidAdapter = new MyBidListRequestedAdapter(this, stringList, animation_type);

                recyclerView.setAdapter(bidAdapter);
                bidAdapter.notifyDataSetChanged();
               // bidAdapter.notifyDataSetChanged();


            } else {
                recyclerView.setVisibility(View.GONE);
                Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {

            T.e("Exception : "+e);
            e.printStackTrace();
        }
    }


}
