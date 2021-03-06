package ch.szederkenyi.heidi.async.callable;

import ch.szederkenyi.heidi.async.AbstractTask.Entity;
import ch.szederkenyi.heidi.data.entities.BaseEntity;
import ch.szederkenyi.heidi.data.entities.DragGameEntity;
import ch.szederkenyi.heidi.data.entities.Help;
import ch.szederkenyi.heidi.data.entities.PicsSelectGameEntity;
import ch.szederkenyi.heidi.data.entities.Question;
import ch.szederkenyi.heidi.data.entities.Ready;
import ch.szederkenyi.heidi.data.entities.Story;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class StoryboardCallable extends AbstractListCallable<BaseEntity> {

    public StoryboardCallable(Entity entity) {
        super(entity);
    }

    @Override
    protected ArrayList<BaseEntity> processContent(String content) {
        final JSONArray array = convertToArray(content);
        final ArrayList<BaseEntity> entities = new ArrayList<BaseEntity>();
        
        int length = null == array ? 0 : array.length();
        for(int i = 0; i < length; ++i) {
            final JSONObject object = array.optJSONObject(i);
            final String type = object.optString("type");
            
            if("story".equalsIgnoreCase(type)) {
                final Story storyEntity = new Story();
                storyEntity.type = type;
                
                storyEntity.text = object.optString("text1");
                storyEntity.background = object.optString("background");
                
                entities.add(storyEntity);
            } else if("question".equalsIgnoreCase(type)) {
                final Question questionEntity = new Question();
                questionEntity.type = type;
                
                questionEntity.questionText = object.optString("questionText");
                
                questionEntity.answer1 = object.optString("answer1");
                questionEntity.answer2 = object.optString("answer2");
                questionEntity.answer3 = object.optString("answer3");
                
                questionEntity.goodAnswer = object.optString("good");

                questionEntity.questionImage = object.optString("questionImage");
                questionEntity.placeholder = object.optString("placeholderImage");
                
                questionEntity.goodImage = object.optString("goodImage");
                questionEntity.badImage = object.optString("badImage");
                
                entities.add(questionEntity);
            } else if("ready".equalsIgnoreCase(type)) {
                final Ready imageEntity = new Ready();
                imageEntity.type = type;
                
                imageEntity.text = object.optString("text");
                imageEntity.image = object.optString("image");
                
                entities.add(imageEntity);
            } else if("help".equalsIgnoreCase(type)) {
                final Help helpEntity = new Help();
                helpEntity.type = type;
                
                helpEntity.text1 = object.optString("text1");
                helpEntity.text2 = object.optString("text2");
                helpEntity.background = object.optString("background");
                
                entities.add(helpEntity);
            } else if("pics_select_game".equalsIgnoreCase(type)) {
                final PicsSelectGameEntity gameEntity = new PicsSelectGameEntity();
                gameEntity.type = type;
                
                gameEntity.folder = object.optString("folder");
                
                entities.add(gameEntity);
            } else if("drag_game".equalsIgnoreCase(type)) {
                final DragGameEntity gameEntity = new DragGameEntity();
                gameEntity.type = type;
                
                gameEntity.folder = object.optString("folder");
                
                entities.add(gameEntity);
            }
        }
        
        return entities;
    }

}
