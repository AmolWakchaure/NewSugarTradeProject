package com.prosolstech.sugartrade.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.prosolstech.sugartrade.R;

import org.json.JSONArray;
import org.json.JSONException;

public class QueryDetailsAdapter extends BaseAdapter {
    JSONArray array;
    Context context;

    public QueryDetailsAdapter(Context context, JSONArray array) {
        this.context = context;
        this.array = array;
    }

    @Override
    public int getCount() {
        if (array != null && array.length() > 0) {
            return array.length();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (array != null && array.length() > 0) {
            try {
                return array.getJSONObject(position);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        int position1 = position;
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.query_list, null);
        holder = new ViewHolder(convertView);
        convertView.setTag(holder);

        try {

            if (array.getJSONObject(position).getString("type").equalsIgnoreCase("User")) {
                holder.llUser.setVisibility(View.VISIBLE);
                holder.txtQuery.setText(array.getJSONObject(position).getString("message"));
            } else {
                holder.llAdmin.setVisibility(View.VISIBLE);
                holder.txtReply.setText(array.getJSONObject(position).getString("message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        convertView.setTag(holder);
        return convertView;

    }

    static class ViewHolder {
        public TextView txtQuery, txtReply;
        LinearLayout llUser, llAdmin;

        public ViewHolder(View convertView) {
            txtQuery = (TextView) convertView.findViewById(R.id.QueryDetailsAdapterTxtQuery);
            txtReply = (TextView) convertView.findViewById(R.id.QueryDetailsAdapterTxtReply);
            llUser = (LinearLayout) convertView.findViewById(R.id.LinaerLayoutUser);
            llAdmin = (LinearLayout) convertView.findViewById(R.id.LinaerLayoutAdmin);
        }
    }
}

