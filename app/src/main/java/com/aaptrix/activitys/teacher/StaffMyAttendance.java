package com.aaptrix.activitys.teacher;

import com.aaptrix.activitys.admin.InstituteBuzzActivityDiff;
import com.aaptrix.activitys.student.InstituteBuzzActivity;
import com.aaptrix.databeans.DatabeanAttendance;
import com.aaptrix.databeans.DatabeanEvents;
import com.aaptrix.R;
import com.aaptrix.tools.FileUtil;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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
import id.zelory.compressor.Compressor;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.net.ssl.SSLContext;

import static com.aaptrix.tools.HttpUrl.STAFF_OWN_ATTENDANCE;
import static com.aaptrix.tools.HttpUrl.UPDATE_USER_PRO_IMAGE;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREFS_USER;
import static com.aaptrix.tools.SPClass.PREF_COLOR;
import static cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;

public class StaffMyAttendance extends AppCompatActivity {
	
	ImageView iv_edit;
	CircleImageView iv_user_img;
	SharedPreferences.Editor editor;
	String userId, roleId, schoolId, userSection, userRollNumber, userClassTeacher;
	
	LinearLayout loader;
	Bitmap bitmap;
	SharedPreferences.Editor editorColor;
	String selToolColor, selDrawerColor, selStatusColor, selTextColor1, selTextColor2, userJson, numberOfUser;
	AppBarLayout appBarLayout;
	TextView tool_title, tv_logout_text;
	ImageView logoutImg;
	
	SharedPreferences.Editor editorUser;
	String userID;
	String userLoginId;
	String userName;
	String userImg;
	String userrType;
	String userPassword;
	String userSchoolId;
	
	//calender
	public GregorianCalendar cal_month, cal_month_copy;
	TextView todayTv;
	TextView submit_lieave_tv;
	TextView absentTv;
	TextView tv_present, tv_holiday;
	TextView tv_absent;
	TextView tv_leave;
	TextView tv_today1;
	TextView view_leaveList;
	MediaPlayer mp;
	ArrayList<DatabeanEvents> eventsArray = new ArrayList<>();
	DatabeanEvents dbe;
	TextView tv_month_year, tv_present_count, tv_absent_count, tv_year, tv_prese_count, tv_absnt_count;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	
	ArrayList<DatabeanAttendance> attandanceArray = new ArrayList<>();
	DatabeanAttendance dbattand;
	String attandId, attandStatus, attandDate;
	TextView percent1, percent2;
	int presentCount, absentCount, totalLeave;
	float totalPresentYear;
	int presentCountMonth, absentCountMonth, totalLeaveMonth;
	float totalPresentMonth;
	ProgressBar progressBar, progressBar1;
	
