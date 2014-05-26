package ch.szederkenyi.heidi.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import ch.szederkenyi.heidi.AppData;
import ch.szederkenyi.heidi.R;
import ch.szederkenyi.heidi.data.ImageLoader;
import ch.szederkenyi.heidi.data.entities.Question;
import ch.szederkenyi.heidi.messages.MessageHandler;
import ch.szederkenyi.heidi.messages.NextStoryMessage;
import ch.szederkenyi.heidi.ui.views.RoundedImageView;
import ch.szederkenyi.heidi.utils.Utils;

public class QuestionFragment extends BaseFragment implements OnClickListener {
    private static final String KEY_ENTITY_OBJECT = "QuestionFragment::EntityObject";
    
    private static final int DELAY_GOOD_ANSWER = 2500;
    private static final int DELAY_BAD_ANSWER = 2500;
    
    private TextView mQuestionText;
    
    private ImageView mSpaceHolderImage;
    private RoundedImageView mQuestionImage;
    
    private TextView mResultText;
    
    private ToggleButton mAnswer1Button;
    private ToggleButton mAnswer2Button;
    private ToggleButton mAnswer3Button;
    
    private Question mQuestionObject;
    
    public static QuestionFragment instantiate(Question questionObject) {
        final Bundle args = new Bundle();
        args.putSerializable(KEY_ENTITY_OBJECT, questionObject);
        
        final QuestionFragment fragment = new QuestionFragment();
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
        final View contentView = inflater.inflate(R.layout.question, container, false);
        
        mQuestionText = (TextView) contentView.findViewById(R.id.question_text);

        mSpaceHolderImage = (ImageView) contentView.findViewById(R.id.question_space_holder);
        mQuestionImage = (RoundedImageView) contentView.findViewById(R.id.question_image);
        
        mResultText = (TextView) contentView.findViewById(R.id.question_result_text);

        mAnswer1Button = (ToggleButton) contentView.findViewById(R.id.question_answer_1);
        mAnswer2Button = (ToggleButton) contentView.findViewById(R.id.question_answer_2);
        mAnswer3Button = (ToggleButton) contentView.findViewById(R.id.question_answer_3);
        
        mQuestionText.setText(mQuestionObject.questionText);
        mQuestionImage.setCornerRadius(Utils.getDip(10f));
        
        ImageLoader.loadImageFromAsset(mSpaceHolderImage, mQuestionObject.placeholder);
        ImageLoader.loadImageFromAsset(mQuestionImage, mQuestionObject.questionImage, Utils.getDip(60));
        
        initializeButton(mAnswer1Button, mQuestionObject.answer1);
        initializeButton(mAnswer2Button, mQuestionObject.answer2);
        initializeButton(mAnswer3Button, mQuestionObject.answer3);
        
        mResultText.setVisibility(View.GONE);
        
        return contentView;
    }
    
    private void initializeButton(ToggleButton button, String answer) {
        button.setOnClickListener(this);
        button.setTag(answer);
        
        if(answer.endsWith(".jpg")) {
            final CharSequence str = ImageLoader.loadImageFromAsset(answer, Utils.getDip(60));
            button.setTextOn(str);
            button.setTextOff(str);
            button.setText(str);
        } else {
            button.setTextOn(answer);
            button.setTextOff(answer);
            button.setText(answer);
        }
        
        if(mQuestionObject.goodAnswer.equalsIgnoreCase(answer)) {
            button.setBackgroundResource(R.drawable.background_answer_good);
        } else {
            button.setBackgroundResource(R.drawable.background_answer_bad);
        }
    }

    @Override
    public void onDestroyView() {
        mQuestionText = null;

        mSpaceHolderImage = null;
        mQuestionImage = null;
        
        mResultText = null;

        if(null != mAnswer1Button) {
            mAnswer1Button.setOnClickListener(null);
            mAnswer1Button = null;
        }

        if(null != mAnswer2Button) {
            mAnswer2Button.setOnClickListener(null);
            mAnswer2Button = null;
        }

        if(null != mAnswer3Button) {
            mAnswer3Button.setOnClickListener(null);
            mAnswer3Button = null;
        }
        
        super.onDestroyView();
    }
    
    @Override
    public String getStackName() {
        if(null == mQuestionObject) {
            return null;
        } else {
            return super.getStackName() + "://" + mQuestionObject.id;
        }
    }

    @Override
    public void onClick(View v) {
        mAnswer1Button.setEnabled(false);
        mAnswer2Button.setEnabled(false);
        mAnswer3Button.setEnabled(false);
        
        final ToggleButton button = (ToggleButton) v;
        final String answer = (String)button.getTag();
        
        final Handler handler = new Handler(new DelayCallback());
        
        if(mQuestionObject.goodAnswer.equalsIgnoreCase(answer)) {
            ImageLoader.loadImageFromAsset(mQuestionImage, mQuestionObject.goodImage);
            
            mResultText.setTextColor(Color.GREEN);
            mResultText.setText(R.string.resultRightText);
            
            handler.sendEmptyMessageDelayed(0, DELAY_GOOD_ANSWER);
        } else {
            ImageLoader.loadImageFromAsset(mQuestionImage, mQuestionObject.badImage);
            
            mResultText.setTextColor(Color.RED);
            mResultText.setText(R.string.resultWrongText);
            
            mAnswer1Button.setChecked(true);
            mAnswer2Button.setChecked(true);
            mAnswer3Button.setChecked(true);
            
            handler.sendEmptyMessageDelayed(0, DELAY_BAD_ANSWER);
        }
        
        mResultText.setVisibility(View.VISIBLE);
    }
    
    private static class DelayCallback implements Handler.Callback {
        
        @Override
        public boolean handleMessage(Message msg) {
            final AppData appdata = AppData.getInstance();
            final MessageHandler handler = appdata.getMessageHandler();
            handler.sendMessage(NextStoryMessage.class);
            return false;
        }
    }
}
