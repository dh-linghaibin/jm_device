package com.jt28.a6735.jmreceipt;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import android_serialport_api.SerialUtilOld;

/**
 * Created by a6735 on 2017/7/18.
 */

public class JMReceipt extends com.telecp.deviceactioninterface.DrawerManager {

    private static final String TAG = "jt128";
    private SerialUtilOld serialUtilOld;
    private String path="/dev/ttyS1";
    private int baudrate=9600;
    private static boolean openDrawer_flag = false;//旋转 抽屉出来
    private static boolean initDrawer_flag = false;//回零
    private static boolean clostDrawer_flag = false;//回来回单检测

    private static int gui_num;
    private static int xuanzhuan_num;
    private static int ceng_num;
    private static int old_gui_num;
    private static int old_xuanzhuan_num;
    private static int draw_num = 0;//抽屉号码
    private static int error_num;//错误码

    private static int hostnumber = 1;//扩展数量
    private static int retransmission = 8;//重发次数
    private static byte[] gateshield = new byte[10];//门屏蔽
    private static byte[] drawershield = new byte[10];//抽屉屏蔽

    private byte[] send_data;//发送内容
    private static String cmd;//发送命令

    private int gl_yun = 0;//过流重发

    private void FlagInit() {
        openDrawer_flag = false;//旋转 抽屉出来
        initDrawer_flag = false;//回零
        clostDrawer_flag = false;//回来回单检测
    }

