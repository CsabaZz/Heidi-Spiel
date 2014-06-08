package ch.szederkenyi.heidi.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ch.szederkenyi.heidi.AppData;
import ch.szederkenyi.heidi.R;
import ch.szederkenyi.heidi.StaticContextApplication;
import ch.szederkenyi.heidi.data.ImageLoader;
import ch.szederkenyi.heidi.data.entities.BaseEntity;
import ch.szederkenyi.heidi.data.entities.DragGameEntity;
import ch.szederkenyi.heidi.messages.FirstQuestionMessage;
import ch.szederkenyi.heidi.messages.MessageHandler;
import ch.szederkenyi.heidi.messages.NextStoryMessage;
import ch.szederkenyi.heidi.ui.AnimationViewListener;
import ch.szederkenyi.heidi.ui.views.dragview.DragController;
import ch.szederkenyi.heidi.ui.views.dragview.DragController.DragListener;
import ch.szederkenyi.heidi.ui.views.dragview.DragLayer;
import ch.szederkenyi.heidi.ui.views.dragview.DropImage;
import ch.szederkenyi.heidi.ui.views.dragview.DropImage.OnViewDropListener;
import ch.szederkenyi.heidi.ui.views.dragview.IDragSource;
import ch.szederkenyi.heidi.utils.ConstantUtils;
import ch.szederkenyi.heidi.utils.Utils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;

public class DragGameFragment extends BaseFragment implements Callback, AnimationListener, OnViewDropListener {
    private static final String TAG = Utils.makeTag(DragGameFragment.class);
    
    private static final String KEY_ENTITY_OBJECT = "DragGameFragment::EntityObject";
    private static final String KEY_RESULTS_OBJECT = "DragGameFragment::ResultsObject";

    private DragController mDragController;
    
    private DragLayer mZutatenLayer;
    private DropImage mPanImage;
    private ImageView mZutatenImage;
    
    private TextView mPointsText;
    
    private DragTouchListener mDragTouchListener;
    private DragListenerForStateSaving mDragListenerForStateSaving;
    
    private DragGameEntity mGameObject;
    private Results mResults;
    
    private Handler mGameHandler;
    
    private ArrayList<String> mImageList;

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
        mGameHandler = new Handler(this);
        
        mDragTouchListener = new DragTouchListener();
        mDragListenerForStateSaving = new DragListenerForStateSaving();
        
        if(null == savedInstanceState) {
            mResults = new Results();
        } else {
            mResults = (Results) savedInstanceState.getSerializable(KEY_RESULTS_OBJECT);
        }
        
