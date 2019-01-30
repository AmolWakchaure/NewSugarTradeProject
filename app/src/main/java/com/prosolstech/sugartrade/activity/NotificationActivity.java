package com.prosolstech.sugartrade.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.prosolstech.sugartrade.R;
import com.prosolstech.sugartrade.adapter.NotificationAdapter;
import com.prosolstech.sugartrade.database.DataBaseHelper;
import com.prosolstech.sugartrade.util.ItemAnimation;

import org.json.JSONArray;

public class NotificationActivity extends AppCompatActivity {

    Context context;
    private RecyclerView recyclerView;
    private NotificationAdapter mAdapter;
    private int animation_type = ItemAnimation.BOTTOM_UP;

    private DataBaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        context = NotificationActivity.this;
        db = new DataBaseHelper(context);
        setToolBar();
        intitializeUI();
    }

    private void intitializeUI() {
        recyclerView = (RecyclerView) findViewById(R.id.NotificationActivityRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        animation_type = ItemAnimation.FADE_IN;
        setAdapter(DataBaseHelper.DBNotification.getList());

    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setAdapter(JSONArray array) {
        //set data and list adapter
        if (array.length() > 0) {
            mAdapter = new NotificationAdapter(this, array, animation_type);
            recyclerView.setAdapter(mAdapter);

            // on item list clicked
            mAdapter.setOnItemClickListener(new NotificationAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                }
            });
        } else {
            Toast.makeText(context, "Oops ! notification not found.", Toast.LENGTH_SHORT).show();
        }
    }
}
