package com.eazy.mywordlist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class AddActivity extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteTextView;
    private EditText def_tv;
    private EditText translation_et;
    public static final String EXTRA_MESSAGE = "com.eazy.android.MyWordList.extra.Message";
    public static final int TEXT_REQUEST = 1;
    private DatabaseHelper mDatabaseHelper;
    private String myUrl;
    private Toolbar toolbar;
    private TextView title;
    //private String[] transList;
    //private Spinner spinner;
    //private Button translate;
    //private Map<String, String> lanMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        toolbar = findViewById(R.id.toolbar_add);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = findViewById(R.id.num_selected_tv);
        title.setText("Add New Word");

        mDatabaseHelper = new DatabaseHelper(this);

        autoCompleteTextView = findViewById(R.id.word_tv);
        def_tv = findViewById(R.id.def_et);

        /*
        lanMap = new HashMap<>();
        lanMap.put("Select a Language", "-1");
        lanMap.put("German", "de");
        lanMap.put("Greek", "el");
        lanMap.put("Indonesian", "id");
        lanMap.put("isiXhosa", "xh");
        lanMap.put("isiZulu", "zu");
        lanMap.put("Northern Sotho", "nso");
        lanMap.put("Malay", "ms");
        lanMap.put("Portuguese", "pt");
        lanMap.put("Romanian", "ro");
        lanMap.put("Setswana", "tn");
        lanMap.put("Spanish", "es");
        lanMap.put("Tajik", "tg");
        lanMap.put("Tatar", "tt");
        lanMap.put("Tok Pisin", "tpi");
        lanMap.put("Turkmen", "tk");

        transList = new String[]{"Select a Language" , "German","Greek","Indonesian","isiXhosa","isiZulu", "Northern Sotho", "Malay", "Portuguese", "Romanian","Setswana", "Spanish",
                "Tajik", "Tatar", "Tok Pisin", "Turkmen"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, transList);
        spinner.setAdapter(adapter);*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_done, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_done_bt:
                String word = autoCompleteTextView.getText().toString();
                String def = def_tv.getText().toString();
                Intent intent = new Intent(this, MainActivity.class);
                AddData(word, def);
                startActivity(intent);
                return true;
        }
        return false;
    }

    public void AddData(String word, String def){
        int result = mDatabaseHelper.addData(word, def, 0);  //could ask user to choose which list to add to, default new

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
        final String fields = "definitions";
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
