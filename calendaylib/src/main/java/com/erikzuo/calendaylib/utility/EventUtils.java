package com.erikzuo.calendaylib.utility;

import com.erikzuo.calendaylib.model.Event;
import com.erikzuo.calendaylib.model.EventRect;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
 

/**
 * Created by YifanZuo on 31/08/2016.
 */
public class EventUtils {

    /**
     * Sorts the events in ascending order.
     *
     * @param events The events to be sorted.
     */
    public static ArrayList<Event> sortEvents(ArrayList<Event> events) {
        Collections.sort(events, new Comparator<Event>() {
            @Override
            public int compare(Event event1, Event event2) {
                long start1 = event1.getStartMinute();
                long start2 = event2.getStartMinute();
                int comparator = start1 > start2 ? 1 : (start1 < start2 ? -1 : 0);

                if (comparator == 0) {
                    long end1 = event1.getEndMinute();
                    long end2 = event2.getEndMinute();
                    comparator = end1 > end2 ? 1 : (end1 < end2 ? -1 : 0);
                }

                if (comparator == 0) {
                    long create1 = event1.getCreateTime();
                    long create2 = event2.getCreateTime();
                    comparator = create1 > create2 ? 1 : (create1 < create2 ? -1 : 0);
                }

                return comparator;
            }
        });

        return events;
    }

    /**
     * Puts overlapping events into different groups and calculate the event bounds for each event in that group.
     *
     * @param events    The event list to be calculated.
     * @param numOfDays
     */
    public static ArrayList<EventRect> getEventRectsForWeekView(List<Event> events, float widthPerDay, Calendar startDate, int
            numOfDays) {
        ArrayList<EventRect> eventRectList = new ArrayList<>();
        ArrayList<Event> weekEvents = new ArrayList<>();

        for (Event event : events) {
            if (isInSameWeek(startDate, event, numOfDays)) {
                weekEvents.add(event);
            }
        }

        // Make "overlapping groups" for all events that overlap with other.
        List<List<Event>> overlapGroups = new ArrayList<>();
        for (Event event : weekEvents) {
            boolean isPlaced = false;
            outerLoop:
            for (List<Event> overlapGroup : overlapGroups) {
                for (Event groupEvent : overlapGroup) {
                    if (isEventsOverlap(groupEvent, event)) {
                        overlapGroup.add(event);
                        isPlaced = true;
                        break outerLoop;
                    }
                }
            }
            if (!isPlaced) {
                List<Event> newGroup = new ArrayList<>();
                newGroup.add(event);
                overlapGroups.add(newGroup);
            }
        }

        for (List<Event> overlapGroup : overlapGroups) {
            eventRectList.addAll(expandOverlapGroup(overlapGroup, widthPerDay, startDate));
        }

        return eventRectList;
    }

    /**
     * Calculate event bounds for each event in the same overlapping group and build eventRect for each event accordingly.
     *
     * @param overlapGroup The group of events which overlap with each other.
     */
    private static ArrayList<EventRect> expandOverlapGroup(List<Event> overlapGroup, float widthPerDay, Calendar startDate) {
        ArrayList<EventRect> mEventRectList = new ArrayList<>();

        // Expand the events to maximum possible width.
        List<List<Event>> columns = new ArrayList<>();
        columns.add(new ArrayList<Event>());
        for (Event event : overlapGroup) {
            boolean isPlaced = false;

            for (List<Event> column : columns) {
                if (column.size() == 0) {
                    column.add(event);
                    isPlaced = true;
                } else if (!isEventsOverlap(event, column.get(column.size() - 1))) {
                    column.add(event);
                    isPlaced = true;
                    break;
                }
            }
            if (!isPlaced) {
                List<Event> newColumn = new ArrayList<>();
                newColumn.add(event);
                columns.add(newColumn);
            }
        }


        // Calculate left and right position for all the events.
        int maxRowCount = 0;
        for (List<Event> column : columns) {
            maxRowCount = Math.max(maxRowCount, column.size());
        }
        for (int i = 0; i < maxRowCount; i++) {
            // Set the left and right values of the event.
            float j = 0;
            for (List<Event> column : columns) {
                if (column.size() >= i + 1) {
                    Event event = column.get(i);

                    int dayGap = event.getStart().get(Calendar.DAY_OF_WEEK) - startDate.get(Calendar.DAY_OF_WEEK);
                    if (dayGap < 0) {
                        dayGap = 7 + dayGap;
                    }
                    float startOffset = dayGap * widthPerDay;

                    float left = startOffset + j / columns.size() * widthPerDay;
                    float right = left + widthPerDay / columns.size();
                    float top = (int) ((event.getStartMinute() - Constants.START_HOUR * 60f) / (Constants.HOURS_PER_DAY * 60f) * (Constants
                            .HOURS_PER_DAY * Constants.CELLS_PER_HOUR * Constants.WEEK_CELL_HEIGHT));
                    float bottom = (int) ((event.getEndMinute() - Constants.START_HOUR * 60f) / (Constants.HOURS_PER_DAY * 60f) * (Constants
                            .HOURS_PER_DAY * Constants.CELLS_PER_HOUR * Constants.WEEK_CELL_HEIGHT));


                    mEventRectList.add(new EventRect(event, left, top, right, bottom));
                }

                j++;
            }
        }

        return mEventRectList;
    }


