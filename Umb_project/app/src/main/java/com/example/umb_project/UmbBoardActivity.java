package com.example.umb_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UmbBoardActivity extends AppCompatActivity {

    Button btnItContent, btnCmt;
    User vo;
    Board dto;

    RequestQueue queue;
    StringRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_umb_board);

        btnItContent = findViewById(R.id.btnItContent);
        btnCmt = findViewById(R.id.btnCmt);

        queue = Volley.newRequestQueue(UmbBoardActivity.this);

        vo = UserInfo.info;

        int method = Request.Method.POST;
        String server_url = "http://220.80.203.18:8081/myapp/BoardList.do";

        request = new StringRequest(
                method,
                server_url,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(UmbBoardActivity.this,
                                "게시판 불러오기 성공",
                                Toast.LENGTH_SHORT).show();
                        try {
                            JSONArray boardArray = new JSONArray(response);

                            for(int i=0; i<boardArray.length(); i++) {
                                JSONObject Object = boardArray.getJSONObject(i);
                                Log.d("seq", Object.getString("article_seq"));
                                Log.d("title", Object.getString("article_title"));
                                Log.d("content", Object.getString("article_content"));
                                Log.d("date", Object.getString("article_date"));
                                Log.d("file", Object.getString("article_file"));
                                Log.d("id", Object.getString("article_id"));
                                Log.d("cnt", Object.getString("article_cnt"));

                                int seq = Integer.parseInt(Object.getString("article_seq"));
                                String title = Object.getString("article_title");
                                String content = Object.getString("article_content");
                                String date = Object.getString("article_date");
                                String file = Object.getString("article_file");
                                String id = Object.getString("article_id");
                                int cnt = Integer.parseInt(Object.getString("article_cnt"));

                                /*list.add(new Board(seq,title,content,date,file,id,cnt));

                                lvBoard.setAdapter(adapter);

                                adapter.notifyDataSetChanged();
*/                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UmbBoardActivity.this,
                                "게시판 불러오기 실패!" + error.toString(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
        ); //end

        queue.add(request);


        btnItContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vo = UserInfo.info;

                Intent intent = new Intent(UmbBoardActivity.this, UmbCommunityActivity.class);
                startActivity(intent);
            }
        });

        btnCmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vo = UserInfo.info;
                Log.d("vo2", vo.getUser_id());


            }
        });
    }


}