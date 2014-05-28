package ch.szederkenyi.heidi.messages;

import android.view.View;

import ch.szederkenyi.heidi.AppData;

public class MessageSendClickListener implements View.OnClickListener {
    
    private Class<?> mMessageClass;
    
    public MessageSendClickListener(Class<?> messageClass) {
        mMessageClass = messageClass;
    }

    @Override
    public void onClick(View v) {
        final AppData appdata = AppData.getInstance();
        final MessageHandler handler = appdata.getMessageHandler();
        handler.sendMessage(mMessageClass);
    }

}
