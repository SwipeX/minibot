package com.minibot.api.method;

/**
 * @author Tyler Sedlar
 * @since 6/28/2015
 */
public interface ChatboxListener {

    void messageReceived(int type, String sender, String message, String clan);
}
