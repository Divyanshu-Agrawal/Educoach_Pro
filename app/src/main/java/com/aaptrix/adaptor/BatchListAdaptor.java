package com.aaptrix.adaptor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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

import java.util.ArrayList;

import com.aaptrix.databeans.DataBeanStudent;
import com.aaptrix.R;
import androidx.annotation.NonNull;

/**
 * Created by googledeveloper on 24/2/17.
 */
public class BatchListAdaptor extends ArrayAdapter<DataBeanStudent> {
	private ArrayList<DataBeanStudent> objects;
	Activity context;
	final String PREF_COLOR = "COLOR";
	DataBeanStudent dbItemsDist;
	
	private boolean IsVisibleMain;
	private ArrayList<DataBeanStudent> studentArray = new ArrayList<>();
	private ArrayList<String> stringArray = new ArrayList<>();
	private DataBeanStudent dbs;
	
	
	public BatchListAdaptor(Activity context, int resource, ArrayList<DataBeanStudent> objects, boolean IsVisible) {
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
			v = inflater.inflate(R.layout.batch_list_item, null);
		}
		
		
		dbItemsDist = objects.get(position);
		
		if (dbItemsDist != null) {
			
			
			TextView tv_user_name = v.findViewById(R.id.tv_user_name);
			CheckBox rd_student = v.findViewById(R.id.rd_student);
			tv_user_name.setText(dbItemsDist.getUserName());
			
			rd_student.setChecked(IsVisibleMain);
			
			final View finalV = v;
			rd_student.setOnCheckedChangeListener((compoundButton, b) -> {
				String value = objects.get(position).getUserName();
				//if (b==true)
				//   Toast.makeText(context, ""+value, Toast.LENGTH_SHORT).show();
				dbs = new DataBeanStudent();
				if (b) {
					if (stringArray.contains(value)) {
						Toast.makeText(context, "Already added", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show();
						stringArray.add(value);
						dbs.setUserName(value);
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

