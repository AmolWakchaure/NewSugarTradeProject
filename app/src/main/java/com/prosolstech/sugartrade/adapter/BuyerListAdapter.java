package com.prosolstech.sugartrade.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import com.prosolstech.sugartrade.activity.BuyerListActivity;
import com.prosolstech.sugartrade.activity.WebViewActivity;
import com.prosolstech.sugartrade.classes.Constants;
import com.prosolstech.sugartrade.classes.T;
import com.prosolstech.sugartrade.model.BuyerInfoDetails;
import com.prosolstech.sugartrade.util.ACU;
import com.prosolstech.sugartrade.util.Constant;
import com.prosolstech.sugartrade.util.ItemAnimation;
import com.prosolstech.sugartrade.util.VU;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BuyerListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context ctx;
    private OnItemClickListener mOnItemClickListener;
    private int animation_type = 0;
    //private JSONArray array;
    private Dialog dialog;
    private RecyclerView recyclerView;
    private ImageView imgClose;

    BuyerListActivity buyerListActivity;
    private ArrayList<BuyerInfoDetails> byerDetails = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(View view, Integer obj, int position);
    }


    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public BuyerListAdapter(Context context, int animation_type, ArrayList<BuyerInfoDetails> byerDetails,
                            BuyerListActivity buyerListActivity) {
        ctx = context;
        //this.array = array;
        this.animation_type = animation_type;
        this.buyerListActivity = buyerListActivity;
        this.byerDetails.addAll(byerDetails);
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView txtName;
        ImageView imgUnFav, imgFav, imgUnlock, imglock;
        Button btnViewRating;
        RatingBar ratingBar;


        public OriginalViewHolder(View v)
        {
            super(v);
            txtName = (TextView) v.findViewById(R.id.BuyerListAdapterTxtName);
            imgUnFav = (ImageView) v.findViewById(R.id.BuyerListAdapterImgUnFav);
            imgFav = (ImageView) v.findViewById(R.id.BuyerListAdapterImgFav);
            imgUnlock = (ImageView) v.findViewById(R.id.BuyerListAdapterImgUnBlock);
            imglock = (ImageView) v.findViewById(R.id.BuyerListAdapterImgBlock);
            ratingBar = (RatingBar) v.findViewById(R.id.BuyerListAdapterReviewDialogRatingBar);
            btnViewRating = (Button) v.findViewById(R.id.BuyerListAdapterBtnViewRating);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_buyer, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Log.e("BuyerListAdapter", "onBindViewHolder : " + position);
        if (holder instanceof OriginalViewHolder) {
            final OriginalViewHolder view = (OriginalViewHolder) holder;
           // Log.e("BuyerListAdapter", ": " + array.toString());
            try {

                final BuyerInfoDetails buyerInfoDetails = byerDetails.get(position);

                if (!buyerInfoDetails.getBuyerWEbLing().equals(Constants.NA))
                {
                    SpannableString content = new SpannableString(buyerInfoDetails.getBuyerName());
                    content.setSpan(new UnderlineSpan(), 0, buyerInfoDetails.getBuyerName().length(), 0);
                    view.txtName.setTextColor(ctx.getResources().getColor(R.color.light_blue_A700));
                    view.txtName.setText(content);

                    view.txtName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                if (buyerInfoDetails.getBuyerWEbLing().contains("http")) {
                                    links(buyerInfoDetails.getBuyerWEbLing());
                                } else {
                                    links("http://" + buyerInfoDetails.getBuyerWEbLing());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                else
                {
                    view.txtName.setText(buyerInfoDetails.getBuyerName());
                }


                view.ratingBar.setRating(Float.parseFloat(buyerInfoDetails.getBuyerRating()));

                if (buyerInfoDetails.getBuyerFavStatus().equalsIgnoreCase("N"))
                {
                    view.imgUnFav.setImageResource(R.drawable.unfavorite);
                    //view.imgUnFav.setVisibility(View.VISIBLE);
                   // view.imgFav.setVisibility(View.GONE);
                }
                else
                {
                    view.imgUnFav.setImageResource(R.drawable.favorite);
                   // view.imgUnFav.setVisibility(View.GONE);
                   // view.imgFav.setVisibility(View.VISIBLE);
                }
                if (buyerInfoDetails.getBuyerblockStatus().equalsIgnoreCase("N"))
                {
                    view.imgUnlock.setVisibility(View.VISIBLE);
                    view.imglock.setVisibility(View.GONE);
                }
                else
                {
                    view.imgUnlock.setVisibility(View.GONE);
                    view.imglock.setVisibility(View.VISIBLE);
                }

                view.imgUnFav.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                       // T.t(""+buyerInfoDetails.getBuyerFavStatus());
                        if (buyerInfoDetails.getBuyerFavStatus().equalsIgnoreCase("N"))
                        {
                            likeData(buyerInfoDetails.getBuyerId(),"like");
                            view.imgUnFav.setImageResource(R.drawable.favorite);
                            buyerListActivity.refreshList(byerDetails,position,"Y");
                        }
                        else
                        {
                            likeData(buyerInfoDetails.getBuyerId(),"unlike");
                            view.imgUnFav.setImageResource(R.drawable.unfavorite);
                            buyerListActivity.refreshList(byerDetails,position,"N");
                        }

                    }
                });

                view.imglock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                        builder.setCancelable(false);
                        builder.setMessage("Do you want to Unblock this person?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                blockDataForunBlock(buyerInfoDetails.getBuyerId());
                                view.imgUnlock.setVisibility(View.VISIBLE);
                                view.imglock.setVisibility(View.GONE);
                                dialog.dismiss();


                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                });
                view.imgUnlock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                        builder.setCancelable(false);
                        builder.setMessage("Do you want to block this person?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                    blockData(buyerInfoDetails.getBuyerId());
                                    view.imgUnlock.setVisibility(View.GONE);
                                    view.imglock.setVisibility(View.VISIBLE);
                                    dialog.dismiss();

                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();

                    }
                });

                view.btnViewRating.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (VU.isConnectingToInternet(ctx)) {

                                allReview(buyerInfoDetails.getBuyerId());

                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
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
        return byerDetails.size();
    }

    private int lastPosition = -1;
    private boolean on_attach = true;

    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, on_attach ? position : -1, animation_type);
            lastPosition = position;
        }
    }

    private void likeData(final String strUserId,final String likeUnlikeStatus) {
        String url = "";
        final ProgressDialog pDialog = new ProgressDialog(ctx);
        pDialog.setMessage("Please wait while data fetch from server...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(ctx);


        url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/like";      //for server
        Log.e("likeData", " ....." + url);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Log.e("likeData", " RESPONSE " + response);
                        pDialog.dismiss();
                        try {
                            JSONObject jobj = new JSONObject(response);
                            if (jobj.getString("message").equalsIgnoreCase("success")) {
                                Toast.makeText(ctx, "Your data save", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ctx, "Please Try Again", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("No Response", "FOUND");
                pDialog.dismiss();
                Toast.makeText(ctx, "No Data Found", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", strUserId);
                params.put("user_by", ACU.MySP.getFromSP(ctx, ACU.MySP.ID, ""));
                params.put("likeUnlikeStatus", likeUnlikeStatus);
                Log.e("BUYERlikeData_PARAMS", " : " + params.toString());

                return params;
            }
        };
        queue.add(stringRequest);
    }


    private void likeDataForUnfavo(final String strUserId) {
        String url = "";
        final ProgressDialog pDialog = new ProgressDialog(ctx);
        pDialog.setMessage("Please wait while data fetch from server...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(ctx);


        url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/unlike";      //for server
        Log.e("likeData", " ....." + url);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Log.e("likeData", " RESPONSE " + response);
                        pDialog.dismiss();
                        try {
                            JSONObject jobj = new JSONObject(response);
                            if (jobj.getString("message").equalsIgnoreCase("success")) {
                                Toast.makeText(ctx, "Your data save", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ctx, "Please Try Again", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("No Response", "FOUND");
                pDialog.dismiss();
                Toast.makeText(ctx, "No Data Found", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", strUserId);
                params.put("userby", ACU.MySP.getFromSP(ctx, ACU.MySP.ID, ""));
                params.put("role", ACU.MySP.getFromSP(ctx, ACU.MySP.ROLE, ""));

                Log.e("BUYERlikeData_PARAMS", " : " + params.toString());

                return params;
            }
        };
        queue.add(stringRequest);
    }

    private void blockData(final String strUserId) {
        String url = "";
        final ProgressDialog pDialog = new ProgressDialog(ctx);
        pDialog.setMessage("Please wait while data fetch from server...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(ctx);


        url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/block";      //for server
        Log.e("blockData", " ....." + url);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Log.e("blockData", " RESPONSE " + response);
                        pDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("message").equalsIgnoreCase("success")) {
                                Toast.makeText(ctx, "Your data save", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ctx, "Please Try Again", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("No Response", "FOUND");
                pDialog.dismiss();
                Toast.makeText(ctx, "No Data Found", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", strUserId);
                params.put("userby", ACU.MySP.getFromSP(ctx, ACU.MySP.ID, ""));
                params.put("role", ACU.MySP.getFromSP(ctx, ACU.MySP.ROLE, ""));

                Log.e("BuyerblockData_params", " : " + params.toString());

                return params;
            }
        };
        queue.add(stringRequest);
    }

    private void blockDataForunBlock(final String strUserId) {
        String url = "";
        final ProgressDialog pDialog = new ProgressDialog(ctx);
        pDialog.setMessage("Please wait while data fetch from server...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(ctx);


        url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/unblock";      //for server
        Log.e("blockData", " ....." + url);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Log.e("blockData", " RESPONSE " + response);
                        pDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("message").equalsIgnoreCase("success")) {
                                Toast.makeText(ctx, "Your data save", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ctx, "Please Try Again", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("No Response", "FOUND");
                pDialog.dismiss();
                Toast.makeText(ctx, "No Data Found", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", strUserId);
                params.put("userby", ACU.MySP.getFromSP(ctx, ACU.MySP.ID, ""));
                params.put("role", ACU.MySP.getFromSP(ctx, ACU.MySP.ROLE, ""));

                Log.e("BuyerblockData_params", " : " + params.toString());

                return params;
            }
        };
        queue.add(stringRequest);
    }

    public void links(String url) {
        if (VU.isConnectingToInternet(ctx)) {
            Intent in_contact = new Intent(ctx, WebViewActivity.class);
            in_contact.putExtra("url", url);
            ctx.startActivity(in_contact);
        }
    }

    public void showCustomDialog() {
        dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_review);
        dialog.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        // if button is clicked, close the custom dialog

        imgClose = (ImageView) dialog.findViewById(R.id.DailogReviewImgCancel);
        recyclerView = (RecyclerView) dialog.findViewById(R.id.DailogReviewRecyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(ctx, 1));
        recyclerView.setHasFixedSize(true);
        animation_type = ItemAnimation.FADE_IN;
        dialog.show();

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    private void allReview(final String strId) {
        String url = "";
        final ProgressDialog pDialog = new ProgressDialog(ctx);
        pDialog.setMessage("Please wait while data fetch from server...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(ctx);
        url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/getRatingReview";      //for server
        Log.e("BUYER_BothURL", " ....." + url);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        Log.e("allReview", " RESPONSE " + response);
                        pDialog.dismiss();
                        showCustomDialog();
                        setReviewListAdapter(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("No Response", "FOUND");
                pDialog.dismiss();
                Toast.makeText(ctx, "No Data Found", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("id", strId);
                Log.e("allReview", " ..... " + params.toString());

                return params;
            }
        };
        queue.add(stringRequest);
    }

    private void setReviewListAdapter(String result) {
        try {
            JSONArray array;
            Log.e("setReviewAdapter", " RESULT " + result.trim());
            array = new JSONArray(result);
            if (array != null && array.length() > 0) {
                ReviewListAdapter reviewListAdapter = new ReviewListAdapter(ctx, array, animation_type);         // if user login as a "Buyer" than show Seller offer ist
                recyclerView.setAdapter(reviewListAdapter);
            } else {
                Toast.makeText(ctx, "No Data Found", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setFilter(ArrayList<BuyerInfoDetails> countryModels)
    {
        byerDetails = new ArrayList<>();
        byerDetails.addAll(countryModels);
        notifyDataSetChanged();
    }

}