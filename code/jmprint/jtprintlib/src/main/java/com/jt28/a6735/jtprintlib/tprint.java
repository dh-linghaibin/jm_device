package com.jt28.a6735.jtprintlib;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by a6735 on 2017/8/14.
 */

public class tprint extends com.telecp.deviceactioninterface.TPrintManager {

//    private PrintJobData printJobData;
//    private PrintAttributes.Margins margins;
//    private PrintItem.ScaleType scaleType;
//    private PrintAttributes.MediaSize mediaSize5x7;
    private Context mCotex;
    private int flag = 0;
    @Override
    public int getPrinterStatus() {
        return flag;
    }

    @Override
    public int printImage(Bitmap bitmap) {
        //continueButtonClicked(bitmap);
        return 0;
    }


    @Override
    public int getServiceVersion() {
        return 0;
    }

    @Override
    public int initDevice(Context context) {
        this.mCotex = context;
//        scaleType = PrintItem.ScaleType.FIT;
//        mediaSize5x7 = new PrintAttributes.MediaSize("na_5x7_5x7in", "5 x 7", 5000, 7000);
//        margins = new PrintAttributes.Margins(500, 500, 500, 500);
        return 0;
    }

    @Override
    public int resetDevice() {
        return 0;
    }

    @Override
    public int destroyService() {
        return 0;
    }

    private void continueButtonClicked(Bitmap m_bip) {
//        createPrintJobData(m_bip);
//        PrintUtil.setPrintJobData(printJobData);
//        PrintUtil.print((Activity) mCotex);
    }

//    private void createPrintJobData(Bitmap m_bip) {
//        createUserSelectedImageJobData(m_bip);//打印图片
//        //Giving the print job a name.
//        printJobData.setJobName("jtprint");
//
//        //Optionally include print attributes.
//        PrintAttributes printAttributes = new PrintAttributes.Builder()
//                .setMediaSize(PrintAttributes.MediaSize.NA_LETTER)
//                .build();
//        printJobData.setPrintDialogOptions(printAttributes);
//
//    }
//
//    private void createUserSelectedImageJobData(Bitmap m_bip) {
//        Bitmap userPickedBitmap;
//
//        userPickedBitmap = m_bip;//MediaStore.Images.Media.getBitmap(mCotex.getContentResolver(), userPickedUri);
//        int width = userPickedBitmap.getWidth();
//        int height = userPickedBitmap.getHeight();
//
//        // if user picked bitmap is too big, just reduce the size, so it will not chock the print plugin
//        if (width * height > 5000) {
//            width = width / 2;
//            height = height / 2;
//            userPickedBitmap = Bitmap.createScaledBitmap(userPickedBitmap, width, height, true);
//        }
//
//        DisplayMetrics mDisplayMetric = mCotex.getResources().getDisplayMetrics();
//        float widthInches =  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_IN, width, mDisplayMetric);
//        float heightInches =  TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_IN, height, mDisplayMetric);
//
//        ImageAsset imageAsset = new ImageAsset(mCotex,
//                userPickedBitmap,
//                ImageAsset.MeasurementUnits.INCHES,
//                widthInches, heightInches);
//
//        PrintItem printItem4x6 = new ImagePrintItem(PrintAttributes.MediaSize.NA_INDEX_4X6,margins, scaleType, imageAsset);
//        PrintItem printItem85x11 = new ImagePrintItem(PrintAttributes.MediaSize.NA_LETTER,margins, scaleType, imageAsset);
//        PrintItem printItem5x7 = new ImagePrintItem(mediaSize5x7,margins, scaleType, imageAsset);
//
//        printJobData = new PrintJobData(mCotex, printItem4x6);
//        printJobData.addPrintItem(printItem85x11);
//        printJobData.addPrintItem(printItem5x7);
//
//    }

//    @Override
//    public void onPrintMetricsDataPosted(PrintMetricsData printMetricsData) {
//        Log.d("JT128",printMetricsData.printResult);
//        if(printMetricsData.printResult.equals(PRINT_RESULT_FAILED)) {
//            flag = 3;
//        } else if(printMetricsData.printResult.equals(PRINT_RESULT_SUCCESS)) {
//            flag = 0;
//        } else if(printMetricsData.printResult.equals(PRINT_RESULT_CANCEL)) {
//            flag = 1;
//        }
//    }
}
