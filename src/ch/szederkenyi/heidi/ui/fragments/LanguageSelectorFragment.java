package ch.szederkenyi.heidi.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.heidi.R;

public class LanguageSelectorFragment extends BaseFragment implements OnClickListener {
    
    private Button mEnglishButton;
    private Button mGermanButton;
    private Button mFrenchButton;
    private Button mItalianButton;
    
    private OnLanguageSelectedListener mSelectionListener;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View contentView = inflater.inflate(R.layout.language_selector, container, false);
        
        mEnglishButton = findViewById(contentView, R.id.language_selector_english, "en");
        mGermanButton = findViewById(contentView, R.id.language_selector_german, "de");
        mFrenchButton = findViewById(contentView, R.id.language_selector_french, "fr");
        mItalianButton = findViewById(contentView, R.id.language_selector_italian, "it");
        
        return contentView;
    }
    
    @Override
    public void onDestroyView() {
        mEnglishButton = destroyButton(mEnglishButton);
        mGermanButton = destroyButton(mGermanButton);
        mFrenchButton = destroyButton(mFrenchButton);
        mItalianButton = destroyButton(mItalianButton);
        
        super.onDestroyView();
    }

    private Button findViewById(View contentView, int id, String langCode) {
        final Button button = (Button) contentView.findViewById(id);
        button.setOnClickListener(this);
        button.setTag(langCode);
        return button;
    }
    
    private Button destroyButton(Button button) {
        if(null != button) {
            button.setOnClickListener(null);
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        if(null != mSelectionListener) {
            final String langCode = (String) v.getTag();
            final String countryCode = "";
            mSelectionListener.onLanguageSelected(langCode, countryCode);
        }
    }
    
    public OnLanguageSelectedListener getOnLanguageSelectedListener() {
        return mSelectionListener;
    }

    public void setOnLanguageSelectedListener(OnLanguageSelectedListener selectionListener) {
        mSelectionListener = selectionListener;
    }

    public interface OnLanguageSelectedListener {
        void onLanguageSelected(String langCode, String countryCode);
    }

}
