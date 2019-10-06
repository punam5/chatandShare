package com.example.hp.chatapp;

import android.os.AsyncTask;
import android.util.JsonReader;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

public class featch_data extends AsyncTask<String,Void,Void> {
    String data="";
    String input="";
    String singleParse="";
    @Override
    protected void onPostExecute(Void aVoid) {

        FindFriendActivity.textView.setText(singleParse);
        super.onPostExecute(aVoid);
    }

    @Override
    protected Void doInBackground(String... strings) {
      /*  try {
            URL url=new URL(strings[0]);
            HttpURLConnection httpURLConnection= (HttpURLConnection) url.openConnection();
            InputStream inputStream=httpURLConnection.getInputStream();
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));


            while(data!=null){

                data=bufferedReader.readLine();
                input+=data;

            }

           */




        String json=strings[0]; // place your json format here in double Quotes with proper escapes .......
        try {
            JSONObject jObject = new JSONObject(json.trim());
            JSONObject jsonObject=jObject.getJSONObject("Users");

            Iterator<?> keys = jsonObject.keys();

            while( keys.hasNext() ) {
                String key = (String)keys.next();
             //   if ( jObject.get(key) instanceof JSONObject ) {

                JSONObject jsonObject1=jsonObject.getJSONObject(key);

                singleParse+="\n\n\n"+"Name: "+jsonObject1.getString("name")+"\n"+
                        "Status: "+jsonObject1.optString("status","No status ")+"\n\n\n";
                //}
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }





        return null;
    }
}
