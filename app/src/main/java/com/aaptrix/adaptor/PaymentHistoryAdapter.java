package com.aaptrix.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aaptrix.R;
import com.aaptrix.databeans.FeeData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class PaymentHistoryAdapter extends ArrayAdapter<FeeData> {

    private ArrayList<FeeData> objects;
    private Context context;
    private int resource;

    public PaymentHistoryAdapter(@NonNull Context context, int resource, @NonNull ArrayList<FeeData> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @SuppressLint({"ViewHolder", "SetTextI18n"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        view = inflater.inflate(resource, null);

        if (objects != null) {
            FeeData data = objects.get(position);
            TextView status = view.findViewById(R.id.payment_status);
            TextView mode = view.findViewById(R.id.payment_mode);
            TextView date = view.findViewById(R.id.payment_date);
            TextView amount = view.findViewById(R.id.payment_amount);

            if (data.getStatus().equals("SUCCESS")) {
                status.setText("Payment Successful");
            } else if (data.getStatus().equals("SUBMITTED")) {
                status.setText("Payment Pending");
            } else {
                status.setText("Payment Failed");
            }

            mode.setText("Payment Via : " + data.getMode());
            amount.setText("₹" + data.getAmount() + "/-");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd", Locale.getDefault());
            try {
                Date d = format.parse(data.getDate());
                assert d != null;
                format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                date.setText("Payment done on : " + format.format(d));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        return view;
    }
}
