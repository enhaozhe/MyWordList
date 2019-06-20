package com.eazy.mywordlist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

public class AddActivity extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteTextView;
    private EditText editText;
    public static final String EXTRA_MESSAGE = "com.eazy.android.MyWordList.extra.Message";
    public static final int TEXT_REQUEST = 1;
    private DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        mDatabaseHelper = getIntent().getParcelableExtra("DatabaseHelper");

        autoCompleteTextView = findViewById(R.id.word_tv);
        editText = findViewById(R.id.def_et);
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
                String def = editText.getText().toString();
                Intent intent = new Intent(this, MainActivity.class);
                AddData(word, def);
                startActivity(intent);
/*
                String[] pass = {word, def};
                intent.putExtra(EXTRA_MESSAGE, pass);
                startActivityForResult(intent, TEXT_REQUEST);*/


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
}
