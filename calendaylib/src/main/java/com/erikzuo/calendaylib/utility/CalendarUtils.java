package com.erikzuo.calendaylib.utility;


import java.util.Calendar;
import java.util.Locale;

/**
 * Created by YifanZuo on 31/08/2016.
 */
public class CalendarUtils {
    public static Calendar getEndDayOfFirstWeek(Calendar startDate, int numOfDays) {
        Calendar endDay = (Calendar) startDate.clone();
        endDay.add(Calendar.DAY_OF_YEAR, numOfDays);

        return endDay;
    }

    public static String getHeaderTextForWeek(Calendar startDate, int numOfDays) {
        Calendar endDay = getEndDayOfFirstWeek(startDate, numOfDays);

        String header = startDate.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        if (startDate.get(Calendar.YEAR) != endDay.get(Calendar.YEAR)) {
            header += " " + startDate.get(Calendar.YEAR);
        } else if (startDate.get(Calendar.MONTH) != endDay.get(Calendar.MONTH)) {
            header += "/" + endDay.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + " " + endDay.get(Calendar.YEAR);
        } else {
            header += " " + endDay.get(Calendar.YEAR);
        }

        return header;
    }

    public static String getHeaderTextForMonth(Calendar startDate) {
        Calendar endDay = getEndDayOfFirstWeek(startDate, Constants.DAYS_PER_WEEK);

        String header = endDay.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        header += " " + endDay.get(Calendar.YEAR);

        return header;
    }


    public static String getDayOfWeek(Calendar startDate, int position) {
        Calendar dayOfWeek = (Calendar) startDate.clone();
        dayOfWeek.add(Calendar.DAY_OF_WEEK, position);

        return dayOfWeek.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
    }


    public static Calendar getDate(Calendar startDate, int position) {
        Calendar date = (Calendar) startDate.clone();
        date.add(Calendar.DAY_OF_YEAR, position);

        return date;
    }

    public static boolean isSameDay(Calendar day1, Calendar day2) {
        return day1.get(Calendar.YEAR) == day2.get(Calendar.YEAR) && day1.get(Calendar.DAY_OF_YEAR) == day2
                .get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isCellToday(Calendar startDate, int numOfDays, int position) {
        int daysToStart = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) - startDate.get(Calendar.DAY_OF_YEAR);


        if (daysToStart < numOfDays && daysToStart >= 0) {
            int column = position % numOfDays;

            if (daysToStart == column) {
                return true;
            }
        }

        return false;

    }

    public static String getWeekday(int index) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        calendar.add(Calendar.DAY_OF_WEEK, index);

        return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
    }

//    public static Calendar makeCalendar(DateTime dateTime) {
//
//
//        Date date = new Date(dateTime.getValue());
//        Calendar result = Calendar.getInstance();
//        result.setTime(date);
//
//        return result;
//    }

    public static Calendar makeCalendar(long time) {
        Calendar result = Calendar.getInstance();
        result.setTimeInMillis(time);

        return result;
    }

    public static int getTimeInMins(Calendar time) {
        return time.get(Calendar.HOUR_OF_DAY) * 60 + time.get(Calendar.MINUTE);
    }
}
