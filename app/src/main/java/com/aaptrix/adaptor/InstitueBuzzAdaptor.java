package com.aaptrix.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import java.util.ArrayList;

import com.aaptrix.databeans.DataBeanInstitueBuzz;
import com.aaptrix.R;

import androidx.annotation.NonNull;

import static com.aaptrix.tools.SPClass.PREF_COLOR;

/**
 * Created by googledeveloper on 24/2/17.
 */
public class InstitueBuzzAdaptor extends ArrayAdapter<DataBeanInstitueBuzz> {

    private ArrayList<DataBeanInstitueBuzz> objects;
    private Context context;

    public InstitueBuzzAdaptor(Context context, int resource, ArrayList<DataBeanInstitueBuzz> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
    }


    @SuppressLint({"InflateParams", "NewApi", "SetTextI18n"})
    @NonNull
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            v = inflater.inflate(R.layout.insti_buzz_list_item, null);
        }
        final DataBeanInstitueBuzz dbItemsDist = objects.get(position);
        if (dbItemsDist != null) {

            TextView clgname = v.findViewById(R.id.clgname);
            ImageView insti_iv_img = v.findViewById(R.id.insti_iv_img);
            SharedPreferences settingsColor = context.getSharedPreferences(PREF_COLOR, 0);
            String frontColor = settingsColor.getString("drawer", "");
            String backColor = settingsColor.getString("tool", "");

            clgname.setText(dbItemsDist.getInstBuzzName());

            switch (dbItemsDist.getInstBuzzName()) {

                case "About Us":
                    Picasso.with(context).load(R.drawable.small_logo).into(insti_iv_img);
                    break;
                case "Activities": {
                    try {
                        Bitmap bigImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.activities_b);
                        Bitmap smallImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.activities_f);
                        Bitmap front = tintImage(smallImage, Color.parseColor(frontColor));
                        Bitmap back = tintImage(bigImage, Color.parseColor(backColor));
                        Bitmap mergedImages = createImage(front, back);
                        insti_iv_img.setImageBitmap(mergedImages);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
                case "Attendance": {
                    try {
                        Bitmap bigImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.attendance_b);
                        Bitmap smallImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.attendance_f);
                        Bitmap front = tintImage(smallImage, Color.parseColor(frontColor));
                        Bitmap back = tintImage(bigImage, Color.parseColor(backColor));
                        Bitmap mergedImages = createImage(front, back);
                        insti_iv_img.setImageBitmap(mergedImages);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

                case "Institute Calendar": {
                    clgname.setText("Calendar");
                    try {
                        Bitmap bigImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.institute_calendar_b);
                        Bitmap smallImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.institute_calendar_f);
                        Bitmap front = tintImage(smallImage, Color.parseColor(frontColor));
                        Bitmap back = tintImage(bigImage, Color.parseColor(backColor));
                        Bitmap mergedImages = createImage(front, back);
                        insti_iv_img.setImageBitmap(mergedImages);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

                case "What's New!": {
                    try {
                        Bitmap bigImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.publications_b);
                        Bitmap smallImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.publications_f);
                        Bitmap front = tintImage(smallImage, Color.parseColor(frontColor));
                        Bitmap back = tintImage(bigImage, Color.parseColor(backColor));
                        Bitmap mergedImages = createImage(front, back);
                        insti_iv_img.setImageBitmap(mergedImages);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

                case "Time Table": {
                    try {
                        Bitmap bigImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.time_table_b);
                        Bitmap smallImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.time_table_f);
                        Bitmap front = tintImage(smallImage, Color.parseColor(frontColor));
                        Bitmap back = tintImage(bigImage, Color.parseColor(backColor));
                        Bitmap mergedImages = createImage(front, back);
                        insti_iv_img.setImageBitmap(mergedImages);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

                case "Remarks": {
                    try {
                        Bitmap bigImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.diary_b);
                        Bitmap smallImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.diary_f);
                        Bitmap front = tintImage(smallImage, Color.parseColor(frontColor));
                        Bitmap back = tintImage(bigImage, Color.parseColor(backColor));
                        Bitmap mergedImages = createImage(front, back);
                        insti_iv_img.setImageBitmap(mergedImages);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

                case "Assignments":
                case "Courses" : {
                    try {
                        Bitmap bigImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.homework_b);
                        Bitmap smallImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.homework_f);
                        Bitmap front = tintImage(smallImage, Color.parseColor(frontColor));
                        Bitmap back = tintImage(bigImage, Color.parseColor(backColor));
                        Bitmap mergedImages = createImage(front, back);
                        insti_iv_img.setImageBitmap(mergedImages);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

                case "Results": {
                    try {
                        Bitmap bigImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.results_b);
                        Bitmap smallImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.results_f);
                        Bitmap front = tintImage(smallImage, Color.parseColor(frontColor));
                        Bitmap back = tintImage(bigImage, Color.parseColor(backColor));
                        Bitmap mergedImages = createImage(front, back);
                        insti_iv_img.setImageBitmap(mergedImages);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

                case "Study Materials": {
                    try {
                        Bitmap bigImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.study_material_b);
                        Bitmap smallImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.study_material_f);
                        Bitmap front = tintImage(smallImage, Color.parseColor(frontColor));
                        Bitmap back = tintImage(bigImage, Color.parseColor(backColor));
                        Bitmap mergedImages = createImage(front, back);
                        insti_iv_img.setImageBitmap(mergedImages);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

                case "Study Videos": {
                    try {
                        Bitmap bigImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.study_videos_b);
                        Bitmap smallImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.study_videos_f);
                        Bitmap front = tintImage(smallImage, Color.parseColor(frontColor));
                        Bitmap back = tintImage(bigImage, Color.parseColor(backColor));
                        Bitmap mergedImages = createImage(front, back);
                        insti_iv_img.setImageBitmap(mergedImages);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

                case "Gallery": {
                    try {
                        Bitmap bigImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.gallery_b);
                        Bitmap smallImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.gallery_f);
                        Bitmap front = tintImage(smallImage, Color.parseColor(frontColor));
                        Bitmap back = tintImage(bigImage, Color.parseColor(backColor));
                        Bitmap mergedImages = createImage(front, back);
                        insti_iv_img.setImageBitmap(mergedImages);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

                case "Teaching Staff": {
                    try {
                        Bitmap bigImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.staff_b);
                        Bitmap smallImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.staff_f);
                        Bitmap front = tintImage(smallImage, Color.parseColor(frontColor));
                        Bitmap back = tintImage(bigImage, Color.parseColor(backColor));
                        Bitmap mergedImages = createImage(front, back);
                        insti_iv_img.setImageBitmap(mergedImages);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

                case "Feedback": {
                    try {
                        Bitmap bigImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.feedback_b);
                        Bitmap smallImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.feedback_f);
                        Bitmap front = tintImage(smallImage, Color.parseColor(frontColor));
                        Bitmap back = tintImage(bigImage, Color.parseColor(backColor));
                        Bitmap mergedImages = createImage(front, back);
                        insti_iv_img.setImageBitmap(mergedImages);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

                case "Refer a Friend": {
                    try {
                        Bitmap bigImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.refer_b);
                        Bitmap smallImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.refer_f);
                        Bitmap front = tintImage(smallImage, Color.parseColor(frontColor));
                        Bitmap back = tintImage(bigImage, Color.parseColor(backColor));
                        Bitmap mergedImages = createImage(front, back);
                        insti_iv_img.setImageBitmap(mergedImages);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
                case "Login" : {
                    try {
                        Bitmap bigImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.login_b);
                        Bitmap smallImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.login_f);
                        Bitmap front = tintImage(smallImage, Color.parseColor(frontColor));
                        Bitmap back = tintImage(bigImage, Color.parseColor(backColor));
                        Bitmap mergedImages = createImage(front, back);
                        insti_iv_img.setImageBitmap(mergedImages);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
                case "Get More Info" : {
                    try {
                        Bitmap bigImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.info_b);
                        Bitmap smallImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.info_f);
                        Bitmap front = tintImage(smallImage, Color.parseColor(frontColor));
                        Bitmap back = tintImage(bigImage, Color.parseColor(backColor));
                        Bitmap mergedImages = createImage(front, back);
                        insti_iv_img.setImageBitmap(mergedImages);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
                case "Guest Exam": {
                    clgname.setText("Test Yourself");
                    try {
                        Bitmap bigImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.results_b);
                        Bitmap smallImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.results_f);
                        Bitmap front = tintImage(smallImage, Color.parseColor(frontColor));
                        Bitmap back = tintImage(bigImage, Color.parseColor(backColor));
                        Bitmap mergedImages = createImage(front, back);
                        insti_iv_img.setImageBitmap(mergedImages);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                case "Online Exam": {
                    try {
                        Bitmap bigImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.results_b);
                        Bitmap smallImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.results_f);
                        Bitmap front = tintImage(smallImage, Color.parseColor(frontColor));
                        Bitmap back = tintImage(bigImage, Color.parseColor(backColor));
                        Bitmap mergedImages = createImage(front, back);
                        insti_iv_img.setImageBitmap(mergedImages);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return v;
    }

    private Bitmap createImage(Bitmap frontImage, Bitmap backImage) {
        Bitmap result = Bitmap.createBitmap(frontImage.getWidth(), frontImage.getHeight(), frontImage.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(backImage, 0f, 0f, null);
        canvas.drawBitmap(frontImage, 0f, 0f, null);
        return result;
    }

    private Bitmap tintImage(Bitmap bitmap, int color) {
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        Bitmap bitmapResult = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapResult);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return bitmapResult;
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

