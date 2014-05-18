package ch.szederkenyi.heidi.ui.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.support.v4.app.FragmentActivity;

import com.example.heidi.R;

import java.util.Collection;

import ch.szederkenyi.heidi.AppData;
import ch.szederkenyi.heidi.async.AbstractTask;
import ch.szederkenyi.heidi.async.StoryboardTask;
import ch.szederkenyi.heidi.data.entities.BaseEntity;
import ch.szederkenyi.heidi.messages.MessageHandler;
import ch.szederkenyi.heidi.messages.NextStoryMessage;
import ch.szederkenyi.heidi.ui.adapters.StoryboardAdapter;
import ch.szederkenyi.heidi.ui.views.LockableViewPager;

public class StoryboardActivity extends FragmentActivity implements Callback, Runnable {
    
    private static final int MSG_TASK = 1;
    
    private LockableViewPager mViewPager;
    private StoryboardAdapter mAdapter;
    
    private Handler mTaskHandler;
    
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.storyboard);
        
        mTaskHandler = new Handler(this);
        
        mAdapter = new StoryboardAdapter(getSupportFragmentManager());
        
        mViewPager = (LockableViewPager) findViewById(R.id.storyboard_viewpager);
        mViewPager.setAdapter(mAdapter);
        mViewPager.lock();
        
        final AppData appdata = AppData.getInstance();
        final MessageHandler handler = appdata.getMessageHandler();
        handler.register(NextStoryMessage.class, this, this);
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        
        if(mAdapter.isEmpty()) {
            final AbstractTask.Entity entity = new AbstractTask.Entity();
            entity.filename = "data.json";
            
            final StoryboardTask task = new StoryboardTask(mTaskHandler, MSG_TASK);
            task.start(entity);
        }
    }
    
    @Override
    protected void onDestroy() {
        final AppData appdata = AppData.getInstance();
        final MessageHandler handler = appdata.getMessageHandler();
        handler.unregister(NextStoryMessage.class, this);
        
        mViewPager = null;
        super.onDestroy();
    }

    @Override
    public boolean handleMessage(Message msg) {
        if(msg.what == MSG_TASK) {
            @SuppressWarnings("unchecked")
            final Collection<? extends BaseEntity> entities = (Collection<? extends BaseEntity>) msg.obj;
            mAdapter.addAll(entities);
            mAdapter.notifyDataSetChanged();
            
            return true;
        }
        
        return false;
    }

    @Override
    public void run() {
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
    }

}
