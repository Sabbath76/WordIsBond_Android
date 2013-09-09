package com.tomapp.wordisbond;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

public class ListViewFiltered extends ListView
{
    View.OnTouchListener mGestureListener;
    Bitmap mSweepImage;

	public ListViewFiltered(Context context) 
	{
		super(context);
		
		mSweepImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.slide);
	}
	
    public ListViewFiltered(Context context, AttributeSet attrs) 
    {
        super(context, attrs);
		
		mSweepImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.slide);
    }

    private float xDistance, yDistance, lastX, lastY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) 
    {
        switch (ev.getAction()) 
        {
            case MotionEvent.ACTION_DOWN:
            {
                xDistance = yDistance = 0f;
                lastX = ev.getX();
                lastY = ev.getY();

                if (ItemListActivity.main.mSelectToExpand)
                {
	    			int position = pointToPosition((int)lastX, (int)lastY);
	    			
	    			if (position >= 0)
	    			{
	    				ItemListActivity.main.setSelected(position);
	    				
	//    				ItemListActivity.main.showHighlight(position);
	    			}
                }
                
                break;
            }
                
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            {
                if (ItemListActivity.main.mSelectToExpand)
                {
	                lastX = ev.getX();
	                lastY = ev.getY();
	                
	    			int position = pointToPosition((int)lastX, (int)lastY);
	    			
	    			if (position >= 0)
	    			{
	    				ItemListActivity.main.setReleased(position);
	    			}
                }
//    			ItemListActivity.main.clearHighlight();
            	break;
            }
            	
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();
                
/*    			int position = pointToPosition((int)curX, (int)curY);
    			if (position >= 0)
    			{
	    			ItemListActivity.main.showHighlight(position);
    			}
    			else
    			{
        			ItemListActivity.main.clearHighlight();
    			}
*/                
                xDistance += Math.abs(curX - lastX);
                yDistance += Math.abs(curY - lastY);
                lastX = curX;
                lastY = curY;
                if(xDistance > yDistance)
                    return false;
//                else
//                	ItemListActivity.main.clearHighlight();

        }

        return super.onInterceptTouchEvent(ev);
        
//        return true;
    }
    
/*    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) 
    {
        return super.onInterceptTouchEvent(ev) && mGestureDetector.onTouchEvent(ev);
    }
*/

    final long MIN_HOLD_TIME = 5000;
    final long SWEEP_FIN_TIME = 10000;
    
/*	@Override
	protected void dispatchDraw(Canvas canvas) 
	{
		super.dispatchDraw(canvas);

		int highlightPosition = ItemListActivity.main.getHighlightPosition();
		if (highlightPosition >= 0)
		{
			long holdTime = ItemListActivity.main.getHighlightTime();
			if (holdTime > MIN_HOLD_TIME)
			{
				float t = 0.0f;
				if (holdTime > SWEEP_FIN_TIME)
					t = 1.0f;
				else
					t = (float)(holdTime - MIN_HOLD_TIME) / (float)(SWEEP_FIN_TIME - MIN_HOLD_TIME);
				View view = getChildAt(highlightPosition-getFirstVisiblePosition());
				if (view == null)
				{
					canvas.drawBitmap(mSweepImage, 0, 20, null);
				}
				else
				{
					int ymin = view.getTop();
					int ymax = view.getBottom();
					int xpos = (int)((float)canvas.getWidth() * (((1.0f - t) * 0.5) + 0.5));
					canvas.drawBitmap(mSweepImage, xpos, ymin, null);
				}
			}
		}		
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
