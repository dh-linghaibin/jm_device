package com.jt28.a6735.idcard;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;

import com.telecp.deviceactioninterface.IDCardInfo;
import com.zkteco.android.biometric.core.device.TransportType;
import com.zkteco.android.biometric.module.idcard.IDCardReader;
import com.zkteco.android.biometric.module.idcard.IDCardReaderFactory;
import com.zkteco.android.biometric.module.idcard.exception.IDCardReaderException;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by a6735 on 2017/9/6.
 */

public class Idcard extends com.telecp.deviceactioninterface.IDCardManager  {
    private static final int VID = 1024;
    private static final int PID = 50010;
    private IDCardReader idCardReader = null;
    private boolean isScanning = false;
    private boolean isStarted = false;
    private boolean hasInit = false;
    private boolean destroyed = false;
    private static final String INTENT_USB_PERMISSION = "com.linc.USB_PERMISSION";
    private int serviceVersion = 0;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            switch(message.what) {
                case 1:
                    if(Idcard.this.getAction() != null && message.obj != null) {
                        Idcard.this.getAction().onDataRead((IDCardInfo)message.obj);
                    }
                default:
                    super.handleMessage(message);
            }
        }
    };

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if("com.linc.USB_PERMISSION".equals(action)) {
                synchronized(this) {
                    UsbDevice device = (UsbDevice)intent.getParcelableExtra("device");
                    if(intent.getBooleanExtra("permission", false) && device != null) {
                        HashMap idrparams = new HashMap();
                        idrparams.put("param.key.vid", Integer.valueOf(1024));
                        idrparams.put("param.key.pid", Integer.valueOf(50010));
                        Idcard.this.idCardReader = IDCardReaderFactory.createIDCardReader(context, TransportType.USB, idrparams);
                        Idcard.this.destroyed = false;
                        context.unregisterReceiver(Idcard.this.mUsbReceiver);

                        try {
                            Idcard.this.idCardReader.open(0);
                        } catch (IDCardReaderException var9) {
                            var9.printStackTrace();
                            return;
                        }

                        Thread thread = new Thread(new Runnable() {
                            public void run() {
                                while(!Idcard.this.destroyed) {
                                    if(Idcard.this.isScanning) {
                                        Message message = new Message();

                                        try {
                                            try {
                                                Idcard.this.idCardReader.findCard(0);
                                                Thread.sleep(1L);
                                            } catch (IDCardReaderException var8) {
                                                message.obj = null;
                                                continue;
                                            }

                                            try {
                                                Idcard.this.idCardReader.selectCard(0);
                                            } catch (IDCardReaderException var7) {
                                                continue;
                                            }

                                            try {
                                                com.zkteco.android.biometric.module.idcard.meta.IDCardInfo e = new com.zkteco.android.biometric.module.idcard.meta.IDCardInfo();
                                                boolean ret = Idcard.this.idCardReader.readCard(0, 1, e);
                                                if(ret) {
                                                    IDCardInfo idCardInfo = new IDCardInfo();
                                                    idCardInfo.setId(e.getId());
                                                    idCardInfo.setAddress(e.getAddress());
                                                    idCardInfo.setBirth(e.getBirth());
                                                    idCardInfo.setDepart(e.getDepart());
                                                    idCardInfo.setName(e.getName());
                                                    idCardInfo.setNation(e.getNation());
                                                    idCardInfo.setSex(e.getSex());
                                                    idCardInfo.setValidityTime(e.getValidityTime());
                                                    message.obj = idCardInfo;
                                                }
                                            } catch (IDCardReaderException var6) {
                                                message.obj = null;
                                            }

                                            message.what = 1;
                                            Idcard.this.mHandler.sendMessage(message);
                                        } catch (InterruptedException var9) {
                                            var9.printStackTrace();
                                        }
                                    }

                                    try {
                                        Thread.sleep(1L);
                                    } catch (InterruptedException var5) {
                                        ;
                                    }
                                }

                            }
                        });
                        thread.setDaemon(true);
                        thread.start();
                        Idcard.this.isStarted = true;
                    }
                }
            }

        }
    };

    @Override
    public void startReadCard() {
        this.isScanning = true;
    }

    @Override
    public void stopReadCard() {
        this.isScanning = false;
    }

    @Override
    public int getServiceVersion() {
        return 0;
    }

    @Override
    public int initDevice(Context context) {
        if(this.hasInit) {
            return 0;
        } else {
            UsbManager mUsbManager = (UsbManager)context.getSystemService(Context.USB_SERVICE);
            IntentFilter permissionFilter = new IntentFilter("com.linc.USB_PERMISSION");
            context.registerReceiver(this.mUsbReceiver, permissionFilter);
            HashMap deviceHashMap = mUsbManager.getDeviceList();
            Iterator iterator = deviceHashMap.values().iterator();

            UsbDevice device;
            do {
                if(!iterator.hasNext()) {
                    return 6;
                }

                device = (UsbDevice)iterator.next();
            } while(device.getVendorId() != 1024 || device.getProductId() != 50010);

            mUsbManager.requestPermission(device, PendingIntent.getBroadcast(context, 0, new Intent("com.linc.USB_PERMISSION"), 0));
            return 0;
        }
    }

    @Override
    public int resetDevice() {
        return 0;
    }

    @Override
    public int destroyService() {
        if(this.isStarted) {
            this.destroyed = true;
            this.isScanning = false;

            try {
                this.idCardReader.close(0);
                return 0;
            } catch (IDCardReaderException var2) {
                var2.printStackTrace();
                return -1;
            }
        } else {
            this.isScanning = false;
            this.destroyed = true;
            return 0;
        }
    }
}
