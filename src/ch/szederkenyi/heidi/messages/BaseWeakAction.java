package ch.szederkenyi.heidi.messages;

import java.lang.ref.WeakReference;

public abstract class BaseWeakAction<A> implements IWeakAction {
    
    private WeakReference<Object> mRecipientReference;
    private A mAction;
    
    public BaseWeakAction(Object recipient, A action) {
        mRecipientReference = new WeakReference<Object>(recipient);
        mAction = action;
    }

    @Override
    public Object getRecipient() {
        return null == mRecipientReference ? null : mRecipientReference.get();
    }
    
    public A getAction() {
        return mAction;
    }

    @Override
    public boolean isAlive() {
        return null != mRecipientReference && null != mRecipientReference.get();
    }

    @Override
    public void PrepareToBeCollected(Object source) {
        if(null != mRecipientReference && mRecipientReference.get() == source) {
            mRecipientReference.clear();
            mRecipientReference = null;
        }
    }

}
