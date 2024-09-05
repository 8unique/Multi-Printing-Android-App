package com.dantsu.thermalprinter.async;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.dantsu.escposprinter.EscPosPrinterCommands;
import com.dantsu.escposprinter.connection.DeviceConnection;
import com.dantsu.escposprinter.exceptions.EscPosConnectionException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Queue;

public abstract class AsyncBigImagePrinting extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = "AsyncBigImagePrinting";

    private Context context;
    private DeviceConnection printerConnection;
    private Bitmap receiptBitMap;

    private TextView logTextView;


    public AsyncBigImagePrinting(DeviceConnection printerConnection, Context context, Bitmap receiptBitMap, TextView logTextView) {
        this.context = context;
        this.printerConnection = printerConnection;
        this.receiptBitMap = receiptBitMap;
        this.logTextView = logTextView;
    }


    private String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(System.currentTimeMillis());
    }
    @Override
    protected Boolean doInBackground(Void... voids) {
        if (printerConnection == null || receiptBitMap == null) {
            return false;
        }

        try {
            printerConnection.connect();
            Log.d(TAG, "Printer connected");

            EscPosPrinterCommands printerCommands = new EscPosPrinterCommands(printerConnection);
            printerCommands.reset();


            if (receiptBitMap != null) {
                byte[] imageArray = EscPosPrinterCommands.bitmapToBytes(receiptBitMap, true);
                byte[] feed = new byte[]{0x1B, 0x4A, (byte) receiptBitMap.getHeight()};
                byte[] cut = new byte[]{0x1D, 0x56, 0x01};

                byte[] allByteArray = new byte[imageArray.length + feed.length + feed.length + cut.length];

                ByteBuffer buff = ByteBuffer.wrap(allByteArray);
                buff.put(imageArray);
                buff.put(feed);
                buff.put(feed);
                buff.put(cut);

                byte[] combined = buff.array();
                printerCommands.printImage(combined);
            }


            return true;
        } catch (Exception e) {
            Log.e(TAG, "Printing error: " + e.getMessage());
            logToText2("Printing error: " + e.getMessage());
            return false;
        } finally {
            // Disconnect printer connection after printing or in case of error
            if (printerConnection != null) {
                printerConnection.disconnect();
            }
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if(aBoolean){
            onSuccess();
            logToText("Print Success");
        }else{
            onError();
            logToText2("Print Failed");
        }
    }

    private void logToText(String logMessage) {
        if (logTextView != null) {
            logTextView.post(() -> logTextView.append(getCurrentDateTime() + "   " + logMessage + "\n"));
        }
    }

    private void logToText2(String logMessage) {
        if (logTextView != null) {
            logTextView.post(() -> {
                SpannableStringBuilder builder = new SpannableStringBuilder();
                SpannableString spannableString = new SpannableString(getCurrentDateTime() + "   " + logMessage + "\n");
                spannableString.setSpan(new ForegroundColorSpan(Color.RED), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                logTextView.append(spannableString);
            });
        }
    }



    public abstract Boolean onError();

    public abstract Boolean onSuccess();








}
