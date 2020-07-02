package com.aaptrix.activitys.teacher;

import com.aaptrix.adaptor.GridImageAdapter;
import com.aaptrix.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.aaptrix.tools.FileUtil;
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
import pl.droidsonroids.gif.GifImageView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import javax.net.ssl.SSLContext;

import static com.aaptrix.tools.HttpUrl.STAFF_LEAVE_APPLICATION;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;
import static cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;

public class StaffLeaveApplication extends AppCompatActivity {
	
	String userId, schoolId, userPassword, userSchoolLogo, numberOfUser;
	AppBarLayout appBarLayout;
	String selToolColor, selDrawerColor, selStatusColor, selTextColor1, selTextColor2;
	TextView tool_title;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	RelativeLayout mainLayoutLeaveApplication;
	EditText etLeaveSubject, etComposeLeaveDetails;
	Button btn_send;
	String str_subject, str_compose, str_leaveStartDate, str_leaveEndDate, userrType, userSection;
	EditText leaveStartDate, leaveEndDate;
	String sdate = "", edate = "";
	AlertDialog.Builder alert;
	TextView tv_password;
	String userTeacherName;
	SimpleDateFormat sdf;
	AlertDialog alertDialog;
	Uri addImageUri = Uri.parse("android.resource://com.aaptrix/drawable/add_image");
	InputStream stream;
	ArrayList<String> imageArray = new ArrayList<>();
	String file_extn;
	ArrayList<String> filepath = new ArrayList<>();
	ArrayList<Uri> image = new ArrayList<>();
	GridView gridView;
	ImageButton imageView;
	MediaPlayer mp;
	RelativeLayout layout;
	CardView cardView;
	GifImageView taskStatus;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_staff_leave_application);
		
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		appBarLayout = findViewById(R.id.appBarLayout);
		tool_title = findViewById(R.id.tool_title);
		mainLayoutLeaveApplication = findViewById(R.id.mainLayoutLeaveApplication);
		setupUI(mainLayoutLeaveApplication);
		gridView = findViewById(R.id.choose_img_grid);
		imageView = findViewById(R.id.sel_image);
		imageView.bringToFront();
		layout = findViewById(R.id.layout);
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		userId = settings.getString("userID", "");
		schoolId = settings.getString("userSchoolId", "");
		userrType = settings.getString("userrType", "");
		userTeacherName = settings.getString("userTeacherName", "");
		userPassword = settings.getString("userPassword", "");
		userSchoolLogo = settings.getString("userSchoolLogo", "");
		numberOfUser = settings.getString("numberOfUser", "");
		userSection = settings.getString("userSection", "");
		mp = MediaPlayer.create(this, R.raw.button_click);
		try {
			stream = getContentResolver().openInputStream(addImageUri);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		cardView = findViewById(R.id.card_view);
		taskStatus = findViewById(R.id.task_status);
		SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
		selToolColor = settingsColor.getString("tool", "");
		selDrawerColor = settingsColor.getString("drawer", "");
		selStatusColor = settingsColor.getString("status", "");
		selTextColor1 = settingsColor.getString("text1", "");
		selTextColor2 = settingsColor.getString("text2", "");
		
		mSwipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
		mSwipeRefreshLayout.setRefreshing(false);
		mSwipeRefreshLayout.setEnabled(false);

		layout.setOnClickListener(v -> {});
		layout.setOnTouchListener((v, event) -> false);
		
		etLeaveSubject = findViewById(R.id.etLeaveSubject);
		etComposeLeaveDetails = findViewById(R.id.etComposeLeaveDetails);

		etComposeLeaveDetails.setOnFocusChangeListener((v, hasFocus) -> {
			if (!hasFocus) {
				hideKeyboard(v);
			}
		});

		etLeaveSubject.setOnFocusChangeListener((v, hasFocus) -> {
			if (!hasFocus) {
				hideKeyboard(v);
			}
		});
		
		leaveStartDate = findViewById(R.id.leaveStartDate);
		leaveEndDate = findViewById(R.id.leaveEndDate);
		
		btn_send = findViewById(R.id.btn_send);
		
		imageView.bringToFront();
		imageView.setOnClickListener(view -> {
			if (PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
				Intent i = new Intent();
				i.setType("image/*");
				i.setAction(Intent.ACTION_GET_CONTENT);
				final int ACTIVITY_SELECT_IMAGE = 1;
				startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
			} else {
				isPermissionGranted();
			}
		});
		
		gridView.setOnItemClickListener((parent, view, position, id) -> {
			if (PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
				Intent i = new Intent();
				i.setType("image/*");
				i.setAction(Intent.ACTION_GET_CONTENT);
				final int ACTIVITY_SELECT_IMAGE = 1;
				startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
			} else {
				isPermissionGranted();
			}
		});
		
		leaveStartDate.setOnClickListener(view -> {
			final Calendar mcurrentDate = Calendar.getInstance();
			int mYear = mcurrentDate.get(Calendar.YEAR);
			int mMonth = mcurrentDate.get(Calendar.MONTH);
			int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
			
			DatePickerDialog mDatePicker = new DatePickerDialog(
					this, R.style.AlertDialogCustom1, (datepicker, selectedyear, selectedmonth, selectedday) -> {
				mcurrentDate.set(Calendar.YEAR, selectedyear);
				mcurrentDate.set(Calendar.MONTH, selectedmonth);
				mcurrentDate.set(Calendar.DAY_OF_MONTH,
						selectedday);
				sdf = new SimpleDateFormat(
						getResources().getString(
								R.string.date_card_formate),
						Locale.US);
				SimpleDateFormat sdf1 = new SimpleDateFormat(
						getResources().getString(
								R.string.date_card_formate_dairy1),
						Locale.US);
				
				sdate = sdf1.format(mcurrentDate
						.getTime());
				edate = sdf1.format(mcurrentDate
						.getTime());
				leaveStartDate.setText(sdf.format(mcurrentDate
						.getTime()));
				leaveEndDate.setText(sdf.format(mcurrentDate
						.getTime()));
			}, mYear, mMonth, mDay);
			
			mDatePicker.show();
		});
		
		leaveEndDate.setOnClickListener(view -> {
			final Calendar mcurrentDate = Calendar.getInstance();
			int mYear = mcurrentDate.get(Calendar.YEAR);
			int mMonth = mcurrentDate.get(Calendar.MONTH);
			int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
			
			DatePickerDialog mDatePicker = new DatePickerDialog(
					this, R.style.AlertDialogCustom1, (datepicker, selectedyear, selectedmonth, selectedday) -> {
				
				mcurrentDate.set(Calendar.YEAR, selectedyear);
				mcurrentDate.set(Calendar.MONTH, selectedmonth);
				mcurrentDate.set(Calendar.DAY_OF_MONTH,
						selectedday);
				sdf = new SimpleDateFormat(
						getResources().getString(
								R.string.date_card_formate),
						Locale.US);
				SimpleDateFormat sdf1 = new SimpleDateFormat(
						getResources().getString(
								R.string.date_card_formate_dairy1),
						Locale.US);
				edate = sdf1.format(mcurrentDate
						.getTime());
				leaveEndDate.setText(sdf.format(mcurrentDate
						.getTime()));
			}, mYear, mMonth, mDay);
			mDatePicker.show();
		});
		
		btn_send.setOnClickListener(view -> {
			mp.start();
			str_subject = etLeaveSubject.getText().toString().trim();
			str_compose = etComposeLeaveDetails.getText().toString().trim();
			str_leaveStartDate = sdate;
			str_leaveEndDate = edate;
			
			if (TextUtils.isEmpty(str_subject)) {
				Toast.makeText(this, "Please enter subject", Toast.LENGTH_SHORT).show();
			} else if (TextUtils.isEmpty(str_compose)) {
				Toast.makeText(this, "Please enter leave details", Toast.LENGTH_SHORT).show();
			} else if (TextUtils.isEmpty(str_leaveStartDate)) {
				Toast.makeText(this, "Please select leave start date", Toast.LENGTH_SHORT).show();
			} else if (TextUtils.isEmpty(str_leaveEndDate)) {
				Toast.makeText(this, "Please select leave end date", Toast.LENGTH_SHORT).show();
			} else {
				boolean bool = CheckDates(str_leaveStartDate, str_leaveEndDate);
				layout.setVisibility(View.VISIBLE);
				layout.bringToFront();
				if (bool) {
					LayoutInflater factory = LayoutInflater.from(this);
					View textEntryView = factory.inflate(R.layout.leave_submit_alert_layout, null);
					
					tv_password = (EditText) textEntryView.findViewById(R.id.tv_password);
					
					alert = new AlertDialog.Builder(this, R.style.DialogTheme);
					//.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
					alert.setView(textEntryView).setPositiveButton("Yes", null).setNegativeButton("Cancel", null);
					alertDialog = alert.create();
					alertDialog.show();
					Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
					Button theButton1 = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
					theButton.setOnClickListener(new CustomListener(alertDialog));
					theButton.setTextColor(Color.parseColor(selToolColor));
					theButton1.setTextColor(Color.parseColor(selToolColor));
				} else {
					Toast.makeText(this, "End date should be more than start date", Toast.LENGTH_SHORT).show();
				}
			}
			
		});
		
		appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.parseColor(selStatusColor));
		}
		tool_title.setTextColor(Color.parseColor(selTextColor1));
		btn_send.setTextColor(Color.parseColor(selTextColor1));
		btn_send.setBackgroundColor(Color.parseColor(selToolColor));
		mSwipeRefreshLayout.setColorScheme(R.color.text_gray);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mp.release();
	}

	public void isPermissionGranted() {
		ActivityCompat.requestPermissions(this,
				new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
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
	
	public static boolean CheckDates(String mValue3, String mValue5) {
		SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
		boolean b = false;
		try {
			if (dfDate.parse(mValue3).before(dfDate.parse(mValue5))) {
				b = true;
			} else
				b = dfDate.parse(mValue3).equals(dfDate.parse(mValue5));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1)
			if (resultCode == Activity.RESULT_OK) {
				ClipData clipData = data.getClipData();
				if (clipData != null) {
					if (clipData.getItemCount() > 4) {
						Toast.makeText(this, "Please select upto only 4 images", Toast.LENGTH_SHORT).show();
					} else {
						for (int i = 0; i < clipData.getItemCount(); i++) {
							image.add(clipData.getItemAt(i).getUri());
						}
						for (int i = 0; i < image.size(); i++) {
							filepath.add(FileUtil.getFileName(this, image.get(i)));
							file_extn = filepath.get(i).substring(filepath.get(i).lastIndexOf(".") + 1);
							try {
								if (file_extn.equals("jpg") || file_extn.equals("jpeg") || file_extn.equals("png")) {
									imageArray.add(filepath.get(i));
								} else {
									FileNotFoundException fe = new FileNotFoundException();
									Toast.makeText(this, "File not in required format.", Toast.LENGTH_SHORT).show();
									throw fe;
								}
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							}
						}
						if (image.size() != 4) {
							image.add(addImageUri);
						}
						GridImageAdapter addAdapter = new GridImageAdapter(this, R.layout.image_add_grid, image);
						gridView.setAdapter(addAdapter);
						imageView.setVisibility(View.GONE);
						addAdapter.notifyDataSetChanged();
					}
				} else {
					image.add(data.getData());
					for (int i = 0; i < image.size(); i++) {
						filepath.add(FileUtil.getFileName(this, image.get(i)));
						file_extn = filepath.get(i).substring(filepath.get(i).lastIndexOf(".") + 1);
						try {
							if (file_extn.equals("jpg") || file_extn.equals("jpeg") || file_extn.equals("png")) {
								imageArray.add(filepath.get(i));
							} else {
								FileNotFoundException fe = new FileNotFoundException();
								Toast.makeText(this, "File not in required format.", Toast.LENGTH_SHORT).show();
								throw fe;
							}
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					}
					if (image.size() != 4) {
						image.add(addImageUri);
					}
					GridImageAdapter addAdapter = new GridImageAdapter(this, R.layout.image_add_grid, image);
					gridView.setAdapter(addAdapter);
					imageView.setVisibility(View.GONE);
					addAdapter.notifyDataSetChanged();
				}
			}
	}

	private void hideKeyboard(View view) {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		assert inputMethodManager != null;
		inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
	
	class CustomListener implements View.OnClickListener {
		private final Dialog dialog;
		
		CustomListener(Dialog dialog) {
			this.dialog = dialog;
		}
		
		@Override
		public void onClick(View v) {
			String mValue = tv_password.getText().toString();
			String old = md5(mValue);
			if (!TextUtils.isEmpty(mValue)) {
				if (old.equals(userPassword)) {
					alertDialog.dismiss();
					try {
						SendLeaveApplication b1 = new SendLeaveApplication(StaffLeaveApplication.this);
						b1.execute(userId, str_subject, str_compose, str_leaveStartDate, str_leaveEndDate, schoolId, userTeacherName);
						dialog.dismiss();
					} catch (Exception e) {
						SendLeaveApplication b1 = new SendLeaveApplication(StaffLeaveApplication.this);
						b1.execute(userId, str_subject, str_compose, str_leaveStartDate, str_leaveEndDate, schoolId, userTeacherName);
					}
				} else {
					Toast.makeText(StaffLeaveApplication.this, "Password Wrong", Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(StaffLeaveApplication.this, "Please enter Password", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	public static String md5(final String s) {
		final String MD5 = "MD5";
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest
					.getInstance(MD5);
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();
			
			// Create Hex String
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
	
	
	@SuppressLint("ClickableViewAccessibility")
	public void setupUI(View view) {
		if (!(view instanceof EditText)) {
			view.setOnTouchListener((v, event) -> {
				hideSoftKeyboard(this);
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
	
	public static void hideSoftKeyboard(Activity activity) {
		InputMethodManager inputMethodManager =
				(InputMethodManager) activity.getSystemService(
						Activity.INPUT_METHOD_SERVICE);
		assert inputMethodManager != null;
		inputMethodManager.hideSoftInputFromWindow(
				Objects.requireNonNull(activity.getCurrentFocus()).getWindowToken(), 0);
	}
	
	@SuppressLint("StaticFieldLeak")
	public class SendLeaveApplication extends AsyncTask<String, String, String> {
		Context ctx;
		
		SendLeaveApplication(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			mSwipeRefreshLayout.setRefreshing(true);
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			
			String userId = params[0];
			String str_subject = params[1];
			String str_compose = params[2];
			String str_leaveStartDate = params[3];
			String str_leaveEndDate = params[4];
			String schoolId = params[5];
			String notiImage = "0";
			
			try {
				ArrayList<String> imageNames = new ArrayList<>();
				if (!imageArray.isEmpty())
					for (int i = 0; i < imageArray.size(); i++) {
						try {
							SSLContext sslContext = SSLContexts.custom().useTLS().build();
							SSLConnectionSocketFactory f = new SSLConnectionSocketFactory(
									sslContext,
									new String[]{"TLSv1.1", "TLSv1.2"},
									null,
									BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
							HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(f).build();
							HttpPost httppost = new HttpPost(STAFF_LEAVE_APPLICATION);
							MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
							entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
							File file = new File(imageArray.get(i));
							FileBody image = new FileBody(file);
							entityBuilder.addPart("image", image);
							entityBuilder.addTextBody("schoolId", schoolId);
							HttpEntity entity = entityBuilder.build();
							httppost.setEntity(entity);
							HttpResponse response = httpclient.execute(httppost);
							HttpEntity httpEntity = response.getEntity();
							String result = EntityUtils.toString(httpEntity);
							JSONObject jsonObject = new JSONObject(result);
							imageNames.add("\"" + jsonObject.getString("imageNm") + "\"");
							if (i == 0)
								notiImage = jsonObject.getString("imageNm");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				Log.e("result", imageNames.toString());
				try {
					SSLContext sslContext = SSLContexts.custom().useTLS().build();
					SSLConnectionSocketFactory f = new SSLConnectionSocketFactory(
							sslContext,
							new String[]{"TLSv1.1", "TLSv1.2"},
							null,
							BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
					HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(f).build();
					HttpPost httppost = new HttpPost(STAFF_LEAVE_APPLICATION);
					MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
					entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
					entityBuilder.addTextBody("str_img", imageNames.toString().replace(" ", ""));
					entityBuilder.addTextBody("schoolId", schoolId);
					entityBuilder.addTextBody("str_leaveEndDate", str_leaveEndDate);
					entityBuilder.addTextBody("str_leaveStartDate", str_leaveStartDate);
					entityBuilder.addTextBody("str_compose", str_compose);
					entityBuilder.addTextBody("user_type", userrType);
					entityBuilder.addTextBody("userId", userId);
					entityBuilder.addTextBody("str_subject", str_subject);
					entityBuilder.addTextBody("noti_image", notiImage);
					HttpEntity entity = entityBuilder.build();
					httppost.setEntity(entity);
					HttpResponse response = httpclient.execute(httppost);
					HttpEntity httpEntity = response.getEntity();
					String res = EntityUtils.toString(httpEntity);
					Log.e("res", res);
					return res;
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Log.d("Leave", result);
			
			if (result.contains("\"submitted\"")) {
				mSwipeRefreshLayout.setRefreshing(false);
				cardView.setVisibility(View.VISIBLE);
				new CountDownTimer(4000, 1000) {
					@Override
					public void onTick(long millisUntilFinished) {

					}

					@Override
					public void onFinish() {
						Intent i = new Intent(ctx, StaffMyAttendance.class);
						startActivity(i);
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
						finish();
						Toast.makeText(ctx, "Application submitted successfully", Toast.LENGTH_SHORT).show();
					}
				}.start();
			} else {
				mSwipeRefreshLayout.setRefreshing(false);
				layout.setVisibility(View.GONE);
				Toast.makeText(ctx, "server issue please try after some time...", Toast.LENGTH_SHORT).show();
			}
		}
		
	}
	
	public final boolean isInternetOn() {
		ConnectivityManager connec = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
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
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}
}
