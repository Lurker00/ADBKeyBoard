package com.android.adbkeyboard;

import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class Rotate extends Service {
    static final String LOG_TAG = "RotateService";
    static final String ACTION = "name.lurker.RotateScreen";

    static private Rotate mSelf = null;
    private WindowManager wm;
    private LinearLayout orientationChanger;
    private WindowManager.LayoutParams orientationLayout;
    private boolean mLocked = false;
    private BroadcastReceiver mReceiver = null;

    public Rotate() {
        super();
        Log.i(LOG_TAG, "Ctor");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSelf = this;
        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        orientationChanger = new LinearLayout(this);
        orientationLayout = new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
                , 0
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                , PixelFormat.RGBA_8888);
        if (mReceiver == null) {
            IntentFilter filter = new IntentFilter(ACTION);
            mReceiver = new Receiver();
            registerReceiver(mReceiver, filter);
        }
        Log.i(LOG_TAG, "onCreate");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(LOG_TAG, "onStartCommand: "+intent);
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mReceiver != null)
            unregisterReceiver(mReceiver);
        Set(-1);
        mSelf = null;
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(LOG_TAG, "onBind");
        return null;
    }

    // android:permission="android.permission.SYSTEM_ALERT_WINDOW"
    private void Set(int orientation) {
        if (orientationChanger == null || orientationLayout == null) {
            Log.w(LOG_TAG, "Not initialized"); return;
        }
        try {
            if (mLocked) {
                mLocked = false;
                wm.removeView(orientationChanger);
            }
            if (orientation >= 0) {
                orientationLayout.screenOrientation = orientation;
                wm.addView(orientationChanger, orientationLayout);
                orientationChanger.setVisibility(View.VISIBLE);
                mLocked = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void Start(Context context) {
        if ( context != null && mSelf == null ) {
            context.startService(new Intent(context, Rotate.class));
        }
    }

    static void stop(Context context) {
        if ( context != null && mSelf != null ) {
            context.stopService(new Intent(context, Rotate.class));
        }
    }

    class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(LOG_TAG, "Action: "+intent.getAction());
            if (!intent.getAction().equals(ACTION)) return;
            if (mSelf == null) {
                Log.i(LOG_TAG, "mSelf == null");
                return;
            }
            int orientation = intent.getIntExtra("o", -1);
            Log.i(LOG_TAG, "Orientation: "+orientation);
            mSelf.Set(orientation);
        }
    }
}
