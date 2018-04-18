package com.example.legendpeng.controlcar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnTouchListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class ControlActivity extends Activity {

    boolean isConnect = true;
    private Button btn_abort;
    private TextView wifinameb;
    private String wifiname;
    private ImageButton ibtn_up, ibtn_down, ibtn_left, ibtn_right;
    private ImageButton signal;

    private InputStream inputStream=null;//定义输入流
    private OutputStream outputStream=null;//定义输出流

    private String iadress;

    private  Handler mHandler;
    private final  int pause=0;
    private final  int qian=1;
    private final  int hou=2;
    private final  int zuo =3;
    private final  int you=4;

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
        btn_abort = (Button) findViewById(R.id.btn_abort);
        ibtn_down = (ImageButton) findViewById(R.id.ib_down);
        ibtn_up = (ImageButton) findViewById(R.id.ib_up);
        ibtn_left = (ImageButton) findViewById(R.id.ib_left);
        ibtn_right = (ImageButton) findViewById(R.id.ib_right);
        signal = (ImageButton) findViewById(R.id.imageButton3);
        wifinameb = (TextView) findViewById(R.id.textView5);

        wifiname = getIntent().getStringExtra("wifinamessid");
        iadress = getIntent().getStringExtra("adress");

        wifinameb.setText(wifiname);



        new Customthread().start();
        ibtn_up.setOnTouchListener(forwordButtonTouch);
        ibtn_down.setOnTouchListener(backButtonTouch);
        ibtn_left.setOnTouchListener(leftButtonTouch);
        ibtn_right.setOnTouchListener(righButtonTouch);



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


    }



        //qian
        private View.OnTouchListener forwordButtonTouch = new View.OnTouchListener() {
            public android.util.Log log;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
//                        try {
//
//
//                            outputStream = socket.getOutputStream();
//                            outputStream.write("0".getBytes());
//                            outputStream.flush();
//                           Toast.makeText(ControlActivity.this,"qian",Toast.LENGTH_SHORT).show();
//                            log.d("outputStream",outputStream.toString());
//
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        break;
                        //Toast.makeText(ControlActivity.this,"qian",Toast.LENGTH_SHORT).show();
                        mHandler.obtainMessage(qian,"1").sendToTarget();
                        ibtn_up.setBackgroundResource(R.drawable.up_pressedplan);
                        signal.setBackgroundResource(R.drawable.red);
                        break;
                    case MotionEvent.ACTION_UP:
//                        try {
//                            outputStream = socket.getOutputStream();
//                            outputStream.write("5".getBytes());
//                            outputStream.flush();
//                            Toast.makeText(ControlActivity.this,"qian",Toast.LENGTH_SHORT).show();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                        //mHandler.obtainMessage(hou,"1").sendToTarget();
                        mHandler.obtainMessage(pause,"0").sendToTarget();
                        signal.setBackgroundResource(R.drawable.green);
                    default:
                        break;
                }

                return false;
            }
        };
        //hou
        private View.OnTouchListener backButtonTouch = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
//                        try {
//                            outputStream = socket.getOutputStream();
//                            outputStream.write("1".getBytes());
//
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                        mHandler.obtainMessage(hou,"2").sendToTarget();
                        ibtn_down.setBackgroundResource(R.drawable.down_pressedplan);
                        signal.setBackgroundResource(R.drawable.red);
                        break;
                    case MotionEvent.ACTION_UP:
//                        try {
//                            outputStream = socket.getOutputStream();
//                            outputStream.write("5".getBytes());
//
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                        mHandler.obtainMessage(pause,"0").sendToTarget();
                        signal.setBackgroundResource(R.drawable.green);
                    default:
                        break;
                }

                return false;
            }
        };
        //zuo
        private View.OnTouchListener leftButtonTouch = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
//                        try {
//                            outputStream = socket.getOutputStream();
//                            outputStream.write("2".getBytes());
//
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                        mHandler.obtainMessage(zuo,"3").sendToTarget();
                        ibtn_left.setBackgroundResource(R.drawable.left_pressedplan);
                        signal.setBackgroundResource(R.drawable.red);
                        break;
                    case MotionEvent.ACTION_UP:
//                        try {
//                            outputStream = socket.getOutputStream();
//                            outputStream.write("6".getBytes());
//
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                        mHandler.obtainMessage(pause,"0").sendToTarget();
                        signal.setBackgroundResource(R.drawable.green);
                    default:
                        break;
                }

                return false;
            }
        };
        //you
        private View.OnTouchListener righButtonTouch = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
//
                        mHandler.obtainMessage(you,"4").sendToTarget();
                        ibtn_right.setBackgroundResource(R.drawable.right_pressedplan);
                        signal.setBackgroundResource(R.drawable.red);
                        break;
                    case MotionEvent.ACTION_UP:
//                     
                        mHandler.obtainMessage(pause,"0").sendToTarget();
                        signal.setBackgroundResource(R.drawable.green);
                        break;
                    default:
                        break;
                }

                return false;
            }
        };



    private class Customthread extends Thread {

        private Socket socket;

        public void run(){

            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            //判断wifi是否开启
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();

            final String ip = intToIp(ipAddress);


            try {
                socket = new Socket("192.168.4.1",8080);
            } catch (IOException e) {
                e.printStackTrace();
            }


            Looper.prepare();
            final Socket finalSocket = socket;

            mHandler=new Handler(){
                public  void handleMessage(Message msg){
                    switch (msg.what){
                        case 0:
                            try {
                                outputStream= finalSocket.getOutputStream();
                                outputStream.write("0".getBytes());
                                outputStream.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        case 1:
                            try { 
                                outputStream= finalSocket.getOutputStream();
                                outputStream.write("1".getBytes());
                                outputStream.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        case 2:
                            try {
                                outputStream= finalSocket.getOutputStream();
                                outputStream.write("2".getBytes());
                                outputStream.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        case 3:
                            try {
                                outputStream= finalSocket.getOutputStream();
                                outputStream.write("3".getBytes());
                                outputStream.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        case 4:
                            try {
                                outputStream= finalSocket.getOutputStream();
                                outputStream.write("4".getBytes());
                                outputStream.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                    }
                }
            };
            Looper.loop();
        }

        private String intToIp(int i) {

            return (i & 0xFF) + "." +
                    ((i >> 8) & 0xFF) + "." +
                    ((i >> 16) & 0xFF) + "." +
                    (i >> 24 & 0xFF);
        }
    }
}

