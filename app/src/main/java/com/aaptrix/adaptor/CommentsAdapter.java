package com.aaptrix.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.aaptrix.R;
import com.aaptrix.databeans.CommentsData;

import org.apache.commons.lang.StringEscapeUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.aaptrix.tools.SPClass.PREFS_NAME;

public class CommentsAdapter extends ArrayAdapter<CommentsData> {

    private Context context;
    private int resource;
    private ArrayList<CommentsData> objects;

    public CommentsAdapter(@NonNull Context context, int resource, @NonNull ArrayList<CommentsData> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
        this.resource = resource;
    }

    @SuppressLint({"InflateParams", "ViewHolder"})
    @NonNull
    public View getView(final int position, View view, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        view = inflater.inflate(resource, null);

        CommentsData data = objects.get(position);

        if (data != null) {
            TextView messageText = view.findViewById(R.id.message_text);
            TextView messaged_user_name = view.findViewById(R.id.messaged_user_name);
            TextView time = view.findViewById(R.id.time);
            LinearLayout mainLayoutLogin = view.findViewById(R.id.mainLayoutLogin);
            LinearLayout chatLayout = view.findViewById(R.id.chatLayout);
            SharedPreferences settings = view.getContext().getSharedPreferences(PREFS_NAME, 0);
            String NAME = settings.getString("userName", "");

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
                Date date = sdf.parse(data.getDate());
                sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm aa", Locale.getDefault());
                String message = StringEscapeUtils.unescapeHtml(data.getComment());

                if (NAME.equals(data.getName())) {
                    messaged_user_name.setVisibility(View.GONE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        messageText.setText(Html.fromHtml(message, Html.FROM_HTML_MODE_COMPACT));
                    } else {
                        messageText.setText(Html.fromHtml(message));
                    }
                    time.setText(sdf.format(date));
                    messaged_user_name.setGravity(Gravity.START);
                    messageText.setGravity(Gravity.START);
                    time.setGravity(Gravity.END);
                    mainLayoutLogin.setGravity(Gravity.END);
                    chatLayout.setBackgroundColor(context.getResources().getColor(R.color.mybg));
                } else if (data.getName() == null || data.getComment() == null) {
                    view.setVisibility(View.GONE);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        messageText.setText(Html.fromHtml(data.getComment(), Html.FROM_HTML_MODE_COMPACT));
                    } else {
                        messageText.setText(Html.fromHtml(data.getComment()));
                    }
                    messaged_user_name.setText(data.getName());
                    time.setText(sdf.format(date));
                    messaged_user_name.setGravity(Gravity.START);
                    messageText.setGravity(Gravity.START);
                    time.setGravity(Gravity.END);
                    mainLayoutLogin.setGravity(Gravity.START);
                    chatLayout.setBackgroundColor(context.getResources().getColor(R.color.otherbg));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return view;
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
