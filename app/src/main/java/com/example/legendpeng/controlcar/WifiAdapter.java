package com.example.legendpeng.controlcar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by legendpeng on 2018/4/1.
 */

public class WifiAdapter extends BaseAdapter{

    //
    // LayoutInflater inflater;
    private Context context;
    List<ScanResult> list;


    public WifiAdapter(Context context) {
        //this.inflater=LayoutInflater.from((Context) context);
        this.context = context;
    }

    public int getCount() {
        if(list == null)
            return 0;
        return list.size();
    }

    public ScanResult getItem(int position) {
        //return position;
        return list.get(position);
    }


    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({ "ViewHolder", "InflateParams" })
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=null;
        TextView tv = null;
        if (convertView != null) {
            tv = (TextView)convertView;

        }else{
            TextView tm = new TextView(context);
            View view1=new View(context);
            tv = tm;
            view=view1;

            //tv.setText(listdata.get(arg0));
        }

        tv.setText(list.get(position).SSID.toString());
        tv.setTextSize(25);
        //view=inflater.inflate(R.layout.activity_main, null);
        ScanResult scanResult = list.get(position);

        //wifi_ssid.setText(scanResult.SSID);
        //Log.i(TAG, "scanResult.SSID="+scanResult);
        // level = WifiManager.calculateSignalLevel(scanResult.level, 5);
        //if(scanResult.capabilities.contains("WEP")||scanResult.capabilities.contains("PSK")||
        //       scanResult.capabilities.contains("EAP"))
        // wifi_level.setImageLevel(level);
        //判断信号强度，显示对应的指示图标
        return tv;
    }

    public void addAllWifis(List<ScanResult> list){
        this.list=list;

    }

    public  void updateState(int state,int pos){


    }
}

