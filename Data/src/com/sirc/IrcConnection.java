/*
 * IrcConnection.java
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
package com.sirc;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Main IRC Connection class in sIRC.
 * <p>
 * sIRC acts as a layer between an IRC server and java applications. It provides
 * an event-driven architecture to handle common IRC events.
 * </p>
 *
 * @author Sorcix
 */
public class IrcConnection {

    /** The sIRC about string, used in CTCP */
    public static String ABOUT = "Sorcix Lib-IRC (sIRC) v" + IrcConnection.VERSION;
    /** Debug: Show raw messages */
    protected static final boolean DEBUG_MSG = false;
    /** Whether this library should call garbage collection. */
    protected static final boolean GARBAGE_COLLECTION = false;
    /** sIRC Library version. */
    public static final String VERSION = "1.1.6-SNAPSHOT";
    /** Advanced listener. */
    private AdvancedListener advancedListener;
    /** Connection InputStream thread. */
    private IrcInput in;
    /** Outgoing message delay. (Flood control) */
    private int messageDelay = 100;
    /** Message listeners. */
    private final List<MessageListener> messageListeners;
    /** Mode listeners. */
    private final List<ModeListener> modeListeners;
    /** Connection OutputStream thread. */
    protected IrcOutput out;
    /** Server listeners. */
    private final List<ServerListener> serverListeners;
    /** Services. */
    private final List<SIRCService> services;
    /** Connection socket. */
    private Socket socket;
    /** Custom version string. */
    private String version;
    /** The server this IrcConnection is connected to. */
    private IrcServer server;
    /** IRC Client state. */
    private final ClientState state;
    /** Whether we're connected or not. */
    private boolean connected;
    /** The Character set to use for encoding the connection */
    private Charset charset = Charset.defaultCharset();
    /** End line character. */
    protected static final String ENDLINE = "\n";
    /** Whether to allow server redirection (bounce) or not. */
    private boolean bounceAllowed;

    /**
     * Creates a new IrcConnection object.
     */
    public IrcConnection() {
        this(null, IrcServer.DEFAULT_PORT, null);
    }

    /**
     * Creates a new IrcConnection object.
     *
     * @param server Server address.
     */
    public IrcConnection(String server) {
        this(server, IrcServer.DEFAULT_PORT, null);
    }

    /**
     * Creates a new IrcConnection object.
     *
     * @param server Server address.
     * @param port Port number to connect to.
     */
    public IrcConnection(String server, int port) {
        this(server, port, null);
    }

    /**
     * Creates a new IrcConnection object.
     *
     * @param server Server address.
     * @param port Port number to connect to
     * @param password The password to use.
     */
    public IrcConnection(String server, int port,
                         String password) {
        this.server = new IrcServer(server, port, password, false);
        serverListeners = new Vector<>(4);
        messageListeners = new Vector<>(4);
        modeListeners = new Vector<>(2);
        services = new Vector<>(0);
        state = new ClientState();
    }

    /**
     * Creates a new IrcConnection object.
     *
     * @param server Server address.
     * @param password The password to use.
     */
    public IrcConnection(String server, String password) {
        this(server, IrcServer.DEFAULT_PORT, password);
    }

    /**
     * Adds a message listener to this IrcConnection.
     *
     * @param listener The message listener to add.
     */
    public void addMessageListener(MessageListener listener) {
        if (listener != null && !messageListeners.contains(listener)) {
            messageListeners.add(listener);
        }
    }

    /**
     * Adds a mode listener to this IrcConnection. Note that adding mode
     * listeners will cause sIRC to check every incoming mode change for
     * supported modes. Modes can also be read by using
     * {@link ServerListener#onMode(IrcConnection, Channel, User, String)} which
     * can be a lot faster for reading modes.
     *
     * @param listener The mode listener to add.
     */
    public void addModeListener(ModeListener listener) {
        if (listener != null && !modeListeners.contains(listener)) {
            modeListeners.add(listener);
        }
    }

    /**
     * Adds a server listener to this IrcConnection.
     *
     * @param listener The server listener to add.
     */
    public void addServerListener(ServerListener listener) {
        if (listener != null && !serverListeners.contains(listener)) {
            serverListeners.add(listener);
        }
    }

    /**
     * Add and load a service. {@code IrcConnection} will call the
     * {@link SIRCService#load(IrcConnection)} method of this
     * {@code SIRCService} after adding it to the service list.
     *
     * @param service The service to add.
     */
    public void addService(SIRCService service) {
        if (service != null && !services.contains(service)) {
            services.add(service);
            service.load(this);
        }
    }

