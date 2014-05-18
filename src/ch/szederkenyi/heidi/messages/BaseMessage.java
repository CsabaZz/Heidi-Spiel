package ch.szederkenyi.heidi.messages;

public class BaseMessage {
    public Object mSender;
    public Object mTarget;
    
    public BaseMessage() { }
    
    public BaseMessage(Object sender) {
        mSender = sender;
    }
    
    public BaseMessage(Object sender, Object target) {
        mSender = sender;
        mTarget = target;
    }
    
    public Object getSender() {
        return mSender;
    }
    
    public Object getTarget() {
        return mTarget;
    }
}
