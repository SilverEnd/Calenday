package com.erikzuo.calendaylib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.erikzuo.calendaylib.R;
import com.erikzuo.calendaylib.listener.AddEventListener;
import com.erikzuo.calendaylib.listener.EventClickListener;
import com.erikzuo.calendaylib.listener.WeekCellClickListener;
import com.erikzuo.calendaylib.model.Event;
import com.erikzuo.calendaylib.utility.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by YifanZuo on 12/08/2016.
 */

public class WeekView extends FrameLayout {


    private Context mContext;


    private int mAxisColor;
    private int mTimelineBgColor;
    private int mWeekdayBgColor;
    private int mCurrentTimeColor;
    private int mTodayColor;
    private int mEventColor;
    private int mAddEventBtnResId;

    private int mPrevSelectedPageId = -1;


    // General attributes
    private int mNumOfDays = 7;
    private Calendar mStartDate;

    private ViewPager mViewPager;
    private WeekViewPagerAdapter mPagerAdapter;

    private List<SingleWeekPage> mWeekViewList;

    private ArrayList<Event> mEventList;


    private AddEventListener mAddEventListener;
    private WeekCellClickListener mEmptyCellClickListener;
    private EventClickListener mEventClickListener;

    public WeekView(Context context) {
        this(context, null);
    }

    public WeekView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeekView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.week_view, this);

        // Get the attribute values (if any).
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WeekView);
        try {
            mNumOfDays = a.getInteger(R.styleable.WeekView_numOfDays, 7);

            mAxisColor = a.getColor(R.styleable.WeekView_weekAxisColor, Color.parseColor("#bbbbbb"));
            mTimelineBgColor = a.getColor(R.styleable.WeekView_timelineBackground, Color.parseColor("#f0f0f0"));
            mWeekdayBgColor = a.getColor(R.styleable.WeekView_weekWeekdayBackground, Color.parseColor("#f0f0f0"));
            mTodayColor = a.getColor(R.styleable.WeekView_weekTodayColor, Color.parseColor("#fffdd0"));
            mEventColor = a.getColor(R.styleable.WeekView_weekEventColor, Color.parseColor("#ff0000"));
            mCurrentTimeColor = a.getColor(R.styleable.WeekView_currentTimeColor, Color.parseColor("#0000ff"));
            mAddEventBtnResId = a.getResourceId(R.styleable.WeekView_addEventBtn, R.drawable.ic_add_white);

        } finally {
            a.recycle();
        }

        init();
    }


    private void init() {
        mStartDate = Calendar.getInstance();
        if (mNumOfDays == 7) {
            mStartDate.add(Calendar.DAY_OF_MONTH, Calendar.MONDAY - mStartDate.get(Calendar.DAY_OF_WEEK));
        }

        mWeekViewList = new ArrayList<>();
        for (int i = 0; i < Constants.MAX_PAGE_NUMBER; i++) {
            mWeekViewList.add(i, null);
        }


        mViewPager = (ViewPager) findViewById(R.id.week_view_pager);
        mPagerAdapter = new WeekViewPagerAdapter();
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(Constants.MAX_PAGE_NUMBER / 2, false);


        mEmptyCellClickListener = new WeekCellClickListener() {
            @Override
            public void onCellClicked(int pageId) {
                if (mPrevSelectedPageId != -1 && mPrevSelectedPageId != pageId) {
                    SingleWeekPage page = mWeekViewList.get(mPrevSelectedPageId);

                    if (page != null) {
                        page.unSelectCell();
                    }

                }
                mPrevSelectedPageId = pageId;
            }
        };

    }

    public void setAddEventListener(AddEventListener addEventListener) {
        this.mAddEventListener = addEventListener;
    }


    public void setEventClickListener(EventClickListener eventClickListener) {
        this.mEventClickListener = eventClickListener;
    }

    // Add and draw event
    public void refresh() {
        SingleWeekPage page = mWeekViewList.get(mViewPager.getCurrentItem());

        if (page != null) {
            page.refresh();
        }
    }


    public void setEventList(ArrayList<Event> events) {
        this.mEventList = events;
    }

    public void goToToday() {
        mViewPager.setCurrentItem(Constants.MAX_PAGE_NUMBER / 2, true);
    }

    class WeekViewPagerAdapter extends PagerAdapter {


        @Override
        public int getCount() {
            return mWeekViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            SingleWeekPage page = new SingleWeekPage(mContext);

            Calendar startOfWeek = (Calendar) mStartDate.clone();
            startOfWeek.add(Calendar.DAY_OF_MONTH, mNumOfDays * (position - Constants.MAX_PAGE_NUMBER / 2));

            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.KEY_START_DATE, startOfWeek);
            bundle.putSerializable(Constants.KEY_NUM_OF_DAYS, mNumOfDays);
            bundle.putSerializable(Constants.KEY_PAGE_ID, position);
            bundle.putParcelableArrayList(Constants.KEY_EVENT_LIST, mEventList);
            bundle.putInt(Constants.KEY_AXIS_COLOR, mAxisColor);
            bundle.putInt(Constants.KEY_TIMELINE_BG_COLOR, mTimelineBgColor);
            bundle.putInt(Constants.KEY_WEEKDAY_BG_COLOR, mWeekdayBgColor);
            bundle.putInt(Constants.KEY_TODAY_COLOR, mTodayColor);
            bundle.putInt(Constants.KEY_EVENT_COLOR, mEventColor);
            bundle.putInt(Constants.KEY_CURREENT_TIME_COLOR, mCurrentTimeColor);
            bundle.putInt(Constants.KEY_ADD_EVENT_BTN_RES_ID, mAddEventBtnResId);

            page.init(bundle, mAddEventListener, mEventClickListener, mEmptyCellClickListener);
            mWeekViewList.set(position, page);

            container.addView(page);

            return page;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mWeekViewList.get(position));
        }
    }


}