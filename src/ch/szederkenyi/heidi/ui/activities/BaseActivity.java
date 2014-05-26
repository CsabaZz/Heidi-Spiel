package ch.szederkenyi.heidi.ui.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import ch.szederkenyi.heidi.StaticContextApplication;

public class BaseActivity extends FragmentActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StaticContextApplication.addActivity(this);
        super.onCreate(savedInstanceState);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        StaticContextApplication.removeActivity(this);
    }

}
