package com.cuuuurzel.onScreenViews;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cuuuurzel.utils.MyUtils;

/**
 * Use this class to manage on-screen dialogs, just like facebook chat-heads.
 * There is an integrate gesture listener, so you don't have to care.
 */
public class OnScreenLayout extends RelativeLayout {

    /**
     * Current direction [ dx, dy ], (both between 0 and 1).
     */
    public float[] direction;

    /**
     * View movement speed.
     */
    public float mSpeed;

    /**
     * Size of the view in pixels.
     */
    public int mWidth, mHeight;

    /**
     * Layout params for the window manager
     */
    protected WindowManager.LayoutParams mLayoutParams;

    /**
     * If you need to dismiss the view outside use this value.
     */
    public boolean toRemove;

    /**
     * Use this to dynamically attach view to the main one.
     */
    protected LayoutInflater layoutInflater;

    public OnScreenLayout( Context c ) { this( c, 250, 250 ); }

    public OnScreenLayout( Context c, int w, int h ) {
        super( c );

        isVisible = true;
        toRemove  = false;
        direction = new float[]{ 0f, 0f };

        setupLayoutThings( w, h );
        setupGestureListener();
        setupInitialViewLayout();
    }

    public boolean onClick( MotionEvent me ) {
        Toast.makeText( getContext(), "View clicked!", Toast.LENGTH_SHORT ).show();
        return true;
    }

    public boolean onFling( MotionEvent m1, MotionEvent m2, float dx, float dy ) {
        Toast.makeText( getContext(), "Fling!", Toast.LENGTH_SHORT ).show();
        return true;
    }

    /**
     * Changes directions and moves the view.
     * Will also change animations.
     */
    public void update( long dt ) {

        float dx = this.direction[0] * mSpeed * dt;
        float dy = this.direction[1] * mSpeed * dt;
        float w = MyUtils.screenW(getContext());
        //float h = MyUtils.screenH( getContext() );

        if ( this.getX()+dx <= -w/2 || this.getX()+dx+this.getWidth() >= w/2 ) {
            onScreenBoundReached();
        } else {
            this.moveTo( this.getX()+dx, this.getY()+dy );
        }

        //Finally, commit changes to the view
        updateLayout();
    }

    /**
     * Will be called when the view touches the screen bounds.
     * If the view is moving, you may change direction here for bouncing effects.
     */
    protected void onScreenBoundReached() {
        Toast.makeText( getContext(), "Screen bound reached...", Toast.LENGTH_SHORT ).show();
    }

    /**
     * Will load the initial layout.
     */
    protected void setupInitialViewLayout() {
        this.setBackgroundColor(0xA0FFFFFF);
    }

    /**
     * Will remove the view from the window manager.
     */
    public void dismiss() {
        if ( !isVisible ) { return; }
        Context c = getContext();
        WindowManager wm = ( WindowManager ) c.getSystemService( Context.WINDOW_SERVICE );
        wm.removeView(this);
        isVisible = false;
    }

    /**
     * Will add the view to the window manager.
     */
    public void show() {
        if ( isVisible ) { return; }
        Context c = getContext();
        WindowManager wm = ( WindowManager ) c.getSystemService( Context.WINDOW_SERVICE );
        wm.addView(this, this.getLayoutParams());
        isVisible = true;
    }

    /**
     * Shows if hidden, hides if shown.
     */
    public void toogleShow() {
        if ( isVisible ) dismiss(); else show();
    }

//Less important thigs

    private boolean isVisible;
    private WindowManager mWindowManager;
    private GestureDetector mGestureDetector;

    private void moveTo( float x, float y ) {
        this.setX( x );
        this.setY(y);
    }

    private void setupGestureListener() {

        GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp( MotionEvent me ) {
                return OnScreenLayout.this.onClick( me );
            }

            @Override
            public boolean onFling( MotionEvent m1, MotionEvent m2, float dx, float dy ) {
                return OnScreenLayout.this.onFling( m1, m2, dx, dy );
            }
        };

        mGestureDetector = new GestureDetector( getContext(), gestureListener );
        setOnTouchListener(
                new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        mGestureDetector.onTouchEvent(motionEvent);
                        return true;
                    }
                }
        );
    }
    public final WindowManager.LayoutParams getLayoutParams() {
        return mLayoutParams;
    }

    private void updateLayout() {
        mLayoutParams.x = (int) this.getX();
        mLayoutParams.y = (int) this.getY();

        mWindowManager.updateViewLayout(
                this, mLayoutParams
        );
    }

    private void setupLayoutThings( int w, int h ) {

        Context c = getContext();

        mWidth    = w;
        mHeight   = h;
        mSpeed = 1;

        layoutInflater = ( LayoutInflater ) c.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        mWindowManager = ( WindowManager ) c.getSystemService( Context.WINDOW_SERVICE );

        mLayoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        mLayoutParams.width = mWidth;
        mLayoutParams.height = mHeight;

        this.moveTo( MyUtils.screenW(c)/2, MyUtils.screenH( c )/2 );
    }

    public boolean isVisible() { return isVisible; }
}
