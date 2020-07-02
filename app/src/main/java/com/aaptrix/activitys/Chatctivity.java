package com.aaptrix.activitys;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.appbar.AppBarLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import com.aaptrix.databeans.DataBeanMessage;
import com.aaptrix.adaptor.LastSeenTime;
import com.aaptrix.adaptor.MessageListAdaptor;
import com.aaptrix.R;

import static com.aaptrix.tools.HttpUrl.SEND_MESSAGE;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

/**
 * Created by Administrator on 11/29/2017.
 */

public class Chatctivity extends AppCompatActivity {
	
	SharedPreferences.Editor editor;
	String userId, userName, userPassword, userSchoolLogo, numberOfUser, schoolId;
	AppBarLayout appBarLayout;
	SharedPreferences.Editor editorColor;
	String selToolColor, selDrawerColor, selStatusColor, selTextColor1, selTextColor2;
	TextView tool_title;
	private String messageReceiverId;
	private String user_token_id;
	private TextView userLastSeen;
	private CircleImageView userChatProfileImage;
	private DatabaseReference rootRef;
	private EditText InputMessageText;
	private String messageSenderId;
	private ListView userMessagesList;
	MessageListAdaptor messageListAdaptor;
	DataBeanMessage dbm;
	ArrayList<DataBeanMessage> messageArray = new ArrayList<>();
	String lastSeenDisplayTime = "0";
	String online = "0";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_layout);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		appBarLayout = findViewById(R.id.appBarLayout);
		tool_title = findViewById(R.id.tool_title);
		
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		assert notificationManager != null;
		notificationManager.cancelAll();
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		editor = settings.edit();
		userId = settings.getString("userID", "");
		userName = settings.getString("userName", "");
		userPassword = settings.getString("userPassword", "");
		userSchoolLogo = settings.getString("userSchoolLogo", "");
		numberOfUser = settings.getString("numberOfUser", "");
		schoolId = settings.getString("str_school_id", "");