        final Bundle args = getArguments();
        if(null != args) {
            mGameObject = (DragGameEntity) args.getSerializable(KEY_ENTITY_OBJECT);
            
            mImageList = new ArrayList<String>();
            
            final Activity activity = StaticContextApplication.getCurrentActivity();
            final AssetManager manager = activity.getAssets();
            
            try {
                final String[] folders = manager.list(mGameObject.folder);
                for(String folder : folders) {
                    final String subfolder = mGameObject.folder + "/" + folder;
                    
                    try {
                        final String[] files = manager.list(subfolder);
                        for(String file : files) {
                            mImageList.add(subfolder + "/" + file);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                
                Collections.shuffle(mImageList);
            } catch (IOException ex) {
                Log.e(TAG, "Can not read the Assets folder", ex);
            }
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View contentView = inflater.inflate(R.layout.drag_game, container, false);
        
        mDragController = new DragController(inflater.getContext());
        mDragController.setDragListener(mDragListenerForStateSaving);
        
        mZutatenLayer = (DragLayer) contentView.findViewById(R.id.drag_game_zutaten_layout);
        mPanImage = (DropImage) contentView.findViewById(R.id.drag_game_pan);
        mZutatenImage = (ImageView) contentView.findViewById(R.id.drag_game_zutaten);
        
        mPointsText = (TextView) contentView.findViewById(R.id.drag_game_points_text);
        
        mPointsText.setText(String.format(getString(R.string.picSelectPointsText), mResults.points));
        
        mZutatenLayer.setDragController(mDragController);
        mDragController.addDropTarget(mZutatenLayer);
        
        mPanImage.setDragController(mDragController);
        mPanImage.setOnViewDropListener(this);
        
        mZutatenImage.setOnTouchListener(mDragTouchListener);
        
        return contentView;
    }
    
    @Override
    public void onDestroyView() {
        if(null != mDragController) {
            mDragController.removeDropTarget(mZutatenLayer);
            mDragController.removeDragListener(mDragListenerForStateSaving);
        }
        
        super.onDestroyView();
    }
    
    @Override
    public void onStart() {
        super.onStart();
        
        final Message removeMsg = Message.obtain(mGameHandler, ConstantUtils.MSG_ADD_IMAGE);
        mGameHandler.sendMessage(removeMsg);
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_RESULTS_OBJECT, mResults);
    }
    
    @Override
    public String getStackName() {
        if(null == mGameObject) {
            return null;
        } else {
            return super.getStackName() + "://" + mGameObject.id;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        if(null == mDragController) {
            return false;
        }
        
        if(msg.what == ConstantUtils.MSG_ADD_IMAGE) {
            if(mImageList.isEmpty()) {
                if(mResults.points == 0) {
                    showFailedDialog();
                } else {
                    showSuccessDialog();
                }
                
                return true;
            }
            
            final AnimationViewListener animationListener = new AnimationViewListener(this);
            animationListener.setView(mZutatenImage);
            
            final Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.push_right_to_left);
            animation.setAnimationListener(animationListener);
            animation.setDuration(12000);
            animation.setFillBefore(true);
            animation.setFillAfter(true);
            
            final String imagename = mImageList.get(0);
            mImageList.remove(0);
            
            mZutatenImage.setTag(R.id.imageNameTag, imagename);
            
            ImageLoader.loadImageFromAsset(mZutatenImage, imagename);
            
            mZutatenImage.startAnimation(animation);
            
            return true;
        } else if(msg.what == ConstantUtils.MSG_REMOVE_IMAGE) {
            final Message removeMsg = Message.obtain(mGameHandler, ConstantUtils.MSG_ADD_IMAGE);
            mGameHandler.sendMessage(removeMsg);
            
            return true;
        }
        
        return false;
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

    @Override
    public void onViewDrop(View view) {
        final String imageName = (String) view.getTag(R.id.imageNameTag);
        
        if(imageName.contains("/right/")) {
            Toast.makeText(getActivity(), "RIGHT", Toast.LENGTH_SHORT).show();
            
            mResults.points += 1;
            mPointsText.setText(String.format(getString(R.string.picSelectPointsText), mResults.points));
        } else {
            mResults.finished = true;
            
            Toast.makeText(getActivity(), "WRONG", Toast.LENGTH_SHORT).show();
            
            showFailedDialog();
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if(mResults.finished) {
            return;
        }
        
        final AnimationViewListener animationListener = (AnimationViewListener) getAnimationListener(animation);
        final View cellView = animationListener.getView();
        
        final Message removeMsg = Message.obtain(mGameHandler, ConstantUtils.MSG_REMOVE_IMAGE, cellView);
        mGameHandler.sendMessage(removeMsg);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        // TODO Auto-generated method stub
        
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

    private class DragTouchListener implements OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (!v.isInTouchMode() 
                    || event.getAction() != MotionEvent.ACTION_DOWN) {
                return false;
            } else {
                v.getAnimation().setAnimationListener(null);
                v.clearAnimation();
                
                Object dragInfo = v;
                mDragController.startDrag(v, mZutatenLayer, dragInfo,
                        DragController.DRAG_ACTION_MOVE);
                return true;
            }
        }
    }
    
    private class DragListenerForStateSaving implements DragListener {

        @Override
        public void onDragStart(IDragSource source, Object info, int dragAction) {
            // Unused auto-generated method stub
        }

        @Override
        public void onDragEnd(IDragSource source, Object info) {
            //final View v = (View) info;
            final Message removeMsg = Message.obtain(mGameHandler, ConstantUtils.MSG_ADD_IMAGE);
            mGameHandler.sendMessage(removeMsg);
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