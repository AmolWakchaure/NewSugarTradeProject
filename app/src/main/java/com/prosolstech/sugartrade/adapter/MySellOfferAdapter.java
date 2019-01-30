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
import android.widget.Toast;

import com.prosolstech.sugartrade.R;
import com.prosolstech.sugartrade.activity.BuySellDashBoardActivity;
import com.prosolstech.sugartrade.activity.MyRequestedActivity;
import com.prosolstech.sugartrade.activity.PlaceSellBidActivity;
import com.prosolstech.sugartrade.util.ACU;
import com.prosolstech.sugartrade.util.DTU;
import com.prosolstech.sugartrade.util.ItemAnimation;
import com.prosolstech.sugartrade.util.VU;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MySellOfferAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context ctx;
    private OnItemClickListener mOnItemClickListener;
    private int animation_type = 0;
    JSONArray array;
    private String flag;

    private TabLayout tabLayout;

    public interface OnItemClickListener {
        void onItemClick(View view, Integer obj, int position);
    }


    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public MySellOfferAdapter(Context context, JSONArray array, int animation_type, TabLayout tabLayout) {
        ctx = context;
        this.array = array;
        this.animation_type = animation_type;
        this.tabLayout = tabLayout;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView txtType, txtRate, txtDate, txtGrade, txtQuantity, txtId, typeTxt, id_tv, qty_val, start_time_tv, end_time_tv, org_sell_tv, claimed_tv;
        LinearLayout llRevert, llRate, llBlank;

        public OriginalViewHolder(View v) {
            super(v);
            txtId = (TextView) v.findViewById(R.id.id);
            txtType = (TextView) v.findViewById(R.id.MyOfferAdapterTxtType);
            txtRate = (TextView) v.findViewById(R.id.MyOfferActivityTxtRate);
            txtDate = (TextView) v.findViewById(R.id.MyOfferActivityTxtDate);
            txtGrade = (TextView) v.findViewById(R.id.MyOfferActivityTxtGrade);
            txtQuantity = (TextView) v.findViewById(R.id.MyOfferActivityTxtQuantity);
            llRate = (LinearLayout) v.findViewById(R.id.MyOfferAdapterLInearLayoutRate);
            llBlank = (LinearLayout) v.findViewById(R.id.MyOfferAdapterLInearLayoutBlank);
            llRevert = (LinearLayout) v.findViewById(R.id.MyOfferActivityLinearLayoutRevert);
            typeTxt = v.findViewById(R.id.typeTxt);
            id_tv = v.findViewById(R.id.id_tv);
            qty_val = v.findViewById(R.id.qty_val);
            start_time_tv = v.findViewById(R.id.start_time_tv);
            end_time_tv = v.findViewById(R.id.end_time_tv);
            org_sell_tv = v.findViewById(R.id.org_sell_tv);
            claimed_tv = v.findViewById(R.id.claimed_tv);

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sell_bid_test, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Log.e("onBindViewHolder", "onBindViewHolder : " + position);
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;
            Log.e("SELL_TOSTRING", ": " + array.toString());

            try {
                view.txtType.setVisibility(View.VISIBLE);
                view.typeTxt.setVisibility(View.VISIBLE);
                view.start_time_tv.setText(DTU.changeTime(array.getJSONObject(position).getString("bid_start_time")));

                long hours5 = Long.parseLong(array.getJSONObject(position).getString("bid_end_time")) / 60; //since both are ints, you get an int
                long minutes5 = Long.parseLong(array.getJSONObject(position).getString("bid_end_time")) % 60;


                String timeLeftFormatted1 = String.format(Locale.getDefault(), "%02d:%02d", hours5, minutes5);
                view.end_time_tv.setText(timeLeftFormatted1);


                if (ACU.MySP.getFromSP(ctx, ACU.MySP.ROLE, "").equals("Seller")) {
                    view.org_sell_tv.setText(array.getJSONObject(position).getString("available_qty"));
                    if (array.getJSONObject(position).getString("claimed").equalsIgnoreCase("null")) {
                        view.claimed_tv.setText("0");
                    } else {
                        view.claimed_tv.setText(array.getJSONObject(position).getString("claimed"));
                        }

                    view.qty_val.setText("Current Available Qty: ");
                    view.id_tv.setText("Sell Post Id:");
                } else {
                    view.qty_val.setText("Required Qty");
                    view.id_tv.setText("Buy Post Id:");
                }
                view.txtId.setText(array.getJSONObject(position).getString("id"));
                view.txtType.setText(array.getJSONObject(position).getString("type"));


                view.txtDate.setText(DTU.changeDateTimeFormat(array.getJSONObject(position).getString("created_date")));
                view.txtGrade.setText(array.getJSONObject(position).getString("category"));


                view.txtQuantity.setText(array.getJSONObject(position).getString("original_qty"));


                if (!array.getJSONObject(position).getString("price_per_qtl").equalsIgnoreCase("0")) {
                    view.llRate.setVisibility(View.VISIBLE);
                    view.llBlank.setVisibility(View.GONE);
                    view.txtRate.setText(array.getJSONObject(position).getString("price_per_qtl"));
                } else {
                    view.llRate.setVisibility(View.GONE);
                    view.llBlank.setVisibility(View.GONE);
                }


                Log.e("SELLER", " : " + array.getJSONObject(position).getString("offer_required_qty"));
                if (array.getJSONObject(position).getString("offer_required_qty").equalsIgnoreCase("") || array.getJSONObject(position).getString("offer_required_qty").equalsIgnoreCase("null")) {
                    view.llRevert.setVisibility(View.GONE);
                    tabLayout.getTabAt(1).setText("My Post");

                } else {
                    view.llRevert.setVisibility(View.VISIBLE);
                    String tt = "MyPost●";
                    tabLayout.getTabAt(1).setText(tt);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent in = new Intent(ctx, PlaceSellBidActivity.class);
                        in.putExtra("flag", "update");
                        in.putExtra("data", String.valueOf(array.getJSONObject(position)));
                        ctx.startActivity(in);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            view.llRevert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent in = new Intent(ctx, MyRequestedActivity.class);
                        in.putExtra("check_flag", ACU.MySP.getFromSP(ctx, ACU.MySP.ROLE, ""));
                        in.putExtra("value", String.valueOf(array.getJSONObject(position)));
                        ctx.startActivity(in);
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


