package com.eazy.mywordlist;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnLongClickListener{

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PagerAdapter adapter;
    private Bundle bundle_new;
    private Bundle bundle_fam;
    private Bundle bundle_known;
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
    private boolean view_mode_status;
    private boolean edit_mode_status;
    private int counter;
    private List<Word> delList;
    private CardAdapter adapter_card;
    private int[] itemLocation;
    private int currentTab;
    private TabLayout.OnTabSelectedListener selectedListener;
    int itab;

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
        famList =  new ArrayList<>();
        knownList =  new ArrayList<>();

        Cursor data = mDatabaseHelper.getData(0);
        while(data.moveToNext()){
            newList.add(new Word(data.getString(0), data.getString(1)));
        }

        data = mDatabaseHelper.getData(1);
        while(data.moveToNext()){
            famList.add(new Word(data.getString(0), data.getString(1)));
        }

        data = mDatabaseHelper.getData(2);
        while(data.moveToNext()){
            knownList.add(new Word(data.getString(0), data.getString(1)));
        }

        createFragments();

        view_mode_status = false;
        edit_mode_status = false;
        //implement tabs and pager
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        adapter = new PagerAdapter(getSupportFragmentManager(), 3, newFragment, famFragment, knownFragment);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        //set icons of tabs
        tabsIcon();

        //tabLayout.addOnTabSelectedListener(selectedListener);
        //if received from add Activity, create card adapter and scroll to relating position
        Intent in = getIntent();
       // Word received = in.getParcelableExtra("edit mode");
        boolean received = in.getBooleanExtra("edit mode",false);

        if(received){
           // int i = adapter_card.getItemPosition(received);
            edit_mode_status = false;
            itemLocation = in.getIntArrayExtra("back item location");
            Log.d("TAG Location", String.valueOf(itemLocation[0]));
            Log.d("TAG Location", String.valueOf(itemLocation[1]));

            TabLayout.Tab t = tabLayout.getTabAt(itemLocation[0]);
            t.select();
            viewMode(itemLocation[1]);
        }else{
            currentTab = 0;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(view_mode_status){
            getMenuInflater().inflate(R.menu.edit_mode, menu);
        }else {
            getMenuInflater().inflate(R.menu.add_button, menu);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if(delete_mode_status){
            quitDeleteMode();
        }else if(view_mode_status) {
            quitViewMode();
        }else if(edit_mode_status){
            quitEditMode();
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_button:
                Log.d(LOG_TAG, "Add Button clicked!");
                Intent intent = new Intent(this, AddActivity.class);
                startActivity(intent);
                return true;
            case R.id.delete_button:
                Log.d(LOG_TAG, "Delete Button clicked!");
                onLongClick(this.getCurrentFocus());
                return true;
            case R.id.del_mode_menu:
                //Remove data stored in removedList from database and related lists
                for(Word w : delList){
                    mDatabaseHelper.deleteData(w);
                    newList.remove(w);
                    famList.remove(w);
                    knownList.remove(w);
                    newFragment.preDelete(w);
                    famFragment.preDelete(w);
                    knownFragment.preDelete(w);
                }
                quitDeleteMode();
                Toast.makeText(this, "Delete!", Toast.LENGTH_SHORT).show();
                return true;
            case android.R.id.home:
                if(delete_mode_status) {
                    quitDeleteMode();
                }else if(view_mode_status){
                    if(edit_mode_status){
                        quitEditMode();
                    }else {
                        quitViewMode();
                    }
                }
                return true;
            case R.id.edit_menu:
                editMode();

        }
        return false;
    }

    public void quitDeleteMode(){
        delete_mode_status = false;
        counter = 0;
        num_item_select.setText("My Word List");
        delList.clear();
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.add_button);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ViewGroup viewGroup = findViewById(R.id.recyclerView);
        ViewGroup v_fam = findViewById(R.id.recyclerView_fam);
        ViewGroup v_known = findViewById(R.id.recyclerView_fam);
        for(int i = 0; i < viewGroup.getChildCount(); i++) {
            viewGroup.getChildAt(i).setBackgroundColor(android.R.drawable.btn_default);  //set all the children to default background color.
        }
        for(int i = 0; i < v_fam.getChildCount(); i++) {
            v_fam.getChildAt(i).setBackgroundColor(android.R.drawable.btn_default);  //set all the children to default background color.
        }
        for(int i = 0; i < v_known.getChildCount(); i++) {
            v_known.getChildAt(i).setBackgroundColor(android.R.drawable.btn_default);  //set all the children to default background color.
        }
        tabsIcon();
    }

    //Enter delete mode.
    @Override
    public boolean onLongClick(View v) {
        delList = new ArrayList<>();
        toolbar.getMenu().clear();
        num_item_select.setText(R.string.nums_items_selected);
        toolbar.inflateMenu(R.menu.delete_mode);
        delete_mode_status = true;
        adapter.notifyDataSetChanged();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tabsIcon();
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

    public void viewMode(int position){
        itab = tabLayout.getSelectedTabPosition();
        List<Word> mList = getList(tabLayout.getSelectedTabPosition());
        adapter_card = new CardAdapter(mList, getApplicationContext());
        viewPager.setAdapter(adapter_card);
        viewPager.setPadding(130,0,130,0);
        view_mode_status = true;
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.edit_mode);
        viewPager.setCurrentItem(position, true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void quitViewMode(){
        adapter = new PagerAdapter(getSupportFragmentManager(), 3, newFragment, famFragment, knownFragment);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(0,0,0,0);
        view_mode_status = false;
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.add_button);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        tabsIcon();
    }

    public void editMode(){
       // view_mode_status = false;
        edit_mode_status = true;
        Intent intent = new Intent(this, AddActivity.class);
        Word word = adapter_card.getmList().get(viewPager.getCurrentItem());
        String[] l = {word.getWord(), word.getDef()};
        intent.putExtra("secret code", l);
        itemLocation = new int[]{itab, viewPager.getCurrentItem()};
        Log.d("TAG Ori tab Location", String.valueOf(itemLocation[0]));
        Log.d("TAG Ori list Location", String.valueOf(itemLocation[1]));
        intent.putExtra("item location", itemLocation);
        startActivity(intent);
    }

    public void quitEditMode(){
        view_mode_status = true;
        edit_mode_status = false;
        num_item_select.setText("My Word List");
        tabsIcon();
    }

    public void updateSelected(){
        num_item_select.setText(counter + " Items Selected");
    }

    public List<Word> getList(int i){
        switch (i){
            case 0:
                return newList;
            case 1:
                return famList;
            case 2:
                return knownList;
        default:
            return  null;
        }
    }

    public boolean getStatus(){
        return delete_mode_status;
    }

    public boolean getViewModeStatus() { return view_mode_status;}

    public void tabsIcon(){
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_new_word);
        tabLayout.getTabAt(0).setText("Don't Know");
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_fam_word);
        tabLayout.getTabAt(1).setText("Almost");
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_knwon_word);
        tabLayout.getTabAt(2).setText("Know");
    }

    public void createFragments(){
        bundle_new = new Bundle();
        bundle_new.putSerializable("getNewList", (Serializable) newList);
        newFragment = new newFragment();
        newFragment.setArguments(bundle_new);

        bundle_fam = new Bundle();
        bundle_fam.putSerializable("getFamList", (Serializable)famList);
        famFragment = new famFragment();
        famFragment.setArguments(bundle_fam);

        bundle_known = new Bundle();
        bundle_known.putSerializable("getKnownList", (Serializable)knownList);
        knownFragment = new knownFragment();
        knownFragment.setArguments(bundle_known);
    }
}
