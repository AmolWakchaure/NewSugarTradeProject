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
import android.widget.EditText;
import android.widget.ImageView;
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
import com.prosolstech.sugartrade.activity.VehicleDetailsActivity;
import com.prosolstech.sugartrade.util.ACU;
import com.prosolstech.sugartrade.util.DTU;
import com.prosolstech.sugartrade.util.ItemAnimation;
import com.prosolstech.sugartrade.util.VU;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MyRequestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context ctx;
    private OnItemClickListener mOnItemClickListener;
    private int animation_type = 0;
    JSONArray array;
    String userType, strRecId = "", strAllocate = "",strRecordID = "";
    private RefreshListner refreshListner;

    public interface OnItemClickListener {
        void onItemClick(View view, Integer obj, int position);
    }


    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public MyRequestAdapter(Context context, JSONArray array, int animation_type, String userType, RefreshListner refreshListner,String strRecordID) {
        ctx = context;
        this.array = array;
        this.animation_type = animation_type;
        this.userType = userType;
        this.refreshListner = refreshListner;
        this.strRecordID = strRecordID;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView txtMillName, txtDate,
        //                txtGrade,
        txtRatePerQtl, txtQty, txt,

//        txtSeason,

        allot_tv,currentReqQty_tv,MyRequestAdapterCurrentReqQuantity;
        EditText edtAllocate;
        Button btnAccept, btnReject, btnAccRej;
        LinearLayout llBothBtn, llSingleBtn;
        ImageView SellerListAdapterImgUnFav;
        private View view_id;


        public OriginalViewHolder(View v) {
            super(v);
            txtMillName = (TextView) v.findViewById(R.id.MyRequestAdapterTxtName);
            txtDate = (TextView) v.findViewById(R.id.MyRequestAdapterTxtDate);
            currentReqQty_tv = (TextView) v.findViewById(R.id.currentReqQty_tv);
            txtRatePerQtl = (TextView) v.findViewById(R.id.MyRequestAdapterTxtRatePerQtl);
            txtQty = (TextView) v.findViewById(R.id.MyRequestAdapterTxtReqQty);
            txt = (TextView) v.findViewById(R.id.txt);

            MyRequestAdapterCurrentReqQuantity = (TextView) v.findViewById(R.id.MyRequestAdapterCurrentReqQuantity);
            edtAllocate = (EditText) v.findViewById(R.id.MyRequestAdapterEdtAllocate);

            btnAccept = (Button) v.findViewById(R.id.MyRequestAdapterBtnAccept);
            btnReject = (Button) v.findViewById(R.id.MyRequestAdapterBtnReject);

            llBothBtn = (LinearLayout) v.findViewById(R.id.MyRequestAdapterLinearLayoutBothBtn);
            llSingleBtn = (LinearLayout) v.findViewById(R.id.MyRequestAdapterLinearLayoutSingleBtn);
            btnAccRej = (Button) v.findViewById(R.id.MyRequestAdapterBtnAcceptReject);
            SellerListAdapterImgUnFav = v.findViewById(R.id.SellerListAdapterImgUnFav);
            allot_tv = v.findViewById(R.id.allot_tv);
            view_id = v.findViewById(R.id.view_id);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_request_list, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Log.e("MyReqADAPTER", "onBindViewHolder : " + position);
        if (holder instanceof OriginalViewHolder) {
            final OriginalViewHolder view = (OriginalViewHolder) holder;
            Log.e("MyReqADAPTER_ARRAY", ": " + array.toString());

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
                view.txtDate.setText(DTU.changeDateTimeFormat(array.getJSONObject(position).getString("requested_date")));
                view.txtMillName.setText(array.getJSONObject(position).getString("company_name"));


                if (ACU.MySP.getFromSP(ctx, ACU.MySP.ROLE, "").equals("Seller"))
                {
                    //Current Available Qty
                    view.currentReqQty_tv.setText("Current Available Qty : ");
                    view.MyRequestAdapterCurrentReqQuantity.setText(array.getJSONObject(position).getString("available_qty"));
                    //Required Qty
                    view.txt.setText("Required Qty : ");
                    view.txtQty.setText(array.getJSONObject(position).getString("original_qty"));
                    view.allot_tv.setVisibility(View.VISIBLE);
                    view.allot_tv.setText("Alloted Qty: ");
                    view.edtAllocate.setVisibility(View.VISIBLE);
                }
                else
                {
                    //Current Required Qty
                    view.currentReqQty_tv.setText("Current Required Qty : ");
                    view.MyRequestAdapterCurrentReqQuantity.setText(array.getJSONObject(position).getString("curr_req_qty"));
                    //Available Qty
                    view.txt.setText("Available Qty : ");
                    view.txtQty.setText(array.getJSONObject(position).getString("available_qty"));
                    view.allot_tv.setVisibility(View.GONE);
                    view.edtAllocate.setVisibility(View.GONE);
                }
                if (array.getJSONObject(position).getString("price_per_qtl").equalsIgnoreCase("0"))
                {
                    view.txtRatePerQtl.setText(array.getJSONObject(position).getString("tender_price"));
                }
                else
                {
                    view.txtRatePerQtl.setText(array.getJSONObject(position).getString("price_per_qtl"));
                }
                view.edtAllocate.setText(array.getJSONObject(position).getString("offer_required_qty"));           // add on 24-08-18
                if (array.getJSONObject(position).getString("is_interested").equalsIgnoreCase("") ||
                        array.getJSONObject(position).getString("is_interested").equalsIgnoreCase("null"))
                {
                    view.llBothBtn.setVisibility(View.VISIBLE);
                    view.llSingleBtn.setVisibility(View.GONE);
                }
                else if (array.getJSONObject(position).getString("is_interested").equalsIgnoreCase("Accept"))
                {
                    view.llBothBtn.setVisibility(View.GONE);
                    view.llSingleBtn.setVisibility(View.VISIBLE);
                    view.view_id.setVisibility(View.VISIBLE);
                    view.btnAccRej.setText("Accepted");
                    view.btnAccRej.setTextColor(ctx.getResources().getColor(R.color.green_500));
                }
                else if (array.getJSONObject(position).getString("is_interested").equalsIgnoreCase("Reject"))
                {
                    view.llBothBtn.setVisibility(View.GONE);
                    view.llSingleBtn.setVisibility(View.VISIBLE);
                    view.btnAccRej.setText("Rejected");
                    view.view_id.setVisibility(View.VISIBLE);
                    view.btnAccRej.setTextColor(ctx.getResources().getColor(android.R.color.darker_gray));
                }

                view.btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (!userType.equalsIgnoreCase("Seller")) {
                                strRecId = array.getJSONObject(position).getString("buy_offer_id");
                            } else {
                                strRecId = array.getJSONObject(position).getString("sell_offer_id");
                            }
                            strAllocate = view.edtAllocate.getText().toString();
                            String btnAcc = view.btnAccept.getText().toString();
                            Log.e("btnRej", " : " + btnAcc);
                            Log.e("strRecId", " : " + strRecId);
                            Log.e("edtAllocate", " : " + view.edtAllocate.getText().toString().trim());
                            if (VU.isConnectingToInternet(ctx)) {

                                if (checkValueFor(view.edtAllocate)) {
                                    if (checkValue(view.txtQty, view.edtAllocate)) {
                                        requestSend(view, btnAcc, strRecId, strAllocate,strRecordID);
                                    } else {
                                        Toast.makeText(ctx, "Alloted value cannot be gretaer then Available Qty", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(ctx, "Alloted value cannot be set to Zero", Toast.LENGTH_SHORT).show();

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                view.btnReject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (!userType.equalsIgnoreCase("Seller")) {
                                strRecId = array.getJSONObject(position).getString("buy_offer_id");
                            } else {
                                strRecId = array.getJSONObject(position).getString("sell_offer_id");
                            }
                            String btnRej = view.btnReject.getText().toString();
                            Log.e("btnRej", " : " + btnRej);
                            Log.e("strRecId", " : " + strRecId);
                            if (VU.isConnectingToInternet(ctx)) {
                                requestSend1(view, btnRej, strRecId, strAllocate);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

               /* view.btnVehicleInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent in = new Intent(ctx, VehicleDetailsActivity.class);
                            in.putExtra("offer_id", array.getJSONObject(position).getString("id"));
                            ctx.startActivity(in);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });*/

            } catch (JSONException e) {
                e.printStackTrace();
            }
            setAnimation(view.itemView, position);
        }
    }


    public boolean checkValueFor(EditText editText) {

        if (leadingZerosCount(editText.getText().toString().trim()) > 0) {
            return false;
        }

        return true;
    }

    public static int leadingZerosCount(String s) {
        int zeros = 0;
        for (int i = 0; i < 3 && i < s.length(); i++) {
            if (s.charAt(i) == '0')
                zeros++;
            else
                break;
        }
        return zeros;
    }


    private boolean checkValue(TextView currentAvailQty, EditText alloted) {


        String curr = currentAvailQty.getText().toString().trim();
        String all = alloted.getText().toString().trim();


        if (Long.parseLong(all) > Long.parseLong(curr)) {
            return false;
        }

        return true;

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

    private void requestSend(final OriginalViewHolder viewHolder, final String buttonText, final String strid, final String allotted,final String strRecordID) {

        final ProgressDialog pDialog = new ProgressDialog(ctx);
        pDialog.setMessage("Please wait while data upload on server...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(ctx);
        String url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/updateBidStatus";      //for server

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        // Display the response string.
                        Log.e("requestSend", " RESPONSE " + response);
                        pDialog.dismiss();
                        try
                        {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("message").equalsIgnoreCase("success"))
                            {
                                Toast.makeText(ctx, "Data save", Toast.LENGTH_SHORT).show();
                                viewHolder.llBothBtn.setVisibility(View.GONE);
                                viewHolder.llSingleBtn.setVisibility(View.VISIBLE);
                                viewHolder.view_id.setVisibility(View.VISIBLE);
                                viewHolder.btnAccRej.setText("Accepted");
                                viewHolder.btnAccRej.setTextColor(ctx.getResources().getColor(R.color.green_500));
                                refreshListner.refresh(ctx);

                            }
                            else
                            {
                                Toast.makeText(ctx, "Try Again", Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            Toast.makeText(ctx, "Try Again", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.e("No_Response", "FOUND");
                pDialog.dismiss();
                Toast.makeText(ctx, "Try Again", Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<>();
                params.put("id", strid);
                params.put("strRecordID", strRecordID);//buy bi id to update curr req qty and  acq qty
                params.put("role", userType);
                params.put("allotted", allotted);
                params.put("userby", ACU.MySP.getFromSP(ctx, ACU.MySP.ID, ""));
                params.put("is_interested", buttonText);

                Log.e("PARAMS", " requestSend " + params.toString());
                return params;
            }
        };
        queue.add(stringRequest);
    }


    private void requestSend1(final OriginalViewHolder viewHolder, final String buttonText, final String strid, final String allotted) {

        final ProgressDialog pDialog = new ProgressDialog(ctx);
        pDialog.setMessage("Please wait while data upload on server...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(ctx);
        String url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/updateBidStatus";      //for server

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Log.e("requestSend", " RESPONSE " + response);
                        pDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("message").equalsIgnoreCase("success")) {
                                Toast.makeText(ctx, "Data save", Toast.LENGTH_SHORT).show();
//                                ((Activity) ctx).finish();

                                viewHolder.llBothBtn.setVisibility(View.GONE);
                                viewHolder.llSingleBtn.setVisibility(View.VISIBLE);
                                viewHolder.btnAccRej.setText("Rejected");
                                viewHolder.view_id.setVisibility(View.VISIBLE);
                                viewHolder.btnAccRej.setTextColor(ctx.getResources().getColor(android.R.color.darker_gray));

                                refreshListner.refresh(ctx);

                            } else {
                                Toast.makeText(ctx, "Try Again", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ctx, "Try Again", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("No_Response", "FOUND");
                pDialog.dismiss();
                Toast.makeText(ctx, "Try Again", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", strid);
                params.put("role", userType);
                params.put("allotted", allotted);
                params.put("userby", ACU.MySP.getFromSP(ctx, ACU.MySP.ID, ""));
                params.put("is_interested", buttonText);

                Log.e("PARAMS", " requestSend " + params.toString());
                return params;
            }
        };
        queue.add(stringRequest);
    }


    public interface RefreshListner {

        void refresh(Context context);
    }
}
