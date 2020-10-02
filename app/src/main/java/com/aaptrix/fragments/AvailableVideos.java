package com.aaptrix.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.aaptrix.R;
import com.aaptrix.activitys.student.OfflineVideosPlay;

import java.io.File;
import java.util.ArrayList;

public class AvailableVideos extends Fragment {

    private Context context;
    private String strSubject;
    private ArrayList<String> fileArray = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private TextView noVideos;

    public AvailableVideos() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        this.context = context;
        super.onAttach(context);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_available_videos, container, false);
        listView = view.findViewById(R.id.listview);
        noVideos = view.findViewById(R.id.no_videos);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        assert getArguments() != null;
        strSubject = getArguments().getString("sub");
        swipeRefreshLayout.setRefreshing(true);
        fetchVideos();

        swipeRefreshLayout.setOnRefreshListener(this::fetchVideos);

        return view;
    }

    private void fetchVideos() {
        fileArray.clear();
        File[] file = context.getExternalFilesDir("Videos/" + strSubject).listFiles();

        if (file != null)
            for (File value : file) {
                fileArray.add(value.getName());
            }

        swipeRefreshLayout.setRefreshing(false);
        if (fileArray.size() != 0) {
            FileAdapter adapter = new FileAdapter(context, R.layout.list_files, fileArray);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            listView.setVisibility(View.VISIBLE);
            noVideos.setVisibility(View.GONE);
        } else {
            listView.setVisibility(View.GONE);
            noVideos.setVisibility(View.VISIBLE);
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
                Intent intent = new Intent(context, OfflineVideosPlay.class);
                intent.putExtra("sub", strSubject);
                intent.putExtra("file", objects.get(position));
                context.startActivity(intent);
            });

            return view;
        }
    }
}
