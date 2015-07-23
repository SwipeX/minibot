package com.minibot.client.natives;

import com.minibot.client.Artificial;

import java.applet.Applet;

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

    int[] getPlayerIndices();

    RSPlayer[] getPlayers();

    RSPlayer getPlayer();

    RSWidget[][] getWidgets();

    int[] getWidgetPositionsX();

    int[] getWidgetPositionsY();

    int[] getWidgetHeights();

    int[] getWidgetWidths();

    int[] getExperiences();

    int[] getLevels();

    int[] getRealLevels();

    @Artificial
    void setHoveredRegionTileX(int newRegionX);

    @Artificial
    void setHoveredRegionTileY(int newRegionY);

    @Artificial
    void processAction(int arg1, int arg2, int op, int arg0, String action, String target, int x, int y);

    default Applet asApplet() {
        return (Applet) this;
    }

    RSCanvas getCanvas();

    RSObjectDefinition loadObjectDefinition(int id);

    RSNpcDefinition loadNpcDefinition(int id);

    RSItemDefinition loadItemDefinition(int id);

    RSNodeDeque[][][] getGroundItems();

    RSInteractableObject[] getObjects();

    RSRegion getRegion();

    void resetMouseIdleTime();

    void setPassword(String to);

    void setUsername(String to);

    String getPassword();

    String getUsername();

    int getHintX();

    int getHintY();

    int getLoginState();

    int getHintNpcIndex();

    int getHintPlayerIndex();

    int getLocalPlayerIndex();

    RSHashTable getItemContainers();

    RSNodeDeque getProjectiles();

    int getCycle();
}