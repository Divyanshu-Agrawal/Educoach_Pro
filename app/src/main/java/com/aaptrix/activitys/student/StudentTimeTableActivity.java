package com.aaptrix.activitys.student;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory;
import cz.msebera.android.httpclient.conn.ssl.SSLContexts;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;
import de.hdodenhof.circleimageview.CircleImageView;
import com.aaptrix.R;
import com.aaptrix.TimeTableFragmetAdaptes;
import com.aaptrix.tools.FileUtil;

import javax.net.ssl.SSLContext;

import id.zelory.compressor.Compressor;

import static com.aaptrix.tools.HttpUrl.UPDATE_USER_PRO_IMAGE;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;
import static cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;

/**
 * Created by Administrator on 11/29/2017.
 */

public class StudentTimeTableActivity extends AppCompatActivity {
	
	SharedPreferences.Editor editor;
	String userId, userImg, userName, userSchoolLogo, str_section;
	AppBarLayout appBarLayout;
	SharedPreferences.Editor editorColor;
	String selToolColor, selDrawerColor, selStatusColor, selTextColor1, selTextColor2;
	TextView tool_title;
	CircleImageView prof_logo;
	TextView nameTV, tv_std_name, tvID, tvCLASS;
	TabLayout tab;
	ImageView iv_edit;
	Bitmap bitmap;
	String loc;
	String selBatch, userrType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.student_time_table_layout);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		appBarLayout = findViewById(R.id.appBarLayout);
		tool_title = findViewById(R.id.tool_title);
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		editor = settings.edit();
		userId = settings.getString("userID", "");
		userSchoolLogo = settings.getString("userSchoolLogo", "");
		userImg = settings.getString("userImg", "");
		userName = settings.getString("userName", "");
		str_section = settings.getString("userSection", "");
		userrType = settings.getString("userrType", "");
		loc = getIntent().getStringExtra("loc");

