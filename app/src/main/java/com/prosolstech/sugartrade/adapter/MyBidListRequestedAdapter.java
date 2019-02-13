package com.prosolstech.sugartrade.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.prosolstech.sugartrade.R;
import com.prosolstech.sugartrade.activity.BookingDetailsActivity;
import com.prosolstech.sugartrade.activity.MyBidRequestedListActivity;
import com.prosolstech.sugartrade.classes.MyApplication;
import com.prosolstech.sugartrade.classes.T;
import com.prosolstech.sugartrade.model.MyBid;
import com.prosolstech.sugartrade.model.MyBidRequestedList;
import com.prosolstech.sugartrade.util.ACU;
import com.prosolstech.sugartrade.util.DTU;
import com.prosolstech.sugartrade.util.ItemAnimation;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyBidListRequestedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Activity ctx;
    private OnItemClickListener mOnItemClickListener;
    private int animation_type = 0;
    private List<MyBidRequestedList> array;
    private JSONArray jsonArray;


    public interface OnItemClickListener {
        void onItemClick(View view, Integer obj, int position);
    }


    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public MyBidListRequestedAdapter(Activity context, List<MyBidRequestedList> array, int animation_type, JSONArray jsonArray) {
        ctx = context;
        this.array = array;
        this.animation_type = animation_type;
        this.jsonArray = jsonArray;


    }


    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView requested__tv, required_quantity_tv, season_tv, grade_tv, offered_quantity_tv, price_tv;
        public TextView MyRequestAdapterBtnAccept,required_quantity_tvlbl,offered_quantity_tvlbl;
        LinearLayout hideLayout;


        public OriginalViewHolder(View v) {
            super(v);
            requested__tv = (TextView) v.findViewById(R.id.requested__tv);
            required_quantity_tv = (TextView) v.findViewById(R.id.required_quantity_tv);
            season_tv = (TextView) v.findViewById(R.id.season_tv);
            grade_tv = (TextView) v.findViewById(R.id.grade_tv);
            grade_tv = (TextView) v.findViewById(R.id.grade_tv);
            offered_quantity_tv = (TextView) v.findViewById(R.id.offered_quantity_tv);
            price_tv = (TextView) v.findViewById(R.id.price_tv);
            MyRequestAdapterBtnAccept = v.findViewById(R.id.MyRequestAdapterBtnAccept);

            required_quantity_tvlbl = (TextView) v.findViewById(R.id.required_quantity_tvlbl);
            offered_quantity_tvlbl = (TextView) v.findViewById(R.id.offered_quantity_tvlbl);
            hideLayout = (LinearLayout) v.findViewById(R.id.hideLayout);


        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_bid_list_requested_adapter, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Log.e("onBindViewHolder", "onBindViewHolder : " + position);
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;
            Log.e("array_TOSTRING", ": " + array.toString());

            final MyBidRequestedList myBid = array.get(position);
            view.requested__tv.setText(DTU.changeDateTimeFormat(myBid.getRequestedTime()));

            view.grade_tv.setText(myBid.getGrade());



            if (myBid.getType().equalsIgnoreCase("Tender"))
            {
                view.price_tv.setText(myBid.getTenderPrice());

            } else {

                view.price_tv.setText(myBid.getPriceQty());
            }
         //   view.price_tv.setText(myBid.getPriceQty());


            view.season_tv.setText(myBid.getSeason());


            if (myBid.getIsinteredstre().equalsIgnoreCase("Accept"))
            {
                view.MyRequestAdapterBtnAccept.setText("Accepted");
                view.MyRequestAdapterBtnAccept.setTextColor(ctx.getResources().getColor(R.color.color_white));
                view.MyRequestAdapterBtnAccept.setBackgroundColor(ctx.getResources().getColor(R.color.green_600));
            }
            else if (myBid.getIsinteredstre().equalsIgnoreCase("Reject"))
            {
                view.MyRequestAdapterBtnAccept.setText("Rejected");
                view.MyRequestAdapterBtnAccept.setTextColor(ctx.getResources().getColor(R.color.color_white));
                view.MyRequestAdapterBtnAccept.setBackgroundColor(ctx.getResources().getColor(R.color.red_600));
            }
            else
            {
                view.MyRequestAdapterBtnAccept.setText("Awaiting");
                view.MyRequestAdapterBtnAccept.setTextColor(ctx.getResources().getColor(R.color.color_white));
                view.MyRequestAdapterBtnAccept.setBackgroundColor(ctx.getResources().getColor(R.color.blue_600));
            }

            String loginStatus = ACU.MySP.getFromSP(MyApplication.context, ACU.MySP.ROLE,"");

            if(loginStatus.equals("Seller"))
            {
                view.hideLayout.setVisibility(View.GONE);
                view.required_quantity_tvlbl.setText("Current Required Quantity : ");
                view.offered_quantity_tvlbl.setText("Bid Quantity form seller : ");
                view.required_quantity_tv.setText(myBid.getAvailQty());
                view.offered_quantity_tv.setText(myBid.getReqty());
            }
            else  if(loginStatus.equals("Buyer"))
            {
                view.hideLayout.setVisibility(View.GONE);
                view.required_quantity_tvlbl.setText("Available Quantity : ");
                view.offered_quantity_tvlbl.setText("Bid Quantity from Buyer : ");
                view.required_quantity_tv.setText(myBid.getAvailQty());
                view.offered_quantity_tv.setText(myBid.getReqty());
            }


            view.MyRequestAdapterBtnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    //if bid status is accepted then display my booking page
                    if (myBid.getIsinteredstre().equalsIgnoreCase("Accept"))
                    {

                        //call api for get single booking data

                        try
                        {

                            String loginStatus = ACU.MySP.getFromSP(MyApplication.context, ACU.MySP.ROLE,"");

                            if(loginStatus.equals("Seller"))
                            {
                                getSingleBookingDetails("buy",myBid.getId());
                            }
                            else
                            {
                                getSingleBookingDetails("sell",myBid.getId());
                            }

                        }
                        catch (Exception e)
                        {
                            T.e("Exception e : "+e);
                            e.printStackTrace();
                        }
                    }

                }
            });

            setAnimation(view.itemView, position);
        }
    }

    private void getSingleBookingDetails(final String type, final String id) {
        String url = "";
        final ProgressDialog pDialog = new ProgressDialog(ctx);
        pDialog.setMessage("Please wait while data fetch from server...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(ctx);

        url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/getMySellBookingSingle";      //for server
        Log.e("FetchVehicleDetail_URL", " ....." + url);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        // Display the response string.
                        pDialog.dismiss();
                        Log.e("FetchVehicleDetail", " RESPONSE " + response);

                        try
                        {
                            JSONArray jsonArray = new JSONArray(response);

                            Intent in = new Intent(ctx, BookingDetailsActivity.class);
                            in.putExtra("flag", ACU.MySP.getFromSP(ctx, ACU.MySP.ROLE, ""));
                            //in.putExtra("data", String.valueOf(array.getJSONObject(position)));
                            in.putExtra("data", ""+jsonArray.getJSONObject(0));
                            in.putExtra("typeStatus", type);
                            in.putExtra("flag_another", "dkjfgk");
                            in.putExtra("type", type);
                            in.putExtra("navigate_status", "from_my_bids");
                            ctx.startActivity(in);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("No Response", "FOUND");
                pDialog.dismiss();

                T.t("Oops ! something went wrong");



            }
        })

        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", ACU.MySP.getFromSP(ctx, ACU.MySP.ID, ""));
                params.put("role", ACU.MySP.getFromSP(ctx, ACU.MySP.ROLE, ""));
                params.put("type", type);
                params.put("offer_book_id", id);

                Log.e("SINGLE_BID_PARMAS", " ..... " + params.toString());
                return params;
            }
        };
        queue.add(stringRequest);
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                on_attach = false;
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    private int lastPosition = -1;
    private boolean on_attach = true;

    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, on_attach ? position : -1, animation_type);
            lastPosition = position;
        }
    }
}
