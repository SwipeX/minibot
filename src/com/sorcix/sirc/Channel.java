/*
 * Channel.java
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

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a channel on the IRC server.
 *
 * @author Sorcix
 */
public final class Channel {

    /** IrcConnection used to send messages to this channel. */
    private final IrcConnection irc;
    /** Channel name */
    private final String name;
    /** The topic of this channel. */
    private String topic;
    /** The user list. */
    private final ConcurrentHashMap<String, User> users;
    /** Possible channel prefixes. */
    protected static final String CHANNEL_PREFIX = "#&+!";

    /**
     * Creates a new {@code Channel} object with given name.
     *
     * @param name The channel name.
     * @param irc The IrcConnection used to send messages to this
     * channel.
     * @param global Whether this object is going to be shared.
     */
    protected Channel(String name, IrcConnection irc, boolean global) {
        this.name = name;
        this.irc = irc;
        users = global ? new ConcurrentHashMap<String, User>(100, .75f, 2) : null;
    }

    /**
     * Adds a user to the user list in this channel.
     *
     * @param user The user to add.
     */
    protected void addUser(User user) {
        if (users != null) {
            users.putIfAbsent(user.getNickLower(), user);
        }
    }

    /**
     * Bans a user from this channel.
     *
     * @param user The user to ban from this channel.
     * @param kick Whether to kick this user after banning.
     */
    public void ban(User user, boolean kick) {
        ban(user, false, null);
    }

    /**
     * Bans a user from this channel with an optional kick message.
     *
     * @param user The User to ban from this channel.
     * @param kick Whether to kick this user after banning
     * @param reason The message to append to the kick sent to the user.
     */
    public void ban(User user, boolean kick, String reason) {
        if (user.getHostName() != null) {
            setMode("+b *!*@*" + user.getHostName());
        } else {
            setMode("+b " + user.getNick() + "!*@*");
        }

        if (kick) {
            if (reason == null) {
                kick(user, "Banned");
            } else {
                kick(user, reason);
            }
        }
    }

    /**
     * Changes the topic of this channel. Note that you need
     * privileges to do this.
     *
     * @param topic The new topic.
     */
    public void changeTopic(String topic) {
        irc.getOutput().send("TOPIC " + name + " :" + topic);
    }

