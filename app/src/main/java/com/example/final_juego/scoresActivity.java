package com.example.final_juego;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class scoresActivity extends AppCompatActivity{

    Button globalBtn, countryBtn, personalBtn;
    TableLayout tableLayout;
    RequestQueue requestQueue;
    String[] header =  {"Ranking", "Username", "Score", "Country"};
    ArrayList<String[]> rows = new ArrayList<>();
    DynamicTable dynamicTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        tableLayout = (TableLayout) findViewById(R.id.table);
        globalBtn = (Button) findViewById(R.id.globalButton);
        countryBtn = (Button) findViewById(R.id.countryButton);
        personalBtn = (Button) findViewById(R.id.personalButton);

        globalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tableLayout.removeAllViews();
                rows.clear();
                dynamicTable = new DynamicTable(tableLayout, getApplicationContext());
                getGlobals(new VolleyCallback(){
                    public void onSuccess(JSONArray result) {
                        JSONObject jsonObject = null;
                        for (int i = 0; i < result.length(); i++) {
                            try {
                                jsonObject = result.getJSONObject(i);
                                String[] row = new String[]{
                                        String.valueOf(i+1), jsonObject.getString("username"),
                                        String.valueOf(jsonObject.getInt("score")),
                                        jsonObject.getString("country")
                                };
                                rows.add(row);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        dynamicTable.addHeader(header);
                        dynamicTable.addData(rows);
                        dynamicTable.bgHeader(Color.MAGENTA);
                        dynamicTable.bgData(Color.GRAY, Color.LTGRAY);

                    }
                });
            }
        });
        countryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tableLayout.removeAllViews();
                rows.clear();
                dynamicTable = new DynamicTable(tableLayout, getApplicationContext());
                getCountry(new VolleyCallback(){
                    public void onSuccess(JSONArray result) {
                        JSONObject jsonObject = null;
                        for (int i = 0; i < result.length(); i++) {
                            try {
                                jsonObject = result.getJSONObject(i);
                                String[] row = new String[]{
                                        String.valueOf(i+1), jsonObject.getString("username"),
                                        String.valueOf(jsonObject.getInt("score")),
                                        jsonObject.getString("country")
                                };
                                rows.add(row);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        dynamicTable.addHeader(header);
                        dynamicTable.addData(rows);
                        dynamicTable.bgHeader(Color.MAGENTA);
                        dynamicTable.bgData(Color.GRAY, Color.LTGRAY);
                    }
                });
            }
        });
        personalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tableLayout.removeAllViews();
                rows.clear();
                dynamicTable = new DynamicTable(tableLayout, getApplicationContext());
                getPersonal(new VolleyCallback(){
                    public void onSuccess(JSONArray result) {
                        JSONObject jsonObject = null;
                        for (int i = 0; i < result.length(); i++) {
                            try {
                                jsonObject = result.getJSONObject(i);
                                String[] row = new String[]{
                                        String.valueOf(i+1),
                                        jsonObject.getString("username"),
                                        String.valueOf(jsonObject.getInt("score")),
                                        jsonObject.getString("country")
                                };
                                rows.add(row);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        dynamicTable.addHeader(header);
                        dynamicTable.addData(rows);
                        dynamicTable.bgHeader(Color.MAGENTA);
                        dynamicTable.bgData(Color.GRAY, Color.LTGRAY);
                    }
                });
            }
        });
    }
    private void getGlobals(final VolleyCallback callback){
        String URL = "http://192.168.1.152:45455/ws/scores/10";
        JsonArrayRequest request = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(scoresActivity.this, "Connection error. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void getCountry(final VolleyCallback callback){
        SharedPreferences preferences = getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
        String cc = preferences.getString("country", "");
        String URL = "http://192.168.1.152:45455/ws/scores/10/code=" + cc;
        JsonArrayRequest request = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(scoresActivity.this, "Connection error. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void getPersonal(final VolleyCallback callback){
        SharedPreferences preferences = getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
        String user = preferences.getString("username", "");
        String URL = "http://192.168.1.152:45455/ws/scores/10/" + user;
        JsonArrayRequest request = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(scoresActivity.this, "Connection error. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    public interface VolleyCallback{
        void onSuccess(JSONArray result);
    }

}
