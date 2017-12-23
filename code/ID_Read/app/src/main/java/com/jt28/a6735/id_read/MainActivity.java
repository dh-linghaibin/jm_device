package com.jt28.a6735.id_read;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jt28.a6735.idcard.Idcard;
import com.telecp.deviceactioninterface.IDCardAction;
import com.telecp.deviceactioninterface.IDCardInfo;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "jt128";
    TextView m_msg;
    TextView m_samid;
    TextView txtinfo;
    ImageView image;
    Button bt_readone;
    private Idcard m_Idread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        m_msg = (TextView) findViewById(R.id.msg);
        m_samid = (TextView) findViewById(R.id.SamId);
        txtinfo = (TextView) findViewById(R.id.txtinfo);
        bt_readone = (Button) findViewById(R.id.ReadOne);
        image = (ImageView) findViewById(R.id.image);

        m_Idread = new Idcard();
        m_Idread.initDevice(getApplicationContext());
        m_Idread.startReadCard();
        bt_readone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_Idread.startReadCard();
            }
        });
        m_Idread.setAction(new IDCardAction() {
            @Override
            public void onDataRead(IDCardInfo idCardInfo) {
                Log.d(TAG,idCardInfo.getAddress());
                Log.d(TAG,idCardInfo.getBirth());
                Log.d(TAG,idCardInfo.getDepart());
                txtinfo.setText(idCardInfo.getAddress() + idCardInfo.getBirth() + idCardInfo.getDepart() );
                Log.d("JT128",idCardInfo.getImgPath() );
                image.setImageBitmap(getDiskBitmap(idCardInfo.getImgPath()));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    };

//    @Override
//    public void onDataRead(IDCardInfo var1) {
//        Log.d(TAG,var1.getAddress());
//        Log.d(TAG,var1.getBirth());
//        Log.d(TAG,var1.getDepart());
//        txtinfo.setText(var1.getAddress() + var1.getBirth() + var1.getDepart() );
//        Log.d("JT128",var1.getImgPath() );
//        image.setImageBitmap(getDiskBitmap(var1.getImgPath()));
//    }

    private Bitmap getDiskBitmap(String pathString) {
        Bitmap bitmap = null;
        try {
            File file = new File(pathString);
            if(file.exists()) {
                bitmap = BitmapFactory.decodeFile(pathString);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return bitmap;
    }
}
