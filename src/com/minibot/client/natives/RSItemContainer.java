package com.minibot.client.natives;


/**
 * @author Tim Dekker
 * @since 7/13/15
 */
public interface RSItemContainer extends ClientNative {

    int[] getIds();

    int[] getStackSizes();
}
