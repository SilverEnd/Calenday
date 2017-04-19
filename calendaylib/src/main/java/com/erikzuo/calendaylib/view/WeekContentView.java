package com.erikzuo.calendaylib.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.MotionEvent;

import com.erikzuo.calendaylib.utility.CalendarUtils;
import com.erikzuo.calendaylib.utility.Constants;
import com.erikzuo.calendaylib.utility.EventUtils;
import com.erikzuo.calendaylib.listener.EventClickListener;
import com.erikzuo.calendaylib.model.Event;
import com.erikzuo.calendaylib.model.EventRect;

import java.util.ArrayList;
import java.util.Calendar;
/**
 * Created by YifanZuo on 30/08/2016.
 */
public class WeekContentView extends RecyclerView {

    private Context mContext;
    private Calendar mStartDate;

    private Paint mAxisPaint;
    private Paint mEventPaint;
    private TextPaint mEventTextPaint;


    private int mAxisColor;
    private int mEventColor;
    private int mCurrentTimeColor;


    private ArrayList<Event> mEventList;
    private ArrayList<EventRect> mEventRectList;


    private int mNumOfDays;
    private float mWidthPerDay;


    private EventClickListener mEventClickListener;


    public WeekContentView(Context context, Bundle bundle, EventClickListener listener) {
        super(context);

        this.mContext = context;
        this.mNumOfDays = bundle.getInt(Constants.KEY_NUM_OF_DAYS);
        this.mStartDate = (Calendar) bundle.getSerializable(Constants.KEY_START_DATE);
        this.mEventList = bundle.getParcelableArrayList(Constants.KEY_EVENT_LIST);
        mAxisColor = bundle.getInt(Constants.KEY_AXIS_COLOR);
        mEventColor = bundle.getInt(Constants.KEY_EVENT_COLOR);
        mCurrentTimeColor = bundle.getInt(Constants.KEY_CURREENT_TIME_COLOR);

        this.mEventClickListener = listener;

        init();
    }

    private void init() {
        // Prepare axis textPaint
        mAxisPaint = new Paint();
        mAxisPaint.setStyle(Paint.Style.STROKE);
        mAxisPaint.setStrokeWidth(0);
        mAxisPaint.setColor(mAxisColor);

        // Prepare event textPaint
        mEventTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        mEventTextPaint.setTextAlign(Paint.Align.LEFT);
        mEventTextPaint.setColor(Color.WHITE);
        mEventTextPaint.setTextSize((int) TypedValue.applyDimension(TypedValue
                .COMPLEX_UNIT_SP, 15, mContext.getResources().getDisplayMetrics()));


        mEventPaint = new Paint();
        mEventPaint.setColor(mEventColor);

        mEventRectList = new ArrayList<>();

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                float x = ev.getX();
                float y = ev.getY();

                for (EventRect eventRect : mEventRectList) {

                    if (eventRect.eventBox.contains((int) x, (int) y)) {
                        if (mEventClickListener != null) {
                            mEventClickListener.onEventClicked(eventRect.event);
                        }

                        return true;
                    }
                }

                break;
            default:
                break;
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        super.dispatchDraw(canvas);

        mWidthPerDay = getWidth() / mNumOfDays;

        // Draw axis/separators.
        drawAxis(canvas);
    }


    private void drawAxis(Canvas canvas) {
        // Prepare to iterate for each hour to draw the hour lines.
        float[] cellLines = new float[Constants.HOURS_PER_DAY * Constants.CELLS_PER_HOUR * 4];
        float[] dayLines = new float[mNumOfDays * 4];

        // Prepare the separator lines for hours.
        for (int i = 0; i < Constants.HOURS_PER_DAY * Constants.CELLS_PER_HOUR; i++) {
            cellLines[i * 4] = 0;
            cellLines[i * 4 + 1] = Constants.WEEK_CELL_HEIGHT * i;
            cellLines[i * 4 + 2] = getWidth();
            cellLines[i * 4 + 3] = Constants.WEEK_CELL_HEIGHT * i;
        }

        // Prepare the separator lines for days.
        for (int i = 0; i < mNumOfDays; i++) {
            dayLines[i * 4] = mWidthPerDay * i;
            dayLines[i * 4 + 1] = 0;
            dayLines[i * 4 + 2] = mWidthPerDay * i;
            dayLines[i * 4 + 3] = getHeight();
        }

        // Draw the lines for hours.
        canvas.drawLines(cellLines, mAxisPaint);
        canvas.drawLines(dayLines, mAxisPaint);


        // Draw current time
        float left = (Calendar.getInstance().get(Calendar.DAY_OF_YEAR) - mStartDate.get(Calendar.DAY_OF_YEAR)) * mWidthPerDay;
        float right = left + mWidthPerDay;
        float top = (CalendarUtils.getTimeInMins(Calendar.getInstance()) - Constants.START_HOUR * 60) / (Constants
                .HOURS_PER_DAY * 60f) * (Constants.HOURS_PER_DAY * Constants.CELLS_PER_HOUR * Constants.WEEK_CELL_HEIGHT);
        float radius = 20;

        if (mNumOfDays == 7){
            radius = 12;
        }


        drawEvents(canvas);

        // Draw current time indicator
        Paint paint = new Paint();
        paint.setColor(mCurrentTimeColor);
        paint.setStrokeWidth(5);


        if (left >= 0 && left < getWidth()) {
            canvas.drawLine(left, top, right, top, paint);
            canvas.drawCircle(left, top, radius, paint);
        }

    }



    private void drawEvents(Canvas canvas) {
        mEventRectList.clear();

        mEventList = EventUtils.sortEvents(mEventList);
        mEventRectList = EventUtils.getEventRectsForWeekView(mEventList, mWidthPerDay, mStartDate, mNumOfDays);


        for (EventRect eventRect : mEventRectList) {
            canvas.drawRect(eventRect.drawingBox, mEventPaint);
            eventRect.drawTitle(canvas, mEventTextPaint);
        }

    }




}