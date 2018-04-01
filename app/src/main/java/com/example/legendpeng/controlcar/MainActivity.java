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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.support.v7.app.AlertDialog;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {


    //初始化ToggleButton
    private ToggleButton tb_switch;
    //初始化ListView
    private ListView lv_wifi;
    //初始化button
    private Button btn_start;

    //ListView中的内容
    private String[] data = { "Apple", "Banana", "Orange", "Watermelon",
            "Pear", "Grape", "Pineapple", "Strawberry", "Cherry", "Mango" };


    private ListView mlistView;
    protected WifiAdmin mWifiAdmin;
    private List<ScanResult> mWifiList;
    public int level;
    protected String ssid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化list view
        //设置ListView的具体内容 (若按下ToggleButton，就将数组中的数据放入listview）
//        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//                MainActivity.this, android.R.layout.simple_list_item_1, data);
//        lv_wifi = (ListView)findViewById(R.id.lv_wifi);


        mWifiAdmin = new WifiAdmin(MainActivity.this);



        initViews();
//        //设置ToggleButton监听事件
//        btn_start=(Button) findViewById(R.id.btn_start);
//        btn_start.setOnClickListener((View.OnClickListener)MainActivity.this);
//        btn_start.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(MainActivity.this,"Connecting...",Toast.LENGTH_LONG).show();
//                //跳转到ControlActivity
//                Intent intent =new Intent(MainActivity.this,ControlActivity.class);
//                //启动
//                startActivity(intent);
//            }
//        });
//        //设置button的监听事件
//        tb_switch=(ToggleButton)findViewById(R.id.tb_switch);
//        tb_switch.setOnFocusChangeListener((View.OnFocusChangeListener) MainActivity.this);

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
     * init the control button, find the button in xml,and set listener.the button include a button
     * and a togglebutton
     * */
    private void initViews() {
        //设置ToggleButton监听事件
        btn_start=(Button) findViewById(R.id.btn_start);
        btn_start.setOnClickListener((View.OnClickListener)MainActivity.this);
        //设置button的监听事件
        tb_switch=(ToggleButton)findViewById(R.id.tb_switch);
        tb_switch.setOnFocusChangeListener((View.OnFocusChangeListener) MainActivity.this);

    }

// do what when button click. the btn_start is start the control UI. the tb_switch is togglebutton,when it is
// open ,check wifi state ,and open wifi ,scan wifi inf.
    public void onClick(View v ) {
        switch (v.getId()) {
            case R.id.btn_start:
                Toast.makeText(MainActivity.this,"Connecting...",Toast.LENGTH_LONG).show();
                //跳转到ControlActivity
                Intent intent =new Intent(MainActivity.this,ControlActivity.class);
                //启动
                startActivity(intent);
                break;
            case R.id.tb_switch:
                tb_switch.setOnCheckedChangeListener(new  CompoundButton.OnCheckedChangeListener(){
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        //Toast.makeText(MainActivity.this,"ischecked?"+isChecked, Toast.LENGTH_SHORT).show();
                        if(isChecked == true){
                            //向listview中插入数据
                            //lv_wifi.setAdapter((ListAdapter) adapter);
                            mWifiAdmin.openWifi(MainActivity.this);
                            mWifiAdmin.startScan(MainActivity.this);
                            mWifiList=mWifiAdmin.getWifiList();
                            if(mWifiList!=null){
                                mlistView.setAdapter(new MyAdapter(this,mWifiList));
                                new Utility().setListViewHeightBasedOnChildren(mlistView);
                            }
                        }
                        else{
                            //清除listview中的内容
                            lv_wifi.setAdapter(null);
                            mWifiAdmin.closeWifi(MainActivity.this);

                        }
                    }
                });
                break;


//            case R.id.scan_wifi:
//                mWifiAdmin.startScan(MainActivity.this);
//                mWifiList=mWifiAdmin.getWifiList();
//                if(mWifiList!=null){
//                    mlistView.setAdapter(new MyAdapter(this,mWifiList));
//                    new Utility().setListViewHeightBasedOnChildren(mlistView);
//                }
//                break;
            default:
                break;
        }
    }
// set the adapter.want to get the view that every Item
//    sdasd
    public  class MyAdapter extends BaseAdapter{

        LayoutInflater inflater;
        List<ScanResult> list;
        public MyAdapter(CompoundButton.OnCheckedChangeListener context, List<ScanResult> list){
            this.inflater=LayoutInflater.from((Context) context);
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

            view=inflater.inflate(R.layout.activity_main, null);
            ScanResult scanResult = list.get(position);
            //wifi_ssid.setText(scanResult.SSID);
            //Log.i(TAG, "scanResult.SSID="+scanResult);
            //level=WifiManager.calculateSignalLevel(scanResult.level,5);
           // if(scanResult.capabilities.contains("WEP")||scanResult.capabilities.contains("PSK")||
            //        scanResult.capabilities.contains("EAP"))
            //wifi_level.setImageLevel(level);
            //判断信号强度，显示对应的指示图标
            return view;
        }
    }

    /*设置listview的高度*/
    //set the weight of the listview
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
    //listen the wifi state.if is connected ,make a toast. in main, check this.
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

