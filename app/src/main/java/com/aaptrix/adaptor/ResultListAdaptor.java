package com.aaptrix.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import com.aaptrix.databeans.DataBeanExamTt;
import com.aaptrix.R;
import androidx.annotation.NonNull;

import static com.aaptrix.tools.SPClass.PREF_COLOR;

/**
 * Created by googledeveloper on 24/2/17.
 */
public class ResultListAdaptor extends ArrayAdapter<DataBeanExamTt> {
	
	private ArrayList<DataBeanExamTt> objects;
	String position;
	Context context;
	
	public ResultListAdaptor(Context context, int resource, ArrayList<DataBeanExamTt> objects) {
		super(context, resource, objects);
		this.objects = objects;
		this.context = context;
	}
	
	@NonNull
	@SuppressLint("InflateParams")
	public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			assert inflater != null;
			v = inflater.inflate(R.layout.result_list_item, null);
		}
		final DataBeanExamTt dbItemsDist = objects.get(position);
		if (dbItemsDist != null) {
			TextView subjectName = v.findViewById(R.id.subjectName);
			TextView examDateOnly = v.findViewById(R.id.examDateOnly);
			SharedPreferences settingsColor = v.getContext().getSharedPreferences(PREF_COLOR, 0);
			String selToolColor = settingsColor.getString("tool", "");
			subjectName.setText(dbItemsDist.getSubjectName());
			examDateOnly.setText(dbItemsDist.getExamDate());
			examDateOnly.setTextColor(Color.parseColor(selToolColor));
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

