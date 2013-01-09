package com.tomapp.wordisbond;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

public class ListViewFiltered extends ListView
{
    View.OnTouchListener mGestureListener;

	public ListViewFiltered(Context context) 
	{
		super(context);
	}
	
    public ListViewFiltered(Context context, AttributeSet attrs) 
    {
        super(context, attrs);
    }

    private float xDistance, yDistance, lastX, lastY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) 
    {
        switch (ev.getAction()) 
        {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                lastX = ev.getX();
                lastY = ev.getY();
                
    			int position = pointToPosition((int)lastX, (int)lastY);
    			
    			if (position >= 0)
    			{
    				ItemListActivity.main.setSelected(position);
    			}
                
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();
                xDistance += Math.abs(curX - lastX);
                yDistance += Math.abs(curY - lastY);
                lastX = curX;
                lastY = curY;
                if(xDistance > yDistance)
                    return false;
        }

        return super.onInterceptTouchEvent(ev);
    }
    
/*    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) 
    {
        return super.onInterceptTouchEvent(ev) && mGestureDetector.onTouchEvent(ev);
    }
*/
    // Return false if we're scrolling in the x direction  
    class YScrollDetector extends SimpleOnGestureListener 
    {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) 
        {
            if(Math.abs(distanceY) > Math.abs(distanceX)) 
            {
                return true;
            }
            return false;
        }
    }

}
