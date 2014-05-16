package ch.szederkenyi.heidi.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.example.heidi.R;

import ch.szederkenyi.heidi.ui.fragments.LanguageSelectorFragment;
import ch.szederkenyi.heidi.utils.Utils;

public class LaunchActivity extends FragmentActivity implements LanguageSelectorFragment.OnLanguageSelectedListener {
    
    private LanguageSelectorFragment mLangSelectorFragment;
    
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.main);
        
        final FragmentManager manager = getSupportFragmentManager();
        mLangSelectorFragment = (LanguageSelectorFragment) manager.findFragmentById(R.id.main_language_selector_fragment);
        mLangSelectorFragment.setOnLanguageSelectedListener(this);
    }
    
    @Override
    protected void onDestroy() {
        if(null != mLangSelectorFragment) {
            mLangSelectorFragment.setOnLanguageSelectedListener(null);
            mLangSelectorFragment = null;
        }
        
        super.onDestroy();
    }

    @Override
    public void onLanguageSelected(String langCode, String countryCode) {
        Utils.changeLanguage(langCode, countryCode);
        
        finish();
        startActivity(new Intent(this, StoryboardActivity.class));
    }

}
