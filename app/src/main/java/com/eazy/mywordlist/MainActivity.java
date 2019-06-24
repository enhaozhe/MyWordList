package com.eazy.mywordlist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnLongClickListener{

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PagerAdapter adapter;
    private Bundle bundle;
    private List<Word> newList;
    private List<Word> famList;
    private List<Word> knownList;
    private newFragment newFragment;
    private famFragment famFragment;
    private knownFragment knownFragment;
    private DatabaseHelper mDatabaseHelper;
    private Toolbar toolbar;
    private TextView num_item_select;
    private boolean delete_mode_status;
    private int counter;
    private List<Word> delList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabaseHelper = new DatabaseHelper(this);
        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        num_item_select = findViewById(R.id.num_selected_tv);
        num_item_select.setText("My Word List");
        delete_mode_status = false;
        counter = 0;

        newList = new ArrayList<>();
        famList = new ArrayList<>();
        knownList = new ArrayList<>();

        Cursor data = mDatabaseHelper.getData();
        while(data.moveToNext()){
            newList.add(new Word(data.getString(0), data.getString(1)));
        }

        bundle = new Bundle();
        bundle.putSerializable("getNewList", (Serializable) newList);
        newFragment = new newFragment();
        newFragment.setArguments(bundle);

        famFragment = new famFragment();

        knownFragment = new knownFragment();

        //implement tabs and pager
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        adapter = new PagerAdapter(getSupportFragmentManager(), 3, newFragment, famFragment, knownFragment);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        //set icons of tabs
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_new_word);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_fam_word);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_knwon_word);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_button, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_button:
                Log.d(LOG_TAG, "Add Button clicked!");
                Intent intent = new Intent(this, AddActivity.class);
                startActivity(intent);
               /* intent.putExtra("DatabaseHelper", (Parcelable) mDatabaseHelper);
                startActivityForResult(intent, 1);*/
                return true;
            case R.id.delete_button:
                Log.d(LOG_TAG, "Delete Button clicked!");
                return true;
            case R.id.del_mode_menu:
                delete_mode_status = false;
                toolbar.getMenu().clear();
                toolbar.inflateMenu(R.menu.add_button);
                adapter.notifyDataSetChanged();
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);

                //Todo: Remove data stored in removedList from database and related lists
                for(Word w : delList){
                    mDatabaseHelper.deleteData(w);
                    newList.remove(w);
                    famList.remove(w);
                    knownList.remove(w);
                    newFragment.preDelete();
                }
                counter = 0;
                num_item_select.setText("My Word List");
                return true;

        }
        return false;
    }


    @Override
    public boolean onLongClick(View v) {
        //Todo: disable the viewPager once enter delete mode
        delList = new ArrayList<>();
        toolbar.getMenu().clear();
        num_item_select.setText(R.string.nums_items_selected);
        toolbar.inflateMenu(R.menu.delete_mode);
        delete_mode_status = true;
        adapter.notifyDataSetChanged();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return true;
    }


    //Use the same database but store them in different arrays
    public void preDelete(View v, int position){
        int i = tabLayout.getSelectedTabPosition();
        Word word;
        switch (i){
            case 1:
                word = famList.get(position);
                break;
            case 2:
                word = knownList.get(position);
                break;
            default:
                word = newList.get(position);
                break;
        }
        if(!delList.contains(word)){
            delList.add(word);
            counter++;
            v.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
        }else{
            delList.remove(word);
            counter--;
            v.setBackgroundColor(android.R.drawable.btn_default);
        }
        updateSelected();
    }

    public void updateSelected(){
        num_item_select.setText(counter + " Items Selected");
    }

    public boolean getStatus(){
        return delete_mode_status;
    }
}
