package com.aaptrix.activitys.admin;

import android.content.SharedPreferences;

import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.appbar.AppBarLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Objects;

import com.aaptrix.fragments.ClassTimeTableFragment;
import com.aaptrix.fragments.ExamTimeTableFragment;
import com.aaptrix.R;
import androidx.fragment.app.FragmentManager;

import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

/**
 * Created by Administrator on 11/29/2017.
 */

public class AdminTimeTableActivity extends AppCompatActivity {
	
	SharedPreferences.Editor editor;
	String userId, userImg, userName, userPassword, userSchoolLogo, numberOfUser, str_section;
	AppBarLayout appBarLayout;
	SharedPreferences.Editor editorColor;
	String selToolColor, selDrawerColor, selStatusColor, selTextColor1, selTextColor2;
	TextView tool_title;
	String selBatch;
	TextView class_tt, exam_tt;
	LinearLayout llTab, layout2, layout1, layLine;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.admin_time_table_layout);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		appBarLayout = findViewById(R.id.appBarLayout);
		llTab = findViewById(R.id.llTab);
		layout1 = findViewById(R.id.layout1);
		layout2 = findViewById(R.id.layout2);
		layLine = findViewById(R.id.layLine);
		tool_title = findViewById(R.id.tool_title);
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		editor = settings.edit();
		userId = settings.getString("userID", "");
		userSchoolLogo = settings.getString("userSchoolLogo", "");
		userImg = settings.getString("userImg", "");
		userName = settings.getString("userName", "");
		str_section = settings.getString("userSection", "");

//color
		SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
		editorColor = settingsColor.edit();
		selToolColor = settingsColor.getString("tool", "");
		selDrawerColor = settingsColor.getString("drawer", "");
		selStatusColor = settingsColor.getString("status", "");
		selTextColor1 = settingsColor.getString("text1", "");
		selTextColor2 = settingsColor.getString("text2", "");
		
		selBatch = getIntent().getStringExtra("selBatch");
		
		class_tt = findViewById(R.id.class_tt);
		exam_tt = findViewById(R.id.exam_tt);
		
		Bundle bundle = new Bundle();
		bundle.putString("selBatch", selBatch);
		bundle.putString("loc", "");
		FragmentManager fm = getSupportFragmentManager();
		ClassTimeTableFragment fragment = new ClassTimeTableFragment();
		fragment.setArguments(bundle);
		fm.beginTransaction().add(R.id.ttFrame, fragment).commit();
		exam_tt.setTextColor(Color.WHITE);
		class_tt.setTextColor(Color.parseColor(selToolColor));
		layout1.setBackgroundColor(Color.WHITE);
		
		class_tt.setOnClickListener(view -> {
			exam_tt.setTextColor(Color.WHITE);
			class_tt.setTextColor(Color.parseColor(selToolColor));
			layout2.setBackgroundColor(Color.parseColor(selToolColor));
			layout1.setBackgroundColor(Color.WHITE);
			Bundle bundle1 = new Bundle();
			bundle1.putString("selBatch", selBatch);
			bundle1.putString("loc", "");
			FragmentManager fm1 = getSupportFragmentManager();
			ClassTimeTableFragment fragment1 = new ClassTimeTableFragment();
			fragment1.setArguments(bundle1);
			fm1.beginTransaction().add(R.id.ttFrame, fragment1).commit();
		});
		exam_tt.setOnClickListener(view -> {
			class_tt.setTextColor(Color.WHITE);
			exam_tt.setTextColor(Color.parseColor(selToolColor));
			layout2.setBackgroundColor(Color.WHITE);
			layout1.setBackgroundColor(Color.parseColor(selToolColor));
			
			Bundle bundle12 = new Bundle();
			bundle12.putString("selBatch", selBatch);
			FragmentManager fm12 = getSupportFragmentManager();
			ExamTimeTableFragment fragment12 = new ExamTimeTableFragment();
			fragment12.setArguments(bundle12);
			fm12.beginTransaction().add(R.id.ttFrame, fragment12).commit();
		});
		appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.parseColor(selStatusColor));
			llTab.setBackgroundColor(Color.parseColor(selToolColor));
		}
		tool_title.setTextColor(Color.parseColor(selTextColor1));
		layLine.setBackgroundColor(Color.parseColor(selToolColor));
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
	
}
