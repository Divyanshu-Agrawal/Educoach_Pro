package com.aaptrix.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aaptrix.R;
import com.aaptrix.databeans.RateData;
import java.util.ArrayList;

public class RateAdapter extends ArrayAdapter<RateData> {

    private int resource;
    private Context context;
    private ArrayList<RateData> objects;

    public RateAdapter(@NonNull Context context, int resource, @NonNull ArrayList<RateData> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
        this.resource = resource;
    }

    @SuppressLint({"ViewHolder", "InflateParams", "SetTextI18n"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        view = inflater.inflate(resource, null);

        TextView name = view.findViewById(R.id.name);
        TextView review = view.findViewById(R.id.review);
        RatingBar ratingBar =  view.findViewById(R.id.rating);

        name.setText("By : " + objects.get(position).getName());
        review.setText(objects.get(position).getReview());
        LayerDrawable drawable = (LayerDrawable) ratingBar.getProgressDrawable();
        drawable.getDrawable(2).setColorFilter(Color.parseColor("#FFD700"), PorterDuff.Mode.SRC_ATOP);
        drawable.getDrawable(1).setColorFilter(Color.parseColor("#00FFD700"), PorterDuff.Mode.SRC_ATOP);
        ratingBar.setIsIndicator(true);
        ratingBar.setRating(Float.parseFloat(objects.get(position).getRating()));

        return view;
    }

    @Override
    public int getViewTypeCount() {
        return Math.max(getCount(), 1);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
