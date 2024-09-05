package com.dantsu.thermalprinter.async;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.dantsu.escposprinter.EscPosPrinterCommands;
import com.dantsu.escposprinter.connection.DeviceConnection;


import java.util.List;

public abstract class AsyncPlainTextPrinting extends AsyncTask<AsyncEscPosPrinter, Integer, Boolean> {

    private final String TAG = "AsyncPlainTextPrinting";
    private List<String> printingText;
    private Context context;
    private int selectedPaperSize;
    private DeviceConnection printerConnection;
    private String imagePath;
    private boolean openCashDrawer;
    private int LARGE_PPR_SIZE = 550;
    private final int SMALL_PPR_SIZE = 400;
    private int printerCategory;
    private Bitmap QR;

    public AsyncPlainTextPrinting(DeviceConnection printerConnection, List<String> printingText, Context context, int selectedPaperSize) {
        this.printingText = printingText;
        this.context = context;
        this.selectedPaperSize = selectedPaperSize;
        this.printerConnection = printerConnection;
    }


    @Override
    protected Boolean doInBackground(AsyncEscPosPrinter... asyncEscPosPrinters) {
        if (printerConnection == null) {
            return false;
        }

        try {

            EscPosPrinterCommands printerCommands = new EscPosPrinterCommands(printerConnection);
            printerCommands.connect();
            printerCommands.reset();
            if(openCashDrawer){
                printerCommands.openCashBox();
            }
            Log.d(TAG, "_imagePath_ "+imagePath);
            Log.d(TAG, "_receiptPrint_ 1");


            for(String s : printingText){
                printerCommands.printText(s);
                Log.d(TAG, "_printingText_ "+s);
            }

            printerCommands.cutPaper();
            printerCommands.disconnect();

            return true;
        } catch (Exception e) {

            Log.d(TAG, "AsyncPlainTextPrinting: printer "+e.toString());
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if(aBoolean){
            onSuccess();
        }else{
            onError();
        }
    }

    public abstract void onError();
    public abstract void onSuccess();

    public void setQR(Bitmap QR) {
        this.QR = QR;
    }
}
