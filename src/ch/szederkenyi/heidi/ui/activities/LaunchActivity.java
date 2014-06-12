package ch.szederkenyi.heidi.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import ch.szederkenyi.heidi.R;
import ch.szederkenyi.heidi.data.storage.StorageManager;
import ch.szederkenyi.heidi.ui.fragments.LanguageSelectorFragment;
import ch.szederkenyi.heidi.utils.Utils;

public class LaunchActivity extends BaseActivity implements LanguageSelectorFragment.OnLanguageSelectedListener {
    
    private static final String INTRO = "data.json";
    
    private LanguageSelectorFragment mLangSelectorFragment;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        
        if(StorageManager.isCategoryCompleted(INTRO)) {
            startActivity(new Intent(this, CategoryChooserActivity.class));
        } else {
            final Intent storyIntent = new Intent(this, StoryboardActivity.class);
            storyIntent.putExtra(StoryboardActivity.EXTRA_DATAFILE, INTRO);
            startActivity(storyIntent);
        }
    }

}
