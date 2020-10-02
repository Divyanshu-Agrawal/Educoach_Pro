package com.aaptrix.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aaptrix.activitys.student.PlayLiveStream;
import com.aaptrix.activitys.student.VideoDetails;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.aaptrix.databeans.VideosData;
import com.aaptrix.R;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class VideoAdapter extends ArrayAdapter<VideosData> {

    private ArrayList<VideosData> objects;
    private Context context;
    private int resource;
    private String type;

    public VideoAdapter(Context context, int resource, ArrayList<VideosData> objects, String type) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
        this.type = type;
    }

    private static class ViewHolder {
        TextView title, subject, desc, startAt;
        ImageView imageView;
        CardView live, start;
    }

    @SuppressLint({"ViewHolder", "SetTextI18n"})
    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        view = inflater.inflate(resource, null);
        ViewHolder holder = new ViewHolder();
        view.setTag(holder);

        SharedPreferences sp = context.getSharedPreferences(PREF_COLOR, Context.MODE_PRIVATE);
        String drawer = sp.getString("drawer", "");
        String text = sp.getString("text1", "");

        if (objects != null) {
            holder.title = view.findViewById(R.id.video_title);
            holder.imageView = view.findViewById(R.id.videoImage);
            holder.subject = view.findViewById(R.id.video_subject);
            holder.desc = view.findViewById(R.id.video_desc);
            holder.live = view.findViewById(R.id.live_video_indicator);
            holder.startAt = view.findViewById(R.id.start_at);
            holder.start = view.findViewById(R.id.start_time);

            holder.subject.setBackgroundColor(Color.parseColor(drawer));
            holder.subject.setTextColor(Color.parseColor(text));

            if (type.equals("live")) {
                if (objects.get(position).getStream().equals("1")) {
                    holder.live.setVisibility(View.VISIBLE);
                    holder.live.bringToFront();
                }
            }

            view.setOnClickListener(v -> {
                if (type.equals("video")) {
                    try {
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
                        String start = objects.get(position).getStart();
                        Date startdate = sdf.parse(start);
                        sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        Date date = sdf.parse(start);
                        Intent intent = new Intent(context, VideoDetails.class);
                        intent.putExtra("title", objects.get(position).getTitle());
                        intent.putExtra("url", objects.get(position).getUrl());
                        intent.putExtra("id", objects.get(position).getId());
                        intent.putExtra("desc", objects.get(position).getDesc());
                        intent.putExtra("endDate", objects.get(position).getEnd());
                        intent.putExtra("tags", objects.get(position).getTags());
                        intent.putExtra("subject", objects.get(position).getSubject());
                        intent.putExtra("time", objects.get(position).getTotalTime());
                        if (!start.equals("0000-00-00 00:00:00")) {
                            if (calendar.getTime().equals(startdate) || (calendar.getTime().after(startdate))) {
                                context.startActivity(intent);
                            } else if (calendar.getTime().before(date)) {
                                sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm aa", Locale.getDefault());
                                Toast.makeText(context, "Starts at " + sdf.format(startdate), Toast.LENGTH_SHORT).show();
                            } else {
                                sdf = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
                                Toast.makeText(context, "Starts at " + sdf.format(startdate), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            context.startActivity(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (objects.get(position).getStream().equals("1")) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        Intent intent = new Intent(context, PlayLiveStream.class);
                        intent.putExtra("title", objects.get(position).getTitle());
                        intent.putExtra("url", objects.get(position).getUrl());
                        intent.putExtra("id", objects.get(position).getId());
                        intent.putExtra("desc", objects.get(position).getDesc());
                        intent.putExtra("comments", objects.get(position).getComments());
                        intent.putExtra("sub", objects.get(position).getSubject());
                        intent.putExtra("date", sdf.format(Calendar.getInstance().getTime()));
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, "Streaming is ended", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            if (type.equals("video")) {
                try {
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
                    String start = objects.get(position).getStart();
                    Date startdate = sdf.parse(start);
                    assert startdate != null;
                    sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date d = sdf.parse(start);
                    String cal = sdf.format(calendar.getTime());
                    if (!start.equals("0000-00-00 00:00:00")) {
                        if (cal.equals(sdf.format(d))) {
                            if (calendar.getTime().before(startdate)) {
                                sdf = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
                                String date = sdf.format(startdate);
                                holder.start.setVisibility(View.VISIBLE);
                                holder.startAt.setText("Starts At : " + date);
                            }
                        } else {
                            if (calendar.getTime().before(startdate)) {
                                sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm aa", Locale.getDefault());
                                String date = sdf.format(startdate);
                                holder.start.setVisibility(View.VISIBLE);
                                holder.startAt.setText("Starts At : " + date);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            holder.title.setText(objects.get(position).getTitle());
            if (objects.get(position).getSubject().equals("0")) {
                holder.subject.setText("All Subjects");
            } else {
                if (!objects.get(position).getSubject().equals("Null"))
                    holder.subject.setText(objects.get(position).getSubject());
                else
                    holder.subject.setVisibility(View.GONE);
            }

            if (objects.get(position).getDesc().length() > 1 && !objects.get(position).getDesc().equals("null")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    holder.desc.setText(Html.fromHtml(objects.get(position).getDesc(), Html.FROM_HTML_MODE_COMPACT));
                } else {
                    holder.desc.setText(Html.fromHtml(objects.get(position).getDesc()));
                }
            } else {
                holder.desc.setVisibility(View.GONE);
            }

            if (objects.get(position).getUrl().contains("youtube") || objects.get(position).getUrl().contains("youtu.be")) {
                holder.imageView.setVisibility(View.VISIBLE);
                String thumbUrl = "http://img.youtube.com/vi/" + videoId(objects.get(position).getUrl())
                        + "/mqdefault.jpg";
                Picasso.with(context).load(thumbUrl).placeholder(R.drawable.youtube).error(R.drawable.youtube).into(holder.imageView);
            } else {
                holder.imageView.setVisibility(View.VISIBLE);
                Picasso.with(context).load(R.drawable.app_logo).into(holder.imageView);
                holder.imageView.setBackgroundColor(Color.WHITE);
            }
        }
        return view;
    }

    private String videoId(String url) {
        int index = url.indexOf("v=");
        String id = url.substring(index + 2, index + 13);
        if (id.equals("ttps://yout")) {
            id = url.split("/")[3];
        }
        return id;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
