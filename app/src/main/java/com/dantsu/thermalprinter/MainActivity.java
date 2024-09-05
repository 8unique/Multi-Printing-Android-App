package com.dantsu.thermalprinter;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.DeviceConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.connection.tcp.TcpConnection;
import com.dantsu.escposprinter.connection.usb.UsbPrintersConnections;
import com.dantsu.escposprinter.exceptions.EscPosConnectionException;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;
import com.dantsu.thermalprinter.async.AsyncBigImagePrinting;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private Bitmap bitmap;
    private static final String ACTION_USB_PERMISSION = "com.dantsu.thermalprinter.USB_PERMISSION";
    private UsbManager usbManager;
    private UsbDevice usbDevice;
    private DeviceConnection usbConnection;
    private TcpConnection connection1;
    private Context context;
    private TextView logTextView;
    private boolean isReceiverRegistered = false;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button button = (Button) this.findViewById(R.id.button_bluetooth_browse);
        button.setOnClickListener(view -> browseBluetoothDevice());
        button = (Button) findViewById(R.id.button_bluetooth);
        button.setOnClickListener(view -> printBluetooth());
        button = (Button) this.findViewById(R.id.button_usb);
        button.setOnClickListener(view -> printUsb());
        button = (Button) this.findViewById(R.id.button_tcp);
        button.setOnClickListener(view -> printTcp());
        button = (Button) this.findViewById(R.id.connect);
        button.setOnClickListener(view -> connect());
        button = (Button) this.findViewById(R.id.disconnect);
        button.setOnClickListener(view -> disconnect());
        button = (Button) this.findViewById(R.id.check_status);
        button.setOnClickListener(view -> statusCheck());

        logTextView = findViewById(R.id.logTextView);


        FullScreenImageView fullScreenImageView = new FullScreenImageView(MainActivity.this);
        fullScreenImageView.init();
        bitmap = fullScreenImageView.getBitmap();
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);


        Button clearButton = findViewById(R.id.clear_button);
        Spinner spinnerPrintingMethod = findViewById(R.id.spinner_printing_method);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView logTextView = findViewById(R.id.logTextView);
                logTextView.setText("");
            }
        });


    }
    /*==============================================================================================
    ======================================BLUETOOTH PART============================================
    ==============================================================================================*/

    public interface OnBluetoothPermissionsGranted {
        void onPermissionsGranted();
    }

    public static final int PERMISSION_BLUETOOTH = 1;
    public static final int PERMISSION_BLUETOOTH_ADMIN = 2;
    public static final int PERMISSION_BLUETOOTH_CONNECT = 3;
    public static final int PERMISSION_BLUETOOTH_SCAN = 4;
    private static final int PERMISSION_REQUEST_USB = 5;

    public OnBluetoothPermissionsGranted onBluetoothPermissionsGranted;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_USB && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == MainActivity.PERMISSION_BLUETOOTH_SCAN) {
                this.checkBluetoothPermissions(this.onBluetoothPermissionsGranted);
                checkUsbPermission(this);
            }
        }
    }


    public void checkBluetoothPermissions(OnBluetoothPermissionsGranted onBluetoothPermissionsGranted) {
        this.onBluetoothPermissionsGranted = onBluetoothPermissionsGranted;
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, MainActivity.PERMISSION_BLUETOOTH);
        } else if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, MainActivity.PERMISSION_BLUETOOTH_ADMIN);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, MainActivity.PERMISSION_BLUETOOTH_CONNECT);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, MainActivity.PERMISSION_BLUETOOTH_SCAN);
        } else {
            this.onBluetoothPermissionsGranted.onPermissionsGranted();
        }
    }

    private BluetoothConnection selectedDevice;

    public void browseBluetoothDevice() {
        this.checkBluetoothPermissions(() -> {
            final BluetoothConnection[] bluetoothDevicesList = (new BluetoothPrintersConnections()).getList();

            if (bluetoothDevicesList != null) {
                final String[] items = new String[bluetoothDevicesList.length + 1];
                items[0] = "Default printer";
                int i = 0;
                for (BluetoothConnection device : bluetoothDevicesList) {
                    items[++i] = device.getDevice().getName();
                }

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Bluetooth printer selection");
                alertDialog.setItems(
                        items,
                        (dialogInterface, i1) -> {
                            int index = i1 - 1;
                            if (index == -1) {
                                selectedDevice = null;
                            } else {
                                selectedDevice = bluetoothDevicesList[index];
                            }
                            Button button = (Button) findViewById(R.id.button_bluetooth_browse);
                            button.setText(items[i1]);
                        }
                );

                AlertDialog alert = alertDialog.create();
                alert.setCanceledOnTouchOutside(false);
                alert.show();
            }
        });

    }

    public void printBluetooth() {
        this.checkBluetoothPermissions(() -> {

            Spinner spinnerPrintingMethod = findViewById(R.id.spinner_printing_method);
            String selectedMethod = spinnerPrintingMethod.getSelectedItem().toString();

            if ("Graphic Printing".equals(selectedMethod)) {
                bigImagePrinting(Connect.getBluetoothDevice(MainActivity.this, "02:08:1D:C2:5E:95"));
            } else if ("Text Printing".equals(selectedMethod)) {
                textPrint(Connect.getBluetoothDevice(MainActivity.this, "02:08:1D:C2:5E:95"));
            }
        });
    }

    /*==============================================================================================
    ===========================================USB PART=============================================
    ==============================================================================================*/

    private void checkUsbPermission(Context context) {

        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        if (!deviceList.isEmpty()) {
            for (UsbDevice device : deviceList.values()) {
                if (usbManager.hasPermission(device)) {
                    usbDevice = device;
                    openUsbConnection();
                    return;
                }
            }
            UsbDevice firstDevice = deviceList.values().iterator().next();
            PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE);
            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);

            context.registerReceiver(usbReceiver, filter, Context.RECEIVER_NOT_EXPORTED);

            usbManager.requestPermission(firstDevice, permissionIntent);
        } else {
            Log.e("USB", "No USB devices found.");
        }
    }

    private void openUsbConnection() {
        usbConnection = UsbPrintersConnections.selectFirstConnected(this);
        if (usbConnection != null) {
            printUsb();
        } else {
            Log.e("USB", "Failed to open USB connection.");
        }
    }

    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            usbDevice = device;
                            openUsbConnection();
                        }
                    } else {
                        Log.e("USB", "Permission denied for USB device: " + device);
                    }
                }
            }
        }
    };

    private void printUsb() {
        checkUsbPermission(this);
        if (usbConnection != null) {
//            bigImagePrinting(usbConnection);

            try {
                Spinner spinnerPrintingMethod = findViewById(R.id.spinner_printing_method);
                String selectedMethod = spinnerPrintingMethod.getSelectedItem().toString();

                if ("Graphic Printing".equals(selectedMethod)) {
                    try {
                        bigImagePrinting(usbConnection);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "USB connection failed: " + e.getMessage());
                        logToText2("USB connection failed: ");
                    }
                } else if ("Text Printing".equals(selectedMethod)) {
                    textPrint(usbConnection);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Invalid port number", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*==============================================================================================
    =========================================TCP PART===============================================
    ==============================================================================================*/

    public void printTcp() {
        final EditText ipAddress = findViewById(R.id.edittext_tcp_ip);
        String ip = ipAddress.getText().toString();
        String port = "9100";

        context = MainActivity.this;

        if (ip.isEmpty()) {
            vibratePhone();
/*            edittext_tcp_ip.setTextColor(context.getResources().getColor(R.color.red));
            edittext_tcp_ip.getBackground().setColorFilter(context.getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);*/
            ipAddress.setTextColor(MainActivity.this.getResources().getColor(R.color.red));
            ipAddress.getBackground().setColorFilter(MainActivity.this.getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
            Toast.makeText(this, "Please enter IP address", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            final TcpConnection connection = new TcpConnection(ip, Integer.parseInt(port));
            Spinner spinnerPrintingMethod = findViewById(R.id.spinner_printing_method);
            String selectedMethod = spinnerPrintingMethod.getSelectedItem().toString();

            if ("Graphic Printing".equals(selectedMethod)) {
                try {
                    bigImagePrinting(new TcpConnection(ip, 9100, 4000));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "TCP connection failed: " + e.getMessage());
                    logToText2("TCP connection failed: ");
                }
            } else if ("Text Printing".equals(selectedMethod)) {
                TcpConnection connection1 = new TcpConnection(ip, 9100, 5000);

                /*boolean isPrinterReady = checkPrinterStatus(ip);
                isPrinterReady;
                if(isPrinterReady){*/
                textPrint(connection1);
                sendAndResponse(connection1);
                /*else {
                    logToText("Is the printer ready? " + isPrinterReady);
                }*/

            }
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "err", Toast.LENGTH_SHORT).show();
        }
    }


    /*==============================================================================================
    ===================================ESC/POS PRINTER PART=========================================
    ==============================================================================================*/
    /**
     * Asynchronous printing
     * }
     */


    private Handler handler = new Handler();

    private void bigImagePrinting(final DeviceConnection printerConnection) {

        EditText roundsEditText = findViewById(R.id.edittext_rounds);
        EditText delayEditText = findViewById(R.id.edittext_delay);

        String roundsText = roundsEditText.getText().toString();
        String delayText = delayEditText.getText().toString();

        context = MainActivity.this;

        if (roundsText.isEmpty()) {
            roundsEditText.setBackground(ContextCompat.getDrawable(this, R.drawable.edittext_bottom_line));

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    roundsEditText.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.edittext_bottom_line_black));
                }
            }, 2000);
        }

        if (delayText.isEmpty()) {
            delayEditText.setBackground(ContextCompat.getDrawable(this, R.drawable.edittext_bottom_line));

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    delayEditText.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.edittext_bottom_line_black));
                }
            }, 2000);
        }

        if (roundsText.isEmpty() || delayText.isEmpty()) {
            vibratePhone();
            Toast.makeText(MainActivity.this, "Please enter values for rounds and delay", Toast.LENGTH_SHORT).show();
            return;
        }

        int numberOfPrints = Integer.parseInt(roundsEditText.getText().toString());
        int delayInSeconds = Integer.parseInt(delayEditText.getText().toString());

        for (int i = 0; i < numberOfPrints; i++) {
            final int currentRound = i + 1;


            AsyncBigImagePrinting bigImagePrinting = new AsyncBigImagePrinting(printerConnection, MainActivity.this, bitmap, logTextView) {
                @Override
                public Boolean onError() {
                    Log.e(TAG, "Error occurred on round " + currentRound);
                    MainActivity.this.logToText2("Print failed on round " + currentRound);
                    return false;
                }

                @Override
                public Boolean onSuccess() {
                    Log.d(TAG, "Print Success on round " + currentRound);
                    MainActivity.this.logToText("Print Success on round " + currentRound);
                    return true;
                }
            };
            bigImagePrinting.execute();

            try {
                Thread.sleep(delayInSeconds * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void textPrint(final DeviceConnection printerConnection) {
        EditText roundsEditText = findViewById(R.id.edittext_rounds);
        EditText delayEditText = findViewById(R.id.edittext_delay);

        String roundsText = roundsEditText.getText().toString();
        String delayText = delayEditText.getText().toString();

        if (roundsText.isEmpty()) {
            roundsEditText.setTextColor(getResources().getColor(R.color.red));
            roundsEditText.getBackground().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
        }

        if (delayText.isEmpty()) {
            delayEditText.setTextColor(getResources().getColor(R.color.red));
            delayEditText.getBackground().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
        }

        if (roundsText.isEmpty() || delayText.isEmpty()) {
            vibratePhone();
            Toast.makeText(MainActivity.this, "Please enter values for rounds and delay", Toast.LENGTH_SHORT).show();
            return;
        }

        int numberOfPrints = Integer.parseInt(roundsText);
        int delayInSeconds = Integer.parseInt(delayText);


        new Thread(new Runnable() {
            public void run() {
                for (int i = 0; i < numberOfPrints; i++) {
                    final int currentRound = i + 1;

                    try {
//                        printerConnection.connect();
                        clearPrinterCache(printerConnection);

                        EscPosPrinter printer = new EscPosPrinter(printerConnection, 203, 48f, 48);
                        printer.printFormattedTextAndCut(
                                "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, context.getResources().getDrawableForDensity(R.drawable.receipt_logo, DisplayMetrics.DENSITY_MEDIUM)) + "</img>\n" +
                                        "[C]<u><font size='big'>ORDER N°045</font></u>\n" +
                                        "[L]\n" +
                                        "[L]\n" +
                                        "[C]================================\n" +
                                        "[L]\n" +
                                        "[L]<b>BEAUTIFUL SHIRT</b>[R]9.99e\n" +
                                        "[L]  + Size : S\n" +
                                        "[L]\n" +
                                        "[L]<b>AWESOME HAT</b>[R]24.99e\n" +
                                        "[L]  + Size : 57/58\n" +
                                        "[L]Item 1 [R]$1.00\n" +
                                        "[L]Item 3 [R]$3.00\n" +
                                        "[L]\n" +
                                        "[L]\n"
                        );

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                logToText("Print Success on round " + currentRound);
                                clearPrinterCache(printerConnection);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                logToText2("Print failed on round " + currentRound);
                            }
                        });
                    }

                    try {
                        Thread.sleep(delayInSeconds * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
//                printerConnection.disconnect();
            }
        }).start();
        clearPrinterCache(printerConnection);
    }
    private void statusCheck(){
        final EditText ipAddress = findViewById(R.id.edittext_tcp_ip);
        String ip = ipAddress.getText().toString();
        String port = "9100";

        TcpConnection connection = new TcpConnection(ip, 9100, 5000);

        byte [] statusCommand = new byte[]{0x10,0x04,0x01}; // online status
        try {
            connection.write(statusCommand);
            byte[] statusResponse = connection.read();

            if (statusResponse != null && statusResponse.length > 0) {
                // Convert byte array to hex string for logging
                StringBuilder responseString = new StringBuilder();
                for (byte b : statusResponse) {
                    responseString.append(String.format("%02X ", b));
                }

                // Log and display the status response as hex
                logToText("statusResponse (Hex): " + responseString.toString());
                Log.d("", "statusResponse (Hex): " + responseString.toString());

                // Optionally display the status response as a string
                logToText("statusResponse (Text): " + new String(statusResponse, StandardCharsets.UTF_8));
            } else {
                logToText("No response received from the device.");
                Log.d("", "No response received from the device.");
            }

        }catch (Exception e){
            Log.d("","error checking status");
        }
    }

    private void clearPrinterCache(DeviceConnection printerConnection) {
        try {
            byte[] initCommand = {0x1B, 0x40};
            byte[] clearBuffer = {0x10, 0x14, 0x08, 0x01, 0x03, 0x14, 0x01, 0x06, 0x02, 0x08};
            printerConnection.write(initCommand);
            printerConnection.write(clearBuffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connect() {
        final EditText ipAddress = findViewById(R.id.edittext_tcp_ip);
        String ip = ipAddress.getText().toString();

        new ConnectTask().execute(ip);
    }

    private void disconnect() {
        new DisconnectTask().execute();
    }

    private class ConnectTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            String ip = params[0];
            connection1 = new TcpConnection(ip, 9100, 5000);

            try {
                connection1.connect();
                return true;  // Connection established
            } catch (EscPosConnectionException e) {
                e.printStackTrace();
                return false;  // Connection failed
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                logToText("Connection established.");
            } else {
                logToText("Failed to establish connection.");
            }
        }
    }

    private class DisconnectTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            if (connection1 != null && connection1.isConnected()) {
                connection1.disconnect();
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                logToText("Disconnected successfully.");
            } else {
                logToText("Failed to disconnect or no active connection.");
            }
        }
    }

    private void logToText(String logMessage) {
        logTextView.append(getCurrentDateTime() + "   " + logMessage + "\n");
    }

    private void logToText2(String logMessage) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        SpannableString spannableString = new SpannableString(getCurrentDateTime() + "   " + logMessage + "\n");
        spannableString.setSpan(new ForegroundColorSpan(Color.RED), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        logTextView.append(spannableString);
    }

    private void sendAndResponse(DeviceConnection printerConnection) {
        if (printerConnection != null) {
            try {
                byte[] initCommand = {0x1B, 0x40};
                byte[] clearBufferCommand = new byte[]{0x10, 0x14, 0x08, 0x01, 0x03, 0x14, 0x01, 0x06, 0x02, 0x08};
                printerConnection.write(initCommand);
                printerConnection.write(clearBufferCommand);

                InputStream inputStream = printerConnection.getInputStream();
                byte[] response = new byte[3];
                int bytesRead = inputStream.read(response);

                String responseHex = String.format("%02X %02X %02X", response[0], response[1], response[2]);
                logToText("Response: " + responseHex);
                Log.d(TAG, "Response: " + responseHex);

            } catch (IOException e) {
                logToText2("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(System.currentTimeMillis());
    }

    void vibratePhone() {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(100);
        }
    }

    private static final int PORT = 9100;

    public static boolean checkPrinterStatus(String ip) {
        try (Socket socket = new Socket(ip, PORT)) {
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();

            outputStream.write(new byte[]{0x10, 0x04, 0x01});
            outputStream.flush();
            int response = inputStream.read();
            if (response != -1) {
                if ((response & 0x08) != 0) {
                    System.out.println("Printer is busy.");
                    return false;
                } else {
                    System.out.println("Printer is ready.");
                }
            }

            outputStream.write(new byte[]{0x10, 0x04, 0x02});
            outputStream.flush();
            response = inputStream.read();
            if (response != -1) {
                if ((response & 0x04) != 0) {
                    System.out.println("Printer cover is open.");
                    return false;
                } else {
                    System.out.println("Printer cover is closed.");
                }
            }

            outputStream.write(new byte[]{0x10, 0x04, 0x03});
            outputStream.flush();
            response = inputStream.read();
            if (response != -1) {
                if ((response & 0x01) != 0) {
                    System.out.println("Paper is out.");
                    return false;
                } else {
                    System.out.println("Paper is loaded.");
                }
            }

            outputStream.write(new byte[]{0x10, 0x04, 0x04});
            outputStream.flush();
            response = inputStream.read();
            if (response != -1) {
                if ((response & 0x20) != 0) {
                    System.out.println("Paper is being fed by the paper feed button.");
                } else {
                    System.out.println("Paper is not being fed.");
                }
            }

            return true;

        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isReceiverRegistered) {
            unregisterReceiver(usbReceiver);
            isReceiverRegistered = false;
        }
    }

}

