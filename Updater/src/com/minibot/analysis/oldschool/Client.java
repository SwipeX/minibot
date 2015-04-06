package com.minibot.analysis.oldschool;

import com.minibot.analysis.visitor.GraphVisitor;
import com.minibot.analysis.visitor.VisitorInfo;
import com.minibot.internal.mod.hooks.FieldHook;
import com.minibot.internal.mod.hooks.InvokeHook;
import com.minibot.util.ArrayIterator;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.*;
import org.objectweb.asm.tree.*;

@VisitorInfo(hooks = {"doAction", "players", "npcs", "canvas", "player", "region", "widgets", "objects",
        "groundItems", "cameraX", "cameraY", "cameraZ", "cameraPitch", "cameraYaw", "mapScale", "mapOffset",
        "mapAngle", "baseX", "baseY", "settings", "gameSettings", "widgetPositionsX", "widgetPositionsY",
        "widgetWidths", "widgetHeights", "renderRules", "tileHeights", "widgetNodes", "npcIndices",
        "loadObjectDefinition", "loadNpcDefinition", "loadItemDefinition", "plane", "gameState"})
public class Client extends GraphVisitor {

    @Override
    public String iface() {
        return updater.getAccessorPackage() + "/Client";
    }

    @Override
    public boolean validate(ClassNode cn) {
        return cn.name.equals("client");
    }

    @Override
    public void visit() {
        visitDoAction();
        visitDefLoader("loadObjectDefinition", "ObjectDefinition", false);
        visitDefLoader("loadNpcDefinition", "NpcDefinition", false);
        visitDefLoader("loadItemDefinition", "ItemDefinition", false);
        visitStaticFields();
        visitAll(new CameraXY());
        visitAll(new CameraZ());
        visitAll(new CameraPY());
        visitAll(new MapHooks());
        visitAll(new MapAngle());
        visit("Varpbits", new SettingHooks());
        visitAll(new WidgetPositionHooks());
        visitAll(new RenderRules());
        visitAll(new TileHeights());
        visitAll(new WidgetNodes());
        visitAll(new HintHooks());
        visitAll(new NpcIndices());
        visitAll(new Plane());
        visitAll(new GameState());
    }

    private void visitDoAction() {
        for (ClassNode cn : updater.classnodes.values()) {
            cn.methods.stream().filter(mn -> mn.desc.startsWith("(IIIILjava/lang/String;Ljava/lang/String;II") &&
                    mn.desc.endsWith(")V")).forEach(mn -> addHook(new InvokeHook("doAction", mn)));
        }
    }

    private void visitDefLoader(String hook, String visitor, boolean transform) {
        for (ClassNode cn : updater.classnodes.values()) {
            cn.methods.stream().filter(mn -> mn.desc.endsWith(")" + desc(visitor))).forEach(mn -> {
                int access = mn.access & ACC_STATIC;
                if (transform ? access == 0 : access > 0)
                    addHook(new InvokeHook(hook, cn.name, mn.name, mn.desc));
            });
        }
    }

    private void visitStaticFields() {
        add("players", cn.getField(null, "[" + desc("Player"), false));
        add("npcs", cn.getField(null, "[" + desc("Npc"), false));
        String playerDesc = desc("Player");
        String regionDesc = desc("Region");
        String widgetDesc = desc("Widget");
        String objectDesc = desc("InteractableObject");
        String dequeDesc = desc("NodeDeque");
        for (ClassNode node : updater.classnodes.values()) {
            for (FieldNode fn : node.fields) {
                if ((fn.access & Opcodes.ACC_STATIC) == 0) continue;
                if (fn.desc.equals("Ljava/awt/Canvas;")) {
                    add("canvas", fn);
                } else if (playerDesc != null && fn.desc.equals(playerDesc)) {
                    add("player", fn);
                } else if (regionDesc != null && fn.desc.equals(regionDesc)) {
                    add("region", fn);
                } else if (widgetDesc != null && fn.desc.equals("[[" + widgetDesc)) {
                    add("widgets", fn);
                } else if (objectDesc != null && fn.desc.equals("[" + objectDesc)) {
                    add("objects", fn);
                } else if (dequeDesc != null && fn.desc.equals("[[[" + dequeDesc)) {
                    add("groundItems", fn);
                }
            }
        }
    }

