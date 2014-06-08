package ch.szederkenyi.heidi.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;

import ch.szederkenyi.heidi.data.entities.BaseEntity;
import ch.szederkenyi.heidi.data.entities.DragGameEntity;
import ch.szederkenyi.heidi.data.entities.Help;
import ch.szederkenyi.heidi.data.entities.PicsSelectGameEntity;
import ch.szederkenyi.heidi.data.entities.Question;
import ch.szederkenyi.heidi.data.entities.Ready;
import ch.szederkenyi.heidi.data.entities.Story;
import ch.szederkenyi.heidi.ui.IResetable;
import ch.szederkenyi.heidi.ui.fragments.DragGameFragment;
import ch.szederkenyi.heidi.ui.fragments.HelpFragment;
import ch.szederkenyi.heidi.ui.fragments.PicsSelectGameFragment;
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
    public Object instantiateItem(ViewGroup container, int position) {
        final Object o = super.instantiateItem(container, position);
        
        if(o instanceof IResetable) {
            ((IResetable)o).resetInterface();
        }
        
        return o;
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
        } else if(entity instanceof PicsSelectGameEntity) {
            return PicsSelectGameFragment.instantiate((PicsSelectGameEntity)entity);
        } else if(entity instanceof DragGameEntity) {
            return DragGameFragment.instantiate((DragGameEntity)entity);
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
    
    public Object getEntity(int position) {
        return mEntities.get(position);
    }

}
