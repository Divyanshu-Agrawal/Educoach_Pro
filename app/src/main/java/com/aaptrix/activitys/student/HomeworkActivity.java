package com.aaptrix.activitys.student;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.appbar.AppBarLayout;

import com.aaptrix.activitys.admin.AddNewHomework;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
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

import static com.aaptrix.tools.HttpUrl.ALL_BATCHS;
import static com.aaptrix.tools.HttpUrl.LIST_OF_HOMEWORK;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREFS_RW;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

/**
 * Created by Administrator on 11/29/2017.
 */

public class HomeworkActivity extends AppCompatActivity {
	
	SharedPreferences.Editor editor;
	String userId, schoolId, userSection, userrType, userName;
	AppBarLayout appBarLayout;
	SharedPreferences.Editor editorColor;
	String selToolColor, selDrawerColor, selStatusColor, selTextColor1, selTextColor2;
	TextView tool_title, homework_tv;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	ArrayList<DataBeanDairy> dairyArrayList = new ArrayList<>();
	DataBeanDairy dbd;
	String diaryId, dairyDate, dairyTitle, dairyDetails, dairyReportedBy, dimg;
	ListView homewrk_list;
	String selBatch;
	DairyListAdaptor dairyListAdaptor;
	LinearLayout add_layout;
	ImageView addhomework;
	Spinner batch_spinner;
	String[] batch_array;
	String skip = "0";
	TextView snack;
	
