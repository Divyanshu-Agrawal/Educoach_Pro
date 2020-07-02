package com.aaptrix.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import com.aaptrix.databeans.DataBeanAboutUs;
import com.aaptrix.R;
import androidx.annotation.NonNull;

/**
 * Created by googledeveloper on 24/2/17.
 */
public class AboutUsMoreInfoListAdaptor extends ArrayAdapter<DataBeanAboutUs> {
	
	private ArrayList<DataBeanAboutUs> objects;
	
	public AboutUsMoreInfoListAdaptor(Context context, int resource, ArrayList<DataBeanAboutUs> objects) {
		super(context, resource, objects);
		this.objects = objects;
	}
	
	
	@NonNull
	@SuppressLint("InflateParams")
	public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
		
		View v = convertView;
		
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			assert inflater != null;
			v = inflater.inflate(R.layout.about_us_more_info_list_item, null);
		}
		
		
		final DataBeanAboutUs dbItemsDist = objects.get(position);
		
		if (dbItemsDist != null) {
			TextView moreinfo = v.findViewById(R.id.moreinfo);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				moreinfo.setText(Html.fromHtml(dbItemsDist.getAboutMoreInfoName(), Html.FROM_HTML_MODE_COMPACT));
			} else {
				moreinfo.setText(Html.fromHtml(dbItemsDist.getAboutMoreInfoName()));
			}
		}
		return v;
		
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

