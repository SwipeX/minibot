package com.minibot.client.natives;

public interface RSLinkedList extends ClientNative {
    RSNode getSentinel();
    RSNode getTail();
}