    /**
     * Sends the MOTD command to the server, which makes the server send us the
     * Message of the Day. (Through ServerListener)
     *
     * @see ServerListener#onMotd(IrcConnection, String)
     * @since 1.0.2
     */
    public void askMotd() {
        out.send(IrcPacketFactory.createMOTD());
    }

    /**
     * Send a raw command to the IRC server.  Unrecognized responses
     * are passed to the AdvancedListener's onUnknown() callback.
     *
     * @param line The raw line to send.
     */
    @SuppressWarnings("deprecation")
    public void sendRaw(String line) {
        out.send(line);
    }

    /**
     * Asks the userlist for a certain channel.
     *
     * @param channel The channel to request the userlist for.
     */
    protected void askNames(Channel channel) {
        out.send(IrcPacketFactory.createNAMES(channel.getName()));
    }

    /**
     * Closes all streams.
     */
    private void close() {
        try {
            in.interrupt();
            out.interrupt();
            // close input stream
            in.close();
            // close output stream
            out.close();
            // close socket
            if (socket.isConnected()) {
                socket.close();
            }
        } catch (Exception ex) {
            // ignore
        }
    }

    /**
     * Connect to the IRC server. You must set the server details and nickname
     * before calling this method!
     *
     * @throws UnknownHostException When the domain name is invalid.
     * @throws IOException When anything went wrong while connecting.
     * @throws NickNameException If the given nickname is already in use or invalid.
     * @throws PasswordException If the server password is incorrect.
     * @see #setServer(String, int)
     * @see #setNick(String)
     */
    public void connect() throws IOException, NickNameException, PasswordException {
        connect((SSLContext) null);
    }

