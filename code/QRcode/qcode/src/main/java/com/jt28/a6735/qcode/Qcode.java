package com.jt28.a6735.qcode;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.jt28.a6735.qcode.driver.UsbSerialDriver;
import com.jt28.a6735.qcode.driver.UsbSerialPort;
import com.jt28.a6735.qcode.driver.UsbSerialProber;
import com.jt28.a6735.qcode.util.HexDump;
import com.jt28.a6735.qcode.util.SerialInputOutputManager;
import com.telecp.deviceactioninterface.CodeScanManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by a6735 on 2017/8/7.
 */
public class Qcode extends CodeScanManager {
    private final String TAG = "jt128";
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private List<UsbSerialPort> mEntries = new ArrayList<>();
    private UsbManager mUsbManager;
    private Context m_activity;
    private UsbSerialPort port;
    private PendingIntent mPermissionIntent ;

    //寻找设备
    private void refreshDeviceList() {
        new AsyncTask<Void, Void, List<UsbSerialPort>>() {
            @Override
            protected List<UsbSerialPort> doInBackground(Void... params) {
                Log.d(TAG, "Refreshing device list ...");
                SystemClock.sleep(1000);

                final List<UsbSerialDriver> drivers =
                        UsbSerialProber.getDefaultProber().findAllDrivers(mUsbManager);

                final List<UsbSerialPort> result = new ArrayList<UsbSerialPort>();
                for (final UsbSerialDriver driver : drivers) {
                    final List<UsbSerialPort> ports = driver.getPorts();
                    Log.d(TAG, String.format("+ %s: %s port%s",
                            driver, ports.size(), ports.size() == 1 ? "" : "s"));
                    result.addAll(ports);
                }
                return result;
            }

            @Override
            protected void onPostExecute(List<UsbSerialPort> result) {
                mEntries.clear();
                mEntries.addAll(result);
                //Log.d(TAG, "Done refreshing, " + mEntries.size() + " entries found." + result.get(0).getDriver().getDevice().toString());
                //找到了
                if(mEntries.size() > 0) {
                    port = mEntries.get(0);
                    //Log.d("jt128","跳转");

                    if (port == null) {
                        //mTitleTextView.setText("No serial device.");
                    } else {
                        mUsbManager.requestPermission(port.getDriver().getDevice(), mPermissionIntent);
                    }
                }
            }
        }.execute((Void) null);
    }

    private void onDeviceStateChange() {
        stopIoManager();
        startIoManager();
    }

    private void stopIoManager() {
        if (mSerialIoManager != null) {
            Log.i(TAG, "Stopping io manager ..");
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    private void startIoManager() {
        if (port != null) {
            Log.i(TAG, "开始接收数据 Starting io manager ..");
            mSerialIoManager = new SerialInputOutputManager(port, mListener);
            mExecutor.submit(mSerialIoManager);
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            switch(message.what) {
                case 1:
                    if(Qcode.this.getCodeScanListener() != null) {
                        Qcode.this.getCodeScanListener().onASCIIMessageScanned(HexDump.dumpHexString((byte[]) message.obj));
                    }
                    endScan();
                default:
                    super.handleMessage(message);
            }
        }
    };


    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private SerialInputOutputManager mSerialIoManager;

    private final SerialInputOutputManager.Listener mListener =
            new SerialInputOutputManager.Listener() {

                @Override
                public void onRunError(Exception e) {
                    Log.d(TAG, "Runner stopped. 错误");
                }

                @Override
                public void onNewData(final byte[] data) {
                    Message message = new Message();
                    message.what = 1;
                    message.obj = data;
                    Qcode.this.mHandler.sendMessage(message);
                }
            };

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)){
                synchronized (this) {
                    UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)){
                        if(device != null){
                            if (port == null) {
                                //mTitleTextView.setText("No serial device.");
                            } else {
                                final UsbManager usbManager = (UsbManager) m_activity.getSystemService(Context.USB_SERVICE);
                                UsbDeviceConnection connection = usbManager.openDevice(port.getDriver().getDevice());
                                if (connection == null) {
                                    //mTitleTextView.setText("Opening device failed");
                                    return;
                                }
                                try {
                                    port.open(connection);
                                    port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
                                } catch (IOException e) {
                                    Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
                                    // mTitleTextView.setText("Error opening device: " + e.getMessage());
                                    try {
                                        port.close();
                                    } catch (IOException e2) {
                                        // Ignore.
                                    }
                                    port = null;
                                    return;
                                }
                                onDeviceStateChange();
                                //mTitleTextView.setText("Serial device: " + port.getClass().getSimpleName());
                            }
                        }
                    }else{
                        Log.d(TAG, "permission denied for device " + device);
                    }
                }
            }
        }
    };

    //获取管理器版本
    @Override
    public int getServiceVersion() {
        return 1;
    }
    //初始化管理器返回0x00表示成功其它值失败
    @Override
    public int initDevice(Context context) {
        //this.int_QRcodeAction = (com.telecp.deviceactioninterface.CodeScanActionCallBack) context;
        this.m_activity = context;
        mUsbManager = (UsbManager) m_activity.getSystemService(Context.USB_SERVICE);
        mPermissionIntent = PendingIntent.getBroadcast(m_activity, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        m_activity.registerReceiver(mUsbReceiver, filter);
        return 0;
    }
    //重置设备 不需要
    @Override
    public int resetDevice() {
        refreshDeviceList();
        return 0;
    }
    //释放相关资源 返回0x00表示成功 其它值失败
    @Override
    public int destroyService() {
        return 0;
    }

    @Override
    public int startScan() {
        refreshDeviceList();
        return 0;
    }

    @Override
    public int endScan() {
        stopIoManager();
        return 0;
    }
}
