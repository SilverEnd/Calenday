package com.erikzuo.calendaylib.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.erikzuo.calendaylib.utility.Constants;
import com.erikzuo.calendaylib.R;
import com.erikzuo.calendaylib.model.WeekViewCell;

import java.util.ArrayList;


/**
 * Created by YifanZuo on 30/08/2016.
 */
public class WeekCellAdapter extends RecyclerView.Adapter<WeekCellAdapter.ViewHolder> {

    private ArrayList<WeekViewCell> mCellList;
    private int mTodayColor,mWeekdayBgColor;
    private int mAddEventBtnResId;


    public WeekCellAdapter(ArrayList<WeekViewCell> mCellList, int todayColor, int weekdayBgColor, int addEventBtnResId) {
        this.mCellList = mCellList;
        this.mTodayColor = todayColor;
        this.mWeekdayBgColor = weekdayBgColor;
        this.mAddEventBtnResId = addEventBtnResId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View itemView = inflater.inflate(R.layout.time_cell, parent, false);
        itemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Constants.WEEK_CELL_HEIGHT));

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(itemView);
        viewHolder.addBtn.setBackgroundResource(mAddEventBtnResId);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WeekViewCell cell = mCellList.get(position);
        holder.addBtn.setVisibility(cell.isSelected() ? View.VISIBLE : View.INVISIBLE);

        if (cell.isToday()){
            holder.container.setBackgroundColor(mTodayColor);
        }else {
            holder.container.setBackgroundColor(mWeekdayBgColor);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mCellList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView addBtn;
        public RelativeLayout container;

        public ViewHolder(View itemView) {
            super(itemView);

            this.addBtn = (ImageView) itemView.findViewById(R.id.add_btn);
            this.container = (RelativeLayout) itemView.findViewById(R.id.day_cell_container);
        }
    }
}
