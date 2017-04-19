package com.erikzuo.calendaylib.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.erikzuo.calendaylib.listener.AddEventListener;
import com.erikzuo.calendaylib.listener.EventClickListener;
import com.erikzuo.calendaylib.model.Event;
import com.erikzuo.calendaylib.model.EventRect;
import com.erikzuo.calendaylib.utility.CalendarUtils;
import com.erikzuo.calendaylib.utility.Constants;
import com.erikzuo.calendaylib.utility.EventUtils;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * Created by YifanZuo on 31/08/2016.
 */
public class SingleMonthPage extends View {
    private Context mContext;

    private Calendar mStartDate;
    private ArrayList<Event> mEventList;
    private Calendar mSelectedDate;


    private int mAxisColor;
    private int mWeekdayBgColor;
    private int mMonthCellColor;
    private int mMonthTextColor;
    private int mOtherMonthCellColor;
    private int mOtherMonthTextColor;
    private int mTodayColor;
    private int mEventColor;
    private int mCurrentTimeColor;
    private int mAddEventBtnResId;


    private Paint mAxisPaint, mMonthCellBgPaint, mOtherMonthCellBgPaint, mWeekdayBgPaint, mMonthHeaderBgPaint, mEventPaint;
    private Paint mWeekdayTextPaint, mMonthHeaderTextPaint, mMonthCellTextPaint, mOtherMonthCellTextPaint;
    private float mMonthHeaderHeight = 120;
    private float mWeekdayHeaderHeight = 100;
    private float mHeightPerDay;
    private float mWidthPerDay;
    private float mWeekdayTextHeight;
    private float mMonthTextHeight;
    private float mMonthCellTextHeight;
    private ArrayList<EventRect> mEventRectList;
    private GestureDetectorCompat mGestureDetector;


    private AddEventListener mAddEventListener;
    private EventClickListener mEventClickListener;


    public SingleMonthPage(Context context) {
        this(context, null);
    }

