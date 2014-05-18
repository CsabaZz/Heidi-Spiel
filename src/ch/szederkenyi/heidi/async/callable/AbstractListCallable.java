package ch.szederkenyi.heidi.async.callable;

import ch.szederkenyi.heidi.async.AbstractTask;
import ch.szederkenyi.heidi.data.entities.BaseEntity;

import java.util.ArrayList;

public abstract class AbstractListCallable<E extends BaseEntity> extends AbstractCallable<ArrayList<E>> {

    protected ArrayList<E> mResults;
    
    public AbstractListCallable(AbstractTask.Entity entity) {
        super(entity);
        mResults = new ArrayList<E>();
    }
}
