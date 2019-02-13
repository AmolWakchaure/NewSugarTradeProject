package com.prosolstech.sugartrade.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.prosolstech.sugartrade.R;
import com.prosolstech.sugartrade.activity.BookingDetailsActivity;
import com.prosolstech.sugartrade.classes.T;
import com.prosolstech.sugartrade.util.DTU;
import com.prosolstech.sugartrade.util.ItemAnimation;
import com.prosolstech.sugartrade.util.VU;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyAllBookingBuyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Activity ctx;
    private OnItemClickListener mOnItemClickListener;
    private int animation_type = 0;
    private JSONArray array;
    String typeOne;



    public interface OnItemClickListener {
        void onItemClick(View view, Integer obj, int position);
    }


    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public MyAllBookingBuyAdapter(Activity context, JSONArray array, int animation_type,String typeOne) {
        ctx = context;
        this.array = array;
        this.animation_type = animation_type;
        this.typeOne = typeOne;

        //this.array = sortJsonArray(array);


    }


    public static JSONArray sortJsonArray(JSONArray array) {
        List<JSONObject> jsons = new ArrayList<JSONObject>();
        for (int i = 0; i < array.length(); i++) {
            try {
                jsons.add(array.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(jsons, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject lhs, JSONObject rhs) {
                String lid = null;
                String rid = null;
                try {
                    lid = lhs.getString("date") + lhs.getString("time");
                    rid = rhs.getString("date") + lhs.getString("time");

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                // Here you could parse string id to integer and then compare.
                assert lid != null;
                assert rid != null;
                return lid.compareTo(rid);
            }
        });
        return new JSONArray(jsons);
    }






    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView txtMillName, txtDate, txtRatePerQtl, txtReqQuantity, txtGrade, id, start, type,
                start_time,
                end_time,availableQty,currReqQty_tv,availableQty_tv;
        public LinearLayout type_lv;
        public ImageView SellerListAdapterImgUnFav;

        public OriginalViewHolder(View v) {
            super(v);
            txtMillName = (TextView) v.findViewById(R.id.MyAllBookingActivityTxtName);
            id = (TextView) v.findViewById(R.id.id);
            txtDate = (TextView) v.findViewById(R.id.MyAllBookingActivityTxtDate);
            txtGrade = (TextView) v.findViewById(R.id.MyAllBookingActivityTxtGrade);
            txtRatePerQtl = (TextView) v.findViewById(R.id.MyAllBookingActivityTxtRatePerQtl);
            txtReqQuantity = (TextView) v.findViewById(R.id.MyAllBookingActivityTxtReqQuantity);
            start = (TextView) v.findViewById(R.id.start);
            type = (TextView) v.findViewById(R.id.type);
            type_lv = (LinearLayout) v.findViewById(R.id.type_lv);
            SellerListAdapterImgUnFav = v.findViewById(R.id.SellerListAdapterImgUnFav);
            start_time = (TextView) v.findViewById(R.id.start_time);
            end_time = (TextView) v.findViewById(R.id.end_time);
            availableQty = (TextView) v.findViewById(R.id.availableQty);
            currReqQty_tv = (TextView) v.findViewById(R.id.currReqQty_tv);
            availableQty_tv = (TextView) v.findViewById(R.id.availableQty_tv);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_booking_test, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Log.e("onBindViewHolder", "onBindViewHolder : " + position);
        if (holder instanceof OriginalViewHolder)
        {
            OriginalViewHolder view = (OriginalViewHolder) holder;

            Log.e("array_TOSTRING", ": " + array.toString());
            try
            {
                if (array.getJSONObject(position).getString("is_favorite").equalsIgnoreCase("Y"))
                {
                    view.SellerListAdapterImgUnFav.setVisibility(View.VISIBLE);
                }
                else
                {
                    view.SellerListAdapterImgUnFav.setVisibility(View.GONE);

                }

                if (array.getJSONObject(position).getString("role").equalsIgnoreCase("Buyer"))
                {
                    view.start.setText("Buy Post id: ");
                    if (array.getJSONObject(position).getString("type").equalsIgnoreCase(""))
                    {
                        view.type_lv.setVisibility(View.GONE);

                        view.availableQty_tv.setText("Available Qty : ");

                       /* String required_qty = array.getJSONObject(position).getString("required_qty");

                        String curr_req_qty = array.getJSONObject(position).getString("curr_req_qty");

                        int total_acq_qty = Integer.valueOf(required_qty) - Integer.valueOf(curr_req_qty);

                        int available_qty = Integer.valueOf(required_qty) - Integer.valueOf(total_acq_qty);*/

                        String allotted = array.getJSONObject(position).getString("allotted");

                        //available qty
                        view.availableQty.setText(""+allotted);
                    }
                    else
                    {
                        view.type_lv.setVisibility(View.VISIBLE);
                        String type = array.getJSONObject(position).getString("type");
                        if(type.equals("buy"))
                        {
                            view.availableQty_tv.setText("Available Qty : ");

                            /*String required_qty = array.getJSONObject(position).getString("required_qty");

                            String curr_req_qty = array.getJSONObject(position).getString("curr_req_qty");

                            int total_acq_qty = Integer.valueOf(required_qty) - Integer.valueOf(curr_req_qty);

                            int available_qty = Integer.valueOf(required_qty) - Integer.valueOf(total_acq_qty);*/

                            String allotted = array.getJSONObject(position).getString("allotted");

                            //available qty
                            view.availableQty.setText(""+allotted);
                        }
                        else
                        {
                            view.availableQty_tv.setText("Required qty : ");
                            // view.currReqQty_tv.setText("Current Available Qty : ");
                            //Required qty
                            view.availableQty.setText(array.getJSONObject(position).getString("required_qty"));
                        }
                        view.type.setText(type);

                    }
                    //current required quantity
                    view.txtReqQuantity.setText(array.getJSONObject(position).getString("curr_req_qty"));
                    T.e("typeOne : "+ typeOne);

                }
                else
                {

                    view.start.setText("Sell Post id: ");
                    view.currReqQty_tv.setText("Current Available Qty : ");
                    view.availableQty_tv.setText("Required Qty : ");

                    if (array.getJSONObject(position).getString("type").equalsIgnoreCase("")) {
                        view.type_lv.setVisibility(View.GONE);
                    } else {
                        view.type_lv.setVisibility(View.VISIBLE);
                        view.type.setText(array.getJSONObject(position).getString("type"));
                    }

                    String required_qty = array.getJSONObject(position).getString("available_qty");
                    String curr_req_qty = array.getJSONObject(position).getString("curr_req_qty");
                    int total_acq_qty = Integer.valueOf(required_qty) - Integer.valueOf(curr_req_qty);
                    int available_qty = Integer.valueOf(required_qty) - Integer.valueOf(total_acq_qty);
                    //current available qty
                    view.txtReqQuantity.setText(""+available_qty);             // last qty gave by user which is accept the record

                    //available qty
                    view.availableQty.setText(""+required_qty);
                }




                view.id.setText(array.getJSONObject(position).getString("offer_id"));
                view.txtMillName.setText(array.getJSONObject(position).getString("company_name"));

                view.txtGrade.setText(array.getJSONObject(position).getString("category"));

                view.start_time.setText(array.getJSONObject(position).getString("bid_start_time").substring(0,5));

                long hours5 = Long.parseLong(array.getJSONObject(position).getString("bid_end_time")) / 60; //since both are ints, you get an int
                long minutes5 = Long.parseLong(array.getJSONObject(position).getString("bid_end_time")) % 60;

                String bid_end_time = String.format(Locale.getDefault(), "%02d:%02d", hours5, minutes5);

                view.end_time.setText(bid_end_time);


                view.txtDate.setText(DTU.changeTimeFormat(array.getJSONObject(position).getString("date"), array.getJSONObject(position).getString("time")));

                if (array.getJSONObject(position).has("type"))
                {
                    if (array.getJSONObject(position).getString("type").equalsIgnoreCase("Tender"))
                    {
                        view.txtRatePerQtl.setText(array.getJSONObject(position).getString("tender_price"));
                    }
                    else
                    {
                        view.txtRatePerQtl.setText(array.getJSONObject(position).getString("price_per_qtl"));
                    }
                }
                else
                 {
                    view.txtRatePerQtl.setText(array.getJSONObject(position).getString("price_per_qtl"));
                }

            } catch (JSONException e) {

                T.e("onBindViewHolder : "+e);
                e.printStackTrace();
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent in = new Intent(ctx, BookingDetailsActivity.class);
                        in.putExtra("flag", "Buyer");
                        in.putExtra("data", String.valueOf(array.getJSONObject(position)));
                        in.putExtra("typeStatus", typeOne);
                        in.putExtra("type", typeOne);
                        ctx.startActivity(in);
                        ctx.finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            setAnimation(view.itemView, position);
        }
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
        return array.length();
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
