package ch.szederkenyi.heidi.messages;

public class GenericMessage<C> extends BaseMessage {
    
    private C mContent;
    
    @SuppressWarnings("unused")
    private GenericMessage() {
        super();
    }
    
    public GenericMessage(C content) {
        super();
        mContent = content;
    }
    
    public GenericMessage(Object sender, C content) {
        super(sender);
        mContent = content;
    }
    
    public GenericMessage(Object sender, Object target, C content) {
        super(sender, target);
        mContent = content;
    }
    
    public C getContent() {
        return mContent;
    }

}
