package com.aaptrix.adaptor;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.daimajia.slider.library.R.id;
import com.daimajia.slider.library.R.layout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;

public class CustomSliderView extends BaseSliderView {

    public CustomSliderView(Context context) {
        super(context);
    }

    public View getView() {

        View v = LayoutInflater.from(this.getContext()).inflate(layout.render_type_text, null);
        ImageView target = v.findViewById(id.daimajia_slider_image);
        LinearLayout frame = v.findViewById(id.description_layout);
        frame.setBackgroundColor(Color.TRANSPARENT);
        frame.setVisibility(View.GONE);

        this.bindEventAndShow(v, target);

        return v;
    }
}