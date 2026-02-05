# Multi-Printing Android App

An Android application for thermal receipt printing with support for **Bluetooth**, **USB**, and **TCP/IP** connections. This project includes a comprehensive ESC/POS printer library for formatting and printing receipts, labels, and images on thermal printers.

![Android](https://img.shields.io/badge/Android-19%2B-green)
![License](https://img.shields.io/badge/License-MIT-blue)
![Java](https://img.shields.io/badge/Java-8-orange)

## Features

- **Bluetooth Printing** - Connect and print to Bluetooth thermal printers
- **USB Printing** - Direct USB connection support for compatible printers
- **TCP/IP Printing** - Network printing over WiFi/Ethernet (port 9100)
- **Formatted Text** - Rich text formatting with alignment, sizes, and styles
- **Barcode Support** - Print various barcode formats (EAN13, EAN8, UPCA, UPCE, Code 39, Code 128, ITF)
- **QR Code Printing** - Generate and print QR codes
- **Image Printing** - Print images and bitmaps on thermal printers
- **Auto Paper Cut** - Automatic paper cutting after print jobs

## Installation

### Requirements

- Android SDK 19+ (Android 4.4 KitKat or higher)
- Gradle 7.0+
- Java 8

### Clone the Repository

```bash
git clone https://github.com/8unique/Multi-Printing-Android-App.git
cd Multi-Printing-Android-App
```

### Build the Project

```bash
./gradlew build
```

Or open the project in Android Studio and build from there.

## Usage

### Permissions

The app requires the following permissions (declared in `AndroidManifest.xml`):

```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
```

### Basic Printing Example

#### TCP/IP Connection

```java
TcpConnection connection = new TcpConnection("192.168.1.100", 9100);
EscPosPrinter printer = new EscPosPrinter(connection, 203, 48f, 32);

printer.printFormattedText(
    "[C]<u><font size='big'>ORDER N°045</font></u>\n" +
    "[L]\n" +
    "[C]================================\n" +
    "[L]\n" +
    "[L]<b>BEAUTIFUL SHIRT</b>[R]$9.99\n" +
    "[L]  + Size : S\n" +
    "[L]\n" +
    "[L]<b>AWESOME HAT</b>[R]$24.99\n" +
    "[L]  + Size : 57/58\n" +
    "[L]\n" +
    "[C]--------------------------------\n" +
    "[L]TOTAL[R]$34.98\n" +
    "[L]\n" +
    "[C]<qrcode size='20'>http://example.com</qrcode>\n"
);
```

#### Bluetooth Connection

```java
BluetoothConnection connection = BluetoothPrintersConnections.selectFirstPaired();
EscPosPrinter printer = new EscPosPrinter(connection, 203, 48f, 32);
printer.printFormattedText("[C]<font size='big'>Hello World!</font>\n");
```

#### USB Connection

```java
UsbConnection usbConnection = UsbPrintersConnections.selectFirstConnected(context);
EscPosPrinter printer = new EscPosPrinter(usbConnection, 203, 48f, 32);
printer.printFormattedText("[C]USB Print Test\n");
```

## Text Formatting Tags

The library supports a rich set of formatting tags for thermal printer output:

### Alignment Tags

| Tag | Description |
|-----|-------------|
| `[L]` | Align text to the left |
| `[C]` | Center align text |
| `[R]` | Align text to the right |

### Text Formatting

| Tag | Description |
|-----|-------------|
| `<b>text</b>` | Bold text |
| `<u>text</u>` | Underlined text |
| `<font size='normal'>` | Normal text size |
| `<font size='wide'>` | Double width text |
| `<font size='tall'>` | Double height text |
| `<font size='big'>` | Double width and height |
| `<font size='big-2'>` | 3x size |
| `<font size='big-3'>` | 4x size |
| `<font size='big-4'>` | 5x size |
| `<font size='big-5'>` | 6x size |
| `<font size='big-6'>` | 7x size |

### Barcodes and QR Codes

```java
// QR Code
"[C]<qrcode size='20'>https://example.com</qrcode>\n"

// Barcode (EAN13)
"[C]<barcode type='ean13' height='10'>1234567890123</barcode>\n"
```

**Supported Barcode Types:**
- `ean13` - EAN-13
- `ean8` - EAN-8
- `upca` - UPC-A
- `upce` - UPC-E
- `39` - Code 39
- `128` - Code 128
- `itf` - Interleaved 2 of 5

### Images

```java
// Print image from drawable resource
"<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, bitmap) + "</img>"
```

## Project Structure

```
Multi-Printing-Android-App/
├── app/                          # Main Android application
│   └── src/main/
│       ├── java/.../thermalprinter/
│       │   ├── MainActivity.java          # Main activity with UI
│       │   ├── TextPrintTemplate.java     # Print template examples
│       │   ├── Connect.java               # Connection management
│       │   └── async/                     # Async printing tasks
│       │       ├── AsyncEscPosPrint.java
│       │       ├── AsyncBigImagePrinting.java
│       │       └── ...
│       └── res/                   # Resources (layouts, drawables)
│
├── escposprinter/                 # ESC/POS Printer Library
│   └── src/main/java/.../escposprinter/
│       ├── EscPosPrinter.java            # Main printer class
│       ├── EscPosPrinterCommands.java    # Raw ESC/POS commands
│       ├── EscPosPrinterSize.java        # Printer size calculations
│       ├── EscPosCharsetEncoding.java    # Character encoding
│       ├── connection/                   # Connection types
│       │   ├── DeviceConnection.java
│       │   ├── bluetooth/
│       │   ├── tcp/
│       │   └── usb/
│       ├── textparser/                   # Text formatting parser
│       │   ├── PrinterTextParser.java
│       │   ├── PrinterTextParserBarcode.java
│       │   ├── PrinterTextParserQRCode.java
│       │   └── PrinterTextParserImg.java
│       ├── barcode/                      # Barcode generation
│       └── exceptions/                   # Custom exceptions
│
└── gradle/                        # Gradle wrapper
```

## Async Printing

For better UI responsiveness, use the async printing classes:

```java
new AsyncEscPosPrint(context, new AsyncEscPosPrint.OnPrintFinished() {
    @Override
    public void onError(AsyncEscPosPrinter asyncEscPosPrinter, int codeException) {
        // Handle error
    }
    
    @Override
    public void onSuccess(AsyncEscPosPrinter asyncEscPosPrinter) {
        // Print successful
    }
}).execute(asyncEscPosPrinter);
```

## Printer Configuration

When creating an `EscPosPrinter` instance, you need to specify:

| Parameter | Description | Example |
|-----------|-------------|---------|
| `connection` | Device connection (TCP, Bluetooth, USB) | `TcpConnection("192.168.1.100", 9100)` |
| `printerDpi` | Printer DPI (dots per inch) | `203` (common for 58mm printers) |
| `printerWidthMM` | Print width in millimeters | `48f` (58mm paper), `72f` (80mm paper) |
| `printerNbrCharactersPerLine` | Max characters per line | `32` (58mm), `48` (80mm) |

## Compatible Printers

This library is compatible with most ESC/POS thermal printers, including:

- Generic 58mm/80mm thermal printers
- Epson TM series
- Star Micronics
- Bixolon
- Citizen
- Most POS receipt printers supporting ESC/POS commands

## Dependencies

```gradle
implementation 'androidx.appcompat:appcompat:1.6.1'
implementation 'com.journeyapps:zxing-android-embedded:4.3.0'  // For QR code generation
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2025

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

## Acknowledgments

- Based on ESC/POS thermal printer command standards
- QR Code generation powered by [ZXing](https://github.com/zxing/zxing)
- Inspired by the Android thermal printing community

## Support

For issues and feature requests, please [open an issue](https://github.com/yourusername/Multi-Printing-Android-App/issues).
