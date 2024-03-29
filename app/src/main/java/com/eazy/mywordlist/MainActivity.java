package com.eazy.mywordlist;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnLongClickListener{

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
    private boolean edit_mode_status;
    private int counter;
    private List<Word> delList;
    private CardAdapter adapter_card;
    private int[] itemLocation;
    private final static String new_button_text = "New";
    private final static String almost_button_text = "Almost";
    private final static String know_button_text = "Know";
    private boolean move_mode_status;
    private boolean firstCreated;
    private Dialog viewDialog;
    private ViewPager card_viewPager;
    private int currentCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabaseHelper = new DatabaseHelper(this);

        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        num_item_select = findViewById(R.id.num_selected_tv);
        num_item_select.setText(getResources().getText(R.string.title));

        delete_mode_status = false;
        move_mode_status = false;
        edit_mode_status = false;
        firstCreated = false;
        counter = 0;
        newList = new ArrayList<>();
        famList =  new ArrayList<>();
        knownList =  new ArrayList<>();

        createFragments();

        //set icons of tabs
        tabsSetUp();

        //if received from add Activity, create card adapter and scroll to relating position
        Intent in = getIntent();
       // Word received = in.getParcelableExtra("edit mode");
        boolean received = in.getBooleanExtra("edit mode",false);

        if(received){
           // int i = adapter_card.getItemPosition(received);
            edit_mode_status = false;
            itemLocation = in.getIntArrayExtra("back item location");

            TabLayout.Tab t = tabLayout.getTabAt(itemLocation[0]);
            if(t!= null) {
                t.select();
            }
            viewMode(itemLocation[1]);
        }else{
            currentCard = 0;
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_button, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(delete_mode_status){
            quitDeleteMode();
        }else if(edit_mode_status){
            viewMode(currentCard);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_button:
                Intent intent = new Intent(this, AddActivity.class);
                startActivity(intent);
                return true;
            case R.id.delete_button:
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
                Toast.makeText(this, "Deleted!", Toast.LENGTH_SHORT).show();
                return true;
            case android.R.id.home:
                if(delete_mode_status) {
                    quitDeleteMode();
                }else if(edit_mode_status){
                    viewMode(currentCard);
                }
                return true;
            case R.id.info_button:
                Intent intent1 = new Intent(this, AboutActivity.class);
                startActivity(intent1);
        }
        return false;
    }

    public void quitDeleteMode(){
        int i = tabLayout.getSelectedTabPosition();
        delete_mode_status = false;
        counter = 0;
        num_item_select.setText(getResources().getText(R.string.title));
        delList.clear();
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.add_button);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        tabsSetUp();
        TabLayout.Tab tab = tabLayout.getTabAt(i);
        if(tab != null) {
            tab.select();
        }
    }

    //Enter delete mode.
    @Override
    public boolean onLongClick(View v) {
        delList = new ArrayList<>();
        toolbar.getMenu().clear();
        num_item_select.setText(R.string.nums_items_selected);
        toolbar.inflateMenu(R.menu.delete_mode);
        delete_mode_status = true;
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

    public void viewMode(int position){
        if(edit_mode_status){
            tabsSetUp();
            edit_mode_status = false;
        }
        List<Word> mList = getList(tabLayout.getSelectedTabPosition());
        viewDialog = new Dialog(this);

        viewDialog.setContentView(R.layout.card_view_pager);
        viewDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        card_viewPager = viewDialog.findViewById(R.id.card_viewPager);

        adapter_card = new CardAdapter(mList, getApplicationContext(), tabLayout.getSelectedTabPosition(), this);
        card_viewPager.setAdapter(adapter_card);
        card_viewPager.setPadding(130,0,130,0);

        currentCard = card_viewPager.getCurrentItem();

        viewDialog.show();
        card_viewPager.setCurrentItem(position, true);

    }

    public void editMode(Word word){
        edit_mode_status = true;
        currentCard = card_viewPager.getCurrentItem();
        Intent intent = new Intent(this, AddActivity.class);
        String[] l = {word.getWord(), word.getDef()};
        intent.putExtra("secret code", l);
        itemLocation = new int[]{tabLayout.getSelectedTabPosition(), card_viewPager.getCurrentItem()};
        intent.putExtra("item location", itemLocation);
        startActivity(intent);
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

    public void tabsSetUp(){
        if(!firstCreated) {
            newList.clear();
            famList.clear();
            knownList.clear();
        }

        Cursor data = mDatabaseHelper.getData(0);
        if(data!=null) {
            while (data.moveToNext()) {
                newList.add(new Word(data.getString(0), data.getString(1)));
            }
        }

        Cursor data2 = mDatabaseHelper.getData(1);
        if(data2!=null) {
            while (data2.moveToNext()) {
                famList.add(new Word(data2.getString(0), data2.getString(1)));
            }
        }

        Cursor data3 = mDatabaseHelper.getData(2);
        if(data3!=null) {
            while (data3.moveToNext()) {
                knownList.add(new Word(data3.getString(0), data3.getString(1)));
            }
        }

        if(!move_mode_status) {
            //implement tabs and pager
            tabLayout = findViewById(R.id.tabLayout);
            viewPager = findViewById(R.id.viewPager);
            adapter = new PagerAdapter(getSupportFragmentManager(), 3, newFragment, famFragment, knownFragment);
            viewPager.setAdapter(adapter);
            tabLayout.setupWithViewPager(viewPager);
        }
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_new_word);
        tabLayout.getTabAt(0).setText("Don't Know");
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_fam_word);
        tabLayout.getTabAt(1).setText("Almost");
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_knwon_word);
        tabLayout.getTabAt(2).setText("Know");
        move_mode_status = false;
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

    public com.eazy.mywordlist.newFragment getNewFragment() {
        return newFragment;
    }

    public com.eazy.mywordlist.famFragment getFamFragment() {
        return famFragment;
    }

    public com.eazy.mywordlist.knownFragment getKnownFragment() {
        return knownFragment;
    }

    //get word item position
    public int getCurrentCard() { return viewPager.getCurrentItem();}

    //get card position
    public int getCardPos(){ return currentCard;}

    public void moveWord(Word word, int target){
        move_mode_status = true;
        int currentTab = tabLayout.getSelectedTabPosition();
        int i = getList(tabLayout.getSelectedTabPosition()).indexOf(word);
        mDatabaseHelper.switchList(word, target);
        getList(tabLayout.getSelectedTabPosition()).remove(word);
        getList(target).add(word);
        notifyLists(tabLayout.getSelectedTabPosition());
        notifyLists(target);
        tabsSetUp();
        TabLayout.Tab tab = tabLayout.getTabAt(currentTab);
        tab.select();
        //viewPager.setCurrentItem(getNewItem(i));
    }

    //return the new position after item moved.
    public int getNewItem(int i){
        if(i-1 >= 0){
            return i-1;
        }else{
            return 0;
        }
    }

    public void notifyLists(int pos){
        switch (pos){
            case 0:
                newFragment.notifyData();
            case 1:
                famFragment.notifyData();
            case 2:
                knownFragment.notifyData();
        }
    }

    public void showAlertDialog(final Word word) {
        final Dialog alert = new Dialog(this);
        View v = getLayoutInflater().inflate(R.layout.move_layout, null);
        int i = tabLayout.getSelectedTabPosition();
        alert.setContentView(v);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final Button bt1 = alert.findViewById(R.id.move_bt_1);
        final Button bt2 = alert.findViewById(R.id.move_bt_2);
        Button bt3 = alert.findViewById(R.id.move_bt_3);

        switch (tabLayout.getSelectedTabPosition()){
            case 0:
                bt1.setText(almost_button_text);
                bt2.setText(know_button_text);
                break;
            case 1:
                bt1.setText(new_button_text);
                bt2.setText(know_button_text);
                break;
            case 2:
                bt1.setText(new_button_text);
                bt2.setText(almost_button_text);
                break;
        }
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button target = v.findViewById(R.id.move_bt_1);
                String t = target.getText().toString();
                moveWord(word, getTargetList(t));
                alert.cancel();
                Toast.makeText(MainActivity.this, "Word is moved to "+bt1.getText().toString() + " list.", Toast.LENGTH_SHORT).show();
            }
        });

        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button target = v.findViewById(R.id.move_bt_2);
                String t = target.getText().toString();
                TextView a = v.findViewById(R.id.word_tv);
                TextView b = v.findViewById(R.id.def_tv);
                moveWord(word, getTargetList(t));
                alert.cancel();
                Toast.makeText(MainActivity.this, "Word is moved to "+bt2.getText().toString() + " list.", Toast.LENGTH_SHORT).show();
            }
        });

        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.cancel();
            }
        });
        alert.show();
    }

    public int getTargetList(String s){
        switch (s){
            case new_button_text:
                return 0;
            case almost_button_text:
                return 1;
            case know_button_text:
                return 2;
        }
        return -1;
    }

    public boolean isDelete_mode_status() {
        return delete_mode_status;
    }

    public void setEdit_mode_status(boolean edit_mode_status) {
        this.edit_mode_status = edit_mode_status;
    }
}
