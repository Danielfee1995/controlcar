package com.example.legendpeng.controlcar;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class ControlActivity extends Activity {

    private Button btn_abort;
    private ImageButton ibtn_up,ibtn_down,ibtn_left,ibtn_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
        // 设置为无标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置为全屏模式
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_control);

        //初始化按钮和ImageButton
        btn_abort = (Button)findViewById(R.id.btn_abort);
        ibtn_down = (ImageButton)findViewById(R.id.ib_down);
        ibtn_up = (ImageButton)findViewById(R.id.ib_up);
        ibtn_left = (ImageButton)findViewById(R.id.ib_left);
        ibtn_right = (ImageButton)findViewById(R.id.ib_right);

        //设置监听
        //终止连接
        btn_abort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ControlActivity.this, MainActivity.class);
                //启动
                startActivity(intent);
            }
        });

        //后退
        ibtn_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ControlActivity.this,"back...",Toast.LENGTH_SHORT).show();
            }
        });

        //前进
        ibtn_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ControlActivity.this,"forward...",Toast.LENGTH_SHORT).show();
            }
        });

        //左
        ibtn_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ControlActivity.this,"left...",Toast.LENGTH_SHORT).show();
            }
        });

        //右
        ibtn_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ControlActivity.this,"right...",Toast.LENGTH_SHORT).show();
            }
        });

    }
}
