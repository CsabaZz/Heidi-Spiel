package ch.szederkenyi.heidi.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import ch.szederkenyi.heidi.R;
import ch.szederkenyi.heidi.StaticContextApplication;
import ch.szederkenyi.heidi.async.AbstractTask;
import ch.szederkenyi.heidi.async.CategoryTask;
import ch.szederkenyi.heidi.data.entities.Category;

import java.util.Collection;
import java.util.Vector;

public class CategoryChooserActivity extends BaseActivity implements Callback, View.OnClickListener {
    
    private static final int MSG_TASK = 1;
    
    private ListView mListView;
    private CategoryListAdapter mAdapter;
    
    private Handler mTaskHandler;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_chooser);
        
        mTaskHandler = new Handler(this);
        
        mAdapter = new CategoryListAdapter();
        mAdapter.setOnClickListener(this);
        
        mListView = (ListView) findViewById(R.id.category_chooser_list);
        mListView.setAdapter(mAdapter);
    }
    
    @Override
    public void onStart() {
        super.onStart();
        
        if(mAdapter.isEmpty()) {
            final AbstractTask.Entity entity = new AbstractTask.Entity();
            entity.filename = "categories.json";
            
            final CategoryTask task = new CategoryTask(mTaskHandler, MSG_TASK);
            task.start(entity);
        }
    }
    
    @Override
    public void onDestroy() {
        if(null != mListView) {
            mListView.setOnItemClickListener(null);
            mListView = null;
        }

        if(null != mAdapter) {
            mAdapter.setOnClickListener(null);
            mAdapter = null;
        }
        
        mTaskHandler = null;
        
        super.onDestroy();
    }

    @Override
    public boolean handleMessage(Message msg) {
        if(msg.what == MSG_TASK) {
            @SuppressWarnings("unchecked")
            final Collection<? extends Category> entities = (Collection<? extends Category>) msg.obj;
            mAdapter.addAll(entities);
            mAdapter.notifyDataSetChanged();
            
            return true;
        }
        
        return false;
    }

    @Override
    public void onClick(View v) {
        final Integer positionObject = (Integer) v.getTag();
        final int position = positionObject.intValue();
        
        final Category category = (Category) mAdapter.getItem(position);
        final String dataFile = category.datafile;
        
        finish();
        
        final Intent storyIntent = new Intent(this, StoryboardActivity.class);
        storyIntent.putExtra(StoryboardActivity.EXTRA_DATAFILE, dataFile);
        startActivity(storyIntent);
    }
    
    private static class CategoryListAdapter extends BaseAdapter {
        
        private Vector<Category> mItems;
        private View.OnClickListener mClickListener;
        
        public CategoryListAdapter() {
            mItems = new Vector<Category>();
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(null == convertView) {
                final Activity activity = StaticContextApplication.getCurrentActivity();
                final LayoutInflater inflater = activity.getLayoutInflater();
                
                convertView = inflater.inflate(R.layout.category_list_item, parent, false);
                convertView.setTag(convertView.findViewById(R.id.category_list_item_button));
            }
            
            final Category category = (Category) getItem(position);
            
            final Button button = (Button) convertView.getTag();
            button.setOnClickListener(mClickListener);
            button.setText(category.title);
            button.setTag(Integer.valueOf(position));
            
            return convertView;
        }

        public void addAll(Collection<? extends Category> collection) {
            mItems.clear();
            mItems.addAll(collection);
        }

        public void setOnClickListener(View.OnClickListener clickListener) {
            mClickListener = clickListener;
        }
        
    }

}
