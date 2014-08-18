package com.android.internal.policy.impl;

public abstract interface KeyguardViewCallback2
{
//  public abstract boolean isShowingAndNotHidden();
//  
//  public abstract void keyguardDone(boolean paramBoolean);
//  
//  public abstract void keyguardDoneDrawing();
  
  public abstract void pokeWakelock();
  
  public abstract void pokeWakelock(int paramInt);
}


/* Location:           C:\Users\Administrator\Desktop\classes-dex2jar.jar
 * Qualified Name:     com.android.internal.policy.impl.KeyguardViewCallback
 * JD-Core Version:    0.7.0.1
 */