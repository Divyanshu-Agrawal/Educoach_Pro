package com.aaptrix.activitys.student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aaptrix.R;
import com.aaptrix.fragments.AvailableVideos;
import com.google.android.material.appbar.AppBarLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class OfflineMaterial extends AppCompatActivity {

    AppBarLayout appBarLayout;
    String selToolColor, selStatusColor, selTextColor1, strSubject;
    TextView tool_title, noMaterial;
    private ArrayList<String> fileArray = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_material);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        appBarLayout = findViewById(R.id.appBarLayout);
        tool_title = findViewById(R.id.tool_title);
        noMaterial = findViewById(R.id.no_material);
        listView = findViewById(R.id.listview);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        strSubject = getIntent().getStringExtra("sub");

        SharedPreferences sp = getSharedPreferences(PREFS_NAME, 0);
        userName = sp.getString("userName", "");

        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        selToolColor = settingsColor.getString("tool", "");
        selStatusColor = settingsColor.getString("status", "");
        selTextColor1 = settingsColor.getString("text1", "");

        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }
        tool_title.setTextColor(Color.parseColor(selTextColor1));
        fetchMaterial();
    }

    private void fetchMaterial() {
        fileArray.clear();
        File[] file = getExternalFilesDir(userName + "/Study Material/" + strSubject).listFiles();

        if (file != null)
            for (File value : file) {
                fileArray.add(value.getName());
            }

        swipeRefreshLayout.setRefreshing(false);
        if (fileArray.size() != 0) {
            FileAdapter adapter = new FileAdapter(this, R.layout.list_files, fileArray);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            noMaterial.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.GONE);
            noMaterial.setVisibility(View.VISIBLE);
        }
    }

    class FileAdapter extends ArrayAdapter<String> {

        private Context context;
        private int resource;
        private ArrayList<String> objects;

        public FileAdapter(@NonNull Context context, int resource, @NonNull ArrayList<String> objects) {
            super(context, resource, objects);
            this.context = context;
            this.resource = resource;
            this.objects = objects;
        }

        @SuppressLint({"ViewHolder", "ClickableViewAccessibility"})
        @NonNull
        @Override
        public View getView(int position, View view, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            view = inflater.inflate(resource, null);

            TextView name = view.findViewById(R.id.title);
            name.setText(objects.get(position));

            view.setOnClickListener(v -> {
                Intent intent = new Intent(context, OfflineMaterialView.class);
                intent.putExtra("sub", strSubject);
                intent.putExtra("file", objects.get(position));
                context.startActivity(intent);
            });

            return view;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}