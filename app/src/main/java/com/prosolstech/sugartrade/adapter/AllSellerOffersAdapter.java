package com.prosolstech.sugartrade.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prosolstech.sugartrade.R;
import com.prosolstech.sugartrade.model.AllSellerOfferInfo;
import com.prosolstech.sugartrade.model.BuyBidModel;
import com.prosolstech.sugartrade.util.ItemAnimation;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class AllSellerOffersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context ctx;
    private OnItemClickListener mOnItemClickListener;
    private int animation_type = 0;
    //JSONArray array;
    ArrayList<AllSellerOfferInfo> SELLER_OFFER_INFO;

    public interface OnItemClickListener {
        void onItemClick(View view, Integer obj, int position);
    }


    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AllSellerOffersAdapter(Context context,  int animation_type,ArrayList<AllSellerOfferInfo> SELLER_OFFER_INFO) {
        ctx = context;
        //this.array = array;
        this.SELLER_OFFER_INFO = SELLER_OFFER_INFO;
        this.animation_type = animation_type;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView txtMillName, txtRate, txtGrade, txtSeason;


        public OriginalViewHolder(View v) {
            super(v);
            txtMillName = (TextView) v.findViewById(R.id.AllSellerOffersAdapterTxtName);
            txtRate = (TextView) v.findViewById(R.id.AllSellerOffersAdapterTxtRate);
            txtGrade = (TextView) v.findViewById(R.id.AllSellerOffersAdapterTxtGrade);
            txtSeason = (TextView) v.findViewById(R.id.AllSellerOffersAdapterTxtSeason);

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_seller_offer_post, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Log.e("MyReqADAPTER", "onBindViewHolder : " + position);
        if (holder instanceof OriginalViewHolder) {
            final OriginalViewHolder view = (OriginalViewHolder) holder;
            //Log.e("MyReqADAPTER_ARRAY", ": " + array.toString());

            AllSellerOfferInfo allSellerOfferInfo = SELLER_OFFER_INFO.get(position);

            view.txtMillName.setText(allSellerOfferInfo.getCompanyName());
            view.txtGrade.setText(allSellerOfferInfo.getGrade());
            view.txtSeason.setText(allSellerOfferInfo.getSeason());
            view.txtRate.setText(allSellerOfferInfo.getPricePerQtl());

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
        return SELLER_OFFER_INFO.size();
    }

    private int lastPosition = -1;
    private boolean on_attach = true;

    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, on_attach ? position : -1, animation_type);
            lastPosition = position;
        }
    }

    public void setFilter(ArrayList<AllSellerOfferInfo> countryModels)
    {
        SELLER_OFFER_INFO = new ArrayList<>();
        SELLER_OFFER_INFO.addAll(countryModels);
        notifyDataSetChanged();
    }
}
