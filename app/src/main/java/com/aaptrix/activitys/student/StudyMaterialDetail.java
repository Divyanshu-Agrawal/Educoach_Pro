package com.aaptrix.activitys.student;

import com.aaptrix.adaptor.StudyDetailAdaptor;
import com.aaptrix.R;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;

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

import static com.aaptrix.tools.HttpUrl.REMOVE_STUDY_MATERIAL;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREFS_RW;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class StudyMaterialDetail extends AppCompatActivity {
	
	AppBarLayout appBarLayout;
	String selToolColor, selStatusColor, selTextColor1, userrType, schoolId;
	TextView tool_title;
	ListView listView;
	TextView title, description;
	String strTitle, strDesc, strId, strPermission;
	long downloadID;
	String[] strUrl;
	ArrayList<String> url = new ArrayList<>();
	SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		setContentView(R.layout.activity_study_material_detail);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		appBarLayout = findViewById(R.id.appBarLayout);
		tool_title = findViewById(R.id.tool_title);
		listView = findViewById(R.id.file_list);
		title = findViewById(R.id.study_title);
		description = findViewById(R.id.study_description);
		strTitle = getIntent().getStringExtra("title");
		strDesc = getIntent().getStringExtra("description");
		strId = getIntent().getStringExtra("id");
		strUrl = getIntent().getStringArrayExtra("url");
		strPermission = getIntent().getStringExtra("permission");
		title.setText(strTitle);

		if (!strDesc.equals("null")) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				description.setText(Html.fromHtml(strDesc, Html.FROM_HTML_MODE_COMPACT));
			} else {
				description.setText(Html.fromHtml(strDesc));
			}
		} else {
			description.setVisibility(View.GONE);
		}

		sp = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		registerReceiver(onDownloadComplete,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		userrType = settings.getString("userrType", "");
		schoolId = settings.getString("str_school_id", "");
		
		SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
		selToolColor = settingsColor.getString("tool", "");
		selStatusColor = settingsColor.getString("status", "");
		selTextColor1 = settingsColor.getString("text1", "");
		appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.parseColor(selStatusColor));
		}
		tool_title.setTextColor(Color.parseColor(selTextColor1));
		tool_title.setText(strTitle);
		
		for (String aStrUrl : strUrl) {
			url.add(aStrUrl.replace("[", "").replace("]", "").replace("\"", ""));
		}
		
		StudyDetailAdaptor adaptor = new StudyDetailAdaptor(this, R.layout.study_detail_list, url, strPermission);
		listView.setAdapter(adaptor);
		adaptor.notifyDataSetChanged();
		
		listView.setOnItemClickListener((parent, view, position, id) -> {
			if (strPermission.equals("1")) {
				if (isInternetOn()) {
					if (PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
						downloadFile(url.get(position));
					} else {
						isPermissionGranted();
					}
				} else {
					Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(this, "You don't have permission to download this file", Toast.LENGTH_SHORT).show();
			}
		});
		
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
	
	public void isPermissionGranted() {
		ActivityCompat.requestPermissions(this,
				new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
	}
	
	
	@Override
	public void onRequestPermissionsResult(int requestCode,
										   @NonNull String permissions[], @NonNull int[] grantResults) {
		if (requestCode == 1) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
			if (downloadID == id) {
				Toast.makeText(StudyMaterialDetail.this, "Download Completed", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	private void downloadFile(String url) {
		String path = Environment.DIRECTORY_DOWNLOADS;
		String downloadUrl = sp.getString("imageUrl", "") + sp.getString("userSchoolId", "") + "/studyMaterial/" + url;
		DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl))
				.setTitle(url)
				.setDescription("Downloading")
				.setMimeType("application/octet-stream")
				.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
				.setDestinationInExternalPublicDir(path, url);
		request.allowScanningByMediaScanner();
		DownloadManager downloadManager= (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
		assert downloadManager != null;
		downloadID = downloadManager.enqueue(request);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(onDownloadComplete);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		} else if (item.getItemId() == R.id.delete) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.DialogTheme);
			alert.setTitle("Are you sure you want to delete").setPositiveButton("Yes", (dialog, which) -> {
				RemoveStudyMaterial removeStudyMaterial = new RemoveStudyMaterial(this);
				removeStudyMaterial.execute(schoolId, strId);
			}).setNegativeButton("No", null);
			AlertDialog alertDialog = alert.create();
			alertDialog.setCancelable(false);
			alertDialog.show();
			Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
			Button theButton1 = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
			theButton.setTextColor(Color.parseColor(selToolColor));
			theButton1.setTextColor(Color.parseColor(selToolColor));
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (userrType.equals("Admin")) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.delete_menu, menu);
			return true;
		} else if (userrType.equals("Teacher")) {
			SharedPreferences sp = getSharedPreferences(PREFS_RW, 0);
			if (sp.getString("Study Materials", "").equals("Active")) {
				MenuInflater inflater = getMenuInflater();
				inflater.inflate(R.menu.delete_menu, menu);
			}
			return true;
		}
		else {
			return true;
		}
	}
	
	@SuppressLint("StaticFieldLeak")
	public class RemoveStudyMaterial extends AsyncTask<String, String, String> {
		Context ctx;
		
		RemoveStudyMaterial(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			String schoolId=params[0];
			String materialID=params[1];
			String data;
			
			try {
				URL url = new URL(REMOVE_STUDY_MATERIAL);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				
				OutputStream outputStream = httpURLConnection.getOutputStream();
				
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
				data = URLEncoder.encode("schoolId", "UTF-8") + "=" + URLEncoder.encode(schoolId, "UTF-8")+ "&" +
						URLEncoder.encode("studyMaterialId", "UTF-8") + "=" + URLEncoder.encode(materialID, "UTF-8");
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
			Log.e("result", result);
			if (!result.isEmpty()) {
				finish();
			} else {
				Toast.makeText(ctx, "Some Error", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
