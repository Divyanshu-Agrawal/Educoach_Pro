package com.aaptrix.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

import com.aaptrix.adaptor.ContactListAdaptor;
import com.aaptrix.databeans.DataBeanContact;
import com.aaptrix.R;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static com.aaptrix.tools.HttpUrl.ADMIN_CONTACTS;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class StaffContacts extends Fragment {
	
	private ListView contacts_list;
	private ArrayList<DataBeanContact> contactArray = new ArrayList<>();
	private DataBeanContact dbc;
	private String userId, userSchoolId;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private SharedPreferences sp_contact;
	private static final String PREFS_CONTACT = "json_contact";
	private TextView no_contact;
	private String type = "AllStaff";
	private String[] category = {"All Employees", "Teacher", "Staff", "Admin", "Other"};
	
	public StaffContacts() {
	
	}
	
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_contact_layout, container, false);
		contacts_list = view.findViewById(R.id.contacts_list);
		no_contact = view.findViewById(R.id.no_contact);
		Spinner typeSpinner = view.findViewById(R.id.contact_spinner);

		Drawable bg = getResources().getDrawable(R.drawable.et_background_design);
		SharedPreferences settingsColor = getContext().getSharedPreferences(PREF_COLOR, 0);
		String selToolColor = settingsColor.getString("tool", "");
		bg.setColorFilter(Color.parseColor(selToolColor), PorterDuff.Mode.SRC_ATOP);
		typeSpinner.setBackground(bg);
		
		mSwipeRefreshLayout = view.findViewById(R.id.activity_main_swipe_refresh_layout);
		mSwipeRefreshLayout.setRefreshing(false);
		mSwipeRefreshLayout.setEnabled(false);
		SharedPreferences settings = view.getContext().getSharedPreferences(PREFS_NAME, 0);
		userId = settings.getString("userID", "");
		userSchoolId = settings.getString("userSchoolId", "");

		sp_contact = Objects.requireNonNull(getActivity()).getSharedPreferences(PREFS_CONTACT, 0);
		String contacts = sp_contact.getString("json_contact", "");
		mSwipeRefreshLayout.setRefreshing(false);
		getAllContacts(contacts);

		if (isInternetOn()) {
			GetAllContacts b1 = new GetAllContacts(view.getContext());
			b1.execute(userId, userSchoolId, type);
		} else {
			mSwipeRefreshLayout.setRefreshing(false);
			getAllContacts(contacts);
		}
		mSwipeRefreshLayout.setColorScheme(R.color.text_gray);
		
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), R.layout.spinner_list_item1, category);
		dataAdapter.setDropDownViewResource(R.layout.spinner_list_item1);
		typeSpinner.setAdapter(dataAdapter);
		typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.text_gray));
				switch (category[position]) {
					case "Teacher":
						type = "2";
						break;
					case "Staff":
						type = "4";
						break;
					case "Other" :
						type = "5";
						break;
					case "Admin":
						type = "1";
						break;
					default:
						type = "AllStaff";
						break;
				}
				GetAllContacts b1 = new GetAllContacts(view.getContext());
				b1.execute(userId, userSchoolId, type);
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			
			}
		});
		
		return view;
	}
	
	//offline
	private void getAllContacts(String contact) {
		try {
			JSONObject jsonRootObject = new JSONObject(contact);
			JSONArray jsonArray = jsonRootObject.getJSONArray("result");
			contactArray.clear();
			for (int i = 0; i < jsonArray.length(); i++) {
				dbc = new DataBeanContact();
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				dbc.setContactUserId(jsonObject.getString("tbl_users_type"));
				dbc.setContactName(jsonObject.getString("tbl_users_name"));
				dbc.setContactNumber(jsonObject.getString("tbl_users_phone"));
				dbc.setContactImg(jsonObject.getString("tbl_users_img"));
				contactArray.add(dbc);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (contactArray.size() != 0) {
			listItms();
		}
	}
	
	
	public final boolean isInternetOn() {
		
		ConnectivityManager connec = (ConnectivityManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CONNECTIVITY_SERVICE);
		
		assert connec != null;
		if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
				connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
				connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
				connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
			
			return true;
		} else if (
				connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
						connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {
			return false;
		}
		return false;
	}
	
	
	private void listItms() {
		ContactListAdaptor contactListAdaptor = new ContactListAdaptor(getActivity(), R.layout.contact_list_item, contactArray, "admin");
		contacts_list.setAdapter(contactListAdaptor);
	}
	
	@SuppressLint("StaticFieldLeak")
	public class GetAllContacts extends AsyncTask<String, String, String> {
		Context ctx;
		
		GetAllContacts(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			mSwipeRefreshLayout.setRefreshing(true);
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			String userId = params[0];
			String userSchoolId = params[1];
			String userType = params[2];
			String data;
			
			try {
				URL url = new URL(ADMIN_CONTACTS);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				
				OutputStream outputStream = httpURLConnection.getOutputStream();
				
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
				data = URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8") + "&" +
						URLEncoder.encode("schoolId", "UTF-8") + "=" + URLEncoder.encode(userSchoolId, "UTF-8") + "&" +
						URLEncoder.encode("userType", "UTF-8") + "=" + URLEncoder.encode(userType, "UTF-8");
				outputStream.write(data.getBytes());
				
				bufferedWriter.write(data);
				bufferedWriter.flush();
				bufferedWriter.close();
				outputStream.flush();
				outputStream.close();
				
				InputStream inputStream = httpURLConnection.getInputStream();
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
				StringBuilder response = new StringBuilder();
				String line;
				
				while ((line = bufferedReader.readLine()) != null) {
					response.append(line);
				}
				bufferedReader.close();
				inputStream.close();
				httpURLConnection.disconnect();
				return response.toString();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			Log.e("All_Contact", "" + result);
			mSwipeRefreshLayout.setRefreshing(false);
			sp_contact = Objects.requireNonNull(getActivity()).getSharedPreferences(PREFS_CONTACT, 0);
			SharedPreferences.Editor se_contact = sp_contact.edit();
			se_contact.clear();
			se_contact.putString("json_contact", result);
			se_contact.apply();
			
			if (result.equals("{\"result\":null}")) {
				no_contact.setVisibility(View.VISIBLE);
				contactArray.clear();
			} else {
				no_contact.setVisibility(View.GONE);
				try {
					JSONObject jsonRootObject = new JSONObject(result);
					JSONArray jsonArray = jsonRootObject.getJSONArray("result");
					contactArray.clear();
					for (int i = 0; i < jsonArray.length(); i++) {
						dbc = new DataBeanContact();
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						dbc.setContactUserId(jsonObject.getString("tbl_users_type"));
						dbc.setContactName(jsonObject.getString("tbl_users_name"));
						dbc.setContactNumber(jsonObject.getString("tbl_users_phone"));
						dbc.setContactImg(jsonObject.getString("tbl_users_img"));
						contactArray.add(dbc);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (contactArray.size() != 0) {
					listItms();
				}
				super.onPostExecute(result);
			}
		}
	}
}
