package ch.szederkenyi.heidi.ui.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.example.heidi.R;

import ch.szederkenyi.heidi.ui.views.LockableViewPager;

public class StoryboardActivity extends FragmentActivity {
    
    private LockableViewPager mViewPager;
    
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.storyboard);
        
        mViewPager = (LockableViewPager) findViewById(R.id.storyboard_viewpager);
    }
    
    @Override
    protected void onDestroy() {
        mViewPager = null;
        super.onDestroy();
    }

}
