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

import com.google.android.material.appbar.AppBarLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Objects;
import java.util.Random;

import com.aaptrix.R;

import static com.aaptrix.activitys.SplashScreen.SCHOOL_NAME;
import static com.aaptrix.activitys.SplashScreen.SENDER_ID;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;
import static com.aaptrix.tools.SPClass.PREF_ROLE;


/**
 * Created by Administrator on 11/29/2017.
 */

public class UpdatePhoneNumberActivity extends AppCompatActivity {
	
	SharedPreferences.Editor editor;
	String userId, userPhone, userSchoolLogo;
	EditText et_number, et_new_number;
	Button btn_sendOtp;
	String str_phone, str_new_number;
	AppBarLayout appBarLayout;
	ImageView school_logo;
	SharedPreferences.Editor editorColor;
	String selToolColor, selDrawerColor, selStatusColor, selTextColor1, selTextColor2;
	TextView tool_title;
	TextView cube1, cube2;
	TextView view1;
	MediaPlayer mp;

	CountDownTimer countDownTimer;
	ProgressBar loader;
	TextView troublePhone;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_phone_number_main_layout);
		Toolbar toolbar = findViewById(R.id.toolbar);
		RelativeLayout mainLayoutChangePassword = findViewById(R.id.mainLayoutChangePassword);
		setupUI(mainLayoutChangePassword);
		
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		appBarLayout = findViewById(R.id.appBarLayout);
		tool_title = findViewById(R.id.tool_title);
		mp = MediaPlayer.create(this, R.raw.button_click);
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		editor = settings.edit();
		userId = settings.getString("userID", "");
		if (getSharedPreferences(PREF_ROLE, 0).getString("userRole", " ").equals("Parent")) {
			userPhone = settings.getString("parentPhone", "");
		} else {
			userPhone = settings.getString("userPhone", "");
		}

		troublePhone = findViewById(R.id.trouble_phone);

		troublePhone.setOnClickListener(v -> {
			Intent intent = new Intent(this, TroubleLoggingIn.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			finish();
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		});

		userSchoolLogo = settings.getString("userSchoolLogo", "");
		
		et_number = findViewById(R.id.et_old_number);
		et_new_number = findViewById(R.id.et_new_number);
		school_logo = findViewById(R.id.school_logo);
		btn_sendOtp = findViewById(R.id.btn_sendOtp);
		cube1 = findViewById(R.id.cube1);
		cube2 = findViewById(R.id.cube2);
		view1 = findViewById(R.id.view1);
		loader = findViewById(R.id.phone_no_loader);

		et_new_number.setOnFocusChangeListener((v, hasFocus) -> {
			if (!hasFocus)
				hideKeyboard(v);
		});

		et_number.setOnFocusChangeListener((v, hasFocus) -> {
			if (!hasFocus)
				hideKeyboard(v);
		});

		Picasso.with(this)
				.load(R.drawable.large_logo).into(school_logo);
		
		btn_sendOtp.setOnClickListener(view -> {
			mp.start();
			str_phone = et_number.getText().toString().trim();
			str_new_number = et_new_number.getText().toString().trim();
			
			if (TextUtils.isEmpty(str_phone)) {
				Toast.makeText(UpdatePhoneNumberActivity.this, "Please enter old mobile number", Toast.LENGTH_SHORT).show();
			} else {
				if (TextUtils.isEmpty(str_new_number)) {
					Toast.makeText(UpdatePhoneNumberActivity.this, "Please enter new mobile number", Toast.LENGTH_SHORT).show();
				} else {
					if (!str_phone.equals(userPhone)) {
						Toast.makeText(UpdatePhoneNumberActivity.this, "Please enter valid old mobile number", Toast.LENGTH_SHORT).show();
					} else {
						loader.setVisibility(View.VISIBLE);
						Toast.makeText(UpdatePhoneNumberActivity.this, "Please wait we check your information", Toast.LENGTH_SHORT).show();
						sendOtp(str_new_number);
						countDownTimer = new CountDownTimer(30000, 1000) {
							
							@Override
							public void onTick(long l) {
							}
							
							@Override
							public void onFinish() {
								sendOtp(str_new_number);
							}
						}.start();
					}
				}
			}
		});
		
		//color
		SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
		editorColor = settingsColor.edit();
		selToolColor = settingsColor.getString("tool", "");
		selDrawerColor = settingsColor.getString("drawer", "");
		selStatusColor = settingsColor.getString("status", "");
		selTextColor1 = settingsColor.getString("text1", "");
		selTextColor2 = settingsColor.getString("text2", "");
		appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
		GradientDrawable bgShape = (GradientDrawable) btn_sendOtp.getBackground();
		bgShape.setColor(Color.parseColor(selToolColor));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.parseColor(selStatusColor));
		}
		tool_title.setTextColor(Color.parseColor(selTextColor1));
		btn_sendOtp.setTextColor(Color.parseColor(selTextColor1));
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

	private void hideKeyboard(View view) {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		assert inputMethodManager != null;
		inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
	
	@SuppressLint("ClickableViewAccessibility")
	public void setupUI(View view) {
		
		// Set up touch listener for non-text box views to hide keyboard.
		if (!(view instanceof EditText)) {
			view.setOnTouchListener((v, event) -> {
				hideSoftKeyboard(UpdatePhoneNumberActivity.this);
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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
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
			smsBackground mGetImagesTask = new smsBackground(this);
			mGetImagesTask.execute(contact, randomNum + "");
		} else {
			Toast.makeText(this, "Please Enter valid Number", Toast.LENGTH_SHORT).show();
		}
	}
	
	@SuppressLint("StaticFieldLeak")
	private class smsBackground extends AsyncTask<String, Void, String> {
		Context ctx;
		String randomNumber, mobileNo;
		String senderId;
		
		smsBackground(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		protected String doInBackground(String... params) {
			String mobile = params[0];
			String randomno = params[1];
			randomNumber = randomno;
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
			loader.setVisibility(View.GONE);
			Toast.makeText(getApplicationContext(), "Code sent to the number", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(UpdatePhoneNumberActivity.this, UpdateNumberOtpScreenActivity.class);
			intent.putExtra("verification_code", "" + randomNumber);
			intent.putExtra("uid", userId);
			intent.putExtra("contact", mobileNo);
			intent.putExtra("userSchoolLogo", userSchoolLogo);
			startActivity(intent);
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			finish();
		}
	}
}
