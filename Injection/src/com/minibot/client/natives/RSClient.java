package com.minibot.client.natives;

import com.minibot.client.Artificial;
import com.minibot.client.GameCanvas;

import java.applet.Applet;

/**
 * Project: minibot
 * Date: 08-04-2015
 * Time: 05:48
 * Created by Dogerina.
 * Copyright under GPL license by Dogerina.
 */
public interface RSClient extends ClientNative {

    int getCameraX();
    int getCameraY();
    int getCameraZ();
    int getCameraYaw();
    int getCameraPitch();
    int getMapScale();
    int getMapAngle();
    int getMapOffset();
    int[][][] getTileHeights();
    byte[][][] getRenderRules();
    int getGameState();
    int getBaseX();
    int getBaseY();
    int getPlane();
    int[] getGameSettings();
    int getMouseIdleTime();
    RSNpc[] getNpcs();
    int[] getNpcIndices();
    RSPlayer[] getPlayers();
    RSPlayer getPlayer();
    RSWidget[][] getWidgets();
    int[] getWidgetPositionsX();
    int[] getWidgetPositionsY();
    int[] getWidgetHeights();
    int[] getWidgetWidths();

    @Artificial
    void processAction(int arg1, int arg2, int op, int arg0, String action, String target, int x, int y);

    default Applet asApplet() {
        return (Applet) this;
    }

    RSCanvas getCanvas();
}
