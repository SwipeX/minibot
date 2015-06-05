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
}