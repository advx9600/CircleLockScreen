package com.android.internal.policy.impl;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.main);
		
		CircleLockScreen lockScreen = new CircleLockScreen(this,new CircleMethod());
		setContentView(lockScreen);
	}
	class CircleMethod implements KeyguardScreenCallback2{

		@Override
		public void pokeWakelock() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void pokeWakelock(int paramInt) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void goToUnlockScreen() {
			// TODO Auto-generated method stub
			
		}
		
	}
}
