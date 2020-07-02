package com.aaptrix.adaptor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.aaptrix.R;
import androidx.annotation.NonNull;

import static com.aaptrix.tools.SPClass.PREFS_NAME;

public class GalleryDetailAdapter extends ArrayAdapter<String> {
	
	private ArrayList<String> objects;
	private Activity context;
	private int resource;
	
	public GalleryDetailAdapter(Activity context, int resource, ArrayList<String> objects) {
		super(context, resource, objects);
		this.objects = objects;
		this.context = context;
		this.resource = resource;
	}
	
	private class ViewHolder {
		ImageView imageView;
	}
	
	@SuppressLint("ViewHolder")
	@NonNull
	@Override
	public View getView(int position, View view, @NonNull ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		assert inflater != null;
		view = inflater.inflate(resource, null);
		ViewHolder holder = new ViewHolder();
		view.setTag(holder);
		
		if (objects != null) {
			try {
				SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
				holder.imageView = view.findViewById(R.id.image_gallery_grid);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					holder.imageView.setClipToOutline(true);
				}
				String url = sp.getString("imageUrl", "") + sp.getString("userSchoolId", "")
						+ "/gallery/" + objects.get(position);
				Picasso.with(context).load(url).into(holder.imageView);
			} catch (Exception e) {
				return view;
			}
		}
		return view;
	}

	@Override
	public int getViewTypeCount() {
		if (getCount() < 1) {
			return 1;
		} else {
			return getCount();
		}
	}
	@Override
	public int getItemViewType(int position) {
		return position;
	}
}
