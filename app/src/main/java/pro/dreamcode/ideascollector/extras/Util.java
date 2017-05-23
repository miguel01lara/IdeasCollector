package pro.dreamcode.ideascollector.extras;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;

import java.util.List;

import pro.dreamcode.ideascollector.R;

/**
 * Created by migue on 28/04/2017.
 */

public class Util {
    
    public static void hideViews(List<View> views){
            for (View view : views) {
                view.setVisibility(View.GONE);
            }
    }

    public static void showViews(List<View> views){
        for (View view : views) {
            view.setVisibility(View.VISIBLE);
        }
    }

    public static void setBackground(View itemView, Drawable drawable) {
        if (Build.VERSION.SDK_INT > 15){
            itemView.setBackground(drawable);
        } else {
            itemView.setBackgroundDrawable(drawable);
        }
    }

    public static void setBullet(View itemView, boolean completed) {
        ImageView bullet = (ImageView) itemView.findViewById(R.id.iv_bulb);
        if (completed) {
            bullet.setImageResource(R.drawable.idea_bullet_completed);
        }
        else {
            bullet.setImageResource(R.drawable.idea_bullet);
        }
    }
}
