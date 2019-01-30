package com.prosolstech.sugartrade;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

public class LoginActiveDialog extends Dialog {

    private Context context;
    private TextView textView, yes;
    private String meessage = "Your Account has been Deactivated .Please Contact Admin!";
    private Listner listner;

    public LoginActiveDialog(@NonNull Context context, Listner listner) {
        super(context);
        this.context = context;
        this.listner = listner;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_isactive);
        setCanceledOnTouchOutside(false);
        textView = findViewById(R.id.dia_msg_0);
        yes = findViewById(R.id.yes);
        textView.setText(meessage);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listner.onClkic();

            }
        });


    }


    public interface Listner {
        void onClkic();
    }

}
