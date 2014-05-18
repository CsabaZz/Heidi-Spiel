package ch.szederkenyi.heidi.async;

import android.os.Handler;

import ch.szederkenyi.heidi.async.callable.StoryboardCallable;
import ch.szederkenyi.heidi.data.entities.BaseEntity;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class StoryboardTask extends AbstractListTask<BaseEntity> {

    public StoryboardTask(Handler handler, int message) {
        super(handler, message);
    }

    @Override
    protected Callable<ArrayList<BaseEntity>> getCallable(
            AbstractTask.Entity entity) {
        return new StoryboardCallable(entity);
    }

}
