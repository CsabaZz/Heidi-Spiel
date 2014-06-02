package ch.szederkenyi.heidi.ui;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public class AnimationViewListener implements AnimationListener {

	private View mView;
	private AnimationListener mListener;

	public AnimationViewListener(AnimationListener listener) {
		mListener = listener;
	}

	public View getView() {
		return mView;
	}

	public void setView(View view) {
		mView = view;
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		mListener.onAnimationEnd(animation);
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		mListener.onAnimationRepeat(animation);
	}

	@Override
	public void onAnimationStart(Animation animation) {
		mListener.onAnimationStart(animation);
	}

}
