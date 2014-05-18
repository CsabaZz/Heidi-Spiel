package ch.szederkenyi.heidi.async;

import android.os.Handler;

import ch.szederkenyi.heidi.data.entities.BaseEntity;

import java.util.ArrayList;

public abstract class AbstractListTask<E extends BaseEntity> extends AbstractTask<ArrayList<E>> {

    public AbstractListTask(Handler handler, int message) {
        super(handler, message);
    }

}
