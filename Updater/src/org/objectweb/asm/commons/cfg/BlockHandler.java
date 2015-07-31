package org.objectweb.asm.commons.cfg;

public interface BlockHandler {

    boolean handle(int followIndex, Block block);
}