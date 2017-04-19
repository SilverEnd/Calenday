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

import com.erikzuo.calendaylib.listener.AddEventListener;
import com.erikzuo.calendaylib.utility.Constants;
import com.erikzuo.calendaylib.listener.EventClickListener;
import com.erikzuo.calendaylib.R;
import com.erikzuo.calendaylib.model.Event;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * Created by YifanZuo on 31/08/2016.
 */
public class MonthView extends FrameLayout {
    private Context mContext;

    // General attributes
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


    private Calendar mStartDate;
    private ViewPager mViewPager;
    private ArrayList<Event> mEventList;
    private ArrayList<SingleMonthPage> mMonthFragmentList;
    private AddEventListener mAddEventListener;
    private EventClickListener mEventClickListener;


    public MonthView(Context context) {
        this(context, null);
    }

    public MonthView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MonthView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.month_view, this);


        // Get the attribute values (if any).
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MonthView);
        try {
            mAxisColor = a.getColor(R.styleable.MonthView_monthAxisColor, Color.parseColor("#bbbbbb"));
            mWeekdayBgColor = a.getColor(R.styleable.MonthView_monthWeekdayBackground, Color.parseColor("#f0f0f0"));
            mMonthCellColor = a.getColor(R.styleable.MonthView_monthCellColor, Color.parseColor("#eeeeee"));
            mMonthTextColor = a.getColor(R.styleable.MonthView_monthTextColor, Color.parseColor("#eeeeee"));
            mOtherMonthCellColor = a.getColor(R.styleable.MonthView_otherMonthCellColor, Color.parseColor("#000000"));
            mOtherMonthTextColor = a.getColor(R.styleable.MonthView_otherMonthTextColor, Color.parseColor("#aaaaaa"));
            mTodayColor = a.getColor(R.styleable.MonthView_monthTodayColor, Color.parseColor("#fffdd0"));
            mEventColor = a.getColor(R.styleable.MonthView_monthEventColor, Color.parseColor("#ff0000"));
            mCurrentTimeColor = a.getColor(R.styleable.MonthView_monthCurrentTimeColor, Color.parseColor("#0000ff"));
            mAddEventBtnResId = a.getResourceId(R.styleable.MonthView_monthAddEventBtn, R.drawable.ic_add_white);

        } finally {
            a.recycle();
        }


        init();
    }

    private void init() {
        mStartDate = Calendar.getInstance();
        mStartDate.set(Calendar.DAY_OF_MONTH, 1);

        mMonthFragmentList = new ArrayList<>();
        for (int i = 0; i < Constants.MAX_PAGE_NUMBER; i++) {
            mMonthFragmentList.add(i, null);
        }

        mViewPager = (ViewPager) findViewById(R.id.week_view_pager);
        MonthPagerAdapter mPagerAdapter = new MonthPagerAdapter();
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(Constants.MAX_PAGE_NUMBER / 2, false);

    }

    public void setEventList(ArrayList<Event> eventList) {
        this.mEventList = eventList;
    }

    public void refresh() {
        SingleMonthPage fragment = mMonthFragmentList.get(mViewPager.getCurrentItem());

        if (fragment != null) {
            fragment.refresh();
        }
    }

    public void setEventClickListener(EventClickListener eventClickListener) {
        this.mEventClickListener = eventClickListener;
    }

    public void setAddEventListener(AddEventListener addEventListener) {
        this.mAddEventListener = addEventListener;
    }

    public void goToToday() {
        mViewPager.setCurrentItem(Constants.MAX_PAGE_NUMBER / 2, true);
    }

    private class MonthPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mMonthFragmentList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            SingleMonthPage page = new SingleMonthPage(mContext);

            Calendar startOfMonth = (Calendar) mStartDate.clone();
            startOfMonth.add(Calendar.MONTH, (position - Constants.MAX_PAGE_NUMBER / 2));
            startOfMonth.set(Calendar.DAY_OF_MONTH, 1);

            if (startOfMonth.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                int dayGap = startOfMonth.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
                dayGap = dayGap < 0 ? 7 + dayGap: dayGap;
                startOfMonth.add(Calendar.DAY_OF_YEAR, -dayGap);
            }

            Bundle bundle  = new Bundle();
            bundle.putSerializable(Constants.KEY_START_DATE, startOfMonth);
            bundle.putParcelableArrayList(Constants.KEY_EVENT_LIST, mEventList);
            bundle.putInt(Constants.KEY_AXIS_COLOR, mAxisColor);
            bundle.putInt(Constants.KEY_WEEKDAY_BG_COLOR, mWeekdayBgColor);
            bundle.putInt(Constants.KEY_MONTH_CELL_COLOR, mMonthCellColor);
            bundle.putInt(Constants.KEY_MONTH_TEXT_COLOR, mMonthTextColor);
            bundle.putInt(Constants.KEY_OTHER_MONTH_CELL_COLOR, mOtherMonthCellColor);
            bundle.putInt(Constants.KEY_OTHER_MONTH_CELL_TEXT_COLOR, mOtherMonthTextColor);
            bundle.putInt(Constants.KEY_TODAY_COLOR, mTodayColor);
            bundle.putInt(Constants.KEY_EVENT_COLOR, mEventColor);
            bundle.putInt(Constants.KEY_CURREENT_TIME_COLOR, mCurrentTimeColor);
            bundle.putInt(Constants.KEY_ADD_EVENT_BTN_RES_ID, mAddEventBtnResId);

            page.init(bundle, mAddEventListener, mEventClickListener);
            mMonthFragmentList.set(position, page);

            container.addView(page);

            return page;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mMonthFragmentList.get(position));
        }
    }
}