    private class CameraXY extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitJump(JumpNode jn) {
                    FieldMemberNode x = (FieldMemberNode) jn.layer(IAND, BALOAD, AALOAD, ISHR, IMUL, GETSTATIC);
                    if (x == null) return;
                    FieldMemberNode y = (FieldMemberNode) jn.layer(IAND, BALOAD, ISHR, IMUL, GETSTATIC);
                    if (y == null) return;
                    addHook(new FieldHook("cameraX", x.fin()));
                    addHook(new FieldHook("cameraY", y.fin()));
                    lock.set(true);
                }
            });
        }
    }

    private class CameraZ extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(final Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitJump(JumpNode jn) {
                    NumberNode nn = jn.firstNumber();
                    if (nn != null && nn.number() == 800) {
                        FieldMemberNode fmn = (FieldMemberNode) jn.layer(ISUB, IMUL, GETSTATIC);
                        if (fmn != null) {
                            addHook(new FieldHook("cameraZ", fmn.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class CameraPY extends BlockVisitor {

        private int added = 0;

        @Override
        public boolean validate() {
            return added < 2;
        }

        @Override
        public void visit(final Block block) {
            block.tree().accept(new NodeVisitor() {
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.opcode() == PUTSTATIC) {
                        NumberNode nn = (NumberNode) fmn.layer(IMUL, IAND, D2I, DMUL, LDC);
                        if (nn != null) {
                            int mul = nn.number();
                            nn = (NumberNode) fmn.layer(IMUL, IAND, SIPUSH);
                            if (nn != null && nn.number() == 0x07FF) {
                                String name = "camera" + (mul > 0 ? "Pitch" : "Yaw");
                                if (hooks.containsKey(name)) return;
                                addHook(new FieldHook(name, fmn.fin()));
                            }
                        }
                    }
                }
            });
        }
    }

    private class MapHooks extends BlockVisitor {

        private final ArrayIterator<String> itr = new ArrayIterator<>("mapScale", "mapOffset");

        @Override
        public boolean validate() {
            return itr.hasNext();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitJump(JumpNode jn) {
                    if (jn.opcode() == IF_ICMPLE || jn.opcode() == IF_ICMPGE) {
                        int push = jn.opcode() == IF_ICMPLE ? 60 : -20;
                        NumberNode nn = jn.firstNumber();
                        if (nn != null && nn.number() == push) {
                            FieldMemberNode fmn = (FieldMemberNode) jn.layer(IMUL, GETSTATIC);
                            if (fmn != null && fmn.desc().equals("I")) {
                                int nameIdx = jn.opcode() == IF_ICMPLE ? 0 : 1;
                                addHook(new FieldHook(itr.get(nameIdx), fmn.fin()));
                            }
                        }
                    }
                }
            });
        }
    }

    private class MapAngle extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.opcode() == PUTSTATIC && fmn.desc().equals("I")) {
                        if (fmn.layer(IMUL, IAND, IADD, IDIV) != null) {
                            hooks.put("mapAngle", new FieldHook("mapAngle", fmn.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class HintHooks extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.opcode() == INVOKESTATIC) {
                        if (mmn.child(0) != null && mmn.child(0).opcode() == IADD) {
                            if (mmn.child(1) != null && mmn.child(1).opcode() == IADD) {
                                if (mmn.child(2) != null && mmn.child(2).opcode() == IMUL) {
                                    AbstractNode xBlock = mmn.child(0).layer(ISHL, ISUB);
                                    AbstractNode yBlock = mmn.child(1).layer(ISHL, ISUB);
                                    FieldMemberNode type = (FieldMemberNode) mmn.child(2).first(GETSTATIC);
                                    if (xBlock != null && yBlock != null && type != null) {
                                        FieldMemberNode x = (FieldMemberNode) xBlock.layer(IMUL, GETSTATIC);
                                        FieldMemberNode baseX = x.parent().next().firstField();
                                        FieldMemberNode y = (FieldMemberNode) yBlock.layer(IMUL, GETSTATIC);
                                        FieldMemberNode baseY = y.parent().next().firstField();
//                                        hooks.put("hintX", new FieldHook("hintX", x.fin()));
                                        hooks.put("baseX", new FieldHook("baseX", baseX.fin()));
//                                        hooks.put("hintY", new FieldHook("hintY", y.fin()));
                                        hooks.put("baseY", new FieldHook("baseY", baseY.fin()));
                                        lock.set(true);
                                    }
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    private class SettingHooks extends BlockVisitor {

        private final ArrayIterator<String> itr = new ArrayIterator<>("settings", "gameSettings");

        @Override
        public boolean validate() {
            return itr.hasNext();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.opcode() == PUTSTATIC && fmn.desc().equals("[I")) {
                        NumberNode nn = (NumberNode) fmn.layer(NEWARRAY, SIPUSH);
                        if (nn != null && nn.number() == 2000) {
                            addHook(new FieldHook(itr.next(), fmn.fin()));
                        }
                    }
                }
            });
        }
    }

    private class WidgetPositionHooks extends BlockVisitor {

        private final ArrayIterator<String> itr = new ArrayIterator<>("widgetPositionsX", "widgetPositionsY",
                "widgetWidths", "widgetHeights");

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.desc().startsWith("(IIIII")) {
                        AbstractNode x = mmn.child(0);
                        if (x == null || x.opcode() != IALOAD) return;
                        AbstractNode y = mmn.child(1);
                        if (y == null || y.opcode() != IALOAD) return;
                        AbstractNode w = mmn.child(2);
                        if (w == null || w.opcode() != IALOAD) return;
                        AbstractNode h = mmn.child(3);
                        if (h == null || h.opcode() != IALOAD) return;
                        AbstractNode[] parents = {x, y, w, h};
                        FieldMemberNode[] fields = new FieldMemberNode[4];
                        for (int i = 0; i < parents.length; i++) {
                            FieldMemberNode fmn = parents[i].firstField();
                            if (fmn == null || !fmn.desc().equals("[I")) return;
                            for (int j = i - 1; j > 0; j--) {
                                if (fields[j].key().equals(fmn.key())) return;
                            }
                            fields[i] = fmn;
                        }
                        for (int i = 0; i < itr.size(); i++) {
                            hooks.put(itr.get(i), new FieldHook(itr.get(i), fields[i].fin()));
                        }
                        lock.set(true);
                    }
                }
            });
        }
    }

    private class RenderRules extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitJump(JumpNode jn) {
                    FieldMemberNode fmn = (FieldMemberNode) jn.layer(IAND, BALOAD, AALOAD, AALOAD, GETSTATIC);
                    if (fmn != null && fmn.desc().equals("[[[B")) {
                        addHook(new FieldHook("renderRules", fmn.fin()));
                        lock.set(true);
                    }
                }
            });
        }
    }

    private class TileHeights extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitVariable(VariableNode vn) {
                    FieldMemberNode fmn = (FieldMemberNode) vn.layer(ISUB, IALOAD, AALOAD, AALOAD, GETSTATIC);
                    if (fmn != null && fmn.desc().equals("[[[I")) {
                        hooks.put("tileHeights", new FieldHook("tileHeights", fmn.fin()));
                        lock.set(true);
                    }
                }
            });
        }
    }

    private class WidgetNodes extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitVariable(VariableNode vn) {
                    if (vn.opcode() == ASTORE) {
                        TypeNode tn = vn.firstType();
                        if (tn != null && tn.type().equals(clazz("WidgetNode"))) {
                            FieldMemberNode fmn = (FieldMemberNode) vn.layer(INVOKEVIRTUAL, GETSTATIC);
                            if (fmn != null && fmn.desc().equals(desc("HashTable"))) {
                                hooks.put("widgetNodes", new FieldHook("widgetNodes", fmn.fin()));
                                lock.set(true);
                            }
                        }
                    }
                }
            });
        }
    }

    private class NpcIndices extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.parent() != null && fmn.parent().opcode() == AALOAD) {
                        if (fmn.desc().equals("[" + desc("Npc"))) {
                            fmn = (FieldMemberNode) fmn.parent().layer(IALOAD, GETSTATIC);
                            if (fmn != null && fmn.desc().equals("[I")) {
                                addHook(new FieldHook("npcIndices", fmn.fin()));
                                lock.set(true);
                            }
                        }
                    }
                }
            });
        }
    }

    private class Plane extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visit(AbstractNode n) {
                    if (n.opcode() == AASTORE) {
                        FieldMemberNode fmn = (FieldMemberNode) n.layer(AALOAD, AALOAD, IMUL, GETSTATIC);
                        if (fmn != null && fmn.desc().equals("I")) {
                            hooks.put("plane", new FieldHook("plane", fmn.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class GameState extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitJump(JumpNode jn) {
                    if (jn.opcode() == IF_ICMPNE) {
                        NumberNode nn = jn.firstNumber();
                        if (nn != null && nn.number() == 1000) {
                            FieldMemberNode fmn = (FieldMemberNode) jn.layer(IMUL, GETSTATIC);
                            if (fmn != null && fmn.owner().equals("client") && fmn.desc().equals("I")) {
                                hooks.put("gameState", new FieldHook("gameState", fmn.fin()));
                                lock.set(true);
                            }
                        }
                    }
                }
            });
        }
    }
}
