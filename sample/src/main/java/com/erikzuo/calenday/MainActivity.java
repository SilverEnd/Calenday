package com.erikzuo.calenday;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.erikzuo.calendaylib.listener.AddEventListener;
import com.erikzuo.calendaylib.listener.EventClickListener;
import com.erikzuo.calendaylib.model.Event;
import com.erikzuo.calendaylib.utility.Constants;
import com.erikzuo.calendaylib.view.MonthView;
import com.erikzuo.calendaylib.view.WeekView;


import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity  {


    private WeekView mWeekView, mThreeDayView;
    private MonthView mMonthView;
    private ArrayList<Event> mEventList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_calendar_tab, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_today_icon:
                if (mMonthView != null && mMonthView.getVisibility() == View.VISIBLE){
                    mMonthView.goToToday();
                }

                if (mWeekView != null && mWeekView.getVisibility() == View.VISIBLE){
                    mWeekView.goToToday();
                }

                if (mThreeDayView != null && mThreeDayView.getVisibility() == View.VISIBLE){
                    mThreeDayView.goToToday();
                }
                return true;
            case R.id.menu_display_month:
                showMonthDisplay();
                return true;
            case R.id.menu_display_week:
                showWeekDisplay();
                return true;
            case R.id.menu_display_day:
                showThreeDayDisplay();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    Event newEvent = data.getParcelableExtra(Constants.KEY_EVENT);
                    mEventList.add(newEvent);

                    refresh();


                    break;
                default:
                    break;
            }
        }
    }

    private void init() {
        mWeekView = (WeekView) findViewById(R.id.week_view);
        mMonthView = (MonthView) findViewById(R.id.month_view);
        mThreeDayView = (WeekView) findViewById(R.id.three_day_view);

        mEventList = new ArrayList<>();

        mWeekView.setEventList(mEventList);
        mMonthView.setEventList(mEventList);
        mThreeDayView.setEventList(mEventList);

        setListener();
    }

    private void setListener() {
        mWeekView.setAddEventListener(new AddEventListener() {
            @Override
            public void onAddEvent(Calendar date) {
                Intent intent = new Intent(MainActivity.this, AddEventActivity.class);
                intent.putExtra("date", date);

                startActivityForResult(intent, 0);
            }
        });

        mWeekView.setEventClickListener(new EventClickListener() {
            @Override
            public void onEventClicked(Event event) {
                Toast.makeText(MainActivity.this, "Clicked " + event.getTitle(), Toast.LENGTH_SHORT).show();

            }
        });


        mMonthView.setAddEventListener(new AddEventListener() {
            @Override
            public void onAddEvent(Calendar date) {
                Intent intent = new Intent(MainActivity.this, AddEventActivity.class);
                intent.putExtra("date", date);

                startActivityForResult(intent, 0);
            }
        });

        mMonthView.setEventClickListener(new EventClickListener() {
            @Override
            public void onEventClicked(Event event) {
                Toast.makeText(MainActivity.this, "Clicked " + event.getTitle(), Toast.LENGTH_SHORT).show();

            }
        });

        mThreeDayView.setAddEventListener(new AddEventListener() {
            @Override
            public void onAddEvent(Calendar date) {
                Intent intent = new Intent(MainActivity.this, AddEventActivity.class);
                intent.putExtra("date", date);

                startActivityForResult(intent, 0);
            }
        });

        mThreeDayView.setEventClickListener(new EventClickListener() {
            @Override
            public void onEventClicked(Event event) {
                Toast.makeText(MainActivity.this, "Clicked " + event.getTitle(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void showMonthDisplay() {
        mWeekView.setVisibility(View.GONE);
        mMonthView.setVisibility(View.VISIBLE);
        mThreeDayView.setVisibility(View.GONE);

    }

    private void showWeekDisplay() {
        mWeekView.setVisibility(View.VISIBLE);
        mMonthView.setVisibility(View.GONE);
        mThreeDayView.setVisibility(View.GONE);


    }

    private void showThreeDayDisplay() {
        mWeekView.setVisibility(View.GONE);
        mMonthView.setVisibility(View.GONE);
        mThreeDayView.setVisibility(View.VISIBLE);

    }

    private void refresh() {
        if (mMonthView != null) {
            mMonthView.refresh();
        }

        if (mWeekView != null) {
            mWeekView.refresh();
        }

        if (mThreeDayView != null) {
            mThreeDayView.refresh();
        }
    }

}
