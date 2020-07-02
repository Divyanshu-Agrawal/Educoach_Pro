package com.aaptrix.adaptor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.aaptrix.databeans.GalleryData;
import com.aaptrix.R;
import androidx.annotation.NonNull;

import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class GalleryAdapter extends ArrayAdapter<GalleryData> {
	
	private ArrayList<GalleryData> objects;
	private Activity context;
	private int resource;
	
	public GalleryAdapter(Activity context, int resource, ArrayList<GalleryData> objects) {
		super(context, resource, objects);
		this.objects = objects;
		this.context = context;
		this.resource = resource;
	}
	
	private class ViewHolder {
		ImageView imageView;
		TextView textView;
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
			SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
			GalleryData data = objects.get(position);
			holder.imageView = view.findViewById(R.id.gallery_preview);
			holder.textView = view.findViewById(R.id.gallery_title);
			String url = sp.getString("imageUrl", "") + sp.getString("userSchoolId", "")
					+ "/gallery/" + data.getImages()[0].replace("\"", "").replace("[", "")
					.replace("]", "").replace("\\", "");
			Picasso.with(context).load(url).into(holder.imageView);
			holder.textView.setText(data.getTitle());

			TextView viewMore = view.findViewById(R.id.view_more);
			SharedPreferences color = context.getSharedPreferences(PREF_COLOR, Context.MODE_PRIVATE);
			viewMore.setBackgroundColor(Color.parseColor(color.getString("drawer", "")));
			viewMore.setTextColor(Color.parseColor(color.getString("text1", "")));
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
