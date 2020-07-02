package com.aaptrix.adaptor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.aaptrix.activitys.student.StudentResult;
import com.aaptrix.databeans.DataBeanStudent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import com.aaptrix.R;

import static com.aaptrix.tools.SPClass.PREFS_NAME;

/**
 * Created by googledeveloper on 24/2/17.
 */

public class StudentResultListRecycleAdaptorEditText extends RecyclerView.Adapter<StudentResultListRecycleAdaptorEditText.ViewHolder> {
	
	private ArrayList<DataBeanStudent> studentArray;
	private ArrayList<DataBeanStudent> studentArray1 = new ArrayList<>();
	private String value;
	private DataBeanStudent dbs1;
	Activity activity;
	String stdSection;
	
	public StudentResultListRecycleAdaptorEditText(ArrayList<DataBeanStudent> countries, Activity activity, String value, String stdSection) {
		this.studentArray = countries;
		this.activity = activity;
		this.value = value;
		this.stdSection = stdSection;
		
	}
	
	@NonNull
	@Override
	public StudentResultListRecycleAdaptorEditText.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
		View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_result_list_item_edit, viewGroup, false);
		return new ViewHolder(view);
	}
	
	@Override
	public void onBindViewHolder(@NonNull final StudentResultListRecycleAdaptorEditText.ViewHolder viewHolder, final int i) {
		
		SharedPreferences settings = activity.getSharedPreferences(PREFS_NAME, 0);
		viewHolder.clgname.setText(studentArray.get(i).getUserName());
		if (studentArray.get(i).getUserImg().equals("0")) {
			viewHolder.userLogo.setImageDrawable(activity.getResources().getDrawable(R.drawable.user_place_hoder));
		} else if (!TextUtils.isEmpty(studentArray.get(i).getUserImg())) {
			String url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "") + "/users/students/profile/" + studentArray.get(i).getUserImg();
			Picasso.with(activity).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(viewHolder.userLogo);
		} else {
			viewHolder.userLogo.setImageDrawable(activity.getResources().getDrawable(R.drawable.user_place_hoder));
		}
		
		switch (value) {
			case "take":
				try {
					String compareValue = studentArray.get(i).getUserLoginId();
					viewHolder.etMarks.setText(compareValue);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				viewHolder.etMarks.setOnFocusChangeListener((view, hasFocus) -> {
					if (!hasFocus) {
						if (TextUtils.isEmpty(viewHolder.etMarks.getText().toString().trim())) {
							Toast.makeText(activity, "Please enter " + studentArray.get(i).getUserName() + " marks", Toast.LENGTH_SHORT).show();
						} else {
							dbs1 = new DataBeanStudent();
							dbs1.setUserID(studentArray.get(i).getUserID());
							dbs1.setUserLoginId(viewHolder.etMarks.getText().toString().trim());
							studentArray1.add(dbs1);
							saveDataInSP(studentArray1, activity);
						}
					}
				});
				
				viewHolder.etMarks.setOnEditorActionListener(
						(v, actionId, event) -> {
							if (actionId == EditorInfo.IME_ACTION_SEARCH ||
									actionId == EditorInfo.IME_ACTION_DONE ||
									event != null &&
											event.getAction() == KeyEvent.ACTION_DOWN &&
											event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
								if (event == null || !event.isShiftPressed()) {
									if (TextUtils.isEmpty(viewHolder.etMarks.getText().toString().trim())) {
										Toast.makeText(activity, "Please enter " + studentArray.get(i).getUserName() + " marks", Toast.LENGTH_SHORT).show();
									} else {
										dbs1 = new DataBeanStudent();
										dbs1.setUserID(studentArray.get(i).getUserID());
										dbs1.setUserLoginId(viewHolder.etMarks.getText().toString().trim());
										studentArray1.add(dbs1);
										saveDataInSP(studentArray1, activity);
									}
									
									InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
									assert imm != null;
									imm.hideSoftInputFromWindow(viewHolder.etMarks.getWindowToken(), 0);
									return true; // consume.
								}
							}
							return false; // pass on to other listeners.
						});
				
				viewHolder.etMarks.addTextChangedListener(new TextWatcher() {
					@Override
					public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
					
					}
					
					@Override
					public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
					
					}
					
					@Override
					public void afterTextChanged(Editable editable) {
						if (viewHolder.etMarks.getText().length() > 1) {
							dbs1 = new DataBeanStudent();
							dbs1.setUserID(studentArray.get(i).getUserID());
							dbs1.setUserLoginId(viewHolder.etMarks.getText().toString().trim());
							studentArray1.add(dbs1);
							saveDataInSP(studentArray1, activity);
						}
					}
				});
				break;
			
			case "update":
				viewHolder.etMarks.setText(studentArray.get(i).getUserLoginId());
				try {
					String compareValue = studentArray.get(i).getUserLoginId();
					viewHolder.etMarks.setText(compareValue);
				} catch (Exception e) {
					e.printStackTrace();
				}
				viewHolder.etMarks.setOnFocusChangeListener((view, hasFocus) -> {
					if (!hasFocus) {
						if (TextUtils.isEmpty(viewHolder.etMarks.getText().toString().trim())) {
							Toast.makeText(activity, "Please enter " + studentArray.get(i).getUserName() + " marks", Toast.LENGTH_SHORT).show();
						} else {
							dbs1 = new DataBeanStudent();
							dbs1.setUserID(studentArray.get(i).getUserID());
							dbs1.setUserLoginId(viewHolder.etMarks.getText().toString().trim());
							studentArray1.add(dbs1);
							saveDataInSP(studentArray1, activity);
						}
					}
				});
				
				viewHolder.etMarks.setOnEditorActionListener(
						(v1, actionId, event) -> {
							if (actionId == EditorInfo.IME_ACTION_SEARCH ||
									actionId == EditorInfo.IME_ACTION_DONE ||
									event != null &&
											event.getAction() == KeyEvent.ACTION_DOWN &&
											event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
								if (event == null || !event.isShiftPressed()) {
									if (TextUtils.isEmpty(viewHolder.etMarks.getText().toString().trim())) {
										Toast.makeText(activity, "Please enter " + studentArray.get(i).getUserName() + " marks", Toast.LENGTH_SHORT).show();
									} else {
										dbs1 = new DataBeanStudent();
										dbs1.setUserID(studentArray.get(i).getUserID());
										dbs1.setUserLoginId(viewHolder.etMarks.getText().toString().trim());
										studentArray1.add(dbs1);
										saveDataInSP(studentArray1, activity);
									}
									
									InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
									assert imm != null;
									imm.hideSoftInputFromWindow(viewHolder.etMarks.getWindowToken(), 0);
									return true; // consume.
								}
							}
							return false; // pass on to other listeners.
						});
				
				
				viewHolder.etMarks.addTextChangedListener(new TextWatcher() {
					@Override
					public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
					
					}
					
					@Override
					public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
					
					}
					
					@Override
					public void afterTextChanged(Editable editable) {
						if (viewHolder.etMarks.getText().length() > 1) {
							dbs1 = new DataBeanStudent();
							dbs1.setUserID(studentArray.get(i).getUserID());
							dbs1.setUserLoginId(viewHolder.etMarks.getText().toString().trim());
							studentArray1.add(dbs1);
							saveDataInSP(studentArray1, activity);
						}
					}
				});
				break;
			
			case "view":
				viewHolder.etMarks.setFocusable(false);
				viewHolder.etMarks.setText(studentArray.get(i).getUserLoginId());
				viewHolder.next.setVisibility(View.VISIBLE);
				viewHolder.itemView.setOnClickListener(view -> {
					Intent intent = new Intent(activity, StudentResult.class);
					intent.putExtra("studentId", studentArray.get(i).getUserID());
					intent.putExtra("studentImage", studentArray.get(i).getUserImg());
					intent.putExtra("studentName", studentArray.get(i).getUserName());
					intent.putExtra("userSection", stdSection);
					intent.putExtra("userType", "teacher");
					activity.startActivity(intent);
				});
				break;
		}
	}
	
	private void saveDataInSP(ArrayList<DataBeanStudent> studentArray1, Activity activity) {
		ArrayList<DataBeanStudent> students = new ArrayList<>();
		for (DataBeanStudent data : studentArray1) {
			boolean isFound = false;
			for (DataBeanStudent data1 : students) {
				if (data1.getUserID().equals(data.getUserID()) || data1.equals(data)) {
					isFound = true;
					break;
				}
			}
			if (!isFound) {
				students.add(data);
			}
		}
		Gson gson = new GsonBuilder().create();
		JsonArray myCustomArray = gson.toJsonTree(students).getAsJsonArray();
		String PREFS_ATTENDANCE = "attendance";
		SharedPreferences sp_attend = activity.getSharedPreferences(PREFS_ATTENDANCE, 0);
		SharedPreferences.Editor se_attend = sp_attend.edit();
		se_attend.clear();
		se_attend.putString("json_attend", myCustomArray.toString());
		se_attend.putString("studentArray", String.valueOf(studentArray1.size()));
		se_attend.apply();
	}
	
	@Override
	public int getItemCount() {
		return studentArray.size();
	}
	
	class ViewHolder extends RecyclerView.ViewHolder {
		
		private TextView clgname;
		private CircleImageView userLogo;
		private EditText etMarks;
		private ImageView next;
		
		ViewHolder(View view) {
			super(view);
			clgname = view.findViewById(R.id.clgname);
			userLogo = view.findViewById(R.id.userLogo);
			etMarks = view.findViewById(R.id.etMarks);
			next = view.findViewById(R.id.next);
		}
	}

	@Override
	public int getItemViewType(int position) {
		return position;
	}
	
}

