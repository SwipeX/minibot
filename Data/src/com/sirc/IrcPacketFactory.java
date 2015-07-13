package com.sirc;

public final class IrcPacketFactory {

    protected static IrcPacket createAWAY(String reason) {
        return new IrcPacket(null, "AWAY", null, reason);
    }

    protected static IrcPacket createMOTD() {
        return new IrcPacket(null, "MOTD", null, null);
    }

    protected static IrcPacket createNAMES(String channel) {
        return new IrcPacket(null, "NAMES", channel, null);
    }

    protected static IrcPacket createNICK(String nick) {
        return new IrcPacket(null, "NICK", nick, null);
    }

    protected static IrcPacket createPASS(String password) {
        return new IrcPacket(null, "PASS", password, null);
    }

    protected static IrcPacket createQUIT(String message) {
        return new IrcPacket(null, "QUIT", null, message);
    }

    protected static IrcPacket createUSER(String username,
                                          String realname) {
        return new IrcPacket(null, "USER", username + " Sorcix.com *", realname);
    }
}