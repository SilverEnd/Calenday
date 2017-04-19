package com.erikzuo.calendaylib.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.erikzuo.calendaylib.utility.CalendarUtils;

import java.util.Calendar;



/**
 * Created by YifanZuo on 24/08/2016.
 */
public class Event implements Parcelable {
    private String id;
    private String title;
    private long startTime, endTime, createTime;

    public Event(String id, String title, long startTime, long endTime, long createTime) {
        this.id = id;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createTime = createTime;
    }

    public String getTitle() {
        return title;
    }

    public Calendar getStart() {
        return CalendarUtils.makeCalendar(startTime);
    }

    public Calendar getEnd() {
        return CalendarUtils.makeCalendar(endTime);
    }

    public long getStartInMillis() {
        return startTime;
    }

    public long getEndInMillis() {
        return endTime;
    }

    public int getStartMinute() {
        Calendar today = (Calendar) getStart().clone();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        return (int) ((startTime - today.getTimeInMillis()) / 1000 / 60);
    }

    public int getEndMinute() {
        Calendar today = (Calendar) getEnd().clone();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        return (int) (((endTime - today.getTimeInMillis()) / 1000 / 60));
    }


    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public long getCreateTime() {
        return createTime;
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    protected Event(Parcel in) {
        id = in.readString();
        title = in.readString();
        startTime = in.readLong();
        endTime = in.readLong();
        createTime = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeLong(startTime);
        dest.writeLong(endTime);
        dest.writeLong(createTime);
    }
}