    @Override
    public boolean equals(Object channel) {
        try {
            return ((Channel) channel).name.equalsIgnoreCase(name) && (irc != null && irc.equals(((Channel) channel).irc));
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Returns the channel name.
     *
     * @return The channel name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gives the topic of this channel, or null if unknown.
     *
     * @return The topic.
     */
    public String getTopic() {
        return topic;
    }

    /**
     * Retrieves a global User object for a user in this channel. This
     * method is not public because end-users should use
     * {@link IrcConnection#createUser(String, String)} which always
     * returns a {@link User}, even if the user is not in this
     * channel.
     *
     * @param nickLower The nickname of this user.
     * @return A user object, or null if the user isn't in this
     * channel.
     */
    protected User getUser(String nickLower) {
        return users.get(nickLower);
    }

    public User getUs() {
        return users.get(irc.getClient().getNickLower());
    }

    /**
     * Get an Iterator containing all users in this channel.
     * <p>
     * <pre>
     * Iterator&lt;User&gt; users = channel.getUsers();
     * User current;
     * while (users.hasNext()) {
     * 	current = users.next();
     * 	System.out.println(current.getNick() + &quot; is in this channel!&quot;);
     * }
     * </pre>
     *
     * @return All users in this channel.
     * @see #isGlobal()
     */
    public Iterator<User> getUsers() {
        return users.values().iterator();
    }

    /**
     * Give a user admin privileges in this channel. (Not supported by
     * RFC!)
     *
     * @param user The user to give admin privileges.
     * @since 1.0.0
     */
    public void giveAdmin(User user) {
        setMode(User.MODE_ADMIN, user, true);
    }

    /**
     * Give a user founder privileges in this channel. (Not supported
     * by RFC!)
     *
     * @param user The user to give founder privileges.
     * @since 1.0.0
     */
    public void giveFounder(User user) {
        setMode(User.MODE_FOUNDER, user, true);
    }

    /**
     * Give a user halfop privileges in this channel. (Not supported
     * by RFC!)
     *
     * @param user The user to give halfop privileges.
     * @since 1.0.0
     */
    public void giveHalfop(User user) {
        setMode(User.MODE_HALF_OP, user, true);
    }

    /**
     * Give a user operator privileges in this channel.
     *
     * @param user The user to give operator privileges.
     */
    public void giveOperator(User user) {
        setMode(User.MODE_OPERATOR, user, true);
    }

    /**
     * Give a user voice privileges in this channel.
     *
     * @param user The user to give voice privileges.
     */
    public void giveVoice(User user) {
        setMode(User.MODE_VOICE, user, true);
    }

    /**
     * Checks whether given user is in this channel.
     *
     * @param nick The nickname to check.
     * @return True if given user is in this channel, false otherwise.
     */
    public boolean hasUser(String nick) {
        return (users != null) && users.containsKey(nick.toLowerCase());
    }

    /**
     * Checks whether given user is in this channel.
     *
     * @param user The user to check.
     * @return True if given user is in this channel, false otherwise.
     */
    public boolean hasUser(User user) {
        return hasUser(user.getNickLower());
    }

    /**
     * Checks whether this Channel object is shared. Shared channel
     * objects contain a list of users.
     *
     * @return True if this channel object is shared.
     */
    public boolean isGlobal() {
        return users != null;
    }

    /**
     * Attempts to join this channel.
     */
    public void join() {
        irc.getOutput().send("JOIN " + name);
    }

    /**
     * Attempts to join this channel using given password.
     *
     * @param password The password needed to join this channel.
     */
    public void join(String password) {
        irc.getOutput().send("JOIN " + name + " " + password);
    }

    /**
     * Kicks given user from this channel.
     *
     * @param user The user to kick from this channel.
     */
    public void kick(User user) {
        irc.getOutput().send("KICK " + name + " " + user.getNick());
    }

    /**
     * Kicks given user from this channel, with reason.
     *
     * @param user The user to kick from this channel.
     * @param reason The reason why this user was kicked.
     */
    public void kick(User user, String reason) {
        irc.getOutput().send("KICK " + name + " " + user.getNick() + " :" + reason);
    }

    /**
     * Attempts to leave/part this channel.
     */
    public void part() {
        irc.getOutput().send("PART " + name);
    }

    /**
     * Remove admin privileges from a user in this channel.
     *
     * @param user The user to remove admin privileges from.
     * @since 1.0.0
     */
    public void removeAdmin(User user) {
        setMode(User.MODE_ADMIN, user, false);
    }

    /**
     * Remove founder privileges from a user in this channel.
     *
     * @param user The user to remove founder privileges from.
     * @since 1.0.0
     */
    public void removeFounder(User user) {
        setMode(User.MODE_FOUNDER, user, false);
    }

    /**
     * Remove halfop privileges from a user in this channel.
     *
     * @param user The user to remove halfop privileges from.
     * @since 1.0.0
     */
    public void removeHalfop(User user) {
        setMode(User.MODE_HALF_OP, user, false);
    }

    /**
     * Remove operator privileges from a user in this channel.
     *
     * @param user The user to remove operator privileges from.
     */
    public void removeOperator(User user) {
        setMode(User.MODE_OPERATOR, user, false);
    }

    /**
     * Removes a user from the user list in this channel.
     *
     * @param user The user to remove.
     */
    protected void removeUser(User user) {
        if (users != null) {
            users.remove(user.getNickLower());
        }
    }

    /**
     * Remove voice privileges from a user in this channel.
     *
     * @param user The user to remove voice privileges from.
     */
    public void removeVoice(User user) {
        setMode(User.MODE_VOICE, user, false);
    }

    /**
     * Changes the nickname of a user in this channel.
     *
     * @param old The old nickname.
     * @param neww The new nickname.
     */
    protected void renameUser(String old, String neww) {
        if (users != null) {
            User user = users.remove(old);
            if (user != null) {
                user.setNick(neww);
                users.put(user.getNickLower(), user);
            }
        }
    }

    /**
     * Send message to channel.
     *
     * @param message The message to send.
     * @see #sendMessage(String)
     */
    public void send(String message) {
        sendMessage(message);
    }

    /**
     * Sends a CTCP ACTION command.
     *
     * @param action The action to send.
     */
    public void sendAction(String action) {
        sendCtcpAction(action);
    }

    /**
     * Sends CTCP request. This is a very primitive way to send CTCP
     * commands, other methods are preferred.
     *
     * @param command Command to send.
     */
    public void sendCtcp(String command) {
        irc.getOutput().send("PRIVMSG " + name + " :" + IrcPacket.CTCP + command + IrcPacket.CTCP);
    }

    /**
     * Sends a CTCP ACTION command.
     *
     * @param action The action to send.
     * @see #sendCtcp(String)
     */
    protected void sendCtcpAction(String action) {
        if ((action != null) && (!action.isEmpty())) {
            sendCtcp("ACTION " + action);
        }
    }

    /**
     * Send message to channel.
     *
     * @param message The message to send.
     */
    public void sendMessage(String message) {
        irc.getOutput().send("PRIVMSG " + name + " :" + message);
    }

    /**
     * Send notice to channel.
     *
     * @param message The notice to send.
     */
    public void sendNotice(String message) {
        irc.getOutput().send("NOTICE " + name + " :" + message);
    }

    /**
     * Changes a channel mode for given user.
     *
     * @param mode The mode character.
     * @param user The target user.
     * @param toggle True to enable the mode, false to disable.
     */
    public void setMode(char mode, User user, boolean toggle) {
        if (toggle) {
            setMode("+" + mode + " " + user.getNick());
        } else {
            setMode("-" + mode + " " + user.getNick());
        }
    }

    /**
     * Changes a channel mode. The channel name is automatically
     * added.
     * <p>
     * <pre>
     * setMode(&quot;+m&quot;);
     * </pre>
     *
     * @param mode The mode to change.
     */
    public void setMode(String mode) {
        irc.getOutput().send("MODE " + name + " " + mode);
    }

    /**
     * Changes the topic of this channel. This does not send a request
     * to the IRC server, to change the topic on the server, use
     * {@link #changeTopic(String)}.
     *
     * @param topic The new topic.
     */
    protected void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Updates the current shared User object with changes in a fresh
     * one and returns the updated shared object.
     *
     * @param user The fresh User object.
     * @param createNew Whether to add this user to the channel if it
     * didn't exist.
     * @return The updated shared User object.
     */
    protected User updateUser(User user, boolean createNew) {
        if (hasUser(user.getNickLower())) {
            // update user if it exists
            User shared = getUser(user.getNickLower());
            shared.updateUser(user);
            return shared;
        } else if (createNew) {
            // create a new one
            addUser(user);
            return user;
        }
        return null;
    }
}