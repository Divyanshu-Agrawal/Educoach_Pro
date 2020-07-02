package com.aaptrix.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;

import com.aaptrix.activitys.admin.IntermidiateScreenActivity;
import com.aaptrix.activitys.student.AchievmentDetailsActivity;
import com.aaptrix.databeans.DataBeanExamTt;
import com.aaptrix.adaptor.ExamTimeTableListAdaptor;
import com.aaptrix.R;

import static com.aaptrix.tools.HttpUrl.ALL_EXAMS;
import static com.aaptrix.tools.HttpUrl.ALL_EXAMS_NEXT;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREFS_RW;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class ExamTimeTableFragment extends Fragment {
	
	private View view;
	private ArrayList<DataBeanExamTt> examTtArray = new ArrayList<>();
	private ArrayList<DataBeanExamTt> examList = new ArrayList<>();
	private DataBeanExamTt dbe;
	private String examId = "", examName = "", examDate = "", subjectName = "", subjectDetails = "", subjectExamDate = "";
	private ListView exam_list;
	private TextView no_time_table, examMainName, examType;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	
	private String schoolId;
	private String userSection;
	private LinearLayout header;
	private String selBatch;
	private SharedPreferences sp_aboutUs;
	private static final String PREFS_ABOUTUS = "json_ctt";
	private int position = 0;
	LinearLayout add_layout;
	ImageView addExam;
	
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_exam_time_table, container, false);
		
		exam_list = view.findViewById(R.id.exam_list);
		no_time_table = view.findViewById(R.id.no_class_table);

		examMainName = view.findViewById(R.id.todayDateTime);
		examType = view.findViewById(R.id.exam_type);
		add_layout = view.findViewById(R.id.add_layout);
		addExam = view.findViewById(R.id.add_exam);
		mSwipeRefreshLayout = view.findViewById(R.id.activity_main_swipe_refresh_layout);
		SharedPreferences settings = view.getContext().getSharedPreferences(PREFS_NAME, 0);
		schoolId = settings.getString("str_school_id", "");
		userSection = settings.getString("userSection", "");
		String userType = settings.getString("userrType", "");

		SharedPreferences sp = getContext().getSharedPreferences(PREFS_RW, 0);
		String json = sp.getString("result", "");

		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONArray jsonArray = jsonObject.getJSONArray("result");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject object = jsonArray.getJSONObject(i);
				if (object.getString("tbl_insti_buzz_cate_name").equals("Time Table")) {
					if (object.getString("tbl_scl_inst_buzz_detl_write_status").equals("Active")) {
						add_layout.setVisibility(View.VISIBLE);
					} else {
						add_layout.setVisibility(View.GONE);
					}
					break;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		addExam.setOnClickListener(v -> {
			Intent i = new Intent(getContext(), IntermidiateScreenActivity.class);
			i.putExtra("str_tool_title", "Add Exam");
			startActivity(i);
		});
		
		//color
		SharedPreferences settingsColor = Objects.requireNonNull(getActivity()).getSharedPreferences(PREF_COLOR, 0);
		String selDrawerColor = settingsColor.getString("drawer", "");
		String selToolColor = settingsColor.getString("tool", "");
		ImageView previous = view.findViewById(R.id.ib_prev);
		FrameLayout previousF = view.findViewById(R.id.ib_prevF);
		FrameLayout nextF = view.findViewById(R.id.Ib_nextF);
		ImageView next = view.findViewById(R.id.Ib_next);
		
		header = view.findViewById(R.id.header);
		mSwipeRefreshLayout.setRefreshing(false);
		mSwipeRefreshLayout.setEnabled(false);
		if (userType.equals("Admin") || userType.equals("Teacher")) {
			assert getArguments() != null;
			selBatch = getArguments().getString("selBatch");
			if (isInternetOn()) {
				GetAllExams b1 = new GetAllExams(view.getContext());
				b1.execute(schoolId, selBatch, "Internal Exam");
			}
			
			previousF.setOnClickListener(view -> {
				if (isInternetOn()) {
					if (position != 0) {
						position = position - 1;
						examMainName.setText(examList.get(position).getExamName());
						if (examList.get(position).getSubjectName().equals("0")) {
							examType.setText("(Minor Exam)");
						} else {
							examType.setText("(Major Exam)");
						}
						GetAllExamsNext b1 = new GetAllExamsNext(view.getContext());
						b1.execute(schoolId, selBatch, examList.get(position).getExamId());
					} else {
						Toast.makeText(view.getContext(), "No More Exam", Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(view.getContext(), "No Network", Toast.LENGTH_SHORT).show();
				}
			});
			
			nextF.setOnClickListener(view -> {
				if (isInternetOn()) {
					if (position < examList.size() - 1) {
						position = position + 1;
						examMainName.setText(examList.get(position).getExamName());
						if (examList.get(position).getSubjectName().equals("0")) {
							examType.setText("(Minor Exam)");
						} else {
							examType.setText("(Major Exam)");
						}
						GetAllExamsNext b1 = new GetAllExamsNext(view.getContext());
						b1.execute(schoolId, selBatch, examList.get(position).getExamId());
					} else {
						Toast.makeText(view.getContext(), "No More Exam", Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(view.getContext(), "No Network", Toast.LENGTH_SHORT).show();
					
				}
			});
		} else {
			sp_aboutUs = view.getContext().getSharedPreferences(PREFS_ABOUTUS, 0);
			String aboutUs = sp_aboutUs.getString("json_ctt", "");
			getCTT(aboutUs);
			mSwipeRefreshLayout.setOnRefreshListener(() -> {
				if (isInternetOn()) {
					examTtArray.clear();
					GetAllExams b1 = new GetAllExams(view.getContext());
					b1.execute(schoolId, userSection, "Internal Exam");
				} else {
					mSwipeRefreshLayout.setRefreshing(false);
					Toast.makeText(getActivity(), "No network Please connect with network for update", Toast.LENGTH_SHORT).show();
				}
			});
			if (isInternetOn()) {
				GetAllExams b1 = new GetAllExams(view.getContext());
				b1.execute(schoolId, userSection, "Internal Exam");
				
			} else {
				getCTT(aboutUs);
			}
			
			previousF.setOnClickListener(view -> {
				if (isInternetOn()) {
					if (position != 0) {
						position = position - 1;
						examMainName.setText(examList.get(position).getExamName());
						if (examList.get(position).getSubjectName().equals("0")) {
							examType.setText("(Minor Exam)");
						} else {
							examType.setText("(Major Exam)");
						}
						GetAllExamsNext b1 = new GetAllExamsNext(view.getContext());
						b1.execute(schoolId, selBatch, examList.get(position).getExamId());
					} else {
						Toast.makeText(view.getContext(), "No More Exam", Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(view.getContext(), "No Network", Toast.LENGTH_SHORT).show();
				}
			});
			
			nextF.setOnClickListener(view -> {
				if (isInternetOn()) {
					if (position < examList.size() - 1) {
						position = position + 1;
						examMainName.setText(examList.get(position).getExamName());
						if (examList.get(position).getSubjectName().equals("0")) {
							examType.setText("(Minor Exam)");
						} else {
							examType.setText("(Major Exam)");
						}
						GetAllExamsNext b1 = new GetAllExamsNext(view.getContext());
						b1.execute(schoolId, selBatch, examList.get(position).getExamId());
					} else {
						Toast.makeText(view.getContext(), "No More Exam", Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(view.getContext(), "No Network", Toast.LENGTH_SHORT).show();
				}
			});
		}
		previous.setColorFilter(Color.parseColor(selDrawerColor));
		next.setColorFilter(Color.parseColor(selDrawerColor));
		GradientDrawable bgShape = (GradientDrawable) add_layout.getBackground();
		bgShape.setColor(Color.parseColor(selToolColor));
		return view;
	}
	
	private void getCTT(String aboutUs) {
		if (aboutUs.equals("{\"result\":null}")) {
			no_time_table.setVisibility(View.VISIBLE);
			exam_list.setVisibility(View.GONE);
			examTtArray.clear();
		} else {
			no_time_table.setVisibility(View.GONE);
			exam_list.setVisibility(View.VISIBLE);
			header.setVisibility(View.VISIBLE);
			
			try {
				JSONObject jsonRootObject = new JSONObject(aboutUs);
				JSONArray jsonArray = jsonRootObject.getJSONArray("result");
				examTtArray.clear();
				for (int i = 0; i < jsonArray.length(); i++) {
					dbe = new DataBeanExamTt();
					
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					examId = jsonObject.getString("tbl_exam_id");
					examName = jsonObject.getString("tbl_exam_name");
					examDate = jsonObject.getString("tbl_exam_start_date");
					subjectName = jsonObject.getString("tbl_exam_details_subject_name");
					subjectDetails = jsonObject.getString("tbl_exam_subject_detail");
					subjectExamDate = jsonObject.getString("tbl_exam_details_date");
					dbe.setExamId(examId);
					dbe.setExamName(examName);
					dbe.setExamDate(examDate);
					dbe.setSubjectName(subjectName);
					dbe.setSubjectDetails(subjectDetails);
					dbe.setSubjectExamDate(subjectExamDate);
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
	
	
	//online
	@SuppressLint("StaticFieldLeak")
	public class GetAllExams extends AsyncTask<String, String, String> {
		Context ctx;
		
		GetAllExams(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			mSwipeRefreshLayout.setRefreshing(true);
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			
			String schoolId = params[0];
			String sectionName = params[1];
			String data;
			
			try {
				
				URL url = new URL(ALL_EXAMS);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				
				OutputStream outputStream = httpURLConnection.getOutputStream();
				
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
				data = URLEncoder.encode("schoolId", "UTF-8") + "=" + URLEncoder.encode(schoolId, "UTF-8") + "&" +
						URLEncoder.encode("section", "UTF-8") + "=" + URLEncoder.encode(sectionName, "UTF-8") + "&" +
						URLEncoder.encode("exam_type", "UTF-8") + "=" + URLEncoder.encode("Internal Exam", "UTF-8");
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
			Log.e("ExamJson_id_name", "" + result);
			mSwipeRefreshLayout.setRefreshing(false);
			sp_aboutUs = ctx.getSharedPreferences(PREFS_ABOUTUS, 0);
			SharedPreferences.Editor se_aboutUs = sp_aboutUs.edit();
			se_aboutUs.clear();
			se_aboutUs.putString("json_ctt", result);
			se_aboutUs.apply();
			if (result.equals("{\"result\":null}")) {
				no_time_table.setVisibility(View.VISIBLE);
				exam_list.setVisibility(View.GONE);
				examTtArray.clear();
			} else {
				no_time_table.setVisibility(View.GONE);
				exam_list.setVisibility(View.VISIBLE);
				header.setVisibility(View.VISIBLE);
				
				try {
					JSONObject jsonRootObject = new JSONObject(result);
					JSONArray jsonArray = jsonRootObject.getJSONArray("result");
					examTtArray.clear();
					for (int i = 0; i < jsonArray.length(); i++) {
						dbe = new DataBeanExamTt();
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						examId = jsonObject.getString("tbl_exam_id");
						examName = jsonObject.getString("tbl_exam_name");
						examDate = jsonObject.getString("tbl_exam_start_date");
						dbe.setSubjectName(jsonObject.getString("exam_major_minor"));
						dbe.setExamId(examId);
						dbe.setExamName(examName);
						dbe.setExamDate(examDate);
						examList.add(dbe);
					}
					examMainName.setText(examList.get(0).getExamName());
					if (examList.get(position).getSubjectName().equals("0")) {
						examType.setText("(Minor Exam)");
					} else {
						examType.setText("(Major Exam)");
					}
					GetAllExamsNext b1 = new GetAllExamsNext(view.getContext());
					b1.execute("", "", examList.get(0).getExamId());
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
	
	@SuppressLint("StaticFieldLeak")
	public class GetAllExamsNext extends AsyncTask<String, String, String> {
		Context ctx;
		
		GetAllExamsNext(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			mSwipeRefreshLayout.setRefreshing(true);
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			
			String examId = params[2];
			String data;
			
			try {
				URL url = new URL(ALL_EXAMS_NEXT);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				
				OutputStream outputStream = httpURLConnection.getOutputStream();
				
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
				data = URLEncoder.encode("examId", "UTF-8") + "=" + URLEncoder.encode(examId, "UTF-8");
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
			mSwipeRefreshLayout.setRefreshing(false);
			
			if (result.equals("{\"result\":null}")) {
				Toast.makeText(ctx, "No Exam Details", Toast.LENGTH_SHORT).show();
				no_time_table.setVisibility(View.VISIBLE);
				exam_list.setVisibility(View.GONE);
			} else {
				no_time_table.setVisibility(View.GONE);
				exam_list.setVisibility(View.VISIBLE);
				try {
					JSONObject jsonRootObject = new JSONObject(result);
					JSONArray jsonArray = jsonRootObject.getJSONArray("result");
					examTtArray.clear();
					for (int i = 0; i < jsonArray.length(); i++) {
						dbe = new DataBeanExamTt();
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						examId = jsonObject.getString("tbl_exam_id");
						examName = jsonObject.getString("tbl_exam_name");
						examDate = jsonObject.getString("tbl_exam_start_date");
						subjectName = jsonObject.getString("tbl_exam_details_subject_name");
						subjectDetails = jsonObject.getString("tbl_exam_subject_detail");
						subjectExamDate = jsonObject.getString("tbl_exam_details_date");
						dbe.setExamId(examId);
						dbe.setExamName(examName);
						dbe.setExamDate(examDate);
						dbe.setSubjectName(subjectName);
						dbe.setSubjectDetails(subjectDetails);
						dbe.setSubjectExamDate(subjectExamDate);
						examTtArray.add(dbe);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (examTtArray.size() != 0) {
					listItms();
				} else {
					no_time_table.setVisibility(View.VISIBLE);
					exam_list.setVisibility(View.GONE);
				}
				super.onPostExecute(result);
			}
		}
	}
	
	private void listItms() {
		Collections.sort(examTtArray, (o1, o2) -> {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
			try {
				return sdf.parse(o1.getExamDate()).compareTo(sdf.parse(o2.getExamDate()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return 0;
		});

		try {
			ExamTimeTableListAdaptor examTimeTableListAdaptor = new ExamTimeTableListAdaptor(getActivity(), R.layout.exam_list_item, examTtArray);
			exam_list.setAdapter(examTimeTableListAdaptor);
			exam_list.setOnItemClickListener((parent, view, position, id) -> {
				String examId = examTtArray.get(position).getExamId();
				String subjectName = examTtArray.get(position).getSubjectName();
				String subjectDetail = examTtArray.get(position).getSubjectDetails();
				String examDate = examTtArray.get(position).getSubjectExamDate();
				
				Intent i11 = new Intent(view.getContext(), AchievmentDetailsActivity.class);
				i11.putExtra("examId", examId);
				i11.putExtra("achImg", "");
				i11.putExtra("achCate", "examTT");
				i11.putExtra("achTitle", subjectName);
				i11.putExtra("achDesc", subjectDetail);
				i11.putExtra("acgDate", examDate);
				startActivity(i11);
				Objects.requireNonNull(getActivity()).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public final boolean isInternetOn() {
		
		// get Connectivity Manager object to check connection
		ConnectivityManager connec =
				(ConnectivityManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CONNECTIVITY_SERVICE);
		
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
	
}
