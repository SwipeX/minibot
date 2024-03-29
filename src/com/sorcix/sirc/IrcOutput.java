/*
 * IrcOutput.java
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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Output thread, and manages the outgoing message queue.
 *
 * @author Sorcix
 */
class IrcOutput extends Thread {

    /** The IrcConnection. */
    private final IrcConnection irc;
    /** Stream used to write to the IRC server. */
    private final BufferedWriter out;
    /** The outgoing message queue. */
    private final IrcQueue queue;
    /** Maximum line length. */
    protected static final int MAX_LINE_LENGTH = 512;

    /**
     * Creates a new output thread.
     *
     * @param irc The IrcConnection using this output thread.
     * @param out The stream to use for communication.
     */
    protected IrcOutput(IrcConnection irc, Writer out) {
        setName("sIRC-OUT:" + irc.getServerAddress() + "-" + irc.getClient().getUserName());
        setPriority(Thread.MIN_PRIORITY);
        setDaemon(true);
        this.irc = irc;
        queue = new IrcQueue();
        this.out = new BufferedWriter(out);
    }

    /**
     * Closes the output stream.
     *
     * @see IrcConnection#disconnect()
     */
    protected void close() throws IOException {
        out.flush();
        out.close();
    }

    /**
     * Sends messages from the output queue.
     */
    @Override
    public void run() {
        try {
            boolean running = true;
            String line;
            while (running) {
                Thread.sleep(irc.getMessageDelay());
                line = queue.take();
                if (line != null) {
                    sendNow(line);
                } else {
                    running = false;
                }
            }
        } catch (InterruptedException e) {
            // end this thread
        }/* catch (final IllegalStateException e) {
            if (this.irc.isConnected()) {
				this.irc.setConnected(false);
				this.irc.disconnect();
			}
			e.printStackTrace();
		}*/
    }

    /**
     * Sends {@link IrcPacket} to the IRC server, using the message queue.
     *
     * @param packet The data to send.
     */
    protected synchronized void send(IrcPacket packet) {
        if (irc.getMessageDelay() == 0) {
            sendNow(packet.getRaw());
            return;
        }
        queue.add(packet.getRaw());
    }

    /**
     * Sends raw line to the IRC server, using the message queue.
     *
     * @param line The raw line to send.
     * @deprecated Use {@link #send(IrcPacket)} instead.
     */
    @Deprecated
    protected synchronized void send(String line) {
        //TODO: Remove in a future release.
        if (irc.getMessageDelay() == 0) {
            sendNow(line);
            return;
        }
        queue.add(line);
    }

    /**
     * Sends {@link IrcPacket} to the IRC server, without using the message
     * queue. This method will ignore any exceptions thrown while
     * sending the message.
     *
     * @param packet The IrcPacket to send.
     */
    protected synchronized void sendNow(IrcPacket packet) {
        try {
            sendNowEx(packet.getRaw());
        } catch (Exception ex) {
            // ignore
        }
    }

    /**
     * Sends raw line to the IRC server, without using the message
     * queue. This method will ignore any exceptions thrown while
     * sending the message.
     *
     * @param line The raw line to send.
     * @deprecated Use {@link #sendNow(IrcPacket)} instead.
     */
    @Deprecated
    protected synchronized void sendNow(String line) {
        //TODO: Remove in a future release.
        try {
            sendNowEx(line);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Sends {@link IrcPacket} to the IRC server, without using the message
     * queue.
     *
     * @param packet The IrcPacket to send.
     * @throws IOException If anything goes wrong while sending this
     * message.
     */
    protected synchronized void sendNowEx(IrcPacket packet) throws IOException {
        sendNowEx(packet.getRaw());
    }

    /**
     * Sends raw line to the IRC server, without using the message
     * queue.
     *
     * @param line The raw line to send.
     * @throws IOException If anything goes wrong while sending this
     * message.
     */
    private synchronized void sendNowEx(String line) throws IOException {
        if (line.length() > (IrcOutput.MAX_LINE_LENGTH - 2)) {
            line = line.substring(0, IrcOutput.MAX_LINE_LENGTH - 2);
        }
        IrcDebug.log(">>> " + line);
        out.write(line + IrcConnection.ENDLINE);
        out.flush();
    }

    /**
     * Shortcut to quickly send a PONG packet back.
     *
     * @param code The code to send with the PONG packet.
     */
    protected void pong(String code) {
        try {
            sendNowEx("PONG " + code);
        } catch (Exception ex) {
            // ignore
        }
    }
}