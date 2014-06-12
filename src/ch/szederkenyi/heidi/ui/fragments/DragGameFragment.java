package ch.szederkenyi.heidi.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ch.szederkenyi.heidi.AppData;
import ch.szederkenyi.heidi.R;
import ch.szederkenyi.heidi.StaticContextApplication;
import ch.szederkenyi.heidi.data.entities.BaseEntity;
import ch.szederkenyi.heidi.data.entities.DragGameEntity;
import ch.szederkenyi.heidi.media.ImageManager;
import ch.szederkenyi.heidi.messages.FirstQuestionMessage;
import ch.szederkenyi.heidi.messages.MessageHandler;
import ch.szederkenyi.heidi.messages.NextStoryMessage;
import ch.szederkenyi.heidi.ui.AnimationViewListener;
import ch.szederkenyi.heidi.utils.ConstantUtils;
import ch.szederkenyi.heidi.utils.Utils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;

public class DragGameFragment extends BaseFragment {
    private static final String TAG = Utils.makeTag(DragGameFragment.class);
    
    private static final int SPEED = 8000;
    
    private static final String KEY_ENTITY_OBJECT = "DragGameFragment::EntityObject";

    private ViewGroup mZutatenLayer;
    private ImageView mPanImage;
    
    private TextView mPointsText;
    
    private DragGameEntity mGameObject;
    private Results mResults;
    
    private IngredientHandler mIngredientHandler;

