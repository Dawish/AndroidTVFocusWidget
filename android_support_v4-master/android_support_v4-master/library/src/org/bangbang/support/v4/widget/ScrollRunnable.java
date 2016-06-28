package org.bangbang.support.v4.widget;

import android.util.Log;
import android.view.View;
import android.widget.Scroller;

abstract class ScrollRunnable implements Runnable {
	private final String TAG = ScrollRunnable.class.getSimpleName();
	private static final boolean DEBUG = true;
	private final View mTarget;

	private Scroller mScroller;

	private int mLastFlingY;

	private int mFinishedAmount;
	private boolean mHasFinish;
	private int mAmount;

	public ScrollRunnable(View targetView) {
		mTarget = targetView;
		mScroller = new Scroller(mTarget.getContext());
	}

	public void start(int amount, int duration) {
		if (!mHasFinish) {
			end();
		}
		
		if (DEBUG) {
			Log.d(TAG, "start scroll. amount: " + amount);
		}

		mAmount = amount;
		onScrollBegin();
		mScroller.startScroll(0, 0, 0, amount, duration);
		mTarget.post(this);
	}
	
	public void finishImmediatelyIfNecessary() {
		boolean more = mScroller.computeScrollOffset();
		if (more) {
			end();
		}
	}

	private void end() {
		mTarget.removeCallbacks(this);
		int remainder = mAmount - mFinishedAmount;
		if (Math.abs(remainder) > 0) {
			if (DEBUG) {
				Log.d(TAG, "previous scroll in not finished, " +
						"finish it now. remainder: " + remainder);
			}
			doScroll(remainder);
		}

		reset();
	}

	@Override
	public void run() {
		final Scroller scroller = mScroller;
		boolean more = scroller.computeScrollOffset();
		final int y = scroller.getCurrY();
		int delta = mLastFlingY - y;
		delta = -delta;
		mFinishedAmount += delta;
		if (DEBUG) {
			Log.d(TAG, "mTotal: " + mFinishedAmount);
			Log.d(TAG, "more: " + more + " delta: " + delta
					+ " time passed: " + scroller.timePassed());
		}
		
		if (more
		// && Math.abs(delta) > 0
		) {
			doScroll(delta);
			mLastFlingY = y;

			mHasFinish = false;
			// going on
			mTarget.post(this);
		} else {
			mHasFinish = true;
			reset();
			onScrollEnd();
		}
	}
	
    abstract protected void onScrollBegin();

	abstract protected void scrollBy(int delta);

    abstract protected void onScrollEnd();

	private void doScroll(int delta) {
		if (DEBUG) {
			Log.d(TAG, "doScroll(). delta: " + delta);
		}
		scrollBy(delta);
	}

	private void reset() {
		mAmount = 0;
		mLastFlingY = 0;
		mFinishedAmount = 0;
	}
}