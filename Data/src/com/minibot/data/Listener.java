package com.minibot.data;

import com.sirc.Channel;
import com.sirc.IrcConnection;
import com.sirc.MessageListener;
import com.sirc.User;

public class Listener implements MessageListener {

    @Override
    public void onMessage(IrcConnection irc, User sender, Channel target, String message) {
        System.out.println(message);
        String raw = Crypto.decrypt(message);
        System.out.println(raw);
        RespondentPool.process(raw);
    }

    //UNUSED BELOW
    @Override
    public void onAction(IrcConnection irc, User sender, Channel target, String action) {

    }

    @Override
    public void onAction(IrcConnection irc, User sender, String action) {

    }

    @Override
    public void onCtcpReply(IrcConnection irc, User sender, String command, String message) {

    }

    @Override
    public void onNotice(IrcConnection irc, User sender, Channel target, String message) {

    }

    @Override
    public void onNotice(IrcConnection irc, User sender, String message) {

    }

    @Override
    public void onPrivateMessage(IrcConnection irc, User sender, String message) {

    }


}