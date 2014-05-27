package ch.szederkenyi.heidi.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;

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

public class StoryboardActivity extends BaseActivity implements Callback {
    
    public static final String EXTRA_DATAFILE = "StoryboardActivity::Datafile";
    
    private LockableViewPager mViewPager;
    private StoryboardAdapter mAdapter;
    
    private Handler mTaskHandler;
    
    private FirstPageRunnable mFirstRunnable;
    private NextPageRunnable mNextRunnable;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storyboard);
        
        mTaskHandler = new Handler(this);
        
        mFirstRunnable = new FirstPageRunnable();
        mFirstRunnable.setActivity(this);
        
        mNextRunnable = new NextPageRunnable();
        mNextRunnable.setActivity(this);
        
        mAdapter = new StoryboardAdapter(getSupportFragmentManager());
        
        mViewPager = (LockableViewPager) findViewById(R.id.storyboard_viewpager);
        mViewPager.setAdapter(mAdapter);
        mViewPager.lock();
        
        final AppData appdata = AppData.getInstance();
        final MessageHandler handler = appdata.getMessageHandler();
        handler.register(FirstStoryMessage.class, this, mFirstRunnable);
        handler.register(NextStoryMessage.class, this, mNextRunnable);
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
        
        mViewPager = null;
        
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
    
    public boolean isLastPage() {
        return mViewPager.getCurrentItem() == mAdapter.getCount() - 1;
    }
    
    public void showNextPage() {
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
    }
    
    public void showFirstPage() {
        mViewPager.setCurrentItem(0, false);
    }
    
    public void showCategories() {
        finish();
        startActivity(new Intent(this, CategoryChooserActivity.class));
    }
    
    private static abstract class BaseRunnable implements Runnable {
        protected StoryboardActivity mActivity;

        public void setActivity(StoryboardActivity mActivity) {
            this.mActivity = mActivity;
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

}
