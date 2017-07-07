package com.outlook.calendar.decorator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemDecoration;
import android.support.v7.widget.RecyclerView.State;
import android.view.View;

import com.outlook.calender.R;

/**
 * Created by ksachan on 7/5/17.
 */

public class OutlookDividerDectorator extends ItemDecoration
{
	private final Paint mPaint;
	private final int   mSize;
	
	public OutlookDividerDectorator(Context context)
	{
		mSize = context.getResources().getDimensionPixelSize(R.dimen.divider_size);
		mPaint = new Paint();
		mPaint.setColor(ContextCompat.getColor(context, R.color.colorDivider));
		mPaint.setStrokeWidth(mSize);
	}
	
	@Override
	public void onDrawOver(Canvas c, RecyclerView parent, State state)
	{
		int top, left = 0, right = parent.getMeasuredWidth();
		for (int i = 0; i < parent.getChildCount(); i++)
		{
			top = parent.getChildAt(i).getTop() - mSize / 2;
			c.drawLine(left, top, right, top, mPaint);
		}
	}
	
	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state)
	{
		if (parent.getChildAdapterPosition(view) > 0)
		{
			outRect.top = mSize;
		}
	}
}