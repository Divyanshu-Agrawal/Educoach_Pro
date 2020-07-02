package com.aaptrix.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.aaptrix.R;
import androidx.annotation.NonNull;

public class GridImageAdapter extends ArrayAdapter<Uri> {
	
	private Context context;
	private int resource;
	private ArrayList<Uri> arrayList;
	
	public GridImageAdapter(Context context, int resource, ArrayList<Uri> arrayList) {
		super(context, resource, arrayList);
		this.context = context;
		this.resource = resource;
		this.arrayList = arrayList;
		
	}
	
	@SuppressLint("ViewHolder")
	@NonNull
	@Override
	public View getView(int position, View view, @NonNull ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		assert inflater != null;
		view = inflater.inflate(resource, null);
		ImageView imageView = view.findViewById(R.id.add_imageview);
		Uri image = arrayList.get(position);
		Picasso.with(context)
				.load(image)
				.fit()
				.into(imageView);
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
