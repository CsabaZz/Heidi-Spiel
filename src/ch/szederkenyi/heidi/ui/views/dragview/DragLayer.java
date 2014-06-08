/*
 * This is a modified version of a class from the Android Open Source Project. 
 * The original copyright and license information follows.
 * 
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.szederkenyi.heidi.ui.views.dragview;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

/**
 * A ViewGroup that coordinates dragging across its dscendants.
 * 
 * <p>
 * This class used DragLayer in the Android Launcher activity as a model. It is
 * a bit different in several respects: (1) It extends MyAbsoluteLayout rather
 * than FrameLayout; (2) it implements IDragSource and IDropTarget methods that
 * were done in a separate Workspace class in the Launcher.
 */
public class DragLayer extends DragAbsLayout implements IDragSource,
		IDropTarget {
	DragController mDragController;

	/**
	 * Used to create a new DragLayer from XML.
	 * 
	 * @param context
	 *            The application's context.
	 * @param attrs
	 *            The attribtues set containing the Workspace's customization
	 *            values.
	 */
	public DragLayer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setDragController(DragController controller) {
		mDragController = controller;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (mDragController != null) {
			return mDragController.dispatchKeyEvent(event)
					|| super.dispatchKeyEvent(event);
		} else {
			return false;
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (mDragController != null) {
			return mDragController.onInterceptTouchEvent(ev);
		} else {
			return false;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mDragController != null) {
			return mDragController.onTouchEvent(ev);
		} else {
			return false;
		}
	}

	@Override
	public boolean dispatchUnhandledMove(View focused, int direction) {
		if (mDragController != null) {
			return mDragController.dispatchUnhandledMove(focused, direction);
		} else {
			return false;
		}
	}

	/**
 */
	// IDragSource interface methods

	/**
	 * setDragController
	 * 
	 */

	/* setDragController is already defined. See above. */

	/**
	 * onDropCompleted
	 * 
	 */

	public void onDropCompleted(View target, boolean success) {
	}

	/**
 */
	// IDropTarget interface implementation

	/**
	 * Handle an object being dropped on the IDropTarget. This is the where a
	 * dragged view gets repositioned at the end of a drag.
	 * 
	 * @param source
	 *            IDragSource where the drag started
	 * @param x
	 *            X coordinate of the drop location
	 * @param y
	 *            Y coordinate of the drop location
	 * @param xOffset
	 *            Horizontal offset with the object being dragged where the
	 *            original touch happened
	 * @param yOffset
	 *            Vertical offset with the object being dragged where the
	 *            original touch happened
	 * @param dragView
	 *            The DragView that's being dragged around on screen.
	 * @param dragInfo
	 *            Data associated with the object being dragged
	 * 
	 */
	public void onDrop(IDragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		View v = (View) dragInfo;

		int w = v.getWidth();
		int h = v.getHeight();
		int left = x - xOffset;
		int top = y - yOffset;
		DragLayer.LayoutParams lp = new DragLayer.LayoutParams(w, h, left, top);
		this.updateViewLayout(v, lp);
	}

	public void onDragEnter(IDragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
	}

	public void onDragOver(IDragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
	}

	public void onDragExit(IDragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
	}

	/**
	 * Check if a drop action can occur at, or near, the requested location.
	 * This may be called repeatedly during a drag, so any calls should return
	 * quickly.
	 * 
	 * @param source
	 *            IDragSource where the drag started
	 * @param x
	 *            X coordinate of the drop location
	 * @param y
	 *            Y coordinate of the drop location
	 * @param xOffset
	 *            Horizontal offset with the object being dragged where the
	 *            original touch happened
	 * @param yOffset
	 *            Vertical offset with the object being dragged where the
	 *            original touch happened
	 * @param dragView
	 *            The DragView that's being dragged around on screen.
	 * @param dragInfo
	 *            Data associated with the object being dragged
	 * @return True if the drop will be accepted, false otherwise.
	 */
	public boolean acceptDrop(IDragSource source, int x, int y, int xOffset,
			int yOffset, DragView dragView, Object dragInfo) {
		return true;
	}

	/**
	 * Estimate the surface area where this object would land if dropped at the
	 * given location.
	 * 
	 * @param source
	 *            IDragSource where the drag started
	 * @param x
	 *            X coordinate of the drop location
	 * @param y
	 *            Y coordinate of the drop location
	 * @param xOffset
	 *            Horizontal offset with the object being dragged where the
	 *            original touch happened
	 * @param yOffset
	 *            Vertical offset with the object being dragged where the
	 *            original touch happened
	 * @param dragView
	 *            The DragView that's being dragged around on screen.
	 * @param dragInfo
	 *            Data associated with the object being dragged
	 * @param recycle
	 *            {@link Rect} object to be possibly recycled.
	 * @return Estimated area that would be occupied if object was dropped at
	 *         the given location. Should return null if no estimate is found,
	 *         or if this target doesn't provide estimations.
	 */
	public Rect estimateDropLocation(IDragSource source, int x, int y,
			int xOffset, int yOffset, DragView dragView, Object dragInfo,
			Rect recycle) {
		return null;
	}

} // end class
