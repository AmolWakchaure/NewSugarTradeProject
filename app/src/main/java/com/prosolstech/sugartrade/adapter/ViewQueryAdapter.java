package com.prosolstech.sugartrade.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prosolstech.sugartrade.R;
import com.prosolstech.sugartrade.activity.QueryDetailsActivity;
import com.prosolstech.sugartrade.classes.T;
import com.prosolstech.sugartrade.util.DTU;

import org.json.JSONArray;
import org.json.JSONException;

public class ViewQueryAdapter extends RecyclerView.Adapter<ViewQueryAdapter.MyViewHolder> {


    Context context;
    JSONArray jsonArray;

    public ViewQueryAdapter(Context context, JSONArray jsonArray) {
        this.context = context;
        this.jsonArray = jsonArray;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtSubject, txtDate, txtQuery;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.txtSubject = (TextView) itemView.findViewById(R.id.ViewQueryAdapterTxtSubject);
            this.txtDate = (TextView) itemView.findViewById(R.id.ViewQueryAdapterTxtDate);
            this.txtQuery = (TextView) itemView.findViewById(R.id.ViewQueryAdapterTxtQuery);
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_query_layout, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        try {
            holder.txtSubject.setText("Reg. " + jsonArray.getJSONObject(position).getString("subject"));

            String strDateTime = jsonArray.getJSONObject(position).getString("created_date");
            //String[] strDate = strDateTime.split(" ");
           // String date = strDate[0];
            holder.txtDate.setText(T.formatTimeStamp(strDateTime));
            holder.txtQuery.setText(jsonArray.getJSONObject(position).getString("query"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        // implement setOnClickListener event on item view.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent in = new Intent(context, QueryDetailsActivity.class);
                    in.putExtra("query_id",jsonArray.getJSONObject(position).getString("id"));
                    context.startActivity(in);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return jsonArray.length();
    }
}