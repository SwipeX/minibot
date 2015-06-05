package com.minibot.data;

import com.sirc.Channel;
import com.sirc.IrcConnection;
import com.sirc.MessageListener;
import com.sirc.User;

import java.util.List;

/**
 * Created by tim on 6/3/15.
 */
public class Host {

    private static IrcConnection instance;
    private static Listener listener;

    public static void main(String[] args) {
        try {
            listener = new Listener();
            instance = new IrcConnection("irc.foonetic.net");
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
