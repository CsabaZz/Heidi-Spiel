package ch.szederkenyi.heidi.ui.fragments;

import android.app.Activity;
import android.content.res.AssetManager;
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
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;

import ch.szederkenyi.heidi.R;
import ch.szederkenyi.heidi.StaticContextApplication;
import ch.szederkenyi.heidi.data.ImageLoader;
import ch.szederkenyi.heidi.data.entities.PicsSelectGameEntity;
import ch.szederkenyi.heidi.ui.AnimationViewListener;
import ch.szederkenyi.heidi.utils.ConstantUtils;
import ch.szederkenyi.heidi.utils.Utils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class PicsSelectGameFragment extends BaseFragment implements Callback, View.OnClickListener, AnimationListener {
    private static final String TAG = Utils.makeTag(PicsSelectGameFragment.class);
    
    private static final String KEY_ENTITY_OBJECT = "PicsSelectGameFragment::EntityObject";
    
    private enum AnimationDirection {
        LEFT_2_RIGHT, RIGHT_2_LEFT
    }
    
    private Handler mGameHandler;
    private AnimationDirection mAnimationDirection;
    
    private PicsSelectGameEntity mGameObject;
    
    private ArrayList<String> mImageList;

    public static Fragment instantiate(PicsSelectGameEntity gameObject) {
        final Bundle args = new Bundle();
        args.putSerializable(KEY_ENTITY_OBJECT, gameObject);
        
        final PicsSelectGameFragment fragment = new PicsSelectGameFragment();
        fragment.setArguments(args);
        
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mGameHandler = new Handler(this);
        mAnimationDirection = AnimationDirection.LEFT_2_RIGHT;
        
        final Bundle args = getArguments();
        if(null != args) {
            mGameObject = (PicsSelectGameEntity) args.getSerializable(KEY_ENTITY_OBJECT);
            
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View contentView = inflater.inflate(R.layout.pics_select_game, container, false);
        return contentView;
    }
    
    @Override
    public void onStart() {
        super.onStart();
        
        final Message removeMsg = Message.obtain(mGameHandler, ConstantUtils.MSG_ADD_IMAGE);
        mGameHandler.sendMessage(removeMsg);
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
    public void onClick(View v) {
        final ImageButton imageButton = (ImageButton) v;
        final String imageName = (String) imageButton.getTag(R.id.imageNameTag);
        
        if(imageName.contains("/right/")) {
            Toast.makeText(getActivity(), "RIGHT", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "WRONG", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        if(msg.what == ConstantUtils.MSG_ADD_IMAGE) {
            final Activity activity = getActivity();
            final LayoutInflater inflater = activity.getLayoutInflater();
            
            final ViewGroup parentView = (ViewGroup) getView();
            final View cellView = inflater.inflate(R.layout.pics_select_game_cell, parentView, false);
            parentView.addView(cellView);
            
            final AnimationViewListener animationListener = new AnimationViewListener(this);
            animationListener.setView(cellView);
            
            final Animation animation = AnimationUtils.loadAnimation(activity, getAnimResId());
            animation.setAnimationListener(animationListener);
            
            final String imagename = mImageList.get(0);
            mImageList.remove(0);
            
            final ImageButton imageButton = (ImageButton) cellView.findViewById(R.id.pics_select_game_cell_image);
            imageButton.setOnClickListener(this);
            imageButton.setTag(R.id.imageNameTag, imagename);
            
            ImageLoader.loadImageFromAsset(imageButton, imagename);
            
            imageButton.startAnimation(animation);
            
            return true;
        } else if(msg.what == ConstantUtils.MSG_REMOVE_IMAGE) {
            final View cellView = (View) msg.obj;
            
            final ViewGroup parentView = (ViewGroup) getView();
            parentView.removeView(cellView);
            
            final Message removeMsg = Message.obtain(mGameHandler, ConstantUtils.MSG_ADD_IMAGE);
            mGameHandler.sendMessage(removeMsg);
            
            return true;
        }
        
        return false;
    }
    
    private int getAnimResId() {
        if(mAnimationDirection == AnimationDirection.LEFT_2_RIGHT) {
            mAnimationDirection = AnimationDirection.RIGHT_2_LEFT;
            return R.anim.push_left_to_right;
        } else {
            mAnimationDirection = AnimationDirection.LEFT_2_RIGHT;
            return R.anim.push_right_to_left;
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        final AnimationViewListener animationListener = (AnimationViewListener) getAnimationListener(animation);
        final View cellView = animationListener.getView();
        
        final ImageButton imageButton = (ImageButton) cellView.findViewById(R.id.pics_select_game_cell_image);
        final String imageName = (String) imageButton.getTag(R.id.imageNameTag);
        
        mImageList.add(imageName);
        
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
            Log.e(TAG, "Somethign goes wrong with mListener", ex);
        }
        
        return null;
    }
}
