/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.szederkenyi.heidi.ui;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.Fragment.SavedState;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import ch.szederkenyi.heidi.ui.fragments.BaseFragment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Implementation of {@link android.support.v4.view.PagerAdapter} that
 * uses a {@link Fragment} to manage each page. This class also handles
 * saving and restoring of fragment's state.
 *
 * <p>This version of the pager is more useful when there are a large number
 * of pages, working more like a list view.  When pages are not visible to
 * the user, their entire fragment may be destroyed, only keeping the saved
 * state of that fragment.  This allows the pager to hold on to much less
 * memory associated with each visited page as compared to
 * {@link FragmentPagerAdapter} at the cost of potentially more overhead when
 * switching between pages.
 *
 * <p>When using FragmentPagerAdapter the host ViewPager must have a
 * valid ID set.</p>
 *
 * <p>Subclasses only need to implement {@link #getItem(int)}
 * and {@link #getCount()} to have a working adapter.
 *
 * <p>Here is an example implementation of a pager containing fragments of
 * lists:
 *
 * {@sample development/samples/Support13Demos/src/com/example/android/supportv13/app/FragmentStatePagerSupport.java
 *      complete}
 *
 * <p>The <code>R.layout.fragment_pager</code> resource of the top-level fragment is:
 *
 * {@sample development/samples/Support13Demos/res/layout/fragment_pager.xml
 *      complete}
 *
 * <p>The <code>R.layout.fragment_pager_list</code> resource containing each
 * individual fragment's layout is:
 *
 * {@sample development/samples/Support13Demos/res/layout/fragment_pager_list.xml
 *      complete}
 */
public abstract class FragmentHashStatePagerAdapter extends PagerAdapter {
    private static final String TAG = "FragmentStatePagerAdapter";
    private static final String KEY_STATES = "FragmentStatePagerAdapter::States";
    private static final boolean DEBUG = false;

    private final FragmentManager mFragmentManager;
    private FragmentTransaction mCurTransaction;

    private HashMap<String, Fragment.SavedState> mSavedState;
    private ArrayList<Fragment> mFragments;
    private Fragment mCurrentPrimaryItem;

    public FragmentHashStatePagerAdapter(FragmentManager fm) {
        mFragmentManager = fm;
        mSavedState = new HashMap<String, Fragment.SavedState>();
        mFragments = new ArrayList<Fragment>();
    }

    /**
     * Return the Fragment associated with a specified position.
     */
    public abstract Fragment getItem(int position);

    @Override
    public void startUpdate(ViewGroup container) {
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // If we already have this item instantiated, there is nothing
        // to do.  This can happen when we are restoring the entire pager
        // from its saved state, where the fragment manager has already
        // taken care of restoring the fragments we previously had instantiated.
        if (mFragments.size() > position) {
            Fragment f = mFragments.get(position);
            if (f != null) {
                return f;
            }
        }

        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }

        Fragment fragment = getItem(position);
        if (DEBUG) Log.v(TAG, "Adding item #" + position + ": f=" + fragment);
        
        final String tag = getTagOfFragment(fragment);
        if (mSavedState.containsKey(tag)) {
            Fragment.SavedState fss = mSavedState.get(tag);
            if (fss != null) {
                fragment.setInitialSavedState(fss);
            }
        }
        fixBug(fragment);
        while (mFragments.size() <= position) {
            mFragments.add(null);
        }
        fragment.setMenuVisibility(false);
        fragment.setUserVisibleHint(false);
        mFragments.set(position, fragment);
        mCurTransaction.add(container.getId(), fragment);

        return fragment;
    }

    
    /**
     * Workaround for the following bug in Support Library: https://code.google.com/p/android/issues/detail?id=37484
     * 
     * @param fragment fragment which has the bug
     */
    private void fixBug(Fragment fragment) {
        final Bundle savedFragmentState = (Bundle) getSavedFragmentState(fragment);
        if (null != savedFragmentState) {
            savedFragmentState.setClassLoader(fragment.getClass().getClassLoader());
        }
    }

    private Object getSavedFragmentState(Fragment fragment) {
        try {
            final Field mSavedFragmentStateField = Fragment.class
                    .getDeclaredField("mSavedFragmentState");
            if (null != mSavedFragmentStateField) {
                mSavedFragmentStateField.setAccessible(true);
                final Bundle mSavedFragmentState = (Bundle) mSavedFragmentStateField.get(fragment);
                if (null != mSavedFragmentState) {
                    return mSavedFragmentState;
                }
            }
        } catch (NoSuchFieldException ex) {
            Log.e(TAG, "Can not find such field: mSavedFragmentState", ex);
        } catch (IllegalAccessException ex) {
            Log.e(TAG, "Can not access to field: mSavedFragmentState", ex);
        } catch (IllegalArgumentException ex) {
            Log.e(TAG, "Somethign goes wrong with mSavedFragmentState", ex);
        }
        
        return null;
    }
    
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if(!mFragmentManager.getFragments().contains(object)) {
            return;
        }
        
        Fragment fragment = (Fragment)object;
        
        if(null == fragment) {
            return;
        }

        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }
        if (DEBUG) Log.v(TAG, "Removing item #" + position + ": f=" + object
                + " v=" + ((Fragment)object).getView());
        mSavedState.put(getTagOfFragment(fragment), mFragmentManager.saveFragmentInstanceState(fragment));
        
        mFragments.set(position, null);

        mCurTransaction.remove(fragment);
    }
    
    private String getTagOfFragment(Fragment fragment) {
        if(fragment instanceof BaseFragment) {
            return ((BaseFragment)fragment).getStackName();
        } else {
            return fragment.getTag();
        }
    }
    
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment)object;
        if (fragment != mCurrentPrimaryItem) {
            if (mCurrentPrimaryItem != null) {
                mCurrentPrimaryItem.setMenuVisibility(false);
                mCurrentPrimaryItem.setUserVisibleHint(false);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }
            mCurrentPrimaryItem = fragment;
        }
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        if (mCurTransaction != null) {
            mCurTransaction.commitAllowingStateLoss();
            mCurTransaction = null;
            mFragmentManager.executePendingTransactions();
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment)object).getView() == view;
    }

    public BaseFragment getFragment(int position) {
        if (mFragments.size() > position) {
            final BaseFragment f = (BaseFragment) mFragments.get(position);
            if (f != null) {
                return f;
            }
        }
        
        return null;
    }

    @Override
    public Parcelable saveState() {
        Bundle state = new Bundle();
        state.putSerializable(KEY_STATES, mSavedState);
        for (int i=0; i<mFragments.size(); i++) {
            Fragment f = mFragments.get(i);
            if (f != null) {
                String key = "f" + i;
                mFragmentManager.putFragment(state, key, f);
            }
        }
        return state;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        if (state != null) {
            Bundle bundle = (Bundle)state;
            bundle.setClassLoader(loader);
            
            restoreState(bundle);
        }
    }

    @SuppressWarnings("unchecked")
    protected void restoreState(Bundle bundle) {
        mSavedState.clear();
        mFragments.clear();
        mSavedState = (HashMap<String, SavedState>) bundle.getSerializable(KEY_STATES);
        Iterable<String> keys = bundle.keySet();
        for (String key: keys) {
            if (key.startsWith("f")) {
                int index = Integer.parseInt(key.substring(1));
                Fragment f = mFragmentManager.getFragment(bundle, key);
                if (f != null) {
                    while (mFragments.size() <= index) {
                        mFragments.add(null);
                    }
                    f.setMenuVisibility(false);
                    mFragments.set(index, f);
                } else {
                    Log.w(TAG, "Bad fragment at key " + key);
                }
            }
        }
    }
}
