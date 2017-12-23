package com.jt28.a6735.jmreceiptcabinet;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jt28.a6735.jmreceipt.JMReceipt;
import com.telecp.deviceactioninterface.DrawerActionCallBack;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "jt128l";

    private JMReceipt m_JMReceipt;

    private Button but_open;
    private Button but_close;
    private Button but_zero;
    private EditText edit_num;
    private TextView tex_errorshow;

    private int num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        m_JMReceipt = new JMReceipt();
        m_JMReceipt.initDevice(getApplicationContext());
        m_JMReceipt.setDrawerActionCallBack(new DrawerActionCallBack() {
            @Override
            public void onDrawerOpened(int i, int i1) {
                Log.d(TAG,"回调 开 打开成功");
               // m_JMReceipt.closeDrawer(num);
            }
            @Override
            public void onDrawerClosed(int i, int i1) {
                if(i1 == 1) {
                    Log.d(TAG, "回调 关 有回单");
                } else if(i1 == 2) {
                    Log.d(TAG, "回调 关 无回单");
                }
//                if(num < 760) {
//                    num += 1;
//                } else {
//                    num = 0;
//                }
                //m_JMReceipt.openDrawer(num);
            }

            @Override
            public void onError(int i, int i1) {
                Message message = new Message();
                message.what = i1;
                mHandler.sendMessage(message);
                Log.d(TAG, "过流");
            }
        });


        but_open = (Button) findViewById(R.id.id_open);
        but_close = (Button) findViewById(R.id.id_close);
        but_zero = (Button) findViewById(R.id.id_zero);
        edit_num = (EditText) findViewById(R.id.id_num);
        tex_errorshow = (TextView) findViewById(R.id.id_erro_show);

        but_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_JMReceipt.openDrawer(Integer.valueOf(edit_num.getText().toString()));
            }
        });

        but_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_JMReceipt.closeDrawer(Integer.valueOf(edit_num.getText().toString()));
            }
        });

        but_zero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_JMReceipt.resetDevice();
            }
        });
    }

//    @Override
//    public void onDrawerOpened(int i, int i1) {
//        Log.d(TAG,"回调 开 打开成功");
//        m_JMReceipt.closeDrawer(num);
//    }
//
//    @Override
//    public void onDrawerClosed(int i, int i1) {
//        if(i1 == 1) {
//            Log.d(TAG, "回调 关 有回单");
//        } else if(i1 == 2) {
//            Log.d(TAG, "回调 关 无回单");
//        }
//        m_JMReceipt.openDrawer(num);
//        if(num < 700) {
//            this.num += 10;
//        } else {
//            this.num = 0;
//        }
//    }
//
//    @Override
//    public void onError(int i, int i1) {
//        Message message = new Message();
//        message.what = i1;
//        mHandler.sendMessage(message);
//    }

    public Handler mHandler=new Handler()
    {
        public void handleMessage(Message msg)
        {
            tex_errorshow.setText(m_JMReceipt.getErrorMsg(msg.what));
            super.handleMessage(msg);
        }
    };

}
