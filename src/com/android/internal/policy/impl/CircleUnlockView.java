package com.android.internal.policy.impl;


import com.android.internal.R;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
//import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class CircleUnlockView extends FrameLayout implements CircleUnlockWidget {
	private static final int AWAKE_INTERVAL_DEFAULT_MS = 10000;
	private static final int MAX_AWAKE_TIME = 30000;
	public static final int MISSED_EVENT = 0;
	public static final int NORMAL_EVENT = -1;
	private final int ARR0W_FADE_IN_OFFSET = 0;
	private final int ARR0W_FADE_OUT_OFFSET = 800;
	private final int ARROW_FADE_IN_DURATION = 800;
	private final int ARROW_FADE_OUT_DURATION = 800;
	private final int CIRCLE_CENTER_DOWN_DURATION = 267;
	private final int CIRCLE_CENTER_UP_DURATION = 267;
	private final int CIRCLE_LOOP_DURATION = 1300;
	private final int CIRCLE_MAX_ALPHA = 255;
	private final int CIRCLE_OUT_DURATION = 300;
	private final int FIRST_MOVE_DURATION = 600;
	private final int INIT_ANGLE_CIRCLE_1 = 82;
	private final int INIT_ANGLE_CIRCLE_2 = 313;
	private final int INIT_ANGLE_CIRCLE_3 = 65;
	private final int LOOP_HANDLER_WHAT = 0;
	protected final double MISSED_DRAG_THRESHOLD = 0.550000011920929D;
	protected final double MISSED_RELEASE_THRESHOLD = 0.550000011920929D;
	private final int SHORT_TAP_FIRST_DURATION = 200;
	private final int SHORT_TAP_FIRST_STARTOFF = 0;
	private final int SHORT_TAP_SECOND_DURATION = 300;
	private final int SHORT_TAP_SECOND_STARTOFF = 600;
	protected String TAG = "CircleLockScreen";
	protected final double UNLOCK_DRAG_THRESHOLD = 1.299999952316284D;
	protected final double UNLOCK_RELEASE_THRESHOLD = 0.800000011920929D;
	private ImageView arrow;
	private LinearLayout arrowContainer;
	private AlphaAnimation arrowFadeIn;
	private AlphaAnimation arrowFadeOut;
	private AnimationSet arrowFadeSet;
	private ObjectAnimator circleAlphaIn1;
	private ObjectAnimator circleAlphaIn2;
	private ObjectAnimator circleAlphaIn3;
	private ObjectAnimator circleAlphaOut1;
	private ObjectAnimator circleAlphaOut2;
	private ObjectAnimator circleAlphaOut3;
	private AnimatorSet circleSet1;
	private AnimatorSet circleSet2;
	private AnimatorSet circleSet3;
	private AnimatorSet circleUpSet;
	private ImageView circle_1;
	private ImageView circle_2;
	private ImageView circle_3;
	private AnimatorSet firstMoveSet;
	private boolean isIgnoreTouch = false;
	protected FrameLayout.LayoutParams lpFrame;
	private final KeyguardScreenCallback2 mCallback;
	private ImageView mCircleCenter;
	private AlphaAnimation mCircleCenterAlphaAnimDown;
	private AlphaAnimation mCircleCenterAlphaAnimUp;
	private ImageView mCircleInPress;
	protected FrameLayout mCircleMain;
	protected RelativeLayout mCircleRoot;
	private final Context mContext;
	private double mDistanceRatio;
	private boolean mDownFirst = false;
	private Handler mHandlerForRepeat;
	private boolean mIsFirst = true;
	private CircleUnlockWidget.OnCircleTouchListener mOnCircleTouchListener;
	private CircleUnlockWidget.OnCircleUnlockListener mOnCircleUnlockListener;
	private long mResumedTimeMillis = 0L;
	private int mStartLocation = -1;
	protected float mX;
	protected float mY;
	private float originalCircleX;
	private float originalCircleY;
	private double radian;


	public CircleUnlockView(Context paramContext,
			KeyguardScreenCallback2 paramKeyguardScreenCallback) {
		super(paramContext);
		this.mContext = paramContext;
		this.mCallback = paramKeyguardScreenCallback;
		setLayout();
		setHandler();
		setAnimation();
		this.mResumedTimeMillis = System.currentTimeMillis();
	}

	private void addToCircleMain() {
		this.mCircleMain.addView(this.mCircleInPress, this.lpFrame);
		this.mCircleMain.addView(this.circle_1, this.lpFrame);
		this.mCircleMain.addView(this.circle_2, this.lpFrame);
		this.mCircleMain.addView(this.circle_3, this.lpFrame);
		this.mCircleMain.addView(this.mCircleCenter, this.lpFrame);
		this.mCircleMain.addView(this.arrowContainer, this.lpFrame);
	}

	private void createArrow() {
		this.arrow = new ImageView(this.mContext);
		this.arrow.setImageResource(R.drawable.keyguard_unlock_circle_arrow_1);
		this.arrow.setAlpha(0);
		this.arrowContainer = new LinearLayout(this.mContext);
		this.arrowContainer.setBackgroundColor(Color.TRANSPARENT);
		this.arrowContainer.addView(this.arrow);
	}

	private void createArrowAnimation() {
		this.arrowFadeIn = new AlphaAnimation(0.0F, 1.0F);
		this.arrowFadeIn.setDuration(800L);
		this.arrowFadeIn.setStartOffset(0L);
		this.arrowFadeOut = new AlphaAnimation(1.0F, 0.0F);
		this.arrowFadeOut.setDuration(800L);
		this.arrowFadeOut.setStartOffset(800L);
		this.arrowFadeSet = new AnimationSet(false);
		this.arrowFadeSet.addAnimation(this.arrowFadeIn);
		this.arrowFadeSet.addAnimation(this.arrowFadeOut);
		this.arrowFadeSet
				.setAnimationListener(new Animation.AnimationListener() {
					public void onAnimationEnd(Animation paramAnonymousAnimation) {
						CircleUnlockView.this.arrowContainer.clearAnimation();
						CircleUnlockView.this.arrowContainer
								.startAnimation(CircleUnlockView.this.arrowFadeSet);
					}

					public void onAnimationRepeat(
							Animation paramAnonymousAnimation) {
					}

					public void onAnimationStart(
							Animation paramAnonymousAnimation) {
					}
				});
	}

	
	private void createCircleAnimation() {
		this.circleAlphaIn1 = ObjectAnimator.ofFloat(this.circle_1, "alpha",
				new float[] { 0.0F, 1.0F });
		this.circleAlphaIn1.setDuration(200L);
		this.circleAlphaIn1.setStartDelay(0L);
		this.circleAlphaOut1 = ObjectAnimator.ofFloat(this.circle_1, "alpha",
				new float[] { 1.0F, 0.0F });
		this.circleAlphaOut1.setDuration(300L);
		this.circleAlphaOut1.setStartDelay(600L);
		this.circleSet1 = new AnimatorSet();
		AnimatorSet localAnimatorSet1 = this.circleSet1;
		Animator[] arrayOfAnimator1 = new Animator[2];
		arrayOfAnimator1[0] = this.circleAlphaIn1;
		arrayOfAnimator1[1] = this.circleAlphaOut1;
		localAnimatorSet1.playTogether(arrayOfAnimator1);
		this.circleAlphaIn2 = ObjectAnimator.ofFloat(this.circle_2, "alpha",
				new float[] { 0.0F, 1.0F });
		this.circleAlphaIn2.setDuration(200L);
		this.circleAlphaIn2.setStartDelay(0L);
		this.circleAlphaOut2 = ObjectAnimator.ofFloat(this.circle_2, "alpha",
				new float[] { 1.0F, 0.0F });
		this.circleAlphaOut2.setDuration(300L);
		this.circleAlphaOut2.setStartDelay(600L);
		this.circleSet2 = new AnimatorSet();
		AnimatorSet localAnimatorSet2 = this.circleSet2;
		Animator[] arrayOfAnimator2 = new Animator[2];
		arrayOfAnimator2[0] = this.circleAlphaIn2;
		arrayOfAnimator2[1] = this.circleAlphaOut2;
		localAnimatorSet2.playTogether(arrayOfAnimator2);
		this.circleSet2.setStartDelay(200L);
		this.circleAlphaIn3 = ObjectAnimator.ofFloat(this.circle_3, "alpha",
				new float[] { 0.0F, 1.0F });
		this.circleAlphaIn3.setDuration(200L);
		this.circleAlphaIn3.setStartDelay(0L);
		this.circleAlphaOut3 = ObjectAnimator.ofFloat(this.circle_3, "alpha",
				new float[] { 1.0F, 0.0F });
		this.circleAlphaOut3.setDuration(300L);
		this.circleAlphaOut3.setStartDelay(600L);
		this.circleSet3 = new AnimatorSet();
		AnimatorSet localAnimatorSet3 = this.circleSet3;
		Animator[] arrayOfAnimator3 = new Animator[2];
		arrayOfAnimator3[0] = this.circleAlphaIn3;
		arrayOfAnimator3[1] = this.circleAlphaOut3;
		localAnimatorSet3.playTogether(arrayOfAnimator3);
		this.circleSet3.setStartDelay(400L);
		this.circleUpSet = new AnimatorSet();
		AnimatorSet localAnimatorSet4 = this.circleUpSet;
		Animator[] arrayOfAnimator4 = new Animator[3];
		arrayOfAnimator4[0] = ObjectAnimator.ofFloat(this.circle_1, "alpha",
				new float[] { 0.0F }).setDuration(400L);
		arrayOfAnimator4[1] = ObjectAnimator.ofFloat(this.circle_2, "alpha",
				new float[] { 0.0F }).setDuration(300L);
		arrayOfAnimator4[2] = ObjectAnimator.ofFloat(this.circle_3, "alpha",
				new float[] { 0.0F }).setDuration(200L);
		localAnimatorSet4.playTogether(arrayOfAnimator4);
		this.circleUpSet.setInterpolator(new QuintEaseOut());
	}

	private void createCircleCenter() {
		this.mCircleCenter = new ImageView(this.mContext);
		this.mCircleCenter
				.setImageResource(R.drawable.keyguard_unlockscreen_lock_01);
	}

	private void createCircleCenterAnimation() {
		this.mCircleCenterAlphaAnimDown = new AlphaAnimation(0.0F, 1.0F);
		this.mCircleCenterAlphaAnimDown.setDuration(267L);
		this.mCircleCenterAlphaAnimDown.setFillAfter(true);
		this.mCircleCenterAlphaAnimUp = new AlphaAnimation(1.0F, 0.0F);
		this.mCircleCenterAlphaAnimUp.setDuration(267L);
		this.mCircleCenterAlphaAnimUp.setFillAfter(true);
	}

	private void createInnerCircle() {
		this.mCircleInPress = new ImageView(this.mContext);
		this.mCircleInPress
				.setImageResource(R.drawable.keyguard_unlock_circle_circleview_in);
	}

	
	private void createThreeCircles() {
		this.circle_1 = new ImageView(this.mContext);
		this.circle_1.setImageResource(R.drawable.keyguard_lockscreen_circle_1);
		this.circle_2 = new ImageView(this.mContext);
		this.circle_2.setImageResource(R.drawable.keyguard_lockscreen_circle_2);
		this.circle_3 = new ImageView(this.mContext);
		this.circle_3.setImageResource(R.drawable.keyguard_lockscreen_circle_3);
		this.circle_1.setAlpha(0.0F);
		this.circle_2.setAlpha(0.0F);
		this.circle_3.setAlpha(0.0F);
	}

	private int getUnlockscreenLockImageIdAt(double paramDouble) {
		int[] arrayOfInt = { R.drawable.keyguard_unlockscreen_lock_01,
				R.drawable.keyguard_unlockscreen_lock_02,
				R.drawable.keyguard_unlockscreen_lock_03,
				R.drawable.keyguard_unlockscreen_lock_04,
				R.drawable.keyguard_unlockscreen_lock_05,
				R.drawable.keyguard_unlockscreen_lock_06,
				R.drawable.keyguard_unlockscreen_lock_07,
				R.drawable.keyguard_unlockscreen_lock_08,
				R.drawable.keyguard_unlockscreen_lock_09,
				R.drawable.keyguard_unlockscreen_lock_10,
				R.drawable.keyguard_unlockscreen_lock_11,
				R.drawable.keyguard_unlockscreen_lock_12,
				R.drawable.keyguard_unlockscreen_lock_13,
				R.drawable.keyguard_unlockscreen_lock_14,
				R.drawable.keyguard_unlockscreen_lock_15,
				R.drawable.keyguard_unlockscreen_lock_16,
				R.drawable.keyguard_unlockscreen_lock_17,
				R.drawable.keyguard_unlockscreen_lock_18,
				R.drawable.keyguard_unlockscreen_lock_19,
				R.drawable.keyguard_unlockscreen_lock_20,
				R.drawable.keyguard_unlockscreen_lock_21,
				R.drawable.keyguard_unlockscreen_lock_22,
				R.drawable.keyguard_unlockscreen_lock_23,
				R.drawable.keyguard_unlockscreen_lock_24,
				R.drawable.keyguard_unlockscreen_lock_25,
				R.drawable.keyguard_unlockscreen_lock_26,
				R.drawable.keyguard_unlockscreen_lock_27,
				R.drawable.keyguard_unlockscreen_lock_28 };
		int i;
		if (paramDouble < 0.1D) {
			i = 0;
			return arrayOfInt[i];
		}
		if (paramDouble >= 1.0D) {
			i = -1 + arrayOfInt.length;
		} else {
			i = -1 + (int) (paramDouble * arrayOfInt.length);
		}
		return arrayOfInt[i];
	}

	private long mFirstTime=0l;
	private void pokeWakelockWithTimeCheck(long time){
		if (Math.abs(time-mFirstTime)>1000){
			mFirstTime = time;
			this.mCallback.pokeWakelock();
		}
	}
	
	private void pokeWakelockWithTimeCheck() {		
		long l = System.currentTimeMillis() - this.mResumedTimeMillis;
		Log.d(this.TAG, "pokeWakelockWithTimeCheck time diffence = " + l);
		mFirstTime= System.currentTimeMillis();
		this.mCallback.pokeWakelock();
//		if (l <= 20000L) {
//			if (mCallback != null)
//				this.mCallback.pokeWakelock(10000);
//			Log.d(this.TAG,
//					"pokeWakelockWithTimeCheck mCallback.pokeWakelock(AWAKE_INTERVAL_DEFAULT_MS)");
//			return;
//		}
//		if ((l > 20000L) && (l < 30000L)) {
//			if (mCallback != null)
//				this.mCallback.pokeWakelock(30000 - (int) l);
//			Log.d(this.TAG, "pokeWakelockWithTimeCheck mCallback.pokeWakelock("
//					+ (30000L - l) + ")");
//			return;
//		}
//		Log.d(this.TAG, "pokeWakelockWithTimeCheck do nothing");
	}

	private void setAnimation() {
		createCircleCenterAnimation();
		createCircleAnimation();
		createArrowAnimation();
	}

	private void setHandler() {
		this.mHandlerForRepeat = new Handler() {
			@Override
			public void handleMessage(Message paramAnonymousMessage) {
				CircleUnlockView.this.startCircleAnimation();
			}
		};
	}

	private void setInnerCircle(View paramView, double paramDouble) {
		int i = (int) (255.0D * paramDouble);
		if (i > 255) {
			i = 255;
		}
		i = 255 - i;
		this.mCircleInPress.setAlpha(i);
		this.arrow.setAlpha(i);
	}

	
	private void startCircleAnimation() {
		this.circle_1.setAlpha(0.0F);
		this.circle_2.setAlpha(0.0F);
		this.circle_3.setAlpha(0.0F);
		this.circle_1.setRotation(0.0F);
		this.circle_2.setRotation(0.0F);
		this.circle_3.setRotation(0.0F);
		this.circleSet1.start();
		this.circleSet2.start();
		this.circleSet3.start();
		this.mHandlerForRepeat.sendEmptyMessageDelayed(0, 1600L);
	}

	public void cleanUp() {
	}

	
	public void clearCircleAnimation() {
		this.circleAlphaIn1.cancel();
		this.circleAlphaOut1.cancel();
		this.circleAlphaIn2.cancel();
		this.circleAlphaOut2.cancel();
		this.circleAlphaIn3.cancel();
		this.circleAlphaOut3.cancel();
		this.circleSet1.cancel();
		this.circleSet2.cancel();
		this.circleSet3.cancel();
		this.circleUpSet.cancel();
		if (this.firstMoveSet != null) {
			this.firstMoveSet.cancel();
		}
		this.mHandlerForRepeat.removeMessages(0);
	}

	
	@Override
	public boolean handleTouchEvent(View paramView, MotionEvent paramMotionEvent) {
//		android.util.Log.i("CircleUnlockView","handleTouchEvent");
		if (this.isIgnoreTouch) {
			if (paramMotionEvent.getAction() == MotionEvent.ACTION_UP) {
				this.isIgnoreTouch = false;
			}
			return false;
		}
		float f1 = paramMotionEvent.getX(0);
		float f2 = paramMotionEvent.getY(0);
		if (this.mCircleMain == null) {
			Log.d(this.TAG,
					"Occured Divided by zero Exception caused by mCircleMain-Null");
			return false;
		}
		if (this.mCircleMain.getHeight() == 0) {
			Log.d(this.TAG,
					"Occured Divided by zero Exception caused by mCircleMain.getHeight() == 0");
			return false;
		}
		if (this.mCircleMain.getWidth() == 0) {
			Log.d(this.TAG,
					"Occured Divided by zero Exception caused by mCircleMain.getWidth() == 0");
			return false;
		}
		switch (paramMotionEvent.getAction()) {
		case MotionEvent.ACTION_POINTER_2_DOWN:
		case MotionEvent.ACTION_POINTER_3_DOWN:
			return false;
		case MotionEvent.ACTION_DOWN:
			this.mDistanceRatio = 0;
			this.mCircleInPress.setAlpha(255);
			this.arrow.setAlpha(255);
			this.mIsFirst = true;
			this.mDownFirst = true;
			if (this.mOnCircleTouchListener != null) {
				this.mOnCircleTouchListener.onCircleTouchDown(paramView);
			}
			pokeWakelockWithTimeCheck();
			this.mCircleRoot.clearAnimation();
			this.mCircleRoot.setVisibility(View.VISIBLE);
			this.mCircleCenter.startAnimation(this.mCircleCenterAlphaAnimDown);
			this.mX = f1;
			this.mY = f2;
			int i1;
			i1 = (int) (this.originalCircleX - this.mCircleMain
					.getMeasuredWidth() / 2);
			if (paramView != null) {
				i1 = (int) (this.originalCircleX - f1
						- this.mCircleMain.getMeasuredWidth() / 2 + paramView
						.getMeasuredWidth() / 2);
				// Log.d(TAG, "this.originalCircleX:" + this.originalCircleX
				// + ",f1:" + f1);
			}
			int i2 = paramView == null ? (int) (this.originalCircleY - this.mCircleMain
					.getMeasuredHeight() / 2) : (int) (this.originalCircleY
					- f2 - this.mCircleMain.getMeasuredHeight() / 2 + paramView
					.getMeasuredHeight() / 2);
			setCenterImage(paramView);
			RelativeLayout.LayoutParams localLayoutParams = (RelativeLayout.LayoutParams) this.mCircleMain
					.getLayoutParams();
			int i3 = this.mCircleRoot.getMeasuredWidth()
					- this.mCircleMain.getMeasuredWidth() - i1;
			int i4 = this.mCircleRoot.getMeasuredHeight()
					- this.mCircleMain.getMeasuredHeight() - i2;
			localLayoutParams.leftMargin = i1;
			localLayoutParams.rightMargin = i3;
			localLayoutParams.topMargin = i2;
			localLayoutParams.bottomMargin = i4;
			this.mCircleMain.setLayoutParams(localLayoutParams);
			this.arrowContainer.startAnimation(this.arrowFadeSet);
			startCircleAnimation();

			break;
		case MotionEvent.ACTION_MOVE:
			int i;
			int j;
			int k;
			double d4;
			pokeWakelockWithTimeCheck(System.currentTimeMillis());
			d4 = 1.299999952316284D;
			if (paramView == null) {
				i = (int) (f1 - this.mX);
				j = (int) (f2 - this.mY) * this.mCircleMain.getWidth()
						/ this.mCircleMain.getHeight();
			} else {
				i = (int) (f1 - paramView.getMeasuredWidth() / 2);
				j = (int) (f2 - paramView.getMeasuredHeight() / 2);
			}
			double d3 = Math.sqrt(Math.pow(i, 2.0D) + Math.pow(j, 2.0D));
			k = this.mCircleMain.getWidth();
			this.mDistanceRatio = (d3 / (k / 2));
			// android.util.Log.i("aaaa","aaa this.mDistanceRatio:"+this.mDistanceRatio);
			setCenterImage(paramView, this.mDistanceRatio);
			setInnerCircle(paramView, this.mDistanceRatio);
			if (this.mDistanceRatio > 0.2D) {
				this.mDownFirst = true;
			}
			if ((this.mDistanceRatio > 0.2D) && (this.mDistanceRatio < d4)) {
				this.mIsFirst = false;
				double d5 = i;
				this.radian = Math.atan2(j * -1, d5);
				float f3 = 90.0F + (float) (180.0D * (-this.radian / 3.141592653589793D));
				float f4 = f3 - 82.0F;
				float f5 = f3 - 313.0F;
				float f6 = f3 - 65.0F;
				if (this.mDownFirst) {
					this.mDownFirst = false;
					clearCircleAnimation();
					if (f4 < 0.0F) {
						f4 += 360.0F;
					}
					if (f5 < 0.0F) {
						f5 += 360.0F;
					}
					if (f6 < 0.0F) {
						f6 += 360.0F;
					}
					this.firstMoveSet = new AnimatorSet();
					AnimatorSet localAnimatorSet1 = this.firstMoveSet;
					Animator[] arrayOfAnimator = new Animator[6];
					arrayOfAnimator[0] = ObjectAnimator.ofFloat(this.circle_1,
							"alpha", new float[] { 0.3F });
					arrayOfAnimator[1] = ObjectAnimator.ofFloat(this.circle_1,
							"rotation", new float[] { f4 });
					arrayOfAnimator[2] = ObjectAnimator.ofFloat(this.circle_2,
							"alpha", new float[] { 0.5F });
					arrayOfAnimator[3] = ObjectAnimator.ofFloat(this.circle_2,
							"rotation", new float[] { f5 });
					arrayOfAnimator[4] = ObjectAnimator.ofFloat(this.circle_3,
							"alpha", new float[] { 0.7F });
					arrayOfAnimator[5] = ObjectAnimator.ofFloat(this.circle_3,
							"rotation", new float[] { f6 });
					localAnimatorSet1.playTogether(arrayOfAnimator);
					this.firstMoveSet.setDuration(300L);
					AnimatorSet localAnimatorSet2 = this.firstMoveSet;
					CubicEaseInOut localCubicEaseInOut = new CubicEaseInOut();
					localAnimatorSet2.setInterpolator(localCubicEaseInOut);
					this.firstMoveSet.start();
				}
			}
			if (this.mDistanceRatio >= d4) {
				this.mDownFirst = true;
			}
			break;
		case MotionEvent.ACTION_POINTER_1_UP:
			this.isIgnoreTouch = true;
		case MotionEvent.ACTION_UP:
			if (this.mOnCircleUnlockListener != null
					&& this.mDistanceRatio > 1.0) {
				this.mOnCircleUnlockListener.onCircleUnlocked(paramView);

			}
			this.mCircleInPress.setAlpha(0);
			this.arrow.setAlpha(0);
			this.arrowContainer.clearAnimation();
			this.mCircleCenter.startAnimation(this.mCircleCenterAlphaAnimUp);
			clearCircleAnimation();
			this.circleUpSet.start();
			double d1;
			if (this.mStartLocation == -1) {
				d1 = 1.299999952316284D;
			}
			// for (double d2 = 0.800000011920929D;; d2 = 0.550000011920929D)
			// {
			this.mStartLocation = -1;
			if (this.mOnCircleTouchListener != null) {
				this.mOnCircleTouchListener.onCircleTouchUp(paramView);
			}
			// if ((d2 > this.mDistanceRatio) || (this.mDistanceRatio >= d1)) {
			// break label1337;
			// }
			// if (!this.mIsFirst) {
			// break;
			// }
			this.mIsFirst = false;
			// if (paramView != null) {
			// break label1317;
			// }
			// this.mCallback.goToUnlockScreen();
			// break;
			// d1 = 0.550000011920929D;
			// }
			// if (this.mOnCircleUnlockListener != null)
			// {
			// this.mOnCircleUnlockListener.onCircleUnlocked(paramView);
			// continue;
			// if (this.mDistanceRatio >= d2) {}
			// }
			// }
			// }
			break;
		}
		// label524:
		// label1317:
		// label1337:
		// for (;;)
		// {
		// return true;

		// break;
		// / 2);
		// }
		// if (this.mOnCircleTouchListener != null) {
		// this.mOnCircleTouchListener.onCircleTouchMove(paramView);
		// }
		// pokeWakelockWithTimeCheck();
		// int i;
		// int j;
		// int k;
		// label574:
		// double d4;
		// if (paramView != null)
		// {
		// int m = paramView.getMeasuredWidth() / 2;
		// int n = paramView.getMeasuredHeight() / 2;
		// i = (int)(f1 - m);
		// j = (int)(f2 - n) * this.mCircleMain.getWidth() /
		// this.mCircleMain.getHeight();
		// double d3 = Math.sqrt(Math.pow(i, 2.0D) + Math.pow(j, 2.0D));
		// if (this.mCircleMain.getWidth() >= this.mCircleMain.getHeight()) {
		// break label709;
		// }
		// k = this.mCircleMain.getWidth();
		// this.mDistanceRatio = (d3 / (k / 2));
		// setCenterImage(paramView, this.mDistanceRatio);
		// setInnerCircle(paramView, this.mDistanceRatio);
		// if (this.mStartLocation != -1) {
		// break label721;
		// }
		// d4 = 1.299999952316284D;
		// label617:
		// if (this.mDistanceRatio >= 0.2D) {
		// break label729;
		// }
		// this.mDownFirst = true;
		// }
		// while ((this.mDistanceRatio >= d4) && (this.mIsFirst))
		// {
		// this.mIsFirst = false;
		// if (paramView != null) {
		// break label1146;
		// }
		// this.mCallback.goToUnlockScreen();
		// break;
		// i = (int)(f1 - this.mX);
		// j = (int)(f2 - this.mY) * this.mCircleMain.getWidth() /
		// this.mCircleMain.getHeight();
		// break label524;
		// label709:
		// k = this.mCircleMain.getHeight();
		// break label574;
		// label721:
		// d4 = 0.550000011920929D;
		// break label617;
		// label729:
		// if ((this.mDistanceRatio > 0.2D) && (this.mDistanceRatio < d4))
		// {
		// double d5 = i;
		// this.radian = Math.atan2(j * -1, d5);
		// float f3 = 90.0F + (float)(180.0D * (-this.radian /
		// 3.141592653589793D));
		// float f4 = f3 - 82.0F;
		// float f5 = f3 - 313.0F;
		// float f6 = f3 - 65.0F;
		// if (this.mDownFirst)
		// {
		// this.mDownFirst = false;
		// clearCircleAnimation();
		// if (f4 < 0.0F) {
		// f4 += 360.0F;
		// }
		// if (f5 < 0.0F) {
		// f5 += 360.0F;
		// }
		// if (f6 < 0.0F) {
		// f6 += 360.0F;
		// }
		// this.firstMoveSet = new AnimatorSet();
		// AnimatorSet localAnimatorSet1 = this.firstMoveSet;
		// Animator[] arrayOfAnimator = new Animator[6];
		// arrayOfAnimator[0] = ObjectAnimator.ofFloat(this.circle_1, "alpha",
		// new float[] { 0.3F });
		// arrayOfAnimator[1] = ObjectAnimator.ofFloat(this.circle_1,
		// "rotation", new float[] { f4 });
		// arrayOfAnimator[2] = ObjectAnimator.ofFloat(this.circle_2, "alpha",
		// new float[] { 0.5F });
		// arrayOfAnimator[3] = ObjectAnimator.ofFloat(this.circle_2,
		// "rotation", new float[] { f5 });
		// arrayOfAnimator[4] = ObjectAnimator.ofFloat(this.circle_3, "alpha",
		// new float[] { 0.7F });
		// arrayOfAnimator[5] = ObjectAnimator.ofFloat(this.circle_3,
		// "rotation", new float[] { f6 });
		// localAnimatorSet1.playTogether(arrayOfAnimator);
		// this.firstMoveSet.setDuration(600L);
		// AnimatorSet localAnimatorSet2 = this.firstMoveSet;
		// CubicEaseInOut localCubicEaseInOut = new CubicEaseInOut();
		// localAnimatorSet2.setInterpolator(localCubicEaseInOut);
		// this.firstMoveSet.start();
		// }
		// if ((this.firstMoveSet != null) && (!this.firstMoveSet.isRunning()))
		// {
		// this.circle_1.setRotation(f4);
		// this.circle_2.setRotation(f5);
		// this.circle_3.setRotation(f6);
		// }
		// }
		// else if (this.mDistanceRatio >= d4)
		// {
		// this.mDownFirst = true;
		// }
		// }
		// label1146:
		
		return true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent) {
		this.originalCircleX = paramMotionEvent.getX();
		this.originalCircleY = paramMotionEvent.getY();
		return false;
	}

	public void onPause() {
	}

	public void onResume() {
		this.mResumedTimeMillis = System.currentTimeMillis();
	}

	public void setCenterImage(View paramView) {
		setCenterImage(paramView, 0.0D);
	}

	protected void setCenterImage(View paramView, double paramDouble) {
		if (paramView == null) {
			this.mCircleCenter
					.setImageResource(getUnlockscreenLockImageIdAt(paramDouble));
			return;
		}
		paramView.setDrawingCacheEnabled(true);
		paramView.buildDrawingCache(true);
		Bitmap localBitmap = paramView.getDrawingCache(true);
		this.mCircleCenter.setImageBitmap(localBitmap);
	}

	public void setLayout() {
		this.mCircleRoot = new RelativeLayout(this.mContext);
		this.mCircleRoot.setVisibility(View.INVISIBLE);
		this.mCircleMain = new FrameLayout(this.mContext);
		this.mCircleRoot.addView(this.mCircleMain, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		addView(this.mCircleRoot, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		FrameLayout.LayoutParams localLayoutParams = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		localLayoutParams.gravity = Gravity.CENTER;
		this.lpFrame = localLayoutParams;
		createInnerCircle();
		createThreeCircles();
		createCircleCenter();
		createArrow();
		addToCircleMain();
	}

	public void setOnCircleTouchListener(
			CircleUnlockWidget.OnCircleTouchListener paramOnCircleTouchListener) {
		this.mOnCircleTouchListener = paramOnCircleTouchListener;
	}

	public void setOnCircleUnlockListener(
			CircleUnlockWidget.OnCircleUnlockListener paramOnCircleUnlockListener) {
		this.mOnCircleUnlockListener = paramOnCircleUnlockListener;
	}

	public void setStartLocation(int paramInt) {
		this.mStartLocation = paramInt;
	}

	public class CubicEaseInOut implements Interpolator {
		public CubicEaseInOut() {
		}

		public CubicEaseInOut(Context paramContext,
				AttributeSet paramAttributeSet) {
		}

		public float getInterpolation(float paramFloat) {
			float f1 = paramFloat / 0.5F;
			if (f1 < 1.0F) {
				return 0.0F + f1 * (f1 * (0.5F * f1));
			}
			float f2 = f1 - 2.0F;
			return 0.0F + 0.5F * (2.0F + f2 * (f2 * f2));
		}
	}

	public class QuintEaseInOut implements Interpolator {
		public QuintEaseInOut() {
		}

		public QuintEaseInOut(Context paramContext,
				AttributeSet paramAttributeSet) {
		}

		public float getInterpolation(float paramFloat) {
			float f1 = paramFloat / 0.5F;
			if (f1 < 1.0F) {
				return 0.0F + f1 * (f1 * (f1 * (f1 * (0.5F * f1))));
			}
			float f2 = f1 - 2.0F;
			return 0.0F + 0.5F * (2.0F + f2 * (f2 * (f2 * (f2 * f2))));
		}
	}

	public class QuintEaseOut implements Interpolator {
		public QuintEaseOut() {
		}

		public QuintEaseOut(Context paramContext, AttributeSet paramAttributeSet) {
		}

		public float getInterpolation(float paramFloat) {
			float f = paramFloat / 1.0F - 1.0F;
			return 0.0F + 1.0F * (1.0F + f * (f * (f * (f * f))));
		}
	}

	public class SineEaseInOut implements Interpolator {
		public SineEaseInOut() {
		}

		public SineEaseInOut(Context paramContext,
				AttributeSet paramAttributeSet) {
		}

		public float getInterpolation(float paramFloat) {
			return 0.0F + -0.5F
					* (FloatMath.cos(3.141593F * paramFloat / 1.0F) - 1.0F);
		}
	}
}

/*
 * Location: C:\Users\Administrator\Desktop\classes-dex2jar.jar
 * 
 * Qualified Name: com.android.internal.policy.impl.CircleUnlockView
 * 
 * JD-Core Version: 0.7.0.1
 */