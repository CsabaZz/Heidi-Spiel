package ch.szederkenyi.heidi.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import ch.szederkenyi.heidi.data.entities.BaseEntity;
import ch.szederkenyi.heidi.data.entities.Help;
import ch.szederkenyi.heidi.data.entities.Question;
import ch.szederkenyi.heidi.data.entities.Ready;
import ch.szederkenyi.heidi.data.entities.Story;
import ch.szederkenyi.heidi.ui.fragments.HelpFragment;
import ch.szederkenyi.heidi.ui.fragments.QuestionFragment;
import ch.szederkenyi.heidi.ui.fragments.ReadyFragment;
import ch.szederkenyi.heidi.ui.fragments.StoryFragment;

import java.util.ArrayList;
import java.util.Collection;


public class StoryboardAdapter extends FragmentHashStatePagerAdapter {
    
    private ArrayList<BaseEntity> mEntities;

    public StoryboardAdapter(FragmentManager fm) {
        super(fm);
        mEntities = new ArrayList<BaseEntity>();
    }

    @Override
    public int getCount() {
        return mEntities.size();
    }

    @Override
    public Fragment getItem(int position) {
        final BaseEntity entity = mEntities.get(position);
        if(entity instanceof Story) {
            return StoryFragment.instantiate((Story)entity);
        } else if(entity instanceof Question) {
            return QuestionFragment.instantiate((Question)entity);
        } else if(entity instanceof Ready) {
            return ReadyFragment.instantiate((Ready)entity);
        } else if(entity instanceof Help) {
            return HelpFragment.instantiate((Help)entity);
        }
        
        return null;
    }
    
    public void addAll(Collection<? extends BaseEntity> collection) {
        mEntities.clear();
        mEntities.addAll(collection);
    }
    
    public boolean isEmpty() {
        return mEntities.isEmpty();
    }

}
