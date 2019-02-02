package com.prosolstech.sugartrade.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
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

public class MyAllBookingSellAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Activity ctx;
    private MyAllBookingSellAdapter.OnItemClickListener mOnItemClickListener;
    private int animation_type = 0;
    JSONArray array;
    TabLayout tabLayout;
    String type;

    public interface OnItemClickListener {
        void onItemClick(View view, Integer obj, int position);
    }


    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public MyAllBookingSellAdapter(Activity context, JSONArray array, int animation_type,String type) {
        ctx = context;
        this.array = array;
        this.animation_type = animation_type;
        this.type = type;
        this.array = sortJsonArray(array);
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
                return rid.compareTo(lid);
            }
        });
        return new JSONArray(jsons);
    }


    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView
                txtMillName,
                txtDate,
                txtRatePerQtl,
                txtReqQuantity,
                txtGrade,
                txtId,
                start,
                type,
                start_time,
                end_time,
                currReqQty_tv,
                availableQty_tv,
                availableQty,
                bookedQty_tv,
                bookedQty;
        public LinearLayout type_lv,booked_qty_li;
        public ImageView SellerListAdapterImgUnFav;

        public OriginalViewHolder(View v) {
            super(v);
            txtMillName = (TextView) v.findViewById(R.id.MyAllBookingActivityTxtName);
            txtDate = (TextView) v.findViewById(R.id.MyAllBookingActivityTxtDate);
            txtGrade = (TextView) v.findViewById(R.id.MyAllBookingActivityTxtGrade);
            txtRatePerQtl = (TextView) v.findViewById(R.id.MyAllBookingActivityTxtRatePerQtl);
            txtReqQuantity = (TextView) v.findViewById(R.id.MyAllBookingActivityTxtReqQuantity);
            txtId = (TextView) v.findViewById(R.id.id);
            start = (TextView) v.findViewById(R.id.start);
            type = (TextView) v.findViewById(R.id.type);
            type_lv = (LinearLayout) v.findViewById(R.id.type_lv);
            booked_qty_li = (LinearLayout) v.findViewById(R.id.booked_qty_li);
            SellerListAdapterImgUnFav = v.findViewById(R.id.SellerListAdapterImgUnFav);

            start_time = (TextView) v.findViewById(R.id.start_time);
            end_time = (TextView) v.findViewById(R.id.end_time);
            currReqQty_tv = (TextView) v.findViewById(R.id.currReqQty_tv);
            availableQty_tv = (TextView) v.findViewById(R.id.availableQty_tv);
            availableQty = (TextView) v.findViewById(R.id.availableQty);
            bookedQty_tv = (TextView) v.findViewById(R.id.bookedQty_tv);
            bookedQty = (TextView) v.findViewById(R.id.bookedQty);
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
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;
            Log.e("array_TOSTRING", ": " + array.toString());

            try {

                String role = array.getJSONObject(position).getString("role");
                if (type.equalsIgnoreCase("sell"))
                {
                    view.start.setText("Sell Post id : ");
                }
                else
                {
                    view.start.setText("Buy Post id : ");

                }


                if (role.equalsIgnoreCase("Buyer"))
                {

//                    view.start.setText("Buy Post id ");

                    if (array.getJSONObject(position).getString("type").equalsIgnoreCase(""))
                    {
                        view.type_lv.setVisibility(View.GONE);

                        String bid_end_time = T.returnEndTime(array.getJSONObject(position).getString("bid_end_time"));
                        view.start_time.setText(array.getJSONObject(position).getString("bid_start_time").substring(0,5));
                        view.end_time.setText(bid_end_time);
                        view.currReqQty_tv.setText("Current Required Quantity : ");
                        view.availableQty_tv.setText("Available Quantity : ");
                        String required_qty = array.getJSONObject(position).getString("required_qty");
                        String alloted_qty = array.getJSONObject(position).getString("allotted");
                        int available_quantity = Integer.valueOf(required_qty) - Integer.valueOf(alloted_qty);
                        view.availableQty.setText(""+available_quantity);
                        view.txtReqQuantity.setText(array.getJSONObject(position).getString("curr_req_qty"));


                    }
                    else
                    {
                        view.type_lv.setVisibility(View.VISIBLE);
                        view.type.setText(array.getJSONObject(position).getString("type"));

                        String bid_end_time = T.returnEndTime(array.getJSONObject(position).getString("bid_end_time"));
                        view.start_time.setText(array.getJSONObject(position).getString("bid_start_time").substring(0,5));
                        view.end_time.setText(bid_end_time);
                        view.currReqQty_tv.setText("Current Required Quantity : ");
                        view.availableQty_tv.setText("Available Quantity : ");
                        String required_qty = array.getJSONObject(position).getString("required_qty");
                        String alloted_qty = array.getJSONObject(position).getString("allotted");
                        int available_quantity = Integer.valueOf(required_qty) - Integer.valueOf(alloted_qty);
                        view.availableQty.setText(""+available_quantity);
                        view.txtReqQuantity.setText(array.getJSONObject(position).getString("curr_req_qty"));


                    }



                }
                else
                {

//                    view.start.setText("Sell Post id ");

                    if (array.getJSONObject(position).getString("type").equalsIgnoreCase(""))
                    {
                        view.type_lv.setVisibility(View.GONE);
                        // view.type_lv.setVisibility(View.VISIBLE);
                        // view.type.setText(array.getJSONObject(position).getString("type"));

                        if(type.equals("buy"))
                        {
                            String bid_end_time = T.returnEndTime(array.getJSONObject(position).getString("bid_end_time"));
                            view.start_time.setText(array.getJSONObject(position).getString("bid_start_time").substring(0,5));
                            view.end_time.setText(bid_end_time);
                            view.currReqQty_tv.setText("Required Quantity : ");
                            view.txtReqQuantity.setText(array.getJSONObject(position).getString("required_qty"));
                            view.currReqQty_tv.setVisibility(View.GONE);
                            view.txtReqQuantity.setVisibility(View.GONE);
                            view.availableQty_tv.setText("Current Required Quantity : ");
                            view.availableQty.setText(array.getJSONObject(position).getString("curr_req_qty"));

                            view.booked_qty_li.setVisibility(View.VISIBLE);
                            view.bookedQty_tv.setText("Available Quantity : ");
                            view.bookedQty.setText(array.getJSONObject(position).getString("allotted"));
                        }
                        else if(type.equals("sell"))
                        {
                            String bid_end_time = T.returnEndTime(array.getJSONObject(position).getString("bid_end_time"));
                            view.start_time.setText(array.getJSONObject(position).getString("bid_start_time").substring(0,5));
                            view.end_time.setText(bid_end_time);
                            view.currReqQty_tv.setText("Required Quantity : ");
                            view.txtReqQuantity.setText(array.getJSONObject(position).getString("required_qty"));
                            view.currReqQty_tv.setVisibility(View.GONE);
                            view.txtReqQuantity.setVisibility(View.GONE);


                            //current available qty
                            view.availableQty_tv.setText("Current Available Quantity : ");
                            //change index name i.e. available_qty : but  need here required qty but now fine because of no time
                            String required_qty = array.getJSONObject(position).getString("available_qty");
                            String allotted = array.getJSONObject(position).getString("allotted");
                            int curr_req_qty = Integer.valueOf(required_qty) - Integer.valueOf(allotted);
                            view.availableQty.setText(""+curr_req_qty);

                            view.booked_qty_li.setVisibility(View.VISIBLE);
                            view.bookedQty_tv.setText("Required Quantity : ");
                            view.bookedQty.setText(array.getJSONObject(position).getString("available_qty"));
                        }
                    }
                    else
                    {
                        view.type_lv.setVisibility(View.VISIBLE);
                        view.type.setText(array.getJSONObject(position).getString("type"));

                        if(type.equals("buy"))
                        {
                            String bid_end_time = T.returnEndTime(array.getJSONObject(position).getString("bid_end_time"));
                            view.start_time.setText(array.getJSONObject(position).getString("bid_start_time").substring(0,5));
                            view.end_time.setText(bid_end_time);
                            view.currReqQty_tv.setText("Required Quantity : ");
                            view.txtReqQuantity.setText(array.getJSONObject(position).getString("required_qty"));
                            view.currReqQty_tv.setVisibility(View.GONE);
                            view.txtReqQuantity.setVisibility(View.GONE);
                            view.availableQty_tv.setText("Current Required Quantity : ");
                            view.availableQty.setText(array.getJSONObject(position).getString("curr_req_qty"));

                            view.booked_qty_li.setVisibility(View.VISIBLE);
                            view.bookedQty_tv.setText("Available Quantity : ");
                            view.bookedQty.setText(array.getJSONObject(position).getString("allotted"));
                        }
                        else if(type.equals("sell"))
                        {
                            String bid_end_time = T.returnEndTime(array.getJSONObject(position).getString("bid_end_time"));
                            view.start_time.setText(array.getJSONObject(position).getString("bid_start_time").substring(0,5));
                            view.end_time.setText(bid_end_time);
                            view.currReqQty_tv.setText("Required Quantity : ");
                            view.txtReqQuantity.setText(array.getJSONObject(position).getString("required_qty"));
                            view.currReqQty_tv.setVisibility(View.GONE);
                            view.txtReqQuantity.setVisibility(View.GONE);

                            //current available qty
                            view.availableQty_tv.setText("Current Available Quantity : ");
                            //change index name i.e. available_qty : but  need here required qty but now fine because of no time
                            String required_qty = array.getJSONObject(position).getString("available_qty");
                            String allotted = array.getJSONObject(position).getString("allotted");
                            int curr_req_qty = Integer.valueOf(required_qty) - Integer.valueOf(allotted);
                            view.availableQty.setText(""+curr_req_qty);

                            view.booked_qty_li.setVisibility(View.VISIBLE);
                            view.bookedQty_tv.setText("Required Quantity : ");
                            view.bookedQty.setText(array.getJSONObject(position).getString("available_qty"));
                        }
                    }





                }


                view.txtId.setText(array.getJSONObject(position).getString("offer_id"));

                if (array.getJSONObject(position).getString("is_favorite").equalsIgnoreCase("Y")) {
                    view.SellerListAdapterImgUnFav.setVisibility(View.VISIBLE);
                } else {
                    view.SellerListAdapterImgUnFav.setVisibility(View.GONE);

                }


                view.txtMillName.setText(array.getJSONObject(position).getString("company_name"));

                view.txtGrade.setText(array.getJSONObject(position).getString("category"));

                view.txtDate.setText(DTU.changeTimeFormat(array.getJSONObject(position).getString("date"), array.getJSONObject(position).getString("time")));


                if (array.getJSONObject(position).has("type")) {
                    if (array.getJSONObject(position).getString("type").equalsIgnoreCase("Tender")) {
                        view.txtRatePerQtl.setText(array.getJSONObject(position).getString("tender_price"));
                    } else {
                        view.txtRatePerQtl.setText(array.getJSONObject(position).getString("price_per_qtl"));
                    }
                } else {
                    view.txtRatePerQtl.setText(array.getJSONObject(position).getString("price_per_qtl"));
                }

            } catch (JSONException e) {

                T.e("JSONException : "+e);
                e.printStackTrace();
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent in = new Intent(ctx, BookingDetailsActivity.class);
                        in.putExtra("flag", "Seller");
                        in.putExtra("flag_another", "Na");
                        in.putExtra("type", type);
                        in.putExtra("data", String.valueOf(array.getJSONObject(position)));
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
