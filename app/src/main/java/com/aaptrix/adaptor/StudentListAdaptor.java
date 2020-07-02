package com.aaptrix.adaptor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
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
public class StudentListAdaptor extends ArrayAdapter<DataBeanStudent> {
	
	private ArrayList<DataBeanStudent> objects;
	Activity context;
	final String PREF_COLOR = "COLOR";
	DataBeanStudent dbItemsDist;
	
	private boolean IsVisibleMain;
	private ArrayList<DataBeanStudent> studentArray = new ArrayList<>();
	private ArrayList<String> stringArray = new ArrayList<>();
	private DataBeanStudent dbs;
	
	
	public StudentListAdaptor(Activity context, int resource, ArrayList<DataBeanStudent> objects, boolean IsVisible) {
		super(context, resource, objects);
		this.objects = objects;
		this.context = context;
		this.IsVisibleMain = IsVisible;
		
	}
	
	
	@SuppressLint("InflateParams")
	@NonNull
	public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
		
		View v = convertView;
		
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			assert inflater != null;
			v = inflater.inflate(R.layout.student_list_item, null);
		}
		
		
		dbItemsDist = objects.get(position);
		
		if (dbItemsDist != null) {
			
			
			TextView tv_user_name = v.findViewById(R.id.tv_user_name);
			CheckBox rd_student = v.findViewById(R.id.rd_student);
			CircleImageView iv_user_image = v.findViewById(R.id.prof_logo1);
			// final RadioButton rd_check=(RadioButton)v.findViewById(R.id.rd_check);
			tv_user_name.setText(dbItemsDist.getUserName());
			String img = dbItemsDist.getUserImg();
			if (!TextUtils.isEmpty(img) && !img.equals("0")) {
				SharedPreferences sp = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
				String url = sp.getString("imageUrl", "") + sp.getString("userSchoolId", "") + "/users/students/profile/" + img;
				Log.e("url", url);
				Picasso.with(context).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(iv_user_image);
			} else {
				// Picasso.with(v.getContext()).load(R.drawable.ic_about_us_img).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(iv_user_image);
				iv_user_image.setImageResource(R.drawable.user_place_hoder);
				
			}
			rd_student.setChecked(IsVisibleMain);
			
			iv_user_image.setOnClickListener(view -> Toast.makeText(context, "" + stringArray, Toast.LENGTH_SHORT).show());
			
			
			final View finalV = v;
			rd_student.setOnCheckedChangeListener((compoundButton, b) -> {
				String value = objects.get(position).getUserID();
				//if (b==true)
				dbs = new DataBeanStudent();
				if (b) {
					if (stringArray.contains(value)) {
						Toast.makeText(context, "Already added", Toast.LENGTH_SHORT).show();
					} else {
						stringArray.add(value);
						dbs.setUserID(value);
						studentArray.add(dbs);
						saveDataInSP(stringArray, finalV, studentArray);
					}
				} else {
					if (stringArray.contains(value)) {
						stringArray.remove(value);
						Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(context, "Already Removed", Toast.LENGTH_SHORT).show();
					}
					
				}
				
			});
			
			
		}
		return v;
		
	}
	
	private void saveDataInSP(ArrayList<String> stringArray, View view, ArrayList<DataBeanStudent> studentArray) {
		Gson gson = new GsonBuilder().create();
		JsonArray myCustomArray = gson.toJsonTree(stringArray).getAsJsonArray();
		JsonArray studentArray1 = gson.toJsonTree(studentArray).getAsJsonArray();
		
		String PREFS_DAIRY = "dairy";
		SharedPreferences sp_dairy = view.getContext().getSharedPreferences(PREFS_DAIRY, 0);
		SharedPreferences.Editor se_dairy = sp_dairy.edit();
		se_dairy.clear();
		se_dairy.putString("dairy", "" + myCustomArray);
		se_dairy.putString("studentArray", "" + studentArray1);
		se_dairy.apply();
		
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

