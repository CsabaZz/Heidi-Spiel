package ch.szederkenyi.heidi.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ch.szederkenyi.heidi.data.entities.Story;

import com.example.heidi.R;

public class StoryFragment extends BaseFragment {
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
        
        mBubbleText = (TextView) contentView.findViewById(R.id.story_bubble);
        mBubbleText.setText(mStoryObject.text);
        
        return contentView;
    }
    
    @Override
    public void onDestroyView() {
        mBubbleText = null;
        super.onDestroyView();
    }
}
