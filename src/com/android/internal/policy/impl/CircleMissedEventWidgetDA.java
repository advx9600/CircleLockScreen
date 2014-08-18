package com.android.internal.policy.impl;

//import android.app.NotificationInfo;

import com.android.internal.R;

//import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.System;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnHoverListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CircleMissedEventWidgetDA extends LinearLayout implements
		CircleMissedEventWidgetInterface {
	private static final String MISSED_EVENT_ARRIVED = "com.android.server.NotificationManagerService.NotificationArrived";
	private static final String MISSED_EVENT_REMOVED = "com.android.server.NotificationManagerService.NotificationRemoved";
	private static final String TAG = "CircleMissedEventWidget";
	private final String BADGE_URI = "content://com.sec.badge/apps";
	private final String CALL_PKG = "com.android.phone";
	private final boolean DEBUG = false;
	private final String EMAIL_PKG = "com.android.email";
	private final int MISSED_EVENT_UPDATE = 4802;
	private String MSG_PKG = "com.android.mms";
	private final int REQ_INDEX_CALL_EMAIL = 1;
	private final int REQ_INDEX_MSG = 123;
	// private ContentObserver mBadgeObserver;
	private BroadcastReceiver mBroadcastReceiver;
	private KeyguardScreenCallback2 mCallback;
	private CircleUnlockWidget.OnCircleUnlockListener mCircleUnlockListener;
	private CircleUnlockWidget mCircleUnlockView;
	private Context mContext;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message paramAnonymousMessage) {
			switch (paramAnonymousMessage.what) {
			// default:
			// return;
			}
			CircleMissedEventWidgetDA.this.handleMissedEventUpdate();
		}
	};
	boolean mIsLiveWallpaper;
	private LinearLayout mMissedCall;
	private LinearLayout mMissedCallBackground;
	private int mMissedCallCount = 0;
	private ImageView mMissedCallIcon;
	private View mMissedCallList = null;
	// private NotificationInfo mMissedCallNotiInfo;
	private TextView mMissedCallTextView;
	private LinearLayout mMissedMsg;
	private LinearLayout mMissedMsgBackground;
	private int mMissedMsgCount = 0;
	private ImageView mMissedMsgIcon;
	private TextView mMissedMsgTextView;
	private LinearLayout mMissedVvm;
	private LinearLayout mMissedVvmBackground;
	private int mMissedVvmCount = 0;
	private ImageView mMissedVvmIcon;
	private TextView mMissedVvmTextView;
	private ContentResolver mResolver;
	private UnlockMode mUnlockMode = UnlockMode.MissedNone;
	private View mUnreadMsgList = null;
	// private NotificationInfo mUnreadMsgNotiInfo;
	// private KeyguardUpdateMonitor mUpdateMonitor;
	private LinearLayout mUpperLayout;
	private TextView mWaterlockHelpText;

	public CircleMissedEventWidgetDA(Context paramContext,
			CircleUnlockWidget paramCircleUnlockWidget,
			KeyguardScreenCallback2 paramKeyguardScreenCallback) {
		super(paramContext);
		this.mContext = paramContext;
		this.mCallback = paramKeyguardScreenCallback;
		// this.mUpdateMonitor = paramKeyguardUpdateMonitor;
		this.mCircleUnlockView = paramCircleUnlockWidget;
		LayoutInflater localLayoutInflater = LayoutInflater.from(paramContext);
		int i = getResources().getConfiguration().orientation;
		// if (this.mContext.getPackageManager().hasSystemFeature(
		// "com.sec.feature.hovering_ui") == true) {
		// if (i == ActivityInfo.SCREEN_ORIENTATION_USER) {
		// Log.d("CircleMissedEventWidget", "landscape mode");
		// localLayoutInflater
		// .inflate(
		// R.layout.keyguard_circlelock_missed_event_da_land_hover,
		// this, true);
		// } else {
		// Log.d("CircleMissedEventWidget", "portrait mode");
		// localLayoutInflater.inflate(
		// R.layout.keyguard_circlelock_missed_event_da_hover,
		// this, true);
		// }
		// } else {
		// if (i == ActivityInfo.SCREEN_ORIENTATION_USER) {
		Log.d("CircleMissedEventWidget", "landscape mode");
		localLayoutInflater.inflate(
				R.layout.keyguard_circlelock_missed_event_da_land, this, true);
		// } else {
		// Log.d("CircleMissedEventWidget", "portrait mode");
		// localLayoutInflater.inflate(
		// R.layout.keyguard_circlelock_missed_event_da, this,
		// true);
		// }
		// }

		this.mUpperLayout = ((LinearLayout) findViewById(R.id.upper_layout));
		this.mUpperLayout.setVisibility(View.INVISIBLE);
		this.mMissedCallIcon = ((ImageView) findViewById(R.id.missed_event_call_image));
		this.mMissedMsgIcon = ((ImageView) findViewById(R.id.missed_event_msg_image));
		this.mMissedVvmIcon = ((ImageView) findViewById(R.id.missed_event_vvm_image));
		this.mMissedCallBackground = ((LinearLayout) findViewById(R.id.missed_event_call_background_layout));
		this.mMissedMsgBackground = ((LinearLayout) findViewById(R.id.missed_event_msg_background_layout));
		this.mMissedVvmBackground = ((LinearLayout) findViewById(R.id.missed_event_vvm_background_layout));
		this.mMissedCallBackground.setVisibility(View.INVISIBLE);
		this.mMissedMsgBackground.setVisibility(View.INVISIBLE);
		this.mMissedVvmBackground.setVisibility(View.INVISIBLE);
		this.mMissedCall = ((LinearLayout) findViewById(R.id.missed_event_call_layout));
		this.mMissedMsg = ((LinearLayout) findViewById(R.id.missed_event_msg_layout));
		this.mMissedVvm = ((LinearLayout) findViewById(R.id.missed_event_vvm_layout));
		this.mMissedCall.setVisibility(View.INVISIBLE);
		this.mMissedMsg.setVisibility(View.INVISIBLE);
		this.mMissedVvm.setVisibility(View.INVISIBLE);
		this.mMissedCallTextView = ((TextView) findViewById(R.id.missed_event_call_text));
		this.mMissedMsgTextView = ((TextView) findViewById(R.id.missed_event_msg_text));
		this.mMissedVvmTextView = ((TextView) findViewById(R.id.missed_event_vvm_text));
		this.mWaterlockHelpText = ((TextView) findViewById(R.id.help_text_for_waterlock));
		this.mWaterlockHelpText.setText("");
		this.mWaterlockHelpText.setVisibility(View.INVISIBLE);
		// if (Settings.System.getInt(paramContext.getContentResolver(),
		// "lockscreen_wallpaper", 1) != 1) {
		// break label803;
		// }

		// this.mIsLiveWallpaper = bool;
		if (this.mContext.getPackageManager().hasSystemFeature(
				"com.sec.feature.hovering_ui") == true) {
			// this.mMissedCallList = new CircleMissedCallList(paramContext);
			// this.mUnreadMsgList = new CircleUnreadMsgList(paramContext);
			// this.mMissedCallList.setVisibility(View.INVISIBLE);
			// this.mUnreadMsgList.setVisibility(View.INVISIBLE);
		}

		this.mMissedCall.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View paramAnonymousView,
					MotionEvent paramAnonymousMotionEvent) {
				switch (paramAnonymousMotionEvent.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (!CircleMissedEventWidgetDA.this.mIsLiveWallpaper) {
						CircleMissedEventWidgetDA.this.mWaterlockHelpText
								.setVisibility(View.VISIBLE);
					}
					mUnlockMode = UnlockMode.MissedCall;
					paramAnonymousView.setTag("MissedEvent");
					CircleMissedEventWidgetDA.this.mCircleUnlockView
							.setOnCircleUnlockListener(CircleMissedEventWidgetDA.this.mCircleUnlockListener);
					CircleMissedEventWidgetDA.this.mMissedCallBackground
							.setBackgroundResource(R.drawable.keyguard_lockscreen_circle_press);
					CircleMissedEventWidgetDA.this.mMissedCallIcon
							.setImageResource(R.drawable.keyguard_lockscreen_missed_event_ic_missedcall_press);
					if (CircleMissedEventWidgetDA.this.mMissedCallCount > 0) {
						CircleMissedEventWidgetDA.this.mMissedCallBackground
								.setVisibility(View.VISIBLE);
						CircleMissedEventWidgetDA.this.mMissedCall
								.setVisibility(View.VISIBLE);
					}
					mMissedMsgBackground.setVisibility(View.INVISIBLE);
					mMissedMsg.setVisibility(View.INVISIBLE);
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_1_UP:
					CircleMissedEventWidgetDA.this.mMissedCallBackground
							.setBackgroundResource(R.drawable.keyguard_lockscreen_circle);
					CircleMissedEventWidgetDA.this.mMissedCallIcon
							.setImageResource(R.drawable.keyguard_lockscreen_missed_event_ic_missedcall);
					CircleMissedEventWidgetDA.this.mWaterlockHelpText
							.setVisibility(View.INVISIBLE);
					if (CircleMissedEventWidgetDA.this.mMissedMsgCount > 0) {
						mMissedMsgBackground.setVisibility(View.VISIBLE);
						mMissedMsg.setVisibility(View.VISIBLE);
					}
					break;

				}
				return CircleMissedEventWidgetDA.this.mCircleUnlockView
						.handleTouchEvent(paramAnonymousView,
								paramAnonymousMotionEvent);

			}
		});
		this.mMissedMsg.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View paramAnonymousView,
					MotionEvent paramAnonymousMotionEvent) {
				switch (paramAnonymousMotionEvent.getAction()) {
				case MotionEvent.ACTION_DOWN:
					CircleMissedEventWidgetDA.this.mCircleUnlockView
							.setOnCircleUnlockListener(CircleMissedEventWidgetDA.this.mCircleUnlockListener);
					mUnlockMode = UnlockMode.MissedMsg;
					paramAnonymousView.setTag("MissedEvent");
					if (!CircleMissedEventWidgetDA.this.mIsLiveWallpaper) {
						CircleMissedEventWidgetDA.this.mWaterlockHelpText
								.setVisibility(View.VISIBLE);
					}
					CircleMissedEventWidgetDA.this.mMissedMsgBackground
							.setBackgroundResource(R.drawable.keyguard_lockscreen_circle);
					CircleMissedEventWidgetDA.this.mMissedMsgIcon
							.setImageResource(R.drawable.keyguard_lockscreen_missed_event_ic_mms_press);
					if (CircleMissedEventWidgetDA.this.mMissedMsgCount > 0) {
						CircleMissedEventWidgetDA.this.mMissedMsgBackground
								.setVisibility(View.VISIBLE);
						CircleMissedEventWidgetDA.this.mMissedMsg
								.setVisibility(View.VISIBLE);
					}
					mMissedCallBackground.setVisibility(View.INVISIBLE);
					mMissedCall.setVisibility(View.INVISIBLE);
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_1_UP:
					CircleMissedEventWidgetDA.this.mMissedMsgBackground
							.setBackgroundResource(R.drawable.keyguard_lockscreen_circle);
					CircleMissedEventWidgetDA.this.mMissedMsgIcon
							.setImageResource(R.drawable.keyguard_lockscreen_missed_event_ic_mms);
					CircleMissedEventWidgetDA.this.mWaterlockHelpText
							.setVisibility(View.INVISIBLE);
					if (CircleMissedEventWidgetDA.this.mMissedCallCount > 0) {
						mMissedCallBackground.setVisibility(View.VISIBLE);
						mMissedCall.setVisibility(View.VISIBLE);
					}
					break;
				}
				return CircleMissedEventWidgetDA.this.mCircleUnlockView
						.handleTouchEvent(paramAnonymousView,
								paramAnonymousMotionEvent);
			}
		});
		this.mCircleUnlockListener = new CircleUnlockWidget.OnCircleUnlockListener() {
			public void onCircleUnlocked(View paramAnonymousView) {
				switch (CircleMissedEventWidgetDA.this.mUnlockMode) {
				case MissedCall:
					CircleMissedEventWidgetDA.this
							.sendIntent(CircleMissedEventWidgetDA.NotiMode.MissedCall);
					break;
				case MissedMsg:
					CircleMissedEventWidgetDA.this
							.sendIntent(CircleMissedEventWidgetDA.NotiMode.MissedMsg);
					break;
				case MissedVvm:
					CircleMissedEventWidgetDA.this
							.sendIntent(CircleMissedEventWidgetDA.NotiMode.MissedVvm);
					break;
				default:
					return;
				}
				CircleMissedEventWidgetDA.this.mCallback.goToUnlockScreen();
			}
		};
		IntentFilter localIntentFilter = new IntentFilter();
		// localIntentFilter
		// .addAction("com.android.server.NotificationManagerService.NotificationArrived");
		// localIntentFilter
		// .addAction("com.android.server.NotificationManagerService.NotificationRemoved");
		// localIntentFilter.addAction("android.intent.action.BOOT_COMPLETED");
		localIntentFilter
				.addAction("com.csipsimple.Notification.SipNotifications.broadcast");
		this.mResolver = this.mContext.getContentResolver();
		this.mBroadcastReceiver = new BroadcastReceiver() {
			public void onReceive(Context paramAnonymousContext,
					Intent paramAnonymousIntent) {
				// String str = paramAnonymousIntent.getAction();
				Message localMessage = CircleMissedEventWidgetDA.this.mHandler
						.obtainMessage(MISSED_EVENT_UPDATE);
				CircleMissedEventWidgetDA.this.mHandler
						.sendMessage(localMessage);
			}
		};
		paramContext.registerReceiver(this.mBroadcastReceiver,
				localIntentFilter);

		Message localMessage = CircleMissedEventWidgetDA.this.mHandler
				.obtainMessage(MISSED_EVENT_UPDATE);
		CircleMissedEventWidgetDA.this.mHandler.sendMessage(localMessage);
		// this.mBadgeObserver = new ContentObserver(this.mHandler)
		// {
		// public void onChange(boolean paramAnonymousBoolean)
		// {
		// Message localMessage =
		// CircleMissedEventWidgetDA.this.mHandler.obtainMessage(4802);
		// CircleMissedEventWidgetDA.this.mHandler.sendMessage(localMessage);
		// }
		// };
		// this.mResolver.registerContentObserver(Uri.parse("content://com.sec.badge/apps"),
		// true, this.mBadgeObserver);
		// updateMissedEvent();
		// if (SamsungLockScreenProperties.getalternatePackageForMessage() !=
		// null) {
		// this.MSG_PKG =
		// SamsungLockScreenProperties.getalternatePackageForMessage();
		// }
		// this.mMissedCall.setFocusableInTouchMode(true);
		// this.mMissedMsg.setFocusableInTouchMode(true);
		// return;
		//
		// }
	}

	private int getNumMissedEvent(NotiMode paramNotiMode) {
		int getNum = 0;
		// NotificationManager localNotificationManager = (NotificationManager)
		// this.mContext
		// .getSystemService("notification");
		// if (localNotificationManager == null) {
		// return 0;
		// }
		ContentResolver contentResolver = this.mContext.getContentResolver();
		// localNotificationManager.
		// localNotificationManager.notify(id, notification);
		Uri uri;
		try {
			if (paramNotiMode == NotiMode.MissedCall) {
				// this.mMissedCallNotiInfo = localNotificationManager
				// .getNotificationInfo("com.android.phone", 1);
				uri = Uri
						.parse("content://com.csipsimple.missedinfo/getMissedCallCount");
				getNum = contentResolver.update(uri, new ContentValues(), null,
						null);
			} else if (paramNotiMode == NotiMode.MissedMsg) {
				uri = Uri
						.parse("content://com.csipsimple.missedinfo/getMissedMsgCount");
				getNum = contentResolver.update(uri, new ContentValues(), null,
						null);
			}
		} catch (Exception e) {
			getNum = 0;
			e.printStackTrace();
		}

		return getNum;
	}

	private void handleMissedEventUpdate() {
		updateMissedEvent();
		updateMissedIcons();
	}

	private void sendIntent(NotiMode paramNotiMode) {
		// PendingIntent localPendingIntent=null;
		if (paramNotiMode == NotiMode.MissedCall) {
			Intent notificationIntent = new Intent(
					"com.csipsimple.phone.action.CALLLOG");
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent contentIntent = PendingIntent.getActivity(
					this.getContext(), 0, notificationIntent,
					PendingIntent.FLAG_CANCEL_CURRENT);
			try {
				contentIntent.send();
			} catch (CanceledException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (paramNotiMode == NotiMode.MissedMsg) {
			Intent notificationIntent = new Intent(
					"com.csipsimple.phone.action.MESSAGES");
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent contentIntent = PendingIntent.getActivity(
					this.getContext(), 0, notificationIntent,
					PendingIntent.FLAG_CANCEL_CURRENT);
			try {
				contentIntent.send();
			} catch (CanceledException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// for (;;)
		// {
		// if (localPendingIntent1 != null) {}
		// try
		// {
		// Log.d("CircleMissedEventWidget", "sendintent = " + paramNotiMode);
		// localPendingIntent1.send();
		// return;
		// }
		// catch (PendingIntent.CanceledException localCanceledException) {}
		// if ((paramNotiMode == NotiMode.MissedMsg) || (paramNotiMode ==
		// NotiMode.MissedEMail))
		// {
		// NotificationInfo localNotificationInfo1 = this.mUnreadMsgNotiInfo;
		// localPendingIntent1 = null;
		// if (localNotificationInfo1 != null)
		// {
		// PendingIntent localPendingIntent2 =
		// this.mUnreadMsgNotiInfo.contentIntent;
		// localPendingIntent1 = null;
		// if (localPendingIntent2 != null) {
		// localPendingIntent1 = this.mUnreadMsgNotiInfo.contentIntent;
		// }
		// }
		// }
		// else
		// {
		// NotiMode localNotiMode = NotiMode.MissedVvm;
		// localPendingIntent1 = null;
		// if (paramNotiMode == localNotiMode)
		// {
		// Intent localIntent = new
		// Intent("com.samsung.vvmapp.action.LAUNCH_VVM",
		// Uri.fromParts("voicemail", "", null));
		// localPendingIntent1 = PendingIntent.getActivity(this.mContext, 0,
		// localIntent, 1073741824);
		// }
		// }
		// }
	}

	private void updateMissedEvent() {
		int i = getNumMissedEvent(NotiMode.MissedCall);
		int j = getNumMissedEvent(NotiMode.MissedMsg);
		int m = 0, n = 0, i1 = 0;
		if (i != this.mMissedCallCount) {
			m = 1;
			if (i > 999) {
				this.mMissedCallCount = 999;
			} else {
				this.mMissedCallCount = i;
			}
		}
		if (j != this.mMissedMsgCount) {
			n = 1;
			if (j > 999) {
				this.mMissedMsgCount = 999;
			} else {
				this.mMissedMsgCount = j;
			}
		}
		if ((i1 != 0) || (m != 0) || (n != 0)) {
			this.mMissedCallTextView.setText(Integer
					.toString(this.mMissedCallCount));
			this.mMissedMsgTextView.setText(Integer
					.toString(this.mMissedMsgCount));
			this.mMissedVvmTextView.setText(Integer
					.toString(this.mMissedVvmCount));
		}

	}

	private void updateMissedIcons() {
		if ((this.mMissedCallCount > 0) || (this.mMissedMsgCount > 0)
				|| (this.mMissedVvmCount > 0)) {
			this.mUpperLayout.setVisibility(0);
			if (this.mMissedCallCount > 0) {
				this.mMissedCall.setVisibility(View.VISIBLE);
				this.mMissedCallBackground.setVisibility(View.VISIBLE);
			} else {
				this.mMissedCall.setVisibility(View.INVISIBLE);
				this.mMissedCallBackground.setVisibility(View.INVISIBLE);
			}

			if (this.mMissedMsgCount > 0) {
				this.mMissedMsg.setVisibility(View.VISIBLE);
				this.mMissedMsgBackground.setVisibility(View.VISIBLE);
			} else {
				this.mMissedMsg.setVisibility(View.INVISIBLE);
				this.mMissedMsgBackground.setVisibility(View.INVISIBLE);
			}
		} else {
			this.mUpperLayout.setVisibility(View.INVISIBLE);
		}
		invalidate();
	}

	public void cleanUp() {
		getContext().unregisterReceiver(this.mBroadcastReceiver);
		// this.mResolver.unregisterContentObserver(this.mBadgeObserver);
	}

	// public CircleMissedCallList getMissedCallList() {
	// return (CircleMissedCallList) this.mMissedCallList;
	// }

	public String getTTSMessage() {
		if ((this.mMissedCallCount > 0) && (this.mMissedMsgCount > 0)) {
			Context localContext3 = this.mContext;
			Object[] arrayOfObject3 = new Object[2];
			arrayOfObject3[0] = Integer.valueOf(this.mMissedCallCount);
			arrayOfObject3[1] = Integer.valueOf(this.mMissedMsgCount);
			return localContext3.getString(17040917, arrayOfObject3);
		}
		if (this.mMissedCallCount > 1) {
			Context localContext2 = this.mContext;
			Object[] arrayOfObject2 = new Object[1];
			arrayOfObject2[0] = Integer.valueOf(this.mMissedCallCount);
			return localContext2.getString(17040915, arrayOfObject2);
		}
		if (this.mMissedMsgCount > 1) {
			Context localContext1 = this.mContext;
			Object[] arrayOfObject1 = new Object[1];
			arrayOfObject1[0] = Integer.valueOf(this.mMissedMsgCount);
			return localContext1.getString(17040916, arrayOfObject1);
		}
		if (this.mMissedCallCount == 1) {
			return this.mContext.getString(17040913);
		}
		if (this.mMissedMsgCount == 1) {
			return this.mContext.getString(17040914);
		}
		return this.mContext.getString(17040912);
	}

	public String getTTSMessage(boolean paramBoolean) {
		String str = this.mContext.getString(17040921);
		if (paramBoolean) {
			if (this.mMissedCallCount > 1) {
				StringBuilder localStringBuilder2 = new StringBuilder();
				Context localContext2 = this.mContext;
				Object[] arrayOfObject2 = new Object[1];
				arrayOfObject2[0] = Integer.valueOf(this.mMissedCallCount);
				return localContext2.getString(17040915, arrayOfObject2) + ". "
						+ str;
			}
			if (this.mMissedCallCount == 1) {
				return this.mContext.getString(17040913) + ". " + str;
			}
		} else {
			if (this.mMissedMsgCount > 1) {
				StringBuilder localStringBuilder1 = new StringBuilder();
				Context localContext1 = this.mContext;
				Object[] arrayOfObject1 = new Object[1];
				arrayOfObject1[0] = Integer.valueOf(this.mMissedMsgCount);
				return localContext1.getString(17040916, arrayOfObject1) + ". "
						+ str;
			}
			if (this.mMissedCallCount == 1) {
				return this.mContext.getString(17040913) + ". " + str;
			}
		}
		return "";
	}

	// public CircleUnreadMsgList getUnreadMsgList() {
	// return (CircleUnreadMsgList) this.mUnreadMsgList;
	// }

	public void onPause() {
	}

	public void onResume() {
		updateMissedEvent();
	}

	public static enum NotiMode {
		MissedCall, MissedMsg, MissedEMail, MissedVvm
	}

	public static enum UnlockMode {
		MissedNone, MissedCall, MissedVvm, MissedMsg, MissedCallAndMsg
	}
}

/*
 * Location: C:\Users\Administrator\Desktop\classes-dex2jar.jar
 * 
 * Qualified Name: com.android.internal.policy.impl.CircleMissedEventWidgetDA
 * 
 * JD-Core Version: 0.7.0.1
 */