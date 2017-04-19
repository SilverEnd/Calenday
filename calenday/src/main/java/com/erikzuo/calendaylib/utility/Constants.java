package com.erikzuo.calendaylib.utility;


import java.io.File;

/**
 * Created by YifanZuo on 16/08/2016.
 */
public class Constants {
    // Add event
    public static final int CHOOSE_CONTACT_FROM_LOCAL = 0;
    public static final int CHOOSE_CONTACT_FROM_SERVER = 1;


    // Event detail
    public static final String KEY_ATTENDEE_LIST = "attendee_list";


    // Calendar
    public static final int DAYS_PER_WEEK = 7;
    public static final int ROWS_PER_MONTH = 6;
    public static final int CELLS_PER_HOUR = 2;
    public static final int START_HOUR = 0;
    public static final int END_HOUR = 24;
    public static final int HOURS_PER_DAY = END_HOUR - START_HOUR;

    public static final int WEEK_CELL_HEIGHT = 170;

    public static final int CODE_ADD_EVENT = 0;
    public static final int CODE_EVENT_DETAIL = 1;
    public static final int CODE_EDIT_EVENT = 2;


    public static final String KEY_EVENT_DATE = "event_date";
    public static final String KEY_EVENT = "event";
    public static final String KEY_EVENT_ID = "event_id";
    public static final String KEY_EDIT_EVENT_TYPE = "edit_event_type";
    public static final String KEY_EVENT_NAME = "event_name";
    public static final String KEY_EVENT_START = "event_start";
    public static final String KEY_EVENT_END = "event_end";
    public static final String KEY_EVENT_POSITION = "event_position";

    public static final String KEY_NUM_OF_DAYS = "num_of_days";
    public static final String KEY_START_DATE = "start_date";
    public static final String KEY_PAGE_ID = "page_id";
    public static final String KEY_EVENT_LIST = "event_list";
    public static final String KEY_AXIS_COLOR = "axis_color";
    public static final String KEY_WEEKDAY_BG_COLOR = "weekday_bg_color";
    public static final String KEY_EVENT_COLOR = "event_color";
    public static final String KEY_MONTH_CELL_COLOR = "month_cell_color";
    public static final String KEY_MONTH_TEXT_COLOR = "month_cell_text_color";
    public static final String KEY_OTHER_MONTH_CELL_COLOR = "other_month_cell_color";
    public static final String KEY_OTHER_MONTH_CELL_TEXT_COLOR = "other_month_cell_text_color";
    public static final String KEY_TODAY_COLOR = "today_color";
    public static final String KEY_TIMELINE_BG_COLOR = "timeline_bg_color";
    public static final String KEY_CURREENT_TIME_COLOR = "current_time_color";
    public static final String KEY_ADD_EVENT_BTN_RES_ID = "add_event_btn_res_id";

    public static final int MAX_PAGE_NUMBER = 101;


    public static final int TYPE_INSERT = 11;
    public static final int TYPE_DELETE = 22;
    public static final int TYPE_QUERY = 33;
    public static final int TYPE_UPDATE = 44;

    //Contacts
    public static final String CONTACT_NAME_LIST = "contact_name_list";

    //Alert
    public static final String ALERT_TITLE = "Oops!";
    public static final String ALERT_MEG = "Please update at least one of your details.";


}