//color
		SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
		editorColor = settingsColor.edit();
		selToolColor = settingsColor.getString("tool", "");
		selDrawerColor = settingsColor.getString("drawer", "");
		selStatusColor = settingsColor.getString("status", "");
		selTextColor1 = settingsColor.getString("text1", "");
		selTextColor2 = settingsColor.getString("text2", "");
		
		rootRef = FirebaseDatabase.getInstance().getReference();
		FirebaseAuth mAuth = FirebaseAuth.getInstance();
		
		messageSenderId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
		messageReceiverId = Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("list_user_id")).toString();
		String messageRecieverName = getIntent().getStringExtra("user_name");
		user_token_id = getIntent().getStringExtra("user_token_id");

		userLastSeen = findViewById(R.id.custom_user_last_seen);
		userChatProfileImage = findViewById(R.id.prof_logo1);
		
		ImageView sendMessageButton = findViewById(R.id.send_message);
		InputMessageText = findViewById(R.id.input_message);
		
		userMessagesList = findViewById(R.id.messages_list_of_users);
		userMessagesList.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
		userMessagesList.setStackFromBottom(true);
		FetchMessages();
		
		tool_title.setText(messageRecieverName);
		
		rootRef.child("Users").child(messageReceiverId).addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				online = Objects.requireNonNull(dataSnapshot.child("online").getValue()).toString();
				final String userThumb = Objects.requireNonNull(dataSnapshot.child("userImg").getValue()).toString();
				
				Picasso.with(Chatctivity.this).load(userThumb).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.user_place_hoder)
						.into(userChatProfileImage, new Callback() {
							@Override
							public void onSuccess() {
							
							}

							@Override
							public void onError() {
								Picasso.with(Chatctivity.this).load(userThumb).placeholder(R.drawable.user_place_hoder).into(userChatProfileImage);
							}
						});
				
				if (online.equals("true")) {
					userLastSeen.setText("Online");
				} else if (TextUtils.isDigitsOnly(online)) {
					try {
						long last_seen = Long.parseLong(online);
						lastSeenDisplayTime = LastSeenTime.getTimeAgo(last_seen, getApplicationContext());
						userLastSeen.setText(lastSeenDisplayTime);
					} catch (Exception e) {
						userLastSeen.setText("Online");
					}
				}
			}
			
			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
			
			}
		});
		
		sendMessageButton.setOnClickListener(view -> SendMessage());
		///
		appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.parseColor(selStatusColor));
		}
		tool_title.setTextColor(Color.parseColor(selTextColor1));
	}
	
	private void FetchMessages() {
		final Map<String, Object> seen = new HashMap<>();
		seen.put("seen", "true");
		rootRef.child("Messages").child(messageSenderId).child(messageReceiverId)
				.addChildEventListener(new ChildEventListener() {
					@Override
					public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
						rootRef.child("Messages").child(messageSenderId).child(messageReceiverId)
								.addListenerForSingleValueEvent(new ValueEventListener() {
									@Override
									public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
										dataSnapshot1.getRef().updateChildren(seen);
									}
									
									@Override
									public void onCancelled(@NonNull DatabaseError databaseError) {
										Log.e("error", databaseError.getMessage());
									}
								});
						
						String data = Objects.requireNonNull(dataSnapshot.getValue()).toString();
						String dataMain = data.replace("{", "");
						String dataMain1 = dataMain.replace("}", "");
						dbm = new DataBeanMessage();
						String[] commaSplit = dataMain1.split(",");
						for (String q : commaSplit) {
							String[] zz = q.split("=");
							if (zz[0].contains("message")) {
								dbm.setMessage(zz[1]);
							}
							if (zz[0].contains("time")) {
								dbm.setTime(zz[1]);
							}
							if (zz[0].contains("type")) {
								dbm.setType(zz[1]);
							}
							if (zz[0].contains("userName")) {
								dbm.setUserNameM(zz[1]);
							}
							if (zz[0].contains("seen")) {
								dbm.setSeen(zz[1]);
							}
						}
						messageArray.add(dbm);
						for (int i = 0; i < messageArray.size(); i++) {
							if (messageArray.get(i).getUserNameM() == null || messageArray.get(i).getMessage() == null) {
								messageArray.remove(i);
								break;
							}
						}
						messageListAdaptor = new MessageListAdaptor(Chatctivity.this, R.layout.achivement_list_item1, messageArray);
						userMessagesList.setAdapter(messageListAdaptor);
						userMessagesList.setSelection(userMessagesList.getAdapter().getCount() - 1);
						messageListAdaptor.notifyDataSetChanged();
					}
					
					@Override
					public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
					
					}
					
					@Override
					public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
					
					}
					
					@Override
					public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
					
					}
					
					@Override
					public void onCancelled(@NonNull DatabaseError databaseError) {
					
					}
				});
	}
	
	private void SendMessage() {
		String messageText = InputMessageText.getText().toString();
		
		if (TextUtils.isEmpty(messageText)) {
			Toast.makeText(Chatctivity.this, "Please write your message", Toast.LENGTH_SHORT).show();
		} else {
			sendNotification(user_token_id, messageText, userName, schoolId);
			String message_sender_ref = "Messages/" + messageSenderId + "/" + messageReceiverId;
			String message_receiver_ref = "Messages/" + messageReceiverId + "/" + messageSenderId;
			DatabaseReference user_message_key = rootRef.child("Messages").child(messageSenderId)
					.child(messageReceiverId).push();
			String message_push_id = user_message_key.getKey();
			Calendar calander = Calendar.getInstance();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:MM");
			String CurrentTime = simpleDateFormat.format(calander.getTime());
			Date c1 = Calendar.getInstance().getTime();
			SimpleDateFormat df = new SimpleDateFormat(getResources().getString(R.string.date_card_formate_chat));
			String formattedDate1 = df.format(c1);
			Map<String, java.io.Serializable> messageTextBody = new HashMap<>();
			
			messageTextBody.put("message", messageText);
			messageTextBody.put("userName", userName);
			messageTextBody.put("seen", false);
			messageTextBody.put("type", "text");
			messageTextBody.put("time", formattedDate1 + " at " + CurrentTime);
			
			Map<String, Object> messageBodyDetails = new HashMap<>();
			
			messageBodyDetails.put(message_sender_ref + "/" + message_push_id, messageTextBody);
			messageBodyDetails.put(message_receiver_ref + "/" + message_push_id, messageTextBody);
			
			rootRef.updateChildren(messageBodyDetails, (databaseError, databaseReference) -> {
				if (databaseError != null) {
					Log.d("Chat_Log", databaseError.getMessage());
				}
				InputMessageText.setText("");
			});
		}
	}
	
	private void sendNotification(String user_token_id, String message, String userName, String schoolId) {
		SendNotification sendNotification = new SendNotification(Chatctivity.this);
		sendNotification.execute(user_token_id, message, userName, schoolId);
	}
	
	@SuppressLint("StaticFieldLeak")
	public class SendNotification extends AsyncTask<String, String, String> {
		Context ctx;
		
		SendNotification(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			
			String user_token_id = params[0];
			String message = params[1];
			String userName = params[2];
			String schoolId = params[3];
			String data;
			NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			assert notificationManager != null;
			notificationManager.cancelAll();
			
			try {
				URL url = new URL(SEND_MESSAGE);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				
				OutputStream outputStream = httpURLConnection.getOutputStream();
				
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
				data = URLEncoder.encode("user_token_id", "UTF-8") + "=" + URLEncoder.encode(user_token_id, "UTF-8") + "&" +
						URLEncoder.encode("message", "UTF-8") + "=" + URLEncoder.encode(message, "UTF-8") + "&" +
						URLEncoder.encode("userName", "UTF-8") + "=" + URLEncoder.encode(userName, "UTF-8") + "&" +
						URLEncoder.encode("tbl_school_id", "UTF-8") + "=" + URLEncoder.encode(schoolId, "UTF-8");
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
				Log.e("JSONdata", "data--" + data);
				
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
			Log.d("Json", "Result" + result);
			super.onPostExecute(result);
		}
		
	}
	
	
	public final boolean isInternetOn() {
		ConnectivityManager connec;
		connec = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		assert connec != null;
		return connec.getActiveNetworkInfo() != null && connec.getActiveNetworkInfo().isAvailable() && connec.getActiveNetworkInfo().isConnectedOrConnecting();
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
}
