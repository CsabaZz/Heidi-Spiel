package ch.szederkenyi.heidi.async.callable;

import android.util.Log;

import ch.szederkenyi.heidi.async.AbstractTask;
import ch.szederkenyi.heidi.data.entities.BaseEntity;
import ch.szederkenyi.heidi.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public abstract class AbstractListCallable<E extends BaseEntity> extends AbstractCallable<ArrayList<E>> {

    protected ArrayList<E> mResults;
    
    public AbstractListCallable(AbstractTask.Entity entity) {
        super(entity);
        mResults = new ArrayList<E>();
    }

    protected JSONArray convertToArray(String content) {
        try {
            return new JSONArray(content);
        } catch(JSONException ex) {
            Log.e(Utils.makeTag(getClass()), "Can not parse the data file!", ex);
        }
        
        return null;
    }
}
