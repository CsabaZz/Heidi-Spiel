package ch.szederkenyi.heidi.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import ch.szederkenyi.heidi.AppData;
import ch.szederkenyi.heidi.data.ImageLoader;
import ch.szederkenyi.heidi.data.entities.Story;
import ch.szederkenyi.heidi.messages.MessageHandler;
import ch.szederkenyi.heidi.messages.NextStoryMessage;

import com.example.heidi.R;

public class StoryFragment extends BaseFragment implements OnClickListener {
    private static final String KEY_ENTITY_OBJECT = "StoryFragment::EntityObject";
    
    private TextView mBubbleText;
    
    private Story mStoryObject;
    
    public static StoryFragment instantiate(Story storyObject) {
        final Bundle args = new Bundle();
        args.putSerializable(KEY_ENTITY_OBJECT, storyObject);
        
        final StoryFragment fragment = new StoryFragment();
        fragment.setArguments(args);
        
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        final Bundle args = getArguments();
        if(null != args) {
            mStoryObject = (Story) args.getSerializable(KEY_ENTITY_OBJECT);
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View contentView = inflater.inflate(R.layout.story, container, false);
        contentView.setOnClickListener(this);
        
        mBubbleText = (TextView) contentView.findViewById(R.id.story_bubble);
        mBubbleText.setText(mStoryObject.text);
        
        ImageLoader.loadBackgroundFromAsset(contentView, mStoryObject.background);
        
        return contentView;
    }
    
    @Override
    public void onDestroyView() {
        final View contentView = getView();
        if(null != contentView) {
            contentView.setOnClickListener(null);
        }
        
        mBubbleText = null;
        
        super.onDestroyView();
    }
    
    @Override
    public String getStackName() {
        if(null == mStoryObject) {
            return null;
        } else {
            return super.getStackName() + "://" + mStoryObject.id;
        }
    }

    @Override
    public void onClick(View v) {
        final AppData appdata = AppData.getInstance();
        final MessageHandler handler = appdata.getMessageHandler();
        handler.sendMessage(NextStoryMessage.class);
    }
}
