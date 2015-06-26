package com.minibot.util.io;

/**
 * @author Tyler Sedlar
 */
public abstract class InternetCallback {

    private int length = -1;

    public abstract void onDownload(int percent);

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}