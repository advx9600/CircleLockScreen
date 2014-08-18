package com.android.internal.policy.impl;

import android.view.MotionEvent;
import android.view.View;

public abstract interface CircleUnlockWidget
{
  public abstract void cleanUp();
  
  public abstract boolean handleTouchEvent(View paramView, MotionEvent paramMotionEvent);
  
  public abstract void onPause();
  
  public abstract void onResume();
  
  public abstract void setOnCircleTouchListener(OnCircleTouchListener paramOnCircleTouchListener);
  
  public abstract void setOnCircleUnlockListener(OnCircleUnlockListener paramOnCircleUnlockListener);
  
  public static abstract interface OnCircleTouchListener
  {
    public abstract void onCircleTouchDown(View paramView);
    
    public abstract void onCircleTouchMove(View paramView);
    
    public abstract void onCircleTouchUp(View paramView);
  }
  
  public static abstract interface OnCircleUnlockListener
  {
    public abstract void onCircleUnlocked(View paramView);
  }
}


/* Location:           C:\Users\Administrator\Desktop\classes-dex2jar.jar
 * Qualified Name:     com.android.internal.policy.impl.CircleUnlockWidget
 * JD-Core Version:    0.7.0.1
 */