package com.aaptrix.activitys.student;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.aaptrix.R;
import com.google.android.material.appbar.AppBarLayout;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class PaymentSuccess extends AppCompatActivity {

    Toolbar toolbar;
    AppBarLayout appBarLayout;
    TextView tool_title;
    String selToolColor, selStatusColor, selTextColor1;
    TextView paymentStatus, txnRef, txnId, txndate, txnamount;
    ImageView txnImage;
    String strAmount, strId, strRef, strstatus;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setTitle("");
        setResult(RESULT_OK);
        appBarLayout = findViewById(R.id.appBarLayout);
        tool_title = findViewById(R.id.tool_title);
        paymentStatus = findViewById(R.id.payment_status);
        txnRef = findViewById(R.id.txn_ref);
        txnId = findViewById(R.id.transaction_id);
        txndate = findViewById(R.id.transaction_date);
        txnamount = findViewById(R.id.transaction_amount);
        txnImage = findViewById(R.id.payment_img);

        strAmount = getIntent().getStringExtra("amount");
        strId = getIntent().getStringExtra("txnId");
        strRef = getIntent().getStringExtra("txnRef");
        strstatus = getIntent().getStringExtra("status");

        txnRef.setText(strRef);
        txnId.setText(strId);
        txnamount.setText(strAmount);

        switch (strstatus) {
            case "SUCCESS":
                paymentStatus.setText("Payment Successful");
                Picasso.with(this).load(R.drawable.payment_success).into(txnImage);
                break;
            case "FAILURE":
                paymentStatus.setText("Payment Failed");
                Picasso.with(this).load(R.drawable.payment_failed).into(txnImage);
                break;
            case "SUBMITTED":
                paymentStatus.setText("Payment Pending");
                Picasso.with(this).load(R.drawable.payment_pending).into(txnImage);
                break;
        }

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        txndate.setText(format.format(Calendar.getInstance().getTimeInMillis()));

        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        selToolColor = settingsColor.getString("tool", "");
        selStatusColor = settingsColor.getString("status", "");
        selTextColor1 = settingsColor.getString("text1", "");

        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }
        tool_title.setTextColor(Color.parseColor(selTextColor1));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, FeePayment.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
