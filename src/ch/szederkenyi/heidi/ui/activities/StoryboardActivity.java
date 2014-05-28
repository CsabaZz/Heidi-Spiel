package ch.szederkenyi.heidi.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import ch.szederkenyi.heidi.AppData;
import ch.szederkenyi.heidi.R;
import ch.szederkenyi.heidi.async.AbstractTask;
import ch.szederkenyi.heidi.async.StoryboardTask;
import ch.szederkenyi.heidi.data.entities.BaseEntity;
import ch.szederkenyi.heidi.messages.FirstStoryMessage;
import ch.szederkenyi.heidi.messages.MessageHandler;
import ch.szederkenyi.heidi.messages.NextStoryMessage;
import ch.szederkenyi.heidi.ui.adapters.StoryboardAdapter;
import ch.szederkenyi.heidi.ui.views.LockableViewPager;
import ch.szederkenyi.heidi.utils.ConstantUtils;

import java.util.Collection;

public class StoryboardActivity extends BaseActivity implements Callback, OnPageChangeListener {
    
    public static final String EXTRA_DATAFILE = "StoryboardActivity::Datafile";
    
    private LockableViewPager mViewPager;
    private StoryboardAdapter mAdapter;
    
    //private ViewGroup mButtonLayout;
    
    //private Button mPreviousButton;
    //private Button mFirstButton;
    //private Button mNextButton;
    //private Button mLastButton;
    
    private Handler mTaskHandler;
    
    //private PreviousPageRunnable mPreviousRunnable;
    private FirstPageRunnable mFirstRunnable;
    private NextPageRunnable mNextRunnable;
    //private LastPageRunnable mLastRunnable;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storyboard);
        
        mTaskHandler = new Handler(this);
        
        //mPreviousRunnable = new PreviousPageRunnable();
        //mPreviousRunnable.setActivity(this);
        
        mFirstRunnable = new FirstPageRunnable();
        mFirstRunnable.setActivity(this);
        
        mNextRunnable = new NextPageRunnable();
        mNextRunnable.setActivity(this);
        
        //mLastRunnable = new LastPageRunnable();
        //mLastRunnable.setActivity(this);
        
        mAdapter = new StoryboardAdapter(getSupportFragmentManager());
        
        mViewPager = (LockableViewPager) findViewById(R.id.storyboard_viewpager);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(this);
        mViewPager.lock();
        
        //mButtonLayout = (ViewGroup) findViewById(R.id.storyboard_button_layout);
        
        //mPreviousButton = (Button) findViewById(R.id.storyboard_previous_page);
        //mFirstButton = (Button) findViewById(R.id.storyboard_first_page);
        //mNextButton = (Button) findViewById(R.id.storyboard_next_page);
        //mLastButton = (Button) findViewById(R.id.storyboard_last_page);
        
        //mPreviousButton.setOnClickListener(new MessageSendClickListener(PreviousStoryMessage.class));
        //mFirstButton.setOnClickListener(new MessageSendClickListener(FirstStoryMessage.class));
        //mNextButton.setOnClickListener(new MessageSendClickListener(NextStoryMessage.class));
        //mLastButton.setOnClickListener(new MessageSendClickListener(LastStoryMessage.class));
        
        final AppData appdata = AppData.getInstance();
        final MessageHandler handler = appdata.getMessageHandler();
        //handler.register(PreviousStoryMessage.class, this, mPreviousRunnable);
        handler.register(FirstStoryMessage.class, this, mFirstRunnable);
        handler.register(NextStoryMessage.class, this, mNextRunnable);
        //handler.register(LastStoryMessage.class, this, mLastRunnable);
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        
        if(mAdapter.isEmpty()) {
            final Intent intent = getIntent();
            final String dataFile = intent.getStringExtra(EXTRA_DATAFILE);
            
            final AbstractTask.Entity entity = new AbstractTask.Entity();
            entity.filename = dataFile;
            
            final StoryboardTask task = new StoryboardTask(mTaskHandler, ConstantUtils.MSG_TASK);
            task.start(entity);
        }
    }
    
    @Override
    protected void onDestroy() {
        final AppData appdata = AppData.getInstance();
        final MessageHandler handler = appdata.getMessageHandler();
        handler.unregister(NextStoryMessage.class, this);
        handler.unregister(FirstStoryMessage.class, this);
        
        if(null != mNextRunnable) {
            mNextRunnable.setActivity(null);
            mNextRunnable = null;
        }
        
        if(null != mFirstRunnable) {
            mFirstRunnable.setActivity(null);
            mFirstRunnable = null;
        }
        
        //if(null != mPreviousButton) {
            //mPreviousButton.setOnClickListener(null);
            //mPreviousButton = null;
        //}
        
        //if(null != mFirstButton) {
            //mFirstButton.setOnClickListener(null);
            //mFirstButton = null;
        //}
        
        //if(null != mNextButton) {
            //mNextButton.setOnClickListener(null);
            //mNextButton = null;
        //}
        
        //if(null != mLastButton) {
            //mLastButton.setOnClickListener(null);
            //mLastButton = null;
        //}
        
        if(null != mViewPager) {
            mViewPager.setOnPageChangeListener(null);
            mViewPager.setAdapter(null);
            mViewPager = null;
        }
        
        //mButtonLayout = null;
        
        super.onDestroy();
    }

    @Override
    public boolean handleMessage(Message msg) {
        if(msg.what == ConstantUtils.MSG_TASK) {
            @SuppressWarnings("unchecked")
            final Collection<? extends BaseEntity> entities = (Collection<? extends BaseEntity>) msg.obj;
            mAdapter.addAll(entities);
            mAdapter.notifyDataSetChanged();
            
            return true;
        }
        
        return false;
    }
    
    public boolean isFirstPage() {
        return mViewPager.getCurrentItem() == 0;
    }
    
    public boolean isLastPage() {
        return mViewPager.getCurrentItem() == mAdapter.getCount() - 1;
    }
    
    public void showPreviousPage() {
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, true);
    }
    
    public void showNextPage() {
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
    }
    
    public void showFirstPage() {
        mViewPager.setCurrentItem(0, false);
    }
    
    public void showLastPage() {
        mViewPager.setCurrentItem(mAdapter.getCount() - 1, false);
    }
    
    public void showCategories() {
        finish();
        startActivity(new Intent(this, CategoryChooserActivity.class));
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onPageSelected(int position) {
        //mPreviousButton.setEnabled(position > 0);
        //mFirstButton.setEnabled(position > 0);
        //mNextButton.setEnabled(position < mAdapter.getCount());
        //mLastButton.setEnabled(position < mAdapter.getCount() - 1);
    }
    
    private static abstract class BaseRunnable implements Runnable {
        protected StoryboardActivity mActivity;

        public void setActivity(StoryboardActivity mActivity) {
            this.mActivity = mActivity;
        }
        
    }
    
    private static class PreviousPageRunnable extends BaseRunnable {

        @Override
        public void run() {
            if(mActivity.isFirstPage()) {
                //Do something
            } else {
                mActivity.showNextPage();
            }
        }
    }
    
    private static class FirstPageRunnable extends BaseRunnable {

        @Override
        public void run() {
            mActivity.showFirstPage();
        }
    }
    
    private static class NextPageRunnable extends BaseRunnable {

        @Override
        public void run() {
            if(mActivity.isLastPage()) {
                mActivity.showCategories();
            } else {
                mActivity.showNextPage();
            }
        }
    }
    
    private static class LastPageRunnable extends BaseRunnable {

        @Override
        public void run() {
            mActivity.showLastPage();
        }
    }

}
