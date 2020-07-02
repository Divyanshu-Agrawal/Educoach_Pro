package com.aaptrix.activitys.admin;

import com.aaptrix.activitys.student.HomeworkActivity;
import com.aaptrix.adaptor.GridImageAdapter;
import com.aaptrix.R;
import com.aaptrix.tools.FileUtil;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
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
import id.zelory.compressor.Compressor;
import pl.droidsonroids.gif.GifImageView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import javax.net.ssl.SSLContext;

import static com.aaptrix.tools.HttpUrl.LIST_OF_SUBJECTS;
import static com.aaptrix.tools.HttpUrl.TEACHER_ADD_HOMEWORK;
import static com.aaptrix.tools.HttpUrl.TEACHER_UPDATE_HOMEWORK;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;
import static cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;

public class AddNewHomework extends AppCompatActivity {
	
	ArrayList<File> imageArray = new ArrayList<>();
	String file_extn;
	ArrayList<String> filepath = new ArrayList<>();
	ArrayList<Uri> image = new ArrayList<>();
	GridView gridView;
	ImageButton imageView;
	Toolbar toolbar;
	AppBarLayout appBarLayout;
	EditText title, description, date;
	Spinner subject;
	Button save;
	String type, strTitle, strDesc, strDate, strImage, strId, batch;
	Uri addImageUri = Uri.parse("android.resource://com.aaptrix/drawable/add_image");
	InputStream stream;
	String selToolColor, selStatusColor, selTextColor1, userSchoolId, userId, userClass;
	String[] oldImage;
	ArrayList<String> sendOldImage = new ArrayList<>();
	String[] subject_array = {"Select Subject"};
	String[] state_id = {"0"};
	String sel_subject;
	RelativeLayout layout;
	MediaPlayer mp;
	CardView cardView;
	GifImageView taskStatus;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_new_homework);
		gridView = findViewById(R.id.choose_img_grid);
		imageView = findViewById(R.id.sel_image);
		toolbar = findViewById(R.id.toolbar);
		appBarLayout = findViewById(R.id.appBarLayout);
		setSupportActionBar(toolbar);
		setTitle("Add Assignment");
		setResult(RESULT_OK);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		SharedPreferences sp_user = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		userSchoolId = sp_user.getString("str_school_id", "");
		userClass = sp_user.getString("userClass", "");
		userId = sp_user.getString("userID", "");
		type = getIntent().getStringExtra("type");
		batch = getIntent().getStringExtra("batch");
		mp = MediaPlayer.create(this, R.raw.button_click);
		cardView = findViewById(R.id.card_view);
		layout = findViewById(R.id.layout);
		taskStatus = findViewById(R.id.task_status);
		title = findViewById(R.id.hw_title);
		description = findViewById(R.id.hw_desc);
		date = findViewById(R.id.hw_date);
		save = findViewById(R.id.save_btn);
		subject = findViewById(R.id.hw_subject);
		ListOfSubjects listOfSubjects = new ListOfSubjects(this);
		listOfSubjects.execute(userSchoolId, userClass, batch);

		title.setOnFocusChangeListener((v, hasFocus) -> {
			if (!hasFocus) {
				hideKeyboard(v);
			}
		});

		description.setOnFocusChangeListener((v, hasFocus) -> {
			if (!hasFocus) {
				hideKeyboard(v);
			}
		});

		date.setOnFocusChangeListener((v, hasFocus) -> {
			if (!hasFocus) {
				hideKeyboard(v);
			}
		});
		
		SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
		selToolColor = settingsColor.getString("tool", "");
		selStatusColor = settingsColor.getString("status", "");
		selTextColor1 = settingsColor.getString("text1", "");
		appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
		save.setBackgroundColor(Color.parseColor(selToolColor));
		save.setTextColor(Color.parseColor(selTextColor1));
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.parseColor(selStatusColor));
		}
		
		try {
			stream = getContentResolver().openInputStream(addImageUri);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		if (type.equals("update")) {
			setTitle("Update Assignment");
			strTitle = getIntent().getStringExtra("title");
			strDesc = getIntent().getStringExtra("description");
			strDate = getIntent().getStringExtra("date");
			strImage = getIntent().getStringExtra("image");
			strId = getIntent().getStringExtra("id");
			sel_subject = getIntent().getStringExtra("subject");
			
			title.setText(strTitle);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				description.setText(Html.fromHtml(strDesc, Html.FROM_HTML_MODE_COMPACT));
			} else {
				description.setText(Html.fromHtml(strDesc));
			}
			date.setText(strDate);
			save.setText("Update");

			
			oldImage = strImage.split(",");
			for (String anOldImage : oldImage) {
				sendOldImage.add(anOldImage.replace("\"", "").replace("[", "").replace("]", ""));
			}
			
			for (int i = 0; i < sendOldImage.size(); i++) {
				Uri uri = Uri.parse(sp_user.getString("imageUrl", "") + sp_user.getString("userSchoolId", "") + "/homework/" + sendOldImage.get(i));
				image.add(uri);
			}
			if (image.size() != 4) {
				image.add(addImageUri);
			}
			GridImageAdapter addAdapter = new GridImageAdapter(this, R.layout.image_add_grid, image);
			gridView.setAdapter(addAdapter);
			imageView.setVisibility(View.GONE);
			addAdapter.notifyDataSetChanged();
		}
		
		date.setOnClickListener(v -> {
			final Calendar mcurrentDate = Calendar.getInstance();
			int mYear = mcurrentDate.get(Calendar.YEAR);
			int mMonth = mcurrentDate.get(Calendar.MONTH);
			int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
			DatePickerDialog mDatePicker = new DatePickerDialog(
					this, R.style.AlertDialogCustom1, (datepicker, selectedyear, selectedmonth, selectedday) -> {
						mcurrentDate.set(Calendar.YEAR, selectedyear);
						mcurrentDate.set(Calendar.MONTH, selectedmonth);
						mcurrentDate.set(Calendar.DAY_OF_MONTH, selectedday);
						SimpleDateFormat sdf = new SimpleDateFormat(getResources().getString(R.string.date_card_formate_dairy5), Locale.US);
						date.setText(sdf.format(mcurrentDate.getTime()));
					}, mYear, mMonth, mDay);
			mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis());
			mDatePicker.show();
		});
		
		imageView.bringToFront();
		imageView.setOnClickListener(v -> {
			if (PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
				Intent photoPickerIntent = new Intent();
				photoPickerIntent.setType("image/*");
				photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
				photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(photoPickerIntent, 1);
			} else {
				isPermissionGranted();
			}
		});

		layout.setOnClickListener(v -> {

		});

		layout.setOnTouchListener((v, event) -> false);

		gridView.setOnItemClickListener((parent, view, position, id) -> {
			image.remove(addImageUri);
			if (PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
				Intent photoPickerIntent = new Intent();
				photoPickerIntent.setType("image/*");
				photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
				photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(photoPickerIntent, 1);
			} else {
				isPermissionGranted();
			}
		});
		
		save.setOnClickListener(v -> {
			mp.start();
			if (!TextUtils.isEmpty(title.getText().toString())) {
				if (!TextUtils.isEmpty(sel_subject) || !sel_subject.equals("Select Subject")) {
					if (!TextUtils.isEmpty(date.getText().toString())) {
						layout.setVisibility(View.VISIBLE);
						layout.bringToFront();
						if (type.equals("add")) {
							UploadHomework uploadHomework = new UploadHomework(this);
							uploadHomework.execute(userId, title.getText().toString(), sel_subject, date.getText().toString(),
									batch, userSchoolId, description.getText().toString());
						} else {
							UpdateHomework updateHomework = new UpdateHomework(this);
							updateHomework.execute(userId, title.getText().toString(), sel_subject,
									date.getText().toString(), strId, description.getText().toString());
						}
					} else {
						Toast.makeText(this, "Please enter Date", Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(this, "Please select subject", Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(this, "Please enter Title", Toast.LENGTH_SHORT).show();
			}
		});
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
	
	@SuppressLint("StaticFieldLeak")
	public class ListOfSubjects extends AsyncTask<String, String, String> {
		Context ctx;
		
		ListOfSubjects(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			
			String schoolId = params[0];
			String userClass = params[1];
			String userSection = params[2];
			
			try {

				SSLContext sslContext = SSLContexts.custom().useTLS().build();
				SSLConnectionSocketFactory f = new SSLConnectionSocketFactory(
						sslContext,
						new String[]{"TLSv1.1", "TLSv1.2"},
						null,
						BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
				HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(f).build();
				HttpPost httppost = new HttpPost(LIST_OF_SUBJECTS);
				MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
				entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
				entityBuilder.addTextBody("schoolId", schoolId);
				entityBuilder.addTextBody("class", userClass);
				entityBuilder.addTextBody("section", userSection);
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
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			try {
				JSONObject jo = new JSONObject(result);
				JSONArray ja = jo.getJSONArray("result");
				subject_array = new String[ja.length() + 1];
				state_id = new String[ja.length() + 1];
				subject_array[0] = "Select Subject";
				state_id[0] = "0";
				for (int i = 0; i < ja.length(); i++) {
					jo = ja.getJSONObject(i);
					subject_array[i + 1] = jo.getString("tbl_batch_subjct_name");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			AllAdaptors();
			super.onPostExecute(result);
		}
	}
	
	private void AllAdaptors() {
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_list_item1, subject_array);
		dataAdapter.setDropDownViewResource(R.layout.spinner_list_item1);
		subject.setAdapter(dataAdapter);
		subject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				sel_subject = subject_array[i];
				((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.text_gray));
			}
			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
			
			}
		});
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1)
			if (resultCode == Activity.RESULT_OK) {
				ClipData clipData = data.getClipData();
				if (clipData != null) {
					if (filepath.size() > 4) {
						Toast.makeText(this, "Please select upto only 4 images", Toast.LENGTH_SHORT).show();
					} else if (clipData.getItemCount() > 4) {
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
									imageArray.add(new Compressor(this)
											.setMaxWidth(1280)
											.setMaxHeight(720)
											.setQuality(25)
											.setCompressFormat(Bitmap.CompressFormat.JPEG)
											.compressToFile(FileUtil.from(this, image.get(i))));
								} else {
									FileNotFoundException fe = new FileNotFoundException();
									Toast.makeText(this, "File not in required format.", Toast.LENGTH_SHORT).show();
									throw fe;
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
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
					if (filepath.size() > 4) {
						Toast.makeText(this, "Please select upto only 4 images", Toast.LENGTH_SHORT).show();
					} else {
						image.add(data.getData());
						for (int i = 0; i < image.size(); i++) {
							filepath.add(FileUtil.getFileName(this, image.get(i)));
							file_extn = filepath.get(i).substring(filepath.get(i).lastIndexOf(".") + 1);
							try {
								if (file_extn.equals("jpg") || file_extn.equals("jpeg") || file_extn.equals("png")) {
									imageArray.add(new Compressor(this)
											.setMaxWidth(1280)
											.setMaxHeight(720)
											.setQuality(25)
											.setCompressFormat(Bitmap.CompressFormat.JPEG)
											.compressToFile(FileUtil.from(this, image.get(i))));
								} else {
									FileNotFoundException fe = new FileNotFoundException();
									Toast.makeText(this, "File not in required format.", Toast.LENGTH_SHORT).show();
									throw fe;
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
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
	}

	public void isPermissionGranted() {
		ActivityCompat.requestPermissions(this,
				new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
	}
	
	
	@Override
	public void onRequestPermissionsResult(int requestCode,
										   @NonNull String permissions[], @NonNull int[] grantResults) {
		switch (requestCode) {
			case 1: {
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
				}
				
			}
		}
	}
	
	@SuppressLint("StaticFieldLeak")
	public class UploadHomework extends AsyncTask<String, String, String> {
		Context ctx;
		
		UploadHomework(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			Toast.makeText(ctx, "Please wait, adding assignment entry", Toast.LENGTH_SHORT).show();
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			String branch_url = TEACHER_ADD_HOMEWORK;
			
			String userId = params[0];
			String str_title = params[1];
			String str_subject = params[2];
			String str_date = params[3];
			String selBatch = params[4];
			String schoolId = params[5];
			String dDetails = params[6];
			String notiImage = "0";
			
			try {
				ArrayList<String> imageNames = new ArrayList<>();
				for (int i = 0; i < imageArray.size(); i++) {
					try {
						SSLContext sslContext = SSLContexts.custom().useTLS().build();
						SSLConnectionSocketFactory f = new SSLConnectionSocketFactory(
								sslContext,
								new String[]{"TLSv1.1", "TLSv1.2"},
								null,
								BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
						HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(f).build();
						HttpPost httppost = new HttpPost(branch_url);
						MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
						entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
						FileBody image = new FileBody(imageArray.get(i));
						entityBuilder.addPart("image", image);
						entityBuilder.addTextBody("schoolId", userSchoolId);
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
					HttpPost httppost = new HttpPost(branch_url);
					MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
					entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
					entityBuilder.addTextBody("str_img", imageNames.toString().replace(" ", ""));
					entityBuilder.addTextBody("userId", userId);
					entityBuilder.addTextBody("str_title", str_title);
					entityBuilder.addTextBody("str_subject", str_subject);
					entityBuilder.addTextBody("str_date", str_date);
					entityBuilder.addTextBody("str_desc", dDetails);
					entityBuilder.addTextBody("schoolId", schoolId);
					entityBuilder.addTextBody("selBatch", selBatch);
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
			Log.d("ADDED", "" + result);
			if (result.contains("submitted")) {
				cardView.setVisibility(View.VISIBLE);
				new CountDownTimer(4000, 1000) {
					@Override
					public void onTick(long millisUntilFinished) {

					}

					@Override
					public void onFinish() {
						startActivity(new Intent(ctx, HomeworkActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
					}
				}.start();
			} else {
				Toast.makeText(ctx, "Server issues", Toast.LENGTH_SHORT).show();
				layout.setVisibility(View.GONE);
			}
			super.onPostExecute(result);
		}
		
	}
	
	@SuppressLint("StaticFieldLeak")
	public class UpdateHomework extends AsyncTask<String, String, String> {
		Context ctx;
		
		UpdateHomework(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			Toast.makeText(ctx, "Please wait assignment updating", Toast.LENGTH_SHORT).show();
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			String userId=params[0];
			String str_title=params[1];
			String str_subject=params[2];
			String str_date=params[3];
			String homeworkId=params[4];
			String str_desc=params[5];
			String data;
			
			try {
				URL url = new URL(TEACHER_UPDATE_HOMEWORK);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				OutputStream outputStream = httpURLConnection.getOutputStream();
				
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
				data = URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8")+ "&" +
						URLEncoder.encode("str_title", "UTF-8") + "=" + URLEncoder.encode(str_title, "UTF-8")+ "&" +
						URLEncoder.encode("str_desc", "UTF-8") + "=" + URLEncoder.encode(str_desc, "UTF-8")+ "&" +
						URLEncoder.encode("str_date", "UTF-8") + "=" + URLEncoder.encode(str_date, "UTF-8")+ "&" +
						URLEncoder.encode("homeworkId", "UTF-8") + "=" + URLEncoder.encode(homeworkId, "UTF-8")+ "&" +
						URLEncoder.encode("str_subject", "UTF-8") + "=" + URLEncoder.encode(str_subject, "UTF-8");
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
			Log.d("Json",""+result);
			if (result.contains("submitted")) {
				cardView.setVisibility(View.VISIBLE);
				new CountDownTimer(4000, 1000) {
					@Override
					public void onTick(long millisUntilFinished) {

					}

					@Override
					public void onFinish() {
						startActivity(new Intent(ctx, HomeworkActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
					}
				}.start();
			} else {
				Toast.makeText(ctx, "Server issues", Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		}
		return true;
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}
