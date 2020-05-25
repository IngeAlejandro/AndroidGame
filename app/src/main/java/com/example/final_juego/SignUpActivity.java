package com.example.final_juego;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class SignUpActivity extends AppCompatActivity {

    TextInputLayout countrySelection;
    AutoCompleteTextView  dropDownText;
    TextInputEditText usernameInput, passwordInput;
    Button btnLogin,btnSignup;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        btnSignup = findViewById(R.id.signUpButton);
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameValidation("http://192.168.100.5:45455/ws/users/" + usernameInput.getText() + "");
            }
        });
        btnLogin = findViewById(R.id.loginButton);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginScreen = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginScreen);
                finish();

            }
        });
        countrySelection = findViewById(R.id.countrySelection);
        dropDownText = findViewById(R.id.dropdown_text);
        String[] items = new String[]{
          "México",
          "United States",
          "日本",
          "España",
          "中国"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        SignUpActivity.this,
                        R.layout.support_simple_spinner_dropdown_item,
                        items);

        dropDownText.setAdapter(adapter);
    }
    private void usernameValidation(String URL){
        JsonObjectRequest request = new JsonObjectRequest(URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String username = response.getString("username");
                    String input = usernameInput.getText().toString();
                    if (username.equals(input)) {
                        usernameInput.setError("User already exists.");
                        usernameInput.requestFocus();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                validation();
            }
        });
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
    private String countryCode(){
        String country = dropDownText.getText().toString();
        String code = "";
        switch (country){
            case "México":
                code = "MX";
                break;
            case "United States":
                code = "US";
                break;
            case "日本":
                code = "JP";
            break;
            case "España":
                code ="ES";
            break;
            case "中国":
                code = "CH";
            break;
            default:
                countrySelection.setError("Invalid Country.");
        }
        return code;
    }
        private void validation() {
            if (usernameInput.getText().toString().trim().equalsIgnoreCase("")) {
                usernameInput.setError("This field can not be blank");
                usernameInput.requestFocus();
            }
            else if (usernameInput.getText().toString().trim().equalsIgnoreCase("")) {
                passwordInput.setError("This field can not be blank");
                passwordInput.requestFocus();
            }
            else if (countryCode().trim().equalsIgnoreCase("")) {
                dropDownText.setError("Invalid Country");
                dropDownText.requestFocus();
            }
            else{
                try {
                    registerUser();
                } catch (AuthFailureError authFailureError) {
                    authFailureError.printStackTrace();
                }
            }
        }
        private void registerUser() throws AuthFailureError {
            try {
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                String URL = "http://192.168.1.152:45455/ws/users/";
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("username", usernameInput.getText().toString());
                jsonBody.put("password", passwordInput.getText().toString());
                jsonBody.put("country", countryCode());
                final String requestBody = jsonBody.toString();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), "Succesfully registered", Toast.LENGTH_SHORT).show();
                        savePreferences(usernameInput.getText().toString(), passwordInput.getText().toString(), countryCode());
                        Intent nextScreen = new Intent(getApplicationContext(), MenuActivity.class);
                        startActivity(nextScreen);
                        finish();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Connection error. Try Again", Toast.LENGTH_SHORT).show();
                        Log.e("VOLLEY", error.toString());
                    }
                }) {
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        try {
                            return requestBody == null ? null : requestBody.getBytes("utf-8");
                        } catch (UnsupportedEncodingException uee) {
                            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                            return null;
                        }
                    }

                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        String responseString = "";
                        if (response != null) {
                            responseString = String.valueOf(response.statusCode);
                            // can get more details such as response.headers
                        }
                        return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                    }
                };

                requestQueue.add(stringRequest);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
}
