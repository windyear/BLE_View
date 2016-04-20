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
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by keng on 2016/4/7.
 */
public class MapActivity extends MainActivity{

    private Handler handler;
    private int count = 0;
    private int POINT_Size;
    private int Point_Type=4;
    private int pre_pointX=570;
    private int pre_pointY=130;
    private int pointX = 570;
    private int pointY = 130;
    private int RSSI1=0;           //用于记录rssi
    private int RSSI2=0;
    private int MoveType = 1;    //定义区分移动a的类型
    private boolean Index = true;  //判断是否发生移动
    private int xSpeed = 0;
    private int ySpeed = 0;
    private String str1,str2,str3;//内置蓝牙设备地址
    private BluetoothDevice device1;//三个蓝牙设备对象
    private BluetoothDevice device2;
    private BluetoothDevice device3;
    private BluetoothGatt mbluetootGatt1;//三个低功耗蓝牙连接对象
    private BluetoothGatt mbluetootGatt2;
    private BluetoothGatt mbluetootGatt3;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();//蓝牙适配器
    private RssiThread rssiThread;//不断读取rssi的线程
    private int flag=0;
    private int mark=0;//判断是否已经连接上蓝牙
    private SensorManager mSensorManager;//电子罗盘管理器
    private int direction;//表示电子罗盘的方向1电梯方向（290），2代表426（200），3代表反电梯方向（110），4代表425（20）
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
        /*str1="00:15:83:00:3D:13";
        str2="00:15:83:00:40:D9";
        str3="00:15:83:00:3D:B2";*/
        str1="20:91:48:32:21:45";
        str2="20:91:48:31:D0:97";
        str3="20:91:48:32:23:30";
        flag++;
        setContentView(mapView);
       // connectBluetooth();
       // rssiThread=new RssiThread();
       // rssiThread.start();

        //获取电子罗盘管理器
        SensorManager mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
         /* 取得需要的Sensor，并注册SensorEventListener */
        mSensorManager.registerListener(mSensorEventListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_NORMAL);


