package com.contafe.trakttv.utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.contafe.trakttv.R;


/**
 * Created by Natig on 7/30/16.
 */
public class CustomAlertDialog {

    private Typeface regularTypeface;
    private Typeface boldTypeface;

    private String title;
    private String message;
    private String buttonText;
    private AlertDialog alertDialog;
    private LinearLayout linearLayout;
    private TextView titleTextView;
    private TextView messageTextView;
    private Button actionButton;

    private ImageView icon;
    private int iconResource = -1;

    private Context context;
    private AlertDialog.Builder alertDialogBuilder;

    // colors
    private Drawable buttonStyle;
    private int textColor = -1;
    private int titleColor = -1;
    private int messageColor = -1;
    private int buttonTextColor = -1;
    private int backgroundColor = -1;
    private boolean cancelable;

    public CustomAlertDialog(Context context) {
        this.context = context;
    }

    public void setIconResource(int iconResource) {
        this.iconResource = iconResource;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setMessageColor(int messageColor) {
        this.messageColor = messageColor;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public void setButtonTextColor(int buttonTextColor) {
        this.buttonTextColor = buttonTextColor;
    }

    public void setButtonStyle(Drawable buttonStyle) {
        this.buttonStyle = buttonStyle;
    }

    public void setRegularTypeface(Typeface regularTypeface) {
        this.regularTypeface = regularTypeface;
    }

    public void setBoldTypeface(Typeface boldTypeface) {
        this.boldTypeface = boldTypeface;
    }

    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
    }

    public void show() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_alert, null);

        // initializing AlertDialogBuilder
        alertDialogBuilder = new AlertDialog.Builder(context);
        // setting view for AlertDialogBuilder
        alertDialogBuilder.setView(promptView);

        //initializing UI Controls
        linearLayout = (LinearLayout) promptView.findViewById(R.id.dialogLinearLayout);
        icon = (ImageView) promptView.findViewById(R.id.dialogIcon);
        titleTextView = (TextView) promptView.findViewById(R.id.dialogTitle);
        messageTextView = (TextView) promptView.findViewById(R.id.dialogMessage);
        actionButton = (Button) promptView.findViewById(R.id.dialogActionButton);

        actionButton.setTransformationMethod(null);

        // setting typeface of each UI control
        titleTextView.setTypeface(boldTypeface);
        messageTextView.setTypeface(regularTypeface);
        actionButton.setTypeface(regularTypeface);

        //setting icon of the dialog
        if (iconResource != -1) {
            Drawable iconDrawable = context.getResources().getDrawable(iconResource);
            icon.setImageDrawable(iconDrawable);
            icon.setVisibility(View.VISIBLE);
        } else {
            icon.setVisibility(View.GONE);
        }

        // setting colors of each UI control
        if (titleColor != -1)
            titleTextView.setTextColor(context.getResources().getColor(titleColor));
        else
            titleTextView.setTextColor(context.getResources().getColor(textColor));

        if (messageColor != -1)
            messageTextView.setTextColor(context.getResources().getColor(messageColor));
        else
            messageTextView.setTextColor(context.getResources().getColor(textColor));

        if (buttonTextColor != -1)
            actionButton.setTextColor(context.getResources().getColor(buttonTextColor));
        else
            actionButton.setTextColor(context.getResources().getColor(textColor));

        // setting button style
        if (buttonStyle != null)
            actionButton.setBackground(buttonStyle);

        ColorDrawable backgroundColorDrawable = new ColorDrawable(context.getResources().getColor(backgroundColor));
        linearLayout.setBackground(backgroundColorDrawable);

        // is button cancelable
        alertDialogBuilder.setCancelable(cancelable);

        // setting texts
        titleTextView.setText(title);
        messageTextView.setText(message);
        actionButton.setText(buttonText);

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonClicked();
                alertDialog.cancel();
            }
        });

    }

    public void buttonClicked() {

    }

    public boolean isShowing() {
        if (alertDialog != null && alertDialog.isShowing())
            return true;
        return false;
    }
}
