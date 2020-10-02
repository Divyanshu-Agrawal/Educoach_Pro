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
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.aaptrix.databeans.DataBeanStudent;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import de.hdodenhof.circleimageview.CircleImageView;
import com.aaptrix.R;

import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

/**
 * Created by googledeveloper on 24/2/17.
 */
public class UserListAdaptor extends ArrayAdapter<DataBeanStudent> {
	
	private ArrayList<DataBeanStudent> objects;
	Activity context;
	DataBeanStudent dbItemsDist;
	private String id;
	
	public UserListAdaptor(Activity context, int resource, ArrayList<DataBeanStudent> objects, String id) {
		super(context, resource, objects);
		this.objects = objects;
		this.context = context;
		this.id = id;
	}
	
	
	@NonNull
	@SuppressLint({"SetTextI18n", "InflateParams"})
	public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			assert inflater != null;
			v = inflater.inflate(R.layout.user_list_item1, null);
		}
		
		dbItemsDist = objects.get(position);
		
		if (dbItemsDist != null) {
			TextView tv_user_name = v.findViewById(R.id.tv_user_name);
			TextView tv_role_name = v.findViewById(R.id.tv_role_name);
			TextView tv_school_name = v.findViewById(R.id.tv_school_name);
			CardView cardView = v.findViewById(R.id.card_view);
			cardView.setCardBackgroundColor(Color.TRANSPARENT);
			CircleImageView iv_user_image = v.findViewById(R.id.prof_logo1);
			tv_user_name.setText(dbItemsDist.getUserName());
			tv_school_name.setText(dbItemsDist.getUserSection());
			SharedPreferences settings = v.getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
			SharedPreferences settingsColor = context.getSharedPreferences(PREF_COLOR, 0);

			if (!id.equals("0")) {
				if (id.equals(dbItemsDist.getUserID())) {
					cardView.setCardBackgroundColor(Color.parseColor(settingsColor.getString("tool", "")));
				}
			}

			if (dbItemsDist.getUserSchoolRoleName().equals("Parent")) {
				tv_role_name.setText("Student");
			} else {
				tv_role_name.setText(dbItemsDist.getUserSchoolRoleName());
			}
			
			String img = dbItemsDist.getUserImg();
			if (!img.equals("0")) {
				String url;
				switch (dbItemsDist.getUserSchoolRoleName()) {
					case "Parent":
						url = settings.getString("imageUrl", "") + dbItemsDist.getUserSchoolId() + "/users/students/profile/" + img;
						Picasso.with(context).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(iv_user_image);
						break;
					case "Admin":
						url = settings.getString("imageUrl", "") + dbItemsDist.getUserSchoolId() + "/users/admin/profile/" + img;
						Picasso.with(context).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(iv_user_image);
						break;
					case "Staff":
						url = settings.getString("imageUrl", "") + dbItemsDist.getUserSchoolId() + "/users/staff/profile/" + img;
						Picasso.with(context).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(iv_user_image);
						break;
					case "Teacher":
						url = settings.getString("imageUrl", "") + dbItemsDist.getUserSchoolId() + "/users/teachers/profile/" + img;
						Picasso.with(context).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(iv_user_image);
						break;
					case "Student":
						url = settings.getString("imageUrl", "") + dbItemsDist.getUserSchoolId() + "/users/students/profile/" + img;
						Picasso.with(context).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(iv_user_image);
						break;
				}
			} else {
				String url = settings.getString("imageUrl", "") + dbItemsDist.getUserSchoolId() + "/other/" + dbItemsDist.getUserSchoolSchoolLogo1();
				Picasso.with(context).load(url).into(iv_user_image);
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

