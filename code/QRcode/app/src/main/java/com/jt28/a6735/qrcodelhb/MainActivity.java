package com.jt28.a6735.qrcodelhb;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jt28.a6735.qcode.Qcode;
import com.telecp.deviceactioninterface.CodeScanActionCallBack;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "jt128";
    private Qcode code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        code = new Qcode();
        code.initDevice(getApplicationContext());
        code.setCodeScanListener(new CodeScanActionCallBack() {
            @Override
            public void onASCIIMessageScanned(String s) {

            }
        });
//        code.setCodeScanListener(new CodeScanActionCallBack() {
//            @Override
//            public void onASCIIMessageScanned(String s) {
//                Log.d("123",""+s);
//            }
//        });
        code.startScan();
    }
}
