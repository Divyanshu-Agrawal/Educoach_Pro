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
import android.os.CountDownTimer;

import androidx.annotation.Nullable;

import com.google.android.material.appbar.AppBarLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
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
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.aaptrix.R;

import static com.aaptrix.activitys.SplashScreen.SCHOOL_ID;
import static com.aaptrix.activitys.SplashScreen.SCHOOL_NAME;
import static com.aaptrix.activitys.SplashScreen.SENDER_ID;
import static com.aaptrix.tools.HttpUrl.UPDATE_PHONE_NUMBER;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;
import static com.aaptrix.tools.SPClass.PREF_ROLE;

/**
 * Created by Administrator on 11/14/2017.
 */

public class UpdateNumberOtpScreenActivity extends AppCompatActivity {
	
	String mob, otp, uid, str_school_logo;
	private EditText otpNumber;
	Button btn_submitOtp;
	TextView resend, tool_title, timer;
	SharedPreferences.Editor editorColor;
	String selToolColor, selDrawerColor, selStatusColor, selTextColor1, selTextColor2;
	AppBarLayout appBarLayout;
	ImageView school_logo;
	private SharedPreferences settings;
	SharedPreferences.Editor editor;
	FirebaseAuth auth;
	MediaPlayer mp;
	PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
	TextView troubleOtp;
	CountDownTimer countDownTimer;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_number_otp_screen_layout);
		
		Toolbar toolbar = findViewById(R.id.toolbar);
		LinearLayout mainLayoutChangePassword = findViewById(R.id.mainLayoutChangePassword);
		setupUI(mainLayoutChangePassword);
		
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		appBarLayout = findViewById(R.id.appBarLayout);
		tool_title = findViewById(R.id.tool_title);
		mp = MediaPlayer.create(this, R.raw.button_click);
		otpNumber = findViewById(R.id.otpNumber);
		btn_submitOtp = findViewById(R.id.btn_submitOtp);
		resend = findViewById(R.id.resend);
		timer = findViewById(R.id.timer);
		school_logo = findViewById(R.id.school_logo);
		troubleOtp = findViewById(R.id.trouble_otp);

		troubleOtp.setOnClickListener(v -> {
			Intent intent = new Intent(this, TroubleFAQ.class);
			startActivity(intent);
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		});

		otpNumber.setOnFocusChangeListener((v, hasFocus) -> {
			if (!hasFocus)
				hideKeyboard(v);
		});
		
		str_school_logo = getIntent().getStringExtra("userSchoolLogo").trim();
		mob = getIntent().getStringExtra("contact").trim();
		uid = getIntent().getStringExtra("uid").trim();
		otp = getIntent().getStringExtra("verification_code").trim();
		
		settings = getSharedPreferences(PREFS_NAME, 0);
		editor = settings.edit();

		Picasso.with(this).load(R.drawable.large_logo).into(school_logo);
		//color
		SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
		editorColor = settingsColor.edit();
		selToolColor = settingsColor.getString("tool", "");
		selDrawerColor = settingsColor.getString("drawer", "");
		selStatusColor = settingsColor.getString("status", "");
		selTextColor1 = settingsColor.getString("text1", "");
		selTextColor2 = settingsColor.getString("text2", "");
		
		
		auth = FirebaseAuth.getInstance();
		btn_submitOtp.setOnClickListener(view -> {
			mp.start();
			String str_otp = otpNumber.getText().toString().trim();
			Toast.makeText(UpdateNumberOtpScreenActivity.this, "Please wait...", Toast.LENGTH_SHORT).show();
			if (TextUtils.isDigitsOnly(otp)) {
				if (str_otp.equals(otp)) {
					UpdatePhoneNumber b1 = new UpdatePhoneNumber(UpdateNumberOtpScreenActivity.this);
					b1.execute(uid, mob);
				} else {
					Toast.makeText(UpdateNumberOtpScreenActivity.this, "Inavlid Otp", Toast.LENGTH_SHORT).show();
				}
			} else {
				sendOtp(mob);
			}
		});
		
		resendTimer();
		resend.setOnClickListener(view -> {
			mp.start();
			resendTimer();
			Toast.makeText(UpdateNumberOtpScreenActivity.this, "Please wait...", Toast.LENGTH_SHORT).show();
			PhoneAuthProvider.getInstance().verifyPhoneNumber("+91" + mob, 60, TimeUnit.SECONDS, UpdateNumberOtpScreenActivity.this, mCallback);
			
			countDownTimer = new CountDownTimer(30000, 1000) {
				
				@Override
				public void onTick(long l) {
				}
				
				@Override
				public void onFinish() {
					sendOtp(mob);
				}
			}.start();
		});
		
		GradientDrawable bgShape = (GradientDrawable) btn_submitOtp.getBackground();
		bgShape.setColor(Color.parseColor(selToolColor));
		appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
		resend.setTextColor(Color.parseColor(selToolColor));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.parseColor(selStatusColor));
		}
		tool_title.setTextColor(Color.parseColor(selTextColor1));
		btn_submitOtp.setTextColor(Color.parseColor(selTextColor1));
	}

	private void hideKeyboard(View view) {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		assert inputMethodManager != null;
		inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
	
	private void resendTimer() {
		timer.setVisibility(View.VISIBLE);
		resend.setVisibility(View.GONE);
		new CountDownTimer(60000, 1000) {
			
			public void onTick(long millisUntilFinished) {
				timer.setText("00 : " + millisUntilFinished / 1000);
			}
			
			public void onFinish() {
				timer.setVisibility(View.GONE);
				resend.setVisibility(View.VISIBLE);
			}
		}.start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mp.release();
	}

	@SuppressLint("ClickableViewAccessibility")
	public void setupUI(View view) {
		if (!(view instanceof EditText)) {
			view.setOnTouchListener((v, event) -> {
				hideSoftKeyboard(UpdateNumberOtpScreenActivity.this);
				return false;
			});
		}
		
		//If a layout container, iterate over children and seed recursion.
		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				View innerView = ((ViewGroup) view).getChildAt(i);
				setupUI(innerView);
			}
		}
	}
	
	public static void hideSoftKeyboard(Activity activity) {
		InputMethodManager inputMethodManager =
				(InputMethodManager) activity.getSystemService(
						Activity.INPUT_METHOD_SERVICE);
		assert inputMethodManager != null;
		inputMethodManager.hideSoftInputFromWindow(
				Objects.requireNonNull(activity.getCurrentFocus()).getWindowToken(), 0);
	}
	
	private int getRandomNumberInRange() {
		Random r = new Random();
		return r.nextInt((999998 - 100001) + 1) + 100001;
	}
	
	@SuppressLint("StaticFieldLeak")
	public class UpdatePhoneNumber extends AsyncTask<String, String, String> {
		Context ctx;
		
		UpdatePhoneNumber(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			Toast.makeText(ctx, "Please wait we are updating", Toast.LENGTH_SHORT).show();
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			
			String userId = params[0];
			String phoneNumber = params[1];
			@SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
			String data;
			
			try {
				
				URL url = new URL(UPDATE_PHONE_NUMBER);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				
				OutputStream outputStream = httpURLConnection.getOutputStream();
				
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
				data = URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8") + "&" +
						URLEncoder.encode("phoneNumber", "UTF-8") + "=" + URLEncoder.encode(phoneNumber, "UTF-8") + "&" +
						URLEncoder.encode("device_id", "UTF-8") + "=" + URLEncoder.encode(deviceId, "UTF-8") + "&" +
						URLEncoder.encode("tbl_school_id", "UTF-8") + "=" + URLEncoder.encode(SCHOOL_ID, "UTF-8") + "&" +
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
				return response.toString();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			Log.d("Json", "" + result);
			//	pDialog.dismiss();
			if (!result.isEmpty()) {
				validLogin();
			} else {
				Toast.makeText(ctx, "Server issues", Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}
		
	}
	
	private void validLogin() {
		Toast.makeText(UpdateNumberOtpScreenActivity.this, "OTP Verified Successfully\nYour contact number is updated", Toast.LENGTH_SHORT).show();
		Intent i = new Intent(UpdateNumberOtpScreenActivity.this, SelectRoleActivity.class);
		i.putExtra("status", "Online");
		startActivity(i);
		editor.putString("userPhone", mob);
		editor.commit();
		finish();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		
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
	
	@SuppressLint("StaticFieldLeak")
	private class smsBackground extends AsyncTask<String, Void, String> {
		Context ctx;
		String mobileNo;
		String senderId;
		
		smsBackground(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		protected String doInBackground(String... params) {
			String mobile = params[0];
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
			Toast.makeText(getApplicationContext(), "Code sent to the number", Toast.LENGTH_SHORT).show();
		}
	}
}