    public static Fragment instantiate(DragGameEntity gameObject) {
        final Bundle args = new Bundle();
        args.putSerializable(KEY_ENTITY_OBJECT, gameObject);
        
        final DragGameFragment fragment = new DragGameFragment();
        fragment.setArguments(args);
        
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mResults = new Results();
        
        final Bundle args = getArguments();
        if(null != args) {
            mGameObject = (DragGameEntity) args.getSerializable(KEY_ENTITY_OBJECT);
            
            mIngredientHandler = new IngredientHandler();
            
            final Activity activity = StaticContextApplication.getCurrentActivity();
            final AssetManager manager = activity.getAssets();
            
            try {
                final String[] folders = manager.list(mGameObject.folder);
                for(String folder : folders) {
                    final String subfolder = mGameObject.folder + "/" + folder;
                    
                    try {
                        final String[] files = manager.list(subfolder);
                        for(String file : files) {
                            mIngredientHandler.addIngredient(subfolder + "/" + file);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                
                mIngredientHandler.shuffle();
            } catch (IOException ex) {
                Log.e(TAG, "Can not read the Assets folder", ex);
            }
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View contentView = inflater.inflate(R.layout.drag_game, container, false);
        
        mZutatenLayer = (ViewGroup) contentView.findViewById(R.id.drag_game_zutaten_layout);
        mPanImage = (ImageView) contentView.findViewById(R.id.drag_game_pan);
        
        mPointsText = (TextView) contentView.findViewById(R.id.drag_game_points_text);
        
        mPointsText.setText(String.format(getString(R.string.picSelectPointsText), mResults.points));
        
        return contentView;
    }
    
    @Override
    public void onStart() {
        super.onStart();
        
        mIngredientHandler.start();
    }
    
    @Override
    public String getStackName() {
        if(null == mGameObject) {
            return null;
        } else {
            return super.getStackName() + "://" + mGameObject.id;
        }
    }
    
    private void showSuccessDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(R.string.dialogPicSelectionSuccessTitle);
        dialog.setMessage(R.string.dialogPicSelectionSuccessText);
        dialog.setPositiveButton(R.string.dialogPicSelectionSuccessButton, new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final AppData appdata = AppData.getInstance();
                final MessageHandler handler = appdata.getMessageHandler();
                handler.sendMessage(NextStoryMessage.class);
            }
        });
        dialog.show();
    }
    
    private void showFailedDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(R.string.dialogBadAnswerTitle);
        dialog.setMessage(R.string.dialogBadAnswerText);
        dialog.setPositiveButton(R.string.dialogBadAnswerButton, new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mResults.points = 0;
                mPointsText.setText(String.format(getString(R.string.picSelectPointsText), mResults.points));
                
                final AppData appdata = AppData.getInstance();
                final MessageHandler handler = appdata.getMessageHandler();
                handler.sendMessage(FirstQuestionMessage.class);
            }
        });
        dialog.show();
    }
    
    private class IngredientHandler {
        private ArrayList<Ingredient> mIngredientList;
        
        public IngredientHandler() {
            mIngredientList = new ArrayList<Ingredient>();
        }

        public void addIngredient(String file) {
            mIngredientList.add(new Ingredient(file));
        }
        
        public void shuffle() {
            Collections.shuffle(mIngredientList);
        }

        public void start() {
            int size = mIngredientList.size();
            for(int i = 0; i < size; ++i) {
                final Ingredient entity = mIngredientList.get(i);
                entity.inflate(mZutatenLayer);
                entity.start(i);
            }
        }
    }
    
    private class Ingredient implements Callback, View.OnClickListener {
        private final String mFile;
        
        private Handler mGameHandler;
        
        private int mIndex;
        
        private ViewGroup mParentLayout;
        private View mCellView;
        
        private ScrollAnimationListener mScrollAnimationListener;
        private SelectAnimationListener mSelectAnimationListener;
        
        public Ingredient(String file) {
            mFile = file;
            
            mGameHandler = new Handler(this);
            
            mScrollAnimationListener = new ScrollAnimationListener();
            mSelectAnimationListener = new SelectAnimationListener();
        }

        public void inflate(ViewGroup parentLayout) {
            mParentLayout = parentLayout;
            
            final Activity activity = (Activity) mParentLayout.getContext();
            final LayoutInflater inflater = activity.getLayoutInflater();
            
            mCellView = inflater.inflate(R.layout.drag_game_item, mParentLayout, false);

            final ImageView zutatenView = (ImageView) mCellView.findViewById(R.id.drag_game_item_zutaten);
            zutatenView.setOnClickListener(this);
            ImageManager.loadImageFromAsset(zutatenView, mFile);
        }
        
        private boolean first;
        
        public void start(int index) {
            mIndex = index;
            
            first = true;
            sendAddMessage();
            first = false;
        }
        
        private void sendAddMessage() {
            int additional = first ? 0 : (mIngredientHandler.mIngredientList.size() - 1) * SPEED;
            
            final Message addMsg = Message.obtain(mGameHandler, ConstantUtils.MSG_ADD_IMAGE);
            mGameHandler.sendMessageDelayed(addMsg, (mIndex * SPEED) + additional);
        }
        
        private void sendRemoveMessage() {
            final Message removeMsg = Message.obtain(mGameHandler, ConstantUtils.MSG_REMOVE_IMAGE);
            mGameHandler.sendMessage(removeMsg);
        }

        @Override
        public boolean handleMessage(Message msg) {
            if(null == getActivity()) {
                return false;
            }
            
            if(msg.what == ConstantUtils.MSG_ADD_IMAGE) {
                if(mResults.finished) {
                    return true;
                }
                
                final AnimationViewListener animationListener = new AnimationViewListener(mScrollAnimationListener);
                animationListener.setView(mCellView);
                
                final Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.push_right_to_left);
                animation.setAnimationListener(animationListener);
                animation.setDuration(SPEED);
                animation.setFillBefore(true);
                animation.setFillAfter(true);
                
                mParentLayout.addView(mCellView);
                mCellView.startAnimation(animation);
                
                return true;
            } else if(msg.what == ConstantUtils.MSG_REMOVE_IMAGE) {
                mParentLayout.removeView(mCellView);
                
                sendAddMessage();
                
                return true;
            }
            
            return false;
        }
        
        @Override
        public void onClick(View v) {
            final View parentView = (View)v.getParent();
            final Animation anim = parentView.getAnimation();
            anim.setAnimationListener(null);
            
            parentView.clearAnimation();
            
            final Transformation transformation = getTransformation(anim);
            final float[] currentTransl = getTransformationTranslate(transformation);
            
            final int[] locationOfV = getPositionOfView(parentView);
            final int[] locationOfPan = getPositionOfView(mPanImage);
            
            final AnimationViewListener animationListener = new AnimationViewListener(mSelectAnimationListener);
            animationListener.setView(parentView);
            
            final TranslateAnimation translate = new TranslateAnimation(
                    currentTransl[0], locationOfPan[0] - locationOfV[0], 
                    currentTransl[1], locationOfPan[1] - locationOfV[1]);
            translate.setAnimationListener(animationListener);
            translate.setDuration(300);
            translate.setFillBefore(true);
            translate.setFillAfter(true);
            
            parentView.startAnimation(translate);
        }
        
        private Animation.AnimationListener getAnimationListener(Animation animation) {
            try {
                Field mListenerField = Animation.class
                        .getDeclaredField("mListener");
                if (null != mListenerField) {
                    mListenerField.setAccessible(true);
                    return (Animation.AnimationListener) mListenerField.get(animation);
                }
            } catch (NoSuchFieldException ex) {
                Log.e(TAG, "Can not find such field: mListener", ex);
            } catch (IllegalAccessException ex) {
                Log.e(TAG, "Can not access to field: mListener", ex);
            } catch (IllegalArgumentException ex) {
                Log.e(TAG, "Something goes wrong with mListener", ex);
            }
            
            return null;
        }
        
        private Transformation getTransformation(Animation animation) {
            
            try {
                Field mListenerField = Animation.class
                        .getDeclaredField("mTransformation");
                if (null != mListenerField) {
                    mListenerField.setAccessible(true);
                    return (Transformation) mListenerField.get(animation);
                }
            } catch (NoSuchFieldException ex) {
                Log.e(TAG, "Can not find such field: mTransformation", ex);
            } catch (IllegalAccessException ex) {
                Log.e(TAG, "Can not access to field: mTransformation", ex);
            } catch (IllegalArgumentException ex) {
                Log.e(TAG, "Something goes wrong with mTransformation", ex);
            }
            
            return new Transformation();
        }
        
        private float[] getTransformationTranslate(Transformation transformation) {
            final Matrix transformationMatrix = transformation.getMatrix();
            
            final float[] matrixValues = new float[9];
            transformationMatrix.getValues(matrixValues);
            
            return new float[] { matrixValues[Matrix.MTRANS_X], matrixValues[Matrix.MTRANS_Y] };
        }
        
        private int[] getPositionOfView(View v) {
            final int[] positions = new int[2];
            v.getLocationOnScreen(positions);
            return positions;
        }
        
        private class ScrollAnimationListener implements Animation.AnimationListener {

            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationRepeat(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(mResults.finished) {
                    return;
                }
                
                sendRemoveMessage();
            }
        }
        
        private class SelectAnimationListener implements Animation.AnimationListener {

            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationRepeat(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(mResults.finished) {
                    return;
                }
                
                final AnimationViewListener animationListener = (AnimationViewListener) getAnimationListener(animation);
                final View cellView = animationListener.getView();
                
                final String imageName = mFile;
                
                if(imageName.contains("/right/")) {
                    Toast.makeText(getActivity(), "RIGHT", Toast.LENGTH_SHORT).show();
                    
                    mResults.points += 1;
                    mPointsText.setText(String.format(getString(R.string.picSelectPointsText), mResults.points));

                    int count = 0;
                    int size = mIngredientHandler.mIngredientList.size();
                    for (int i = 0; i < size; i++) {
                        if (mIngredientHandler.mIngredientList.get(i).mFile.contains("/right/")) {
                            count += 1;
                        }
                    }
                    
                    if(mResults.points == count) {
                        mResults.finished = true;
                        showSuccessDialog();
                    }
                } else {
                    mResults.finished = true;
                    
                    Toast.makeText(getActivity(), "WRONG", Toast.LENGTH_SHORT).show();
                    
                    showFailedDialog();
                }
                
                final Message removeMsg = Message.obtain(mGameHandler, ConstantUtils.MSG_REMOVE_IMAGE, cellView);
                mGameHandler.sendMessage(removeMsg);
            }
        }
    }
    
    private static class Results extends BaseEntity {

        /**
         * 
         */
        private static final long serialVersionUID = -121232782554920580L;
        
        public int points;
        public boolean finished;
    }
}
