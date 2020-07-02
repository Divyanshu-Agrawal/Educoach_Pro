package com.aaptrix.activitys.student;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.appbar.AppBarLayout;

import com.aaptrix.activitys.admin.AddNewDiary;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
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
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

import com.aaptrix.adaptor.DairyListAdaptor;
import com.aaptrix.databeans.DataBeanDairy;
import com.aaptrix.R;

import static com.aaptrix.tools.HttpUrl.LIST_OF_DAIRYS;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREFS_RW;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

/**
 * Created by Administrator on 11/29/2017.
 */

public class DairyActivity extends AppCompatActivity {
	
	SharedPreferences.Editor editor;
	String userId, schoolId, userSection, userName;
	AppBarLayout appBarLayout;
	SharedPreferences.Editor editorColor;
	String selToolColor, selDrawerColor, selStatusColor, selTextColor1, selTextColor2;
	TextView tool_title, no_dairy;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	ArrayList<DataBeanDairy> dairyArrayList = new ArrayList<>();
	DataBeanDairy dbd;
	String diaryId, dairyDate, dairyTitle, dairyDetails, dairyReportedBy, dimg;
	ListView dairy_list;
	DairyListAdaptor dairyListAdaptor;
	String teachBatchName, userrType, studentId;
	//offline
	private SharedPreferences sp_aboutUs;
	SharedPreferences.Editor se_aboutUs;
	public static final String PREFS_ABOUTUS = "json_about1";
	View footerView;
	TextView snack;
	int a = 10;
	String skip = "0";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dairy_layout);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		appBarLayout = findViewById(R.id.appBarLayout);
		tool_title = findViewById(R.id.tool_title);
		no_dairy = findViewById(R.id.no_dairy);
		dairy_list = findViewById(R.id.dairy_list);
		no_dairy.setVisibility(View.GONE);
		snack = findViewById(R.id.snack);
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		editor = settings.edit();
		userId = settings.getString("userID", "");
		userName = settings.getString("userName", "");
		schoolId = settings.getString("str_school_id", "");
		userSection = settings.getString("userSection", "");
		userrType = settings.getString("userrType", "");
		mSwipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
		//color
		SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
		editorColor = settingsColor.edit();
		selToolColor = settingsColor.getString("tool", "");
		selDrawerColor = settingsColor.getString("drawer", "");
		selStatusColor = settingsColor.getString("status", "");
		selTextColor1 = settingsColor.getString("text1", "");
		selTextColor2 = settingsColor.getString("text2", "");
		
		callFile("0");
		
		appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.parseColor(selStatusColor));
		}
		tool_title.setTextColor(Color.parseColor(selTextColor1));
		
	}
	
	private void callFile(final String s) {
		if (userrType.equals("Teacher")) {
			teachBatchName = getIntent().getStringExtra("batch");
			studentId = getIntent().getStringExtra("studentId");
			
			mSwipeRefreshLayout.setOnRefreshListener(() -> {
				if (isInternetOn()) {
					dairyArrayList.clear();
					a = 10;
					GetAllDairyDetails b1 = new GetAllDairyDetails(DairyActivity.this);
					b1.execute(schoolId, teachBatchName, studentId, userrType, userId, "0");
				} else {
					mSwipeRefreshLayout.setRefreshing(false);
					Toast.makeText(DairyActivity.this, "No network Please connect with network for update", Toast.LENGTH_SHORT).show();
				}
			});
			if (isInternetOn()) {
				GetAllDairyDetails b1 = new GetAllDairyDetails(DairyActivity.this);
				// Toast.makeText(getApplicationContext(), studentId+"   "+userId, Toast.LENGTH_SHORT ).show();
				b1.execute(schoolId, teachBatchName, studentId, userrType, userId, s);
			}
		}
		if (userrType.equals("Admin")) {
			teachBatchName = getIntent().getStringExtra("batch");
			studentId = getIntent().getStringExtra("studentId");
			
			mSwipeRefreshLayout.setOnRefreshListener(() -> {
				if (isInternetOn()) {
					dairyArrayList.clear();
					a = 10;
					GetAllDairyDetails b1 = new GetAllDairyDetails(DairyActivity.this);
					b1.execute(schoolId, teachBatchName, studentId, userrType, userId, "0");
				} else {
					mSwipeRefreshLayout.setRefreshing(false);
					Toast.makeText(DairyActivity.this, "No network Please connect with network for update", Toast.LENGTH_SHORT).show();
				}
			});
			if (isInternetOn()) {
				GetAllDairyDetails b1 = new GetAllDairyDetails(DairyActivity.this);
				b1.execute(schoolId, teachBatchName, studentId, userrType, userId, s);
			}
		} else if (userrType.equals("Student")) {
			mSwipeRefreshLayout.setOnRefreshListener(() -> {
				if (isInternetOn()) {
					dairyArrayList.clear();
					a = 10;
					GetAllDairyDetails b1 = new GetAllDairyDetails(DairyActivity.this);
					b1.execute(schoolId, userSection, userId, userrType, "", "0");
				} else {
					mSwipeRefreshLayout.setRefreshing(false);
					Toast.makeText(DairyActivity.this, "No network Please connect with network for update", Toast.LENGTH_SHORT).show();
				}
			});
			sp_aboutUs = getSharedPreferences(PREFS_ABOUTUS, 0);
			String aboutUs = sp_aboutUs.getString("json_aboutus1", "");

			if (aboutUs != null && !aboutUs.equals("null") && !aboutUs.isEmpty() && aboutUs.length() > 5) {
				getAboutUs(aboutUs);
				snack.setVisibility(View.VISIBLE);
                new CountDownTimer(3000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        snack.setVisibility(View.GONE);
                    }
                }.start();
			} else {
				GetAllDairyDetails b1 = new GetAllDairyDetails(DairyActivity.this);
				b1.execute(schoolId, userSection, userId, userrType, s);
			}
		}
	}
	
	private void getAboutUs(String aboutUs) {
		Log.d("aboutUs", aboutUs);
		if (aboutUs.equals("{\"result\":null}")) {
			no_dairy.setVisibility(View.VISIBLE);
			dairyArrayList.clear();
		} else {
			no_dairy.setVisibility(View.GONE);
			try {
				JSONObject jsonRootObject = new JSONObject(aboutUs);
				JSONArray jsonArray = jsonRootObject.getJSONArray("result");
				dairyArrayList.clear();
				for (int i = 0; i < jsonArray.length(); i++) {
					dbd = new DataBeanDairy();
					
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					diaryId = jsonObject.getString("tbl_diary_id");
					dairyDate = jsonObject.getString("tbl_diary_date");
					dairyTitle = jsonObject.getString("tbl_diary_title");
					dairyDetails = jsonObject.getString("tbl_diary_details");
					dairyReportedBy = jsonObject.getString("tbl_diary_reportedBy");
					dbd.setDiaryId(diaryId);
					dbd.setDairyDate(dairyDate);
					dbd.setDairyTitle(dairyTitle);
					dbd.setDairyDetails(dairyDetails);
					dbd.setDairyReportedBy(dairyReportedBy);
					dairyArrayList.add(dbd);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (dairyArrayList.size() != 0) {
				listItms(skip);
			}
		}
	}
	
	
	//online
	@SuppressLint("StaticFieldLeak")
	public class GetAllDairyDetails extends AsyncTask<String, String, String> {
		Context ctx;
		
		GetAllDairyDetails(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			mSwipeRefreshLayout.setRefreshing(true);
			super.onPreExecute();
			
		}
		
		@Override
		protected String doInBackground(String... params) {
			
			String schoolId = params[0];
			String sectionName = params[1];
			String studentId = params[2];
			String userrType = params[3];
			skip = params[4];
			String data;
			
			try {
				
				URL url = new URL(LIST_OF_DAIRYS);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				
				OutputStream outputStream = httpURLConnection.getOutputStream();
				
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
				data = URLEncoder.encode("schoolId", "UTF-8") + "=" + URLEncoder.encode(schoolId, "UTF-8") + "&" +
						URLEncoder.encode("section", "UTF-8") + "=" + URLEncoder.encode(sectionName, "UTF-8") + "&" +
						URLEncoder.encode("studentId", "UTF-8") + "=" + URLEncoder.encode(studentId, "UTF-8") + "&" +
						URLEncoder.encode("userrType", "UTF-8") + "=" + URLEncoder.encode(userrType, "UTF-8");
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
			Log.d("Dairy", "" + result);
			mSwipeRefreshLayout.setRefreshing(false);
			sp_aboutUs = getSharedPreferences(PREFS_ABOUTUS, 0);
			se_aboutUs = sp_aboutUs.edit();
			se_aboutUs.clear();
			se_aboutUs.putString("json_aboutus1", result);
			se_aboutUs.commit();
			if (result.equals("{\"result\":null}")) {
				if (dairyArrayList.size() > 0) {
					no_dairy.setVisibility(View.GONE);
					dairy_list.removeFooterView(footerView);
				} else {
					no_dairy.setVisibility(View.VISIBLE);
					dairy_list.setVisibility(View.GONE);
				}
			} else {
				no_dairy.setVisibility(View.GONE);
				try {
					dairyArrayList.clear();
					JSONObject jsonRootObject = new JSONObject(result);
					JSONArray jsonArray = jsonRootObject.getJSONArray("result");
					for (int i = 0; i < jsonArray.length(); i++) {
						dbd = new DataBeanDairy();
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						diaryId = jsonObject.getString("tbl_diary_id");
						dairyDate = jsonObject.getString("tbl_diary_date");
						dairyTitle = jsonObject.getString("tbl_diary_title");
						dairyDetails = jsonObject.getString("tbl_diary_details");
						dairyReportedBy = jsonObject.getString("tbl_diary_reportedBy");
						dimg = jsonObject.getString("tbl_diary_img");
						dbd.setDiaryId(diaryId);
						dbd.setDairyDate(dairyDate);
						dbd.setDairyTitle(dairyTitle);
						dbd.setDairyDetails(dairyDetails);
						dbd.setDairyReportedBy(dairyReportedBy);
						dbd.setDiaryImg(dimg);
						dairyArrayList.add(dbd);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (dairyArrayList.size() != 0) {
					listItms(skip);
				}
				super.onPostExecute(result);
			}
		}
	}
	
	private void listItms(String skip) {
		dairyListAdaptor = new DairyListAdaptor(DairyActivity.this, R.layout.class_tt_list_item, dairyArrayList);
		dairy_list.setAdapter(dairyListAdaptor);
//		dairy_list.setSelectionFromTop(Integer.parseInt(skip), 0);
		dairy_list.removeFooterView(footerView);//Add view to list view as footer view
//		addHeaderFooterView();
		dairy_list.setOnItemClickListener((parent, view, position, id) -> {
			String dairyId = dairyArrayList.get(position).getDiaryId();
			String dairyTitle = dairyArrayList.get(position).getDairyTitle();
			String dairyDetails = dairyArrayList.get(position).getDairyDetails();
			String dairyDate = dairyArrayList.get(position).getDairyDate();
			String dairyImage = dimg;
			
			Intent i11 = new Intent(DairyActivity.this, AchievmentDetailsActivity.class);
			i11.putExtra("dairyId", dairyId);
			//i11.putExtra("achImg","");
			i11.putExtra("achCate", "dairy");
			i11.putExtra("achTitle", dairyTitle);
			i11.putExtra("achDesc", dairyDetails);
			i11.putExtra("acgDate", dairyDate);
			i11.putExtra("achImg", dairyImage);
			startActivity(i11);
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		});
		
		if (dairyArrayList.size() < 9) {
			dairy_list.removeFooterView(footerView);//Add view to list view as footer view
		}

		SharedPreferences sp = getSharedPreferences(PREFS_RW, 0);
		String json = sp.getString("result", "");

		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONArray jsonArray = jsonObject.getJSONArray("result");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject object = jsonArray.getJSONObject(i);
				if (object.getString("tbl_insti_buzz_cate_name").equals("Remarks")) {
					if (object.getString("tbl_scl_inst_buzz_detl_write_status").equals("Active")) {
						dairy_list.setOnItemLongClickListener((arg0, arg1, pos, id) -> {
							// TODO Auto-generated method stub
							if (isInternetOn()) {
								Intent intent = new Intent(DairyActivity.this, AddNewDiary.class);
								intent.putExtra("type", "update");
								intent.putExtra("title", dairyArrayList.get(pos).getDairyTitle());
								intent.putExtra("reportedBy", dairyArrayList.get(pos).getDairyReportedBy());
								intent.putExtra("description",  dairyArrayList.get(pos).getDairyDetails());
								intent.putExtra("date", dairyArrayList.get(pos).getDairyDate());
								intent.putExtra("image", dairyArrayList.get(pos).getDiaryImg());
								intent.putExtra("id", dairyArrayList.get(pos).getDiaryId());
								startActivity(intent);
								Log.v("long clicked", "pos: " + pos);
							} else {
								Toast.makeText(DairyActivity.this, "No network Please connect with network for update", Toast.LENGTH_SHORT).show();
							}
							return true;
						});
					}
					break;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public final boolean isInternetOn() {
		ConnectivityManager connec = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		// Check for network connections
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
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}
	
}
