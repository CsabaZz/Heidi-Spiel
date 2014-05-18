package ch.szederkenyi.heidi.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ch.szederkenyi.heidi.AppData;
import ch.szederkenyi.heidi.data.ImageLoader;
import ch.szederkenyi.heidi.data.entities.Help;
import ch.szederkenyi.heidi.messages.MessageHandler;
import ch.szederkenyi.heidi.messages.NextStoryMessage;
import ch.szederkenyi.heidi.ui.views.LockableScrollView;

import com.example.heidi.R;

public class HelpFragment extends BaseFragment implements OnClickListener {
    private static final String KEY_ENTITY_OBJECT = "StoryFragment::EntityObject";
    
    private LockableScrollView mScrollView;
    
    private TextView mText1View;
    private TextView mText2View;
    private ImageView mImageView;
    
    private Help mHelpObject;
    
    public static HelpFragment instantiate(Help helpObject) {
        final Bundle args = new Bundle();
        args.putSerializable(KEY_ENTITY_OBJECT, helpObject);
        
        final HelpFragment fragment = new HelpFragment();
        fragment.setArguments(args);
        
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        final Bundle args = getArguments();
        if(null != args) {
            mHelpObject = (Help) args.getSerializable(KEY_ENTITY_OBJECT);
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View contentView = inflater.inflate(R.layout.help, container, false);
        
        mScrollView = (LockableScrollView) contentView;
        mScrollView.setEnableScrolling(false);
        mScrollView.getChildAt(0).setOnClickListener(this);
        
        mText1View = (TextView) contentView.findViewById(R.id.help_text1);
        mText1View.setText(mHelpObject.text1);
        
        mText2View = (TextView) contentView.findViewById(R.id.help_text2);
        mText2View.setText(mHelpObject.text2);

        mImageView = (ImageView) contentView.findViewById(R.id.help_image);
        ImageLoader.loadImageFromAsset(mImageView, mHelpObject.background);
        
        mText1View.setVisibility(View.VISIBLE);
        mText2View.setVisibility(View.GONE);
        
        return contentView;
    }
    
    @Override
    public void onDestroyView() {
        final View contentView = getView();
        if(null != contentView) {
            contentView.setOnClickListener(null);
        }

        mImageView = null;
        
        mText2View = null;
        mText1View = null;
        
        mScrollView = null;
        
        super.onDestroyView();
    }
    
    @Override
    public String getStackName() {
        if(null == mHelpObject) {
            return null;
        } else {
            return super.getStackName() + "://" + mHelpObject.id;
        }
    }

    @Override
    public void onClick(View v) {
        if(mText1View.getVisibility() == View.VISIBLE) {
            mText1View.setVisibility(View.GONE);
            mText1View.post(new Runnable() {
                
                @Override
                public void run() {
                    mScrollView.smoothScrollTo(0, mScrollView.getChildAt(0).getMeasuredWidth());
                    mScrollView.post(new Runnable() {
                        
                        @Override
                        public void run() {
                            mText2View.setVisibility(View.VISIBLE);
                        }
                    });
                }
            });
        } else {
            final AppData appdata = AppData.getInstance();
            final MessageHandler handler = appdata.getMessageHandler();
            handler.sendMessage(NextStoryMessage.class);
        }
    }
}
