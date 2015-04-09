package com.minibot.client.natives;

/**
 * Project: minibot
 * Date: 08-04-2015
 * Time: 19:10
 * Created by Dogerina.
 * Copyright under GPL license by Dogerina.
 */
public interface RSHashTable extends ClientNative {
    int getIndex();
    int getSize();
    RSNode[] getBuckets();
    RSNode getHead();
    RSNode getTail();
}
