package com.aaptrix.activitys;

import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;

import com.google.android.material.appbar.AppBarLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Objects;

import com.aaptrix.R;

/**
 * Created by Administrator on 11/14/2017.
 */

public class TermsConditionsActivity extends AppCompatActivity {
	
	AppBarLayout appBarLayout;
	LinearLayout loader;
	WebView webview;
	TextView tool;
	String url, tool_title;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.terms_condition_layout);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		appBarLayout = findViewById(R.id.appBarLayout);
		
		loader = findViewById(R.id.loader);
		tool = findViewById(R.id.tool);
		webview = findViewById(R.id.webview);
		loader.setVisibility(View.VISIBLE);
		url = getIntent().getStringExtra("url");
		tool_title = getIntent().getStringExtra("tool_title");
		tool.setText(tool_title);
		Handler handler2 = new Handler();
		handler2.postDelayed(() -> loader.setVisibility(View.GONE), 2000);
		
		webview.setWebViewClient(new MyBrowser());
		webview.getSettings().setLoadsImagesAutomatically(true);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		webview.loadUrl(url);
		
	}
	
	private class MyBrowser extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
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
		finish();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}
}
