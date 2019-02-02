package com.prosolstech.sugartrade.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.prosolstech.sugartrade.R;
import com.prosolstech.sugartrade.adapter.NotificationAdapter;
import com.prosolstech.sugartrade.database.DataBaseHelper;
import com.prosolstech.sugartrade.util.ACU;
import com.prosolstech.sugartrade.util.ItemAnimation;

import org.json.JSONArray;

public class NotificationActivity extends AppCompatActivity {

    Context context;
    private RecyclerView recyclerView;
    private NotificationAdapter mAdapter;
    private int animation_type = ItemAnimation.BOTTOM_UP;

    private DataBaseHelper db;
    private CoordinatorLayout  drawer_layout;

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
        drawer_layout = (CoordinatorLayout) findViewById(R.id.drawer_layout);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_notification, menu);
        return super.onCreateOptionsMenu(menu);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.add_delete_notification:
                deleteNotification("all","");
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void deleteNotification(String status,String notiId) {


        try
        {
            JSONArray jsonArray = DataBaseHelper.DBNotification.getList();


            if(jsonArray.length() > 0)
            {
                if(status.equals("single"))
                {
                    //delete single notification
                    DataBaseHelper.DBNotification.deleteParticularNoti(notiId);
                    //refresh list
                    refreshList();
                }
                else
                {
                    //delete all notification
                    Snackbar snackbar = Snackbar
                            .make(drawer_layout, "Do you want to delete all notifications?", Snackbar.LENGTH_LONG)
                            .setAction("Delete", new View.OnClickListener() {
                                @Override
                                public void onClick(View view)
                                {

                                    //delete single notification
                                    DataBaseHelper.DBNotification.deleteAllNotes();
                                    //refresh list
                                    refreshList();


                                }
                            });

                    snackbar.show();
                }

            }
            else
            {
                Toast.makeText(context, "Oops ! notification not found.", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {

        }


    }

    private void refreshList() {

        JSONArray jsonArrayData = DataBaseHelper.DBNotification.getList();
        mAdapter = new NotificationAdapter(NotificationActivity.this, jsonArrayData, animation_type,NotificationActivity.this);
        recyclerView.setAdapter(mAdapter);
        Toast.makeText(context, "Notifications deleted.", Toast.LENGTH_SHORT).show();
    }


    private void setAdapter(JSONArray array) {
        //set data and list adapter
        if (array.length() > 0) {
            mAdapter = new NotificationAdapter(this, array, animation_type,NotificationActivity.this);
            recyclerView.setAdapter(mAdapter);


        } else {
            Toast.makeText(context, "Oops ! notification not found.", Toast.LENGTH_SHORT).show();
        }
    }
}
