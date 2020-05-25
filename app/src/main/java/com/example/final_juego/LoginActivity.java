package com.example.final_juego;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText usernameInput, passwordInput;
    Button btnLogin, btnSignup;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        btnLogin = findViewById(R.id.loginButton);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginValidation(new VolleyCallback2(){
                    public void onSuccess(JSONObject response) {
                        try {
                            String password = response.getString("password");
                            String input = passwordInput.getText().toString();
                            if (password.equals(input)) {
                                savePreferences(response.getString("username"), response.getString("password"), response.getString("country"));
                                Intent nextScreen = new Intent(getApplicationContext(), MenuActivity.class);
                                startActivity(nextScreen);
                                finish();
                            }
                            else {
                                passwordInput.setError("Password is incorrect.");
                                passwordInput.requestFocus();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },"http://192.168.1.152:45455/ws/users/" + usernameInput.getText() + "");
            }
        });
        btnSignup = findViewById(R.id.signUpButton);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpScreen = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(signUpScreen);
                finish();
            }
        });
        getPreferences();
    }

    private void loginValidation(final VolleyCallback2 callback, String URL){
        JsonObjectRequest request = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        usernameInput.setError("User not found.");
                        usernameInput.requestFocus();
                    }
                });
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private interface VolleyCallback2{
        void onSuccess(JSONObject result);
    }
    private void savePreferences(String username, String password, String country){
        SharedPreferences preferences = getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.putString("country", country);
        editor.putBoolean("session", true);
        editor.commit();
    }
    private void getPreferences(){
        SharedPreferences preferences = getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE);
        usernameInput.setText(preferences.getString("username", ""));
        passwordInput.setText(preferences.getString("password", ""));
    }
}
