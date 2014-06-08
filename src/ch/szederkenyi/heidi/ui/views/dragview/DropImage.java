package ch.szederkenyi.heidi.ui.views.dragview;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class DropImage extends ImageView implements IDragSource, IDropTarget {
    
    private static final int LEVEL_NORMAL = 0;
    private static final int LEVEL_DRAGGED = 1;
    
    private DragController mController;
    private OnViewDropListener mViewDropListener;

    public DropImage(Context context) {
        super(context);
    }

    public DropImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DropImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public DragController getDragController() {
        return mController;
    }

    @Override
    public void setDragController(DragController controller) {
        if(null != mController) {
            mController.removeDropTarget(this);
        }
        
        mController = controller;
        
        if(null != mController) {
            mController.addDropTarget(this);
        }
    }
    
    @Override
    public void onDropCompleted(View target, boolean success) {
        // TODO Auto-generated method stub
    }

    public OnViewDropListener getOnViewDropListener() {
        return mViewDropListener;
    }

    public void setOnViewDropListener(OnViewDropListener viewDropListener) {
        mViewDropListener = viewDropListener;
    }

    @Override
    public void onDrop(IDragSource source, int x, int y, int xOffset, int yOffset,
            DragView dragView, Object dragInfo) {
        if(null != mViewDropListener) {
            mViewDropListener.onViewDrop(((View)dragInfo));
        }
    }

    @Override
    public void onDragEnter(IDragSource source, int x, int y, int xOffset, int yOffset,
            DragView dragView, Object dragInfo) {
        setImageLevel(LEVEL_DRAGGED);
    }

    @Override
    public void onDragOver(IDragSource source, int x, int y, int xOffset, int yOffset,
            DragView dragView, Object dragInfo) { }

    @Override
    public void onDragExit(IDragSource source, int x, int y, int xOffset, int yOffset,
            DragView dragView, Object dragInfo) {
        setImageLevel(LEVEL_NORMAL);
    }

    @Override
    public boolean acceptDrop(IDragSource source, int x, int y, int xOffset, int yOffset,
            DragView dragView, Object dragInfo) {
        return isEnabled();
    }

    @Override
    public Rect estimateDropLocation(IDragSource source, int x, int y, int xOffset, int yOffset,
            DragView dragView, Object dragInfo, Rect recycle) {
        return null;
    }
    
    public interface OnViewDropListener {
        void onViewDrop(View view);
    }

}
