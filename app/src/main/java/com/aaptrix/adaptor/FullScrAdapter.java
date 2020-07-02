package com.aaptrix.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.aaptrix.R;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import static com.aaptrix.tools.SPClass.PREFS_NAME;

public class FullScrAdapter extends PagerAdapter {
	
	private Context context;
	private ArrayList<String> galleryArray;
	
	public FullScrAdapter(Context context, ArrayList<String> galleryArray) {
		this.context = context;
		this.galleryArray = galleryArray;
	}
	
	@Override
	public int getCount() {
		return galleryArray.size();
	}
	
	@Override
	public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
		return view == object;
	}
	
	@NonNull
	@Override
	public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		assert layoutInflater != null;
		@SuppressLint("InflateParams") final View view = layoutInflater.inflate(R.layout.fullscr_gallery, null);
		PhotoView image = view.findViewById(R.id.fullscr_imageview);
		String data = galleryArray.get(position);
		SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		String url = sp.getString("imageUrl", "") + sp.getString("userSchoolId", "") + "/gallery/" + data;
		Picasso.with(context)
				.load(url)
				.into(image);
		container.addView(view);
		return view;
	}
	
	@Override
	public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
		container.removeView((View) object);
	}


}
