package com.aaptrix.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.aaptrix.databeans.DataBeanStudent;
import com.aaptrix.R;
import androidx.annotation.NonNull;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.aaptrix.tools.SPClass.PREFS_NAME;

public class StudentPerformanceAdapter extends ArrayAdapter<DataBeanStudent> {
	
	Context context;
	int resource;
	private ArrayList<DataBeanStudent> object;
	
	public StudentPerformanceAdapter(Context context, int resource, ArrayList<DataBeanStudent> object) {
		super(context, resource, object);
		this.context = context;
		this.resource = resource;
		this.object = object;
	}
	
	@SuppressLint("InflateParams")
	@NonNull
	public View getView(final int position, View view, @NonNull ViewGroup parent) {
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			assert inflater != null;
			view = inflater.inflate(resource, null);
			DataBeanStudent db = object.get(position);
			if (db != null) {
				CircleImageView userProfile = view.findViewById(R.id.userLogo);
				TextView studentName = view.findViewById(R.id.student_name);
				SharedPreferences settings = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
				
				if (db.getUserImg().equals("0")) {
					userProfile.setImageDrawable(getContext().getResources().getDrawable(R.drawable.user_place_hoder));
				} else if (!TextUtils.isEmpty(db.getUserImg())) {
					String url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "") + "/users/students/profile/" + db.getUserImg();
					Log.e("url", url);
					Picasso.with(context).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(userProfile);
				} else {
					userProfile.setImageDrawable(getContext().getResources().getDrawable(R.drawable.user_place_hoder));
				}
				
				studentName.setText(db.getUserName());
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
