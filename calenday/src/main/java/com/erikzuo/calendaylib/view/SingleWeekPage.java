package com.erikzuo.calendaylib.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.erikzuo.calendaylib.R;
import com.erikzuo.calendaylib.listener.AddEventListener;
import com.erikzuo.calendaylib.utility.CalendarUtils;
import com.erikzuo.calendaylib.utility.Constants;
import com.erikzuo.calendaylib.listener.EventClickListener;
import com.erikzuo.calendaylib.listener.RecyclerItemClickListener;
import com.erikzuo.calendaylib.adapter.WeekCellAdapter;
import com.erikzuo.calendaylib.listener.WeekCellClickListener;
import com.erikzuo.calendaylib.model.WeekViewCell;
import com.erikzuo.calendaylib.model.Event;

import java.util.ArrayList;
import java.util.Calendar;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * Created by YifanZuo on 30/08/2016.
 */
public class SingleWeekPage extends FrameLayout {
    private Context mContext;

    // Cells
    private int mPageId;
    private ArrayList<WeekViewCell> mCellList;
    private int mSelectedCellId = -1;
    private WeekCellAdapter mWeekCellAdapter;

    private TextView mMonthHeader;
    private LinearLayout mDayHeader;
    private LinearLayout mContent;
    private WeekContentView mWeekContent;


    // Time column attributes
    private int mTimeTextColor = Color.BLACK;
    private int mTimeCellWidth = 150;
    private float mTimeCellTextHeight;
    private int mTimeTextSize = 50;


    private int mNumOfDays = 1;
    private Calendar mStartDate = Calendar.getInstance();


    private AddEventListener mAddEventListener;
    private WeekCellClickListener mEmptyCellClickListener;
    private EventClickListener mEventClickListener;

    private ArrayList<Event> mEventList;


    private int mAxisColor;
    private int mTimelineBgColor;
    private int mWeekdayBgColor;
    private int mCurrentTimeColor;
    private int mTodayColor;
    private int mEventColor;
    private int mAddEventBtnResId;


    public SingleWeekPage(Context context) {
        this(context, null);
    }

