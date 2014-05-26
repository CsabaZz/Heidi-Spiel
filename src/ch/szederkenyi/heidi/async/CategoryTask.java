package ch.szederkenyi.heidi.async;

import android.os.Handler;

import ch.szederkenyi.heidi.async.callable.CategoryCallable;
import ch.szederkenyi.heidi.data.entities.Category;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class CategoryTask extends AbstractListTask<Category> {

    public CategoryTask(Handler handler, int message) {
        super(handler, message);
    }

    @Override
    protected Callable<ArrayList<Category>> getCallable(
            AbstractTask.Entity entity) {
        return new CategoryCallable(entity);
    }

}
