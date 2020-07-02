package com.aaptrix.activitys.admin;

import com.aaptrix.activitys.teacher.TeacherDairyActivity;
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

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;

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
import java.util.Date;
import java.util.Objects;

import javax.net.ssl.SSLContext;

import static com.aaptrix.tools.HttpUrl.TEACHER_ADD_DAIRY;
import static com.aaptrix.tools.HttpUrl.TEACHER_UPDATE_DAIRY;
import static com.aaptrix.tools.SPClass.PREFS_DAIRY;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;
import static cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;

public class AddNewDiary extends AppCompatActivity {
	
	ArrayList<File> imageArray = new ArrayList<>();
	String file_extn;
	ArrayList<String> filepath = new ArrayList<>();
	ArrayList<Uri> image = new ArrayList<>();
	GridView gridView;
	ImageButton imageView;
	Toolbar toolbar;
	RelativeLayout layout;
	AppBarLayout appBarLayout;
	EditText title, description, reportedBy;
	ProgressBar progressBar;
	Button save;
	String type, strTitle, strDesc, strDate, strImage, strId, strReported, batch;
	Uri addImageUri = Uri.parse("android.resource://com.aaptrix/drawable/add_image");
	InputStream stream;
	String selToolColor, selStatusColor, selTextColor1, userSchoolId;
	String[] oldImage;
	ArrayList<String> sendOldImage = new ArrayList<>();
	MediaPlayer mp;
	CardView cardView;
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_new_diary);
		gridView = findViewById(R.id.choose_img_grid);
		imageView = findViewById(R.id.sel_image);
		toolbar = findViewById(R.id.toolbar);
		appBarLayout = findViewById(R.id.appBarLayout);
		setSupportActionBar(toolbar);
		setTitle("Add Remarks");
		setResult(RESULT_OK);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		type = getIntent().getStringExtra("type");
		batch = getIntent().getStringExtra("batch");
		cardView = findViewById(R.id.card_view);
		title = findViewById(R.id.tv_dairy_title);
		layout = findViewById(R.id.layout);
		description = findViewById(R.id.tv_dairy_details);
		reportedBy = findViewById(R.id.tv_dairy_reprtedBy);
		progressBar = findViewById(R.id.loader);
		save = findViewById(R.id.save_btn);
		mp = MediaPlayer.create(this, R.raw.button_click);

		title.setOnFocusChangeListener((v, hasFocus) -> {
			if (!hasFocus)
				hideKeyboard(v);
		});

		description.setOnFocusChangeListener((v, hasFocus) -> {
			if (!hasFocus)
				hideKeyboard(v);
		});

		SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
		selToolColor = settingsColor.getString("tool", "");
		selStatusColor = settingsColor.getString("status", "");
		selTextColor1 = settingsColor.getString("text1", "");
		
		SharedPreferences sp_user = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		userSchoolId = sp_user.getString("str_school_id", "");
		appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
		save.setBackgroundColor(Color.parseColor(selToolColor));
		save.setTextColor(Color.parseColor(selTextColor1));

		reportedBy.setText(sp_user.getString("userName", ""));
		reportedBy.setFocusable(false);

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
			setTitle("Update Remarks");
			strTitle = getIntent().getStringExtra("title");
			strDesc = getIntent().getStringExtra("description");
			strDate = getIntent().getStringExtra("date");
			strImage = getIntent().getStringExtra("image");
			strId = getIntent().getStringExtra("id");
			strReported = getIntent().getStringExtra("reportedBy");
			reportedBy.setText(strReported);
			reportedBy.setFocusable(false);

			reportedBy.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
				@Override
				public boolean onCreateActionMode(ActionMode mode, Menu menu) {
					return false;
				}

				@Override
				public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
					return false;
				}

				@Override
				public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
					return false;
				}

				@Override
				public void onDestroyActionMode(ActionMode mode) {

				}
			});
			reportedBy.setLongClickable(false);
			reportedBy.setTextIsSelectable(false);

			title.setText(strTitle);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				description.setText(Html.fromHtml(strDesc, Html.FROM_HTML_MODE_COMPACT));
			} else {
				description.setText(Html.fromHtml(strDesc));
			}
			
			save.setText("Update");
			imageView.setVisibility(View.GONE);
			gridView.setEnabled(false);
			oldImage = strImage.split(",");
			for (String anOldImage : oldImage) {
				sendOldImage.add(anOldImage.replace("\"", "").replace("[", "").replace("]", ""));
			}
			for (int i = 0; i < sendOldImage.size(); i++) {
				Uri uri = Uri.parse(sp_user.getString("imageUrl", "") + sp_user.getString("userSchoolId", "") + "/diary/" + sendOldImage.get(i));
				image.add(uri);
			}
			GridImageAdapter addAdapter = new GridImageAdapter(this, R.layout.image_add_grid, image);
			gridView.setAdapter(addAdapter);
			imageView.setVisibility(View.GONE);
			addAdapter.notifyDataSetChanged();
		}
		
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
		
		gridView.setOnItemClickListener((parent, view, position, id) -> {
//			image.clear();
//			filepath.clear();
//			imageArray.clear();
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

		layout.setOnClickListener(v -> {

		});

		layout.setOnTouchListener((v, event) -> false);
		
		save.setOnClickListener(v -> {
			mp.start();
			save.setEnabled(false);
			if (!TextUtils.isEmpty(title.getText().toString())) {
				if (!TextUtils.isEmpty(description.getText().toString())) {
					layout.setVisibility(View.VISIBLE);
					layout.bringToFront();
					if (type.equals("add")) {
						Date c1 = Calendar.getInstance().getTime();
						SimpleDateFormat df = new SimpleDateFormat(getResources().getString(R.string.date_card_formate_dairy1));
						String formattedDate = df.format(c1);
						SharedPreferences sp = getSharedPreferences(PREFS_DAIRY, 0);
						String studentArray = sp.getString("studentArray", "");
						UploadDiary uploadDiary = new UploadDiary(this);
						uploadDiary.execute(sp_user.getString("userID", ""), title.getText().toString(),
								reportedBy.getText().toString(), formattedDate, batch, userSchoolId, studentArray, description.getText().toString());
					} else {
						UpdateDiary updateDiary = new UpdateDiary(this);
						updateDiary.execute(sp_user.getString("userSchoolId", ""), title.getText().toString(),
								description.getText().toString(), strDate, strId);
					}
				} else {
					Toast.makeText(this, "Please enter Description", Toast.LENGTH_SHORT).show();
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
	public class UploadDiary extends AsyncTask<String, String, String> {
		Context ctx;
		
		UploadDiary(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			Toast.makeText(ctx, "Please wait, adding remarks entry", Toast.LENGTH_SHORT).show();
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			String branch_url = TEACHER_ADD_DAIRY;
			
			String userId = params[0];
			String str_title = params[1];
			String reportedBy = params[2];
			String str_date = params[3];
			String selBatch = params[4];
			String schoolId = params[5];
			String stdId = params[6];
			String dairyDetails = params[7];
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
					entityBuilder.addTextBody("str_desc", dairyDetails);
					entityBuilder.addTextBody("str_date", str_date);
					entityBuilder.addTextBody("selBatch", selBatch);
					entityBuilder.addTextBody("schoolId", schoolId);
					entityBuilder.addTextBody("student_array", stdId);
					entityBuilder.addTextBody("reportedBy", reportedBy);
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
			Log.e("ADDED", result);
			if (result.contains("submitted")) {
				cardView.setVisibility(View.VISIBLE);
				new CountDownTimer(4000, 1000) {
					@Override
					public void onTick(long millisUntilFinished) {

					}

					@Override
					public void onFinish() {
						startActivity(new Intent(ctx, TeacherDairyActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
					}
				}.start();
			} else {
				layout.setVisibility(View.GONE);
				Toast.makeText(ctx, "Server issues", Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}
	}
	
	@SuppressLint("StaticFieldLeak")
	public class UpdateDiary extends AsyncTask<String, String, String> {
		Context ctx;
		
		UpdateDiary(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			Toast.makeText(ctx, "Please wait remarks updating", Toast.LENGTH_SHORT).show();
			super.onPreExecute();
			
		}
		
		@Override
		protected String doInBackground(String... params) {
			String schoolId = params[0];
			String str_title = params[1];
			String str_desc = params[2];
			String str_date = params[3];
			String diaryId = params[4];
			String data;
			
			try {
				
				URL url = new URL(TEACHER_UPDATE_DAIRY);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				
				OutputStream outputStream = httpURLConnection.getOutputStream();
				
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
				data = URLEncoder.encode("schoolId", "UTF-8") + "=" + URLEncoder.encode(schoolId, "UTF-8") + "&" +
						URLEncoder.encode("str_title", "UTF-8") + "=" + URLEncoder.encode(str_title, "UTF-8") + "&" +
						URLEncoder.encode("str_desc", "UTF-8") + "=" + URLEncoder.encode(str_desc, "UTF-8") + "&" +
						URLEncoder.encode("str_date", "UTF-8") + "=" + URLEncoder.encode(str_date, "UTF-8") + "&" +
						URLEncoder.encode("diaryId", "UTF-8") + "=" + URLEncoder.encode(diaryId, "UTF-8");
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
			Log.e("Json", "" + result);
			if (result.contains("submitted")) {
				cardView.setVisibility(View.VISIBLE);
				new CountDownTimer(4000, 1000) {
					@Override
					public void onTick(long millisUntilFinished) {

					}

					@Override
					public void onFinish() {
						startActivity(new Intent(ctx, TeacherDairyActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
					}
				}.start();
			} else {
				layout.setVisibility(View.GONE);
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
