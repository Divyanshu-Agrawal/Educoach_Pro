package com.aaptrix.activitys.student;

import com.aaptrix.adaptor.StudyDetailAdaptor;
import com.aaptrix.R;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.leanback.widget.HorizontalGridView;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
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
import android.os.PowerManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aaptrix.fragments.GetMoreVideos;
import com.google.android.material.appbar.AppBarLayout;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

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
	String strTitle, strDesc, strId, strPermission, strTags, strSubject;
	String[] strUrl;
	ArrayList<String> url = new ArrayList<>();
	SharedPreferences sp;
	HorizontalGridView gridView;
	ProgressDialog mProgressDialog;
	
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
		strTags = getIntent().getStringExtra("tags");
		strSubject = getIntent().getStringExtra("subject");
		title.setText(strTitle);
		gridView = findViewById(R.id.tags);

		if (strTags.equals("null") || strTags.equals("")) {
			gridView.setVisibility(View.GONE);
		} else {
			String[] tag = strTags.replace("[", "").replace("]", "").trim().split(",");
			TagsAdapter adapter = new TagsAdapter(this, tag);
			gridView.setAdapter(adapter);
			adapter.notifyDataSetChanged();
		}

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

		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("Downloading...");
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setCancelable(false);
		
		for (String aStrUrl : strUrl) {
			url.add(aStrUrl.replace("[", "").replace("]", "").replace("\"", ""));
		}
		
		StudyDetailAdaptor adaptor = new StudyDetailAdaptor(this, R.layout.study_detail_list, url, strPermission, strSubject);
		listView.setAdapter(adaptor);
		adaptor.notifyDataSetChanged();
		
		listView.setOnItemClickListener((parent, view, position, id) -> {
			if (strPermission.equals("1")) {
				if (isInternetOn()) {
					if (PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
						DownloadTask downloadTask = new DownloadTask(this);
						downloadTask.execute(url.get(position));
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

	class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.ViewHolder>{

		private Context context;
		private String[] tags;

		public TagsAdapter(Context context, String[] tags){
			this.context = context;
			this.tags = tags;
		}

		public class ViewHolder extends RecyclerView.ViewHolder {

			TextView tagsName;

			public ViewHolder(View view) {
				super(view);
				tagsName = view.findViewById(R.id.tag_name);
			}
		}

		@NotNull
		@Override
		public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
			final View view = LayoutInflater.from(this.context).inflate(R.layout.tags_list, parent, false);
			return new ViewHolder(view);
		}

		@Override
		public void onBindViewHolder(@NotNull ViewHolder holder, final int position) {
			if (!tags[position].isEmpty()) {
				holder.tagsName.setText(tags[position].trim());
				holder.tagsName.setOnClickListener(v -> {
					Intent intent = new Intent(context, MaterialByTag.class);
					intent.putExtra("subject", strSubject);
					intent.putExtra("tag", tags[position]);
					context.startActivity(intent);
				});
			} else {
				holder.itemView.setVisibility(View.GONE);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getItemCount() {
			return tags.length;
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
	
	public void isPermissionGranted() {
		ActivityCompat.requestPermissions(this,
				new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
	}
	
	
	@Override
	public void onRequestPermissionsResult(int requestCode,
										   @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == 1) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@SuppressLint("StaticFieldLeak")
	private class DownloadTask extends AsyncTask<String, Integer, String> {

		private Context context;
		private PowerManager.WakeLock mWakeLock;

		public DownloadTask(Context context) {
			this.context = context;
		}

		@Override
		protected String doInBackground(String... sUrl) {

			String downloadUrl = sUrl[0];
			String[] splitUrl = downloadUrl.split("/");
			String name = splitUrl[splitUrl.length - 1];

			downloadUrl = sp.getString("imageUrl", "") + sp.getString("userSchoolId", "") + "/studyMaterial/" + downloadUrl;

			InputStream input = null;
			OutputStream output = null;
			HttpURLConnection connection = null;
			try {
				String ext = name.substring(name.lastIndexOf(".") + 1);
				File outputFile = File.createTempFile(name.replace(ext, "").replace(".", ""), "." + ext, getCacheDir());
				URL url = new URL(downloadUrl);
				connection = (HttpURLConnection) url.openConnection();
				connection.connect();

				if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
					return "Server returned HTTP " + connection.getResponseCode()
							+ " " + connection.getResponseMessage();
				}

				int fileLength = connection.getContentLength();

				// download the file
				input = connection.getInputStream();
				output = new FileOutputStream(outputFile);

				byte[] data = new byte[4096];
				long total = 0;
				int count;
				while ((count = input.read(data)) != -1) {
					if (isCancelled()) {
						input.close();
						return null;
					}
					total += count;
					if (fileLength > 0)
						publishProgress((int) (total * 100 / fileLength));
					output.write(data, 0, count);
				}
				fileEncrypt(outputFile.getName(), name);
			} catch (Exception e) {
				return e.toString();
			} finally {
				try {
					if (output != null)
						output.close();
					if (input != null)
						input.close();
				} catch (IOException ignored) {

				}

				if (connection != null)
					connection.disconnect();
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
					getClass().getName());
			mWakeLock.acquire();
			mProgressDialog.show();
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate(progress);
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.setMax(100);
			mProgressDialog.setProgress(progress[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			mWakeLock.release();
			mProgressDialog.dismiss();
			if (result != null) {
				Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
				Log.e("error", result);
			}
			else
				Toast.makeText(context,"File downloaded", Toast.LENGTH_SHORT).show();
		}

		private void fileEncrypt(String fileName, String outputName) throws Exception {
			String key = context.getSharedPreferences(PREFS_NAME, 0).getString("video_key", "aaptrixtechnopvt");

			File file = new File(context.getCacheDir(), fileName);
			int size = (int) file.length();
			byte[] bytes = new byte[size];

			BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
			buf.read(bytes, 0, bytes.length);
			buf.close();

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			byte[] bKey = key.getBytes(StandardCharsets.UTF_8);
			SecretKeySpec keySpec = new SecretKeySpec(bKey, "AES");
			IvParameterSpec ivSpec = new IvParameterSpec(bKey);
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
			byte[] decrypted = cipher.doFinal(bytes);
			File outputFile = new File(context.getExternalFilesDir("Study Material/" + strSubject), outputName);

			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile));
			bos.write(decrypted);
			bos.flush();
			bos.close();
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
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
				Intent i = new Intent(ctx, StudyMaterial.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).
						putExtra("sub", "All Subjects");
				startActivity(i);
				finish();
			} else {
				Toast.makeText(ctx, "Some Error", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
