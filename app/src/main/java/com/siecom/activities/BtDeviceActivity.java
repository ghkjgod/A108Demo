package com.siecom.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;

import com.siecom.adapter.DeviceItemAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class BtDeviceActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {


    public RecyclerView mRecyclerView;
    public LinearLayoutManager mLayoutManager;

    public List<BluetoothDevice> dataset;
    public DeviceItemAdapter mAdapter;
    public SwipeRefreshLayout mSwipeRefreshWidget;
    private BluetoothReciever _bluetoothReceive = null;

    private BluetoothAdapter localBluetoothAdapter;
    private final String STATE_CHANGED = "android.bluetooth.adapter.action.STATE_CHANGED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(getResources().getString(R.string.bt_scan));
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);

        mRecyclerView = (RecyclerView) findViewById(R.id.device_item_list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mSwipeRefreshWidget = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget);
        mSwipeRefreshWidget.setOnRefreshListener(this);

        localBluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        if (!(localBluetoothAdapter.isEnabled()))
            startActivity(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"));

        initBt();


    }

    private void startFindBt(){
        if (localBluetoothAdapter != null && !localBluetoothAdapter.isDiscovering()){
            Log.e("start","find");
            localBluetoothAdapter.startDiscovery();
        }
    }
    public void initBt(){
        if(dataset == null)
            dataset = new ArrayList<>();
        bluetoochAdapterMgr();
        if(mAdapter==null){
            mAdapter = new DeviceItemAdapter(this,dataset);
            mRecyclerView.setAdapter(mAdapter);
        }
        mAdapter.notifyDataSetChanged();

        this._bluetoothReceive = new BluetoothReciever();

        InitializeBluetoochAdapter();
    }

    @Override
    public void onRefresh() {
        Snackbar.make(mSwipeRefreshWidget, getResources().getString(R.string.btScan), Snackbar.LENGTH_LONG).setAction("Action", null).show();
        startFindBt();
    }

    // 用广播接收器来更新蓝牙扫描的状态
    private class BluetoothReciever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent
                    .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            switch (action){
                case BluetoothDevice.ACTION_FOUND:

                    boolean isPresence = false;
                    try {
                        for(int i =0;i < dataset.size();i++){
                            if(dataset.get(i).getAddress().equals(device.getAddress())){
                                isPresence = true;
                                break;
                            }else{
                                isPresence = false;
                            }
                        }
                        if(!isPresence){
                            dataset.add(device);
                            mAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                break;

                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:

                    Log.e("bt","ACTION_BOND_STATE_CHANGED");
                    if(device.getBondState() == BluetoothDevice.BOND_BONDED) {
                        Snackbar.make(mSwipeRefreshWidget, device.getName() + getResources().getString(R.string.btBond) + getResources().getString(R.string.please_click), Snackbar.LENGTH_LONG).setAction("Action", null).show();

                    }
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Log.e("bt","ACTION_DISCOVERY_FINISHED");
                    mSwipeRefreshWidget.setRefreshing(false);

                    break;
                case STATE_CHANGED:

                    initBt();

                    break;

            }

        }
    }
    private void InitializeBluetoochAdapter() {

        IntentFilter localIntentFilter = new IntentFilter();
        localIntentFilter.addAction("android.bluetooth.device.action.FOUND");
        localIntentFilter
                .addAction("android.bluetooth.device.action.BOND_STATE_CHANGED");
        localIntentFilter
                .addAction("android.bluetooth.adapter.action.SCAN_MODE_CHANGED");
        localIntentFilter
                .addAction("android.bluetooth.adapter.action.STATE_CHANGED");
        localIntentFilter
                .addAction("android.bluetooth.device.action.PAIRING_REQUEST");
        localIntentFilter
                .addAction("android.bluetooth.adapter.action.DISCOVERY_STARTED");
        localIntentFilter
                .addAction("android.bluetooth.adapter.action.DISCOVERY_FINISHED");
        this.registerReceiver(this._bluetoothReceive, localIntentFilter);

    }

    private void bluetoochAdapterMgr() {

        if (localBluetoothAdapter != null) {

            Set<?> localSet = localBluetoothAdapter.getBondedDevices();
            dataset.clear();
            if (localSet.size() > 0) {
                int i = 0;
                Iterator<?> localIterator = localSet.iterator();
                while (true) {
                    if (!(localIterator.hasNext()))
                        return;
                    BluetoothDevice localBluetoothDevice = (BluetoothDevice) localIterator
                            .next();
                    Log.i("BLEDevice", "Paired devices [" + i + "]: name->"
                            + localBluetoothDevice.getName() + ", address->"
                            + localBluetoothDevice.getAddress());

                    dataset.add(localBluetoothDevice);
                }
            }

        }
    }

}
