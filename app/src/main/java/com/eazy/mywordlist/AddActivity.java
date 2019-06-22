package com.eazy.mywordlist;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

import android.os.AsyncTask;

public class AddActivity extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteTextView;
    private EditText def_tv;
    private EditText translation_et;
    public static final String EXTRA_MESSAGE = "com.eazy.android.MyWordList.extra.Message";
    public static final int TEXT_REQUEST = 1;
    private DatabaseHelper mDatabaseHelper;
    private String myUrl;
    private String[] transList;
    private Spinner spinner;
    private Button translate;
    private Map<String, String> lanMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        mDatabaseHelper = new DatabaseHelper(this);

        autoCompleteTextView = findViewById(R.id.word_tv);
        def_tv = findViewById(R.id.def_et);
        translation_et = findViewById(R.id.trans_et);
        spinner = findViewById(R.id.trans_spinner);
        translate = findViewById(R.id.trans_bt);


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
        spinner.setAdapter(adapter);

        translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = autoCompleteTextView.getText().toString();
                GetTranslation();
            }
        });

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
        boolean result = mDatabaseHelper.addData(word, def);

        if(result){
            Toast.makeText(this, "Word is added successfully!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Adding word failed!", Toast.LENGTH_SHORT).show();
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

    public void GetTranslation(){
        final String input = autoCompleteTextView.getText().toString();
        final String fields = "definitions";
        final String language = "en/id";
        final String strictMatch = "false";
        final String word_id = input.toLowerCase();
        String url =  "https://od-api.oxforddictionaries.com/api/v2/translations/" + language + "/" + word_id;

        MyDictionaryRequest myDictionaryRequest = new MyDictionaryRequest(this, translation_et);
        myDictionaryRequest.execute(url);
        Log.d("TAG...Get Translation: ", url);
        Log.d("TAG...Get Translation: ", "Succeed!");
    }

    private String getLanguage() {
        final String[] key = new String[1];
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                key[0] = (String) parent.getItemAtPosition(position);
                }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
        Log.d("TAG...Get Translation: ",lanMap.get(key[0]) );

        return lanMap.get(key[0]);
    }

    public static boolean check_for_word(String word) {
        // System.out.println(word);
        try {
            BufferedReader in = new BufferedReader(new FileReader(
                    "/usr/share/dict/american-english"));
            String str;
            while ((str = in.readLine()) != null) {
                if (str.indexOf(word) != -1) {
                    return true;
                }
            }
            in.close();
        } catch (IOException e) {
        }

        return false;
    }
}
