package ch.szederkenyi.heidi.async.callable;

import ch.szederkenyi.heidi.async.AbstractTask.Entity;
import ch.szederkenyi.heidi.data.entities.Category;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class CategoryCallable extends AbstractListCallable<Category> {

    public CategoryCallable(Entity entity) {
        super(entity);
    }

    @Override
    protected ArrayList<Category> processContent(String content) {
        final JSONArray array = convertToArray(content);
        final ArrayList<Category> categories = new ArrayList<Category>();
        
        int length = null == array ? 0 : array.length();
        for(int i = 0; i < length; ++i) {
            final JSONObject object = array.optJSONObject(i);
            final Category categoryEntity = new Category();
            categoryEntity.title = object.optString("title");
            categoryEntity.datafile = object.optString("datafile");
            
            categories.add(categoryEntity);
        }
        
        return categories;
    }

}
