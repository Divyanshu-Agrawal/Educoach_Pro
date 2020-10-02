package com.aaptrix.adaptor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aaptrix.activitys.student.StudyMaterialDetail;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import com.aaptrix.R;
import com.aaptrix.activitys.FullScrView;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.DOWNLOAD_SERVICE;

public class StudyDetailAdaptor extends ArrayAdapter<String> implements ActivityCompat.OnRequestPermissionsResultCallback {

    Context context;
    int resource;
    ArrayList<String> objects;
    private String permission, strSubject;
    ProgressDialog mProgressDialog;
    private SharedPreferences sp;

    public StudyDetailAdaptor(Context context, int resource, ArrayList<String> objects, String permission, String strSubject) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
        this.permission = permission;
        this.strSubject = strSubject;
    }

    private static class ViewHolder {
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

            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage("Downloading...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);

            if (isInternetOn()) {
                if (permission.equals("1")) {
                    holder.title.setOnClickListener(v -> {
                        if (PermissionChecker.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
                            DownloadTask downloadTask = new DownloadTask(context);
                            downloadTask.execute(objects.get(position));
                        } else {
                            isPermissionGranted();
                        }
                    });

                    holder.icon.setOnClickListener(v -> {
                        if (PermissionChecker.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
                            DownloadTask downloadTask = new DownloadTask(context);
                            downloadTask.execute(objects.get(position));
                        } else {
                            isPermissionGranted();
                        }
                    });

                    download.setOnClickListener(v -> {
                        if (PermissionChecker.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
                            DownloadTask downloadTask = new DownloadTask(context);
                            downloadTask.execute(objects.get(position));
                        } else {
                            isPermissionGranted();
                        }
                    });

                    viewIcon.setOnClickListener(v -> {
                        Intent intent = new Intent(context, FullScrView.class);
                        intent.putStringArrayListExtra("material", objects);
                        intent.putExtra("position", position);
                        intent.putExtra("permission", permission);
                        intent.putExtra("sub", strSubject);
                        context.startActivity(intent);
                    });
                }

                previewImage.setOnClickListener(v -> {
                    Intent intent = new Intent(context, FullScrView.class);
                    intent.putStringArrayListExtra("material", objects);
                    intent.putExtra("position", position);
                    intent.putExtra("permission", permission);
                    intent.putExtra("sub", strSubject);
                    context.startActivity(intent);
                });

                previewPdf.setOnTouchListener((v, event) -> {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        Intent intent = new Intent(context, FullScrView.class);
                        intent.putStringArrayListExtra("material", objects);
                        intent.putExtra("position", position);
                        intent.putExtra("permission", permission);
                        intent.putExtra("sub", strSubject);
                        context.startActivity(intent);
                    }
                    return false;
                });
            } else {
                Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show();
            }

            sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
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

    @SuppressLint("StaticFieldLeak")
    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {

            String downloadUrl = sUrl[0];
            String[] splitUrl = downloadUrl.split("/");
            String name = splitUrl[splitUrl.length - 1];

            downloadUrl = sp.getString("imageUrl", "") + sp.getString("userSchoolId", "") + "/studyMaterial/" + downloadUrl;

            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                String ext = name.substring(name.lastIndexOf(".") + 1);
                File outputFile = File.createTempFile(name.replace(ext, "").replace(".", ""), "." + ext, context.getCacheDir());
                URL url = new URL(downloadUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(outputFile);

                byte[] data = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    if (fileLength > 0)
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
                fileEncrypt(outputFile.getName(), name);
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mProgressDialog.dismiss();
            if (result != null) {
                Toast.makeText(context, "Error: " + result, Toast.LENGTH_LONG).show();
                Log.e("error", result);
            }
            else
                Toast.makeText(context,"File downloaded", Toast.LENGTH_SHORT).show();
        }

        private void fileEncrypt(String fileName, String outputName) throws Exception {
            String key = context.getSharedPreferences(PREFS_NAME, 0).getString("video_key", "aaptrixtechnopvt");

            File file = new File(context.getCacheDir(), fileName);
            int size = (int) file.length();
            byte[] bytes = new byte[size];

            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] bKey = key.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec keySpec = new SecretKeySpec(bKey, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(bKey);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] decrypted = cipher.doFinal(bytes);
            File outputFile = new File(context.getExternalFilesDir("Study Material/" + strSubject), outputName);

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile));
            bos.write(decrypted);
            bos.flush();
            bos.close();
        }
    }

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
