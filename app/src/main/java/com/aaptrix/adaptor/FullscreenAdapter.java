package com.aaptrix.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import com.aaptrix.R;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import static com.aaptrix.tools.SPClass.PREFS_NAME;

public class FullscreenAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<String> studymaterial;
    private TextView watermark;
    private LinearLayout notice;
    private PDFView pdfView;

    public FullscreenAdapter(Context context, ArrayList<String> studymaterial) {
        this.context = context;
        this.studymaterial = studymaterial;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") final View view = layoutInflater.inflate(R.layout.fullscr_view, null);
        PhotoView photoView = view.findViewById(R.id.fullscr_image);
        pdfView = view.findViewById(R.id.fullscr_pdf);
        watermark = view.findViewById(R.id.watermark);
        notice = view.findViewById(R.id.notice);
        ImageView dismiss = view.findViewById(R.id.dismiss);
        notice.bringToFront();

        dismiss.setOnClickListener(v -> notice.setVisibility(View.GONE));

        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String rollNo = sp.getString("unique_id", "");

        if (sp.getString("userrType", "").equals("Guest")) {
            rollNo = context.getResources().getString(R.string.app_name);
        }

        String url = sp.getString("imageUrl", "") + sp.getString("userSchoolId", "") + "/studyMaterial/" + studymaterial.get(position);
        String fileExt = studymaterial.get(position).substring(studymaterial.get(position).lastIndexOf(".") + 1);

        watermark.setText(rollNo);
        watermark.bringToFront();
        setTimer();

        if ("pdf".equals(fileExt)) {
            photoView.setVisibility(View.GONE);
            pdfView.setVisibility(View.VISIBLE);
            new Thread(() -> {
                try {
                    downloadFile(url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            Picasso.with(context).load(url).into(photoView);
            photoView.setVisibility(View.VISIBLE);
            pdfView.setVisibility(View.GONE);
        }
        container.addView(view);
        return view;
    }

    private void setTimer() {
        new CountDownTimer(15000, 15000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                Random random = new Random();
                int color = Color.argb(100, random.nextInt(256), random.nextInt(256), random.nextInt(256));
                watermark.setTextColor(color);
                watermark.bringToFront();
                start();
            }
        }.start();
    }

    private void downloadFile(String url) {
        try {
            URL u = new URL(url);
            u.openConnection();
            DataInputStream stream = new DataInputStream(u.openStream());
            pdfView.fromStream(stream).load();
        } catch(IOException e) {
            // swallow a 404
        }
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return studymaterial.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
