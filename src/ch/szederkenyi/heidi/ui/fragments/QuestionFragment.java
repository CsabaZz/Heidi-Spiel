package ch.szederkenyi.heidi.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import ch.szederkenyi.heidi.data.ImageLoader;
import ch.szederkenyi.heidi.data.entities.Question;

import com.example.heidi.R;

public class QuestionFragment extends BaseFragment {
    private static final String KEY_ENTITY_OBJECT = "QuestionFragment::EntityObject";
    
    private TextView mQuestionText;
    
    private ImageView mSpaceHolderImage;
    private ImageView mQuestionImage;
    
    private Button mAnswer1Button;
    private Button mAnswer2Button;
    private Button mAnswer3Button;
    
    private Question mQuestionObject;
    
    public static StoryFragment instantiate(Question questionObject) {
        final Bundle args = new Bundle();
        args.putSerializable(KEY_ENTITY_OBJECT, questionObject);
        
        final StoryFragment fragment = new StoryFragment();
        fragment.setArguments(args);
        
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        final Bundle args = getArguments();
        if(null != args) {
            mQuestionObject = (Question) args.getSerializable(KEY_ENTITY_OBJECT);
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View contentView = inflater.inflate(R.layout.story, container, false);
        
        mQuestionText = (TextView) contentView.findViewById(R.id.question_text);

        mSpaceHolderImage = (ImageView) contentView.findViewById(R.id.question_space_holder);
        mQuestionImage = (ImageView) contentView.findViewById(R.id.question_image);

        mAnswer1Button = (Button) contentView.findViewById(R.id.question_answer_1);
        mAnswer2Button = (Button) contentView.findViewById(R.id.question_answer_2);
        mAnswer3Button = (Button) contentView.findViewById(R.id.question_answer_3);
        
        mQuestionText.setText(mQuestionObject.question);
        
        ImageLoader.loadImageFromAsset(mSpaceHolderImage, mQuestionObject.placeholder);
        ImageLoader.loadImageFromAsset(mQuestionImage, mQuestionObject.image);
        
        mAnswer1Button.setText(mQuestionObject.answer1);
        mAnswer2Button.setText(mQuestionObject.answer2);
        mAnswer3Button.setText(mQuestionObject.answer3);
        
        return contentView;
    }
    
    @Override
    public void onDestroyView() {
        mQuestionText = null;

        mSpaceHolderImage = null;
        mQuestionImage = null;

        mAnswer1Button = null;
        mAnswer2Button = null;
        mAnswer3Button = null;
        
        super.onDestroyView();
    }
    
    @Override
    public String getStackName() {
        return super.getStackName() + "://" + mQuestionObject.id;
    }
}