	//offline
	private SharedPreferences sp_attend;
	SharedPreferences.Editor se_attend;
	public static final String PREFS_ATTEND = "json_attend";
	LinearLayout mainLayoutAttendance;
	MaterialCalendarView calendarView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_staff_my_attendance);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		mp = MediaPlayer.create(this, R.raw.button_click);
		setTitle("");
		setResult(RESULT_OK);
		appBarLayout = findViewById(R.id.appBarLayout);
		mainLayoutAttendance = findViewById(R.id.mainLayoutAttendance);
		tool_title = findViewById(R.id.tool_title);
		//color
		SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
		editorColor = settingsColor.edit();
		selToolColor = settingsColor.getString("tool", "");
		selDrawerColor = settingsColor.getString("drawer", "");
		selStatusColor = settingsColor.getString("status", "");
		selTextColor1 = settingsColor.getString("text1", "");
		selTextColor2 = settingsColor.getString("text2", "");
		calendarView = findViewById(R.id.calendar);
		//typecasting
		iv_user_img = findViewById(R.id.iv_user_img);
		iv_edit = findViewById(R.id.iv_edit1);
		tv_logout_text = findViewById(R.id.tv_logout_text);
		logoutImg = findViewById(R.id.logoutImg);
		
		progressBar = findViewById(R.id.progressBar);
		progressBar1 = findViewById(R.id.progressBar1);
		
		loader = findViewById(R.id.loader);
		todayTv = findViewById(R.id.todayTv);
		tv_present = findViewById(R.id.tv_present);
		tv_holiday = findViewById(R.id.tv_holiday);
		tv_absent = findViewById(R.id.tv_absent);
		tv_leave = findViewById(R.id.tv_leave);
		tv_today1 = findViewById(R.id.tv_today);
		absentTv = findViewById(R.id.absentTv);
		submit_lieave_tv = findViewById(R.id.submit_lieave_tv);
		view_leaveList = findViewById(R.id.view_leaveList);
		
		
		tv_month_year = findViewById(R.id.tv_month_year);
		tv_present_count = findViewById(R.id.tv_present_count);
		tv_absent_count = findViewById(R.id.tv_absent_count);
		tv_year = findViewById(R.id.tv_year);
		tv_prese_count = findViewById(R.id.tv_prsnt_count);
		tv_absnt_count = findViewById(R.id.tv_absnt_count);
		percent1 = findViewById(R.id.percent1);
		percent2 = findViewById(R.id.percent2);

		SimpleDateFormat sdf = new SimpleDateFormat("MMM, yyyy", Locale.getDefault());
		tv_month_year.setText(sdf.format(Calendar.getInstance().getTime()));
		
		mSwipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
		mSwipeRefreshLayout.setEnabled(false);
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		editor = settings.edit();
		userId = settings.getString("userID", "");
		userLoginId = settings.getString("userLoginId", "");
		userName = settings.getString("userName", "");
		userImg = settings.getString("userImg", "");
		userSection = settings.getString("userSection", "");
		userRollNumber = settings.getString("userRollNumber", "");
		userClassTeacher = settings.getString("userTeacherName", "");
		userrType = settings.getString("userrType", "");
		userPassword = settings.getString("userPassword", "");
		roleId = settings.getString("str_role_id", "");
		schoolId = settings.getString("str_school_id", "");
		numberOfUser = settings.getString("numberOfUser", "");

		cal_month = (GregorianCalendar) GregorianCalendar.getInstance();
		cal_month_copy = (GregorianCalendar) cal_month.clone();
		sp_attend = getSharedPreferences(PREFS_ATTEND, 0);
		se_attend = sp_attend.edit();
		String attendance = sp_attend.getString("json_attend", "");
		mSwipeRefreshLayout.setRefreshing(false);
		
		
		SharedPreferences settingsUser = getSharedPreferences(PREFS_USER, 0);
		editorUser = settingsUser.edit();
		userJson = settingsUser.getString("user", "");
		if (isInternetOn()) {
			cal_month = (GregorianCalendar) GregorianCalendar.getInstance();
			cal_month_copy = (GregorianCalendar) cal_month.clone();
			eventsArray.clear();
			attandanceArray.clear();
			GetAttandance b1 = new GetAttandance(this);
			b1.execute(userId);
		} else {
			cal_month = (GregorianCalendar) GregorianCalendar.getInstance();
			cal_month_copy = (GregorianCalendar) cal_month.clone();
			mSwipeRefreshLayout.setRefreshing(false);
			showAttendance(attendance);
			Toast.makeText(this, "No network Please connect with network for update", Toast.LENGTH_SHORT).show();
		}
		
		
		if (userImg.equals("0")) {
			iv_user_img.setImageDrawable(getResources().getDrawable(R.drawable.user_place_hoder));
		} else if (!TextUtils.isEmpty(userImg)) {
			String url;
			switch (userrType) {
				case "Parent":
					url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "") + "/users/students/profile/" + userImg;
					Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(iv_user_img);
					break;
				case "Admin":
					url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "") + "/users/admin/profile/" + userImg;
					Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(iv_user_img);
					break;
				case "Staff":
					url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "") + "/users/staff/profile/" + userImg;
					Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(iv_user_img);
					break;
				case "Teacher":
					url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "") + "/users/teachers/profile/" + userImg;
					Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(iv_user_img);
					break;
				case "Student":
					url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "") + "/users/students/profile/" + userImg;
					Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(iv_user_img);
					break;
			}
		} else {
			iv_user_img.setImageDrawable(getResources().getDrawable(R.drawable.user_place_hoder));
		}
		
		iv_edit.setOnClickListener(view -> {
			if (isInternetOn()) {
				if (ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
					ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
				}
				
				Intent gallery = new Intent();
				gallery.setAction(Intent.ACTION_GET_CONTENT);
				gallery.setType("image/*");
				startActivityForResult(gallery, 1);
			} else {
				Toast.makeText(this, "No network Please connect with network for update", Toast.LENGTH_SHORT).show();
			}
		});
		
		
		submit_lieave_tv.setOnClickListener(view -> {
			mp.start();
			if (isInternetOn()) {
				Intent i = new Intent(this, StaffLeaveApplication.class);
				startActivity(i);
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			} else {
				Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show();
			}
		});
		
		view_leaveList.setOnClickListener(view -> {
			mp.start();
			if (isInternetOn()) {
				Intent i = new Intent(this, StaffLeaveList.class);
				startActivity(i);
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			} else {
				Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show();
			}
		});
		
		appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
		logoutImg.setColorFilter(Color.parseColor(selToolColor));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.parseColor(selStatusColor));
		}
		tool_title.setTextColor(Color.parseColor(selTextColor1));
		tv_logout_text.setTextColor(Color.parseColor(selTextColor1));
		tv_absent.setTextColor(Color.parseColor(selToolColor));
		tv_present.setTextColor(Color.parseColor(selToolColor));
		tv_holiday.setTextColor(Color.parseColor(selToolColor));
		tv_leave.setTextColor(Color.parseColor(selToolColor));
		absentTv.setBackground(getResources().getDrawable(R.drawable.cube));
		GradientDrawable drawable = (GradientDrawable) absentTv.getBackground();
		drawable.setStroke(2, Color.parseColor(selToolColor)); // set stroke width and stroke color

		tv_today1.setTextColor(Color.parseColor(selToolColor));
		submit_lieave_tv.setTextColor(Color.parseColor(selTextColor1));
		view_leaveList.setTextColor(Color.parseColor(selTextColor1));
		submit_lieave_tv.setBackgroundColor(Color.parseColor(selToolColor));
		view_leaveList.setBackgroundColor(Color.parseColor(selToolColor));
		tv_month_year.setTextColor(Color.parseColor(selToolColor));
		tv_year.setTextColor(Color.parseColor(selToolColor));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mp.release();
	}
	
	private void startAnimation(int d, int b) {
		ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", 100, (100 - d));
		progressAnimator.setDuration(3000);
		progressAnimator.setInterpolator(new LinearInterpolator());
		progressAnimator.start();
		ObjectAnimator progressAnimator1 = ObjectAnimator.ofInt(progressBar1, "progress", 100, (100 - b));
		progressAnimator1.setDuration(3000);
		progressAnimator1.setInterpolator(new LinearInterpolator());
		progressAnimator1.start();
	}
	
	
	private void showAttendance(String attendance) {
		if (attendance.equals("{\"result\":null}")) {
			tv_present_count.setText("N.A");
			tv_absent_count.setText("N.A");
			tv_prese_count.setText("N.A");
			tv_absnt_count.setText("N.A");
			percent1.setText("0%");
			percent2.setText("0%");
			eventDetails();
			Toast.makeText(this, "No Attendance", Toast.LENGTH_SHORT).show();
		} else {
			
			try {
				JSONObject jsonRootObject = new JSONObject(attendance);
				JSONArray jsonArray = jsonRootObject.getJSONArray("result");
				eventsArray.clear();
				for (int i = 0; i < jsonArray.length(); i++) {
					dbattand = new DatabeanAttendance();
					dbe = new DatabeanEvents();
					
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					attandId = jsonObject.getString("tbl_attandance_id");
					attandStatus = jsonObject.getString("tbl_attandance_status");
					attandDate = jsonObject.getString("tbl_attandance_date");
					
					String[] separated = attandDate.split("-");
					String month = separated[1];
					String year = separated[0];
					
					if (month.contentEquals(android.text.format.DateFormat.format("MM", cal_month)) && year.contentEquals(android.text.format.DateFormat.format("yyyy", cal_month))) {
						switch (attandStatus) {
							case "Present":
								presentCountMonth++;
								break;
							case "Absent":
								absentCountMonth++;
								break;
							case "Leave":
								totalLeaveMonth++;
								break;
						}
					}
					
					if (year.contentEquals(android.text.format.DateFormat.format("yyyy", cal_month))) {
						switch (attandStatus) {
							case "Present":
								presentCount++;
								break;
							case "Absent":
								absentCount++;
								break;
							case "Leave":
								totalLeave++;
								break;
						}
					}
					dbattand.setAttandId(attandId);
					dbattand.setAttandStatus(attandStatus);
					dbattand.setAttandDate(attandDate);
					dbe.setEventDate(attandDate);
					dbe.setEventTitle(attandStatus);
					eventsArray.add(dbe);
					attandanceArray.add(dbattand);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (attandanceArray.size() != 0) {
				setAttendance();
			}
		}
	}
	
	
	//online
	
	
	@SuppressLint("StaticFieldLeak")
	public class GetAttandance extends AsyncTask<String, String, String> {
		Context ctx;
		
		GetAttandance(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			mSwipeRefreshLayout.setRefreshing(true);
			presentCount = 0;
			absentCount = 0;
			totalLeave = 0;
			totalPresentYear = 0;
			presentCountMonth = 0;
			absentCountMonth = 0;
			totalLeaveMonth = 0;
			totalPresentMonth = 0;
			mainLayoutAttendance.setVisibility(View.GONE);
			
			
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			
			String userId = params[0];
			String data;
			
			try {
				
				URL url = new URL(STAFF_OWN_ATTENDANCE);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				OutputStream outputStream = httpURLConnection.getOutputStream();
				
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
				data = URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");
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
			Log.e("Events", "" + result);
			sp_attend = getSharedPreferences(PREFS_ATTEND, 0);
			se_attend = sp_attend.edit();
			se_attend.clear();
			se_attend.putString("json_attend", result);
			se_attend.commit();
			mSwipeRefreshLayout.setRefreshing(false);
			mainLayoutAttendance.setVisibility(View.VISIBLE);
			
			try {
				if (result.equals("{\"result\":null}")) {
					tv_present_count.setText("N.A");
					tv_absent_count.setText("N.A");
					tv_prese_count.setText("N.A");
					tv_absnt_count.setText("N.A");
					percent1.setText("0%");
					percent2.setText("0%");
					eventDetails();
					Toast.makeText(ctx, "No Attendance", Toast.LENGTH_SHORT).show();
				} else {
					
					try {
						JSONObject jsonRootObject = new JSONObject(result);
						JSONArray jsonArray = jsonRootObject.getJSONArray("result");
						eventsArray.clear();
						for (int i = 0; i < jsonArray.length(); i++) {
							dbattand = new DatabeanAttendance();
							dbe = new DatabeanEvents();
							
							JSONObject jsonObject = jsonArray.getJSONObject(i);
							attandId = jsonObject.getString("tbl_attandance_id");
							attandStatus = jsonObject.getString("tbl_attandance_status");
							attandDate = jsonObject.getString("tbl_attandance_date");
							
							String[] separated = attandDate.split("-");
							String month = separated[1];
							String year = separated[0];
							
							if (month.contentEquals(android.text.format.DateFormat.format("MM", cal_month)) && year.contentEquals(android.text.format.DateFormat.format("yyyy", cal_month))) {
								switch (attandStatus) {
									case "Present":
										presentCountMonth++;
										break;
									case "Absent":
										absentCountMonth++;
										break;
									case "Leave":
										totalLeaveMonth++;
										break;
								}
							}
							
							if (year.contentEquals(android.text.format.DateFormat.format("yyyy", cal_month))) {
								switch (attandStatus) {
									case "Present":
										presentCount++;
										break;
									case "Absent":
										absentCount++;
										break;
									case "Leave":
										totalLeave++;
										break;
								}
							}
							dbattand.setAttandId(attandId);
							dbattand.setAttandStatus(attandStatus);
							dbattand.setAttandDate(attandDate);
							dbe.setEventDate(attandDate);
							dbe.setEventTitle(attandStatus);
							eventsArray.add(dbe);
							attandanceArray.add(dbattand);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					if (attandanceArray.size() != 0) {
						setAttendance();
					}
					super.onPostExecute(result);
				}
			} catch (Exception ignored) {
			
			}
		}
		
	}
	
	private void setAttendance() {
		eventDetails();
		tv_prese_count.setText("Present : " + presentCount);
		tv_absnt_count.setText("Absent  : " + absentCount);
		
		tv_present_count.setText("Present : " + presentCountMonth);
		tv_absent_count.setText("Absent  : " + absentCountMonth);
		//setPercent
		float a = (presentCount + absentCount);
		totalPresentYear = presentCount / a;
		int b = (int) (totalPresentYear * 100);
		percent2.setText("" + b + "%");
		
		float c = (presentCountMonth + absentCountMonth);
		totalPresentMonth = presentCountMonth / c;
		int d = (int) (totalPresentMonth * 100);
		percent1.setText("" + d + "%");
		
		startAnimation(d, b);
		progressBar.setProgress(d);
		progressBar1.setProgress(b);
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
					File actualImage = FileUtil.from(this, filePath);
					File compressedImage = new Compressor(this)
							.setMaxWidth(640)
							.setMaxHeight(480)
							.setQuality(75)
							.setCompressFormat(Bitmap.CompressFormat.WEBP)
							.setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
									Environment.DIRECTORY_PICTURES).getAbsolutePath())
							.compressToFile(actualImage);
					bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.fromFile(compressedImage));
					
					UpdateProfileImage updateProfileImage = new UpdateProfileImage(this, compressedImage);
					updateProfileImage.execute(userId);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			} else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
				Exception error = result.getError();
				Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show();
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
			Log.d("Json", "" + result);
			try {
				JSONObject jsonObject = new JSONObject(result);
				if (jsonObject.getString("success").equals("true")) {
					editor.putString("userImg", jsonObject.getString("imageNm"));
					editor.commit();
					String firebase_userID = "educoach" + userId + "@educoach.co.in";
					String firebase_password = "educoach" + userId;
					FirebaseAuth mAuth = FirebaseAuth.getInstance();
					mAuth.signInWithEmailAndPassword(firebase_userID, firebase_password)
							.addOnCompleteListener(task -> {
								String current_user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
								DatabaseReference storeUserDefaultDataReference = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
								try {
									storeUserDefaultDataReference.child("userImg").setValue(jsonObject.getString("imageNm"))
											.addOnCompleteListener(task1 -> {
												Log.e("task", "" + task1.isSuccessful());
											});
								} catch (JSONException e) {
									e.printStackTrace();
								}
							});
					Toast.makeText(ctx, "Your Image is Updated", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(ctx, "Not uploaded image is too large", Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			super.onPostExecute(result);
		}
		
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if (item.getItemId() == android.R.id.home) {
			if (userrType.equals("Student")) {
				Intent i = new Intent(this, InstituteBuzzActivity.class);
				startActivity(i);
				finish();
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			} else {
				Intent i = new Intent(this, InstituteBuzzActivityDiff.class);
				startActivity(i);
				finish();
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			}
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	
	@Override
	public void onBackPressed() {
		if (userrType.equals("Student")) {
			Intent i = new Intent(this, InstituteBuzzActivity.class);
			startActivity(i);
			finish();
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		} else {
			Intent i = new Intent(this, InstituteBuzzActivityDiff.class);
			startActivity(i);
			finish();
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		}
	}
	
	public final boolean isInternetOn() {
		
		// get Connectivity Manager object to check connection
		ConnectivityManager connec =
				(ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		
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

	private void eventDetails() {
		calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_NONE);
		calendarView.setAllowClickDaysOutsideCurrentMonth(false);
		calendarView.setShowOtherDates(MaterialCalendarView.SHOW_NONE);
		calendarView.setDateSelected(CalendarDay.today(), true);
		calendarView.setSelectionColor(Color.WHITE);

		List<CalendarDay> absentDay = new ArrayList<>();
		List<CalendarDay> presentDay = new ArrayList<>();
		List<CalendarDay> leaveDay = new ArrayList<>();
		List<CalendarDay> holiday = new ArrayList<>();

		for (int i = 0; i < eventsArray.size(); i++) {
			if (eventsArray.get(i).getEventTitle().equals("Absent")) {
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
					Date date = sdf.parse(eventsArray.get(i).getEventDate());
					absentDay.add(new CalendarDay(date));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}

		for (int i = 0; i < eventsArray.size(); i++) {
			if (eventsArray.get(i).getEventTitle().equals("Present")) {
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
					Date date = sdf.parse(eventsArray.get(i).getEventDate());
					presentDay.add(new CalendarDay(date));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}

		for (int i = 0; i < eventsArray.size(); i++) {
			if (eventsArray.get(i).getEventTitle().equals("Leave")) {
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
					Date date = sdf.parse(eventsArray.get(i).getEventDate());
					leaveDay.add(new CalendarDay(date));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}

		for (int i = 0; i < eventsArray.size(); i++) {
			if (eventsArray.get(i).getEventTitle().equals("Holiday")) {
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
					Date date = sdf.parse(eventsArray.get(i).getEventDate());
					holiday.add(new CalendarDay(date));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}

		for (int i = 0; i < eventsArray.size(); i++) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
				Date date = sdf.parse(eventsArray.get(i).getEventDate());
				calendarView.setDateSelected(date, true);
				calendarView.setSelectionColor(Color.parseColor(selToolColor));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		calendarView.addDecorators(new TodayDecor(CalendarDay.today()), new PresentDecor(presentDay),
				new AbsentDecor(absentDay), new LeaveDecor(leaveDay), new HolidayDecor(holiday));

	}

	public class AbsentDecor implements DayViewDecorator {

		private List<CalendarDay> days;

		AbsentDecor(List<CalendarDay> days) {
			this.days = days;
		}

		@Override
		public boolean shouldDecorate(CalendarDay day) {
			return days.contains(day) && !day.equals(CalendarDay.today());
		}

		@Override
		public void decorate(DayViewFacade view) {
			view.addSpan(new ForegroundColorSpan(Color.RED));
//			view.addSpan(new BackgroundColorSpan(Color.WHITE));
			Drawable drawable = getResources().getDrawable(R.drawable.day_item_background);
			drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
			view.setSelectionDrawable(drawable);
		}
	}

	public class HolidayDecor implements DayViewDecorator {

		private List<CalendarDay> days;

		HolidayDecor(List<CalendarDay> days) {
			this.days = days;
		}

		@Override
		public boolean shouldDecorate(CalendarDay day) {
			return days.contains(day) && !day.equals(CalendarDay.today());
		}

		@Override
		public void decorate(DayViewFacade view) {
			view.addSpan(new ForegroundColorSpan(getResources().getColor(R.color.orange)));
			Drawable drawable = getResources().getDrawable(R.drawable.day_item_background);
			drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
			view.setSelectionDrawable(drawable);
		}
	}

	public class PresentDecor implements DayViewDecorator {

		private List<CalendarDay> days;

		PresentDecor(List<CalendarDay> days) {
			this.days = days;
		}

		@Override
		public boolean shouldDecorate(CalendarDay day) {
			return days.contains(day) && !day.equals(CalendarDay.today());
		}

		@Override
		public void decorate(DayViewFacade view) {
			view.addSpan(new ForegroundColorSpan(Color.GREEN));
//			view.addSpan(new BackgroundColorSpan(Color.WHITE));
			Drawable drawable = getResources().getDrawable(R.drawable.day_item_background);
			drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
			view.setSelectionDrawable(drawable);
		}
	}

	public class LeaveDecor implements DayViewDecorator {

		private List<CalendarDay> days;

		LeaveDecor(List<CalendarDay> days) {
			this.days = days;
		}

		@Override
		public boolean shouldDecorate(CalendarDay day) {
			return days.contains(day) && !day.equals(CalendarDay.today());
		}

		@Override
		public void decorate(DayViewFacade view) {
			view.addSpan(new ForegroundColorSpan(getResources().getColor(R.color.cream_yellow)));
//			view.addSpan(new BackgroundColorSpan(Color.WHITE));
			Drawable drawable = getResources().getDrawable(R.drawable.day_item_background);
			drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
			view.setSelectionDrawable(drawable);
		}
	}

	public class TodayDecor implements DayViewDecorator {

		private CalendarDay day;

		TodayDecor(CalendarDay day) {
			this.day = day;
		}

		@Override
		public boolean shouldDecorate(CalendarDay day) {
			return day.equals(this.day);
		}

		@Override
		public void decorate(DayViewFacade view) {
			view.addSpan(new ForegroundColorSpan(Color.parseColor(selDrawerColor)));
			Drawable drawable = getResources().getDrawable(R.drawable.cube);
			drawable.setColorFilter(Color.parseColor(selToolColor), PorterDuff.Mode.ADD);
			view.setSelectionDrawable(drawable);
		}
	}
}
