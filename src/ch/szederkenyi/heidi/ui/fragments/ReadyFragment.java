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
import ch.szederkenyi.heidi.data.entities.Ready;
import ch.szederkenyi.heidi.messages.MessageHandler;
import ch.szederkenyi.heidi.messages.NextStoryMessage;

import com.example.heidi.R;

public class ReadyFragment extends BaseFragment implements OnClickListener {
    private static final String KEY_ENTITY_OBJECT = "StoryFragment::EntityObject";
    
    private TextView mTextView;
    private ImageView mImageView;
    
    private Ready mReadyObject;
    
    public static ReadyFragment instantiate(Ready readyObject) {
        final Bundle args = new Bundle();
        args.putSerializable(KEY_ENTITY_OBJECT, readyObject);
        
        final ReadyFragment fragment = new ReadyFragment();
        fragment.setArguments(args);
        
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        final Bundle args = getArguments();
        if(null != args) {
            mReadyObject = (Ready) args.getSerializable(KEY_ENTITY_OBJECT);
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View contentView = inflater.inflate(R.layout.ready, container, false);
        contentView.setOnClickListener(this);
        
        mTextView = (TextView) contentView.findViewById(R.id.ready_text);
        mTextView.setText(mReadyObject.text);

        mImageView = (ImageView) contentView.findViewById(R.id.ready_image);
        ImageLoader.loadImageFromAsset(mImageView, mReadyObject.image);
        
        return contentView;
    }
    
    @Override
    public void onDestroyView() {
        final View contentView = getView();
        if(null != contentView) {
            contentView.setOnClickListener(null);
        }

        mImageView = null;
        mTextView = null;
        
        super.onDestroyView();
    }
    
    @Override
    public String getStackName() {
        if(null == mReadyObject) {
            return null;
        } else {
            return super.getStackName() + "://" + mReadyObject.id;
        }
    }

    @Override
    public void onClick(View v) {
        final AppData appdata = AppData.getInstance();
        final MessageHandler handler = appdata.getMessageHandler();
        handler.sendMessage(NextStoryMessage.class);
    }
}
