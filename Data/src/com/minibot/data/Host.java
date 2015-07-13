package com.minibot.data;

import com.sirc.Channel;
import com.sirc.IrcConnection;

/**
 * @author Tim Dekker
 * @since 6/3/15
 */
public class Host {

    public static void main(String... args) {
        try {
            Listener listener = new Listener();
            IrcConnection instance = new IrcConnection("irc.foonetic.net");
            instance.setNick("Master");
            instance.connect();
            instance.addMessageListener(listener);
            Channel channel = instance.createChannel("Minibot");
            channel.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}