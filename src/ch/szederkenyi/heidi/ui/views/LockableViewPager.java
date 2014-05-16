
package ch.szederkenyi.heidi.ui.views;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class LockableViewPager extends ViewPager {
    private static final String STATE_SUPER = "LockableViewPager::SuperState";
    private static final String STATE_LOCKED = "LockableViewPager::LockedState";

    private boolean mLocked;

    public LockableViewPager(Context context) {
        super(context);
    }

    public LockableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    @Override
    public boolean canScrollHorizontally(int direction) {
        if (this.mLocked) {
            return false;
        } else {
            return super.canScrollHorizontally(direction);
        }
    }

    @Override
    public final void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;

            mLocked = bundle.getBoolean(STATE_LOCKED, false);

            final Parcelable superState = bundle.getParcelable(STATE_SUPER);
            super.onRestoreInstanceState(superState);
        } else {
            super.onRestoreInstanceState(null);
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();

        bundle.putBoolean(STATE_LOCKED, mLocked);
        bundle.putParcelable(STATE_SUPER, super.onSaveInstanceState());

        return bundle;
    }

    public boolean isLocked() {
        return this.mLocked;
    }

    public void lock() {
        setLocked(true);
    }

    public void unlock() {
        setLocked(false);
    }

    public void setLocked(boolean locked) {
        this.mLocked = locked;
    }

}