    public SingleWeekPage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SingleWeekPage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.single_week_view, this);

        mContext = context;

    }

    public void init(Bundle bundle, AddEventListener addEventListener, EventClickListener eventClickListener, WeekCellClickListener
            emptyCellClickListener) {
        if (bundle != null) {
            mNumOfDays = bundle.getInt(Constants.KEY_NUM_OF_DAYS);
            mPageId = bundle.getInt(Constants.KEY_PAGE_ID);
            mStartDate = (Calendar) bundle.getSerializable(Constants.KEY_START_DATE);
            mEventList = bundle.getParcelableArrayList(Constants.KEY_EVENT_LIST);
            mAxisColor = bundle.getInt(Constants.KEY_AXIS_COLOR);
            mTimelineBgColor = bundle.getInt(Constants.KEY_TIMELINE_BG_COLOR);
            mWeekdayBgColor = bundle.getInt(Constants.KEY_WEEKDAY_BG_COLOR);
            mTodayColor = bundle.getInt(Constants.KEY_TODAY_COLOR);
            mEventColor = bundle.getInt(Constants.KEY_EVENT_COLOR);
            mCurrentTimeColor = bundle.getInt(Constants.KEY_CURREENT_TIME_COLOR);
            mAddEventBtnResId = bundle.getInt(Constants.KEY_ADD_EVENT_BTN_RES_ID);
        }

        mAddEventListener = addEventListener;
        mEventClickListener = eventClickListener;
        mEmptyCellClickListener = emptyCellClickListener;

        mMonthHeader = (TextView) findViewById(R.id.month_header);
        mDayHeader = (LinearLayout) findViewById(R.id.day_row_container);
        mContent = (LinearLayout) findViewById(R.id.content_container);

        mMonthHeader.setText(CalendarUtils.getHeaderTextForWeek(mStartDate, mNumOfDays));
        mMonthHeader.setTextColor(Color.BLACK);
        mMonthHeader.setTextSize(14);


        setDayHeader();
        setContent();

        invalidate();

    }

    private void setDayHeader() {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                .WRAP_CONTENT);
        mDayHeader.setLayoutParams(p);
        mDayHeader.setPadding(mTimeCellWidth, 0, 0, 0);
        mDayHeader.setBackgroundColor(Color.parseColor("#f0f0f0"));


        for (int i = 0; i < mNumOfDays; i++) {
            TextView day = new TextView(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.weight = 1;
            day.setLayoutParams(params);

            day.setGravity(Gravity.CENTER);
            day.setText(CalendarUtils.getDayOfWeek(mStartDate, i) + "\n" + CalendarUtils.getDate(mStartDate, i).get(Calendar
                    .DAY_OF_MONTH));


            mDayHeader.addView(day);
        }
    }

    private void setContent() {
        Timeline timeline = new Timeline(mContext);
        timeline.setLayoutParams(new LinearLayout.LayoutParams(mTimeCellWidth, Constants.WEEK_CELL_HEIGHT * Constants.HOURS_PER_DAY *
                Constants.CELLS_PER_HOUR));

        mContent.addView(timeline);


        mCellList = new ArrayList<>();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.KEY_START_DATE, mStartDate);
        bundle.putSerializable(Constants.KEY_NUM_OF_DAYS, mNumOfDays);
        bundle.putParcelableArrayList(Constants.KEY_EVENT_LIST, mEventList);
        bundle.putInt(Constants.KEY_AXIS_COLOR, mAxisColor);
        bundle.putInt(Constants.KEY_EVENT_COLOR, mEventColor);
        bundle.putInt(Constants.KEY_CURREENT_TIME_COLOR, mCurrentTimeColor);

        mWeekContent = new WeekContentView(mContext, bundle, mEventClickListener);
        mWeekContent.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Constants.WEEK_CELL_HEIGHT *
                Constants.HOURS_PER_DAY * Constants.CELLS_PER_HOUR));
        mWeekContent.setItemAnimator(null);

        for (int i = 0; i < mNumOfDays * Constants.HOURS_PER_DAY * Constants.CELLS_PER_HOUR; i++) {
            mCellList.add(new WeekViewCell(i, false, CalendarUtils.isCellToday(mStartDate, mNumOfDays, i)));
        }

        mWeekCellAdapter = new WeekCellAdapter(mCellList, mTodayColor, mWeekdayBgColor, mAddEventBtnResId);
        mWeekContent.setAdapter(mWeekCellAdapter);
        mWeekContent.setLayoutManager(new GridLayoutManager(mContext, mNumOfDays) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        mWeekContent.setNestedScrollingEnabled(false);
        mWeekContent.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mEmptyCellClickListener != null) {
                    mEmptyCellClickListener.onCellClicked(mPageId);
                }

                handleCellClick(mCellList.get(position));

            }
        }));
        mContent.addView(mWeekContent);


        final ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_container);
        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, 7 * Constants.WEEK_CELL_HEIGHT * Constants.CELLS_PER_HOUR);
            }
        }, 0);


