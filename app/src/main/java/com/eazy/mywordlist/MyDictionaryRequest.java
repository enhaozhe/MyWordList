package com.eazy.mywordlist;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MyDictionaryRequest extends AsyncTask<String, Integer, String> {

    private Context context;
    private EditText def_tv;

    MyDictionaryRequest(Context context, EditText de){
        this.context = context;
        def_tv = de;
    }

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

        String def;

            try {
                JSONObject resultObject = new JSONObject(result);
                JSONArray resultsArray = resultObject.getJSONArray("results");

                JSONObject lexicalObject = resultsArray.getJSONObject(0);
                JSONArray lexicalArray = lexicalObject.getJSONArray("lexicalEntries");

                JSONObject entriesObject = lexicalArray.getJSONObject(0);
                JSONArray entriesArray = entriesObject.getJSONArray("entries");

                JSONObject sensesObject = entriesArray.getJSONObject(0);
                JSONArray sensesArray = sensesObject.getJSONArray("senses");

                JSONObject definitionObject = sensesArray.getJSONObject(0);
                JSONArray definitionArray = definitionObject.getJSONArray("definitions");

                def = definitionArray.getString(0);

                def_tv.setText(def);

            } catch (JSONException ex) {
                Toast.makeText(context, "Please check your spelling or Internet Connection!", Toast.LENGTH_SHORT).show();
            }
    }
}
