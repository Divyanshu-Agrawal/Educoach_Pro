package com.aaptrix.activitys;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aaptrix.BuildConfig;
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
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import com.aaptrix.R;

import static com.aaptrix.activitys.SplashScreen.SCHOOL_NAME;
import static com.aaptrix.activitys.SplashScreen.SENDER_ID;
import static com.aaptrix.tools.HttpUrl.FORGOT_PASSWORD;
import static com.aaptrix.tools.SPClass.PREF_ROLE;

/**
 * Created by Administrator on 11/29/2017.
 */

public class ForgotPasswordActivity extends AppCompatActivity {
	
	ImageView school_logo;
	RelativeLayout mainLayoutRegister;
	ProgressBar loader;
	String str_number, uid;
	String verification_code;
	TextView version;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgot_password_layout_new);
		mainLayoutRegister = findViewById(R.id.mainLayoutLogin);
		setupUI(mainLayoutRegister);
		version = findViewById(R.id.version);
		version.setText("Version " + BuildConfig.VERSION_NAME);

		school_logo = findViewById(R.id.school_logo);
		loader = findViewById(R.id.loader);
		loader.setVisibility(View.VISIBLE);
		str_number = getIntent().getStringExtra("number");
		Picasso.with(this).load(R.drawable.large_logo).into(school_logo);
		checkNumberExits(str_number);
	}
	
	private void checkNumberExits(String str_number) {
		ForGotPassword forGotPassword = new ForGotPassword(ForgotPasswordActivity.this);
		forGotPassword.execute(str_number);
	}
	
	@SuppressLint("ClickableViewAccessibility")
	public void setupUI(View view) {
		if (!(view instanceof EditText)) {
			view.setOnTouchListener((v, event) -> {
				hideKeyboard(v);
				return false;
			});
		}
		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				View innerView = ((ViewGroup) view).getChildAt(i);
				setupUI(innerView);
			}
		}
	}

	private void hideKeyboard(View view) {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		assert inputMethodManager != null;
		inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
	
	@SuppressLint("StaticFieldLeak")
	public class ForGotPassword extends AsyncTask<String, String, String> {
		Context ctx;
		
		ForGotPassword(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			
			Toast.makeText(ctx, "Please wait we are checking your details", Toast.LENGTH_SHORT).show();
			super.onPreExecute();
			
		}
		
		@Override
		protected String doInBackground(String... params) {
			String phone = params[0];
			@SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
			String data;
			Log.d("phone", phone);
			try {
				URL url = new URL(FORGOT_PASSWORD);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				
				OutputStream outputStream = httpURLConnection.getOutputStream();
				
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
				data = URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8") + "&" +
						URLEncoder.encode("device_id", "UTF-8") + "=" + URLEncoder.encode(deviceId, "UTF-8") + "&" +
						URLEncoder.encode("user_type", "UTF-8") + "=" + URLEncoder.encode(getSharedPreferences(PREF_ROLE, 0).getString("userRole", ""), "UTF-8");
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
				Log.d("phone", response.toString());
				return response.toString();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			if (result != null && TextUtils.isDigitsOnly(result)) {
				uid = result;
				sendOtp(str_number);
			} else {
				Toast.makeText(ctx, "Your mobile number not match any account", Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}
	}

	public void sendOtp(String contact) {
		if (!TextUtils.isEmpty(contact) && (contact.length() == 10)) {
			int randomNum = getRandomNumberInRange();
			Toast.makeText(getApplicationContext(), "Code sent to the number", Toast.LENGTH_SHORT).show();
			smsBackground mGetImagesTask = new smsBackground(this);
			mGetImagesTask.execute(contact, randomNum + "");
		} else {
			Toast.makeText(this, "Please Enter valid Number", Toast.LENGTH_SHORT).show();
		}
	}

	private int getRandomNumberInRange() {
		Random r = new Random();
		return r.nextInt((999998 - 100001) + 1) + 100001;
	}

	@SuppressLint("StaticFieldLeak")
	private class smsBackground extends AsyncTask<String, Void, String> {
		Context ctx;
		String mobileNo;
		String senderId;
		String otp;

		smsBackground(Context ctx) {
			this.ctx = ctx;
		}

		@Override
		protected String doInBackground(String... params) {
			String mobile = params[0].replace("+91", "");
			String randomno = params[1];
			otp = randomno;
			mobileNo = mobile;
			sms_Send(mobile, Integer.parseInt(randomno));
			return null;
		}

		private void sms_Send(String mobileNo, int randomNum) {

			String authkey = getResources().getString(R.string.sms_Auth_key);
			senderId = SENDER_ID;
			String message = randomNum + " is your verification code for "+ SCHOOL_NAME +" | Smart App for Smart Coaching.";
			String route = "4";

			URLConnection myURLConnection;
			URL myURL;
			BufferedReader reader;
			String encoded_message = URLEncoder.encode(message);
			String mainUrl = "http://msg.sjainventures.com/api/sendhttp.php?";

			StringBuilder sbPostData = new StringBuilder(mainUrl);
			sbPostData.append("authkey=").append(authkey);
			sbPostData.append("&mobiles=").append(mobileNo);
			sbPostData.append("&message=").append(encoded_message);
			sbPostData.append("&route=").append(route);
			sbPostData.append("&sender=").append(senderId);

			try {
				mainUrl = sbPostData.toString();
				myURL = new URL(mainUrl);
				myURLConnection = myURL.openConnection();
				myURLConnection.connect();
				reader = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));

				String response;
				while ((response = reader.readLine()) != null)
					Log.e("RESPONSE", "" + response);
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void onPostExecute(String s) {
			super.onPostExecute(s);
			verification_code = otp;
			Intent intent = new Intent(ForgotPasswordActivity.this, OtpScreenActivity.class);
			intent.putExtra("OTPNumber", "" + verification_code);
			intent.putExtra("type", "forgot");
			intent.putExtra("uid", uid);
			intent.putExtra("contact", str_number);
			startActivity(intent);
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			Toast.makeText(getApplicationContext(), "Code sent to the number", Toast.LENGTH_SHORT).show();
			finish();
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}
}
