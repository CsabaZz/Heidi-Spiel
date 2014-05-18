package ch.szederkenyi.heidi.messages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MessageHandler {
    private Map<Class<?>, List<IWeakAction>> mActions;
    
    public MessageHandler() {
        mActions = new HashMap<Class<?>, List<IWeakAction>>();
    }
    
    public void register(Class<?> clazz, Object recipient, Runnable action) {
        register(clazz, new WeakAction(recipient, action));
    }
    
    private void register(Class<?> clazz, IWeakAction action) {
        final List<IWeakAction> actions = getOrCreate(clazz);
        actions.add(action);
        
        cleanUp();
    }
    
    private List<IWeakAction> getOrCreate(Class<?> clazz) {
        if(mActions.containsKey(clazz)) {
            return mActions.get(clazz);
        } else {
            final List<IWeakAction> actions = new ArrayList<IWeakAction>();
            mActions.put(clazz, actions);
            return actions;
        }
    }

    public void sendMessage(Class<?> clazz) {
        if(mActions.containsKey(clazz)) {
            final List<IWeakAction> actions = mActions.get(clazz);
            for(IWeakAction action : actions) {
                action.Execute();
            }
        }
    }
    
    public void sendMessage(Class<?> clazz, Object arg) {
        if(mActions.containsKey(clazz)) {
            final List<IWeakAction> actions = mActions.get(clazz);
            for(IWeakAction action : actions) {
                action.Execute(arg);
            }
        }
    }
    
    public void unregister(Class<?> clazz, Object source) {
        if(mActions.containsKey(clazz)) {
            final List<IWeakAction> actions = mActions.get(clazz);
            for(IWeakAction action : actions) {
                action.PrepareToBeCollected(source);
            }
        }
        
        cleanUp();
    }
    
    private void cleanUp() {
        final Set<Class<?>> keys = mActions.keySet();
        for(Class<?> key : keys) {
            final List<IWeakAction> values = mActions.get(key);
            
            int size = values.size();
            while(true) {
                size -= 1;
                
                if(size < 0) {
                    break;
                }
                
                final IWeakAction value = values.get(size);
                if(value.isAlive()) {
                    continue;
                } else {
                    values.remove(value);
                }
            }
        }
    }
}
