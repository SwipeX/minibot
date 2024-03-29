package com.minibot.client;

import com.minibot.api.util.Random;
import com.minibot.api.util.Renderable;

import javax.swing.KeyStroke;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameCanvas extends Canvas implements Renderable {

    public static final int INPUT_MOUSE = 0x2;
    public static final int INPUT_KEYBOARD = 0x4;
    private static final List<Renderable> renderables = new CopyOnWriteArrayList<>();
    private final BufferedImage raw;
    private final BufferedImage backBuffer;

    private final EventQueue queue;

    private int input = INPUT_MOUSE | INPUT_KEYBOARD;
    private int mouseX;
    private int mouseY;

    public GameCanvas() {
        raw = new BufferedImage(Toolkit.getDefaultToolkit().getScreenSize().width,
                Toolkit.getDefaultToolkit().getScreenSize().height, BufferedImage.TYPE_INT_ARGB);
        backBuffer = new BufferedImage(Toolkit.getDefaultToolkit().getScreenSize().width,
                Toolkit.getDefaultToolkit().getScreenSize().height, BufferedImage.TYPE_INT_ARGB);
        requestFocusInWindow();
        queue = Toolkit.getDefaultToolkit().getSystemEventQueue();
        queue.push(new EventQueue() {
            @Override
            public void dispatchEvent(AWTEvent evt) {
                if (!evt.getSource().equals(this)) {
                    super.dispatchEvent(evt);
                    return;
                }
                if (!evt.getSource().equals("bot")) {
                    if (evt instanceof MouseEvent && (input & INPUT_MOUSE) == 0
                            || evt instanceof KeyEvent && (input & INPUT_KEYBOARD) == 0) {
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

    public BufferedImage capture() {
        return backBuffer;
    }

    private void push(AWTEvent evt) {
        if (!evt.getSource().equals(this)) {
            dispatchEvent(evt);
            return;
        }
        if (!evt.getSource().equals("bot")) {
            if (evt instanceof MouseEvent && (input & INPUT_MOUSE) == 0
                    || evt instanceof KeyEvent && (input & INPUT_KEYBOARD) == 0) {
                return;
            }
        }
        dispatchEvent(evt);
    }

    public void pressMouse(boolean left) {
        push(generateMouseEvent(MouseEvent.MOUSE_PRESSED, mouseX, mouseY,
                left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3));
    }

    public void releaseMouse(boolean left) {
        int offset = Random.nextInt(20, 30);
        push(generateMouseEvent(MouseEvent.MOUSE_RELEASED, mouseX, mouseY,
                left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3, offset));
        push(generateMouseEvent(MouseEvent.MOUSE_CLICKED, mouseX, mouseY,
                left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3, offset));
    }

    public void clickMouse(boolean left) {
        pressMouse(left);
        releaseMouse(left);
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


    public void scrollMouse(boolean up) {
        queue.postEvent(new MouseWheelEvent(this, MouseEvent.MOUSE_WHEEL, System.currentTimeMillis(), 0,
                mouseX, mouseY, 0, false, MouseWheelEvent.WHEEL_UNIT_SCROLL, 1, up ? -1 : 1));
    }

    private KeyEvent generateKeyEvent(char key, int type, int timeOffset) {
        KeyStroke stroke = KeyStroke.getKeyStroke(key);
        int keycode = stroke.getKeyCode();
        if (key >= 'a' && key <= 'z') {
            keycode -= 32;
        }
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
        renderables.parallelStream().forEach(r -> {
            if (r != null && g != null) {
                r.render(g);
            }
        });
    }

    public static void addRenderable(Renderable renderable) {
        if (renderable != null) {
            renderables.add(renderable);
        }
    }

    public static void removeRenderable(Renderable renderable) {
        if (renderable != null) {
            renderables.remove(renderable);
        }
    }

    public int getInput() {
        return input;
    }

    public void setInput(int input) {
        this.input = input;
    }

    public int getMouseX() {
        return mouseX;
    }

    public void setMouseX(int mouseX) {
        this.mouseX = mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    public void setMouseY(int mouseY) {
        this.mouseY = mouseY;
    }
}