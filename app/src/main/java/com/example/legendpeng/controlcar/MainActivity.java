package com.example.legendpeng.controlcar;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import java.util.List;

public class MainActivity extends Activity {

    //初始化ToggleButton
    private ToggleButton tb_switch;
    //初始化button
    private Button btn_start;
    private EditText wifiname;
    private ListView list;
    private WifiAdapter adapter;
    private WiFiAdmin admin;
    private static final int PERMISSION_WIFI_CODE = 1001;
    private static final int PERMISSION_FILE_CODE = 1002;
    private int curPosition = -1;
    private  String adress;
    private  String wifinamessid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
        // 设置为无标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置为全屏模式
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int code = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (code != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_FILE_CODE);
            }
        }

        wifiname=(EditText)findViewById(R.id.et_appliance);
        initList();
        setStateListener();
//        admin.openWifi();
 //       checkPermission();

        //设置ToggleButton监听事件
        tb_switch = (ToggleButton) findViewById(R.id.tb_switch);
        tb_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Toast.makeText(MainActivity.this,"ischecked?"+isChecked, Toast.LENGTH_SHORT).show();
                if (isChecked == true) {
                    //向listview中插入数据

                    admin.openWifi();

                    list.setAdapter(adapter);
                    initList();
                    setStateListener();
                    checkPermission();

//
//                    //list.setAdapter(adapter);
//                    setStateListener();
//                    admin.openWifi();
//                    initList();
//                    checkPermission();


                } else {
                    //清除listview中的内容
                    admin.closeWifi();

                    list.setAdapter(null);

                }
            }
        });

        //设置button的监听事件
        btn_start = (Button) findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "Connecting...", Toast.LENGTH_LONG).show();
                //跳转到ControlActivity
                Intent intent = new Intent(MainActivity.this, ControlActivity.class);
                intent.putExtra("wifiname",wifiname.getText());
                intent.putExtra("adress",adress);
                intent.putExtra("wifinamessid",wifinamessid);

                //启动
                startActivity(intent);


            }
        });
    }

    private void initList() {
        admin = WiFiAdmin.getInstance(this);
        list = (ListView) findViewById(R.id.lv_wifi);
        adapter = new WifiAdapter(this);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                curPosition = position;
                final ScanResult result = adapter.getItem(position);
                if (admin.getConnectInfo().getSSID().equals("\"" + result.SSID + "\"")) {
                    adress=result.BSSID.toString();
                    wifinamessid=result.SSID.toString();
                    wifiname.setText(result.SSID.toString());
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("是否断开连接？")
                            .setPositiveButton("断开", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    admin.disconnectWifi();
                                }
                            })
                            .setNegativeButton("不保存", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    admin.forgetWifi(result.SSID);
                                }
                            })
                            .create().show();
                    return;
                }
                final int security = admin.getSecurity(result);
                int netid = admin.isWifiConfig(result.SSID);
                if (netid == -1) {
                    if (security != WiFiAdmin.SECURITY_NONE) {
                        final EditText pwdEt = new EditText(MainActivity.this);
                        //弹出输入密码对话框
                        new AlertDialog.Builder(MainActivity.this)
                                .setView(pwdEt)
                                .setTitle("请输入密码")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        admin.configWifi(result, pwdEt.getText().toString(), security);
                                        admin.connectWifi(admin.isWifiConfig(result.SSID));
                                        wifiname.setText(result.SSID.toString());
                                        adress=result.BSSID.toString();
                                        wifinamessid=result.SSID.toString();
                                    }
                                })
                                .setNegativeButton("取消", null)
                                .create().show();
                    } else {
                        admin.configWifi(result, "", security);
                        admin.connectWifi(admin.isWifiConfig(result.SSID));
                    }
                } else {
                    admin.connectWifi(netid);
                }

            }
        });
    }
    private void setStateListener() {
        admin.addWifiStateChangeListener(new WiFiAdmin.WifiStateChangeListener() {
            @Override
            public void onSignalStrengthChanged(int level) {

            }

            @Override
            public void onWifiConnecting() {
                adapter.updateState(0,curPosition);
            }

            @Override
            public void onWifiGettingIP() {
                adapter.updateState(1,curPosition);
            }

            @Override
            public void onWifiConnected() {
                adapter.updateState(2,curPosition);
            }

            @Override
            public void onWifiDisconnect() {
                adapter.updateState(4,curPosition);

            }

            @Override
            public void onWifiEnabling() {

            }

            @Override
            public void onWifiEnable() {

            }

            @Override
            public void onPasswordError() {
                adapter.updateState(3,curPosition);
            }

            @Override
            public void onWifiIDChange() {

            }
        });
    }


    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int code = ContextCompat.checkSelfPermission(this, Manifest.permission_group.LOCATION);
            if (code != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_WIFI_CODE);
            } else {
                admin.startWifiScan();
                adapter.addAllWifis(admin.getScanResults());
                list.setAdapter(adapter);
            }
        } else {
            admin.startWifiScan();
            adapter.addAllWifis(admin.getScanResults());
            list.setAdapter(adapter);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        admin.removeWifiStateChangeListener();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_WIFI_CODE:
                if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    admin.startWifiScan();
                    adapter.addAllWifis(admin.getScanResults());
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;

        }
    }

}

