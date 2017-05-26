package pro.dreamcode.ideascollector.widgets;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import pro.dreamcode.ideascollector.AppIdeasCollector;
import pro.dreamcode.ideascollector.R;

/**
 * Created by migue on 23/05/2017.
 */

public class CollectorDatePicker extends LinearLayout implements View.OnTouchListener {

    private static final int TOP = 1;
    private static final int BOTTOM = 3;
    private static final int MESSAGE_WHAT = 123;
    private static final long DELAY = 200;
    private static final long DELAY2 = 130;
    private static final long DELAY3 = 80;
    private static final long INIT_DELAY = 800;
    private long times = 0;

    private boolean increment;
    private boolean decrement;
    private int activeTxv;

    private TextView monthPicker;
    private TextView dayPicker;
    private TextView yearPicker;
    private Calendar calendar;
    private SimpleDateFormat simpleDateFotmat;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            ++times;
            if (increment) {
                increment(activeTxv);
            } else if (decrement) {
                decrement(activeTxv);
            }

            long delay = DELAY;

             if (times > 31){
                delay = DELAY3;
            } else if (times > 8) {
                delay = DELAY2;
            }

            if (increment || decrement) {
                handler.removeMessages(MESSAGE_WHAT);
                handler.sendEmptyMessageDelayed(MESSAGE_WHAT, delay);
            }

            return true;
        }
    });

    public CollectorDatePicker(Context context) {
        super(context);
        init(context);
    }

    public CollectorDatePicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CollectorDatePicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.date_picker_widget, this);
        calendar = Calendar.getInstance();
        simpleDateFotmat = new SimpleDateFormat("MMM");
    }

    private void update(int date, int month, int year, int hour, int minute, int second) {
        calendar.set(Calendar.DATE, date);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.HOUR, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        yearPicker.setText(String.valueOf(year));
        dayPicker.setText(String.valueOf(date));
        monthPicker.setText(simpleDateFotmat.format(calendar.getTime()));
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("super", super.onSaveInstanceState());
        bundle.putInt("date", calendar.get(Calendar.DATE));
        bundle.putInt("month", calendar.get(Calendar.MONTH));
        bundle.putInt("year", calendar.get(Calendar.YEAR));
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle){
            Bundle bundle = (Bundle) state;
            state = bundle.getParcelable("super");
            int date = bundle.getInt("date");
            int month = bundle.getInt("month");
            int year = bundle.getInt("year");
            update(date, month, year, 0, 0, 0);
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        dayPicker = (TextView) this.findViewById(R.id.day_picker);
        monthPicker = (TextView) this.findViewById(R.id.month_picker);
        yearPicker = (TextView) this.findViewById(R.id.year_picker);

        AppIdeasCollector.setRalewayThin(getContext(), dayPicker, monthPicker, yearPicker);

        dayPicker.setOnTouchListener(this);
        monthPicker.setOnTouchListener(this);
        yearPicker.setOnTouchListener(this);

        int date = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        update(date, month, year, 0, 0, 0);
    }

    public long getTime(){
        return  calendar.getTimeInMillis();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (v.getId()){
            case  R.id.day_picker:
                processTouchAcorddingly(dayPicker, event);
                break;
            case R.id.month_picker:
                processTouchAcorddingly(monthPicker, event);
                break;
            case R.id.year_picker:
                processTouchAcorddingly(yearPicker, event);
                break;
        }

        return true;
    }

    private void processTouchAcorddingly(TextView txv, MotionEvent event){

        Drawable [] drawables = txv.getCompoundDrawables();
        if (hasBottomDrawable(drawables) && hasTopDrawable(drawables)){
            Rect topBounds = drawables[TOP].getBounds();
            Rect bottomBounds = drawables[BOTTOM].getBounds();
            float x = event.getX();
            float y = event.getY();
            activeTxv = txv.getId();
            times = 0;

            if (topDrawableHit(txv, topBounds.height(), x, y)){

                if (isActionDown(event)){
                    increment = true;
                    toggleDrawables(txv, true);
                    increment(txv.getId());
                    handler.removeMessages(MESSAGE_WHAT);
                    handler.sendEmptyMessageDelayed(MESSAGE_WHAT, INIT_DELAY);

                } else if (isActionUpOrCancel(event)){
                    increment = false;
                    toggleDrawables(txv, false);
                }

            } else if(bottomDrawableHit(txv, bottomBounds.height(), x, y)){

                if (isActionDown(event)){
                    decrement = true;
                    toggleDrawables(txv, true);
                    decrement(txv.getId());
                    handler.removeMessages(MESSAGE_WHAT);
                    handler.sendEmptyMessageDelayed(MESSAGE_WHAT, INIT_DELAY);

                } else if (isActionUpOrCancel(event)){
                    decrement = false;
                    toggleDrawables(txv, false);
                }

            } else {
                increment = false;
                decrement = false;
                toggleDrawables(txv, false);
            }
        }
    }

    private void decrement(int txvId) {
        switch (txvId){
            case R.id.day_picker:
                calendar.add(Calendar.DATE, -1);
                break;
            case R.id.month_picker:
                calendar.add(Calendar.MONTH, -1);
                break;
            case R.id.year_picker:
                calendar.add(Calendar.YEAR, -1);
                break;
        }
        set();
    }

    private void increment(int txvId) {
        switch (txvId){
            case R.id.day_picker:
                calendar.add(Calendar.DATE, 1);
                break;
            case R.id.month_picker:
                calendar.add(Calendar.MONTH, 1);
                break;
            case R.id.year_picker:
                calendar.add(Calendar.YEAR, 1);
                break;
        }
        set();
    }

    private void set() {
        int date = calendar.get(Calendar.DATE);
        int year = calendar.get(Calendar.YEAR);

        dayPicker.setText(date + "");
        yearPicker.setText(year + "");
        monthPicker.setText(simpleDateFotmat.format(calendar.getTime()));
    }

    private boolean isActionDown(MotionEvent event){
        return event.getAction() == MotionEvent.ACTION_DOWN;
    }

    private boolean isActionUpOrCancel(MotionEvent event) {
        return event.getAction() == MotionEvent.ACTION_UP
                || event.getAction() == MotionEvent.ACTION_CANCEL;
    }

    private boolean topDrawableHit(TextView txv, int drawableHeight, float x, float y){
        float xMax = txv.getWidth();
        float xMin = 0;
        float yMax = drawableHeight;
        float yMin = 0;

        boolean r = x < xMax && x > xMin && y < yMax && y > yMin;
        return r;
    }

    private boolean bottomDrawableHit(TextView txv, int drawableHeight, float x, float y){
        float xMax = txv.getWidth();
        float xMin = 0;
        float yMax = txv.getHeight();
        float yMin = txv.getHeight() - drawableHeight;

        boolean r = x < xMax && x > xMin && y < yMax && y > yMin;
        return r;
    }

    private boolean hasTopDrawable(Drawable[] drawables){
        return drawables[TOP] != null;
    }

    private boolean hasBottomDrawable(Drawable[] drawables){
        return drawables[BOTTOM] != null;
    }

    private void toggleDrawables(TextView txv, boolean pressed){
        if (pressed){
            if (increment){
                txv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.up_pressed, 0, R.drawable.down_normal);

            }else if (decrement){
                txv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.up_normal, 0, R.drawable.down_pressed);
            }
        } else {
                txv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.up_normal, 0, R.drawable.down_normal);
        }
    }
}
