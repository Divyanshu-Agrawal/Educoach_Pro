package com.aaptrix.activitys;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.util.Log;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import com.aaptrix.R;

/**
 * Created by Administrator on 4/15/2018.
 */

public class FullScreenImageActivity extends Activity {
	PhotoView iv_image;
	ImageView tv_close;
	String leaveImg;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.full_screen_image_layout);
		iv_image = findViewById(R.id.iv_image);
		tv_close = findViewById(R.id.tv_close);
		leaveImg = getIntent().getStringExtra("leaveImg");
		Log.e("img", leaveImg);
		Picasso.with(this).load(leaveImg).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(iv_image);
		tv_close.setOnClickListener(view -> finish());
	}
}
