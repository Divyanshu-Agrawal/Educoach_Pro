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

import com.aaptrix.activitys.admin.AddNewPublication;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import com.aaptrix.adaptor.ActivitiesListAdaptor;
import com.aaptrix.databeans.DataBeanActivities;
import com.aaptrix.R;

import static com.aaptrix.tools.HttpUrl.ALL_PUBLICATION;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREFS_RW;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

/**
 * Created by Administrator on 11/29/2017.
 */

public class PublicationActivity extends AppCompatActivity {

	String userId, userSchoolId, userSchoolLogo, userRoleId, userSection, userrType;
	AppBarLayout appBarLayout;
	String selToolColor, selDrawerColor, selStatusColor, selTextColor1, selTextColor2;
	TextView tool_title;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	
	ArrayList<DataBeanActivities> activitiesArray = new ArrayList<>();
	DataBeanActivities dbact;
	String activiId, activiTitle, activiDesc, activiDate, activiImg, achImg;
	ListView publication_list;
	ActivitiesListAdaptor activitiesListAdaptor;
	LinearLayout add_layout;
	ImageView iv_add_more_publication;
	
	//offline
	private SharedPreferences sp_acti;
	SharedPreferences.Editor se_acti;
	public static final String PREFS_ACTI = "json_acti";
	TextView no_publication;
	TextView snack;
	View footerView;
	String skip;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.publication_layout);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		appBarLayout = findViewById(R.id.appBarLayout);
		tool_title = findViewById(R.id.tool_title);
		no_publication = findViewById(R.id.no_publication);
		publication_list = findViewById(R.id.publication_list);
		mSwipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
		add_layout = findViewById(R.id.add_layout);
		snack = findViewById(R.id.snack);
		iv_add_more_publication = findViewById(R.id.iv_add_more_publication);
		mSwipeRefreshLayout.setRefreshing(false);
		publication_list.setEnabled(true);

		SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
		selToolColor = settingsColor.getString("tool", "");
		selDrawerColor = settingsColor.getString("drawer", "");
		selStatusColor = settingsColor.getString("status", "");
		selTextColor1 = settingsColor.getString("text1", "");
		selTextColor2 = settingsColor.getString("text2", "");
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		userId = settings.getString("userID", "");
		userSchoolId = settings.getString("str_school_id", "");
		userSchoolLogo = settings.getString("userSchoolLogo", "");
		userRoleId = settings.getString("str_role_id", "");
		userSection = settings.getString("userSection", "");
		userrType = settings.getString("userrType", "");

		sp_acti = getSharedPreferences(PREFS_ACTI, 0);
		String acti = sp_acti.getString("json_acti", "");
		mSwipeRefreshLayout.setRefreshing(false);
		publication_list.setEnabled(true);

		GetAllPublication b1 = new GetAllPublication(PublicationActivity.this);
		b1.execute(userSchoolId, userSection, userrType, "0");

		SharedPreferences sp = getSharedPreferences(PREFS_RW, 0);
		String json = sp.getString("result", "");

		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONArray jsonArray = jsonObject.getJSONArray("result");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject object = jsonArray.getJSONObject(i);
				if (object.getString("tbl_insti_buzz_cate_name").equals("What's New!")) {
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

		iv_add_more_publication.setOnClickListener(view -> {
			if (isInternetOn()) {
				Intent intent = new Intent(this, AddNewPublication.class);
				intent.putExtra("type", "add");
				startActivity(intent);
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			} else {
				Toast.makeText(PublicationActivity.this, "No network Please connect with network for update", Toast.LENGTH_SHORT).show();
			}
		});
		
		mSwipeRefreshLayout.setOnRefreshListener(() -> {
			if (isInternetOn()) {
				publication_list.setEnabled(false);
				activitiesArray.clear();
				GetAllPublication b = new GetAllPublication(PublicationActivity.this);
				b.execute(userSchoolId, userSection, userrType, "0");
			} else {
				Toast.makeText(PublicationActivity.this, "No network Please connect with network for update", Toast.LENGTH_SHORT).show();
				mSwipeRefreshLayout.setRefreshing(false);
				publication_list.setEnabled(true);
			}
		});
		
		appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.parseColor(selStatusColor));
		}
		tool_title.setTextColor(Color.parseColor(selTextColor1));
		mSwipeRefreshLayout.setColorScheme(R.color.text_gray);
		GradientDrawable bgShape = (GradientDrawable) add_layout.getBackground();
		bgShape.setColor(Color.parseColor(selToolColor));
		
	}
	
	private void getAllPublication(String acti) {
		if (acti.equals("{\"result\":null}")) {
			no_publication.setVisibility(View.VISIBLE);
			activitiesArray.clear();
		} else {
			try {
				no_publication.setVisibility(View.GONE);
				JSONObject jsonRootObject = new JSONObject(acti);
				JSONArray jsonArray = jsonRootObject.getJSONArray("result");
				activitiesArray.clear();
				for (int i = 0; i < jsonArray.length(); i++) {
					dbact = new DataBeanActivities();
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					activiId = jsonObject.getString("tbl_school_publication_id");
					activiTitle = jsonObject.getString("tbl_school_publication_title");
					activiDesc = jsonObject.getString("tbl_school_publication_desc");
					activiDate = jsonObject.getString("tbl_school_publication_date");
					activiImg = jsonObject.getString("tbl_school_publication_img");
					dbact.setActiviId(activiId);
					dbact.setActiviTitle(activiTitle);
					dbact.setActiviDesc(activiDesc);
					dbact.setActiviDate(activiDate);
					dbact.setActiviImg(activiImg);
					activitiesArray.add(dbact);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (activitiesArray.size() != 0) {
				listItms();
			}
		}
	}

	@SuppressLint("StaticFieldLeak")
	public class GetAllPublication extends AsyncTask<String, String, String> {
		Context ctx;
		
		GetAllPublication(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			mSwipeRefreshLayout.setRefreshing(true);
			publication_list.setEnabled(false);
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			String schoolId = params[0];
			String userSection = params[1];
			String userrType = params[2];
			skip = params[3];
			String data;
			
			try {
				URL url = new URL(ALL_PUBLICATION);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				
				OutputStream outputStream = httpURLConnection.getOutputStream();
				
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
				data = URLEncoder.encode("schoolId", "UTF-8") + "=" + URLEncoder.encode(schoolId, "UTF-8") + "&" +
						URLEncoder.encode("userSection", "UTF-8") + "=" + URLEncoder.encode(userSection, "UTF-8") + "&" +
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
			Log.e("ACHIVE", "" + result);
			//	loader.setVisibility(View.GONE);
			try {
				mSwipeRefreshLayout.setRefreshing(false);
				publication_list.setEnabled(true);
				sp_acti = getSharedPreferences(PREFS_ACTI, 0);
				se_acti = sp_acti.edit();
				se_acti.clear();
				se_acti.putString("json_acti", result);
				se_acti.commit();
				
				if (result.equals("{\"result\":null}")) {
					if (activitiesArray.size() > 0) {
						no_publication.setVisibility(View.GONE);
						publication_list.removeFooterView(footerView);
					} else {
						no_publication.setVisibility(View.VISIBLE);
						publication_list.setVisibility(View.GONE);
					}
				} else {
					try {
						activitiesArray.clear();
						no_publication.setVisibility(View.GONE);
						publication_list.setVisibility(View.VISIBLE);
						JSONObject jsonRootObject = new JSONObject(result);
						JSONArray jsonArray = jsonRootObject.getJSONArray("result");
						// activitiesArray.clear();
						for (int i = 0; i < jsonArray.length(); i++) {
							dbact = new DataBeanActivities();
							JSONObject jsonObject = jsonArray.getJSONObject(i);
							activiId = jsonObject.getString("tbl_school_publication_id");
							activiTitle = jsonObject.getString("tbl_school_publication_title");
							activiDesc = jsonObject.getString("tbl_school_publication_desc");
							activiDate = jsonObject.getString("tbl_school_publication_date");
							activiImg = jsonObject.getString("tbl_school_publication_img");
							dbact.setActiviId(activiId);
							dbact.setActiviTitle(activiTitle);
							dbact.setActiviDesc(activiDesc);
							dbact.setActiviDate(activiDate);
							dbact.setActiviImg(activiImg);
							activitiesArray.add(dbact);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					if (activitiesArray.size() != 0) {
						listItms();
					}
				}
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
			}
			super.onPostExecute(result);
		}
		
	}
	
	private void listItms() {
		activitiesListAdaptor = new ActivitiesListAdaptor(PublicationActivity.this, R.layout.achivement_list_item1, activitiesArray, "publication");
		publication_list.setAdapter(activitiesListAdaptor);

		publication_list.setOnItemClickListener((adapterView, view, i, l) -> {
			String achId = activitiesArray.get(i).getActiviId();
			achImg = activitiesArray.get(i).getActiviImg();
			String achCate = "publication";
			String achTitle = activitiesArray.get(i).getActiviTitle();
			String achDesc = activitiesArray.get(i).getActiviDesc();
			String acgDate = activitiesArray.get(i).getActiviDate();
			Intent i11 = new Intent(PublicationActivity.this, AchievmentDetailsActivity.class);
			i11.putExtra("achId", achId);
			i11.putExtra("achImg", achImg);
			i11.putExtra("achCate", achCate);
			i11.putExtra("achTitle", achTitle);
			i11.putExtra("achDesc", achDesc);
			i11.putExtra("acgDate", acgDate);
			startActivity(i11);
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		});

		SharedPreferences sp = getSharedPreferences(PREFS_RW, 0);
		String json = sp.getString("result", "");

		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONArray jsonArray = jsonObject.getJSONArray("result");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject object = jsonArray.getJSONObject(i);
				if (object.getString("tbl_insti_buzz_cate_name").equals("Publications")) {
					if (object.getString("tbl_scl_inst_buzz_detl_write_status").equals("Active")) {
						publication_list.setOnItemLongClickListener((arg0, arg1, pos, id) -> {
							if (isInternetOn()) {
								Intent intent = new Intent(this, AddNewPublication.class);
								intent.putExtra("type", "update");
								intent.putExtra("title", activitiesArray.get(pos).getActiviTitle());
								intent.putExtra("description", activitiesArray.get(pos).getActiviDesc());
								intent.putExtra("date", activitiesArray.get(pos).getActiviDate());
								intent.putExtra("image", activitiesArray.get(pos).getActiviImg());
								intent.putExtra("id", activitiesArray.get(pos).getActiviId());
								startActivity(intent);
							} else {
								Toast.makeText(PublicationActivity.this, "No network Please connect with network for update", Toast.LENGTH_SHORT).show();
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
		ConnectivityManager connec =
				(ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
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