    /**
     * Connect to the IRC server. You must set the server details and nickname
     * before calling this method!
     *
     * @param sslctx The SSLContext to use.
     * @throws UnknownHostException When the domain name is invalid.
     * @throws IOException When anything went wrong while connecting.
     * @throws NickNameException If the given nickname is already in use or invalid.
     * @throws PasswordException If the server password is incorrect.
     * @see #setServer(String, int)
     * @see #setNick(String)
     */
    public void connect(SSLContext sslctx) throws IOException, NickNameException, PasswordException {
        if (server.isSecure()) {
            try {
                if (sslctx == null) {
                    sslctx = SSLContext.getDefault();
                }
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
            connect(sslctx.getSocketFactory());
        } else {
            connect(SocketFactory.getDefault());
        }
    }

    /**
     * Connect to the IRC server. You must set the server details and nickname
     * before calling this method!
     *
     * @param sfact The SocketFactory to create a socket with.
     * @throws UnknownHostException When the domain name is invalid.
     * @throws IOException When anything went wrong while connecting.
     * @throws NickNameException If the given nickname is already in use or invalid.
     * @throws PasswordException If the server password is incorrect.
     * @see #setServer(String, int)
     * @see #setNick(String)
     */
    public void connect(SocketFactory sfact) throws IOException, NickNameException, PasswordException {
        // check if a server is given
        if (server.getAddress() == null) {
            throw new IOException("Server address is not set!");
        }
        // connect socket
        if (socket == null || !socket.isConnected()) {
            Socket socket = sfact.createSocket(server.getAddress(), server.getPort());
            this.socket = null;
            connect(socket);
        } else {
            connect(socket);
        }
    }

    /**
     * Connect to the IRC server. You must set the server details and nickname
     * before calling this method!
     *
     * @param sock The socket to connect to.
     * @throws UnknownHostException When the domain name is invalid.
     * @throws IOException When anything went wrong while connecting.
     * @throws NickNameException If the given nickname is already in use or invalid.
     * @throws PasswordException If the server password is incorrect.
     * @see #setServer(String, int)
     * @see #setNick(String)
     * @since 1.0.0
     */
    public void connect(Socket sock) throws IOException, NickNameException, PasswordException {
        boolean reconnecting = true;
        // don't even try if nickname is empty
        if (state.getClient() == null || state.getClient().getNick().trim().isEmpty()) {
            throw new NickNameException("Nickname is empty or null!");
        }
        // allows for handling SASL, etc. before doing IRC handshake
        // set to input socket
        if (sock != null && socket != sock) {
            socket = sock;
            reconnecting = false;
        }
        // open streams
        out = new IrcOutput(this, new OutputStreamWriter(socket.getOutputStream(), charset));
        in = new IrcInput(this, new InputStreamReader(socket.getInputStream(), charset));
        if (!reconnecting) {
            // send password if given
            if (server.getPassword() != null) {
                out.sendNowEx(IrcPacketFactory.createPASS(server
                        .getPassword()));
            }
            out.sendNowEx(IrcPacketFactory.createUSER(state.getClient()
                    .getUserName(), state.getClient().getNick()));
        }
        out.sendNowEx(IrcPacketFactory.createNICK(state.getClient()
                .getNick()));
        // wait for reply
        String line;
        loop:
        while ((line = in.getReader().readLine()) != null) {
            IrcDebug.log(line);
            IrcPacket decoder = new IrcPacket(line, this);
            if (decoder.isNumeric()) {
                int command = decoder.getNumericCommand();
                switch (command) {
                    case 1:
                    case 2:
                    case 3: {
                        String nick = decoder.getArgumentsArray()[0];
                        if (!state.getClient().getNick().equals(nick)) {
                            setNick(nick);
                        }
                    }
                    break;
                    case 4: // login OK
                        break loop;
                    case 432:
                    case 433: {
                        // bad/in-use nickname nickname
                        throw new NickNameException("Nickname " + state.getClient().getNick() + " already in use or not allowed!");
                    } // break; unnecessary due to throw
                    case 464: {
                        // wrong password
                        disconnect();
                        throw new PasswordException("Invalid password");
                    } // break; unnecessary due to throw
                }
            }
            if (line.startsWith("PING ")) {
                out.pong(line.substring(5));
            }
        }
        // start listening
        in.start();
        out.start();
        // we are connected
        connected = true;
        // send events
        for (Iterator<ServerListener> it = getServerListeners(); it
                .hasNext(); ) {
            it.next().onConnect(this);
        }
    }

    /**
     * Creates a {@link Channel} object with given channel name. Note that this
     * method does not actually create a channel on the IRC server, it just
     * creates a {@link Channel} object linked to this {@code IrcConnection}. If
     * the local user is in the channel this method will return a global channel
     * object containing a user list.
     *
     * @param name The channel name, starting with #.
     * @return A {@code Channel} object representing given channel.
     * @see Channel#isGlobal()
     */
    public Channel createChannel(String name) {
        if (Channel.CHANNEL_PREFIX.indexOf(name.charAt(0)) < 0) {
            name = "#" + name;
        }
        return state.hasChannel(name) ? state.getChannel(name) : new Channel(name, this, false);
    }

    /**
     * Creates a {@link User} object with given nickname. This will create a
     * {@link User} object without any information about modes.
     *
     * @param nick The nickname.
     * @return A {@code User} object representing given user.
     * @see User#User(String, IrcConnection)
     */
    public User createUser(String nick) {
        return new User(nick, this);
    }

    /**
     * Creates a {@link User} object with given nickname. This will attempt to
     * retrieve a global {@link User} object for given {@link Channel}
     * containing information about user modes. If it isn't possible to return a
     * global {@link User} object, this method will return a new {@link User}.
     *
     * @param nick The nickname.
     * @param channel The channel this user is in.
     * @return A {@code User} object representing given user.
     */
    public User createUser(String nick, String channel) {
        User empty = createUser(nick);
        return state.hasChannel(channel)
                && state.getChannel(channel).hasUser(nick) ? state.getChannel(channel).getUser(nick) : empty;
    }

    /**
     * Disconnects from the server. In the case a connection to the server is
     * alive, this method will send the QUIT command and wait for the server to
     * disconnect us.
     */
    public void disconnect() {
        disconnect(null);
    }

    /**
     * Disconnects from the server. In the case a connection to the server is
     * alive, this method will send the QUIT command and wait for the server to
     * disconnect us.
     *
     * @param message The QUIT message to use.
     */
    public void disconnect(String message) {
        if (connected) {
            out.sendNow(IrcPacketFactory.createQUIT(message));
        } else {
            close();
            state.removeAll();
            garbageCollection();
        }
    }

    /**
     * Runs garbage collection.
     */
    protected void garbageCollection() {
        if (IrcConnection.GARBAGE_COLLECTION) {
            System.gc();
        }
    }

    /**
     * Gives the advanced listener used by this {@code IrcConnection}.
     *
     * @return The advanced listener, or null.
     */
    protected AdvancedListener getAdvancedListener() {
        return advancedListener;
    }

    /**
     * Gives all channels we're currently in.
     *
     * @return All channels we're currently in.
     */
    public Iterator<Channel> getChannels() {
        return state.getChannels();
    }

    /**
     * Returns the character set that is used for the connection's encoding. The
     * default is the system default returned by
     * {@link Charset#defaultCharset()}.
     *
     * @return The character set for the connection's encoding.
     */
    public Charset getCharset() {
        return charset;
    }

    /**
     * Returns the client used by this {@code IrcConnection}.
     *
     * @return User representing this client.
     */
    public User getClient() {
        return state.getClient();
    }

    /**
     * Returns the outgoing message delay in milliseconds.
     *
     * @return Outgoing message delay in milliseconds.
     */
    public int getMessageDelay() {
        return messageDelay;
    }

    /**
     * Returns all {@link MessageListener}s registered with this IrcConnection.
     *
     * @return All {@code MessageListeners}.
     */
    protected Iterator<MessageListener> getMessageListeners() {
        return messageListeners.iterator();
    }

    /**
     * Returns all {@link ModeListener}s registered with this IrcConnection.
     *
     * @return All {@code ModeListeners}.
     */
    protected Iterator<ModeListener> getModeListeners() {
        return modeListeners.iterator();
    }

    /**
     * Returns the output thread used for sending messages through this
     * {@code IrcConnection}.
     *
     * @return The {@code IrcOutput} used to send messages.
     */
    protected IrcOutput getOutput() {
        return out;
    }

    /**
     * Returns the server this {@code IrcConnection} connects to.
     *
     * @return The IRC server.
     */
    public IrcServer getServer() {
        return server;
    }

    /**
     * Gives the server address this {@code IrcConnection} is using to connect.
     *
     * @return Server address.
     * @since 1.0.0
     */
    public String getServerAddress() {
        return server.getAddress();
    }

    /**
     * Returns all {@link ServerListener}s registered with this IrcConnection.
     *
     * @return All {@code ServerListeners}.
     */
    protected Iterator<ServerListener> getServerListeners() {
        return serverListeners.iterator();
    }

    /**
     * Gives the port number this {@code IrcConnection} is using to connect.
     *
     * @return Port number
     * @since 1.0.0
     */
    public int getServerPort() {
        return server.getPort();
    }

    /**
     * Returns all services running on this IrcConnection.
     *
     * @return All running services.
     */
    private Iterator<SIRCService> getServices() {
        return services.iterator();
    }

    /**
     * Retrieves the {@link ClientState} for this {@code IrcConnection}.
     *
     * @return The {@link ClientState}.
     * @since 1.1.0
     */
    public ClientState getState() {
        return state;
    }

    /**
     * Gives the version string used.
     *
     * @return The version string.
     * @since 0.9.4
     */
    protected String getVersion() {
        if (version != null) {
            return version;
        }
        return IrcConnection.ABOUT;
    }

    /**
     * Returns whether this connection is allowed to be redirected.
     *
     * @return {@code true} if redirection is allowed, {@code false} otherwise.
     */
    public boolean isBounceAllowed() {
        return bounceAllowed;
    }

    /**
     * Checks whether the client is still connected.
     *
     * @return True if the client is connected, false otherwise.
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Checks if given {@link User} object represents us.
     *
     * @param user {@code User} to check
     * @return True if given {@code User} represents us, false otherwise.
     */
    public boolean isUs(User user) {
        return user.equals(state.getClient());
    }

    /**
     * Checks whether this connection is using SSL.
     *
     * @return True if this connection is using SSL, false otherwise.
     */
    public boolean isUsingSSL() {
        return server.isSecure();
    }

    /**
     * Calls {@link #removeService(SIRCService)} for all registered services.
     *
     * @see #removeService(SIRCService)
     */
    public void removeAllServices() {
        if (!services.isEmpty()) {
            for (Iterator<SIRCService> it = getServices(); it
                    .hasNext(); ) {
                removeService(it.next());
            }
        }
    }

    /**
     * Removes a message listener from this IrcConnection.
     *
     * @param listener The message listener to remove.
     */
    public void removeMessageListener(MessageListener listener) {
        if (listener != null && messageListeners.contains(listener)) {
            messageListeners.remove(listener);
        }
    }

    /**
     * Removes a mode listener from this IrcConnection.
     *
     * @param listener The mode listener to remove.
     */
    public void removeModeListener(ModeListener listener) {
        if (listener != null && modeListeners.contains(listener)) {
            modeListeners.remove(listener);
        }
    }

    /**
     * Removes a server listener from this IrcConnection.
     *
     * @param listener The server listener to remove.
     */
    public void removeServerListener(ServerListener listener) {
        if (listener != null && serverListeners.contains(listener)) {
            serverListeners.remove(listener);
        }
    }

    /**
     * Remove a service. {@code IrcConnection} will call the
     * {@link SIRCService#unload(IrcConnection)} method of this
     * {@code SIRCService} after removing it the service list.
     *
     * @param service The service to remove.
     */
    public void removeService(SIRCService service) {
        if (service != null && !services.contains(service)) {
            service.unload(this);
            services.remove(service);
        }
    }

    /**
     * Sets the advanced listener used in this {@code IrcConnection}.
     *
     * @param listener The advanced listener to use, or {@code null}.
     */
    public void setAdvancedListener(AdvancedListener listener) {
        advancedListener = listener;
    }

    /**
     * Marks you as away on the server. If any user sends a message to you while
     * marked as away, the the server will send them a message back.
     *
     * @param reason The reason for being away.
     * @see #setNotAway()
     * @since 1.0.2
     */
    public void setAway(String reason) {
        out.send(IrcPacketFactory.createAWAY(reason));
    }

    /**
     * Sets whether this connection is allowed to be redirected. If {@code true}
     * , sIRC will change server when it receives a bounce reply.
     *
     * @param bounceAllowed {@code true} if redirection is allowed, {@code false}
     * otherwise.
     */
    public void setBounceAllowed(boolean bounceAllowed) {
        this.bounceAllowed = bounceAllowed;
    }

    /**
     * Sets the character set to use for the connections's encoding. If a
     * connection is already open, it will need to be closed then reopened
     * before any encoding changes will take effect.
     *
     * @param charset The character set to use for the connection's encoding.
     */
    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    /**
     * Changes the connection state of the client.
     *
     * @param connected Whether we are still connected.
     */
    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    /**
     * Sets the outgoing message delay in milliseconds. Note that sending a lot
     * of messages in a short period of time might cause the server to
     * disconnect you. The default is 1 message each 100ms.
     *
     * @param messageDelay The outgoing message delay in milliseconds.
     */
    public void setMessageDelay(int messageDelay) {
        if (messageDelay < 0) {
            throw new IllegalArgumentException(
                    "Message Delay can't be negative!");
        }
        this.messageDelay = messageDelay;
    }

    /**
     * Changes the nickname of this client. While connected, this method will
     * attempt to change the nickname on the server.
     *
     * @param nick New nickname.
     */
    public void setNick(String nick) {
        if (!connected) {
            if (nick != null) {
                if (state.getClient() == null) {
                    state.setClient(new User(nick, "sIRC", null, null, this));
                    return;
                }
                state.getClient().setNick(nick);
            }
        } else {
            out.sendNow(IrcPacketFactory.createNICK(nick));
        }
    }

    public void setUsername(String username) {
        setUsername(username, null);
    }

    public void setUsername(String username, String realname) {
        if (!connected) {
            if (username != null) {
                if (state.getClient() == null) {
                    state.setClient(new User(null, username, null, realname, this));
                }
            }
        }
    }

    /**
     * Removes the away mark.
     *
     * @see #setAway(String)
     * @since 1.0.2
     */
    public void setNotAway() {
        setAway(null);
    }

    /**
     * Sets the server details to use while connecting.
     *
     * @param server The server to connect to.
     */
    public void setServer(IrcServer server) {
        if (!connected) {
            this.server = server;
        }
    }

    /**
     * Sets the server details to use while connecting.
     *
     * @param address The address of the server.
     * @param port The port number to use.
     * @since 1.0.0
     */
    public void setServer(String address, int port) {
        setServerAddress(address);
        setServerPort(port);
    }

    /**
     * Sets the server address to use while connecting.
     *
     * @param address The address of the server.
     * @since 1.0.0
     */
    public void setServerAddress(String address) {
        if (!connected && address != null) {
            server.setAddress(address);
        }
    }

    /**
     * Sets the server address to use while connecting.
     *
     * @param port The port number to use.
     */
    public void setServerPort(int port) {
        if (!connected && port > 0) {
            server.setPort(port);
        }
    }

    /**
     * Sets whether this connection should use SSL to connect. Note that the
     * connection will fail if the server has no valid certificate. This
     * property can only be changed while sIRC is not connected to an IRC
     * server.
     *
     * @param usingSSL True to use SSL, false otherwise.
     * @see #setServerPort(int)
     */
    public void setUsingSSL(boolean usingSSL) {
        if (!connected) {
            server.setSecure(usingSSL);
        }
    }

    /**
     * Set the string returned on CTCP VERSION and FINGER commands.
     *
     * @param version The string to return on CTCP VERSION and FINGER commands, or
     * {@code null} to use the default sIRC version string.
     * @since 0.9.4
     */
    public void setVersion(String version) {
        this.version = version;
    }
}