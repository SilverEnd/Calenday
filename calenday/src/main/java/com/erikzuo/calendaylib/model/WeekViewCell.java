package com.erikzuo.calendaylib.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by YifanZuo on 26/08/2016.
 */
public class WeekViewCell implements Parcelable {
    private int id;
    private boolean isSelected;
    private boolean isToday;

    public WeekViewCell(int id, boolean isSelected, boolean isToday) {
        this.id = id;
        this.isSelected = isSelected;
        this.isToday = isToday;
    }

    public int getId() {
        return id;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public boolean isToday() {
        return isToday;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    protected WeekViewCell(Parcel in) {
        id = in.readInt();
        isSelected = in.readByte() != 0;
        isToday = in.readByte() != 0;

    }

    public static final Creator<WeekViewCell> CREATOR = new Creator<WeekViewCell>() {
        @Override
        public WeekViewCell createFromParcel(Parcel in) {
            return new WeekViewCell(in);
        }

        @Override
        public WeekViewCell[] newArray(int size) {
            return new WeekViewCell[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeByte((byte) (isSelected ? 1 : 0));
        dest.writeByte((byte) (isToday ? 1 : 0));

    }
}
