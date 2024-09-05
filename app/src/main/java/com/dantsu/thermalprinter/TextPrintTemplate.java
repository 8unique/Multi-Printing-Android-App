package com.dantsu.thermalprinter;

import android.content.Context;
import android.graphics.Typeface;

import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.tcp.TcpConnection;
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException;
import com.dantsu.escposprinter.exceptions.EscPosConnectionException;
import com.dantsu.escposprinter.exceptions.EscPosEncodingException;
import com.dantsu.escposprinter.exceptions.EscPosParserException;
import com.dantsu.thermalprinter.async.AsyncEscPosPrinter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TextPrintTemplate {
    private Context context;
    private static EscPosPrinter printer;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    public TextPrintTemplate(Context context) {
        this.context = context;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        this.timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    }
/*

    public static void initPrinter() throws EscPosConnectionException {

    }
*/

    public static void printTemplate() throws EscPosEncodingException, EscPosBarcodeException, EscPosParserException, EscPosConnectionException {
        TcpConnection connection = new TcpConnection("192.168.1.185", 9100);
        /*printer = new EscPosPrinter(connection, 203, 48f, 32);

        StringBuilder receipt = new StringBuilder();
        receipt.append("[C]<font size='big'>Company Name</font>\n");
        receipt.append("[C]==============================\n");
        receipt.append("[C]==============================\n");
        receipt.append("[L]\n");
        receipt.append("[L]Item 1 [R]$1.00\n");
        receipt.append("[L]Item 2 [R]$2.00\n");
        receipt.append("[L]Item 3 [R]$3.00\n");
        receipt.append("[C]------------------------------\n");
        receipt.append("[L]Total [R]$6.00\n");
        receipt.append("[L]\n");
        receipt.append("[C]<qrcode size='20'>http://www.example.com</qrcode>\n");

        printer.printFormattedText(receipt.toString());*/

        EscPosPrinter printer = new EscPosPrinter(connection, 203, 48f, 32);
        printer
                .printFormattedText(
                                "[L]\n" +
                                "[C]<u><font size='big'>ORDER N°045</font></u>\n" +
                                "[L]\n" +
                                "[C]================================\n" +
                                "[L]\n" +
                                "[L]<b>BEAUTIFUL SHIRT</b>[R]9.99e\n" +
                                "[L]  + Size : S\n" +
                                "[L]\n" +
                                "[L]<b>AWESOME HAT</b>[R]24.99e\n" +
                                "[L]  + Size : 57/58\n"
                );

    }



    public void disconnectPrinter() {
        if (printer != null) {
            printer.disconnectPrinter();
        }
    }
}
