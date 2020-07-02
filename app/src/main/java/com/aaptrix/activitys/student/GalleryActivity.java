package com.aaptrix.activitys.student;

import com.aaptrix.activitys.admin.AddNewGallery;
import com.aaptrix.adaptor.GalleryAdapter;
import com.aaptrix.databeans.GalleryData;
import com.aaptrix.R;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;

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

import static com.aaptrix.tools.HttpUrl.ALL_GALLERY;
import static com.aaptrix.tools.HttpUrl.REMOVE_GALLERY;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREFS_RW;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class GalleryActivity extends AppCompatActivity {
	
	AppBarLayout appBarLayout;
	String selToolColor, selStatusColor, selTextColor1;
	TextView tool_title;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	TextView no_gallery;
	ListView listView;
	LinearLayout add_layout;
	ImageView addGallery;
	String userId, userSchoolId, userRoleId, userrType;
	View footerView;
	String skip;
	ArrayList<GalleryData> galleryArray = new ArrayList<>();
	GalleryData galleryData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		appBarLayout = findViewById(R.id.appBarLayout);
		tool_title = findViewById(R.id.tool_title);
		no_gallery = findViewById(R.id.no_gallery);
		listView = findViewById(R.id.gallery_list);
		mSwipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
		add_layout = findViewById(R.id.add_layout);
		addGallery = findViewById(R.id.add_gallery);
		mSwipeRefreshLayout.setRefreshing(false);
		listView.setEnabled(true);
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		userId = settings.getString("userID", "");
		userSchoolId = settings.getString("str_school_id", "");
		userRoleId = settings.getString("str_role_id", "");
		userrType = settings.getString("userrType", "");

		SharedPreferences sp = getSharedPreferences(PREFS_RW, 0);
		String json = sp.getString("result", "");

		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONArray jsonArray = jsonObject.getJSONArray("result");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject object = jsonArray.getJSONObject(i);
				if (object.getString("tbl_insti_buzz_cate_name").equals("Gallery")) {
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

		addGallery.setOnClickListener(view -> {
			if (isInternetOn()) {
				Intent i = new Intent(GalleryActivity.this, AddNewGallery.class);
				startActivity(i);
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			} else {
				Toast.makeText(this, "No network Please connect with network", Toast.LENGTH_SHORT).show();
			}
		});
		
		if (isInternetOn()) {
			GetGallery getGallery = new GetGallery(this);
			getGallery.execute(userSchoolId, "0", userrType);
		} else {
			Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
		}
		
		mSwipeRefreshLayout.setOnRefreshListener(() -> {
			if (isInternetOn()) {
				listView.setEnabled(false);
				galleryArray.clear();
				GetGallery getGallery = new GetGallery(this);
				getGallery.execute(userSchoolId, "0", userrType);
			} else {
				Toast.makeText(this, "No network Please connect with network for update", Toast.LENGTH_SHORT).show();
				mSwipeRefreshLayout.setRefreshing(false);
				listView.setEnabled(true);
			}
		});

		SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
		selToolColor = settingsColor.getString("tool", "");
		selStatusColor = settingsColor.getString("status", "");
		selTextColor1 = settingsColor.getString("text1", "");
//		addHeaderFooterView();
		
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

	
	@SuppressLint("StaticFieldLeak")
	public class GetGallery extends AsyncTask<String, String, String> {
		Context ctx;
		
		GetGallery(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			mSwipeRefreshLayout.setRefreshing(true);
			listView.setEnabled(false);
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			String schoolId = params[0];
			skip = params[1];
			String usertype = params[2];
			String data;
			
			try {
				URL url = new URL(ALL_GALLERY);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				
				OutputStream outputStream = httpURLConnection.getOutputStream();
				
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
				data = URLEncoder.encode("schoolId", "UTF-8") + "=" + URLEncoder.encode(schoolId, "UTF-8") + "&" +
						URLEncoder.encode("userType", "UTF-8") + "=" + URLEncoder.encode(usertype, "UTF-8");
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
			Log.e("result", String.valueOf(result));
			try {
				mSwipeRefreshLayout.setRefreshing(false);
				if (result.equals("{\"result\":null}")) {
					if (galleryArray.size() > 0) {
						no_gallery.setVisibility(View.GONE);
						listView.removeFooterView(footerView);
					} else {
						no_gallery.setVisibility(View.VISIBLE);
						listView.setVisibility(View.GONE);
					}
				} else {
					try {
						galleryArray.clear();
						no_gallery.setVisibility(View.GONE);
						listView.setVisibility(View.VISIBLE);
						JSONObject jsonRootObject = new JSONObject(result);
						JSONArray jsonArray = jsonRootObject.getJSONArray("result");
						for (int i = 0; i < jsonArray.length(); i++) {
							galleryData = new GalleryData();
							JSONObject jObject = jsonArray.getJSONObject(i);
							galleryData.setTitle(jObject.getString("tbl_school_gallery_title"));
							galleryData.setId(jObject.getString("tbl_school_gallery_id"));
							galleryData.setImages(jObject.getString("tbl_school_gallery_img").split(","));
							galleryArray.add(galleryData);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					listItms(skip);
				}
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
			}
			super.onPostExecute(result);
		}
		
	}
	
	private void listItms(String skip) {
		listView.setEnabled(true);
		GalleryAdapter galleryAdapter = new GalleryAdapter(this, R.layout.gallery_list_item, galleryArray);
		listView.setAdapter(galleryAdapter);
		listView.setSelectionFromTop(Integer.parseInt(skip), 0);
		listView.removeFooterView(footerView);
//		addHeaderFooterView();
		listView.setOnItemClickListener((parent, view, position, id) -> {
			Log.e("pos", String.valueOf(position));
			Intent intent = new Intent(this, GalleryDetail.class);
			intent.putExtra("title", galleryArray.get(position).getTitle());
			intent.putExtra("images", galleryArray.get(position).getImages());
			intent.putExtra("id", galleryArray.get(position).getId());
			startActivity(intent);
		});
		
		if (galleryArray.size() < 9) {
			listView.removeFooterView(footerView);
		}
		
		if (userrType.equals("Admin")) {
			listView.setOnItemLongClickListener((arg0, arg1, pos, id) -> {
				if (isInternetOn()) {
					AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.DialogTheme);
					alert.setTitle("Are you sure you want to delete").setPositiveButton("Yes", (dialog, which) -> {
						RemoveGallery removeGallery = new RemoveGallery(this);
						removeGallery.execute(galleryArray.get(pos).getId(), userSchoolId);
					}).setNegativeButton("No", null);
					AlertDialog alertDialog = alert.create();
					alertDialog.setCancelable(false);
					alertDialog.show();
					Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
					Button theButton1 = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
					theButton.setTextColor(Color.parseColor(selToolColor));
					theButton1.setTextColor(Color.parseColor(selToolColor));
				} else {
					Toast.makeText(this, "No network Please connect with network for update", Toast.LENGTH_SHORT).show();
				}
				return true;
			});
		} else if (userrType.equals("Teacher")) {
			SharedPreferences sp = getSharedPreferences(PREFS_RW, 0);
			if (sp.getString("Gallery", "").equals("Active")) {
				listView.setOnItemLongClickListener((arg0, arg1, pos, id) -> {
					if (isInternetOn()) {
						AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.DialogTheme);
						alert.setTitle("Are you sure you want to delete").setPositiveButton("Yes", (dialog, which) -> {
							RemoveGallery removeGallery = new RemoveGallery(this);
							removeGallery.execute(galleryArray.get(pos).getId(), userSchoolId);
						}).setNegativeButton("No", null);
						AlertDialog alertDialog = alert.create();
						alertDialog.setCancelable(false);
						alertDialog.show();
						Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
						Button theButton1 = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
						theButton.setTextColor(Color.parseColor(selToolColor));
						theButton1.setTextColor(Color.parseColor(selToolColor));
					} else {
						Toast.makeText(this, "No network Please connect with network for update", Toast.LENGTH_SHORT).show();
					}
					return true;
				});
			}
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
	
	@SuppressLint("StaticFieldLeak")
	public class RemoveGallery extends AsyncTask<String, String, String> {
		Context ctx;
		
		RemoveGallery(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			mSwipeRefreshLayout.setRefreshing(true);
			listView.setEnabled(false);
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			String gallery_id = params[0];
			String schoolId = params[1];
			String data;
			
			try {
				URL url = new URL(REMOVE_GALLERY);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				
				OutputStream outputStream = httpURLConnection.getOutputStream();
				
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
				data = URLEncoder.encode("galleryId", "UTF-8") + "=" + URLEncoder.encode(gallery_id, "UTF-8") + "&" +
						URLEncoder.encode("schoolId", "UTF-8") + "=" + URLEncoder.encode(schoolId, "UTF-8");
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
			super.onPostExecute(result);
			if (result.contains("success") || result.contains("true")) {
				startActivity(new Intent(ctx, GalleryActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				finish();
			} else {
				Toast.makeText(ctx, "Error in deleting images", Toast.LENGTH_SHORT).show();
			}
		}
		
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
