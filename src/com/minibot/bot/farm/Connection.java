package com.minibot.bot.farm;

import com.minibot.api.util.Random;
import com.minibot.util.Crypto;
import com.sorcix.sirc.Channel;
import com.sorcix.sirc.IrcConnection;

/**
 * @author Tim Dekker
 * @since 6/3/15.
 */
public class Connection {

    public static final String DEFAULT_IRC = "irc.foonetic.net";
    public static final String DEFAULT_CHANNEL = "Minibot";
    private Channel channel;

    public Connection(String server) {
        try {
            IrcConnection instance = new IrcConnection(server);
            instance.setNick("Mini-" + Random.nextInt(0, 100000));
            instance.connect();
            channel = instance.createChannel(DEFAULT_CHANNEL);
            channel.join();
            channel.send("OK");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void script(int type, String rsn, String macro) {
        channel.send(Crypto.encrypt((type == 0 ? "START" : "FINISH") + String.format("&%s&%s", rsn, macro)));
    }

    public void chin(String rsn, int chins, int runtime) {
        channel.send(Crypto.encrypt(rsn + "&" + chins + "&" + runtime));
    }
}