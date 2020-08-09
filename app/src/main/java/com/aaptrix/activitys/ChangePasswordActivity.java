package com.aaptrix.activitys;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.appbar.AppBarLayout;

import com.aaptrix.activitys.admin.InstituteBuzzActivityDiff;
import com.aaptrix.activitys.student.InstituteBuzzActivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import com.aaptrix.R;

import static com.aaptrix.activitys.SplashScreen.SCHOOL_ID;
import static com.aaptrix.tools.HttpUrl.CHANGE_PASSWORD;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;
import static com.aaptrix.tools.SPClass.PREF_ROLE;

/**
 * Created by Administrator on 11/29/2017.
 */

public class ChangePasswordActivity extends AppCompatActivity {
	
	SharedPreferences.Editor editor;
	String userId, userPassword, userSchoolLogo, numberOfUser, userPhone, userrType;
	EditText oldPass, newPass, conPass;
	AppBarLayout appBarLayout;
	ImageView school_logo;
	SharedPreferences.Editor editorColor;
	String selToolColor, selDrawerColor, selStatusColor, selTextColor1, selTextColor2;
	TextView tool_title;
	TextView tc, pas;
	MediaPlayer mp;
	TextView cube1, cube2;
	TextView view1, troublePass;
	String newP;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_password_main_layout);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		appBarLayout = findViewById(R.id.appBarLayout);
		tool_title = findViewById(R.id.tool_title);
		tc = findViewById(R.id.tc);
		pas = findViewById(R.id.pas);
		oldPass = findViewById(R.id.et_old_password1);
		newPass = findViewById(R.id.et_new_password2);
		conPass = findViewById(R.id.et_con_password3);
		mp = MediaPlayer.create(this, R.raw.button_click);
		troublePass = findViewById(R.id.trouble_chng_pss);

		troublePass.setOnClickListener(v -> {
			Intent intent = new Intent(this, TroubleFAQ.class);
			startActivity(intent);
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		});

		oldPass.setOnFocusChangeListener((v, hasFocus) -> {
			if (!hasFocus)
				hideKeyboard(v);
		});

		newPass.setOnFocusChangeListener((v, hasFocus) -> {
			if (!hasFocus)
				hideKeyboard(v);
		});

		conPass.setOnFocusChangeListener((v, hasFocus) -> {
			if (!hasFocus)
				hideKeyboard(v);
		});

		school_logo = findViewById(R.id.school_logo);
		Button change1 = findViewById(R.id.btnChange);
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		editor = settings.edit();
		userId = settings.getString("userID", "");
		if (getSharedPreferences(PREF_ROLE, 0).getString("userRole", " ").equals("Parent")) {
			userPassword = settings.getString("parentPassword", "");
		} else {
			userPassword = settings.getString("password", "");
		}
		userSchoolLogo = settings.getString("userSchoolLogo", "");
		numberOfUser = settings.getString("numberOfUser", "");
		userPhone = settings.getString("userPhone", "");
		userrType = settings.getString("userrType", "");
		cube1 = findViewById(R.id.cube1);
		cube2 = findViewById(R.id.cube2);
		view1 = findViewById(R.id.view1);
		
		SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
		editorColor = settingsColor.edit();
		selToolColor = settingsColor.getString("tool", "");
		selDrawerColor = settingsColor.getString("drawer", "");
		selStatusColor = settingsColor.getString("status", "");
		selTextColor1 = settingsColor.getString("text1", "");
		selTextColor2 = settingsColor.getString("text2", "");
		Picasso.with(this).load(R.drawable.large_logo).into(school_logo);
		
		change1.setOnClickListener(view -> {
			mp.start();
			try {
				String oldMd5 = oldPass.getText().toString().trim();
				String old = md5(oldMd5);
				newP = newPass.getText().toString().trim();
				String conP = conPass.getText().toString().trim();
				if (TextUtils.isEmpty(oldMd5)) {
					Toast.makeText(ChangePasswordActivity.this, "Please enter old password", Toast.LENGTH_SHORT).show();
				} else {
					if (TextUtils.isEmpty(newP)) {
						Toast.makeText(ChangePasswordActivity.this, "Please enter new password", Toast.LENGTH_SHORT).show();
					} else {
						if (TextUtils.isEmpty(conP)) {
							Toast.makeText(ChangePasswordActivity.this, "Please enter confirm password", Toast.LENGTH_SHORT).show();
							
						} else if (!newP.equals(conP)) {
							Toast.makeText(ChangePasswordActivity.this, "Enter Password is mismatch", Toast.LENGTH_SHORT).show();
							
						} else if (newP.length() < 6) {
							Toast.makeText(ChangePasswordActivity.this, "Password must have 6 characters", Toast.LENGTH_SHORT).show();
						}
						else {
							if (old.equals(userPassword)) {

								UpdateProfilePassword updateProfilePassword = new UpdateProfilePassword(ChangePasswordActivity.this);
								updateProfilePassword.execute(userPhone, newP);
							} else {
								Toast.makeText(ChangePasswordActivity.this, "Please enter valid old password", Toast.LENGTH_SHORT).show();
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
		GradientDrawable bgShape = (GradientDrawable) change1.getBackground();
		bgShape.setColor(Color.parseColor(selToolColor));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.parseColor(selStatusColor));
		}
		tool_title.setTextColor(Color.parseColor(selTextColor1));
		change1.setTextColor(Color.parseColor(selTextColor1));
		tc.setTextColor(Color.parseColor(selToolColor));
		pas.setTextColor(Color.parseColor(selToolColor));
		GradientDrawable drawable = (GradientDrawable) cube1.getBackground();
		drawable.setStroke(2, Color.parseColor(selToolColor));
		GradientDrawable drawable1 = (GradientDrawable) cube2.getBackground();
		drawable1.setStroke(2, Color.parseColor(selToolColor));
		view1.setBackgroundColor(Color.parseColor(selToolColor));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mp.release();
	}
	
	public static String md5(final String s) {
		final String MD5 = "MD5";
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest
					.getInstance(MD5);
			digest.update(s.getBytes());
			byte[] messageDigest = digest.digest();
			
			StringBuilder hexString = new StringBuilder();
			for (byte aMessageDigest : messageDigest) {
				StringBuilder h = new StringBuilder(Integer.toHexString(0xFF & aMessageDigest));
				while (h.length() < 2)
					h.insert(0, "0");
				hexString.append(h);
			}
			return hexString.toString();
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	private void hideKeyboard(View view) {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		assert inputMethodManager != null;
		inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
	
	@SuppressLint("StaticFieldLeak")
	public class UpdateProfilePassword extends AsyncTask<String, String, String> {
		Context ctx;
		
		UpdateProfilePassword(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			Toast.makeText(ctx, "Please wait we are updating your profile password", Toast.LENGTH_SHORT).show();
			super.onPreExecute();
			
		}
		
		@Override
		protected String doInBackground(String... params) {
			
			String userId = params[0];
			String newP = params[1];
			@SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
			String data;
			
			try {
				
				URL url = new URL(CHANGE_PASSWORD);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				
				OutputStream outputStream = httpURLConnection.getOutputStream();
				
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
				data = URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8") + "&" +
						URLEncoder.encode("newPass", "UTF-8") + "=" + URLEncoder.encode(newP, "UTF-8") + "&" +
						URLEncoder.encode("user_type", "UTF-8") + "=" + URLEncoder.encode(getSharedPreferences(PREF_ROLE, 0).getString("userRole", ""), "UTF-8") + "&" +
						URLEncoder.encode("device_id", "UTF-8") + "=" + URLEncoder.encode(deviceId, "UTF-8") + "&" +
						URLEncoder.encode("tbl_school_id", "UTF-8") + "=" + URLEncoder.encode(SCHOOL_ID, "UTF-8");
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
			Log.d("PASSWORD", "" + result);
			if (!result.equals("Error")) {
				validLogin();
				
			} else {
				Toast.makeText(ctx, "Some issues", Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}
		
	}
	
	private void validLogin() {
		Toast.makeText(ChangePasswordActivity.this, "Password Updated", Toast.LENGTH_SHORT).show();
		String neww = md5(newP);
		
		Intent i = new Intent(ChangePasswordActivity.this, UserProfile.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("password", neww);
		editor.commit();
		finish();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
            /*Intent i = new Intent(ChangePasswordActivity.this, InstituteBuzzActivity.class);
            startActivity(i);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);*/
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent i;
		if (userrType.equals("Student")) {
			i = new Intent(ChangePasswordActivity.this, InstituteBuzzActivity.class);
		} else {
			i = new Intent(ChangePasswordActivity.this, InstituteBuzzActivityDiff.class);
		}
		startActivity(i);
		finish();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}
}
