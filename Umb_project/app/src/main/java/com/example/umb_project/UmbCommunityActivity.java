package com.example.umb_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.example.umb_project.info.BoardInfo;
import com.example.umb_project.info.UserInfo;
import com.example.umb_project.vo.Board;
import com.example.umb_project.vo.User;

import java.util.HashMap;
import java.util.Map;

public class UmbCommunityActivity extends AppCompatActivity {

    TextView tvTitle, tvWriter2;
    EditText edtTitle, edtContent;
    Button btnSave, btnCancel;

    RequestQueue queue;
    StringRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_umb_community);

        tvTitle = findViewById(R.id.tvTitle);
        tvWriter2 = findViewById(R.id.tvWriter2);
        edtTitle = findViewById(R.id.edtTitle);
        edtContent = findViewById(R.id.edtTitle);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);


        queue = Volley.newRequestQueue(UmbCommunityActivity.this);

        User vo = UserInfo.info;

        tvWriter2.setText(vo.getUser_id());

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int method = Request.Method.POST;
                String server_url = "http://220.80.203.18:8081/myapp/Android/InsertContent";

                request = new StringRequest(
                        method,
                        server_url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(UmbCommunityActivity.this,
                                        "등록 성공"+response,
                                        Toast.LENGTH_SHORT).show();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(UmbCommunityActivity.this,
                                        "등록 실패!"+error.toString(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                ){
                    @NonNull
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> param = new HashMap<>();

                        param.put("article_title", edtTitle.getText().toString());
                        param.put("article_content", edtContent.getText().toString());
                        param.put("article_file", "N");
                        param.put("article_id", vo.getUser_id());

                        return param;
                    }
                };//end
                queue.add(request);
            }
        });
    }
}