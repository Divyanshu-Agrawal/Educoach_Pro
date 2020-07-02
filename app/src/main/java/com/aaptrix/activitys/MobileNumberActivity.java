package com.aaptrix.activitys;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.Context;
import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aaptrix.BuildConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Random;

import com.aaptrix.R;

import static com.aaptrix.activitys.SplashScreen.SCHOOL_NAME;
import static com.aaptrix.activitys.SplashScreen.SENDER_ID;


public class MobileNumberActivity extends Activity {
	
	String uid;
	String contact;
	ImageView school_logo;
	ProgressBar loader_send_otp;
	TextView version;
	
	@SuppressLint("SetTextI18n")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mobile_number_layout);
		school_logo = findViewById(R.id.school_logo);
		loader_send_otp = findViewById(R.id.loader);
		uid = getIntent().getStringExtra("userID");
		contact = getIntent().getStringExtra("userPhone");

		version = findViewById(R.id.version);
		version.setText("Version " + BuildConfig.VERSION_NAME);

		loader_send_otp.setVisibility(View.VISIBLE);
		sendOtp(contact);
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
			String message = randomNum + " is your verification code for " + SCHOOL_NAME + " | Smart App for Smart Coaching.";
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
			Intent intent = new Intent(MobileNumberActivity.this, OtpScreenActivity.class);
			intent.putExtra("OTPNumber", "" + otp);
			intent.putExtra("uid", uid);
			intent.putExtra("contact", contact);
			intent.putExtra("type", "register_code");
			startActivity(intent);
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			finish();
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
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}
}
