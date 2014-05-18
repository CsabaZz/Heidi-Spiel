package ch.szederkenyi.heidi;

import ch.szederkenyi.heidi.messages.MessageHandler;

public class AppData {
    private static final Object LOCK_OBJ = new Object();
    
    private MessageHandler mMessageHandler;
    
    private static AppData sInstance;
    public static AppData getInstance() {
        if(null == sInstance) {
            synchronized (LOCK_OBJ) {
                if(null == sInstance) {
                    sInstance = new AppData();
                }
            }
        }
        
        return sInstance;
    }
    
    private AppData() {
        mMessageHandler = new MessageHandler();
    }
    
    public MessageHandler getMessageHandler() {
        return mMessageHandler;
    }
}
