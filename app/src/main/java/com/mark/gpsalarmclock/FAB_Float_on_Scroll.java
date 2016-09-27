package com.mark.gpsalarmclock;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;


public class FAB_Float_on_Scroll extends FloatingActionButton.Behavior {
    final String LOG_TAG = "myLogs";


    public FAB_Float_on_Scroll(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);

     //   Log.d(LOG_TAG, "coordinatorLayout " + coordinatorLayout);
       // Log.d(LOG_TAG, "child " + child);
       // Log.d(LOG_TAG, "AonNestedScroll " + target.get);
      //  Log.d(LOG_TAG, "dxConsume " + dxConsumed + " y= " + dyConsumed);
     //   Log.d(LOG_TAG, "dxUnconsumed " + dxUnconsumed + "y " + dyUnconsumed);
        //child -> Floating Action Button
        if (dyConsumed > 0) {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
            int fab_bottomMargin = layoutParams.bottomMargin;
            child.animate().translationY(child.getHeight() + fab_bottomMargin).setInterpolator(new LinearInterpolator()).start();
        } else if (dyConsumed < 0) {
            child.animate().translationY(0).setInterpolator(new LinearInterpolator()).start();
        }
        else {
            if(dyConsumed == 0 && dyUnconsumed < 0) {
            //    Log.d(LOG_TAG, "open" );
                child.animate().translationY(0).setInterpolator(new LinearInterpolator()).start();

            }
            else  {
                if(dyConsumed == 0 && dyUnconsumed > 0) {
                //    Log.d(LOG_TAG, "hide" );
                    CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
                    int fab_bottomMargin = layoutParams.bottomMargin;
                    child.animate().translationY(child.getHeight() + fab_bottomMargin).setInterpolator(new LinearInterpolator()).start();
                }
            }
        }
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }
}

