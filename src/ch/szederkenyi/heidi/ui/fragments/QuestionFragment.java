package ch.szederkenyi.heidi.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import ch.szederkenyi.heidi.AppData;
import ch.szederkenyi.heidi.R;
import ch.szederkenyi.heidi.data.entities.Question;
import ch.szederkenyi.heidi.media.ImageManager;
import ch.szederkenyi.heidi.messages.FirstQuestionMessage;
import ch.szederkenyi.heidi.messages.MessageHandler;
import ch.szederkenyi.heidi.messages.NextStoryMessage;
import ch.szederkenyi.heidi.ui.IResetable;
import ch.szederkenyi.heidi.ui.views.RoundedImageView;
import ch.szederkenyi.heidi.utils.ConstantUtils;
import ch.szederkenyi.heidi.utils.Utils;

public class QuestionFragment extends BaseFragment implements OnClickListener, IResetable {
    private static final String KEY_ENTITY_OBJECT = "QuestionFragment::EntityObject";
    
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
        
        ImageManager.loadImageFromAsset(mSpaceHolderImage, mQuestionObject.placeholder);
        ImageManager.loadImageFromAsset(mQuestionImage, mQuestionObject.questionImage);
        
        initializeButton(mAnswer1Button, mQuestionObject.answer1);
        initializeButton(mAnswer2Button, mQuestionObject.answer2);
        initializeButton(mAnswer3Button, mQuestionObject.answer3);
        
        resetInterface();
        
        return contentView;
    }
    
    private void initializeButton(ToggleButton button, String answer) {
        button.setOnClickListener(this);
        button.setTag(answer);
        
        if(answer.endsWith(".jpg")) {
            final CharSequence str = ImageManager.loadImageFromAsset(answer, Utils.getDip(60));
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
    public void resetInterface() {
        if(null == getView()) {
            return;
        }
        
        mAnswer1Button.setEnabled(true);
        mAnswer2Button.setEnabled(true);
        mAnswer3Button.setEnabled(true);
        
        mAnswer1Button.setChecked(false);
        mAnswer2Button.setChecked(false);
        mAnswer3Button.setChecked(false);
        
        mResultText.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        mAnswer1Button.setEnabled(false);
        mAnswer2Button.setEnabled(false);
        mAnswer3Button.setEnabled(false);
        
        final ToggleButton button = (ToggleButton) v;
        final String answer = (String)button.getTag();
        
        final DelayCallback callback = new DelayCallback();
        callback.setResetable(this);
        
        final Handler handler = new Handler(callback);
        
        if(mQuestionObject.goodAnswer.equalsIgnoreCase(answer)) {
            ImageManager.loadImageFromAsset(mQuestionImage, mQuestionObject.goodImage);
            
            mResultText.setTextColor(Color.GREEN);
            mResultText.setText(R.string.resultRightText);
            
            handler.sendEmptyMessageDelayed(ConstantUtils.MSG_GOOD_ANSER, ConstantUtils.DELAY_GOOD_ANSWER);
        } else {
            ImageManager.loadImageFromAsset(mQuestionImage, mQuestionObject.badImage);
            
            mResultText.setTextColor(Color.RED);
            mResultText.setText(R.string.resultWrongText);
            
            mAnswer1Button.setChecked(true);
            mAnswer2Button.setChecked(true);
            mAnswer3Button.setChecked(true);
            
            final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle(R.string.dialogBadAnswerTitle);
            dialog.setMessage(R.string.dialogBadAnswerText);
            dialog.setPositiveButton(R.string.dialogBadAnswerButton, new DialogInterface.OnClickListener() {
                
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.sendEmptyMessage(ConstantUtils.MSG_BAD_ANSWER);
                }
            });
            
            final AlertDialog d = dialog.create();
            d.getWindow().setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);
            d.show();
        }
        
        mResultText.setVisibility(View.VISIBLE);
    }
    
    private static class DelayCallback implements Handler.Callback {
        
        private IResetable mResetable;
        
        public void setResetable(IResetable resetable) {
            mResetable = resetable;
        }
        
        @Override
        public boolean handleMessage(Message msg) {
            if(null != mResetable) {
                mResetable.resetInterface();
            }
            
            final AppData appdata = AppData.getInstance();
            final MessageHandler handler = appdata.getMessageHandler();
            
            if(msg.what == ConstantUtils.MSG_GOOD_ANSER) {
                handler.sendMessage(NextStoryMessage.class);
                return true;
            } else if(msg.what == ConstantUtils.MSG_BAD_ANSWER) {
                handler.sendMessage(FirstQuestionMessage.class);
                return true;
            } else {
                return false;
            }
        }
    }
}
