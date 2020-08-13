package com.aaptrix.adaptor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.aaptrix.R;
import com.aaptrix.activitys.FullScrView;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.DOWNLOAD_SERVICE;

public class StudyDetailAdaptor extends ArrayAdapter<String> implements ActivityCompat.OnRequestPermissionsResultCallback {

    Context context;
    int resource;
    ArrayList<String> objects;
    private SharedPreferences sp;
    private long downloadID;
    private String permission;

    public StudyDetailAdaptor(Context context, int resource, ArrayList<String> objects, String permission) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
        this.permission = permission;
    }

    private class ViewHolder {
        TextView title;
        ImageView icon;
    }

    @SuppressLint({"ViewHolder", "ClickableViewAccessibility"})
    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        view = inflater.inflate(resource, null);
        ViewHolder holder = new ViewHolder();
        view.setTag(holder);
        if (objects != null) {
            holder.title = view.findViewById(R.id.title);
            holder.icon = view.findViewById(R.id.file_icon);
            ImageView download = view.findViewById(R.id.download_icon);
            ImageView viewIcon = view.findViewById(R.id.view_icon);
            ImageView previewImage = view.findViewById(R.id.certificate_preview_image);
            WebView previewPdf = view.findViewById(R.id.certificate_preview_pdf);
            holder.title.setText(objects.get(position));

            if (permission.equals("0")) {
                download.setVisibility(View.GONE);
            }

            if (isInternetOn()) {
                if (permission.equals("1")) {
                    holder.title.setOnClickListener(v -> {
                        if (PermissionChecker.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
                            downloadFile(objects.get(position));
                        } else {
                            isPermissionGranted();
                        }
                    });

                    holder.icon.setOnClickListener(v -> {
                        if (PermissionChecker.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
                            downloadFile(objects.get(position));
                        } else {
                            isPermissionGranted();
                        }
                    });

                    download.setOnClickListener(v -> {
                        if (PermissionChecker.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
                            downloadFile(objects.get(position));
                        } else {
                            isPermissionGranted();
                        }
                    });

                    viewIcon.setOnClickListener(v -> {
                        Intent intent = new Intent(context, FullScrView.class);
                        intent.putStringArrayListExtra("material", objects);
                        intent.putExtra("position", position);
                        intent.putExtra("permission", permission);
                        context.startActivity(intent);
                    });
                }

                previewImage.setOnClickListener(v -> {
                    Intent intent = new Intent(context, FullScrView.class);
                    intent.putStringArrayListExtra("material", objects);
                    intent.putExtra("position", position);
                    intent.putExtra("permission", permission);
                    context.startActivity(intent);
                });

                previewPdf.setOnTouchListener((v, event) -> {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        Intent intent = new Intent(context, FullScrView.class);
                        intent.putStringArrayListExtra("material", objects);
                        intent.putExtra("position", position);
                        intent.putExtra("permission", permission);
                        context.startActivity(intent);
                    }
                    return false;
                });
            } else {
                Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show();
            }

            sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            context.registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            String url = sp.getString("imageUrl", "") + sp.getString("userSchoolId", "") + "/studyMaterial/" + objects.get(position);
            String fileExt = objects.get(position).substring(objects.get(position).lastIndexOf(".") + 1);
            switch (fileExt) {
                case "pdf":
                    Picasso.with(context).load(R.drawable.pdf).into(holder.icon);
                    previewImage.setVisibility(View.GONE);
                    previewPdf.setVisibility(View.VISIBLE);
                    String pdfUrl = "https://docs.google.com/viewerng/viewer?url=" + url;
                    previewPdf.loadUrl(pdfUrl);
                    break;
                case "png":
                    Picasso.with(context).load(R.drawable.png).into(holder.icon);
                    Picasso.with(context).load(url).into(previewImage);
                    previewImage.setVisibility(View.VISIBLE);
                    previewPdf.setVisibility(View.GONE);
                    break;
                case "jpg":
                case "jpeg":
                    Picasso.with(context).load(R.drawable.jpg).into(holder.icon);
                    Picasso.with(context).load(url).into(previewImage);
                    previewImage.setVisibility(View.VISIBLE);
                    previewPdf.setVisibility(View.GONE);
                    break;
                default:
                    Picasso.with(context).load(R.drawable.file).into(holder.icon);
                    Picasso.with(context).load(url).into(previewImage);
                    previewImage.setVisibility(View.VISIBLE);
                    previewPdf.setVisibility(View.GONE);
                    break;
            }
        }
        return view;
    }

    private void downloadFile(String url) {
        String path = Environment.DIRECTORY_DOWNLOADS;
        String downloadUrl = sp.getString("imageUrl", "") + sp.getString("userSchoolId", "") + "/studyMaterial/" + url;
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl))
                .setTitle(url)
                .setDescription("Downloading")
                .setMimeType("application/octet-stream")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(path, url);
        request.allowScanningByMediaScanner();
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        assert downloadManager != null;
        downloadID = downloadManager.enqueue(request);
    }

    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctx, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadID == id) {
                Toast.makeText(context, "Download Completed", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void isPermissionGranted() {
        ActivityCompat.requestPermissions((Activity) context,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public final boolean isInternetOn() {
        ConnectivityManager connec =
                (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
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

    @Override
    public int getViewTypeCount() {
        return Math.max(getCount(), 1);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
