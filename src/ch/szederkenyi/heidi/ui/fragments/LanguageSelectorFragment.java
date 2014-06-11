package ch.szederkenyi.heidi.ui.fragments;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import ch.szederkenyi.heidi.R;
import ch.szederkenyi.heidi.media.ImageManager;

public class LanguageSelectorFragment extends BaseFragment implements OnClickListener {
    
    private ImageView mImageView;
    
    private Button mEnglishButton;
    private Button mGermanButton;
    private Button mFrenchButton;
    private Button mItalianButton;
    
    private OnLanguageSelectedListener mSelectionListener;
    
    private MediaPlayer mBgMusic;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View contentView = inflater.inflate(R.layout.language_selector, container, false);
        
        mImageView = (ImageView) contentView.findViewById(R.id.language_selector_image);
        
        ImageManager.loadImageFromAsset(mImageView, "heidi-im-alps.jpg");
        
        mEnglishButton = findViewById(contentView, R.id.language_selector_english, "en");
        mGermanButton = findViewById(contentView, R.id.language_selector_german, "de");
        mFrenchButton = findViewById(contentView, R.id.language_selector_french, "fr");
        mItalianButton = findViewById(contentView, R.id.language_selector_italian, "it");

        mBgMusic = MediaPlayer.create(inflater.getContext(), R.raw.heidi_game_einleitung);
        mBgMusic.setAudioStreamType(AudioManager.STREAM_SYSTEM);
        mBgMusic.setLooping(true);
        mBgMusic.start();
        
        return contentView;
    }
    
    @Override
    public void onDestroyView() {
        mEnglishButton = destroyButton(mEnglishButton);
        mGermanButton = destroyButton(mGermanButton);
        mFrenchButton = destroyButton(mFrenchButton);
        mItalianButton = destroyButton(mItalianButton);
        
        mImageView = null;
        
        if(null != mBgMusic) {
            mBgMusic.stop();
            mBgMusic.release();
            mBgMusic = null;
        }
        
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