    public SingleMonthPage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SingleMonthPage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;
        mGestureDetector = new GestureDetectorCompat(mContext, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                handleClickEvent(e.getX(), e.getY());

                return super.onSingleTapConfirmed(e);
            }
        });
    }

    public void init(Bundle bundle, AddEventListener addEventListener, EventClickListener
            eventClickListener) {
        mStartDate = (Calendar) bundle.getSerializable(Constants.KEY_START_DATE);
        mEventList = bundle.getParcelableArrayList(Constants.KEY_EVENT_LIST);
        mAxisColor = bundle.getInt(Constants.KEY_AXIS_COLOR);
        mWeekdayBgColor = bundle.getInt(Constants.KEY_WEEKDAY_BG_COLOR);
        mMonthCellColor = bundle.getInt(Constants.KEY_MONTH_CELL_COLOR);
        mMonthTextColor = bundle.getInt(Constants.KEY_MONTH_TEXT_COLOR);
        mOtherMonthCellColor = bundle.getInt(Constants.KEY_OTHER_MONTH_CELL_COLOR);
        mOtherMonthTextColor = bundle.getInt(Constants.KEY_OTHER_MONTH_CELL_TEXT_COLOR);
        mTodayColor = bundle.getInt(Constants.KEY_TODAY_COLOR);
        mEventColor = bundle.getInt(Constants.KEY_EVENT_COLOR);
        mCurrentTimeColor = bundle.getInt(Constants.KEY_CURREENT_TIME_COLOR);
        mAddEventBtnResId = bundle.getInt(Constants.KEY_ADD_EVENT_BTN_RES_ID);



        mAddEventListener = addEventListener;
        mEventClickListener = eventClickListener;


        // Initialise paints
        mAxisPaint = new Paint();
        mAxisPaint.setColor(mAxisColor);

        mWeekdayBgPaint = new Paint();
        mWeekdayBgPaint.setColor(mWeekdayBgColor);

        mMonthHeaderBgPaint = new Paint();
        mMonthHeaderBgPaint.setColor(Color.WHITE);

        mMonthCellBgPaint = new Paint();
        mMonthCellBgPaint.setColor(mMonthCellColor);

        mOtherMonthCellBgPaint = new Paint();
        mOtherMonthCellBgPaint.setColor(mOtherMonthCellColor);


        Rect bound = new Rect();
        Paint.FontMetricsInt fontMetrics;

        mWeekdayTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWeekdayTextPaint.setColor(Color.BLACK);
        mWeekdayTextPaint.setTextSize(50);
        mWeekdayTextPaint.setTextAlign(Paint.Align.CENTER);
        mWeekdayTextPaint.getTextBounds("Mon", 0, "Mon".length(), bound);
        fontMetrics = mWeekdayTextPaint.getFontMetricsInt();
        mWeekdayTextHeight = bound.height() + fontMetrics.bottom / 3 + fontMetrics.top / 3;

        mMonthHeaderTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMonthHeaderTextPaint.setColor(Color.BLACK);
        mMonthHeaderTextPaint.setTextSize(60);
        mMonthHeaderTextPaint.setTextAlign(Paint.Align.CENTER);
        mMonthHeaderTextPaint.getTextBounds("Mon", 0, "Mon".length(), bound);
        fontMetrics = mMonthHeaderTextPaint.getFontMetricsInt();
        mMonthTextHeight = bound.height() + fontMetrics.bottom / 2f + fontMetrics.top / 2f;


        mMonthCellTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMonthCellTextPaint.setColor(mMonthTextColor);
        mMonthCellTextPaint.setTextSize(50);
        mMonthCellTextPaint.setTextAlign(Paint.Align.RIGHT);
        mMonthCellTextPaint.getTextBounds("00", 0, "00".length(), bound);
        fontMetrics = mMonthCellTextPaint.getFontMetricsInt();
        mMonthCellTextHeight = bound.height() + fontMetrics.bottom / 2 + fontMetrics.top / 2;

        mOtherMonthCellTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOtherMonthCellTextPaint.setColor(mOtherMonthTextColor);
        mOtherMonthCellTextPaint.setTextSize(50);
        mOtherMonthCellTextPaint.setTextAlign(Paint.Align.RIGHT);
        mOtherMonthCellTextPaint.getTextBounds("00", 0, "00".length(), bound);


        mEventPaint = new Paint();
        mEventPaint.setColor(mEventColor);
        mEventRectList = new ArrayList<>();
    }

    // Draw everything!
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mWidthPerDay = getWidth() / Constants.DAYS_PER_WEEK;
        mHeightPerDay = (getHeight() - mMonthHeaderHeight - mWeekdayHeaderHeight) / Constants.ROWS_PER_MONTH;

        drawMonthHeader(canvas);
        drawWeekdayHeader(canvas);
        drawMonthCell(canvas);
        drawAxis(canvas);
        drawEvents(canvas);
    }

    private void drawMonthHeader(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth(), mMonthHeaderHeight, mMonthHeaderBgPaint);
        canvas.drawText(CalendarUtils.getHeaderTextForMonth(mStartDate), getWidth() / 2, mMonthHeaderHeight / 2 +
                mMonthTextHeight, mMonthHeaderTextPaint);
    }

    private void drawWeekdayHeader(Canvas canvas) {
        canvas.drawRect(0, mMonthHeaderHeight, getWidth(), mMonthHeaderHeight + mWeekdayHeaderHeight, mWeekdayBgPaint);

        for (int i = 0; i < Constants.DAYS_PER_WEEK; i++) {
            float x = i * mWidthPerDay + mWidthPerDay / 2;
            float y = mMonthHeaderHeight + mWeekdayHeaderHeight / 2 + mWeekdayTextHeight;
            canvas.drawText(CalendarUtils.getWeekday(i), x, y, mWeekdayTextPaint);
        }
    }

    private void drawAxis(Canvas canvas) {
        // Prepare to iterate for each hour to draw the hour lines.
        float[] cellLines = new float[Constants.ROWS_PER_MONTH * 4];
        float[] dayLines = new float[Constants.DAYS_PER_WEEK * 4];

        // Prepare horizontal separator lines.
        for (int i = 0; i < Constants.ROWS_PER_MONTH; i++) {
            cellLines[i * 4] = 0;
            cellLines[i * 4 + 1] = mHeightPerDay * i + mMonthHeaderHeight + mWeekdayHeaderHeight;
            cellLines[i * 4 + 2] = getWidth();
            cellLines[i * 4 + 3] = mHeightPerDay * i + mMonthHeaderHeight + mWeekdayHeaderHeight;
        }

        // Prepare vertical separator lines.
        for (int i = 0; i < Constants.DAYS_PER_WEEK; i++) {
            dayLines[i * 4] = mWidthPerDay * i;
            dayLines[i * 4 + 1] = +mMonthHeaderHeight;
            dayLines[i * 4 + 2] = mWidthPerDay * i;
            dayLines[i * 4 + 3] = getHeight();
        }

        // Draw the lines for hours.
        canvas.drawLines(cellLines, mAxisPaint);
        canvas.drawLines(dayLines, mAxisPaint);
    }

    private void drawMonthCell(Canvas canvas) {
        for (int i = 0; i < Constants.DAYS_PER_WEEK * Constants.ROWS_PER_MONTH; i++) {
            Calendar day = CalendarUtils.getDate(mStartDate, i);
            int month = CalendarUtils.getEndDayOfFirstWeek(mStartDate, Constants.DAYS_PER_WEEK).get(Calendar.MONTH);

            int row = i / Constants.DAYS_PER_WEEK;
            int col = i % Constants.DAYS_PER_WEEK;

            float left = col * mWidthPerDay;
            float right = (col + 1) * mWidthPerDay;
            float top = row * mHeightPerDay + mMonthHeaderHeight + mWeekdayHeaderHeight;
            float bottom = top + mHeightPerDay;

            if (day.get(Calendar.MONTH) != month) {
//                if (CalendarUtils.isSameDay(day, Calendar.getInstance())) {
//                    Paint paint = new Paint();
//                    paint.setColor(Color.parseColor("#fffdd0"));
//
//                    canvas.drawRect(
//                            left,
//                            top,
//                            right,
//                            bottom,
//                            paint);
//                } else {
                    canvas.drawRect(
                            left,
                            top,
                            right,
                            bottom,
                            mOtherMonthCellBgPaint);
//                }


                canvas.drawText(
                        day.get(Calendar.DAY_OF_MONTH) + "",
                        right - mWidthPerDay / 8,
                        top + mHeightPerDay / 6 + mMonthCellTextHeight,
                        mOtherMonthCellTextPaint);
            } else {
                if (CalendarUtils.isSameDay(day, Calendar.getInstance())) {
                    Paint paint = new Paint();
                    paint.setColor(mTodayColor);

                    canvas.drawRect(
                            left,
                            top,
                            right,
                            bottom,
                            paint);
                }else {
                    canvas.drawRect(
                            left,
                            top,
                            right,
                            bottom,
                            mMonthCellBgPaint);
                }

                canvas.drawText(
                        day.get(Calendar.DAY_OF_MONTH) + "",
                        right - mWidthPerDay / 8,
                        top + mHeightPerDay / 6 + mMonthCellTextHeight,
                        mMonthCellTextPaint);
            }


        }


    }

    private void drawEvents(Canvas canvas) {
        mEventRectList.clear();

        mEventList = EventUtils.sortEvents(mEventList);
        mEventRectList = EventUtils.getEventRectsForMonthView(mEventList, mStartDate, mWidthPerDay, mHeightPerDay, mMonthHeaderHeight +
                mWeekdayHeaderHeight);

        for (EventRect eventRect : mEventRectList) {
            canvas.drawRect(eventRect.drawingBox, mEventPaint);
        }
    }


    // Handle touch event

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    private void handleClickEvent(float x, float y) {
        if (y > mMonthHeaderHeight + mWeekdayHeaderHeight && y < getHeight()) {
            mSelectedDate = getDateByPosition(x, y);

            showSingleDayDialog();
        }
    }


    private Calendar getDateByPosition(float x, float y) {
        int row = (int) ((y - mMonthHeaderHeight - mWeekdayHeaderHeight) / mHeightPerDay);
        int column = (int) (x / mWidthPerDay);

        Log.d("row", row + "");
        Log.d("col", column + "");


        return CalendarUtils.getDate(mStartDate, row * Constants.DAYS_PER_WEEK + column);
    }

    private void showSingleDayDialog() {
        Dialog dialog = new Dialog(mContext);
        SingleWeekPage page = new SingleWeekPage(mContext);

        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.KEY_START_DATE, mSelectedDate);
        bundle.putSerializable(Constants.KEY_NUM_OF_DAYS, 1);
        bundle.putSerializable(Constants.KEY_PAGE_ID, 0);
        bundle.putParcelableArrayList(Constants.KEY_EVENT_LIST, mEventList);
        bundle.putInt(Constants.KEY_AXIS_COLOR, mAxisColor);
        bundle.putInt(Constants.KEY_WEEKDAY_BG_COLOR, mWeekdayBgColor);
        bundle.putInt(Constants.KEY_TODAY_COLOR, mTodayColor);
        bundle.putInt(Constants.KEY_EVENT_COLOR, mEventColor);
        bundle.putInt(Constants.KEY_CURREENT_TIME_COLOR, mCurrentTimeColor);
        bundle.putInt(Constants.KEY_ADD_EVENT_BTN_RES_ID, mAddEventBtnResId);

        page.init(bundle, mAddEventListener, mEventClickListener, null);

        dialog.setContentView(page);
        dialog.show();

        Point size = new Point();
        dialog.getWindow().getWindowManager().getDefaultDisplay().getSize(size);

        int width = size.x;
        int height = size.y;

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = (int) (width / 1.25f);
        params.height = (int) (height / 1.25f);
        params.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(params);
        dialog.setCanceledOnTouchOutside(true);

    }

    public void refresh() {
        invalidate();
    }


}