//color
		SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
		editorColor = settingsColor.edit();
		selToolColor = settingsColor.getString("tool", "");
		selDrawerColor = settingsColor.getString("drawer", "");
		selStatusColor = settingsColor.getString("status", "");
		selTextColor1 = settingsColor.getString("text1", "");
		selTextColor2 = settingsColor.getString("text2", "");
		
		prof_logo = findViewById(R.id.prof_logo);
		nameTV = findViewById(R.id.nameTV);
		tvID = findViewById(R.id.tvID);
		tvCLASS = findViewById(R.id.tvCLASS);
		tv_std_name = findViewById(R.id.tv_std_name);
		iv_edit = findViewById(R.id.iv_edit);
		
		nameTV.setText(userName);
		tv_std_name.setText(str_section);
		
		selBatch = getIntent().getStringExtra("selBatch");
		
		if (userImg.equals("0")) {
			prof_logo.setImageDrawable(getResources().getDrawable(R.drawable.user_place_hoder));
		} else if (!TextUtils.isEmpty(userImg)) {
			String url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "") + "/users/students/profile/" + userImg;
			Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(prof_logo);
		} else {
			prof_logo.setImageDrawable(getResources().getDrawable(R.drawable.user_place_hoder));
		}
		
		ViewPager viewPager = findViewById(R.id.viewpager);
		
		TimeTableFragmetAdaptes fa1 = new TimeTableFragmetAdaptes(StudentTimeTableActivity.this, getSupportFragmentManager(), loc);
		viewPager.setAdapter(fa1);
		if (loc.equals("sidebarexam")) {
			viewPager.setCurrentItem(1);
		}
		
		tab = findViewById(R.id.sliding_tabs);
		tab.setupWithViewPager(viewPager);
		
		iv_edit.setOnClickListener(view -> {
			if (isInternetOn()) {
				if (ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
					ActivityCompat.requestPermissions(StudentTimeTableActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
				}
				Intent gallery = new Intent();
				gallery.setAction(Intent.ACTION_GET_CONTENT);
				gallery.setType("image/*");
				startActivityForResult(gallery, 1);
			} else {
				Toast.makeText(StudentTimeTableActivity.this, "No network Please connect with network for update", Toast.LENGTH_SHORT).show();
			}
		});
		
		
		appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.parseColor(selStatusColor));
		}
		tool_title.setTextColor(Color.parseColor(selTextColor1));
		tab.setBackgroundColor(Color.parseColor(selToolColor));
		tab.setSelectedTabIndicatorColor(Color.parseColor(selToolColor));
		tab.setTabTextColors(Color.parseColor("#ffffff"), Color.parseColor(selToolColor));
		tvID.setTextColor(Color.parseColor(selToolColor));
		tvCLASS.setTextColor(Color.parseColor(selToolColor));
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
	
	
	public final boolean isInternetOn() {
		
		// get Connectivity Manager object to check connection
		ConnectivityManager connec = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		
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
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		Uri filePath;
		if (requestCode == 1 && resultCode == RESULT_OK) {
			filePath = data.getData();
			
			assert filePath != null;
			CropImage.activity(filePath)
					.setGuidelines(CropImageView.Guidelines.ON)/*
                  .setMinCropResultSize(100,100)
                  .setMaxCropResultSize(950,1250)*/
					.setAspectRatio(150, 150)
					.setGuidelines(CropImageView.Guidelines.ON)
					.setCropShape(CropImageView.CropShape.OVAL)
					.start(this);
		}
		
		if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
			CropImage.ActivityResult result = CropImage.getActivityResult(data);
			if (resultCode == RESULT_OK) {
				filePath = result.getUri();
				try {
					File actualImage = FileUtil.from(StudentTimeTableActivity.this, filePath);
					File compressedImage = new Compressor(StudentTimeTableActivity.this)
							.setMaxWidth(640)
							.setMaxHeight(480)
							.setQuality(75)
							.setCompressFormat(Bitmap.CompressFormat.WEBP)
							.setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
									Environment.DIRECTORY_PICTURES).getAbsolutePath())
							.compressToFile(actualImage);
					bitmap = MediaStore.Images.Media.getBitmap(StudentTimeTableActivity.this.getContentResolver(), Uri.fromFile(compressedImage));
					prof_logo.setImageBitmap(bitmap);
					
					UpdateProfileImage updateProfileImage = new UpdateProfileImage(StudentTimeTableActivity.this, compressedImage);
					updateProfileImage.execute(userId);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			} else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
				Exception error = result.getError();
				Toast.makeText(StudentTimeTableActivity.this, "" + error, Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	
	@SuppressLint("StaticFieldLeak")
	public class UpdateProfileImage extends AsyncTask<String, String, String> {
		Context ctx;
		File image;
		
		UpdateProfileImage(Context ctx, File image) {
			this.ctx = ctx;
			this.image = image;
		}
		
		@Override
		protected void onPreExecute() {
			Toast.makeText(ctx, "Please wait we are updating your profile", Toast.LENGTH_SHORT).show();
			super.onPreExecute();
			
		}
		
		@Override
		protected String doInBackground(String... params) {
			String userId = params[0];
			
			try {
				SSLContext sslContext = SSLContexts.custom().useTLS().build();
				SSLConnectionSocketFactory f = new SSLConnectionSocketFactory(
						sslContext,
						new String[]{"TLSv1.1", "TLSv1.2"},
						null,
						BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
				HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(f).build();
				HttpPost httppost = new HttpPost(UPDATE_USER_PRO_IMAGE);
				MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
				entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
				FileBody newImage = new FileBody(image);
				entityBuilder.addPart("image", newImage);
				entityBuilder.addTextBody("userId", userId);
				HttpEntity entity = entityBuilder.build();
				httppost.setEntity(entity);
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity httpEntity = response.getEntity();
				String result = EntityUtils.toString(httpEntity);
				Log.e("result", result);
				return result;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			Log.e("Json", "" + result);
			try {
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject.getString("success").equals("true")) {
					editor.putString("userImg", jsonObject.getString("imageNm"));
					editor.commit();
					Toast.makeText(StudentTimeTableActivity.this, "Your Image is Updated", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(StudentTimeTableActivity.this, "Not uploaded image is too large", Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			super.onPostExecute(result);
		}
		
	}
	
	
}
