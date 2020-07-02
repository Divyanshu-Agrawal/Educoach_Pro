package com.aaptrix.activitys;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import com.google.android.material.appbar.AppBarLayout;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import com.aaptrix.databeans.AllUsers;
import com.aaptrix.R;

import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

/**
 * Created by Administrator on 11/29/2017.
 */

public class AllUsersChatActivity extends AppCompatActivity {
	
	SharedPreferences.Editor editor;
	String userId, userName11, userPassword, userSchoolLogo, numberOfUser, userSchoolId1, userEmailId;
	AppBarLayout appBarLayout;
	SharedPreferences.Editor editorColor;
	String selToolColor, selDrawerColor, selStatusColor, selTextColor1, selTextColor2;
	TextView tool_title;
	String user_token_id, android_id;
	
	FirebaseUser currentUser;
	private DatabaseReference UsersReference;
	private RecyclerView allUsersList;
	private FirebaseAuth mAuth;
	private DatabaseReference allDatabaseUserreference;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	public SharedPreferences settings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_users_chat_main_layout);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		appBarLayout = findViewById(R.id.appBarLayout);
		tool_title = findViewById(R.id.tool_title);
		mSwipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
		mSwipeRefreshLayout.setRefreshing(true);
		
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		assert notificationManager != null;
		notificationManager.cancelAll();
		settings = getSharedPreferences(PREFS_NAME, 0);
		editor = settings.edit();
		userId = settings.getString("userID", "");
		userName11 = settings.getString("userName", "");
		userEmailId = settings.getString("userEmailId", "");
		userPassword = settings.getString("userPassword", "");
		userSchoolId1 = settings.getString("str_school_id", "");
		userSchoolLogo = settings.getString("userSchoolLogo", "");
		numberOfUser = settings.getString("numberOfUser", "");
		//firebase
		mSwipeRefreshLayout.setRefreshing(true);
		mAuth = FirebaseAuth.getInstance();
		currentUser = mAuth.getCurrentUser();
		UsersReference = FirebaseDatabase.getInstance().getReference().child("Users");
		user_token_id = FirebaseInstanceId.getInstance().getToken();
		android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
		if (currentUser != null) {
			String online_user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
			Log.d("online_user_id", "" + online_user_id);
			UsersReference = FirebaseDatabase.getInstance().getReference().child("Users").child(online_user_id);
			UsersReference.child("user_token_id").setValue(user_token_id);
			mSwipeRefreshLayout.setRefreshing(true);
			// mSwipeRefreshLayout.setEnabled( false );
		}
		
		Handler handler2 = new Handler();
		handler2.postDelayed(() -> {
			mSwipeRefreshLayout.setRefreshing(false);
			mSwipeRefreshLayout.setEnabled(false);
			
		}, 4000);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		allUsersList = findViewById(R.id.all_users_list);
		allUsersList.setHasFixedSize(true);
		allUsersList.setLayoutManager(new LinearLayoutManager(this));
		allDatabaseUserreference = FirebaseDatabase.getInstance().getReference().child("Users");
		SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
		editorColor = settingsColor.edit();
		selToolColor = settingsColor.getString("tool", "");
		selDrawerColor = settingsColor.getString("drawer", "");
		selStatusColor = settingsColor.getString("status", "");
		selTextColor1 = settingsColor.getString("text1", "");
		selTextColor2 = settingsColor.getString("text2", "");
		mSwipeRefreshLayout.setColorScheme(R.color.text_gray);
		appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.parseColor(selStatusColor));
		}
		tool_title.setTextColor(Color.parseColor(selTextColor1));
	}
	
	
	@Override
	protected void onStart() {
		super.onStart();
		try {
			currentUser = mAuth.getCurrentUser();
			if (currentUser != null) {
				UsersReference.child("online").setValue("true");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		FirebaseRecyclerAdapter<AllUsers, AllUsersViewHolder> firebaseRecyclerAdapter
				= new FirebaseRecyclerAdapter<AllUsers, AllUsersViewHolder>(AllUsers.class,
				R.layout.all_users_display_layout,
				AllUsersViewHolder.class,
				allDatabaseUserreference
		
		) {
			@Override
			protected void populateViewHolder(final AllUsersViewHolder viewHolder, AllUsers model, final int position) {
				viewHolder.setUser_name(model.getUser_name());
				viewHolder.setUser_status(model.getUser_status());
				viewHolder.setUser_image(getApplicationContext(), model.getUser_image());
				final String list_user_id = getRef(position).getKey();
				assert list_user_id != null;
				allDatabaseUserreference.child(list_user_id).addValueEventListener(new ValueEventListener() {
					@Override
					public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
						try {
							final String userName = Objects.requireNonNull(dataSnapshot.child("userName").getValue()).toString();
							final String userImage = Objects.requireNonNull(dataSnapshot.child("userImg").getValue()).toString();
							final String userStatus = Objects.requireNonNull(dataSnapshot.child("userrType").getValue()).toString();
							String online_Status = Objects.requireNonNull(dataSnapshot.child("online").getValue()).toString();
							String userSchoolId;
//							Log.e("userid", Objects.requireNonNull(dataSnapshot.child("userID").getValue()).toString());
							try {
								userSchoolId = Objects.requireNonNull(dataSnapshot.child("userSchoolId").getValue()).toString();
							} catch (Exception e) {
								userSchoolId = "2";
							}
							final String user_token_id = Objects.requireNonNull(dataSnapshot.child("user_token_id").getValue()).toString();
							if (userSchoolId.equals(userSchoolId1)) {
								if (userName.equals(userName11)) {
									viewHolder.setUser_name1();
								} else {
									viewHolder.setUser_name("" + position);
								}
							} else {
								viewHolder.setUser_name1();
							}
							viewHolder.setUserOnline(online_Status);
							viewHolder.setUser_name(userName);
							viewHolder.setUser_status(userStatus);
							viewHolder.setUser_image(getApplicationContext(), userImage);
							viewHolder.mView.setOnClickListener(view -> {
								if (dataSnapshot.child("online").exists()) {
									Intent chatIntent = new Intent(AllUsersChatActivity.this, Chatctivity.class);
									chatIntent.putExtra("list_user_id", list_user_id);
									chatIntent.putExtra("user_name", userName);
									chatIntent.putExtra("userImage", userImage);
									chatIntent.putExtra("user_token_id", user_token_id);
									startActivity(chatIntent);
								}
							});
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
					@Override
					public void onCancelled(@NonNull DatabaseError databaseError) {
					
					}
				});
			}
		};
		allUsersList.setAdapter(firebaseRecyclerAdapter);
	}
	
	
	public static class AllUsersViewHolder extends RecyclerView.ViewHolder {
		View mView;
		
		public AllUsersViewHolder(View itemView) {
			super(itemView);
			mView = itemView;
		}
		
		void setUser_name(String userName) {
			TextView name = mView.findViewById(R.id.all_users_username);
			name.setText(userName);
		}
		
		void setUser_name1() {
			CardView name = mView.findViewById(R.id.cv);
			name.setVisibility(View.GONE);
		}
		
		void setUser_status(String user_status) {
			TextView status = mView.findViewById(R.id.all_users_status);
			status.setText(user_status);
		}
		
		void setUser_image(Context context, String user_image) {
			CircleImageView image = mView.findViewById(R.id.all_users_profile_image);
			SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
			String userSchoolId1 = settings.getString("str_school_id", "");
			String url;
			switch (settings.getString("userrType", "")) {
				case "Parent":
					url = settings.getString("imageUrl", "") + userSchoolId1 + "/users/students/profile/" + user_image;
					Picasso.with(context).load(url).error(R.drawable.user_place_hoder).placeholder(R.drawable.user_place_hoder).into(image);
					break;
				case "Admin":
					url = settings.getString("imageUrl", "") + userSchoolId1 + "/users/admin/profile/" + user_image;
					Picasso.with(context).load(url).error(R.drawable.user_place_hoder).placeholder(R.drawable.user_place_hoder).into(image);
					break;
				case "Staff":
					url = settings.getString("imageUrl", "") + userSchoolId1 + "/users/staff/profile/" + user_image;
					Picasso.with(context).load(url).error(R.drawable.user_place_hoder).placeholder(R.drawable.user_place_hoder).into(image);
					break;
				case "Teacher":
					url = settings.getString("imageUrl", "") + userSchoolId1 + "/users/teachers/profile/" + user_image;
					Picasso.with(context).load(url).error(R.drawable.user_place_hoder).placeholder(R.drawable.user_place_hoder).into(image);
					break;
				case "Student":
					url = settings.getString("imageUrl", "") + userSchoolId1 + "/users/students/profile/" + user_image;
					Log.e("pro url", url);
					Picasso.with(context).load(url).error(R.drawable.user_place_hoder).placeholder(R.drawable.user_place_hoder).into(image);
					break;
			}
		}
		
		void setUserOnline(String online_status) {
			ImageView onlineStatusView = mView.findViewById(R.id.online_status);
			if (online_status.equals("true")) {
				onlineStatusView.setVisibility(View.VISIBLE);
			} else {
				onlineStatusView.setVisibility(View.INVISIBLE);
			}
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		if (currentUser != null) {
			UsersReference.child("online").setValue(ServerValue.TIMESTAMP);
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
			if (currentUser != null) {
				UsersReference.child("online").setValue(ServerValue.TIMESTAMP);
			}
			finish();
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (currentUser != null) {
			UsersReference.child("online").setValue(ServerValue.TIMESTAMP);
		}
		finish();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}
}
