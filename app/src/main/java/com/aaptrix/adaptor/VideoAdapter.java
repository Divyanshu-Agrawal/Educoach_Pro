package com.aaptrix.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

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
        TextView title, subject, desc;
        ImageView imageView;
        CardView live;
    }

    @SuppressLint("ViewHolder")
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

            holder.subject.setBackgroundColor(Color.parseColor(drawer));
            holder.subject.setTextColor(Color.parseColor(text));

            if (type.equals("live")) {
                if (objects.get(position).getStream().equals("1")) {
                    holder.live.setVisibility(View.VISIBLE);
                    holder.live.bringToFront();
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

            if (!objects.get(position).getDesc().equals("null")) {
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
