package com.minibot.api.method;

import com.minibot.Minibot;
import com.minibot.api.action.tree.Action;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
public class RuneScape {
    public static boolean MODEL_RENDERING_ENABLED = true;
    public static boolean LANDSCAPE_RENDERING_ENABLED = true;
    private static final Queue<Node> queue = new ArrayBlockingQueue<>(2000);
    private static Node last;
    private static long lastProcess;

    public static int queueSize() {
        return queue.size();
    }

    public static void processActions() {
        Iterator<Node> iterator = queue.iterator();
        while (iterator.hasNext()) {
            Node cur = iterator.next();
            boolean fire = true;
            if (last != null && (System.currentTimeMillis() - lastProcess) < 15) {
                if (last.opcode == cur.opcode && last.arg1 == cur.arg1 && last.arg2 == cur.arg2) {
                    fire = false;
                }
            }
            if (fire) {
                cur.fire();
                lastProcess = System.currentTimeMillis();
                last = cur;
            }
            iterator.remove();
        }
    }

    public static void processAction(Action action, String actionText, String targetText, int x, int y) {
        processAction(action.arg0, action.arg1, action.arg2, action.opcode, actionText, targetText, x, y);
    }

    public static void processAction(Action action, String actionText, String targetText) {
        processAction(action.arg0, action.arg1, action.arg2, action.opcode, actionText, targetText, 50, 50);
    }

    public static void processAction(int arg0, int arg1, int arg2, int opcode, String actionText, String targetText, int x, int y) {
        if (!Game.isLoggedIn())
            return;
        Node node = new Node();
        node.arg0 = arg0;
        node.arg1 = arg1;
        node.arg2 = arg2;
        node.opcode = opcode;
        node.actionText = actionText;
        node.targetText = targetText;
        node.x = x;
        node.y = y;
        queue.offer(node);
    }

    private static class Node {

        private int arg0, arg1, arg2, opcode;
        private int x = 0;
        private int y = 0;
        private String actionText;
        private String targetText;

        private void fire() {
            Minibot.instance().client().processAction(arg1, arg2, opcode, arg0, actionText, targetText, x, y);
        }
    }
}
