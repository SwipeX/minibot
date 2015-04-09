package com.minibot.client;

import com.minibot.api.method.Widgets;
import com.minibot.api.util.Random;
import com.minibot.api.util.Renderable;
import com.minibot.client.natives.RSWidget;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

/**
 * @author Tyler Sedlar
 */
public class GameCanvas extends Canvas implements Renderable {

    public static final int INPUT_MOUSE = 0x2;
    public static final int INPUT_KEYBOARD = 0x4;

    public final BufferedImage raw;
    public final BufferedImage backBuffer;

    public final EventQueue queue;

    public int input = INPUT_MOUSE | INPUT_KEYBOARD;
    public int mouseX = 0, mouseY = 0;

    public GameCanvas() {
        this.raw = new BufferedImage(765, 503, BufferedImage.TYPE_INT_ARGB);
        this.backBuffer = new BufferedImage(765, 503, BufferedImage.TYPE_INT_ARGB);
        requestFocusInWindow();
        queue = Toolkit.getDefaultToolkit().getSystemEventQueue();
        queue.push(new EventQueue() {
            public void dispatchEvent(AWTEvent evt) {
                if (!evt.getSource().equals(this)) {
                    super.dispatchEvent(evt);
                    return;
                }
                if (!evt.getSource().equals("bot")) {
                    if (evt instanceof MouseEvent && (input & INPUT_MOUSE) == 0) {
                        return;
                    } else if (evt instanceof KeyEvent && (input & INPUT_KEYBOARD) == 0) {
                        return;
                    }
                }
                super.dispatchEvent(evt);
            }
        });
    }

    @Override
    public Graphics getGraphics() {
        Graphics g = super.getGraphics();
        Graphics2D paint = backBuffer.createGraphics();
        paint.drawImage(raw, 0, 0, null);
        try {
            render(paint);
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        paint.dispose();
        g.drawImage(backBuffer, 0, 0, null);
        g.dispose();
        return raw.createGraphics();
    }

    private AWTEvent mask(AWTEvent e) {
        e.setSource("bot");
        return e;
    }

    private AWTEvent generateMouseEvent(int type, int x, int y, int button, int timeOffset) {
        return mask(new MouseEvent(this, type, System.currentTimeMillis() + timeOffset, 0, x, y,
                button != MouseEvent.MOUSE_MOVED ? 1 : 0, false, button));
    }

    private AWTEvent generateMouseEvent(int type, int x, int y, int button) {
        return generateMouseEvent(type, x, y, button, 0);
    }

    public void moveMouse(int x, int y) {
        queue.postEvent(generateMouseEvent(MouseEvent.MOUSE_MOVED, (mouseX = x), (mouseY = y), MouseEvent.NOBUTTON));
    }

    public void pressMouse(boolean left) {
        queue.postEvent(generateMouseEvent(MouseEvent.MOUSE_PRESSED, mouseX, mouseY,
                left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3));
    }

    public void releaseMouse(boolean left) {
        int offset = Random.nextInt(20, 30);
        queue.postEvent(generateMouseEvent(MouseEvent.MOUSE_RELEASED, mouseX, mouseY,
                left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3, offset));
        queue.postEvent(generateMouseEvent(MouseEvent.MOUSE_CLICKED, mouseX, mouseY,
                left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3, offset));
    }

    public void clickMouse(boolean left) {
        pressMouse(left);
        releaseMouse(left);
    }

    public void scrollMouse(boolean up) {
        queue.postEvent(new MouseWheelEvent(this, MouseEvent.MOUSE_WHEEL, System.currentTimeMillis(), 0,
                mouseX, mouseY, 0, false, MouseWheelEvent.WHEEL_UNIT_SCROLL, 1, up ? -1 : 1));
    }

    private KeyEvent generateKeyEvent(char key, int type, int timeOffset) {
        KeyStroke stroke = KeyStroke.getKeyStroke(key);
        int keycode = stroke.getKeyCode();
        if (key >= 'a' && key <= 'z')
            keycode -= 32;
        return new KeyEvent(this, type, System.currentTimeMillis() + timeOffset, stroke.getModifiers(), keycode, key,
                KeyEvent.KEY_LOCATION_STANDARD);
    }

    private KeyEvent generateKeyEvent(char key, int type) {
        return generateKeyEvent(key, type, 0);
    }

    public void pressKey(char key) {
        queue.postEvent(generateKeyEvent(key, KeyEvent.KEY_PRESSED));
    }

    public void releaseKey(char key) {
        int offset = Random.nextInt(20, 30);
        queue.postEvent(generateKeyEvent(key, KeyEvent.KEY_RELEASED, offset));
        queue.postEvent(generateKeyEvent(key, KeyEvent.KEY_TYPED, offset));
    }

    public void typeKey(char key) {
        pressKey(key);
        releaseKey(key);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public void render(Graphics2D g) {
        RSWidget[][] raw = Widgets.raw();
        if (raw == null)
            return;
        for (int i = 0; i < raw.length; i++) {
            RSWidget[] comp = raw[i];
            g.setColor(Color.WHITE);
            if (comp == null)
                continue;
            for (RSWidget widget : comp) {
                if (widget == null)
                    continue;
                g.drawRect(
                        widget.getContainerX() + widget.getX(),
                        widget.getContainerY() + widget.getY(),
                        widget.getWidth(),
                        widget.getHeight()
                );
                g.setColor(Color.GREEN);
                g.drawString(i + "," + widget.getIndex(), widget.getContainerX(), widget.getContainerY());
            }
        }
    }
}

