package ch.szederkenyi.heidi.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import ch.szederkenyi.heidi.utils.Utils;

import com.example.heidi.R;

public class LanguageSelectorFragment extends BaseFragment implements OnClickListener {
    
    private Button mEnglishButton;
    private Button mGermanButton;
    private Button mFrenchButton;
    private Button mItalianButton;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View contentView = inflater.inflate(R.layout.language_selector, container, false);
        
        mEnglishButton = findViewById(contentView, R.id.language_selector_english);
        mGermanButton = findViewById(contentView, R.id.language_selector_english);
        mFrenchButton = findViewById(contentView, R.id.language_selector_english);
        mItalianButton = findViewById(contentView, R.id.language_selector_english);
        
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

    private Button findViewById(View contentView, int id) {
        final Button button = (Button) contentView.findViewById(id);
        button.setOnClickListener(this);
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
        final String langCode = (String) v.getTag();
        Utils.changeLanguage(langCode, countryCode);
    }

}
