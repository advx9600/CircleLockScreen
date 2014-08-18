package com.android.internal.policy.impl;

//import android.app.enterprise.EnterpriseDeviceManager;
//import android.app.enterprise.MiscPolicy;
//import android.app.enterprise.kioskmode.KioskMode;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
//import android.os.ServiceManager;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.util.Log;
//import android.view.IWindowManager;
//import android.view.IWindowManager.Stub;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

//import com.android.internal.telephony.IccCard.State;
//import com.android.internal.widget.LockPatternUtils;
//import com.samsung.voiceshell.VoiceEngineResultListener;
//import com.samsung.voiceshell.WakeUpCmdRecognizer;



import java.util.List;

import com.android.internal.R;

public class CircleLockScreen extends FrameLayout implements KeyguardScreen {
	private static final String HELP_OVERLAY_CHECKED = "help_overlay_checked";
	private final boolean DBG = false;
	private final int FADE_IN_OUT_ANIMATION_DURATION = 300;
	private final int MSG_GO_TO_UNLOCK_SCREEN = 1;
	private final int MSG_SET_VOICE_RECOGNITION_FAIL = 0;
	private String TAG = "CircleLockScreen";
	private int helpOverlayTip = 0;
	private boolean isHelpOverlayUSA;
	private AudioManager mAudioManager;
	private KeyguardScreenCallback2 mCallback;
	// private CircleLockScreenMotion mCircleLockScreenMotion;
	private CircleUnlockWidget.OnCircleTouchListener mCircleTouchListener;
	private CircleUnlockWidget.OnCircleUnlockListener mCircleUnlockListener;
//	private ClockWidget mClockWidget;
	private Context mContext;
	private int mCreationOrientation;
	private boolean mDefaultHelpOverlay;
	private String mDefaultHelpText;
	private AlphaAnimation mFadeInAnimation = new AlphaAnimation(0.0F, 1.0F);
	private AlphaAnimation mFadeOutAnimation = new AlphaAnimation(1.0F, 0.0F);
	private Handler mHandler;
	private TextView mHelpText;
	private boolean mIsCameraShortCut;
	private boolean mIsHelpTextEnabled = false;
	private boolean mIsMotionLockscreen;
	private boolean mIsMultipleWakeUpOn = false;
	private boolean mIsWaterRipple;
	// private KioskMode mKioskMode = null;
	private ImageView mLockIcon;
	// private LockPatternUtils mLockPatternUtils;
//	private LockscreenHelpOverlayInterface mLockscreenHelpOverlay;
	private View mMissedEventWidget;
	private RelativeLayout mRootLayout;
	// private CircleShortcutWidget mShortcutWidget;
	private boolean mTalkbackEnabled = false;
	// private TickerWidget mTickerWidget;
	private View mUnlockWidget;
//	private KeyguardUpdateMonitor mUpdateMonitor;
	// private UsimWidget mUsimWidget;
	// private MultipleWakeUpVoiceEngineThread mVoiceEngineThread;
	private TextView mVoiceHelpText;
	private ImageView mVoiceIcon;

	// private IWindowManager mWindowManager;

