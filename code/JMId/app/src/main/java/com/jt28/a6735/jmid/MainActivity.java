package com.jt28.a6735.jmid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.jt28.a6735.idcard.Idcard;
import com.telecp.deviceactioninterface.IDCardAction;
import com.telecp.deviceactioninterface.IDCardInfo;

public class MainActivity extends AppCompatActivity {
    public static Idcard m_Idread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_Idread = new Idcard();
        m_Idread.initDevice(getApplicationContext());
        m_Idread.startReadCard();
        m_Idread.setAction(new IDCardAction() {
            @Override
            public void onDataRead(IDCardInfo idCardInfo) {
                Log.d("JT128",idCardInfo.getAddress());
                Log.d("JT128",idCardInfo.getBirth());
                Log.d("JT128",idCardInfo.getDepart());
            }
        });
    }
}