    private static int h_var1,h_var2;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            switch(message.what) {
                case 1:
                    if(JMReceipt.this.getDrawerActionCallBack() != null) {
                        JMReceipt.this.getDrawerActionCallBack().onError(h_var1,h_var2);
                    }
                    break;
                case 2:
                    if(JMReceipt.this.getDrawerActionCallBack() != null) {
                        JMReceipt.this.getDrawerActionCallBack().onDrawerOpened(h_var1,h_var2);
                    }
                    break;
                case 3:
                    if(JMReceipt.this.getDrawerActionCallBack() != null) {
                        JMReceipt.this.getDrawerActionCallBack().onDrawerClosed(h_var1,h_var2);
                    }
                    break;
                default:
                    super.handleMessage(message);
            }
        }
    };


    public JMReceipt() {
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            File sdCardDir = Environment.getExternalStorageDirectory();//获取SDCard目录,2.2的时候为:/mnt/sdcart  2.1的时候为：/sdcard，所以使用静态方法得到路径会好一点。
            Log.d(TAG, sdCardDir.getPath());
            File f = new File(sdCardDir,"JMReceipt.ini");
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(f);
                Ini ini = new Ini();
                try {
                    ini.load(fis);
                    String tt;
                    int val;
                    tt = ini.getValue("SerialPort","path");
                    path = tt.replace("\r","");
                    Log.d(TAG, tt);
                    tt = ini.getValue("SerialPort","baudrate");
                    baudrate = Integer.valueOf(tt.trim());
                    Log.d(TAG, tt);
                    tt = ini.getValue("SetUp","hostnumber");
                    hostnumber = Integer.valueOf(tt.trim());
                    Log.d(TAG, tt);
                    tt = ini.getValue("SetUp","retransmission");
                    retransmission = Integer.valueOf(tt.trim());
                    Log.d(TAG, tt);
                    tt = ini.getValue("SetUp","gateshield1");
                    val = Integer.valueOf(tt.trim());
                    gateshield[0] = (byte)val;
                    Log.d(TAG, tt);
                    tt = ini.getValue("SetUp","gateshield2");
                    val = Integer.valueOf(tt.trim());
                    gateshield[1] = (byte)val;
                    Log.d(TAG, tt);
                    tt = ini.getValue("SetUp","gateshield3");
                    val = Integer.valueOf(tt.trim());
                    gateshield[2] = (byte)val;
                    Log.d(TAG, tt);
                    tt = ini.getValue("SetUp","gateshield4");
                    val = Integer.valueOf(tt.trim());
                    gateshield[3] = (byte)val;
                    Log.d(TAG, tt);
                    tt = ini.getValue("SetUp","gateshield5");
                    val = Integer.valueOf(tt.trim());
                    gateshield[4] = (byte)val;
                    Log.d(TAG, tt);
                    tt = ini.getValue("SetUp","gateshield6");
                    val = Integer.valueOf(tt.trim());
                    gateshield[5] = (byte)val;
                    Log.d(TAG, tt);
                    tt = ini.getValue("SetUp","gateshield7");
                    val = Integer.valueOf(tt.trim());
                    gateshield[6] = (byte)val;
                    Log.d(TAG, tt);
                    tt = ini.getValue("SetUp","gateshield8");
                    val = Integer.valueOf(tt.trim());
                    gateshield[7] = (byte)val;
                    Log.d(TAG, tt);
                    tt = ini.getValue("SetUp","gateshield9");
                    val = Integer.valueOf(tt.trim());
                    gateshield[8] = (byte)val;
                    Log.d(TAG, tt);
                    tt = ini.getValue("SetUp","gateshield10");
                    val = Integer.valueOf(tt.trim());
                    gateshield[9] = (byte)val;
                    Log.d(TAG, tt);
                    tt = ini.getValue("SetUp","drawershield1");
                    val = Integer.valueOf(tt.trim());
                    drawershield[0] = (byte)val;
                    Log.d(TAG, tt);
                    tt = ini.getValue("SetUp","drawershield2");
                    val = Integer.valueOf(tt.trim());
                    drawershield[1] = (byte)val;
                    Log.d(TAG, tt);
                    tt = ini.getValue("SetUp","drawershield3");
                    val = Integer.valueOf(tt.trim());
                    drawershield[2] = (byte)val;
                    Log.d(TAG, tt);
                    tt = ini.getValue("SetUp","drawershield4");
                    val = Integer.valueOf(tt.trim());
                    drawershield[3] = (byte)val;
                    Log.d(TAG, tt);
                    tt = ini.getValue("SetUp","drawershield5");
                    val = Integer.valueOf(tt.trim());
                    drawershield[4] = (byte)val;
                    Log.d(TAG, tt);
                    tt = ini.getValue("SetUp","drawershield6");
                    val = Integer.valueOf(tt.trim());
                    drawershield[5] = (byte)val;
                    Log.d(TAG, tt);
                    tt = ini.getValue("SetUp","drawershield7");
                    val = Integer.valueOf(tt.trim());
                    drawershield[6] = (byte)val;
                    Log.d(TAG, tt);
                    tt = ini.getValue("SetUp","drawershield8");
                    val = Integer.valueOf(tt.trim());
                    drawershield[7] = (byte)val;
                    Log.d(TAG, tt);
                    tt = ini.getValue("SetUp","drawershield9");
                    val = Integer.valueOf(tt.trim());
                    drawershield[8] = (byte)val;
                    Log.d(TAG, tt);
                    tt = ini.getValue("SetUp","drawershield10");
                    val = Integer.valueOf(tt.trim());
                    drawershield[9] = (byte)val;
                    Log.d(TAG, tt);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.d(TAG,"错误");
            }
        }
        for(int i = 0;i < 10;i++) {
            drawershield[i] = 0x01;
        }
    }
    /*
    * 检查boxNum号(1-N号抽屉)的状态
    0xFF表示检测超时
    0x01表示抽屉为打开状态
    0x02表示抽屉为打开关闭的中间状态
    0x03表示关闭的无单抽屉
    0x04表示关闭的有单抽屉
    * */
    @Override
    public int checkStatus(int i) {
        return 0xFF;
    }
    /*
    * 简单打开boxNum号抽屉(1-N号抽屉)
    0x00 表示收到操作反馈
    0xFF 表示操作超时
    * */
    @Override
    public int openDrawer(int i) {
        gl_yun = 0;//错误初始化

        draw_num = i;
        gui_num = i/760 + 1;
        xuanzhuan_num = ((i%760)/10) + 1;
        ceng_num = ((i%760)%10) + 1;
        write_log("设备开 地址：" + gui_num + "旋转：" + xuanzhuan_num + " 层" + ceng_num);

        FlagInit();

        if( (old_gui_num == gui_num) && (old_xuanzhuan_num == xuanzhuan_num) && (draw_num != 0) ){
            CmdSend("按钮开",gui_num,ceng_num);
            openDrawer_flag = true;
        } else {
            old_gui_num = gui_num;
            old_xuanzhuan_num = xuanzhuan_num;
            CmdSend("检测阻挡", gui_num, ceng_num);
            openDrawer_flag = true;
        }
        return 0;
    }
    /*
     * 简单关闭boxNum号抽屉(1-N号抽屉)
     0x00 表示收到操作反馈
     0xFF 表示操作超时
     */
    @Override
    public int closeDrawer(int i) {
        gl_yun = 0;//错误初始化

        FlagInit();
        draw_num = i;
        gui_num = i/760 + 1;
        xuanzhuan_num = ((i%760)/10);
        ceng_num = ((i%760)%10) + 1;
        write_log("设备关 地址：" + gui_num + "旋转：" + xuanzhuan_num + " 层" + ceng_num);
        CmdSend("抽屉关位置",gui_num,ceng_num);
        clostDrawer_flag = true;
        return 0x00;
    }
    //获取柜子的数量
    @Override
    public int getMachineCount() {
        return hostnumber*760;
    }
    /*
    * 获取设备的类型
    0x01平面式
    0x02旋转式
    * */
    @Override
    public int getMachineType() {
        return 0x02;
    }
    /*
    * 获取抽屉的总数量
    * */
    @Override
    public int getDrawerCount() {
        return 760;
    }
    /*
    * 根据errorCode得到容易理解的错误描述
    返回的内容为各厂家对errorCode的错误解释。
    * */
    @Override
    public String getErrorMsg(int i) {
        String error_name = "";
        switch (i) {
            case 1:
                error_name = "检测阻挡 有阻挡";
                break;
            case 2:
                error_name = "检测抽屉 抽屉未关闭";
                break;
            case 3:
                error_name = "回零 失败";
                break;
            case 4:
                error_name = "旋转 失败";
                break;
            case 5:
                error_name = "抽屉开 卡住";
                break;
            case 6:
                error_name = "抽屉开 失败";
                break;
            case 7:
                error_name = "抽屉关 卡住";
                break;
            case 8:
                error_name = "抽屉关 失败";
                break;
            case 9:
                error_name = "按钮开 失败";
                break;
            case 10:
                error_name = "按钮关 失败";
                break;
            case 11:
                error_name = "发送超时 设备不应答";
                break;
            default:
                break;
        }
        return error_name;
    }
    //获取管理器版本
    @Override
    public int getServiceVersion() {
        return 0;
    }
    //初始化设备 返回0x00表示成功 其它值失败  回零
    @Override
    public int initDevice(Context context) {
        try {
            //设置串口号、波特率，
            serialUtilOld =new SerialUtilOld(path,baudrate,0);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        //this.m_DrawerActionCallBack = (DrawerActionCallBack) context;
        //开始串口接收
        readThread=new ReadThread();
        readThread.start();
        m_Thread.start();

        LogToFileUtils.init(context);
        LogToFileUtils.write("初始化");

        return 0;
    }
    //重置设备，一般需要结合具体设备进行重新上电操作。返回0x00表示成功
    private int start_num = 1;//回零柜子
    @Override
    public int resetDevice() {
        FlagInit();
        initDrawer_flag = true;
        gui_num = 1;
        xuanzhuan_num = 1;
        ceng_num = 1;
        start_num = 1;
        CmdSend("检测阻挡",start_num,1);
        return 0;
    }
    /*
    * 释放相关资源
    返回0x00表示成功
    其它值失败
    * */
    @Override
    public int destroyService() {
        //m_DrawerActionCallBack = null;
        return 0;
    }

    private void write_log(String str) {
        Log.d(TAG, str);
        LogToFileUtils.write(str);
    }
    //发送命令
    private int CmdSend(String cmd,int conter_num,int draw_num) {
        mWorking = false;
        mWorking_count = 0;

        this.cmd = cmd;
        if(serialUtilOld == null) {
            return 0;
        }
        serialUtilOld.getDataByte();//读取一下清空干肉数据
        send_data = new byte[16];
        send_data[0] = (byte)0x3a;
        send_data[1] = (byte)conter_num;
        send_data[2] = (byte)0x16;

        switch (cmd) {
            case "检测阻挡":{
                overtime = 500;
                send_data[2] = 0x24;
                send_data[3] = gateshield[0];
                send_data[4] = gateshield[1];
                send_data[5] = gateshield[2];
                send_data[6] = gateshield[3];
                send_data[7] = gateshield[4];
                send_data[8] = gateshield[5];
                send_data[9] = gateshield[6];
                send_data[10] = gateshield[7];
                send_data[11] = gateshield[8];
                send_data[12] = gateshield[9];
            }
                break;

            case "检测抽屉":{
                overtime = 1000;
                send_data[2] = 0x25;
                send_data[3] = drawershield[0];
                send_data[4] = drawershield[1];
                send_data[5] = drawershield[2];
                send_data[6] = drawershield[3];
                send_data[7] = drawershield[4];
                send_data[8] = drawershield[5];
                send_data[9] = drawershield[6];
                send_data[10] = drawershield[7];
                send_data[11] = drawershield[8];
                send_data[12] = drawershield[9];
            }
                break;

            case "回零":{
                overtime = 15000;
                send_data[2] = 0x12;
            }
                break;

            case "旋转":{
                overtime = 15000;
                send_data[2] = 0x13;
                send_data[3] = (byte)xuanzhuan_num;
            }
                break;

            case "抽屉开":{
                overtime = 4000;
                send_data[2] = (byte)0x14;
                send_data[3] = (byte)draw_num;
            }
                break;

            case "抽屉开位置":{
                overtime = 1000;
                send_data[2] = 0x15;
                send_data[3] = (byte)draw_num;
            }
                break;

            case "抽屉关":{
                overtime = 4000;
                send_data[2] = 0x16;
                send_data[3] = (byte)draw_num;
            }
                break;

            case "抽屉关位置":{
                overtime = 1000;
                send_data[2] = 0x17;
                send_data[3] = (byte)draw_num;
            }
                break;

            case "检测回单":{
                overtime = 500;
                send_data[2] = 0x18;
            }
                break;

            case "按钮开":{
                overtime = 500;
                send_data[2] = 0x21;
            }
                break;

            case "按钮关":{
                overtime = 500;
                send_data[2] = 0x22;
            }
                break;

            case "按钮检测":{
                overtime = 500;
                send_data[2] = 0x23;
            }
                break;

            case "抽屉使用次数":{
                overtime = 1000;
                send_data[2] = 0x77;
                send_data[3] = (byte)draw_num;
            }
                break;
        }
        send_data[13] = (byte)(send_data[1] + send_data[2]);
        send_data[14] = (byte)(send_data[3]+send_data[4]+send_data[5]+send_data[6]+send_data[7]+send_data[8]+send_data[9]+send_data[10]+send_data[11]+send_data[12]);
        send_data[15] = (byte)0x0a;
        try {
            write_log( "发送"+ cmd+Arrays.toString(send_data) + "地址" + send_data[1] );
            serialUtilOld.setData(send_data);
        } catch (NullPointerException e) {
            write_log("串口 发送错误");
            e.printStackTrace();
        }

        mWorking = true;
        mWorking_count = retransmission;
        //m_Thread.start();
        //new Thread(new ThreadShow()).start();
        return 0x00;
    }

    private ThreadShow m_ThreadShow = new ThreadShow();
    private Thread m_Thread = new Thread(m_ThreadShow);

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (mWorking) {
                    try {
                        write_log( "重新发送 " + cmd + Arrays.toString(send_data) + overtime);
//                        if(cmd.equals("抽屉关")) {
//                            send_data[3] = (byte)0x01;
//                        }
                        serialUtilOld.setData(send_data);
                    } catch (NullPointerException e) {
                        write_log( "串口 发送错误");
                        e.printStackTrace();
                    }
                }
            } else if (msg.what == 2) {
                write_log("串口 重发失败");
                //调用失败时间
                //m_Thread.interrupt();
                //readThread.interrupt();
                mWorking = false;
                mWorking_count = 0;
//                if (m_DrawerActionCallBack != null) {
//                    m_DrawerActionCallBack.onError(draw_num, 11);
//
//                }
                h_var1 = draw_num;
                h_var2 = 11;
                Message message = new Message();
                message.what = 1;
                JMReceipt.this.mHandler.sendMessage(message);
                error_num = 11;
            }
        };
    };

    private static int overtime = 3000;//超时时间
    private boolean mWorking = false;//是否工作
    private int mWorking_count = 0;//重发次数
    // 线程类
    class ThreadShow implements Runnable {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (true) {
                while (mWorking_count > 0) {
                    try {
                        if (mWorking) {
                            int time_cout = 0;
                            while( (mWorking) && (time_cout < overtime) ) {
                                Thread.sleep(1);
                                time_cout++;
                            }
                            if (mWorking) {
                                try {
                                    Thread.sleep(200);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                serialUtilOld.getDataByte();//读取一下清空干肉数据
                                if (mWorking) {
                                    mWorking_count--;
                                    if (mWorking_count == 0) {
                                        Message msg2 = new Message();
                                        msg2.what = 2;
                                        handler.sendMessage(msg2);
                                    } else {
                                        //write_log( "线程 重发");
                                        Message msg = new Message();
                                        msg.what = 1;
                                        handler.sendMessage(msg);
                                    }
                                }
                            }
                            //write_log( "线程 send..." + mWorking_count + mWorking);
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        write_log( "线程 error...");
                    }
                }
            }
        }
    }

    private byte[] rec_data;
    private ReadThread readThread;
    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!Thread.currentThread().isInterrupted()) {
                if(mWorking) {
                    try {
                        rec_data = serialUtilOld.getDataByteCmd();
                    } catch (Exception e) {
                        e.printStackTrace();
                        readThread.interrupt();
                    }
                    if (rec_data != null) {
                        overtime = 0;
                        mWorking_count = 0;
                        mWorking = false;
                        write_log( "接收" + Arrays.toString(rec_data));
                        if (rec_data[1] == (byte) 0x88) {
                            switch (cmd) {
                                case "检测阻挡": {
                                    if (rec_data[17] == (byte) 0x21) {
                                        write_log( "检测阻挡 无阻挡");
                                        if ( (openDrawer_flag) || (initDrawer_flag) ){
                                            try {
                                                Thread.sleep(100);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            if ( (openDrawer_flag) || (initDrawer_flag) ){
                                                if(!mWorking) {
                                                    CmdSend("检测抽屉", gui_num, ceng_num);
                                                }
                                            }
                                        }
                                    } else {
                                        int error_draw_num = 0;
                                        if(rec_data[18] ==  1) {
                                            error_draw_num = 1;
                                        }
                                        else if(rec_data[19] ==  1) {
                                            error_draw_num = 2;
                                        }
                                        else if(rec_data[20] ==  1) {
                                            error_draw_num = 3;
                                        }
                                        else if(rec_data[21] ==  1) {
                                            error_draw_num = 4;
                                        }
                                        else if(rec_data[22] ==  1) {
                                            error_draw_num = 5;
                                        }
                                        else if(rec_data[23] ==  1) {
                                            error_draw_num = 6;
                                        }
                                        else if(rec_data[24] ==  1) {
                                            error_draw_num = 7;
                                        }
                                        else if(rec_data[25] ==  1) {
                                            error_draw_num = 8;
                                        }
                                        else if(rec_data[26] ==  1) {
                                            error_draw_num = 9;
                                        }
                                        else if(rec_data[27] ==  1) {
                                            error_draw_num = 10;
                                        }
                                        try {
                                            Thread.sleep(100);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        //抽屉回来
                                        openDrawer_flag = false;
                                        clostDrawer_flag= false;
                                        CmdSend("抽屉关", gui_num, error_draw_num);
//                                        if(m_DrawerActionCallBack != null) {
//                                            m_DrawerActionCallBack.onError(error_draw_num, 0x01);
//                                            error_num = 1;
//                                        }
                                        h_var1 = error_draw_num;
                                        h_var2 = 1;
                                        Message message = new Message();
                                        message.what = 1;
                                        JMReceipt.this.mHandler.sendMessage(message);
                                        error_num = 1;
                                        write_log("检测阻挡 有阻挡");
                                    }
                                }
                                break;

                                case "检测抽屉": {
                                    if (rec_data[17] == (byte) 0x21) {
                                        write_log( "检测抽屉 无阻挡");
                                        if (openDrawer_flag) {
                                            try {
                                                Thread.sleep(100);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            if (openDrawer_flag) {
                                                if (!mWorking) {
                                                    CmdSend("旋转", gui_num, ceng_num);
                                                }
                                            }
                                        } else if(initDrawer_flag) {
                                            try {
                                                Thread.sleep(100);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            if (initDrawer_flag) {
                                                if(!mWorking) {
                                                    CmdSend("回零", start_num, ceng_num);
                                                }
                                            }
                                        }
                                    } else {
                                        int error_draw_num = 0;
                                        if(rec_data[18] ==  1) {
                                            error_draw_num = 1;
                                        }
                                        if(rec_data[19] ==  1) {
                                            error_draw_num = 2;
                                        }
                                        if(rec_data[20] ==  1) {
                                            error_draw_num = 3;
                                        }
                                        if(rec_data[21] ==  1) {
                                            error_draw_num = 4;
                                        }
                                        if(rec_data[22] ==  1) {
                                            error_draw_num = 5;
                                        }
                                        if(rec_data[23] ==  1) {
                                            error_draw_num = 6;
                                        }
                                        if(rec_data[24] ==  1) {
                                            error_draw_num = 7;
                                        }
                                        if(rec_data[25] ==  1) {
                                            error_draw_num = 8;
                                        }
                                        if(rec_data[26] ==  1) {
                                            error_draw_num = 9;
                                        }
                                        if(rec_data[27] ==  1) {
                                            error_draw_num = 10;
                                        }
//                                        if(m_DrawerActionCallBack != null) {
//                                            m_DrawerActionCallBack.onError(error_draw_num, 0x02);
//                                            error_num = 2;
//                                        }
                                        h_var1 = error_draw_num;
                                        h_var2 = 0x02;
                                        Message message = new Message();
                                        message.what = 1;
                                        JMReceipt.this.mHandler.sendMessage(message);
                                        error_num = 2;

                                        write_log( "检测抽屉 有阻挡");
                                    }
                                }
                                break;

                                case "回零": {
                                    if (rec_data[17] == (byte) 0x21) {
                                        write_log( "回零 成功");
                                        //连续初始化
                                        if (start_num < hostnumber+1) {
                                            if (initDrawer_flag) {
                                                try {
                                                    Thread.sleep(100);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                                if (initDrawer_flag) {
                                                    if (!mWorking) {
                                                        if (start_num < hostnumber+1) {
                                                            start_num++;
                                                            CmdSend("检测阻挡", start_num, 1);
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            initDrawer_flag = false;
                                        }
                                    } else {
                                        write_log( "回零 失败");
                                        h_var1 = draw_num;
                                        h_var2 = 0x03;
                                        Message message = new Message();
                                        message.what = 1;
                                        JMReceipt.this.mHandler.sendMessage(message);
                                        error_num = 0x03;
                                    }
//                                    write_log( "回零 成功");
//                                    //连续初始化
//                                    if (start_num < hostnumber+1) {
//                                        if (initDrawer_flag) {
//                                            try {
//                                                Thread.sleep(100);
//                                            } catch (InterruptedException e) {
//                                                e.printStackTrace();
//                                            }
//                                            if (initDrawer_flag) {
//                                                if (!mWorking) {
//                                                    if (start_num < hostnumber+1) {
//                                                        start_num++;
//                                                        CmdSend("检测阻挡", start_num, 1);
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    } else {
//                                        initDrawer_flag = false;
//                                    }
                                }
                                break;

                                case "旋转": {
                                    if (rec_data[17] == (byte) 0x21) {
                                        write_log( "旋转 成功");
                                        if (openDrawer_flag) {
                                            try {
                                                Thread.sleep(100);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            if (openDrawer_flag) {
                                                if(!mWorking) {
                                                    CmdSend("按钮开", gui_num, ceng_num);
                                                }
                                            }
                                        }
                                    } else {
                                        write_log( "旋转 失败");
//                                        if(m_DrawerActionCallBack != null) {
//                                            m_DrawerActionCallBack.onError(draw_num, 0x04);
//                                            error_num = 4;
//                                        }
                                        h_var1 = draw_num;
                                        h_var2 = 0x04;
                                        Message message = new Message();
                                        message.what = 1;
                                        JMReceipt.this.mHandler.sendMessage(message);
                                        error_num = 0x04;
                                    }
                                }
                                break;

                                case "抽屉开": {
                                    if (rec_data[17] == (byte) 0x21) {
                                        write_log( "抽屉开 成功");
                                        //m_DrawerActionCallBack.onDrawerOpened();
                                        if (openDrawer_flag) {
//                                            if(m_DrawerActionCallBack != null) {
//                                                m_DrawerActionCallBack.onDrawerOpened(draw_num, 0x01);
//                                            }
                                            h_var1 = draw_num;
                                            h_var2 = 0x01;
                                            Message message = new Message();
                                            message.what = 2;
                                            JMReceipt.this.mHandler.sendMessage(message);

                                            try {
                                                Thread.sleep(100);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            if (openDrawer_flag) {
                                                if(!mWorking) {
                                                    CmdSend("按钮检测", gui_num, ceng_num);
                                                }
                                            }
                                        }
                                    } else if (rec_data[17] == (byte) 0x22) {
                                        write_log( "抽屉开 过流");
                                        try {
                                            Thread.sleep(100);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        if(gl_yun < 2) {
                                            gl_yun++;
                                            CmdSend(cmd, gui_num, ceng_num);
                                        } else {
//                                            if (m_DrawerActionCallBack != null) {
//                                                m_DrawerActionCallBack.onError(draw_num, 0x05);
//
//                                            }
                                            h_var1 = draw_num;
                                            h_var2 = 0x05;
                                            Message message = new Message();
                                            message.what = 1;
                                            JMReceipt.this.mHandler.sendMessage(message);
                                            error_num = 5;
                                        }
                                    } else {
                                        write_log( "抽屉开 失败");
//                                        if (m_DrawerActionCallBack != null) {
//                                            m_DrawerActionCallBack.onError(draw_num, 0x06);
//                                            error_num = 6;
//                                        }
                                        h_var1 = draw_num;
                                        h_var2 = 6;
                                        Message message = new Message();
                                        message.what = 1;
                                        JMReceipt.this.mHandler.sendMessage(message);
                                        error_num = 6;
                                    }
                                }
                                break;

                                case "抽屉开位置": {
                                    if (rec_data[17] == (byte) 0x21) {
                                        write_log( "抽屉开位置 在位置");
                                    } else {
                                        write_log( "抽屉开位置 不再位置");
                                    }
                                }
                                break;

                                case "抽屉关": {
                                    if (rec_data[17] == (byte) 0x21) {
                                        write_log( "抽屉关 成功");
                                        if( (openDrawer_flag) || (clostDrawer_flag) ){
                                            try {
                                                Thread.sleep(100);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            if( (openDrawer_flag) || (clostDrawer_flag) ) {
                                                if(!mWorking) {
                                                    CmdSend("按钮关", gui_num, ceng_num);
                                                }
                                            }
                                        }
                                    } else if (rec_data[17] == (byte) 0x22) {
                                        write_log( "抽屉关 过流");
                                        try {
                                            Thread.sleep(100);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        if(gl_yun < 2) {
                                            gl_yun++;
                                            CmdSend(cmd, gui_num, ceng_num);
                                        } else {
//                                            if (m_DrawerActionCallBack != null) {
//                                                m_DrawerActionCallBack.onError(draw_num, 0x07);
//                                                error_num = 7;
//                                            }
                                            h_var1 = draw_num;
                                            h_var2 = 7;
                                            Message message = new Message();
                                            message.what = 1;
                                            JMReceipt.this.mHandler.sendMessage(message);
                                            error_num = 7;
                                        }
                                    } else {
                                        write_log( "抽屉关 失败");
//                                        if(m_DrawerActionCallBack != null) {
//                                            m_DrawerActionCallBack.onError(draw_num, 0x08);
//                                            error_num = 8;
//                                        }
                                        h_var1 = draw_num;
                                        h_var2 = 8;
                                        Message message = new Message();
                                        message.what = 1;
                                        JMReceipt.this.mHandler.sendMessage(message);
                                        error_num = 8;
                                    }
                                }
                                break;

                                case "抽屉关位置": {
                                    if (rec_data[17] == (byte) 0x21) {
                                        write_log( "抽屉关位置 在位置");
                                        if(clostDrawer_flag) {
                                            try {
                                                Thread.sleep(100);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            if(clostDrawer_flag) {
                                                CmdSend("检测回单", gui_num, ceng_num);
                                            }
                                        }
                                    } else {
                                        write_log( "抽屉关位置 不再位置");
                                        if(clostDrawer_flag) {
                                            try {
                                                Thread.sleep(100);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            if(clostDrawer_flag) {
                                                CmdSend("抽屉关", gui_num, ceng_num);
                                            }
                                        }
                                    }
                                }
                                break;

                                case "检测回单": {
                                    if (rec_data[17] == (byte) 0x21) {
                                        write_log("检测回单 有回单");
                                        if( (openDrawer_flag) || (clostDrawer_flag) ) {
                                            openDrawer_flag = false;
                                            try {
                                                Thread.sleep(300);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
//                                            if(m_DrawerActionCallBack != null) {
//                                                m_DrawerActionCallBack.onDrawerClosed(draw_num,0x01);
//                                            }
                                            h_var1 = draw_num;
                                            h_var2 = 0x01;
                                            Message message = new Message();
                                            message.what = 3;
                                            JMReceipt.this.mHandler.sendMessage(message);
                                        }
                                    } else {
                                        write_log( "检测回单 无回单");
                                        if( (openDrawer_flag) || (clostDrawer_flag) ) {
                                            openDrawer_flag = false;
                                            try {
                                                Thread.sleep(300);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
//                                            if(m_DrawerActionCallBack != null) {
//                                                m_DrawerActionCallBack.onDrawerClosed(draw_num,0x02);
//                                            }
                                            h_var1 = draw_num;
                                            h_var2 = 0x02;
                                            Message message = new Message();
                                            message.what = 3;
                                            JMReceipt.this.mHandler.sendMessage(message);
                                        }
                                    }
                                }
                                break;

                                case "按钮开": {
                                    if (rec_data[17] == (byte) 0x21) {
                                        write_log( "按钮开 成功");
                                        if (openDrawer_flag) {
                                            try {
                                                Thread.sleep(100);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            if (openDrawer_flag) {
                                                if(!mWorking) {
                                                    CmdSend("抽屉开", gui_num, ceng_num);
                                                }
                                            }
                                        }
                                    } else {
                                        write_log( "按钮开 失败");
//                                        if(m_DrawerActionCallBack != null) {
//                                            m_DrawerActionCallBack.onError(draw_num, 0x09);
//                                            error_num = 9;
//                                        }
                                        h_var1 = draw_num;
                                        h_var2 = 9;
                                        Message message = new Message();
                                        message.what = 1;
                                        JMReceipt.this.mHandler.sendMessage(message);
                                        error_num = 9;
                                    }
                                }
                                break;

                                case "按钮关": {
                                    if (rec_data[17] == (byte) 0x21) {
                                        write_log( "按钮关 成功");
                                        if ( (openDrawer_flag) || (clostDrawer_flag) ) {
                                            try {
                                                Thread.sleep(100);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            if ( (openDrawer_flag) || (clostDrawer_flag) ) {
                                                if(!mWorking) {
                                                    CmdSend("检测回单", gui_num, ceng_num);
                                                }
                                            }
                                        }
                                    } else {
                                        write_log( "按钮关 失败");
//                                        if(m_DrawerActionCallBack != null) {
//                                            m_DrawerActionCallBack.onError(draw_num, 10);
//                                            error_num = 10;
//                                        }
                                        h_var1 = draw_num;
                                        h_var2 = 10;
                                        Message message = new Message();
                                        message.what = 1;
                                        JMReceipt.this.mHandler.sendMessage(message);
                                        error_num = 10;
                                    }
                                }
                                break;

                                case "按钮检测": {
                                    if (rec_data[17] == (byte) 0x21) {
                                        write_log( "按钮检测 按下");
                                        if (openDrawer_flag) {
                                            try {
                                                Thread.sleep(100);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            if (openDrawer_flag) {
                                                if(!mWorking) {
                                                    CmdSend("抽屉关", gui_num, ceng_num);
                                                }
                                            }
                                        }
                                    } else {
                                        write_log( "按钮检测 未按下");
                                        if (openDrawer_flag) {
                                            try {
                                                Thread.sleep(800);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            if (openDrawer_flag) {
                                                if(!mWorking) {
                                                    CmdSend("按钮检测", gui_num, ceng_num);
                                                }
                                            }
                                        }
                                    }
                                }
                                break;

                                case "抽屉使用次数": {
                                }
                                break;
                            }
                        } else {
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            serialUtilOld.getDataByte();//读取一下清空干肉数据
                            write_log( "校验 重发");
                            CmdSend(cmd, gui_num, ceng_num);
                        }
                    }
                }
            }
        }
    }

}
