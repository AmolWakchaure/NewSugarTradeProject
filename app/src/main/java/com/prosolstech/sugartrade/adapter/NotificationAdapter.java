package com.prosolstech.sugartrade.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.prosolstech.sugartrade.R;
import com.prosolstech.sugartrade.activity.NotificationActivity;
import com.prosolstech.sugartrade.util.DTU;
import com.prosolstech.sugartrade.util.ItemAnimation;
import com.prosolstech.sugartrade.util.VU;

import org.json.JSONArray;
import org.json.JSONException;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context ctx;
    private OnItemClickListener mOnItemClickListener;
    private int animation_type = 0;
    private NotificationActivity notificationActivity;
    JSONArray array;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }


    public NotificationAdapter(Context context, JSONArray array, int animation_type,NotificationActivity notificationActivity) {
        ctx = context;
        this.array = array;
        this.animation_type = animation_type;
        this.notificationActivity = notificationActivity;
    }


    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView name, description;
        private Button deleteNotification;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.name);
            description = (TextView) v.findViewById(R.id.description);
            deleteNotification = (Button) v.findViewById(R.id.deleteNotification);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_list_item, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;
            try {
                view.name.setText(array.getJSONObject(position).getString("title"));

                view.description.setText(DTU.changeDateTimeFormat(array.getJSONObject(position).getString("body")));

                final String notiId = array.getJSONObject(position).getString("id");
                view.deleteNotification.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        notificationActivity.deleteNotification("single",notiId);
                    }
                });
            } catch (JSONException e) {
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