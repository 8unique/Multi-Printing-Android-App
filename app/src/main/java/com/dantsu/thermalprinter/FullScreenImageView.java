package com.dantsu.thermalprinter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.pax.gl.page.IPage;
import com.pax.gl.page.IPage.EAlign;
import com.pax.gl.page.IPage.ILine.IUnit;
import com.pax.gl.page.PaxGLPage;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class FullScreenImageView {
    Context context;
    private PaxGLPage iPaxGLPage;
    private Bitmap bitmap;
    private static final int FONT_SMALL = 20;
    private static final int FONT_Small_NORMAL = 23;
    private static final int FONT_NORMAL = 26;
    private static final int FONT_BIG = 28;
    private static final int FONT_BIGEST = 34;
    private static final int BARCODE_WIDTH = 500;
    private static final int BARCODE_HEIGHT = 100;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private IPage page;

    public FullScreenImageView(Context context) {
        this.context = context;
    }

    public void init() {
        iPaxGLPage = PaxGLPage.getInstance(context);
//        IPage page = iPaxGLPage.createPage();
        this.page = iPaxGLPage.createPage();

        /*page.setTypefaceObj(Typeface.createFromAsset(context.getAssets(), "Roboto-Regular.ttf"));

        page.addLine().addUnit(" ", FONT_NORMAL);

        page.addLine().addUnit("Font Small - 20", FONT_SMALL, EAlign.CENTER);
        page.addLine().addUnit("Apple Apple - 24", FONT_Small_NORMAL, EAlign.CENTER);
        page.addLine().addUnit("Font Small_Normal - 24", FONT_Small_NORMAL, EAlign.CENTER);
        page.addLine().addUnit("Font Normal - 26", FONT_NORMAL, EAlign.CENTER);
        page.addLine().addUnit("Font Big - 28", FONT_BIG, EAlign.CENTER);
        page.addLine().addUnit("Font Biggest - 35", FONT_BIGEST, EAlign.CENTER);
        page.addLine().addUnit(".", FONT_NORMAL, EAlign.LEFT);
        page.addLine().addUnit(".", FONT_NORMAL, EAlign.LEFT);
        page.addLine().addUnit(".", FONT_NORMAL, EAlign.LEFT);
        page.addLine().addUnit(".", FONT_NORMAL, EAlign.LEFT);*/


//        page.addLine().addUnit(getImageFromAssetsFile("logo.png"), EAlign.CENTER);
        page.setTypefaceObj(Typeface.createFromAsset(context.getAssets(), "Roboto-Regular.ttf"));
        page.addLine().addUnit(" ", FONT_SMALL);

        String currentDate = dateFormat.format(new Date());
        String currentTime = timeFormat.format(new Date());

//        page.addLine().addUnit("on " + currentDate + " at " + currentTime, FONT_SMALL, EAlign.CENTER, IUnit.TEXT_STYLE_NORMAL);
        page.addLine().addUnit(" ", FONT_Small_NORMAL);

        /*page.addLine().addUnit("Employee: Owner", FONT_Small_NORMAL, EAlign.LEFT);
        page.addLine().addUnit("POS: POS 1", FONT_Small_NORMAL, EAlign.LEFT);
        page.addLine().addUnit("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -", FONT_NORMAL, EAlign.CENTER);
        page.addLine().addUnit("Dine in", FONT_Small_NORMAL, EAlign.LEFT);
        page.addLine().addUnit("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -", FONT_NORMAL, EAlign.CENTER);
        page.addLine()
                .addUnit("Apple", FONT_Small_NORMAL, EAlign.LEFT)
                .addUnit("Rs1.25 Rs1.25Rs1.25Rs1.25 Rs1.25 ", FONT_Small_NORMAL, EAlign.RIGHT);
        page.addLine().addUnit("1 x Rs1.25", FONT_Small_NORMAL, EAlign.LEFT);
        page.addLine().addUnit(" ", FONT_Small_NORMAL, EAlign.LEFT);
        page.addLine()
                .addUnit("Dhev", FONT_Small_NORMAL, EAlign.LEFT)
                .addUnit("Rs1.30", FONT_Small_NORMAL, EAlign.RIGHT);
        page.addLine().addUnit("1 x Rs1.30", FONT_Small_NORMAL, EAlign.LEFT);
        page.addLine().addUnit(" ", FONT_Small_NORMAL, EAlign.LEFT);
        page.addLine()
                .addUnit("Test", FONT_Small_NORMAL, EAlign.LEFT)
                .addUnit("Rs1.25", FONT_Small_NORMAL, EAlign.RIGHT);*/
        page.addLine().addUnit(" ", FONT_Small_NORMAL, EAlign.LEFT);
        page.addLine().addUnit("1 x Rs1.25", FONT_Small_NORMAL, EAlign.LEFT);
        page.addLine().addUnit("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -", FONT_NORMAL, EAlign.CENTER);
        page.addLine()
                .addUnit("Subtotal", FONT_Small_NORMAL, EAlign.LEFT,IUnit.TEXT_STYLE_BOLD)
                .addUnit("Rs3.80", FONT_Small_NORMAL, EAlign.RIGHT,IUnit.TEXT_STYLE_BOLD);
        page.addLine()
                .addUnit("Test, 6%", FONT_Small_NORMAL, EAlign.LEFT)
                .addUnit("Rs0.23", FONT_Small_NORMAL, EAlign.RIGHT);
        page.addLine().addUnit("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -", FONT_NORMAL, EAlign.CENTER);
       page.addLine()
                .addUnit("Total", FONT_BIGEST, EAlign.LEFT,IUnit.TEXT_STYLE_BOLD)
                .addUnit("Rs4.03", FONT_BIGEST, EAlign.RIGHT,IUnit.TEXT_STYLE_BOLD);
        page.addLine().addUnit(" ", FONT_Small_NORMAL, EAlign.LEFT);

        page.addLine()
                .addUnit("Cash", FONT_Small_NORMAL, EAlign.LEFT)
                .addUnit("Rs4.03", FONT_Small_NORMAL, EAlign.RIGHT);
        page.addLine().addUnit("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -", FONT_NORMAL, EAlign.CENTER);
        page.addLine()
                .addUnit( currentDate + currentTime, FONT_Small_NORMAL, EAlign.LEFT, IUnit.TEXT_STYLE_NORMAL)
                .addUnit("Rs#1-1024", FONT_Small_NORMAL, EAlign.RIGHT);
        page.addLine().addUnit(" ", FONT_Small_NORMAL, EAlign.LEFT);
        page.addLine().addUnit(" ", FONT_Small_NORMAL, EAlign.LEFT);
        page.addLine().addUnit(" ", FONT_Small_NORMAL, EAlign.LEFT);
        page.addLine().addUnit(" ", FONT_Small_NORMAL, EAlign.LEFT);



        /*page.addLine().addUnit("===================================", FONT_NORMAL, EAlign.CENTER);

        page.addLine().addUnit(" ", FONT_NORMAL);


        //page.addLine().addUnit("BEAUTIFUL SHIRT               9.99€", FONT_NORMAL);
        page.addLine()
                .addUnit("BEAUTIFUL SHIRT   ", FONT_NORMAL, EAlign.LEFT)
                .addUnit("9.99€", FONT_NORMAL, EAlign.RIGHT);
        page.addLine().addUnit("  + Size : S", FONT_NORMAL);

        //page.addLine().addUnit("AWESOME HAT                  24.99€", FONT_NORMAL);
        page.addLine()
                .addUnit("AWESOME HAT ", FONT_NORMAL, EAlign.LEFT)
                .addUnit("24.99€", FONT_NORMAL, EAlign.RIGHT);
        page.addLine().addUnit("  + Size : 57/58", FONT_NORMAL);

        page.addLine().addUnit(" ", FONT_NORMAL);
        page.addLine().addUnit("- - - - - - - - - - - - - - - - - - - - - - - - - - -- - - -", FONT_NORMAL, EAlign.CENTER);

        //page.addLine().addUnit("TOTAL PRICE :                34.98€", FONT_NORMAL);
        page.addLine()
                .addUnit("TOTAL PRICE : ", FONT_NORMAL, EAlign.LEFT)
                .addUnit("34.98€", FONT_NORMAL, EAlign.RIGHT);
        page.addLine()
               .addUnit("TAX : ", FONT_NORMAL, EAlign.LEFT)
                .addUnit("4.23€", FONT_NORMAL, EAlign.RIGHT);

        page.addLine().addUnit(" ", FONT_NORMAL);

        page.addLine().addUnit("===================================", FONT_NORMAL, EAlign.CENTER);

        page.addLine().addUnit(" ", FONT_NORMAL);

        page.addLine().addUnit("Customer :", FONT_NORMAL);
        page.addLine().addUnit("Raymond DUPONT", FONT_NORMAL);
        page.addLine().addUnit("5 rue des girafes", FONT_NORMAL);
        page.addLine().addUnit("31547 PERPETES", FONT_NORMAL);
        page.addLine().addUnit("Tel : +33801201456", FONT_NORMAL);

        page.setTypefaceObj(Typeface.createFromAsset(context.getAssets(), "Roboto-Regular.ttf"));

        page.addLine().addUnit(" ", FONT_NORMAL);

        page.addLine().addUnit(getImageFromAssetsFile("logo.png"), EAlign.CENTER);
        page.setTypefaceObj(Typeface.createFromAsset(context.getAssets(), "Fangsong.ttf"));
        page.addLine().addUnit("ORDER N°045", FONT_BIGEST, EAlign.CENTER, IUnit.TEXT_STYLE_NORMAL);
        page.setTypefaceObj(Typeface.createFromAsset(context.getAssets(), "Roboto-Regular.ttf"));
        page.addLine().addUnit(" ", FONT_SMALL);*/
        String barcodeData = "12341234";
        Bitmap barcodeBitmap;
        try {
            barcodeBitmap = generateBarcode(barcodeData, BARCODE_WIDTH, BARCODE_HEIGHT);
        } catch (WriterException e) {
            e.printStackTrace();
            barcodeBitmap = null;
        }
        page.addLine().addUnit(barcodeBitmap,EAlign.CENTER);


//        int width = 570;
//        Bitmap bitmap = iPaxGLPage.pageToBitmap(page, width);

//        setBitmap(iPaxGLPage.pageToBitmap(page, width));
    }

    public Bitmap getImageFromAssetsFile(String fileName) {
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

//    public void setBitmap(Bitmap scaledBitmap) {
//        this.bitmap = scaledBitmap;
//    }

    public Bitmap getBitmap() {
        return iPaxGLPage.pageToBitmap(page, 570);
    }
    private Bitmap generateBarcode(String barcodeData, int width, int height) throws WriterException {
        BitMatrix bitMatrix = new Code128Writer().encode(barcodeData, BarcodeFormat.CODE_128, width, height, null);

        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[y * width + x] = bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF;
            }
        }

        Bitmap barcodeBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        barcodeBitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        return barcodeBitmap;
    }
    public Bitmap generateQRcode(String QRData, int width, int height) {
        Writer writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(QRData, BarcodeFormat.QR_CODE, width, height);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}