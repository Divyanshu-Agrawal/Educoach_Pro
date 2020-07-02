package com.aaptrix.activitys;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import com.aaptrix.activitys.admin.InstituteBuzzActivityDiff;
import com.aaptrix.R;
import com.aaptrix.activitys.student.InstituteBuzzActivity;

import static com.aaptrix.tools.HttpUrl.ALL_CITY;
import static com.aaptrix.tools.HttpUrl.ALL_ROLE;
import static com.aaptrix.tools.HttpUrl.ALL_SCHOOL;
import static com.aaptrix.tools.HttpUrl.ALL_STATES;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class SelectRoleActivity extends Activity {
	Button btn_login;
	String str_state, str_city, str_school, str_role, str_logo, str_logo1;
	String state_array[] = {"Select State"}, state_id[] = {"0"}, city_array[] = {"Select City"}, city_id[] = {"0"},
			school_array[] = {"Select School"}, school_id[] = {"0"}, school_img[] = {"0"}, school_img1[] = {"0"}, role_array[] = {"Select Role"}, role_id[] = {"0"};
	SharedPreferences settings;
	SharedPreferences.Editor editor;
	private SharedPreferences sp_state, sp_city, sp_school, sp_role;
	SharedPreferences.Editor se_state, se_city, se_school, se_role;
	public static final String PREF_STATES = "state";
	public static final String PREF_CITYS = "city";
	public static final String PREF_SCHOOLS = "school";
	public static final String PREF_ROLES = "role";
	String Networkstatus;
	Spinner tv_state, tv_city, tv_school, tv_role;
	String sel_state, selCity, selSchool, selRole, selLogo, selLogo1;
	String selToolColor, selDrawerColor, selStatusColor, selTextColor1, selTextColor2;
	//	String a[]={"Select City"};
	ProgressBar loader, loader1, loader2, loader3;
	String tool_color[] = {"0"}, drawer_color[] = {"0"}, status_color[] = {"0"}, text_color1[] = {"0"}, text_color2[] = {"0"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_role);
		tv_state = findViewById(R.id.tv_state);
		tv_city = findViewById(R.id.select_city);
		tv_school = findViewById(R.id.tv_school);
		tv_role = findViewById(R.id.tv_role);
		btn_login = findViewById(R.id.btn_login);
		
		loader = findViewById(R.id.loader);
		loader1 = findViewById(R.id.loader1);
		loader2 = findViewById(R.id.loader2);
		loader3 = findViewById(R.id.loader3);
		//prefrancess
		settings = getSharedPreferences(PREFS_NAME, 0);
		String status = settings.getString("logged", "");
		String userType = settings.getString("userrType", "");
		//status
		try {
			Networkstatus = getIntent().getStringExtra("status");
		} catch (NullPointerException e) {
			Networkstatus = "Online";
		}
		touchEvents();
		if (Networkstatus.equals("Online")) {
			if (status.equals("logged")) {
				switch (userType) {
					case "Student": {
						Intent i = new Intent(SelectRoleActivity.this, InstituteBuzzActivity.class);
						startActivity(i);
						finish();
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
						break;
					}
					case "Teacher": {
						Intent i = new Intent(SelectRoleActivity.this, InstituteBuzzActivityDiff.class);
						startActivity(i);
						finish();
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
						break;
					}
					case "Admin": {
						Intent i = new Intent(SelectRoleActivity.this, InstituteBuzzActivityDiff.class);
						startActivity(i);
						finish();
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
						break;
					}
					case "Staff": {
						Intent i = new Intent(SelectRoleActivity.this, InstituteBuzzActivityDiff.class);
						startActivity(i);
						finish();
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
						break;
					}
					case "Others": {
						Intent i = new Intent(SelectRoleActivity.this, InstituteBuzzActivityDiff.class);
						startActivity(i);
						finish();
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
						break;
					}
				}
			} else {
				GetAllStates b1 = new GetAllStates(this);
				b1.execute();
				AllAdaptors();
				Allmethods();
			}
		} else if (Networkstatus.equals("Offline")) {
			if (status.equals("logged")) {
				switch (userType) {
					case "Student": {
						Intent i = new Intent(SelectRoleActivity.this, InstituteBuzzActivity.class);
						startActivity(i);
						finish();
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
						break;
					}
					case "Teacher": {
						Intent i = new Intent(SelectRoleActivity.this, InstituteBuzzActivityDiff.class);
						startActivity(i);
						finish();
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
						break;
					}
					case "Admin": {
						Intent i = new Intent(SelectRoleActivity.this, InstituteBuzzActivityDiff.class);
						startActivity(i);
						finish();
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
						break;
					}
					case "Staff": {
						Intent i = new Intent(SelectRoleActivity.this, InstituteBuzzActivityDiff.class);
						startActivity(i);
						finish();
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
						break;
					}
					case "Others": {
						Intent i = new Intent(SelectRoleActivity.this, InstituteBuzzActivityDiff.class);
						startActivity(i);
						finish();
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
						break;
					}
				}
			} else {
				sp_state = getSharedPreferences(PREF_STATES, 0);
				String states = sp_state.getString("states_json", "");
				Log.d("states", states);
				getAllStates(states);
				
				sp_city = getSharedPreferences(PREF_CITYS, 0);
				String city = sp_city.getString("city_json", "");
				Log.d("city", city);
				getAllCitys(city);
				
				sp_school = getSharedPreferences(PREF_SCHOOLS, 0);
				String school = sp_school.getString("school_json", "");
				Log.d("school", school);
				getAllSchools(school);
				
				sp_role = getSharedPreferences(PREF_ROLES, 0);
				String role = sp_role.getString("role_json", "");
				Log.d("role", role);
				getAllRoles(role);
				AllAdaptors();
				Allmethods();
			}
		}
	}
	
	@SuppressLint("ClickableViewAccessibility")
	private void touchEvents() {
		tv_state.setOnTouchListener((view, motionEvent) -> {
			if (!isInternetOn()) {
				Toast.makeText(SelectRoleActivity.this, "No internet", Toast.LENGTH_SHORT).show();
			}
			return false;
		});
		tv_city.setOnTouchListener((view, motionEvent) -> {
			if (!isInternetOn()) {
				Toast.makeText(SelectRoleActivity.this, "No internet", Toast.LENGTH_SHORT).show();
			}
			return false;
		});
		tv_school.setOnTouchListener((view, motionEvent) -> {
			if (!isInternetOn()) {
				Toast.makeText(SelectRoleActivity.this, "No internet", Toast.LENGTH_SHORT).show();
			}
			return false;
		});
		tv_role.setOnTouchListener((view, motionEvent) -> {
			if (!isInternetOn()) {
				Toast.makeText(SelectRoleActivity.this, "No internet", Toast.LENGTH_SHORT).show();
			}
			return false;
		});
	}
	
	private void AllAdaptors() {
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(SelectRoleActivity.this, R.layout.spinner_list_item, state_array);
		// Drop down layout style - list view with radio button
		dataAdapter.setDropDownViewResource(R.layout.spinner_list_item);
		// attaching data adapter to spinner
		tv_state.setAdapter(dataAdapter);
		
		ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(SelectRoleActivity.this, R.layout.spinner_list_item, city_array);
		// Drop down layout style - list view with radio button
		dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item);
		// attaching data adapter to spinner
		tv_city.setAdapter(dataAdapter1);
		
		ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<>(SelectRoleActivity.this, R.layout.spinner_list_item, school_array);
		// Drop down layout style - list view with radio button
		dataAdapter2.setDropDownViewResource(R.layout.spinner_list_item);
		// attaching data adapter to spinner
		tv_school.setAdapter(dataAdapter2);
		
		ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<>(SelectRoleActivity.this, R.layout.spinner_list_item, role_array);
		// Drop down layout style - list view with radio button
		dataAdapter3.setDropDownViewResource(R.layout.spinner_list_item);
		// attaching data adapter to spinner
		tv_role.setAdapter(dataAdapter3);
	}
	
	private void AllAdaptors1(String[] city_array) {
		ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(SelectRoleActivity.this, R.layout.spinner_list_item, city_array);
		// Drop down layout style - list view with radio button
		dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item);
		// attaching data adapter to spinner
		tv_city.setAdapter(dataAdapter1);
		
		ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<>(SelectRoleActivity.this, R.layout.spinner_list_item, school_array);
		// Drop down layout style - list view with radio button
		dataAdapter2.setDropDownViewResource(R.layout.spinner_list_item);
		// attaching data adapter to spinner
		tv_school.setAdapter(dataAdapter2);
		
		ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<>(SelectRoleActivity.this, R.layout.spinner_list_item, role_array);
		// Drop down layout style - list view with radio button
		dataAdapter3.setDropDownViewResource(R.layout.spinner_list_item);
		// attaching data adapter to spinner
		tv_role.setAdapter(dataAdapter3);
	}
	
	private void AllAdaptors2(String[] school_array) {
		ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<>(SelectRoleActivity.this, R.layout.spinner_list_item, school_array);
		// Drop down layout style - list view with radio button
		dataAdapter2.setDropDownViewResource(R.layout.spinner_list_item);
		// attaching data adapter to spinner
		tv_school.setAdapter(dataAdapter2);
		
		ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<>(SelectRoleActivity.this, R.layout.spinner_list_item, role_array);
		// Drop down layout style - list view with radio button
		dataAdapter3.setDropDownViewResource(R.layout.spinner_list_item);
		// attaching data adapter to spinner
		tv_role.setAdapter(dataAdapter3);
	}
	
	private void AllAdaptors3(String[] role_array) {
		ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<>(SelectRoleActivity.this, R.layout.spinner_list_item, role_array);
		// Drop down layout style - list view with radio button
		dataAdapter3.setDropDownViewResource(R.layout.spinner_list_item);
		// attaching data adapter to spinner
		tv_role.setAdapter(dataAdapter3);
	}
	
	private void Allmethods() {
		tv_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				sel_state = state_id[i];
				((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.dark_green));
				if (sel_state.equals("0")) {
					Toast.makeText(SelectRoleActivity.this, "Please select state", Toast.LENGTH_SHORT).show();
				} else {
					if (isInternetOn()) {
						GetAllCitys b1 = new GetAllCitys(SelectRoleActivity.this);
						b1.execute(String.valueOf(Integer.parseInt(state_id[i])));
					} else {
						String city_array[] = {"Select City"};
						AllAdaptors1(city_array);
						String school_array[] = {"Select School"};
						AllAdaptors2(school_array);
						String role_array[] = {"Select Role"};
						AllAdaptors3(role_array);
						Toast.makeText(SelectRoleActivity.this, "No internet", Toast.LENGTH_SHORT).show();
					}
				}
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
			
			}
		});
		
		tv_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.dark_green));
				selCity = city_id[i];
				if (selCity.equals("0")) {
					Toast.makeText(SelectRoleActivity.this, "Please select city", Toast.LENGTH_SHORT).show();
				} else {
					if (isInternetOn()) {
						GetAllSchool b1 = new GetAllSchool(SelectRoleActivity.this);
						b1.execute(String.valueOf(Integer.parseInt(city_id[i])));
					} else {
						Toast.makeText(SelectRoleActivity.this, "No internet", Toast.LENGTH_SHORT).show();
					}
				}
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
			
			}
		});
		
		tv_school.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.dark_green));
				selSchool = school_id[i];
				selLogo = school_img[i];
				selLogo1 = school_img1[i];
				selToolColor = tool_color[i];
				selDrawerColor = drawer_color[i];
				selStatusColor = status_color[i];
				selTextColor1 = text_color1[i];
				selTextColor2 = text_color2[i];
				
				//Toast.makeText(SelectRoleActivity.this, ""+selToolColor, Toast.LENGTH_SHORT).show();
				SharedPreferences settings = getSharedPreferences(PREF_COLOR, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("tool", selToolColor);
				editor.putString("drawer", selDrawerColor);
				editor.putString("status", selStatusColor);
				editor.putString("text1", selTextColor1);
				editor.putString("text2", selTextColor2);
				editor.apply();
				
				if (selSchool.equals("0")) {
					Toast.makeText(SelectRoleActivity.this, "Please select school", Toast.LENGTH_SHORT).show();
				} else {
					if (isInternetOn()) {
						GetAllRoles b1 = new GetAllRoles(SelectRoleActivity.this);
						b1.execute(String.valueOf(Integer.parseInt(school_id[i])));
					} else {
						Toast.makeText(SelectRoleActivity.this, "No internet", Toast.LENGTH_SHORT).show();
					}
				}
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
			
			}
		});
		
		tv_role.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.dark_green));
				selRole = role_id[i];
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
			
			}
		});
		
		btn_login.setOnClickListener(view -> {
			str_state = sel_state;
			str_city = selCity;
			str_school = selSchool;
			str_role = selRole;
			str_logo = selLogo;
			str_logo1 = selLogo1;
			
			if (isInternetOn()) {
				if (str_state.equals("0")) {
					Toast.makeText(SelectRoleActivity.this, "Please choose above fields", Toast.LENGTH_SHORT).show();
				} else {
					if (str_city.equals("0")) {
						Toast.makeText(SelectRoleActivity.this, "Please Select City", Toast.LENGTH_SHORT).show();
					} else {
						if (str_school.equals("0")) {
							Toast.makeText(SelectRoleActivity.this, "Please Select School", Toast.LENGTH_SHORT).show();
						} else {
							if (str_role.equals("0")) {
								Toast.makeText(SelectRoleActivity.this, "Please Select Role", Toast.LENGTH_SHORT).show();
							} else {
								if (isInternetOn()) {
									
									Log.d("State", "" + str_state);
									Log.d("City", "" + str_city);
									Log.d("School", "" + str_school);
									Log.d("Role", "" + str_role);
									Log.d("Logo", "" + str_logo);
									
									Intent i = new Intent(SelectRoleActivity.this, AppLogin.class);
									i.putExtra("school_id", str_school);
									i.putExtra("role_id", str_role);
									i.putExtra("school_logo", str_logo);
									i.putExtra("school_logo1", str_logo1);
									startActivity(i);
									overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
									
								} else {
									Toast.makeText(SelectRoleActivity.this, "No internet", Toast.LENGTH_SHORT).show();
									
								}
							}
						}
					}
				}
			} else {
				Toast.makeText(SelectRoleActivity.this, "No internet", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	public final boolean isInternetOn() {
		ConnectivityManager connec =
				(ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		// Check for network connections
		assert connec != null;
		if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
				connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
				connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
				connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
			// if connected with internet
			return true;
		} else if (
				connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
						connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {
			return false;
		}
		return false;
	}
	
	//Offlines files
	private void getAllStates(String states) {
		try {
			JSONObject jo = new JSONObject(states);
			//Get the instance of JSONArray that contains JSONObjects
			JSONArray ja = jo.getJSONArray("result");
			//Iterate the jsonArray and print the info of JSONObjects
			state_array = new String[ja.length() + 1];
			state_id = new String[ja.length() + 1];
			state_array[0] = "Select State";
			state_id[0] = "0";
			for (int i = 0; i < ja.length(); i++) {
				jo = ja.getJSONObject(i);
				state_id[i + 1] = jo.getString("tbl_states_id");
				state_array[i + 1] = jo.getString("tbl_states_name");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		AllAdaptors();
	}
	
	private void getAllCitys(String city) {
		if (!city.equals("{\"result\":null}")) {
			try {
				JSONObject jo = new JSONObject(city);
				//Get the instance of JSONArray that contains JSONObjects
				JSONArray ja = jo.getJSONArray("result");
				city_array = new String[ja.length() + 1];
				city_id = new String[ja.length() + 1];
				city_array[0] = "Select City";
				city_id[0] = "0";
				
				for (int i = 0; i < ja.length(); i++) {
					jo = ja.getJSONObject(i);
					city_id[i + 1] = jo.getString("tbl_city_id");
					city_array[i + 1] = jo.getString("tbl_city_name");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			AllAdaptors1(city_array);
		} else {
			String city_array[] = {"Select City"};
			AllAdaptors1(city_array);
			String school_array[] = {"Select School"};
			AllAdaptors2(school_array);
			String role_array[] = {"Select Role"};
			AllAdaptors3(role_array);
			Toast.makeText(SelectRoleActivity.this, "No City", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void getAllSchools(String school) {
		if (!school.equals("{\"result\":null}")) {
			try {
				JSONObject jo = new JSONObject(school);
				//Get the instance of JSONArray that contains JSONObjects
				JSONArray ja = jo.getJSONArray("result");
				//Iterate the jsonArray and print the info of JSONObjects
				school_array = new String[ja.length() + 1];
				school_id = new String[ja.length() + 1];
				school_img = new String[ja.length() + 1];
				school_img1 = new String[ja.length() + 1];
				
				tool_color = new String[ja.length() + 1];
				drawer_color = new String[ja.length() + 1];
				status_color = new String[ja.length() + 1];
				text_color1 = new String[ja.length() + 1];
				text_color2 = new String[ja.length() + 1];
				
				school_array[0] = "Select School";
				school_id[0] = "0";
				school_img[0] = "Nil";
				school_img1[0] = "Nil";
				
				for (int i = 0; i < ja.length(); i++) {
					jo = ja.getJSONObject(i);
					school_id[i + 1] = jo.getString("tbl_school_id");
					school_array[i + 1] = jo.getString("tbl_school_name");
					school_img[i + 1] = jo.getString("tbl_school_logo");
					school_img1[i + 1] = jo.getString("tbl_school_logo2");
					
					tool_color[i + 1] = jo.getString("tbl_school_tool_color");
					drawer_color[i + 1] = jo.getString("tbl_school_menu_color");
					status_color[i + 1] = jo.getString("tbl_school_status_color");
					text_color1[i + 1] = jo.getString("tbl_school_text_color1");
					text_color2[i + 1] = jo.getString("tbl_school_text_color2");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			AllAdaptors2(school_array);
		} else {
			String school_array[] = {"Select School"};
			AllAdaptors2(school_array);
			String role_array[] = {"Select Role"};
			AllAdaptors3(role_array);
			Log.d("ARRAY", "" + Arrays.toString(school_array));
			Toast.makeText(SelectRoleActivity.this, "No School", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void getAllRoles(String role) {
		if (!role.equals("{\"result\":null}")) {
			try {
				JSONObject jo = new JSONObject(role);
				//Get the instance of JSONArray that contains JSONObjects
				JSONArray ja = jo.getJSONArray("result");
				//Iterate the jsonArray and print the info of JSONObjects
				role_array = new String[ja.length() + 1];
				role_id = new String[ja.length() + 1];
				role_array[0] = "Select Role";
				role_id[0] = "0";
				for (int i = 0; i < ja.length(); i++) {
					jo = ja.getJSONObject(i);
					role_id[i + 1] = jo.getString("tbl_role_id");
					role_array[i + 1] = jo.getString("tbl_role_name");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			AllAdaptors3(role_array);
		} else {
			String role_array[] = {"Select Role"};
			AllAdaptors3(role_array);
			Toast.makeText(SelectRoleActivity.this, "No Role", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	//Online Files
	//getAllStates
	@SuppressLint("StaticFieldLeak")
	public class GetAllStates extends AsyncTask<String, String, String> {
		Context ctx;
		
		GetAllStates(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			loader.setVisibility(View.VISIBLE);
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			try {
				URL url = new URL(ALL_STATES);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				
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
			} catch (MalformedURLException s) {
				s.printStackTrace();
			} catch (IOException s) {
				s.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			
			loader.setVisibility(View.GONE);
			//save state for offline
			sp_state = getSharedPreferences(PREF_STATES, 0);
			se_state = sp_state.edit();
			se_state.clear();
			se_state.putString("states_json", result);
			se_state.apply();
			
			try {
				JSONObject jo = new JSONObject(result);
				//Get the instance of JSONArray that contains JSONObjects
				JSONArray ja = jo.getJSONArray("result");
				//Iterate the jsonArray and print the info of JSONObjects
				state_array = new String[ja.length() + 1];
				state_id = new String[ja.length() + 1];
				state_array[0] = "Select State";
				state_id[0] = "0";
				for (int i = 0; i < ja.length(); i++) {
					jo = ja.getJSONObject(i);
					state_id[i + 1] = jo.getString("tbl_states_id");
					state_array[i + 1] = jo.getString("tbl_states_name");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			AllAdaptors();
			super.onPostExecute(result);
		}
	}
	
	@SuppressLint("StaticFieldLeak")
	public class GetAllCitys extends AsyncTask<String, String, String> {
		Context ctx;
		
		GetAllCitys(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			loader1.setVisibility(View.VISIBLE);
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			
			String state_id = params[0];
			String data;
			
			try {
				URL url = new URL(ALL_CITY);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				
				OutputStream outputStream = httpURLConnection.getOutputStream();
				
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
				data = URLEncoder.encode("state_id", "UTF-8") + "=" + URLEncoder.encode(state_id, "UTF-8");
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
			
			loader1.setVisibility(View.GONE);
			sp_city = getSharedPreferences(PREF_CITYS, 0);
			se_city = sp_city.edit();
			se_city.clear();
			se_city.putString("city_json", result);
			se_city.apply();
			
			Log.d("File", result);
			if (!result.equals("{\"result\":null}")) {
				try {
					JSONObject jo = new JSONObject(result);
					//Get the instance of JSONArray that contains JSONObjects
					JSONArray ja = jo.getJSONArray("result");
					city_array = new String[ja.length() + 1];
					city_id = new String[ja.length() + 1];
					city_array[0] = "Select City";
					city_id[0] = "0";
					for (int i = 0; i < ja.length(); i++) {
						jo = ja.getJSONObject(i);
						city_id[i + 1] = jo.getString("tbl_city_id");
						city_array[i + 1] = jo.getString("tbl_city_name");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				AllAdaptors1(city_array);
				String school_array[] = {"Select School"};
				AllAdaptors2(school_array);
				String role_array[] = {"Select Role"};
				AllAdaptors3(role_array);
			} else {
				String city_array[] = {"Select City"};
				AllAdaptors1(city_array);
				String school_array[] = {"Select School"};
				AllAdaptors2(school_array);
				String role_array[] = {"Select Role"};
				AllAdaptors3(role_array);
				Toast.makeText(SelectRoleActivity.this, "No City", Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}
	}
	
	//getAllSchools
	@SuppressLint("StaticFieldLeak")
	public class GetAllSchool extends AsyncTask<String, String, String> {
		Context ctx;
		
		GetAllSchool(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			loader2.setVisibility(View.VISIBLE);
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			String city_id = params[0];
			String data;
			try {
				URL url = new URL(ALL_SCHOOL);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				
				OutputStream outputStream = httpURLConnection.getOutputStream();
				
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
				data = URLEncoder.encode("city_id", "UTF-8") + "=" + URLEncoder.encode(city_id, "UTF-8");
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
			loader2.setVisibility(View.GONE);
			sp_school = getSharedPreferences(PREF_SCHOOLS, 0);
			se_school = sp_school.edit();
			se_school.clear();
			se_school.putString("school_json", result);
			se_school.apply();
			
			Log.d("File", result);
			if (!result.equals("{\"result\":null}")) {
				try {
					JSONObject jo = new JSONObject(result);
					//Get the instance of JSONArray that contains JSONObjects
					JSONArray ja = jo.getJSONArray("result");
					//Iterate the jsonArray and print the info of JSONObjects
					school_array = new String[ja.length() + 1];
					school_id = new String[ja.length() + 1];
					school_img = new String[ja.length() + 1];
					school_img1 = new String[ja.length() + 1];
					
					tool_color = new String[ja.length() + 1];
					drawer_color = new String[ja.length() + 1];
					status_color = new String[ja.length() + 1];
					text_color1 = new String[ja.length() + 1];
					text_color2 = new String[ja.length() + 1];
					
					school_array[0] = "Select School";
					school_id[0] = "0";
					school_img[0] = "Nil";
					school_img1[0] = "Nil";
					
					for (int i = 0; i < ja.length(); i++) {
						jo = ja.getJSONObject(i);
						school_id[i + 1] = jo.getString("tbl_school_id");
						school_array[i + 1] = jo.getString("tbl_school_name");
						school_img[i + 1] = jo.getString("tbl_school_logo");
						school_img1[i + 1] = jo.getString("tbl_school_logo2");
						
						tool_color[i + 1] = jo.getString("tbl_school_tool_color");
						drawer_color[i + 1] = jo.getString("tbl_school_menu_color");
						status_color[i + 1] = jo.getString("tbl_school_status_color");
						text_color1[i + 1] = jo.getString("tbl_school_text_color1");
						text_color2[i + 1] = jo.getString("tbl_school_text_color2");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				AllAdaptors2(school_array);
			} else {
				String school_array[] = {"Select School"};
				AllAdaptors2(school_array);
				String role_array[] = {"Select Role"};
				AllAdaptors3(role_array);
				Log.d("ARRAY", "" + Arrays.toString(school_array));
				Toast.makeText(SelectRoleActivity.this, "No School", Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}
	}
	
	//getAllRoles
	@SuppressLint("StaticFieldLeak")
	public class GetAllRoles extends AsyncTask<String, String, String> {
		Context ctx;
		
		GetAllRoles(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			loader3.setVisibility(View.VISIBLE);
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			try {
				URL url = new URL(ALL_ROLE);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				
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
			loader3.setVisibility(View.GONE);
			
			sp_role = getSharedPreferences(PREF_ROLES, 0);
			se_role = sp_role.edit();
			se_role.clear();
			se_role.putString("role_json", result);
			se_role.apply();
			
			if (!result.equals("{\"result\":null}")) {
				try {
					JSONObject jo = new JSONObject(result);
					//Get the instance of JSONArray that contains JSONObjects
					JSONArray ja = jo.getJSONArray("result");
					//Iterate the jsonArray and print the info of JSONObjects
					role_array = new String[ja.length() + 1];
					role_id = new String[ja.length() + 1];
					role_array[0] = "Select Role";
					role_id[0] = "0";
					for (int i = 0; i < ja.length(); i++) {
						jo = ja.getJSONObject(i);
						role_id[i + 1] = jo.getString("tbl_role_id");
						role_array[i + 1] = jo.getString("tbl_role_name");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				AllAdaptors3(role_array);
			} else {
				String role_array[] = {"Select Role"};
				AllAdaptors3(role_array);
				Toast.makeText(SelectRoleActivity.this, "No Role", Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}
	}
}
