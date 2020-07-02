package com.aaptrix.adaptor;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aaptrix.R;
import com.aaptrix.activitys.guest.StartGuestExam;
import com.aaptrix.databeans.OnlineExamData;

import java.util.ArrayList;

public class GuestExamAdapter extends ArrayAdapter<OnlineExamData> {

    private Context context;
    private int resource;
    private ArrayList<OnlineExamData> objects;

    public GuestExamAdapter(@NonNull Context context, int resource, @NonNull ArrayList<OnlineExamData> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @SuppressLint({"ViewHolder", "InflateParams", "SetTextI18n"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(resource, null);

        TextView examName = view.findViewById(R.id.exam_name);
        TextView examDate = view.findViewById(R.id.exam_date);
        TextView examTime = view.findViewById(R.id.exam_time);
        TextView examDuration = view.findViewById(R.id.exam_duration);
        TextView sub = view.findViewById(R.id.status);

        examDate.setVisibility(View.GONE);
        examTime.setVisibility(View.GONE);
        examDuration.setVisibility(View.GONE);
        sub.setBackgroundColor(Color.parseColor("#4C4C4C"));
        sub.setGravity(Gravity.CENTER);
        sub.setTextColor(Color.WHITE);
        examName.setPadding(10, 20, 10, 20);
        sub.setPadding(0, 10, 0, 10);

        if (objects != null) {
            OnlineExamData data = objects.get(position);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                examName.setText(Html.fromHtml(data.getName(), Html.FROM_HTML_MODE_COMPACT));
            } else {
                examName.setText(Html.fromHtml(data.getName()));
            }
            sub.setText("Course : " + data.getSubject());

            view.setOnClickListener(v1 -> {
                LayoutInflater factory = LayoutInflater.from(context);
                @SuppressLint("InflateParams") final View v = factory.inflate(R.layout.guest_exam_dialog, null);
                AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.DialogTheme);

                alert.setView(v).setPositiveButton("Proceed",
                        (dialog, whichButton) -> {
                            Intent intent = new Intent(context, StartGuestExam.class);
                            intent.putExtra("examId", data.getId());
                            intent.putExtra("examName", data.getName());
                            intent.putExtra("course", data.getSubject());
                            context.startActivity(intent);
                        }).setNegativeButton("Cancel", null);
                AlertDialog alertDialog = alert.create();
                alertDialog.show();
                Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                Button theButton1 = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                theButton.setTextColor(context.getResources().getColor(R.color.text_gray));
                theButton1.setTextColor(context.getResources().getColor(R.color.text_gray));
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setEnabled(true);
            });
        }
        return view;
    }
}
