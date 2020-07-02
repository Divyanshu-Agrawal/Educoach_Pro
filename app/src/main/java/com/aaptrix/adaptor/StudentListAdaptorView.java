package com.aaptrix.adaptor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.aaptrix.databeans.DataBeanStudent;
import androidx.annotation.NonNull;
import de.hdodenhof.circleimageview.CircleImageView;
import com.aaptrix.R;

import static com.aaptrix.tools.SPClass.PREFS_NAME;

/**
 * Created by googledeveloper on 24/2/17.
 */
public class StudentListAdaptorView extends ArrayAdapter<DataBeanStudent> {
	
	private ArrayList<DataBeanStudent> objects;
	Activity context;
	DataBeanStudent dbItemsDist;
	
	public StudentListAdaptorView(Activity context, int resource, ArrayList<DataBeanStudent> objects) {
		super(context, resource, objects);
		this.objects = objects;
		this.context = context;
	}
	
	@SuppressLint("InflateParams")
	@NonNull
	public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			assert inflater != null;
			v = inflater.inflate(R.layout.student_list_item1, null);
		}
		dbItemsDist = objects.get(position);
		if (dbItemsDist != null) {
			TextView tv_user_name = v.findViewById(R.id.tv_user_name);
			CircleImageView iv_user_image = v.findViewById(R.id.prof_logo1);
			// final RadioButton rd_check=(RadioButton)v.findViewById(R.id.rd_check);
			tv_user_name.setText(dbItemsDist.getUserName());
			String img = dbItemsDist.getUserImg();
			if (!TextUtils.isEmpty(img) && !img.equals("0")) {
				SharedPreferences sp = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
				String url = sp.getString("imageUrl", "") + sp.getString("userSchoolId", "") + "/users/students/profile/" + img;
				Picasso.with(context).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(iv_user_image);
			} else {
				iv_user_image.setImageResource(R.drawable.user_place_hoder);
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

