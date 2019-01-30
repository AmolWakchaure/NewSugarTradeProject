package com.prosolstech.sugartrade;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.prosolstech.sugartrade.activity.LinkActivity;
import com.prosolstech.sugartrade.activity.LinkUrlActivity;


public class ConfirmationDialog extends Dialog {
    private Context context;
    private Button btnNegative;
    private Button noBtn;

    private TextView dialogMsgTv;
    private String txe;
    private DialogListner dialogListner;

    public ConfirmationDialog(@NonNull Context context, DialogListner dialogListner) {
        super(context);
        this.context = context;
        this.dialogListner = dialogListner;
        }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_confirmation);
        setCancelable(false);
        setCanceledOnTouchOutside(false);


        dialogMsgTv = findViewById(R.id.dialogMsgTv);
        btnNegative = findViewById(R.id.btnNegative);


        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                dialogListner.click();

            }
        });

        dialogMsgTv.setLinkTextColor(Color.BLUE); // default link color for clickable span, we can also set it in xml by android:textColorLink=""
        ClickableSpan normalLinkClickSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, LinkUrlActivity.class);
                context.startActivity(intent);




            }

        };

        ClickableSpan noUnderLineClickSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, LinkActivity.class);
                context.startActivity(intent);


            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
                ds.setColor(Color.BLUE);
            }
        };

        makeLinks(dialogMsgTv, new String[]{
                "Privacy Policy", "Terms & Conditions"
        }, new ClickableSpan[]{
                normalLinkClickSpan, noUnderLineClickSpan
        });


    }


    public void makeLinks(TextView textView, String[] links, ClickableSpan[] clickableSpans) {
        SpannableString spannableString = new SpannableString(textView.getText());
        for (int i = 0; i < links.length; i++) {
            ClickableSpan clickableSpan = clickableSpans[i];
            String link = links[i];

            int startIndexOfLink = textView.getText().toString().indexOf(link);
            spannableString.setSpan(clickableSpan, startIndexOfLink,
                    startIndexOfLink + link.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textView.setHighlightColor(
                Color.TRANSPARENT); // prevent TextView change background when highlight
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(spannableString, TextView.BufferType.SPANNABLE);
    }


}