    public static ArrayList<EventRect> getEventRectsForMonthView(List<Event> events, Calendar startDate, float widthPerDay, float
            heightPerDay, float startTop) {
        ArrayList<EventRect> eventRectList = new ArrayList<>();
        ArrayList<Event> monthEvents = new ArrayList<>();
        for (Event event : events) {
            if (isInSameMonth(startDate, event)) {
                monthEvents.add(event);
            }
        }

        // Make "overlapping groups" for all events that overlap with other.
        List<List<Event>> overlapGroups = new ArrayList<>();
        for (Event event : monthEvents) {
            boolean isPlaced = false;
            outerLoop:
            for (List<Event> overlapGroup : overlapGroups) {
                for (Event groupEvent : overlapGroup) {
                    if (isEventsOverlap(groupEvent, event)) {
                        overlapGroup.add(event);
                        isPlaced = true;
                        break outerLoop;
                    }
                }
            }
            if (!isPlaced) {
                List<Event> newGroup = new ArrayList<>();
                newGroup.add(event);
                overlapGroups.add(newGroup);
            }
        }

        for (List<Event> overlapGroup : overlapGroups) {
            eventRectList.add(collapseOverlapGroup(overlapGroup, startDate, widthPerDay, heightPerDay, startTop));
        }

        return eventRectList;
    }


    private static EventRect collapseOverlapGroup(List<Event> overlapGroup, Calendar startDate, float widthPerDay, float heightPerDay,
                                                  float startTop) {
        EventRect eventRect = null;

        int dayGap = overlapGroup.get(0).getStart().get(Calendar.DAY_OF_YEAR) - startDate.get(Calendar.DAY_OF_YEAR);

        int row = dayGap / 7;
        int column = dayGap % 7;

        float left = column * widthPerDay;
        float right = left + widthPerDay / 2f;

        float startHeight = Integer.MAX_VALUE;
        float endHeight = Integer.MIN_VALUE;

        for (Event event : overlapGroup) {
            float start = event.getStartMinute();
            float end = event.getEndMinute();

            if (start < startHeight) {
                startHeight = start;
            }

            if (end > endHeight) {
                endHeight = end;
            }
        }

        float bottom = startTop + row * heightPerDay + endHeight * (heightPerDay / (24f * 60f));
        float top = startTop + row * heightPerDay + startHeight * (heightPerDay / (24f * 60f));

        Calendar from = (Calendar) startDate.clone();
        from.add(Calendar.DAY_OF_YEAR, row * Constants.DAYS_PER_WEEK + column);

        eventRect = new EventRect(
                null,
                left,
                top,
                right,
                bottom);

        return eventRect;
    }


    /**
     * Checks if two events overlap.
     *
     * @param event1 The first event.
     * @param event2 The second event.
     * @return true if the events overlap.
     */
    private static boolean isEventsOverlap(Event event1, Event event2) {
        long start1 = event1.getStartInMillis();
        long end1 = event1.getEndInMillis();
        long start2 = event2.getStartInMillis();
        long end2 = event2.getEndInMillis();
        return (start1 < end2) && (end1 > start2) && isSameDay(event1, event2);
    }

    public static ArrayList<Event> getEventsOfSingleDay(ArrayList<Event> events, Calendar date) {
        ArrayList<Event> result = new ArrayList<>();

        for (Event event : events) {
            if (CalendarUtils.isSameDay(date, event.getStart())) {
                result.add(event);
            }
        }

        return result;
    }

    private static boolean isSameDay(Event event1, Event event2) {
        return (event1.getStart().get(Calendar.YEAR) == event2.getStart().get(Calendar.YEAR)) && (event1.getStart().get(Calendar
                .DAY_OF_YEAR) == event2.getStart().get(Calendar.DAY_OF_YEAR));
    }

    private static boolean isInSameWeek(Calendar startDate, Event event, int numOfDays) {
        int dayGap = event.getStart().get(Calendar.DAY_OF_YEAR) - startDate.get(Calendar.DAY_OF_YEAR);

        return dayGap >= 0 && dayGap < numOfDays && startDate.get(Calendar.YEAR) == event.getStart().get(Calendar.YEAR);
    }

    private static boolean isInSameMonth(Calendar startDate, Event event) {
        int dayGap = event.getStart().get(Calendar.DAY_OF_YEAR) - startDate.get(Calendar.DAY_OF_YEAR);

        return dayGap >= 0 && dayGap < Constants.DAYS_PER_WEEK * Constants.ROWS_PER_MONTH && startDate.get(Calendar.YEAR) == event
                .getStart().get(Calendar.YEAR);
    }


}
