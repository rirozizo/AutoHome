package com.rirozizo.autohome;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Switch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;

public class DashBoard extends AppCompatActivity {

    Switch sw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        sw = (Switch) findViewById(R.id.switch1);
        //sw.setText("test");

        new DashBoard.RESTApiManager().execute();
    }

    public void refresh(View v) {
        new DashBoard.RESTApiManager().execute();
    }

    private class RESTApiManager extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String result = "";
            try {
                URL restAPI = new URL("https://io.adafruit.com/api/feeds?x-aio-key="); //Add AIO key here
                URLConnection tc = restAPI.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(tc.getInputStream()));
                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = in.readLine()) != null) {
                    //JSONArray ja = new JSONArray(line);

                    //for (int i = 0; i < ja.length(); i++) {
                    //    JSONObject jo = (JSONObject) ja.get(i);
                    //    listItems.add(jo.getString("name"));
                    //}

                    sb.append(line + "\n");
                }

                result = sb.toString();

                Log.i("TEST", result);


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        protected void onPostExecute(String result) {

            ArrayList<String> listItems = new ArrayList<String>();
            try {
                JSONArray json = new JSONArray(result);

                for (int i = 0; i < json.length(); i++) {
                    JSONObject feed = json.getJSONObject(i);
                    listItems.add(feed.getString("name") + ": " + feed.get("last_value"));
                }

            } catch (JSONException e) {
                // manage exceptions
            }

            String s = result.substring(result.indexOf('\n')+9);
            String[] lines = s.split("\n");
            String[] dataLines = Arrays.copyOfRange(lines, 0, lines.length - 2);

            dataLines[1] = dataLines[1].substring(result.indexOf('\n')+4);
            //Log.i("AFTER EDITS", dataLines[1]);
            String[] name = dataLines[1].split(":");
            String[] status = dataLines[6].split(":");
            //Log.i("AFTER SPLIT 1", name[0]);
            //Log.i("AFTER SPLIT 2", name[1]);
            name[1] = name[1].replace("\"","");
            name[1] = name[1].replace(",","");
            status[1] = status[1].replace("\"","");
            status[1] = status[1].replace(",","");
            sw.setText(name[1]);
            Log.i("SSS", status[1]);
            if (status[1].equals("ON")) {
                sw.setChecked(true);
            } else {
                sw.setChecked(false);
            }

            //Log.i("switch", "After If");

        }

    }
}