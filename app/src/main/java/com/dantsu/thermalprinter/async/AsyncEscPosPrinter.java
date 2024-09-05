package com.dantsu.thermalprinter.async;

import android.graphics.Bitmap;

import com.dantsu.escposprinter.EscPosPrinterSize;
import com.dantsu.escposprinter.connection.DeviceConnection;

import java.util.List;

public class AsyncEscPosPrinter extends EscPosPrinterSize {
    private DeviceConnection printerConnection;
    private String[] textsToPrint = new String[0];
    private int stringArrayPrint,textGraphicPrint;
    private List<String> stringList;

    public AsyncEscPosPrinter(DeviceConnection printerConnection, int printerDpi, float printerWidthMM, int printerNbrCharactersPerLine) {
        super(printerDpi, printerWidthMM, printerNbrCharactersPerLine);
        this.printerConnection = printerConnection;
    }

    public DeviceConnection getPrinterConnection() {
        return this.printerConnection;
    }

    public AsyncEscPosPrinter setTextsToPrint(String[] textsToPrint) {
        this.textsToPrint = textsToPrint;
        return this;
    }

    public AsyncEscPosPrinter addTextToPrint(String textToPrint) {
        String[] tmp = new String[this.textsToPrint.length + 1];
        System.arraycopy(this.textsToPrint, 0, tmp, 0, this.textsToPrint.length);
        tmp[this.textsToPrint.length] = textToPrint;
        this.textsToPrint = tmp;
        return this;
    }
    public AsyncEscPosPrinter addTextToPrint(String textToPrint,int arrayPrint) {

        this.stringArrayPrint = arrayPrint;
        if(false/*arrayPrint == MainActivity.array_printing*/){
            this.textsToPrint = textToPrint.split("\n");
        }else{
            String[] tmp = new String[this.textsToPrint.length + 1];
            System.arraycopy(this.textsToPrint, 0, tmp, 0, this.textsToPrint.length);
            tmp[this.textsToPrint.length] = textToPrint;
            this.textsToPrint = tmp;
        }
        return this;
    }

    public AsyncEscPosPrinter addTextToPrint(List<String> stringList, int stringArrayPrint, int textGraphicPrint, int paperSize, boolean openCashDrawer, String logoPath, Bitmap QR, int receiptType){
        System.out.println("_stringList_ "+stringList);
        this.stringList = stringList;
        this.stringArrayPrint = stringArrayPrint;
        this.textGraphicPrint = textGraphicPrint;
        return this;
    }


    public int getStringArrayPrint() {
        return stringArrayPrint;
    }

    public String[] getTextsToPrint() {
        return this.textsToPrint;
    }


    public List<String> getStringList() {
        return stringList;
    }

    public int getTextGraphicPrint() {
        return textGraphicPrint;
    }

}