	public CircleLockScreen(Context paramContext,
			KeyguardScreenCallback2 paramKeyguardScreenCallback) {
		super(paramContext);		
		this.mContext = paramContext;
		// this.mUpdateMonitor = paramKeyguardUpdateMonitor;
		this.mCallback = paramKeyguardScreenCallback;
		// this.mLockPatternUtils = paramLockPatternUtils;
		this.mCreationOrientation = paramContext.getResources()
				.getConfiguration().orientation;
		this.mFadeInAnimation.setFillAfter(true);
		this.mFadeInAnimation.setDuration(300L);
		this.mFadeOutAnimation.setFillAfter(true);
		this.mFadeOutAnimation.setDuration(300L);
		// this.mWindowManager =
		// IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
		this.mAudioManager = ((AudioManager) this.mContext
				.getSystemService("audio"));
		// String str1 =
		// Settings.Secure.getString(paramContext.getContentResolver(),
		// "enabled_accessibility_services");
		// if (str1 != null) {
		// this.mTalkbackEnabled = str1.matches("(?i).*talkback.*");
		// }
		// Log.d(this.TAG, "mTalkbackEnabled =" + this.mTalkbackEnabled);
		LayoutInflater localLayoutInflater;
		localLayoutInflater = LayoutInflater.from(paramContext);
//		if (this.mCreationOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//			localLayoutInflater.inflate(R.layout.keyguard_circlelock_main,
//					this, true);
//		} else {
			localLayoutInflater.inflate(R.layout.keyguard_circlelock_main_land,
					this, true);
//		}
		// TextView localTextView = (TextView) findViewById(16909185);
		// if (localTextView != null) {
		// localTextView.setText("");
		// localTextView.setVisibility(View.INVISIBLE);
		// localTextView.setSelected(true);
		// }

		//
		// this.mLockscreenHelpOverlay = new LockscreenHelpOverlay(
		// this.mContext, paramKeyguardScreenCallback,
		// paramConfiguration, local3, n,
		// this.mShortcutWidget.getItemCount());
		// continue;
		setFocusable(true);
		setFocusableInTouchMode(true);
		// setDescendantFocusability(262144);
		this.mRootLayout = ((RelativeLayout) findViewById(R.id.circle_lockscreen_root));
		this.mIsMotionLockscreen = false;
		this.mIsCameraShortCut = false;
		if ((this.mIsMotionLockscreen) || (this.mIsCameraShortCut)) {
			// this.mCircleLockScreenMotion = new CircleLockScreenMotion(
			// paramContext, paramLockPatternUtils,
			// paramKeyguardScreenCallback);
		}
		this.mHelpText = ((TextView) findViewById(R.id.circle_lockscreen_help_text));
		this.mHelpText.setFocusableInTouchMode(true);
		this.mVoiceHelpText = ((TextView) findViewById(R.id.circle_lockscreen_voice_help_text));
		this.mVoiceHelpText.setFocusableInTouchMode(true);
		this.mVoiceHelpText.setVisibility(View.GONE);
		this.mVoiceIcon = ((ImageView) findViewById(R.id.circle_lockscreen_voice_icon));
		this.mLockIcon = ((ImageView) findViewById(R.id.circle_lockscreen_lock_icon));

		this.mIsWaterRipple = false;
		this.mIsHelpTextEnabled = false;
		if (!this.mIsHelpTextEnabled) {
			this.mHelpText.setVisibility(View.GONE);
		} else {
			this.mHelpText.setVisibility(View.VISIBLE);
		}

		this.mUnlockWidget = createUnlockWidget();
		// this.mMissedEventWidget = new CircleMissedEventWidgetDA(getContext(),
		// (CircleUnlockWidget) this.mUnlockWidget,
		// paramKeyguardScreenCallback);
		this.mRootLayout.addView(this.mUnlockWidget, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		this.mCircleTouchListener = new CircleUnlockWidget.OnCircleTouchListener() {
			public void onCircleTouchDown(View paramAnonymousView) {
				if ((paramAnonymousView == null)
						|| (paramAnonymousView.getTag() == null)) {
					if (!CircleLockScreen.this.mHelpText.getText().equals(
							CircleLockScreen.this.mDefaultHelpText)) {
						CircleLockScreen.this.mHelpText
								.setText(CircleLockScreen.this.mDefaultHelpText);
						CircleLockScreen.this.mHelpText
								.startAnimation(CircleLockScreen.this.mFadeInAnimation);
					}
					if (!CircleLockScreen.this.mIsMotionLockscreen) {
						CircleLockScreen.this.bedimBackground(true);
					}
					return;
				}
				if (paramAnonymousView.getTag().equals("ShortcutWidget")) {
					if (CircleLockScreen.this.mMissedEventWidget
							.getVisibility() != View.GONE) {
						CircleLockScreen.this.mMissedEventWidget
								.setVisibility(View.INVISIBLE);
					}
					CircleLockScreen.this.mHelpText
							.setText(R.string.lockscreen_short_cut_guide_text);
					CircleLockScreen.this.mHelpText
							.startAnimation(CircleLockScreen.this.mFadeInAnimation);
					return;
				}
				if (paramAnonymousView.getTag().equals("MissedEvent")) {
					if ((CircleLockScreen.this.mIsWaterRipple)) {
						// ((CircleUnlockRipple)
						// CircleLockScreen.this.mUnlockWidget)
						// .setStartLocation(0);
					} else {
						CircleLockScreen.this.mHelpText
								.setText(R.string.lockscreen_missed_event_guide_text);
						CircleLockScreen.this.mHelpText
								.startAnimation(CircleLockScreen.this.mFadeInAnimation);
						CircleLockScreen.this.bedimBackground(true);
						((CircleUnlockView) CircleLockScreen.this.mUnlockWidget)
								.setStartLocation(0);

					}
				}
			}

			public void onCircleTouchMove(View paramAnonymousView) {
			}

			public void onCircleTouchUp(View paramAnonymousView) {
				if (!CircleLockScreen.this.mHelpText.getText().equals(
						CircleLockScreen.this.mDefaultHelpText)) {
					CircleLockScreen.this.mHelpText
							.setText(CircleLockScreen.this.mDefaultHelpText);
					CircleLockScreen.this.mHelpText
							.startAnimation(CircleLockScreen.this.mFadeInAnimation);
				}
				if (CircleLockScreen.this.mMissedEventWidget.getVisibility() == View.GONE) {
					CircleLockScreen.this.mMissedEventWidget
							.setVisibility(View.VISIBLE);
				}
				CircleLockScreen.this.bedimBackground(false);
			}
		};
		this.mCircleUnlockListener = new CircleUnlockWidget.OnCircleUnlockListener() {
			public void onCircleUnlocked(View paramAnonymousView) {
				if (CircleLockScreen.this.mCallback != null) {
					CircleLockScreen.this.mCallback.goToUnlockScreen();
				}
			}
		};
		// this.mClockWidget = new ClockWidget(paramContext,
		// paramContext.getResources().getConfiguration());
		// ((FrameLayout) findViewById(R.id.circle_lockscreen_clock))
		// .addView(this.mClockWidget);
		this.mMissedEventWidget = new CircleMissedEventWidgetDA(getContext(),
				(CircleUnlockWidget) this.mUnlockWidget,
				paramKeyguardScreenCallback);

		// this.mShortcutWidget = new CircleShortcutWidget(paramContext,
		// paramKeyguardScreenCallback,
		// (CircleUnlockWidget) this.mUnlockWidget,
		// paramConfiguration,
		// paramLockPatternUtils.usingBiometricWeak(),
		// this.mUpdateMonitor);
		// ((FrameLayout) findViewById(16909044))
		// .addView(this.mShortcutWidget);
		((LinearLayout) findViewById(R.id.circle_lockscreen_missed_event))
				.addView(this.mMissedEventWidget, LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT);
		// localLayoutParams = new FrameLayout.LayoutParams(-1, -1);
		// this.mRootLayout.addView(this.mUnlockWidget, 0, localLayoutParams);
		// if (this.mIsMotionLockscreen) {
		// this.mRootLayout.addView(this.mCircleLockScreenMotion, -1, -1);
		// }
		// paramKeyguardUpdateMonitor.registerInfoCallback(this);
		// paramKeyguardUpdateMonitor.registerSimStateCallback(this);
		((CircleUnlockWidget) this.mUnlockWidget)
				.setOnCircleTouchListener(this.mCircleTouchListener);
		// startVoiceEngineThread(paramContext, paramKeyguardScreenCallback);
		refreshDefaultHelpText(false);
		this.mHelpText.setText(this.mDefaultHelpText);
	}

	private void bedimBackground(boolean paramBoolean) {
		if (this.mIsWaterRipple) {
			return;
		}
		if (paramBoolean) {
			this.mUnlockWidget.setBackgroundColor(0x55000000);
			return;
		}
		this.mUnlockWidget.setBackgroundColor(0);
	}

	private View createUnlockWidget() {
		// if (this.mIsWaterRipple) {
		// return new CircleUnlockRipple(this.mContext, this.mCallback);
		// }
		return new CircleUnlockView(this.mContext, this.mCallback);
	}

	private void refreshDefaultHelpText(boolean paramBoolean) {
		this.mDefaultHelpText = this.mContext.getResources().getString(
				R.string.lockscreen_unlock_guide_text);
	}

	public void cleanUp() {
		if (this.DBG) {
			Log.d(this.TAG, "cleanUp()");
		}
		// if ((!this.mIsMotionLockscreen) && (this.mDefaultHelpOverlay)
		// && (this.mLockscreenHelpOverlay != null)) {
		// this.mLockscreenHelpOverlay.cleanUp();
		// this.mLockscreenHelpOverlay = null;
		// }
		if (this.mUnlockWidget != null) {
			((CircleUnlockWidget) this.mUnlockWidget).cleanUp();
		}
		// this.mClockWidget.cleanUp();
		// if (this.mShortcutWidget != null) {
		// this.mShortcutWidget.cleanUp();
		// }
		// if ((SamsungLockScreenProperties.isDisplayUsimWidget())
		// && (this.mUsimWidget != null)) {
		// this.mUsimWidget.cleanUp();
		// }
		((CircleMissedEventWidgetInterface) this.mMissedEventWidget).cleanUp();
		// if (this.mTickerWidget != null) {
		// this.mTickerWidget.cleanUp();
		// }
		// if (this.mCircleLockScreenMotion != null) {
		// this.mCircleLockScreenMotion.cleanUp();
		// }
		// if ((this.mVoiceEngineThread != null)
		// && (this.mVoiceEngineThread.isSupportMultipleWakeUp())) {
		// this.mVoiceEngineThread.wakeUpDestroy();
		// this.mVoiceEngineThread.quit();
		// }
		// this.mUpdateMonitor.reportClockVisible(false);
		// this.mUpdateMonitor.removeCallback(this);
		// this.mCallback = null;
		// this.mHandler = null;
	}

	@Override
	public void onDetachedFromWindow() {
		cleanUp();
		super.onDetachedFromWindow();
	}

	public boolean dispatchKeyEvent(KeyEvent paramKeyEvent) {
		// if ((paramKeyEvent.getAction() == 0)
		// && (((InputMethodManager) getContext().getSystemService(
		// "input_method")).isAccessoryKeyboardState() != 0)
		// && (this.mCallback != null)) {
		// this.mCallback.goToUnlockScreen();
		// }
		return super.dispatchKeyEvent(paramKeyEvent);
	}

	public boolean dispatchPopulateAccessibilityEvent(
			AccessibilityEvent paramAccessibilityEvent) {
		super.dispatchPopulateAccessibilityEvent(paramAccessibilityEvent);
		return true;
	}

	public void goToUnlockScreen() {
		if (this.mHandler != null) {
			this.mHandler.sendEmptyMessage(1);
		}
	}

	public boolean needsInput() {
		return false;
	}

	public void onClockVisibilityChanged() {
	}

	public void onCsSimStateChanged(int paramInt) {
	}

	public void onDeviceProvisioned() {
	}

	public void onPause() {
		if (this.DBG) {
			Log.d(this.TAG, "onPause()");
		}
		// if ((!this.mIsMotionLockscreen) && (this.mDefaultHelpOverlay)
		// && (!SamsungLockScreenProperties.isTabletDevice())
		// && (this.helpOverlayTip > 0)
		// && (this.mLockscreenHelpOverlay != null)) {
		// Log.d(this.TAG, "LockscreenHelpOverlay removeView");
		// this.mLockscreenHelpOverlay.onPause();
		// this.mRootLayout.removeView((View) this.mLockscreenHelpOverlay);
		// }
		if (this.mUnlockWidget != null) {
			((CircleUnlockWidget) this.mUnlockWidget).onPause();
		}
//		if (this.mClockWidget != null) {
//			this.mClockWidget.onPause();
//		}
		// if (this.mShortcutWidget != null) {
		// this.mShortcutWidget.onPause();
		// }
		// if ((SamsungLockScreenProperties.isDisplayUsimWidget())
		// && (this.mUsimWidget != null)) {
		// this.mUsimWidget.onPause();
		// }
		((CircleMissedEventWidgetInterface) this.mMissedEventWidget).onPause();
		// if (this.mTickerWidget != null) {
		// this.mTickerWidget.onPause();
		// }
		// if (this.mCircleLockScreenMotion != null) {
		// this.mCircleLockScreenMotion.onPause();
		// }
		// if ((this.mVoiceEngineThread != null)
		// && (this.mVoiceEngineThread.isSupportMultipleWakeUp())) {
		// this.mVoiceEngineThread.wakeUpTerminateVerify();
		// }
	}

	public void onPhoneStateChanged(int paramInt) {
	}

	public void onResume() {
		if (this.DBG) {
			Log.d(this.TAG, "onResume()");
		}
		if (this.mUnlockWidget != null) {
			((CircleUnlockWidget) this.mUnlockWidget).onResume();
		}
//		this.mClockWidget.onResume();
		// if ((SamsungLockScreenProperties.isDisplayUsimWidget())
		// && (this.mUsimWidget != null)) {
		// this.mUsimWidget.onResume();
		// }
		((CircleMissedEventWidgetInterface) this.mMissedEventWidget).onResume();
		// if (this.mTickerWidget != null) {
		// this.mTickerWidget.onResume();
		// }
		// if (this.mShortcutWidget != null) {
		// this.mShortcutWidget.onResume();
		// }
		// Log.d(this.TAG,
		// "mCallback.isShowingAndNotHidden()="
		// + this.mCallback.isShowingAndNotHidden());
		// if ((this.mVoiceEngineThread != null)
		// && (this.mVoiceEngineThread.isSupportMultipleWakeUp())
		// && (this.mCallback.isShowingAndNotHidden())) {
		// this.mVoiceEngineThread.wakeUpStartVerify();
		// }
		// if (this.mVoiceEngineThread == null) {
		// startVoiceEngineThread(this.mContext, this.mCallback);
		// }
		// if (this.mCircleLockScreenMotion != null) {
		// this.mCircleLockScreenMotion.onResume();
		// }
		// if ((!this.mIsMotionLockscreen) && (this.mDefaultHelpOverlay)
		// && (!SamsungLockScreenProperties.isTabletDevice())
		// && (this.mLockscreenHelpOverlay != null)) {
		// this.mLockscreenHelpOverlay.onResume();
		// this.helpOverlayTip = this.mLockscreenHelpOverlay.getWhichTip();
		// if ((this.helpOverlayTip > 0)
		// && (this.mRootLayout
		// .indexOfChild((View) this.mLockscreenHelpOverlay) == -1)) {
		// Log.d(this.TAG, "LockscreenHelpOverlay addView");
		// this.mRootLayout.addView((View) this.mLockscreenHelpOverlay);
		// this.mHelpText.setVisibility(8);
		// }
		// }
	}

	public void onRingerModeChanged(int paramInt) {
	}

	public void onTimeChanged() {
	}

	public boolean onTouchEvent(MotionEvent paramMotionEvent) {
		switch (paramMotionEvent.getAction()) {
		case MotionEvent.ACTION_DOWN:
			((CircleUnlockWidget) this.mUnlockWidget)
					.setOnCircleUnlockListener(this.mCircleUnlockListener);
			break;
		}
		return ((CircleUnlockWidget) this.mUnlockWidget).handleTouchEvent(null,
				paramMotionEvent);
	}

	// public void onWindowFocusChanged(boolean paramBoolean) {
	// String str1 = this.TAG;
	// String str2;
	// if (paramBoolean) {
	// str2 = "focused";
	// Log.d(str1, str2);
	// if ((this.mVoiceEngineThread != null)
	// && (this.mVoiceEngineThread.isSupportMultipleWakeUp())) {
	// if (!paramBoolean) {
	// break label147;
	// }
	// this.mVoiceEngineThread.wakeUpStartVerify();
	// }
	// }
	// for (;;) {
	// if ((this.mLockscreenHelpOverlay != null) && (paramBoolean)) {
	// this.mLockscreenHelpOverlay.setWhichHelpInfo();
	// this.helpOverlayTip = this.mLockscreenHelpOverlay.getWhichTip();
	// if ((this.helpOverlayTip > 0)
	// && (this.mRootLayout
	// .indexOfChild((View) this.mLockscreenHelpOverlay) == -1)) {
	// Log.d(this.TAG, "LockscreenHelpOverlay addView");
	// this.mRootLayout
	// .addView((View) this.mLockscreenHelpOverlay);
	// this.mHelpText.setVisibility(8);
	// }
	// }
	// return;
	// str2 = "unfocused";
	// break;
	// label147: this.mVoiceEngineThread.wakeUpTerminateVerify();
	// }
	// }

	// public void setVoiceRecognitionFailed() {
	// if (this.mHandler != null) {
	// this.mHandler.sendEmptyMessage(0);
	// }
	// }

}