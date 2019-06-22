package com.eazy.mywordlist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import android.os.AsyncTask;

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

        mDatabaseHelper = new DatabaseHelper(this);

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

    private String dictionaryEntries(String input) {
        final String language = "en-gb";
        final String fields = "definitions";
        final String strictMatch = "false";
        final String word_id = input.toLowerCase();
        return "https://od-api.oxforddictionaries.com:443/api/v2/entries/" + language + "/" + word_id + "?" + "fields=" + fields + "&strictMatch=" + strictMatch;
    }

    public void GetDefinition(View view) {
        String word = autoCompleteTextView.getText().toString();
      //  if(check_for_word(word)){
            String def = dictionaryEntries(word);
            editText.setText(def);
            Log.d("TAG: Get definition: ", "Succeed!");
            //return;
        //}
       // Log.d("TAG: Get definition: ", "Failed!");

    }

    //in android calling network requests on the main thread forbidden by default
    //create class to do async job
    private class CallbackTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            final String app_id = "cfd35d9e";
            final String app_key = "0bfffe0a4f8937a3c99e567d49328031";
            try {
                URL url = new URL(params[0]);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Accept","application/json");
                urlConnection.setRequestProperty("app_id",app_id);
                urlConnection.setRequestProperty("app_key",app_key);

                // read the output from the server
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }

                return stringBuilder.toString();

            }
            catch (Exception e) {
                e.printStackTrace();
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.d("Definition is ", result);
        }
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
