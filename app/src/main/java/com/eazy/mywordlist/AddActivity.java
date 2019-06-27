package com.eazy.mywordlist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class AddActivity extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteTextView;
    private EditText def_tv;
    private EditText translation_et;
    private Button search_bt;
    public static final String EXTRA_MESSAGE = "com.eazy.android.MyWordList.extra.Message";
    public static final int TEXT_REQUEST = 1;
    private DatabaseHelper mDatabaseHelper;
    private String myUrl;
    private Toolbar toolbar;
    private TextView title;
    private boolean edit_mode;
    private MainActivity mainActivity;
    private int[] itemLocation;
    //private String[] transList;
    //private Spinner spinner;
    //private Button translate;
    //private Map<String, String> lanMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        edit_mode = false;
        toolbar = findViewById(R.id.toolbar_add);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = findViewById(R.id.num_selected_tv);
        title.setText("Add New Word");

        mDatabaseHelper = new DatabaseHelper(this);

        autoCompleteTextView = findViewById(R.id.word_tv);
        def_tv = findViewById(R.id.def_et);
        search_bt = findViewById(R.id.search_button);

        autoCompleteTextView.addTextChangedListener(textWatcher); //enable search button when text is entered, disable otherwise

        Intent intent= getIntent();
        String[] s = intent.getStringArrayExtra("secret code");
        itemLocation = intent.getIntArrayExtra("item location");
        if(s!=null)
        {
            edit_mode = true;
            autoCompleteTextView.setText(s[0]);
            def_tv.setText(s[1]);
            title.setText("Edit");
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String string = autoCompleteTextView.getText().toString().trim();

            if(!string.isEmpty()){
                search_bt.setEnabled(true);
                search_bt.setBackgroundColor(getResources().getColor(R.color.search));
            }else{
                search_bt.setEnabled(false);
                search_bt.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_done, menu);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (edit_mode) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("edit mode", true);
            edit_mode = false;
            startActivity(intent);
        } else {
            super.onBackPressed();
        }
    }
    //Todo: Make the input section rounder
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_done_bt:
                String word = autoCompleteTextView.getText().toString();
                String def = def_tv.getText().toString();
                Intent intent = new Intent(this, MainActivity.class);
                AddData(word, def);
                if(edit_mode){
                    intent.putExtra("edit mode", true);
                    intent.putExtra("back item location", itemLocation);
                    Log.d("TAG add tab Location", String.valueOf(itemLocation[0]));
                    Log.d("TAG add list Location", String.valueOf(itemLocation[1]));

                }
                edit_mode = false;
                startActivity(intent);
                return true;
        }
        return false;
    }

    public void AddData(String word, String def){
        int result;
        if(edit_mode){
            mDatabaseHelper.updateData(new Word(word,def));
            return;
        }else {
            result = mDatabaseHelper.addData(word, def, 0);  //could ask user to choose which list to add to, default new
        }
        if(result == 0){
            Toast.makeText(this, "Word is added successfully!", Toast.LENGTH_SHORT).show();
        }else if(result == -2){
            Toast.makeText(this, "Adding word failed!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "This Word Already Existed!", Toast.LENGTH_SHORT).show();
        }

    }

    private String dictionaryEntries() {
        final String input = autoCompleteTextView.getText().toString();
        final String fields = "pronunciations";
        final String language = "en-us";
        final String strictMatch = "false";
        final String word_id = input.toLowerCase();
        return "https://od-api.oxforddictionaries.com/api/v2/entries/" + language + "/" + word_id;
    }

    public void GetDefinition(View view) {
        myUrl = dictionaryEntries();
        Log.d("TAG... Get definition: ", myUrl);

        MyDictionaryRequest myDictionaryRequest = new MyDictionaryRequest(this, def_tv);
        myDictionaryRequest.execute(myUrl);
        Log.d("TAG... Get definition: ", "Succeed!");
    }
}
