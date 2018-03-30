package com.example.legendpeng.controlcar;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

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
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    private Button check_wifi,open_wifi,close_wifi,scan_wifi;
    private ListView mlistView;
    protected WifiAdmin mWifiAdmin;
    private List<ScanResult> mWifiList;
    public int level;
    protected String ssid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWifiAdmin = new WifiAdmin(MainActivity.this);
        initViews();
        IntentFilter filter = new IntentFilter(
                WifiManager.NETWORK_STATE_CHANGED_ACTION);
        //="android.net.wifi.STATE_CHANGE"  监听wifi状态的变化
        registerReceiver(mReceiver, filter);
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                AlertDialog.Builder alert=new AlertDialog.Builder(MainActivity.this);
                ssid=mWifiList.get(position).SSID;
                alert.setTitle(ssid);
                alert.setMessage("输入密码");
                final EditText et_password=new EditText(MainActivity.this);
                final SharedPreferences preferences=getSharedPreferences("wifi_password",Context.MODE_PRIVATE);
                et_password.setText(preferences.getString(ssid, ""));
                alert.setView(et_password);
                //alert.setView(view1);
                alert.setPositiveButton("连接", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String pw = et_password.getText().toString();
                        if(null == pw  || pw.length() < 8){
                            Toast.makeText(MainActivity.this, "密码至少8位", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        SharedPreferences.Editor editor=preferences.edit();
                        editor.putString(ssid, pw);   //保存密码
                        editor.commit();
                        mWifiAdmin.addNetwork(mWifiAdmin.CreateWifiInfo(ssid, et_password.getText().toString(), 3));
                    }
                });
                alert.setNegativeButton("取消", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //
                        //mWifiAdmin.removeWifi(mWifiAdmin.getNetworkId());
                    }
                });
                alert.create();
                alert.show();

            }
        });
    }

    /*
     * 控件初始化
     * */
    private void initViews() {
        check_wifi=(Button) findViewById(R.id.check_wifi);
        open_wifi=(Button) findViewById(R.id.open_wifi);
        close_wifi=(Button) findViewById(R.id.close_wifi);
        scan_wifi=(Button) findViewById(R.id.scan_wifi);
        mlistView=(ListView) findViewById(R.id.list);
        check_wifi.setOnClickListener((View.OnClickListener) MainActivity.this);
        open_wifi.setOnClickListener((View.OnClickListener) MainActivity.this);
        close_wifi.setOnClickListener((View.OnClickListener) MainActivity.this);
        scan_wifi.setOnClickListener((View.OnClickListener) MainActivity.this);
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.check_wifi:
                mWifiAdmin.checkState(MainActivity.this);
                break;
            case R.id.open_wifi:
                mWifiAdmin.openWifi(MainActivity.this);
                break;
            case R.id.close_wifi:
                mWifiAdmin.closeWifi(MainActivity.this);
                break;
            case R.id.scan_wifi:
                mWifiAdmin.startScan(MainActivity.this);
                mWifiList=mWifiAdmin.getWifiList();
                if(mWifiList!=null){
                    mlistView.setAdapter(new MyAdapter(this,mWifiList));
                    new Utility().setListViewHeightBasedOnChildren(mlistView);
                }
                break;
            default:
                break;
        }
    }

    public class MyAdapter extends BaseAdapter{
        LayoutInflater inflater;
        List<ScanResult> list;
        public MyAdapter(Context context, List<ScanResult> list){
            this.inflater=LayoutInflater.from(context);
            this.list=list;
        }
        @Override
        public int getCount() {
            return list.size();
        }
        @Override
        public Object getItem(int position) {
            return position;
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @SuppressLint({ "ViewHolder", "InflateParams" })
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view=null;
//            view=inflater.inflate(R.layout.activity_main, null);
//            ScanResult scanResult = list.get(position);
//
//            wifi_ssid.setText(scanResult.SSID);
//            Log.i(TAG, "scanResult.SSID="+scanResult);
//            level=WifiManager.calculateSignalLevel(scanResult.level,5);
//            if(scanResult.capabilities.contains("WEP")||scanResult.capabilities.contains("PSK")||
//                    scanResult.capabilities.contains("EAP"))
//            wifi_level.setImageLevel(level);
//            //判断信号强度，显示对应的指示图标
            return view;
        }
    }

    /*设置listview的高度*/
    public class Utility {
        public void setListViewHeightBasedOnChildren(ListView listView) {
            ListAdapter listAdapter = listView.getAdapter();
            if (listAdapter == null) {
                return;
            }
            int totalHeight = 0;
            for (int i = 0; i < listAdapter.getCount(); i++) {
                View listItem = listAdapter.getView(i, null, listView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
            listView.setLayoutParams(params);
        }
    }
    //监听wifi状态
    private BroadcastReceiver mReceiver = new BroadcastReceiver (){
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if(wifiInfo.isConnected()){
                WifiManager wifiManager = (WifiManager) context
                        .getSystemService(Context.WIFI_SERVICE);
                String wifiSSID = wifiManager.getConnectionInfo()
                        .getSSID();
                Toast.makeText(context, wifiSSID+"连接成功", Toast.LENGTH_SHORT).show();
            }
        }

    };
}

