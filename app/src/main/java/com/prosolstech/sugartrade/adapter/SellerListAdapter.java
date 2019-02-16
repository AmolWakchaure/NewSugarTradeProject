package com.prosolstech.sugartrade.adapter;

import android.app.Activity;
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
import com.prosolstech.sugartrade.activity.SellerListActivity;
import com.prosolstech.sugartrade.activity.WebViewActivity;
import com.prosolstech.sugartrade.classes.Constants;
import com.prosolstech.sugartrade.classes.T;
import com.prosolstech.sugartrade.model.BuyerInfoDetails;
import com.prosolstech.sugartrade.model.SellerInfoDetails;
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

public class SellerListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context ctx;
    private SellerListAdapter.OnItemClickListener mOnItemClickListener;
    private int animation_type = 0;
   // JSONArray array;
    Dialog dialog;
    RecyclerView recyclerView;
    ImageView imgClose;
    SellerListActivity sellerListActivity;

    private ArrayList<SellerInfoDetails> sellerDetails = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(View view, Integer obj, int position);
    }


    public void setOnItemClickListener(final SellerListAdapter.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public SellerListAdapter(Context context, int animation_type,ArrayList<SellerInfoDetails> sellerDetails,
                             SellerListActivity sellerListActivity) {
        ctx = context;
       // this.array = array;
        this.animation_type = animation_type;
        this.sellerListActivity = sellerListActivity;
        this.sellerDetails.addAll(sellerDetails);
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView txtName;
        ImageView imgUnFav, imgUnlock, imgFav, imgBlock;
        Button btnViewRating;
        RatingBar ratingBar;

        public OriginalViewHolder(View v) {
            super(v);
            txtName = (TextView) v.findViewById(R.id.SellerListAdapterTxtName);
            imgUnFav = (ImageView) v.findViewById(R.id.SellerListAdapterImgUnFav);
            imgUnlock = (ImageView) v.findViewById(R.id.SellerListAdapterImgUnBlock);
            imgFav = (ImageView) v.findViewById(R.id.SellerListAdapterImgFav);
            imgBlock = (ImageView) v.findViewById(R.id.SellerListAdapterImgBlock);
            ratingBar = (RatingBar) v.findViewById(R.id.SellerListAdapterReviewDialogRatingBar);
            btnViewRating = (Button) v.findViewById(R.id.SellerListAdapterBtnViewRating);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_seller, parent, false);
        vh = new SellerListAdapter.OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Log.e("SellerListAdapter", "onBindViewHolder : " + position);
        if (holder instanceof SellerListAdapter.OriginalViewHolder) {
            final SellerListAdapter.OriginalViewHolder view = (SellerListAdapter.OriginalViewHolder) holder;
            //Log.e("SellerListAdapter", ": " + array.toString());

            final SellerInfoDetails buyerInfoDetails = sellerDetails.get(position);

            try {
                if (!buyerInfoDetails.getSellerWEbLing().equalsIgnoreCase(Constants.NA)) {
                    SpannableString content = new SpannableString(buyerInfoDetails.getSellerName());
                    content.setSpan(new UnderlineSpan(), 0, buyerInfoDetails.getSellerName().length(), 0);
                    view.txtName.setTextColor(ctx.getResources().getColor(R.color.light_blue_A700));
                    view.txtName.setText(content);

                    view.txtName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                                if (buyerInfoDetails.getSellerWEbLing().contains("http")) {
                                    links(buyerInfoDetails.getSellerWEbLing());
                                } else {
                                    links("http://" + buyerInfoDetails.getSellerWEbLing());
                                }

                        }
                    });
                } else {
                    view.txtName.setText(buyerInfoDetails.getSellerName());
                }
//                view.txtName.setText(array.getJSONObject(position).getString("name"));

                view.ratingBar.setRating(Float.parseFloat(buyerInfoDetails.getSellerRating()));


                //fav and unfav
                if (buyerInfoDetails.getSellerFavStatus().equalsIgnoreCase("N"))
                {
                    view.imgUnFav.setImageResource(R.drawable.unfavorite);
                }
                else
                {
                    view.imgUnFav.setImageResource(R.drawable.favorite);
                }


                if (buyerInfoDetails.getSellerblockStatus().equalsIgnoreCase("N")) {

                    view.imgUnlock.setImageResource(R.drawable.unlocked);
                }
                else
                {

                    view.imgUnlock.setImageResource(R.drawable.locked);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            view.imgUnFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    T.e("getSellerFavStatus() : "+buyerInfoDetails.getSellerFavStatus());

                    if (buyerInfoDetails.getSellerFavStatus().equalsIgnoreCase("N"))
                    {

                        T.e("getSellerrId() : "+buyerInfoDetails.getSellerrId());
                        likeData(buyerInfoDetails.getSellerrId(),"like");
                        view.imgUnFav.setImageResource(R.drawable.favorite);
                        sellerListActivity.refreshList(sellerDetails,position,"Y");
                        //sellerListActivity.refreshList(position);
                        //view.imgUnFav.setVisibility(View.GONE);
                        //.imgFav.setVisibility(View.VISIBLE);

                    }
                    else
                    {
                        T.e("getSellerrId() : "+buyerInfoDetails.getSellerrId());
                        likeData(buyerInfoDetails.getSellerrId(),"unlike");
                        view.imgUnFav.setImageResource(R.drawable.unfavorite);
                        sellerListActivity.refreshList(sellerDetails,position,"N");
                        //view.imgUnFav.setVisibility(View.VISIBLE);
                        //view.imgFav.setVisibility(View.GONE);
                    }

                }
            });

            view.imgUnlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    //check if user block or unblock
                    if (buyerInfoDetails.getSellerblockStatus().equalsIgnoreCase("N"))
                    {
                        // if seller unblock found block it

                        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                        builder.setCancelable(false);
                        builder.setMessage("Do you want to block this person?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                dialog.dismiss();
                                //blockData(buyerInfoDetails.getSellerrId());
                                blockData(buyerInfoDetails.getSellerrId(),"block");
                                view.imgUnlock.setImageResource(R.drawable.locked);
                                sellerListActivity.refreshListBlockUnblock(sellerDetails,position,"Y");

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
                    else
                    {
                        // if seller block found unblock it
                        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                        builder.setCancelable(false);
                        builder.setMessage("Do you want to unblock this person?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                dialog.dismiss();
                                blockData(buyerInfoDetails.getSellerrId(),"unblock");
                                view.imgUnlock.setImageResource(R.drawable.unlocked);
                                sellerListActivity.refreshListBlockUnblock(sellerDetails,position,"N");

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

                }
            });
            /*view.imgUnlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                    builder.setCancelable(false);
                    builder.setMessage("Do you want to block this person?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                                blockData(buyerInfoDetails.getSellerrId());
                                view.imgUnlock.setVisibility(View.GONE);
                                view.imgBlock.setVisibility(View.VISIBLE);
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
            });*/

            view.btnViewRating.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (VU.isConnectingToInternet(ctx)) {

                            allReview(buyerInfoDetails.getSellerrId());

                    }
                }
            });

            setAnimation(view.itemView, position);
        }
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
        return sellerDetails.size();
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
                Log.e("SellerlikeData_PARAMS", " : " + params.toString());

                return params;
            }
        };
        queue.add(stringRequest);
    }
    private void blockData(final String strUserId,final String blockUnblockStatus) {
        String url = "";
        final ProgressDialog pDialog = new ProgressDialog(ctx);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(ctx);


        url = ACU.MySP.MAIN_URL + "sugar_trade/index.php/API/blockUnblockUser";      //for server
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
                params.put("blockUnblockStatus", blockUnblockStatus);
                Log.e("SellerlikeData_PARAMS", " : " + params.toString());

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

                Log.e("SellerblockData_params", " : " + params.toString());

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
    public void setFilter(ArrayList<SellerInfoDetails> countryModels)
    {
        sellerDetails = new ArrayList<>();
        sellerDetails.addAll(countryModels);
        notifyDataSetChanged();
    }
}