	//offline
	private SharedPreferences sp_aboutUs;
	SharedPreferences.Editor se_aboutUs;
	public static final String PREFS_ABOUTUS = "json_about";
	View footerView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homework_layout);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		appBarLayout = findViewById(R.id.appBarLayout);
		tool_title = findViewById(R.id.tool_title);
		homework_tv = findViewById(R.id.homework_tv);
		homework_tv.setVisibility(View.GONE);
		homewrk_list = findViewById(R.id.homewrk_list);
		batch_spinner = findViewById(R.id.batch_spinner);
		snack = findViewById(R.id.snack);
		add_layout = findViewById(R.id.add_layout);
		addhomework = findViewById(R.id.add_homework);
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		editor = settings.edit();
		userId = settings.getString("userID", "");
		schoolId = settings.getString("str_school_id", "");
		userSection = settings.getString("userSection", "");
		userrType = settings.getString("userrType", "");
		userName = settings.getString("userName", "");
		
		SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
		editorColor = settingsColor.edit();
		selToolColor = settingsColor.getString("tool", "");
		selDrawerColor = settingsColor.getString("drawer", "");
		selStatusColor = settingsColor.getString("status", "");
		selTextColor1 = settingsColor.getString("text1", "");
		selTextColor2 = settingsColor.getString("text2", "");
		
		callFile("0");

		if (selToolColor != null && !selToolColor.isEmpty() && !selToolColor.equals("null"))
			appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.parseColor(selStatusColor));
		}
		tool_title.setTextColor(Color.parseColor(selTextColor1));
		GradientDrawable bgShape = (GradientDrawable) add_layout.getBackground();
		bgShape.setColor(Color.parseColor(selToolColor));
	}
	
	private void callFile(final String s) {

		SharedPreferences sp = getSharedPreferences(PREFS_RW, 0);
		String result = sp.getString("result", "");

		try {
			JSONObject jsonObject = new JSONObject(result);
			JSONArray jsonArray = jsonObject.getJSONArray("result");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject object = jsonArray.getJSONObject(i);
				if (object.getString("tbl_insti_buzz_cate_name").equals("Assignments")) {
					if (object.getString("tbl_scl_inst_buzz_detl_write_status").equals("Active")) {
						add_layout.setVisibility(View.VISIBLE);
					} else {
						add_layout.setVisibility(View.GONE);
					}
					break;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		addhomework.setOnClickListener(v -> {
			Intent intent = new Intent(this, AddNewHomework.class);
			intent.putExtra("type", "add");
			intent.putExtra("batch", selBatch);
			startActivity(intent);
		});

		if (userrType.equals("Teacher") || userrType.equals("Admin")) {
			try {
				File directory = getFilesDir();
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(directory, "batches")));
				String json = in.readObject().toString();
				in.close();
				if (!json.equals("{\"result\":null}")) {
					try {
						JSONObject jo = new JSONObject(json);
						JSONArray ja = jo.getJSONArray("result");
						batch_array = new String[ja.length()];
						for (int i = 0; i < ja.length(); i++) {
							jo = ja.getJSONObject(i);
							batch_array[i] = jo.getString("tbl_batch_name");
						}
						selBatch = batch_array[0];
					} catch (JSONException e) {
						e.printStackTrace();
					}
					setBatch();
				} else {
					Toast.makeText(this, "No Batch", Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
				GetAllBatches b1 = new GetAllBatches(this);
				b1.execute(schoolId);
			}
			mSwipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
			mSwipeRefreshLayout.setOnRefreshListener(() -> {
				if (isInternetOn()) {
					dairyArrayList.clear();
					GetAllHomework b1 = new GetAllHomework(HomeworkActivity.this);
					b1.execute(schoolId, selBatch, userId, userrType, "0");
				} else {
					mSwipeRefreshLayout.setRefreshing(false);
					Toast.makeText(HomeworkActivity.this, "No network Please connect with network for update", Toast.LENGTH_SHORT).show();
				}
			});
		} else {
			batch_spinner.setVisibility(View.GONE);
			mSwipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
			mSwipeRefreshLayout.setOnRefreshListener(() -> {
				if (isInternetOn()) {
					dairyArrayList.clear();
					GetAllHomework b1 = new GetAllHomework(HomeworkActivity.this);
					b1.execute(schoolId, userSection, userId, userrType, "0");
				} else {
					mSwipeRefreshLayout.setRefreshing(false);
					Toast.makeText(HomeworkActivity.this, "No network Please connect with network for update", Toast.LENGTH_SHORT).show();
				}
			});
			sp_aboutUs = getSharedPreferences(PREFS_ABOUTUS, 0);
			String aboutUs = sp_aboutUs.getString("json_aboutus", "");
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
				GetAllHomework b1 = new GetAllHomework(HomeworkActivity.this);
				b1.execute(schoolId, userSection, userId, userrType, s);
			}
		}
	}

	@SuppressLint("StaticFieldLeak")
	public class GetAllBatches extends AsyncTask<String, String, String> {
		Context ctx;

		GetAllBatches(Context ctx) {
			this.ctx = ctx;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {

			String school_id = params[0];
			String data;

			try {

				URL url = new URL(ALL_BATCHS);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);

				OutputStream outputStream = httpURLConnection.getOutputStream();

				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
				data = URLEncoder.encode("school_id", "UTF-8") + "=" + URLEncoder.encode(school_id, "UTF-8") + "&" +
						URLEncoder.encode("tbl_users_id", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");
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
			Log.d("result", result);
			if (!result.equals("{\"result\":null}")) {
				try {
					JSONObject jo = new JSONObject(result);
					JSONArray ja = jo.getJSONArray("result");
					batch_array = new String[ja.length()];
					for (int i = 0; i < ja.length(); i++) {
						jo = ja.getJSONObject(i);
						batch_array[i + 1] = jo.getString("tbl_batch_name");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				setBatch();
			} else {
				Toast.makeText(ctx, "No Batch", Toast.LENGTH_SHORT).show();
			}

			super.onPostExecute(result);
		}

	}

	private void setBatch() {
		ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(this, R.layout.spinner_list_item1, batch_array);
		dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
		batch_spinner.setAdapter(dataAdapter1);
		batch_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				selBatch = batch_array[i];
				GetAllHomework b1 = new GetAllHomework(HomeworkActivity.this);
				b1.execute(schoolId, selBatch, userId, userrType, "0");
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});
	}
	
	private void getAboutUs(String aboutUs) {
		if (aboutUs.equals("{\"result\":null}")) {
			homework_tv.setVisibility(View.VISIBLE);
			dairyArrayList.clear();
		} else {
			homework_tv.setVisibility(View.GONE);
			try {
				JSONObject jsonRootObject = new JSONObject(aboutUs);
				JSONArray jsonArray = jsonRootObject.getJSONArray("result");
				dairyArrayList.clear();
				for (int i = 0; i < jsonArray.length(); i++) {
					dbd = new DataBeanDairy();
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					diaryId = jsonObject.getString("tbl_homework_id");
					dairyDate = jsonObject.getString("tbl_homework_date");
					dairyTitle = jsonObject.getString("tbl_homework_name");
					dairyDetails = jsonObject.getString("tbl_homework_details");
					dairyReportedBy = jsonObject.getString("tbl_homework_subject");
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
	
	@SuppressLint("StaticFieldLeak")
	public class GetAllHomework extends AsyncTask<String, String, String> {
		Context ctx;
		
		GetAllHomework(Context ctx) {
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
			String userId = params[2];
			String userrType = params[3];
			skip = params[4];
			String data;
			
			try {
				URL url = new URL(LIST_OF_HOMEWORK);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				OutputStream outputStream = httpURLConnection.getOutputStream();
				
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
				data = URLEncoder.encode("schoolId", "UTF-8") + "=" + URLEncoder.encode(schoolId, "UTF-8") + "&" +
						URLEncoder.encode("section", "UTF-8") + "=" + URLEncoder.encode(sectionName, "UTF-8") + "&" +
						URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8") + "&" +
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
			Log.e("Json", "" + result);
			sp_aboutUs = getSharedPreferences(PREFS_ABOUTUS, 0);
			se_aboutUs = sp_aboutUs.edit();
			se_aboutUs.clear();
			se_aboutUs.putString("json_aboutus", result);
			se_aboutUs.commit();
			mSwipeRefreshLayout.setRefreshing(false);
			
			if (result.equals("{\"result\":null}")) {
				if (userrType.equals("Student")) {
					if (dairyArrayList.size() > 0) {
						homework_tv.setVisibility(View.GONE);
						homewrk_list.removeFooterView(footerView);
					} else {
						homework_tv.setVisibility(View.VISIBLE);
						homewrk_list.setVisibility(View.GONE);
					}
				} else {
					homework_tv.setVisibility(View.VISIBLE);
					homewrk_list.setVisibility(View.GONE);
					Toast.makeText(ctx, "No Assignment", Toast.LENGTH_SHORT).show();
				}
			} else {
				homework_tv.setVisibility(View.GONE);
				homewrk_list.setVisibility(View.VISIBLE);
				try {

					JSONObject jsonRootObject = new JSONObject(result);
					JSONArray jsonArray = jsonRootObject.getJSONArray("result");
					dairyArrayList.clear();
					for (int i = 0; i < jsonArray.length(); i++) {
						dbd = new DataBeanDairy();
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						diaryId = jsonObject.getString("tbl_homework_id");
						dairyDate = jsonObject.getString("tbl_homework_date");
						dairyTitle = jsonObject.getString("tbl_homework_name");
						dairyDetails = jsonObject.getString("tbl_homework_details");
						dairyReportedBy = jsonObject.getString("tbl_homework_subject");
						dimg = jsonObject.getString("tbl_homework_img");
//                        String[] image = dimg.split(",");
//                        String url = settings.getString("url", "") + "/homework/" + image[0].replace("\"", "").replace("[", "").replace("]", "");
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
		dairyListAdaptor = new DairyListAdaptor(HomeworkActivity.this, R.layout.class_tt_list_item, dairyArrayList);
		homewrk_list.setAdapter(dairyListAdaptor);
		dairyListAdaptor.notifyDataSetChanged();
		homewrk_list.setSelectionFromTop(Integer.parseInt(skip), 0);
		homewrk_list.removeFooterView(footerView);//Add view to list view as footer view

		homewrk_list.setOnItemClickListener((parent, view, position, id) -> {
			String dairyId = dairyArrayList.get(position).getDiaryId();
			String dairyTitle = dairyArrayList.get(position).getDairyTitle();
			String dairyDetails = dairyArrayList.get(position).getDairyDetails();
			String dairyDate = dairyArrayList.get(position).getDairyDate();
			String dairyimg = dairyArrayList.get(position).getDiaryImg();
			Intent i11 = new Intent(HomeworkActivity.this, AchievmentDetailsActivity.class);
			i11.putExtra("dairyId", dairyId);
			i11.putExtra("achImg", dairyimg);
			i11.putExtra("achCate", "homework");
			i11.putExtra("achTitle", dairyTitle);
			i11.putExtra("achDesc", dairyDetails);
			i11.putExtra("acgDate", dairyDate);
			i11.putExtra("batch", selBatch);
			startActivity(i11);
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		});
		
		if (dairyArrayList.size() < 9) {
			homewrk_list.removeFooterView(footerView);//Add view to list view as footer view
		}

		SharedPreferences sp = getSharedPreferences(PREFS_RW, 0);
		String json = sp.getString("result", "");

		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONArray jsonArray = jsonObject.getJSONArray("result");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject object = jsonArray.getJSONObject(i);
				if (object.getString("tbl_insti_buzz_cate_name").equals("Assignments")) {
					if (object.getString("tbl_scl_inst_buzz_detl_write_status").equals("Active")) {
						homewrk_list.setOnItemLongClickListener((arg0, arg1, pos, id) -> {
							if (isInternetOn()) {
								Intent intent = new Intent(HomeworkActivity.this, AddNewHomework.class);
								intent.putExtra("type", "update");
								intent.putExtra("batch", selBatch);
								intent.putExtra("title", dairyArrayList.get(pos).getDairyTitle());
								intent.putExtra("description", dairyArrayList.get(pos).getDairyDetails());
								intent.putExtra("date", dairyArrayList.get(pos).getDairyDate());
								intent.putExtra("subject", dairyArrayList.get(pos).getDairyReportedBy());
								intent.putExtra("id", dairyArrayList.get(pos).getDiaryId());
								intent.putExtra("image", dairyArrayList.get(pos).getDiaryImg());
								startActivity(intent);
							} else {
								Toast.makeText(HomeworkActivity.this, "No network Please connect with network for update", Toast.LENGTH_SHORT).show();
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
		if (connec != null) {
			if (Objects.requireNonNull(connec).getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
					connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
					connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
					connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
				return true;
			} else if (
					connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
							connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {
				return false;
			}
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
