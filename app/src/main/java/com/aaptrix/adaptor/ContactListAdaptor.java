package com.aaptrix.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.aaptrix.databeans.DataBeanContact;
import com.aaptrix.R;
import de.hdodenhof.circleimageview.CircleImageView;

import androidx.annotation.NonNull;

import static com.aaptrix.tools.SPClass.PREFS_NAME;

/**
 * Created by googledeveloper on 24/2/17.
 */
public class ContactListAdaptor extends ArrayAdapter<DataBeanContact> {
	
	private ArrayList<DataBeanContact> objects;
	DataBeanContact dbItemsDist;
	Context context;
	private String contactType;
	
	public ContactListAdaptor(Context context, int resource, ArrayList<DataBeanContact> objects, String contactType) {
		super(context, resource, objects);
		this.objects = objects;
		this.context = context;
		this.contactType = contactType;
	}
	
	
	@SuppressLint("InflateParams")
	@NonNull
	public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
		
		View v = convertView;
		
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			assert inflater != null;
			v = inflater.inflate(R.layout.contact_list_item, null);
		}
		
		
		dbItemsDist = objects.get(position);
		
		if (dbItemsDist != null) {
			
			TextView clgname = v.findViewById(R.id.clgname);
			TextView tv_number = v.findViewById(R.id.tv_number);
			CircleImageView iv_contact_img = v.findViewById(R.id.iv_contact_img);
			
			ImageView iv_call = v.findViewById(R.id.iv_call);
			ImageView iv_msg = v.findViewById(R.id.iv_msg);
			clgname.setText(dbItemsDist.getContactName());
			tv_number.setText(dbItemsDist.getContactNumber());
			
			SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
			
			String img = dbItemsDist.getContactImg();
			if (contactType.equals("student")) {
				if (img.equals("0")) {
					iv_contact_img.setImageDrawable(context.getResources().getDrawable(R.drawable.user_place_hoder));
				} else {
					String url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "") + "/contact/" + img;
					Picasso.with(context).load(url).error(R.drawable.user_place_hoder).placeholder(R.drawable.user_place_hoder).into(iv_contact_img);
				}
			} else if (contactType.equals("admin")) {
				String url;
				if (!dbItemsDist.getContactImg().equals("0")) {
					switch (dbItemsDist.getContactUserId()) {
						case "Parent":
							url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "") + "/users/students/profile/" + dbItemsDist.getContactImg();
							Picasso.with(context).load(url).error(R.drawable.user_place_hoder).placeholder(R.drawable.user_place_hoder).into(iv_contact_img);
							break;
						case "Admin":
							url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "") + "/users/admin/profile/" + dbItemsDist.getContactImg();
							Picasso.with(context).load(url).error(R.drawable.user_place_hoder).placeholder(R.drawable.user_place_hoder).into(iv_contact_img);
							break;
						case "Staff":
							url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "") + "/users/staff/profile/" + dbItemsDist.getContactImg();
							Picasso.with(context).load(url).error(R.drawable.user_place_hoder).placeholder(R.drawable.user_place_hoder).into(iv_contact_img);
							break;
						case "Teacher":
							url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "") + "/users/teachers/profile/" + dbItemsDist.getContactImg();
							Picasso.with(context).load(url).error(R.drawable.user_place_hoder).placeholder(R.drawable.user_place_hoder).into(iv_contact_img);
							break;
						case "Student":
							url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "") + "/users/students/profile/" + dbItemsDist.getContactImg();
							Picasso.with(context).load(url).error(R.drawable.user_place_hoder).placeholder(R.drawable.user_place_hoder).into(iv_contact_img);
							break;
					}
				} else {
					Picasso.with(context).load(R.drawable.user_place_hoder).into(iv_contact_img);
				}
			}
			
			iv_call.setOnClickListener(view -> {
				dbItemsDist = objects.get(position);
				Intent intent = new Intent(Intent.ACTION_DIAL);
				intent.setData(Uri.parse("tel:" + "+91" + dbItemsDist.getContactNumber()));
				context.startActivity(intent);
			});
			
			iv_msg.setOnClickListener(view -> {
				dbItemsDist = objects.get(position);
				Intent intentsms = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + dbItemsDist.getContactNumber()));
				intentsms.putExtra("sms_body", "");
				context.startActivity(intentsms);
			});
			
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

