package com.minibot.client.natives;

public interface RSHashTable extends ClientNative {

    int getIndex();

    int getSize();

    RSNode[] getBuckets();

    RSNode getHead();

    RSNode getTail();
}