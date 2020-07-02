package com.aaptrix.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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

import com.aaptrix.activitys.admin.UpdateClassTimeTable;
import com.aaptrix.adaptor.ClassTimeTableListAdaptor;
import com.aaptrix.databeans.DataBeanExamTt;
import com.aaptrix.R;

import static com.aaptrix.tools.HttpUrl.LIST_OF_CLASS_TIME_TABLE;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREFS_RW;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class ClassTimeTableFragment extends Fragment {
	
	private View view;
	private ArrayList<DataBeanExamTt> examTtArray = new ArrayList<>();
	private DataBeanExamTt dbe;
	private String examId;
	private String examDate;
	private String subjectName;
	private String subjectDetails;
	private String subjectExamDate;
	private ListView exam_list;
	private TextView no_class_table, todayDateTime;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private String schoolId;
	private String userSection, userType;
	private String currentDay;
	private Calendar c;
	private SimpleDateFormat df;
	private String selBatch;
	private LinearLayout header;
	private SharedPreferences sp_aboutUs;
	private static final String PREFS_ABOUTUS = "json_ett";
	private String loc;
	
	@SuppressLint({"SimpleDateFormat", "SetTextI18n"})
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		view = inflater.inflate(R.layout.fragment_class_time_table, container, false);
		
		exam_list = view.findViewById(R.id.exam_list);
		no_class_table = view.findViewById(R.id.no_class_table);
		todayDateTime = view.findViewById(R.id.todayDateTime);
		mSwipeRefreshLayout = view.findViewById(R.id.activity_main_swipe_refresh_layout);
		SharedPreferences settings = view.getContext().getSharedPreferences(PREFS_NAME, 0);
		schoolId = settings.getString("str_school_id", "");
		userSection = settings.getString("userSection", "");
		userType = settings.getString("userrType", "");
		ImageView previous = view.findViewById(R.id.ib_prev);
		FrameLayout previousF = view.findViewById(R.id.ib_prevF);
		FrameLayout nextF = view.findViewById(R.id.Ib_nextF);
		ImageView next = view.findViewById(R.id.Ib_next);
		header = view.findViewById(R.id.header);
		loc = getArguments().getString("loc");
		
		SharedPreferences settingsColor = Objects.requireNonNull(getActivity()).getSharedPreferences(PREF_COLOR, 0);
		String selDrawerColor = settingsColor.getString("drawer", "");
		
		if (userType.equals("Admin") || userType.equals("Teacher")) {
			assert getArguments() != null;
			selBatch = getArguments().getString("selBatch");
			c = Calendar.getInstance();
			df = new SimpleDateFormat("dd-MMM-yyyy");
			String formattedDate = df.format(c.getTime());
			final SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
			final Date d = new Date();
			Log.d("sdf", "" + d);
			mSwipeRefreshLayout.setRefreshing(false);
			mSwipeRefreshLayout.setEnabled(false);
			currentDay = sdf.format(d);
			todayDateTime.setText(formattedDate + ", " + currentDay);
			
			if (isInternetOn()) {
				GetAllClassTimeTable b1 = new GetAllClassTimeTable(view.getContext());
				b1.execute(schoolId, selBatch, currentDay);
			}

			mSwipeRefreshLayout.setOnRefreshListener(() -> {
				if (isInternetOn()) {
					examTtArray.clear();
					mSwipeRefreshLayout.setRefreshing(true);
					GetAllClassTimeTable b1 = new GetAllClassTimeTable(view.getContext());
					b1.execute(schoolId, selBatch, currentDay);
				}
			});
			
			previousF.setOnClickListener(v -> {
				c.add(Calendar.DAY_OF_YEAR, -1);
				Date tomorrow = c.getTime();
				currentDay = sdf.format(tomorrow);
				df = new SimpleDateFormat("dd-MMM-yyyy");
				String formattedDate1 = df.format(c.getTime());
				todayDateTime.setText(formattedDate1 + "," + currentDay);
				examTtArray.clear();
				GetAllClassTimeTable b1 = new GetAllClassTimeTable(view.getContext());
				b1.execute(schoolId, selBatch, currentDay);
			});
			
			nextF.setOnClickListener(v -> {
				c.add(Calendar.DAY_OF_YEAR, 1);
				Date tomorrow = c.getTime();
				currentDay = sdf.format(tomorrow);
				df = new SimpleDateFormat("dd-MMM-yyyy");
				String formattedDate12 = df.format(c.getTime());
				todayDateTime.setText(formattedDate12 + "," + currentDay);
				
				examTtArray.clear();
				GetAllClassTimeTable b1 = new GetAllClassTimeTable(view.getContext());
				b1.execute(schoolId, selBatch, currentDay);
			});
			
		} else {
			c = Calendar.getInstance();
			final SimpleDateFormat sdf = new SimpleDateFormat("EEEE");


			if (loc.equals("sidebarclass")) {
				c.add(Calendar.DAY_OF_YEAR, 1);
				Date tomorrow = c.getTime();
				currentDay = sdf.format(tomorrow);
				df = new SimpleDateFormat("dd-MMM-yyyy");
				String formattedDate = df.format(c.getTime());
				todayDateTime.setText(formattedDate + ", " + currentDay);
			} else {
				final Date d = new Date();
				currentDay = sdf.format(d);
				df = new SimpleDateFormat("dd-MMM-yyyy");
				String formattedDate = df.format(c.getTime());
				todayDateTime.setText(formattedDate + ", " + currentDay);
			}
			
			if (isInternetOn()) {
				GetAllClassTimeTable b1 = new GetAllClassTimeTable(view.getContext());
				b1.execute(schoolId, userSection, currentDay);
			} else {
				sp_aboutUs = view.getContext().getSharedPreferences(PREFS_ABOUTUS, 0);
				String aboutUs = sp_aboutUs.getString("json_ett", "");
				Log.d("aboutUs", aboutUs);
				getCTT(aboutUs);
				
			}
			previousF.setOnClickListener(v -> {
				if (isInternetOn()) {
					c.add(Calendar.DAY_OF_YEAR, -1);
					Date tomorrow = c.getTime();
					currentDay = sdf.format(tomorrow);
					df = new SimpleDateFormat("dd-MMM-yyyy");
					String formattedDate13 = df.format(c.getTime());
					todayDateTime.setText(formattedDate13 + "," + currentDay);
					examTtArray.clear();
					GetAllClassTimeTable b1 = new GetAllClassTimeTable(view.getContext());
					b1.execute(schoolId, userSection, currentDay);
				} else {
					Toast.makeText(view.getContext(), "No Network", Toast.LENGTH_SHORT).show();
					
				}
				
			});
			
			
			nextF.setOnClickListener(v -> {
				if (isInternetOn()) {
					
					c.add(Calendar.DAY_OF_YEAR, 1);
					Date tomorrow = c.getTime();
					currentDay = sdf.format(tomorrow);
					df = new SimpleDateFormat("dd-MMM-yyyy");
					String formattedDate14 = df.format(c.getTime());
					todayDateTime.setText(formattedDate14 + "," + currentDay);
					
					examTtArray.clear();
					GetAllClassTimeTable b1 = new GetAllClassTimeTable(view.getContext());
					b1.execute(schoolId, userSection, currentDay);
				} else {
					Toast.makeText(view.getContext(), "No Network", Toast.LENGTH_SHORT).show();
					
				}
			});
			
		}
		
		previous.setColorFilter(Color.parseColor(selDrawerColor));
		next.setColorFilter(Color.parseColor(selDrawerColor));
		return view;
	}
	
	//online
	@SuppressLint("StaticFieldLeak")
	public class GetAllClassTimeTable extends AsyncTask<String, String, String> {
		Context ctx;
		
		GetAllClassTimeTable(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			//loader.setVisibility(View.VISIBLE);
			mSwipeRefreshLayout.setRefreshing(true);
			
			super.onPreExecute();
			
		}
		
		@Override
		protected String doInBackground(String... params) {
			
			String schoolId = params[0];
			String sectionName = params[1];
			String currentDay = params[2];
			String data;
			
			try {
				
				URL url = new URL(LIST_OF_CLASS_TIME_TABLE);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				
				OutputStream outputStream = httpURLConnection.getOutputStream();
				
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
				data = URLEncoder.encode("schoolId", "UTF-8") + "=" + URLEncoder.encode(schoolId, "UTF-8") + "&" +
						URLEncoder.encode("section", "UTF-8") + "=" + URLEncoder.encode(sectionName, "UTF-8") + "&" +
						URLEncoder.encode("currentDay", "UTF-8") + "=" + URLEncoder.encode(currentDay, "UTF-8");
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
			//loader.setVisibility(View.GONE);
			mSwipeRefreshLayout.setRefreshing(false);
			sp_aboutUs = ctx.getSharedPreferences(PREFS_ABOUTUS, 0);
			SharedPreferences.Editor se_aboutUs = sp_aboutUs.edit();
			se_aboutUs.clear();
			se_aboutUs.putString("json_ett", result);
			se_aboutUs.apply();
			if (result != null && result.equals("{\"result\":null}")) {
				exam_list.setVisibility(View.GONE);
				//header.setVisibility(View.GONE);
				no_class_table.setVisibility(View.VISIBLE);
				examTtArray.clear();
			} else {
				no_class_table.setVisibility(View.GONE);
				exam_list.setVisibility(View.VISIBLE);
				header.setVisibility(View.VISIBLE);
				try {
					JSONObject jsonRootObject = new JSONObject(result);
					JSONArray jsonArray = jsonRootObject.getJSONArray("result");
					JSONArray teacherNames = jsonRootObject.getJSONArray("teacherList");
					SharedPreferences sp = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
					sp.edit().putString("teacherArray", teacherNames.toString()).apply();
					examTtArray.clear();
					for (int i = 0; i < jsonArray.length(); i++) {
						dbe = new DataBeanExamTt();
						
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						examId = jsonObject.getString("tbl_class_tt_period_id");
						examDate = jsonObject.getString("tbl_class_tt_teacher_name");
						subjectName = jsonObject.getString("tbl_class_tt_subject_name");
						subjectDetails = jsonObject.getString("tbl_class_tt_subject_details");
						subjectExamDate = jsonObject.getString("tbl_class_tt_current_date");
						dbe.setExamId(examId);
						dbe.setExamDate(examDate);
						dbe.setSubjectName(subjectName);
						dbe.setSubjectDetails(subjectDetails);
						dbe.setSubjectExamDate(subjectExamDate);
						dbe.setClassId(jsonObject.getString("tbl_class_tt_id"));
						dbe.setStartDate(jsonObject.getString("tbl_class_start_time"));
						dbe.setEndDate(jsonObject.getString("tbl_class_end_time"));
						examTtArray.add(dbe);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (examTtArray.size() != 0) {
					listItms();
				}
				super.onPostExecute(result);
			}
		}
		
	}
	
	private void getCTT(String aboutUs) {
		if (aboutUs.equals("{\"result\":null,")) {
			exam_list.setVisibility(View.GONE);
			header.setVisibility(View.GONE);
			no_class_table.setVisibility(View.VISIBLE);
			mSwipeRefreshLayout.setRefreshing(false);
			examTtArray.clear();
		} else {
			no_class_table.setVisibility(View.GONE);
			exam_list.setVisibility(View.VISIBLE);
			header.setVisibility(View.VISIBLE);
			mSwipeRefreshLayout.setRefreshing(false);
			try {
				JSONObject jsonRootObject = new JSONObject(aboutUs);
				JSONArray jsonArray = jsonRootObject.getJSONArray("result");
				JSONArray teacherNames = jsonRootObject.getJSONArray("teacherList");
				SharedPreferences sp = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
				sp.edit().putString("teacherArray", teacherNames.toString()).apply();
				examTtArray.clear();
				for (int i = 0; i < jsonArray.length(); i++) {
					dbe = new DataBeanExamTt();
					
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					examId = jsonObject.getString("tbl_class_tt_period_id");
					examDate = jsonObject.getString("tbl_class_tt_teacher_name");
					subjectName = jsonObject.getString("tbl_class_tt_subject_name");
					subjectDetails = jsonObject.getString("tbl_class_tt_subject_details");
					subjectExamDate = jsonObject.getString("tbl_class_tt_current_date");
					dbe.setExamId(examId);
					dbe.setExamDate(examDate);
					dbe.setSubjectName(subjectName);
					dbe.setSubjectDetails(subjectDetails);
					dbe.setSubjectExamDate(subjectExamDate);
					dbe.setClassId(jsonObject.getString("tbl_class_tt_id"));
					dbe.setStartDate(jsonObject.getString("tbl_class_start_time"));
					dbe.setEndDate(jsonObject.getString("tbl_class_end_time"));
					examTtArray.add(dbe);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (examTtArray.size() != 0) {
				listItms();
			}
		}
	}
	
	
	private void listItms() {
		ClassTimeTableListAdaptor classTimeTableListAdaptor = new ClassTimeTableListAdaptor(getActivity(), R.layout.class_tt_list_item, examTtArray);
		exam_list.setAdapter(classTimeTableListAdaptor);

		SharedPreferences sp = getContext().getSharedPreferences(PREFS_RW, 0);
		String json = sp.getString("result", "");

		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONArray jsonArray = jsonObject.getJSONArray("result");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject object = jsonArray.getJSONObject(i);
				if (object.getString("tbl_insti_buzz_cate_name").equals("Time Table")) {
					if (object.getString("tbl_scl_inst_buzz_detl_write_status").equals("Active")) {
						exam_list.setOnItemClickListener((parent, view1, position, id) -> {
							Intent intent = new Intent(getContext(), UpdateClassTimeTable.class);
							intent.putExtra("subject", examTtArray.get(position).getSubjectName());
							intent.putExtra("day", currentDay);
							intent.putExtra("batch", selBatch);
							intent.putExtra("id", examTtArray.get(position).getClassId());
							intent.putExtra("teacher", examTtArray.get(position).getExamDate());
							intent.putExtra("start_time", examTtArray.get(position).getStartDate());
							intent.putExtra("end_time", examTtArray.get(position).getEndDate());
							intent.putExtra("period", examTtArray.get(position).getExamId());
							startActivity(intent);
						});
					}
					break;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public final boolean isInternetOn() {
		ConnectivityManager connec = (ConnectivityManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CONNECTIVITY_SERVICE);
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
