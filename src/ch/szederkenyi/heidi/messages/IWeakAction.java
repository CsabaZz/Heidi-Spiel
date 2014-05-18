package ch.szederkenyi.heidi.messages;

public interface IWeakAction {
    
    Object getRecipient();
    
    boolean isAlive();
    
    void Execute();
    
    void Execute(Object arg);
    
    void PrepareToBeCollected(Object source);

}
