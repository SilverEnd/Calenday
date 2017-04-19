package com.erikzuo.calenday;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.erikzuo.calendaylib.model.Event;
import com.erikzuo.calendaylib.utility.Constants;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddEventActivity extends AppCompatActivity {

    private EditText mEventName;
    private TextView mDateTv, mFromTimeTv, mToTimeTv;
    private Button mSaveBtn, mCancelBtn;
    private Calendar mFromCalendar, mToCalendar;
    private SimpleDateFormat mDateFormat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        init();

    }

    private void init() {
        mEventName = (EditText) findViewById(R.id.add_event_name);
        mDateTv = (TextView) findViewById(R.id.add_event_date);
        mFromTimeTv = (TextView) findViewById(R.id.add_event_time_from);
        mToTimeTv = (TextView) findViewById(R.id.add_event_time_to);
        mSaveBtn = (Button) findViewById(R.id.add_event_save_btn);
        mCancelBtn = (Button) findViewById(R.id.add_event_cancel_btn);



        mFromCalendar = (Calendar) getIntent().getSerializableExtra(Constants.KEY_EVENT_DATE);
        if (mFromCalendar == null) {
            mFromCalendar = Calendar.getInstance();
        }
        mFromCalendar.set(Calendar.MINUTE, 30 * Math.round(mFromCalendar.get(Calendar.MINUTE) / 30f));


        mToCalendar = (Calendar) mFromCalendar.clone();
        mToCalendar.add(Calendar.MINUTE, 30);

        mDateFormat = new SimpleDateFormat("E, MMM dd, yyyy");


        setDate(mFromCalendar);
        setTime(mFromCalendar, mFromTimeTv);
        setTime(mToCalendar, mToTimeTv);

        setListener();
    }

    private void setListener() {
        mDateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddEventActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mFromCalendar.set(year, monthOfYear, dayOfMonth);
                        mToCalendar.set(year, monthOfYear, dayOfMonth);
                        setDate(mFromCalendar);
                    }
                }, mFromCalendar.get(Calendar.YEAR), mFromCalendar.get(Calendar.MONTH), mFromCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        mFromTimeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(AddEventActivity.this, new TimePickerDialog
                        .OnTimeSetListener() {


                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int
                            minute) {

                        mFromCalendar.set(mFromCalendar.get(Calendar.YEAR), mFromCalendar
                                .get(Calendar.MONTH), mFromCalendar.get(Calendar
                                .DAY_OF_MONTH), hourOfDay, minute);

                        setTime(mFromCalendar, mFromTimeTv);

                    }
                }, mFromCalendar.get(Calendar.HOUR_OF_DAY), mFromCalendar.get(Calendar.MINUTE), true).show();
            }
        });

        mToTimeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(AddEventActivity.this, new TimePickerDialog
                        .OnTimeSetListener() {


                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int
                            minute) {

                        mToCalendar.set(mToCalendar.get(Calendar.YEAR), mToCalendar
                                .get(Calendar.MONTH), mToCalendar.get(Calendar
                                .DAY_OF_MONTH), hourOfDay, minute);

                        setTime(mToCalendar, mToTimeTv);


                    }
                }, mToCalendar.get(Calendar.HOUR_OF_DAY), mToCalendar.get(Calendar.MINUTE), true).show();

            }
        });


        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFromCalendar.compareTo(mToCalendar) >= 0) {
                    Toast.makeText(AddEventActivity.this, "Please choose a valid time frame", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(mEventName.getText().toString())) {
                    Toast.makeText(AddEventActivity.this, "Please enter the event title", Toast.LENGTH_SHORT).show();
                } else {
                    Event newEvent = new Event(
                            "",
                            mEventName.getText().toString(),
                            mFromCalendar.getTimeInMillis(),
                            mToCalendar.getTimeInMillis(),
                            Calendar.getInstance().getTimeInMillis()
                    );

                    Intent result = new Intent();
                    result.putExtra(Constants.KEY_EVENT, newEvent);

                    setResult(RESULT_OK, result);
                    onBackPressed();
                }
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setDate(Calendar date) {
        mDateTv.setText(mDateFormat.format(date.getTime()));
    }

    private void setTime(Calendar date, TextView tv) {
        int hourOfDay = date.get(Calendar.HOUR_OF_DAY);
        int minute = date.get(Calendar.MINUTE);

        String time = (hourOfDay < 10 ? "0" + String.valueOf
                (hourOfDay) : String.valueOf(hourOfDay)) + " " +
                ": " + (minute < 10 ? "0" + String.valueOf
                (minute) : String.valueOf(minute));

        tv.setText(time);
    }


}
