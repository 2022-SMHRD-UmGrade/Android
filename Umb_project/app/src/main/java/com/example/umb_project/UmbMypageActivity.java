package com.example.umb_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.umb_project.info.UserInfo;
import com.example.umb_project.vo.User;

public class UmbMypageActivity extends AppCompatActivity {

    TextView infoId, infoName, infoNick, infoEmail, infoPhone,
            infoJoindate, infoAddr, infoType, infoStatus, infoPoint;

    RequestQueue queue;
    StringRequest request;

    Button btnUtpage;

    User vo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_umb_mypage);

        infoId = findViewById(R.id.infoId);
        infoName = findViewById(R.id.infoName);
        infoNick = findViewById(R.id.infoNick);
        infoEmail = findViewById(R.id.infoEmail);
        infoPhone = findViewById(R.id.infoPhone);
        infoJoindate = findViewById(R.id.infoJoindate);
        infoAddr = findViewById(R.id.infoAddr);
        infoType = findViewById(R.id.infoType);
        infoStatus = findViewById(R.id.infoStatus);
        infoPoint = findViewById(R.id.infoPoint);
        btnUtpage = findViewById(R.id.btnUtpage);

        queue = Volley.newRequestQueue(UmbMypageActivity.this);

        vo = UserInfo.info;

        infoId.setText(vo.getUser_id());
        infoName.setText(vo.getUser_name());
        infoNick.setText(vo.getUser_nick());
        infoEmail.setText(vo.getUser_email());
        infoPhone.setText(vo.getUser_phone());
        infoJoindate.setText(vo.getUser_joindate());
        infoAddr.setText(vo.getUser_addr());
        infoType.setText(vo.getUser_type());
        infoStatus.setText(vo.getUser_status());
        infoPoint.setText(vo.getUser_point());

        Log.d("ddddddd", vo.toString());
        btnUtpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vo = UserInfo.info;
                Log.d("ddd", vo.toString());
                Intent intent = new Intent(UmbMypageActivity.this, UmbUserUpdateActivity.class);
                startActivity(intent);
            }
        });
    }
}