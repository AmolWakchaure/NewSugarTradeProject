package com.prosolstech.sugartrade;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.prosolstech.sugartrade.activity.LinkActivity;
import com.prosolstech.sugartrade.activity.LinkUrlActivity;


public class MandatoryFieldsDialog extends Dialog {
    private Context context;
    private Button btnNegative;
    private Button noBtn;

    private TextView dialogMsgTv;
    private String txe;
    private DialogListner dialogListner;

    public MandatoryFieldsDialog(@NonNull Context context, DialogListner dialogListner) {
        super(context);
        this.context = context;
        this.dialogListner = dialogListner;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_mandatory);
        setCancelable(true);
        setCanceledOnTouchOutside(false);


        dialogMsgTv = findViewById(R.id.dialogMsgTv);
        btnNegative = findViewById(R.id.btnNegative);


        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }


}
