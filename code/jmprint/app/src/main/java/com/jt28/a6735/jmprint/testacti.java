package com.jt28.a6735.jmprint;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.jt28.a6735.jtprintlib.tprint;

import java.io.IOException;
/**
 * Created by a6735 on 2017/8/15.
 */

public class testacti extends AppCompatActivity {
    private Bitmap m_bit;
    private tprint m_tprint;
    private final int PICKFILE_RESULT_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testacti);

        m_tprint = new tprint();
        m_tprint.initDevice(this);

        Button buttonPick = (Button) findViewById(R.id.id_sel_t);
        buttonPick.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICKFILE_RESULT_CODE);
            }});

        Button print = (Button) findViewById(R.id.id_print_t);
        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //continueButtonClicked(view);
                m_tprint.printImage(m_bit);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        switch (requestCode) {
            case PICKFILE_RESULT_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri userPickedUri;
                    userPickedUri = data.getData();
                    try {
                        m_bit = MediaStore.Images.Media.getBitmap(getContentResolver(), userPickedUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }
}
