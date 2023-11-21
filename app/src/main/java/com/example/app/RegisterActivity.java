package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername,etEmail,etPassword,etRepeatpassword;//未設定名稱
    private String username,email, password,repeatpassword ;//未設定名稱
    private String URL = "http://0w0chen3060-2.ddns.net:7777/app_login/register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }
    public void register(View view) {
        username = email = password = "";

        etUsername = findViewById(R.id.et_LoginPassword);
        etPassword = findViewById(R.id.et_RegisterPassword);
        etEmail = findViewById(R.id.et_Email);
        etRepeatpassword = findViewById(R.id.et_ConformPassword);

        username = etUsername.getText().toString().trim();
        email = etEmail.getText().toString().trim();
        password = etPassword.getText().toString().trim();


        String name = etUsername.getText().toString();
        String emil = etEmail.getText().toString();
        String password = etPassword.getText().toString();


        if (name.length() == 0) {
            Toast.makeText(RegisterActivity.this, "用户名不能為空", Toast.LENGTH_SHORT).show();
            return;
        } else if (4 >= name.length()) {
            Toast.makeText(RegisterActivity.this, "用户名長度應在4-18字之間", Toast.LENGTH_SHORT).show();
            return;
        } else if (name.length() >= 18) {
            Toast.makeText(RegisterActivity.this, "用户名長度應在4-18字之間", Toast.LENGTH_SHORT).show();
            return;
        } else if (emil.length() == 0) {
            Toast.makeText(RegisterActivity.this, "Email不能為空", Toast.LENGTH_SHORT).show();
            return;
        } else if (password.length() == 0) {
            Toast.makeText(RegisterActivity.this, "密碼不能為空", Toast.LENGTH_SHORT).show();
            return;
        } else if (4 >=password.length()) {
            Toast.makeText(RegisterActivity.this, "密碼長度應在4-18字之間", Toast.LENGTH_SHORT).show();
            return;
        } else if (password.length() >= 18) {
            Toast.makeText(RegisterActivity.this, "密碼長度應在4-18字之間", Toast.LENGTH_SHORT).show();
            return;
        }


        if (!email.equals("") && !password.equals("")) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("res", response);
                    if (response.equals("success")) {
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (response.equals("failure")) {
                        Toast.makeText(RegisterActivity.this, "Invalid Login Id/Password", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(RegisterActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> data = new HashMap<>();
                    data.put("username", username);
                    data.put("email", email);
                    data.put("password", password);

                    return data;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        } else {
            Toast.makeText(this, "Fields can not be empty!", Toast.LENGTH_SHORT).show();
        }

        TextView btn=findViewById(R.id.AlreadyHaveAccount);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }
}