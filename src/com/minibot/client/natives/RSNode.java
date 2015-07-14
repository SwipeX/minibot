package com.minibot.client.natives;

public interface RSNode extends ClientNative {

    RSNode getPrevious();

    RSNode getNext();

    long getUid();
}