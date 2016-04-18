package com.example.user.ble_view;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by keng on 2016/4/7.
 */
public class MapActivity extends MainActivity{

    private int count = 0;
    private int POINT_Size;
    private int pointX = 380;
    private int pointY = 100;
    private int MoveType = 1;    //定义区分移动a的类型
    private boolean Index = true;  //判断是否发生移动
    private int xSpeed = 7;
    private int ySpeed = 7;
    private String str1,str2,str3;//内置蓝牙设备地址
    private BluetoothDevice device1;//三个蓝牙设备对象
    private BluetoothDevice device2;
    private BluetoothDevice device3;
    private BluetoothGatt mbluetootGatt1;//三个低功耗蓝牙连接对象
    private BluetoothGatt mbluetootGatt2;
    private BluetoothGatt mbluetootGatt3;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();//蓝牙适配器
    private RssiThread rssiThread;//不断读取rssi的线程
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //获取窗口管理器
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        final MapView mapView = new MapView(this);

        POINT_Size = 10;

        setContentView(mapView);
        connectBluetooth();
        rssiThread=new RssiThread();
        rssiThread.start();

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0x123) {
                    mapView.invalidate();
                }
            }
        };

        mapView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (event.getKeyCode()) {
                    case KeyEvent.KEYCODE_B: {
                        Intent intent = new Intent(MapActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    }
                    case KeyEvent.KEYCODE_A: {
                        Index=true;
                        MoveType=1;
                        break;
                    }
                    case KeyEvent.KEYCODE_S: {
                        Index=true;
                        MoveType=4;
                        break;
                    }
                    case KeyEvent.KEYCODE_D: {
                        Index=true;
                        MoveType=2;
                        break;
                    }
                    case KeyEvent.KEYCODE_W: {
                        Index=true;
                        MoveType=3;
                        break;
                    }
                }
                return true;
            }
        });

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (Index == true) {
                    if (count == 18) {
                        count = 0;
                        Index = false;
                    } else {
                        count++;
                        switch (MoveType) {
                            case 1:  //向左运动
                            {
                                pointX = pointX - xSpeed;
                                break;
                            }
                            case 2:  //向右运动
                            {
                                pointX = pointX + xSpeed;
                                break;
                            }
                            case 3:  //向上运动
                            {
                                pointY = pointY - ySpeed;
                                break;
                            }
                            case 4:  //向下运动
                            {
                                pointY = pointY + ySpeed;
                                break;
                            }
                        }
                    }
                } else {

                }
                handler.sendEmptyMessage(0x123);
            }
        }, 0, 200);

    }

    class MapView extends View {

        Paint paint = new Paint();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.map);
        Matrix matrix = new Matrix();

        public MapView(Context context) {
            super(context);
            setFocusable(true);
        }

        public void onDraw(Canvas canvas) {
            matrix.setScale(0.6f, 1.0f);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawBitmap(bitmap, matrix, null);
            paint.setColor(Color.rgb(255, 0, 0));
            canvas.drawCircle(pointX, pointY, POINT_Size, paint);
        }
    }
    //写一个函数启动蓝牙连接
    public void connectBluetooth(){
        str1="00:15:83:00:3D:13";
        str2="00:15:83:00:40:D9";
        str3="00:15:83:00:3D:B2";
        device1 = bluetoothAdapter.getRemoteDevice(str1);
        mbluetootGatt1=device1.connectGatt(MapActivity.this, true, gattCallback);
        device2 = bluetoothAdapter.getRemoteDevice(str2);
        mbluetootGatt2=device2.connectGatt(MapActivity.this, true, gattCallback);
        device3 = bluetoothAdapter.getRemoteDevice(str3);
        mbluetootGatt3=device3.connectGatt(MapActivity.this, true, gattCallback);

    }
    //这个是蓝牙连接回调函数
    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback()
    {
        @Override
        public void onConnectionStateChange(BluetoothGatt mBluetoothGatt, int status, int newState)
        {
            //设备连接状态改变会回调这个函数
            // Log.v(TAG, "回调函数已经调用");
            super.onConnectionStateChange(mBluetoothGatt, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTED)
            {
                //连接成功, 可以把这个gatt 保存起来, 需要读rssi的时候就
                Log.v("MainActivity", "回调函数已经调用");
            }
        }
        @Override
        //底层获取RSSI后会回调这个函数
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            if(gatt==mbluetootGatt1){
                Log.v("MainActivity",""+(200+rssi));}
            else if(gatt==mbluetootGatt2){
                Log.v("MainActivity",""+(400+rssi));
            }else{
                Log.v("MainActivity",""+(600+rssi));
            }
        }
    };
    //内部线程类
    public class RssiThread extends Thread {
        @Override
        public void run() {
            Log.v("MainActivity","读取一次三个设备的的RSSI");
            // while(true){
            for(int i=0;i<300;i++){
                mbluetootGatt1.readRemoteRssi();
                mbluetootGatt2.readRemoteRssi();
                mbluetootGatt3.readRemoteRssi();
                try {
                    Thread.currentThread().sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
