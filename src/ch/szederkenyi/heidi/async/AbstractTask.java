package ch.szederkenyi.heidi.async;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import ch.szederkenyi.heidi.utils.Base64;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public abstract class AbstractTask<E> implements Runnable {
    protected int mMessage;
    protected Handler mHandler;
    
    private FutureTask<E> mTask;
    
    protected abstract Callable<E> getCallable(AbstractTask.Entity entity);
    
    public AbstractTask(Handler handler, int message) {
        super();
        
        this.mMessage = message;
        this.mHandler = handler;
    }
    
    public void start(AbstractTask.Entity entity) {
        final Callable<E> callable = getCallable(entity);
        mTask = new FutureTask<E>(callable);
        
        Thread t = new Thread(this);
        t.setDaemon(true);
        t.setPriority(Thread.NORM_PRIORITY - 1);
        t.start();
    }
    
    @Override
    public void run() {
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(mTask);
        
        while(!mTask.isDone());
        executor.shutdown();
        
        final Object result = get(mTask);
        sendMessage(result);
    }

    protected void sendMessage(Object result) {
        mHandler.sendMessage(Message.obtain(mHandler, mMessage, result));
    }

    protected Object get(FutureTask<E> future) {
        try {
            return future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static class Entity {
        public String url;
        public String filename;
        
        public List<BasicNameValuePair> params;
        private String authorization;
        
        public Entity() {
            params = new ArrayList<BasicNameValuePair>();
        }
        
        public String getAuthorization() {
            return authorization;
        }
        
        public boolean hasAuthorization() {
            return !TextUtils.isEmpty(authorization);
        }
        
        public void setAuthorization(String username, String password) {
            authorization = Base64.encode(username + ":" + password);
        }
    }
}
