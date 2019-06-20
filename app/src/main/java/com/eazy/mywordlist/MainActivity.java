package com.eazy.mywordlist;

import android.content.Intent;
import android.database.Cursor;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PagerAdapter adapter;
    private Bundle bundle;
    private List<Word> newList;
    private newFragment newFragment;
    private famFragment famFragment;
    private knownFragment knownFragment;
    private DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Intent mIntent = getIntent();
        //String[] receiveM = mIntent.getStringArrayExtra(AddActivity.EXTRA_MESSAGE);

        mDatabaseHelper = new DatabaseHelper(this);

        newList = new ArrayList<>();
        /*
        if(receiveM != null) {
            newList.add(new Word(receiveM[0], receiveM[1]));
        }*/

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
                Log.d(LOG_TAG, "Button clicked!");
                Intent intent = new Intent(this, AddActivity.class);
                intent.putExtra("DatabaseHelper", (Parcelable) mDatabaseHelper);
                startActivityForResult(intent, 1);
                return true;
        }
        return false;
    }
}