        //添加一个图片单击事件
       /* mapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (flag) {
                    case 1:
                        device1 = bluetoothAdapter.getRemoteDevice(str1);
                        mbluetootGatt1 = device1.connectGatt(MapActivity.this, true, gattCallback);

                        break;
                    case 2:
                        device2 = bluetoothAdapter.getRemoteDevice(str2);
                        mbluetootGatt2 = device2.connectGatt(MapActivity.this, true, gattCallback);
                        break;
                   /* case 3:
                        device3 = bluetoothAdapter.getRemoteDevice(str3);
                        mbluetootGatt3 = device3.connectGatt(MapActivity.this, true, gattCallback);
                        break;
                    case 4:
                        rssiThread = new RssiThread();
                        rssiThread.start();
                        break;
                    default:
                        break;
                }
            }
        });*/

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0x123) {
                    mapView.invalidate();
                }
                if(msg.what==0x1234){
                    Log.i("MainActivity","RSSI1="+RSSI1+"RSSI2="+RSSI2);
                    if(RSSI1>=-52){
                        if(Point_Type>=2&&Point_Type<=6)
                        {
                            pointY=497;       //4
                            Point_Type=4;
                            Log.i("MainActivity","4");
                        }
                        else{
                            Log.i("MainActivity","波动大,去除");
                        }
                    }

                    if(RSSI1>=-58&&RSSI1<=-53){        //3,5
                        if(Point_Type>=2&&Point_Type<=6)
                        {
                            pointY=497;       //4
                            Point_Type=4;
                            Log.i("MainActivity","3,5");
                        }
                        else{
                            Log.i("MainActivity","波动大,去除");
                        }
                    }
                    if(RSSI1>=-61&&RSSI1<=-59){
                        if(RSSI2>=-68){
                            if(Point_Type>=4&&Point_Type<=7)
                            {
                                pointY=707;               //6
                                Point_Type=6;
                                Log.i("MainActivity","6");
                            }
                            else{
                                Log.i("MainActivity","波动大,去除");
                            }
                        }else {
                            if(Point_Type<=4)
                            {
                                pointY=287;              //2
                                Point_Type=2;
                                Log.i("MainActivity","2");
                            }
                            else{
                                Log.i("MainActivity","波动大,去除");
                            }
                        }
                    }
                    if(RSSI1>=-64&&RSSI1<=-62){
                       if(RSSI2>=-68){
                           if(Point_Type<=4)
                           {
                               pointY=182;                //1
                               Point_Type=1;
                               Log.i("MainActivity","1");
                           }
                           else{
                               Log.i("MainActivity","波动大,去除");
                           }
                       }else {
                           if(Point_Type>=6&&Point_Type<=8)
                           {
                               pointY=812;                  //7
                               Point_Type=7;
                               Log.i("MainActivity","7");
                           }
                           else{
                               Log.i("MainActivity","波动大,去除");
                           }
                       }
                    }
                    if(RSSI2>=-52){           //10
                        if(Point_Type>=8&&Point_Type<=12){
                            pointY=1127;
                            Point_Type=10;
                            Log.i("MainActivity","10");
                        }
                        else{
                            Log.i("MainActivity","波动大,去除");
                        }
                    }
                    if(RSSI2<=-53&&RSSI2>=-57){              //9,11
                        if(Point_Type>=8&&Point_Type<=12){
                            pointY=1127;
                            Point_Type=10;
                            Log.i("MainActivity","10");
                        }
                        else{
                            Log.i("MainActivity","波动大,去除");
                        }
                    }
                    if(RSSI2<=-58&&RSSI2>=-60){
                        if(RSSI1>=-68){
                            if(Point_Type>=7&&Point_Type<=10){
                                pointY=917;           //8
                                Point_Type=8;
                                Log.i("MainActivity","8");
                            }
                            else{
                                Log.i("MainActivity","波动大,去除");
                            }
                        }else{
                            if(Point_Type>=10&&Point_Type<=13){
                                pointY=1337;          //12
                                Point_Type=12;
                                Log.i("MainActivity","12");
                            }
                            else{
                                Log.i("MainActivity","波动大,去除");
                            }
                        }
                    }
                    if(RSSI2<=-61&&RSSI2>=-64){
                        if(RSSI1>=-68){
                            if(Point_Type>=6&&Point_Type<=8)
                            {
                                pointY=812;           //7
                                Point_Type=7;
                                Log.i("MainActivity","7");
                            }
                            else{
                                Log.i("MainActivity","波动大,去除");
                            }
                        }else{
                            if(Point_Type>=10)
                            {
                                pointY=1442;          //13
                                Point_Type=13;
                                Log.i("MainActivity","13");
                            }
                            else{
                                Log.i("MainActivity","波动大,去除");
                            }
                        }
                    }
                }
            }
        };

        /*mapView.setOnKeyListener(new View.OnKeyListener() {
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
        });*/

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (count == 5) {
                    count = 0;
                    xSpeed=(pointX-pre_pointX)/8;
                    ySpeed=(pointY-pre_pointY)/8;
                    if(mark>=2){
                        mbluetootGatt1.readRemoteRssi();
                        mbluetootGatt2.readRemoteRssi();
                    }
                    } else {
                        count++;
                        pre_pointX=pre_pointX+xSpeed;
                        pre_pointY=pre_pointY+ySpeed;
                        }
                handler.sendEmptyMessage(0x123);
            }
        }, 0, 100);

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
            canvas.drawCircle(pre_pointX, pre_pointY, POINT_Size, paint);
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
             Log.i("MainActivity", "回调函数已经调用");
            //Toast.makeText(MapActivity.this, "已连接", Toast.LENGTH_SHORT).show();
            super.onConnectionStateChange(mBluetoothGatt, status, newState);
            if (newState == BluetoothProfile.STATE_CONNECTED)
            {
                //连接成功, 可以把这个gatt 保存起来, 需要读rssi的时候就
                flag++;
                Log.i("MainActivity", "回调函数已经调用"+flag);
                //Toast.makeText(MapActivity.this, "已连接", Toast.LENGTH_SHORT).show();
                mark++;
            }
        }
        @Override
        //底层获取RSSI后会回调这个函数
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            /*if(gatt==mbluetootGatt1){
                Log.i("MainActivity",""+(200+rssi));}
            else if(gatt==mbluetootGatt2){
                Log.i("MainActivity",""+(400+rssi));
            }else{
                Log.i("MainActivity",""+(600+rssi));
            }*/
            if(gatt==mbluetootGatt1) {
                RSSI1 = rssi;
            }
            if(gatt==mbluetootGatt2) {
                RSSI2 = rssi;
            }
            handler.sendEmptyMessage(0x1234);
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

    private final SensorEventListener mSensorEventListener = new SensorEventListener()
    {
        public void onAccuracyChanged(Sensor sensor, int accuracy)    {       }
        public void onSensorChanged(SensorEvent event)    {
/* 判断Sensor的种类 */
            if (event.sensor.getType() == Sensor.TYPE_ORIENTATION)      {
/* 取得X值资料
 * x_data是水平值
 * 由于分成8个方向，所以每个方向为45°
 * */
                float x_data = event.values[SensorManager.DATA_X];
//245到335为电梯方向
                if (x_data > 245 && x_data <= 335)        {
                    direction=1;
                    Log.i("MainActivity","电梯方向" + String.valueOf(x_data));}
                //245到335为电梯方向
                if (x_data > 155 && x_data <= 245)        {
                    direction=2;
                    Log.i("MainActivity","426方向" + String.valueOf(x_data));}
                //65到155为电梯方向
                if (x_data > 65 && x_data <= 155)        {
                    direction=3;
                    Log.i("MainActivity","反电梯方向" + String.valueOf(x_data));}
                //335到65为电梯方向
                if (x_data > 335 || x_data <= 65)        {
                    direction=4;
                    Log.i("MainActivity","电梯方向" + String.valueOf(x_data));}
     }    }  };
}
