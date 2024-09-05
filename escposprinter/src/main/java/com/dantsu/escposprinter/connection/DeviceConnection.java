package com.dantsu.escposprinter.connection;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.dantsu.escposprinter.exceptions.EscPosConnectionException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class DeviceConnection {
    protected OutputStream outputStream;
    protected InputStream inputStream;
    protected byte[] data;

    public DeviceConnection() {
        this.outputStream = null;
        this.inputStream = null;
        this.data = new byte[0];
    }

    public abstract DeviceConnection connect() throws EscPosConnectionException;
    public abstract DeviceConnection disconnect();

    public boolean isConnected() {
        return this.outputStream != null;
    }

    public InputStream getInputStream() {
        return this.inputStream;
    }

    public byte[] read() throws EscPosConnectionException, IOException {
        if (!this.isConnected()) {
            throw new EscPosConnectionException("Unable to read data from device.");
        }
        try {
            this.inputStream.read(this.data);
            int waitingTime = this.data.length / 16;
            this.data = new byte[0];
            if(waitingTime > 0) {
                Thread.sleep(waitingTime);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new EscPosConnectionException(e.getMessage());
        }

        return null; // No data read
    }

    public void write(byte[] bytes) {
        byte[] data = new byte[bytes.length + this.data.length];
        System.arraycopy(this.data, 0, data, 0, this.data.length);
        System.arraycopy(bytes, 0, data, this.data.length, bytes.length);
        this.data = data;
    }

    public void send() throws EscPosConnectionException {
        this.send(0);
    }

    public void send(int addWaitingTime) throws EscPosConnectionException {
        if(!this.isConnected()) {
            throw new EscPosConnectionException("Unable to send data to device.");
        }
        try {
            this.outputStream.write(this.data);
            this.outputStream.flush();
            int waitingTime = addWaitingTime + this.data.length / 16;
            this.data = new byte[0];
            if(waitingTime > 0) {
                Thread.sleep(waitingTime);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new EscPosConnectionException(e.getMessage());
        }
    }
}