//        mWeekContent.scrollTo(0, 200);

    }

    private void handleCellClick(WeekViewCell cell) {
        if (mSelectedCellId != -1) {
            if (mSelectedCellId == cell.getId()) {
                prepareAddEvent(mSelectedCellId);
            } else {
                mCellList.get(mSelectedCellId).setSelected(false);
                mWeekCellAdapter.notifyItemChanged(mSelectedCellId);

                mCellList.get(cell.getId()).setSelected(true);
                mWeekCellAdapter.notifyItemChanged(cell.getId());

                mSelectedCellId = cell.getId();
            }
        } else {
            mCellList.get(cell.getId()).setSelected(true);
            mWeekCellAdapter.notifyItemChanged(cell.getId());

            mSelectedCellId = cell.getId();
        }

    }

    public void unSelectCell() {
        if (mSelectedCellId != -1) {
            mCellList.get(mSelectedCellId).setSelected(false);
            mWeekCellAdapter.notifyItemChanged(mSelectedCellId);

            mSelectedCellId = -1;
        }
    }

    private void prepareAddEvent(int position) {
        String dayOfWeek = CalendarUtils.getDayOfWeek(mStartDate, position % mNumOfDays);
        Calendar date = CalendarUtils.getDate(mStartDate, position % mNumOfDays);
        int hour = position / mNumOfDays / Constants.CELLS_PER_HOUR + Constants.START_HOUR;
        int minute = position / mNumOfDays % Constants.CELLS_PER_HOUR * (60 / Constants.CELLS_PER_HOUR);
        date.set(Calendar.HOUR_OF_DAY, hour);
        date.set(Calendar.MINUTE, minute);


        if (mAddEventListener != null) {
            mAddEventListener.onAddEvent(date);
        }

        mCellList.get(mSelectedCellId).setSelected(false);
        mWeekCellAdapter.notifyItemChanged(mSelectedCellId);
        mSelectedCellId = -1;

//        Toast.makeText(mContext, "Add event at " + getTime(hour) + ", " + dayOfWeek + " " + date.get(Calendar.DAY_OF_MONTH) + "-" + date
//                .getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()) + "-" + date.get(Calendar.YEAR), Toast.LENGTH_LONG)
//                .show();
    }

    public void refresh() {
        mWeekContent.invalidate();
    }


    class Timeline extends View {

        // Paints
        private Paint mAxisPaint;
        private Paint mTimeTextPaint;
        private Paint mTimeBackgroundPaint;

        public Timeline(Context context) {
            super(context);

            init();
        }


        private void init() {
            // Prepare axis textPaint
            mAxisPaint = new Paint();
            mAxisPaint.setStyle(Paint.Style.STROKE);
            mAxisPaint.setStrokeWidth(0);
            mAxisPaint.setColor(mAxisColor);


            // Measure settings for time column.
            mTimeTextPaint = new Paint(ANTI_ALIAS_FLAG);
            mTimeTextPaint.setTextAlign(Paint.Align.CENTER);
            mTimeTextPaint.setColor(mTimeTextColor);
            mTimeTextPaint.setTextSize(mTimeTextSize);
            Rect rect = new Rect();
            mTimeTextPaint.getTextBounds("00:00", 0, "00:00".length(), rect);
            mTimeCellTextHeight = rect.height() + 20;

            mTimeBackgroundPaint = new Paint();
            mTimeBackgroundPaint.setColor(mTimelineBgColor);

            // Prepare hour separator color textPaint


//            mHolder.addCallback(this);

        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            // Draw the time column.
            drawAxis(canvas);
            drawTimeColumn(canvas);

        }


        private void drawAxis(Canvas canvas) {
            // Prepare to iterate for each hour to draw the hour lines.
            float[] cellLines = new float[Constants.HOURS_PER_DAY * Constants.CELLS_PER_HOUR * 4];

            // Prepare the separator lines for hours.
            for (int i = 0; i < Constants.HOURS_PER_DAY * Constants.CELLS_PER_HOUR; i++) {
                cellLines[i * 4] = 0;
                cellLines[i * 4 + 1] = Constants.WEEK_CELL_HEIGHT * i;
                cellLines[i * 4 + 2] = getWidth();
                cellLines[i * 4 + 3] = Constants.WEEK_CELL_HEIGHT * i;
            }


            // Draw the lines for hours.
            canvas.drawLines(cellLines, mAxisPaint);

//            // Draw current time
//            float height = (CalendarUtils.getTimeInMins(Calendar.getInstance()) - Constants.START_HOUR * 60) / (Constants
//                    .HOURS_PER_DAY * 60f) * (Constants.HOURS_PER_DAY * Constants.CELLS_PER_HOUR * Constants.WEEK_CELL_HEIGHT);
//            Paint paint = new Paint();
//            paint.setColor(Color.BLUE);
//            paint.setStyle(Paint.Style.STROKE);
//            paint.setStrokeWidth(3);
//            canvas.drawLine(0, height, getWidth(), height, paint);
        }

        private void drawTimeColumn(Canvas canvas) {
            // Draw the background color for the time column(exclude top left time_cell)
            canvas.drawRect(
                    0,
                    0,
                    mTimeCellWidth,
                    getHeight(),
                    mTimeBackgroundPaint);

            for (int i = 0; i < Constants.HOURS_PER_DAY * Constants.CELLS_PER_HOUR; i++) {
                float top = Constants.WEEK_CELL_HEIGHT * i;

                canvas.drawText(
                        getTime(i),
                        mTimeCellWidth / 2f,
                        top + mTimeCellTextHeight,
                        mTimeTextPaint);
            }
        }

        // Get measured date and time
        private String getTime(int position) {
            int hour = position / Constants.CELLS_PER_HOUR + Constants.START_HOUR;
            int minute = position % Constants.CELLS_PER_HOUR * (60 / Constants.CELLS_PER_HOUR);

            return (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute);
        }


    }

}
