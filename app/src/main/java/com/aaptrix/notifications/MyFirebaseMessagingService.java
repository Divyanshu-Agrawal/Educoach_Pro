package com.aaptrix.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.aaptrix.activitys.AppLogin;
import com.aaptrix.activitys.admin.IntermidiateScreenActivity;
import com.aaptrix.activitys.student.ActivitiesActivity;
import com.aaptrix.activitys.student.DairyActivity;
import com.aaptrix.activitys.student.GalleryActivity;
import com.aaptrix.activitys.student.HomeworkActivity;
import com.aaptrix.activitys.student.PublicationActivity;
import com.aaptrix.activitys.student.StudentTimeTableActivity;
import com.aaptrix.activitys.student.StudyMaterial;
import com.aaptrix.activitys.student.VideoLibrary;
import com.aaptrix.activitys.teacher.SchoolCalenderActivity;
import com.aaptrix.activitys.teacher.TeacherDairyActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import com.aaptrix.R;

import static com.aaptrix.tools.SPClass.PREFS_NAME;

/**
 * Created by google on 21/9/16.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    String userSchoolName, schoolId, userSchoolLogo, userType;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        userSchoolName = settings.getString("userSchoolName", "");
        schoolId = settings.getString("str_school_id", "");
        userSchoolLogo = settings.getString("userSchoolLogo1", "");
        userType = settings.getString("userrType", "");

        if (remoteMessage.getData() != null) {

            Bitmap bigPicture;
            Bitmap largeIcon = getBitmap(remoteMessage.getData().get("picture"));

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder nb = new NotificationCompat.Builder(this);

            nb.setAutoCancel(true);
            nb.setLargeIcon(largeIcon);
            nb.setContentTitle(remoteMessage.getData().get("title"));
            nb.setContentText(remoteMessage.getData().get("body"));
            if (!remoteMessage.getData().get("big_picture").equals("0")) {
                bigPicture = getBitmap(remoteMessage.getData().get("big_picture"));
                nb.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bigPicture));
            }
            nb.setWhen(System.currentTimeMillis());
            assert notificationManager != null;
            nb.setAutoCancel(true);
            nb.setSmallIcon(R.drawable.notification_icon);
            nb.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
            nb.setLights(Color.RED, 3000, 3000);
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                int importance = NotificationManager.IMPORTANCE_HIGH;
//                NotificationChannel notificationChannel = new NotificationChannel("00", "App Notification", importance);
//                notificationChannel.enableLights(true);
//                notificationChannel.setLightColor(Color.RED);
//                notificationChannel.enableVibration(true);
//                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400});
//                nb.setChannelId("00");
//                notificationManager.createNotificationChannel(notificationChannel);
//            }

            Intent intent;
            switch (Objects.requireNonNull(remoteMessage.getData().get("module_name"))) {
                case "What's New!": {
                    intent = new Intent(this, PublicationActivity.class); {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            nb.setChannelId("00");
                            notifChannel("What's New", "00", notificationManager);
                        }
                    }
                }
                break;
                case "Activity": {
                    if (userType.equals("Student")) {
                        intent = new Intent(this, ActivitiesActivity.class);
                    } else {
                        intent = new Intent(this, IntermidiateScreenActivity.class);
                        intent.putExtra("str_tool_title", "Activities");
                    }
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        nb.setChannelId("01");
                        notifChannel("Activity", "01", notificationManager);
                    }
                }
                break;
                case "Gallery": {
                    intent = new Intent(this, GalleryActivity.class);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        nb.setChannelId("02");
                        notifChannel("Gallery", "02", notificationManager);
                    }
                }
                break;
                case "Study Video": {
                    intent = new Intent(this, VideoLibrary.class);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        nb.setChannelId("03");
                        notifChannel("Study Video", "03", notificationManager);
                    }
                }
                break;
                case "Study Material": {
                    intent = new Intent(this, StudyMaterial.class);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        nb.setChannelId("04");
                        notifChannel("Study Material", "04", notificationManager);
                    }
                }
                break;
                case "Event": {
                    intent = new Intent(this, SchoolCalenderActivity.class);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        nb.setChannelId("05");
                        notifChannel("Event", "05", notificationManager);
                    }
                }
                break;
                case "Dairy": {
                    if (userType.equals("Student")) {
                        intent = new Intent(this, DairyActivity.class);
                    } else {
                        intent = new Intent(this, TeacherDairyActivity.class);
                    }
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        nb.setChannelId("06");
                        notifChannel("Diary", "06", notificationManager);
                    }
                }
                break;
                case "Homework": {
                    intent = new Intent(this, HomeworkActivity.class);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        nb.setChannelId("07");
                        notifChannel("Homework", "07", notificationManager);
                    }
                }
                break;
                case "Timetable": {
                    if (userType.equals("Student")) {
                        intent = new Intent(this, StudentTimeTableActivity.class);
                        intent.putExtra("loc", "dashboard");
                    } else {
                        intent = new Intent(this, IntermidiateScreenActivity.class);
                        intent.putExtra("str_tool_title", "Time Table");
                    }
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        nb.setChannelId("08");
                        notifChannel("Timetable", "08", notificationManager);
                    }
                }
                break;
                default: {
                    intent = new Intent(this, AppLogin.class);
                    intent.putExtra("status", "Online");
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        nb.setChannelId("09");
                        notifChannel("Other", "09", notificationManager);
                    }
                }
                break;
            }

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            nb.setContentIntent(pendingIntent);
            notificationManager.notify(0, nb.build());
        }
    }

    private void notifChannel(String name, String id, NotificationManager manager) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(id, name, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400});
            manager.createNotificationChannel(notificationChannel);
        }
    }

    private Bitmap getBitmap(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream stream = connection.getInputStream();
            return BitmapFactory.decodeStream(stream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}