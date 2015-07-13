/*
 * IrcAdaptor.java
 * 
 * This file is part of the Sorcix Java IRC Library (sIRC).
 * 
 * Copyright (C) 2008-2010 Vic Demuzere http://sorcix.com
 * 
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.sorcix.sirc;

/**
 * Implements all sIRC listeners. Extending this class allows you to
 * listen to events by overriding its methods. This requires you to
 * register the class as {@code ServerListener}, {@code
 * MessageListener} and {@code ModeListener} based on which events you
 * want to receive.
 *
 * @author Sorcix
 */
public abstract class IrcAdaptor implements ServerListener, MessageListener, ModeListener {

    @Override
    public void onAction(IrcConnection irc, User sender, Channel target, String action) {
    }

    @Override
    public void onAction(IrcConnection irc, User sender, String action) {
    }

    @Override
    public void onAdmin(IrcConnection irc, Channel channel, User sender, User user) {
    }

    @Override
    public void onConnect(IrcConnection irc) {
    }

    @Override
    public void onCtcpReply(IrcConnection irc, User sender, String command, String message) {
    }

    @Override
    public void onDeAdmin(IrcConnection irc, Channel channel, User sender, User user) {
    }

    @Override
    public void onDeFounder(IrcConnection irc, Channel channel, User sender, User user) {
    }

    @Override
    public void onDeHalfop(IrcConnection irc, Channel channel, User sender, User user) {
    }

    @Override
    public void onDeOp(IrcConnection irc, Channel channel, User sender, User user) {
    }

    @Override
    public void onDeVoice(IrcConnection irc, Channel channel, User sender, User user) {
    }

    @Override
    public void onDisconnect(IrcConnection irc) {
    }

    @Override
    public void onFounder(IrcConnection irc, Channel channel, User sender, User user) {
    }

    @Override
    public void onHalfop(IrcConnection irc, Channel channel, User sender, User user) {
    }

    @Override
    public void onInvite(IrcConnection irc, User sender, User user, Channel channel) {
    }

    @Override
    public void onJoin(IrcConnection irc, Channel channel, User user) {
    }

    @Override
    public void onKick(IrcConnection irc, Channel channel, User sender, User user, String message) {
    }

    @Override
    public void onMessage(IrcConnection irc, User sender, Channel target, String message) {
    }

    @Override
    public void onMode(IrcConnection irc, Channel channel, User sender, String mode) {
    }

    @Override
    public void onMotd(IrcConnection irc, String motd) {
    }

    @Override
    public void onNick(IrcConnection irc, User oldUser, User newUser) {
    }

    @Override
    public void onNotice(IrcConnection irc, User sender, Channel target, String message) {
    }

    @Override
    public void onNotice(IrcConnection irc, User sender, String message) {
    }

    @Override
    public void onOp(IrcConnection irc, Channel channel, User sender, User user) {
    }

    @Override
    public void onPart(IrcConnection irc, Channel channel, User user, String message) {
    }

    @Override
    public void onPrivateMessage(IrcConnection irc, User sender, String message) {
    }

    @Override
    public void onQuit(IrcConnection irc, User user, String message) {
    }

    @Override
    public void onTopic(IrcConnection irc, Channel channel, User sender, String topic) {
    }

    @Override
    public void onVoice(IrcConnection irc, Channel channel, User sender, User user) {
    }
}