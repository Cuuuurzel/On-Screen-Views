package com.cuuuurzel.onScreenViews;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import java.util.ArrayList;

/**
 * Use this class to manage you on-screen views.
 */
public class OnScreenLayoutsManagementService extends Service {

    /**
     * Animation's FPS.
     */
    public static float FPS = 30;

    /**
     * Used to post the update callback.
     */
    public static int DT = ( int )( 1000 / FPS );

    /**
     * Used to post updates.
     */
    private Handler mHandler;

    /**
     * List of attached views.
     */
    protected ArrayList<OnScreenLayout> views;

    /**
     * Used to update all the views.
     */
    public Runnable updateCallback = new Runnable() {

        public void run() {

            //Get milliseconds from last update
            long currentTime = System.currentTimeMillis();
            long dt = currentTime - lastUpdateTime;

            //Update all visible views
            for ( OnScreenLayout view : views ) {
                if ( view.isVisible() ) { view.update( dt ); }
                if ( view.toRemove ) { view.dismiss(); }
            }

            //Get ready for next update
            lastUpdateTime = currentTime;
            scheduleUpdate();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
        views = new ArrayList<OnScreenLayout>();
        createInitialViews();
        scheduleUpdate();
    }

    /**
     * Called by the constructor to create the first views.
     */
    protected void createInitialViews() {}

//Less important things

    private void scheduleUpdate() {
        mHandler.postDelayed( updateCallback, DT );
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }

    private long lastUpdateTime;
}