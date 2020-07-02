package com.aaptrix.activitys.student;

import com.aaptrix.adaptor.FullScrAdapter;
import com.aaptrix.adaptor.GalleryDetailAdapter;
import com.aaptrix.R;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

import static com.aaptrix.tools.HttpUrl.REMOVE_GALLERY;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class GalleryDetail extends AppCompatActivity {
	
	Intent intent;
	GridView gridView;
	ViewPager pager;
	ImageButton closeBtn;
	String title, id;
	String[] image;
	ArrayList<String> galleryArray = new ArrayList<>();
	FullScrAdapter adapter;
	AppBarLayout appBarLayout;
	TextView tool_title;
	LinearLayout add_layout;
	SharedPreferences sp;
	ImageView addGallery;
	String selToolColor, selStatusColor, selTextColor1, userrType, schoolId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery_detail);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		gridView = findViewById(R.id.gallery_gridview);
		Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		appBarLayout = findViewById(R.id.appBarLayout);
		tool_title = findViewById(R.id.tool_title);
		pager = findViewById(R.id.gallery_viewpager);
		closeBtn = findViewById(R.id.close_btn);
		intent = getIntent();
		add_layout = findViewById(R.id.add_layout);
		addGallery = findViewById(R.id.add_gallery);
		title = intent.getStringExtra("title");
		tool_title.setText(title);
		image = intent.getStringArrayExtra("images");
		id = intent.getStringExtra("id");
		closeBtn.setVisibility(View.GONE);
		SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
		selToolColor = settingsColor.getString("tool", "");
		selStatusColor = settingsColor.getString("status", "");
		selTextColor1 = settingsColor.getString("text1", "");

		StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
		StrictMode.setVmPolicy(builder.build());
		
		sp = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		userrType = sp.getString("userrType", "");
		schoolId = sp.getString("str_school_id", "");
		
		appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.parseColor(selStatusColor));
		}
		tool_title.setTextColor(Color.parseColor(selTextColor1));
		gridItem(image);
	}
	
	private void gridItem(String[] image) {
		for (String aStrUrl : image) {
			galleryArray.add(aStrUrl.replace("[", "").replace("]", "")
					.replace("\"", "").replace("\\","").replace(" ", ""));
		}
		GalleryDetailAdapter galleryAdapter = new GalleryDetailAdapter(this, R.layout.image_gallery, galleryArray);
		gridView.setAdapter(galleryAdapter);
		galleryAdapter.notifyDataSetChanged();
		galleryAdapter.notifyDataSetInvalidated();
		gridView.setEnabled(true);
		
		gridView.setOnItemClickListener((parent, view, position, id) -> {
			pager.setVisibility(View.VISIBLE);
			closeBtn.setVisibility(View.VISIBLE);
			adapter = new FullScrAdapter(this, galleryArray);
			pager.setAdapter(adapter);
			pager.setCurrentItem(position);
			adapter.notifyDataSetChanged();
			add_layout.setVisibility(View.VISIBLE);
			add_layout.bringToFront();
		});

		addGallery.setOnClickListener(v -> {
			String url = sp.getString("imageUrl", "") + sp.getString("userSchoolId", "") + "/gallery/" + galleryArray.get(pager.getCurrentItem());
			if (PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
				Picasso.with(this)
						.load(url)
						.into(new Target() {
							@Override
							public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
								Intent sharingIntent = new Intent(Intent.ACTION_SEND);
								sharingIntent.setType("image/jpeg");
								sharingIntent.putExtra(Intent.EXTRA_STREAM, getBitmapUri(bitmap));
								startActivity(Intent.createChooser(sharingIntent, "Share via..."));
							}

							@Override
							public void onBitmapFailed(Drawable errorDrawable) {

							}

							@Override
							public void onPrepareLoad(Drawable placeHolderDrawable) { }
						});
			} else {
				isPermissionGranted();
			}
		});
		
		closeBtn.setOnClickListener(v -> {
			pager.setVisibility(View.GONE);
			closeBtn.setVisibility(View.GONE);
			add_layout.setVisibility(View.GONE);
		});
	}

	private Uri getBitmapUri(Bitmap bitmap) {
		Uri bitmapUri = null;
		File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/" + R.string.app_name);
		boolean success = true;
		if(!storageDir.exists()){
			success = storageDir.mkdirs();
		}
		if(success) {
			File imageFile = new File(storageDir, "temp.jpg");
			try {
				OutputStream fOut = new FileOutputStream(imageFile);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fOut);
				fOut.close();
				bitmapUri = Uri.fromFile(imageFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bitmapUri;
	}

	public void isPermissionGranted() {
		ActivityCompat.requestPermissions(this,
				new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		} else if (item.getItemId() == R.id.delete) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.DialogTheme);
			alert.setTitle("Are you sure you want to delete").setPositiveButton("Yes", (dialog, which) -> {
				RemoveGallery removeGallery = new RemoveGallery(this);
				removeGallery.execute(id, schoolId);
			}).setNegativeButton("No", null);
			AlertDialog alertDialog = alert.create();
			alertDialog.setCancelable(false);
			alertDialog.show();
			Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
			Button theButton1 = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
			theButton.setTextColor(Color.parseColor(selToolColor));
			theButton1.setTextColor(Color.parseColor(selToolColor));
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (userrType.equals("Admin")) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.delete_menu, menu);
			return true;
		} else {
			return true;
		}
	}
	
	@SuppressLint("StaticFieldLeak")
	public class RemoveGallery extends AsyncTask<String, String, String> {
		Context ctx;
		
		RemoveGallery(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			String gallery_id = params[0];
			String schoolId = params[1];
			String data;
			
			try {
				URL url = new URL(REMOVE_GALLERY);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				
				OutputStream outputStream = httpURLConnection.getOutputStream();
				
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
				data = URLEncoder.encode("galleryId", "UTF-8") + "=" + URLEncoder.encode(gallery_id, "UTF-8") + "&" +
						URLEncoder.encode("schoolId", "UTF-8") + "=" + URLEncoder.encode(schoolId, "UTF-8");
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
			super.onPostExecute(result);
			if (result.contains("success") || result.contains("true")) {
				startActivity(new Intent(ctx, GalleryActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				finish();
			} else {
				Toast.makeText(ctx, "Error in deleting images", Toast.LENGTH_SHORT).show();
			}
		}
		
	}
}
