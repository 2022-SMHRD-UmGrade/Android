package com.example.umb_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.umb_project.info.UserInfo;
import com.example.umb_project.vo.User;

public class UmbBoardActivity extends AppCompatActivity {

    Button btnItContnet;
    User vo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_umb_board);

        btnItContnet = findViewById(R.id.btnItContent);

        vo = UserInfo.info;

        btnItContnet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vo = UserInfo.info;

                Intent intent = new Intent(UmbBoardActivity.this, UmbCommunityActivity.class);
                startActivity(intent);
            }
        });
    }
}