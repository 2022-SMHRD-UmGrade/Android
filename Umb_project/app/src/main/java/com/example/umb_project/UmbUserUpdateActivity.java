package com.example.umb_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.example.umb_project.info.UserInfo;
import com.example.umb_project.vo.User;

import java.util.HashMap;
import java.util.Map;

public class UmbUserUpdateActivity extends AppCompatActivity {
    EditText edtChPw, edtChNick, edtChEmail, edtChAddr;
    Button btnUpdate;
    TextView tvUser3;
    RequestQueue queue;
    StringRequest request;

    User vo = UserInfo.info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_umb_user_update);
        tvUser3 = findViewById(R.id.tvUser3);
        edtChPw = findViewById(R.id.edtChPw);
        edtChNick = findViewById(R.id.edtChNick);
        edtChEmail = findViewById(R.id.edtChEmail);
        edtChAddr = findViewById(R.id.edtChAddr);
        btnUpdate = findViewById(R.id.btnUpdate);

        tvUser3.setText(vo.getUser_id());

        queue = Volley.newRequestQueue(UmbUserUpdateActivity.this);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int method = Request.Method.POST;
                String server_url = "http://220.80.203.18:8081/myapp/Android/Update";

                request = new StringRequest(
                        method,
                        server_url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(UmbUserUpdateActivity.this,
                                        "수정 성공" + response,
                                        Toast.LENGTH_SHORT).show();
                                Log.d("dddd", vo.toString());
                                Intent intent = new Intent(UmbUserUpdateActivity.this, UmbMypageActivity.class);

                                startActivity(intent);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(UmbUserUpdateActivity.this,
                                        "수정 실패!" + error.toString(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                ) {
                    @NonNull
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> param = new HashMap<>();
                        param.put("user_id", vo.getUser_id());
                        param.put("user_pw", edtChPw.getText().toString());
                        param.put("user_nick", edtChNick.getText().toString());
                        param.put("user_email", edtChEmail.getText().toString());
                        param.put("user_addr", edtChAddr.getText().toString());

                        return param;
                    }
                }; //end
                queue.add(request);
            }
        });
    }
}