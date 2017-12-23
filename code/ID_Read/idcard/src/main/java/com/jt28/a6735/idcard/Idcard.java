package com.jt28.a6735.idcard;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.huashi.otg.sdk.HSIDCardInfo;
import com.huashi.otg.sdk.HandlerMsg;
import com.huashi.otg.sdk.HsOtgApi;
import com.telecp.deviceactioninterface.IDCardAction;
import com.telecp.deviceactioninterface.IDCardInfo;

import java.io.File;
import java.text.SimpleDateFormat;

/**
 * Created by a6735 on 2017/7/18.
 */

public class Idcard extends com.telecp.deviceactioninterface.IDCardManager {
    private Context mact;
    private HsOtgApi api;//读卡器
    private IDCardAction action;
    private SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日");// 设置日期格式
    private String filepath = "";//授权目录
    private int readflag = 1;

    @Override
    public void setAction(IDCardAction action) {
        this.action = action;
    }
    //开启身份证读卡
    @Override
    public void startReadCard() {
        if(readflag == 1) {
            readflag = 0;
            new Thread(new ThreadShow()).start();
        }
    }

    // 线程类
    class ThreadShow implements Runnable {
        @Override
        public void run() {
            while (readflag == 0) {
                Log.d("JT128", "run...");
                if (readflag == 0) {
                    if (api.Authenticate(200, 200) != 1) {
                        //statu.setText("卡认证失败");
                        Log.d("JT128", "重复卡读取");
                        //return;
                    }
                }
                if (readflag == 0) {
                    HSIDCardInfo ici = new HSIDCardInfo();
                    if (api.ReadCard(ici, 200, 1300) == 1) {
                        readflag = 1;
                        Message msg = Message.obtain();
                        msg.obj = ici;
                        msg.what = HandlerMsg.READ_SUCCESS;
                        hx.sendMessage(msg);
                        Log.d("JT128", "读取到");
                    }
                }
                if(readflag == 0) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    //停止身份证读卡
    @Override
    public void stopReadCard() {
        readflag = 1;
    }
    //获取管理器版本
    @Override
    public int getServiceVersion() {
        return 1;
    }
    //初始化管理器返回0x00表示成功其它值失败
    @Override
    public int initDevice(Context context) {
        readflag = 1;
        this.mact = context;
        filepath = "/mnt/sdcard/wltlib";// 授权目录
        if (api != null) {
            api.unInit();
        }
        api = new HsOtgApi(hx, mact);
        int ret = api.init();//因为第一次需要点击授权，所以第一次点击时候的返回是-1所以我利用了广播接受到授权后用handler发送消息
        return 0;
    }
    //重置设备 不需要
    @Override
    public int resetDevice() {
        readflag = 1;
        return 0;
    }
    //释放相关资源 返回0x00表示成功 其它值失败
    @Override
    public int destroyService() {
        if (api != null) {
            api.unInit();
        }
        readflag = 1;
        return 0;
    }

    private void DeleteFile(String filename) {
        File file = new File(filename);
        if (file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                file.delete(); // delete()方法 你应该知道 是删除的意思;
            }
        }
    }

    Handler hx = new Handler(){
        public void handleMessage(android.os.Message msg) {
                filepath = "/mnt/sdcard/wltlib";// 授权目录
                if (msg.what == 99 || msg.what == 100) {
                    Log.d("JT128", "重复卡读取");
                }
                //第一次授权时候的判断是利用handler判断，授权过后就不用这个判断了
                if (msg.what == HandlerMsg.CONNECT_SUCCESS) {
                    //statu.setText("连接成功");
                    //sam.setText(api.GetSAMID());
                }
                if (msg.what == HandlerMsg.CONNECT_ERROR) {
                    //statu.setText("连接失败");
                    readflag = 1;
                }
                if (msg.what == HandlerMsg.READ_SUCCESS) {
                    HSIDCardInfo ic = (HSIDCardInfo) msg.obj;
                    try {
                        int ret = api.Unpack(filepath, ic.getwltdata());// 照片解码
                        if (ret != 0) {// 读卡失败
                            return;
                        }
                    } catch (Exception e) {
                        filepath = "";// 授权目录
                    }
                    IDCardInfo idmsg = new IDCardInfo();

                    idmsg.setAddress(ic.getAddr());//地址
                    idmsg.setBirth(df.format(ic.getBirthDay()));//生日
                    idmsg.setDepart(ic.getDepartment());//单位
                    idmsg.setId(ic.getIDCard());//身份证号码
                    idmsg.setName(ic.getPeopleName());//名字
                    idmsg.setNation(ic.getPeople());//国籍
                    idmsg.setSex(ic.getSex());//性别
                    idmsg.setValidityTime(ic.getStrartDate() + "-" + ic.getEndDate());//有效期
                    idmsg.setImgPath(filepath + "/zp.bmp");//图像路径
                    if (action != null) {
                        action.onDataRead(idmsg);
                    }
                }
        };
    };
}
