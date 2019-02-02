package com.prosolstech.sugartrade.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.prosolstech.sugartrade.R;
import com.prosolstech.sugartrade.activity.BookingDetailsActivity;
import com.prosolstech.sugartrade.activity.MyBidDetailActivity;
import com.prosolstech.sugartrade.activity.MyBidRequestedListActivity;
import com.prosolstech.sugartrade.activity.PlaceBuyBidActivity;
import com.prosolstech.sugartrade.activity.PlaceSellBidActivity;
import com.prosolstech.sugartrade.model.MyBid;
import com.prosolstech.sugartrade.util.ACU;
import com.prosolstech.sugartrade.util.DTU;
import com.prosolstech.sugartrade.util.ItemAnimation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyBidAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Activity ctx;
    private OnItemClickListener mOnItemClickListener;
    private int animation_type = 0;
    private List<MyBid> array;


    public interface OnItemClickListener {
        void onItemClick(View view, Integer obj, int position);
    }


    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public MyBidAdapter(Activity context, List<MyBid> array, int animation_type) {
        ctx = context;
        this.array = array;
        this.animation_type = animation_type;


    }


    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView id_my_bid, txtDate, type_id, type_my_bid;
        public LinearLayout type_lv, tap_lv;
        public Button view_details_btn, view_bids_btn;

        public OriginalViewHolder(View v) {
            super(v);
            id_my_bid = (TextView) v.findViewById(R.id.id_my_bid);
            type_id = (TextView) v.findViewById(R.id.type_id);
            txtDate = (TextView) v.findViewById(R.id.company_named);
            view_details_btn = v.findViewById(R.id.view_details_btn);
            tap_lv = v.findViewById(R.id.tap_lv);
            type_lv = v.findViewById(R.id.type_lv);
            type_my_bid = v.findViewById(R.id.type_my_bid);
            view_bids_btn = v.findViewById(R.id.view_bids_btn);

        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_bid_list_item, parent, false);
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

            final MyBid myBid = array.get(position);

            if (ACU.MySP.getFromSP(ctx, ACU.MySP.ROLE, "").equalsIgnoreCase("Buyer")) {
                view.type_id.setText("Sell Post Id: ");
                view.id_my_bid.setText(myBid.getId());
                view.type_lv.setVisibility(View.VISIBLE);
                view.type_my_bid.setText(myBid.getType());

            } else {
                view.type_lv.setVisibility(View.GONE);
                view.type_id.setText("Buy Post Id: ");
                view.id_my_bid.setText(myBid.getId());

            }
            view.txtDate.setText(myBid.getCompanyName());


            view.view_bids_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ctx, MyBidRequestedListActivity.class);
                    intent.putExtra("MyBid", myBid);
                    ctx.startActivity(intent);

                }
            });


            view.view_details_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {


                    if (ACU.MySP.getFromSP(ctx, ACU.MySP.ROLE, "").equalsIgnoreCase("Buyer"))
                    {
                        //get bid details
                        buyBidData(ctx,myBid);
                    }
                    else
                    {
                         //get bid details
                        buyBidData(ctx,myBid);
                    }


                }
            });
            setAnimation(view.itemView, position);
        }
    }
    private void buyBidData(final Context context, final MyBid myBid) {
        String url = "";
        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait while data fetch from server...");
        pDialog.setCancelable(false);
        pDialog.show();


        RequestQueue queue = Volley.newRequestQueue(context);
       // url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/getBidDetailsById";      //for server
        url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/getSinglePostByid";      //for server
        Log.e("BUYER_BothURL", " ....." + url);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Log.e("dfsfs", " RESPONSE " + response);

                        try
                        {
                            JSONArray jsonArray = new JSONArray(response);
                            //setData(jsonArray);
                            pDialog.dismiss();

                            if (ACU.MySP.getFromSP(ctx, ACU.MySP.ROLE, "").equalsIgnoreCase("Buyer"))
                            {
                                Intent in = new Intent(ctx, PlaceSellBidActivity.class);
                                in.putExtra("flag", "update");
                                in.putExtra("post_status", "single_post");
                                in.putExtra("data", String.valueOf(jsonArray.getJSONObject(0)));
                                ctx.startActivity(in);

                            }
                            else
                            {
                                Intent intent = new Intent(ctx, PlaceBuyBidActivity.class);
                                intent.putExtra("flag", "update");
                                intent.putExtra("post_status", "single_post");
                                intent.putExtra("data", ""+jsonArray.getJSONObject(0));
                                ctx.startActivity(intent);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("fdgfdgfde", error.toString());
                pDialog.dismiss();

                Toast.makeText(context, "Please Try Again!", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("id", ACU.MySP.getFromSP(context, ACU.MySP.ID, ""));
                params.put("my_post_id", ""+myBid.getId());
                params.put("role", ACU.MySP.getFromSP(context, ACU.MySP.ROLE, ""));

                Log.e("BUYER_PARMAS", " ..... " + params.toString());

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
