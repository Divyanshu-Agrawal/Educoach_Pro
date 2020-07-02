package com.aaptrix.adaptor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import com.aaptrix.databeans.DataBeanMessage;
import androidx.annotation.NonNull;
import com.aaptrix.R;

import static com.aaptrix.tools.SPClass.PREFS_NAME;

/**
 * Created by googledeveloper on 24/2/17.
 */
public class MessageListAdaptor extends ArrayAdapter<DataBeanMessage> {
	
	private ArrayList<DataBeanMessage> objects;
	Activity context;
	DataBeanMessage dbItemsDist;
	
	public MessageListAdaptor(Activity context, int resource, ArrayList<DataBeanMessage> objects) {
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
			v = inflater.inflate(R.layout.messages_layout_of_user, null);
		}
		
		
		dbItemsDist = objects.get(position);
		
		if (dbItemsDist != null) {
			TextView messageText = v.findViewById(R.id.message_text);
			TextView messaged_user_name = v.findViewById(R.id.messaged_user_name);
			TextView time = v.findViewById(R.id.time);
			LinearLayout mainLayoutLogin = v.findViewById(R.id.mainLayoutLogin);
			LinearLayout chatLayout = v.findViewById(R.id.chatLayout);
			SharedPreferences settings = v.getContext().getSharedPreferences(PREFS_NAME, 0);
			String NAME = settings.getString("userName", "");
			if (NAME.equals(dbItemsDist.getUserNameM())) {
				messaged_user_name.setVisibility(View.GONE);
				messageText.setText(dbItemsDist.getMessage());
				time.setText(dbItemsDist.getTime());
				messaged_user_name.setGravity(Gravity.START);
				messageText.setGravity(Gravity.START);
				time.setGravity(Gravity.END);
				mainLayoutLogin.setGravity(Gravity.END);
				chatLayout.setBackgroundColor(context.getResources().getColor(R.color.mybg));
			} else if (dbItemsDist.getUserNameM() == null || dbItemsDist.getMessage() == null){
				v.setVisibility(View.GONE);
			} else {
				messageText.setText(dbItemsDist.getMessage());
				messaged_user_name.setText(dbItemsDist.getUserNameM());
				time.setText(dbItemsDist.getTime());
				messaged_user_name.setGravity(Gravity.START);
				messageText.setGravity(Gravity.START);
				time.setGravity(Gravity.END);
				mainLayoutLogin.setGravity(Gravity.START);
				chatLayout.setBackgroundColor(context.getResources().getColor(R.color.otherbg));
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