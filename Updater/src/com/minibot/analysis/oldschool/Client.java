package com.minibot.analysis.oldschool;

import com.minibot.analysis.visitor.GraphVisitor;
import com.minibot.analysis.visitor.VisitorInfo;
import com.minibot.mod.hooks.FieldHook;
import com.minibot.mod.hooks.InvokeHook;
import com.minibot.util.ArrayIterator;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.query.InsnQuery;
import org.objectweb.asm.commons.cfg.tree.NodeTree;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.*;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;

@VisitorInfo(hooks = {"processAction", "players", "npcs", "canvas", "player", "region", "widgets", "objects",
        "groundItems", "cameraX", "cameraY", "cameraZ", "cameraPitch", "cameraYaw", "mapScale", "mapOffset",
        "mapAngle", "baseX", "baseY", "settings", "gameSettings", "widgetPositionsX", "widgetPositionsY",
        "widgetWidths", "widgetHeights", "renderRules", "tileHeights", "widgetNodes", "npcIndices", "playerIndices",
        "loadObjectDefinition", "loadNpcDefinition", "loadItemDefinition", "plane", "gameState", "mouseIdleTime",
        "hoveredRegionTileX", "hoveredRegionTileY", "experiences", "levels", "realLevels", "username", "password", "loginState",
        "hintX", "hintY", "hintPlayerIndex", "hintNpcIndex", "screenWidth", "screenHeight", "screenZoom", "screenState"})
public class Client extends GraphVisitor {

    @Override
    public String iface() {
        return getUpdater().getAccessorPackage() + "/Client";
    }

    @Override
    public boolean validate(ClassNode cn) {
        return cn.name.equals("client");
    }

    @Override
    public void visit() {
        visitProcessAction();
        visitMouseIdleTime();
        visitDefLoader("loadObjectDefinition", "ObjectDefinition", false);
        visitDefLoader("loadNpcDefinition", "NpcDefinition", false);
        visitDefLoader("loadItemDefinition", "ItemDefinition", false);
        visitStaticFields();
        visitAll(new HintPlayerIndex());
        visitAll(new HintNpcIndex());
        visitAll(new ExperienceHooks());
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
        visitAll(new PlayerIndices());
        visitAll(new Plane());
        visitAll(new GameState());
        visitAll(new Username());
        visitAll(new Password());
        visitAll(new LoginState());
        visitAll(new ScreenVisitor());
        visitIfM(new ScreenState(), t -> t.desc.startsWith("([L") && t.desc.contains(";IIIIII"));
        visitAll(new HoveredRegionTiles());
    }

    private void visitMouseIdleTime() {
        for (ClassNode cn : getUpdater().getClassnodes().values()) {
            if (!cn.interfaces.contains("java/awt/event/MouseListener"))
                continue;
            for (MethodNode meth : cn.methods) {
                if (!meth.name.equals("mouseExited"))
                    continue;
                getUpdater().graphs().get(cn).get(meth).forEach(b -> b.tree().accept(new NodeVisitor() {
                    @Override
                    public void visitField(FieldMemberNode fmn) {
                        if (fmn.opcode() != PUTSTATIC || fmn.children() != 1)
                            return;
                        NumberNode iconst_0 = fmn.firstNumber();
                        if (iconst_0 == null || iconst_0.number() != 0)
                            return;
                        addHook(new FieldHook("mouseIdleTime", fmn.fin()));
                    }
                }));
            }
        }
    }

    private void visitProcessAction() {
        for (ClassNode cn : getUpdater().getClassnodes().values()) {
            cn.methods.stream().filter(mn -> mn.desc.startsWith("(IIIILjava/lang/String;Ljava/lang/String;II") &&
                    mn.desc.endsWith(")V")).forEach(mn -> addHook(new InvokeHook("processAction", mn)));
        }
    }

    private void visitDefLoader(String hook, String visitor, boolean transform) {
        for (ClassNode cn : getUpdater().getClassnodes().values()) {
            cn.methods.stream().filter(mn -> mn.desc.endsWith(")" + desc(visitor))).forEach(mn -> {
                int access = mn.access & ACC_STATIC;
                if (transform ? access == 0 : access > 0)
                    addHook(new InvokeHook(hook, cn.name, mn.name, mn.desc));
            });
        }
    }

    private void visitStaticFields() {
        add("players", getCn().getField(null, "[" + desc("Player"), false));
        add("npcs", getCn().getField(null, "[" + desc("Npc"), false));
        String playerDesc = desc("Player");
        String regionDesc = desc("Region");
        String widgetDesc = desc("Widget");
        String objectDesc = desc("InteractableObject");
        String dequeDesc = desc("NodeDeque");
        for (ClassNode node : getUpdater().getClassnodes().values()) {
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

    private class ScreenVisitor extends BlockVisitor {

        private int added;

        @Override
        public boolean validate() {
            return added < 3;
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitNumber(NumberNode nn) {
                    if (nn.number() == 334) {
                        FieldMemberNode set = (FieldMemberNode) nn.preLayer(IDIV, ISHL, IMUL, PUTSTATIC);
                        if (set != null) {
                            addHook(new FieldHook("screenZoom", set.fin()));
                            added++;
                        }
                    }
                }

                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.opcode() == PUTSTATIC) {
                        List<AbstractNode> divs = fmn.layerAll(IMUL, IADD, IDIV);
                        if (divs == null || divs.size() != 2)
                            return;
                        for (AbstractNode idiv : divs) {
                            FieldMemberNode val = (FieldMemberNode) idiv.layer(IMUL, GETSTATIC);
                            if (val == null)
                                continue;
                            if (!getHooks().containsKey("screenWidth")) {
                                addHook(new FieldHook("screenWidth", val.fin()));
                                added++;
                            } else if (!getHooks().containsKey("screenHeight")) {
                                addHook(new FieldHook("screenHeight", val.fin()));
                                added++;
                            }
                        }
                    }
                }
            });
        }
    }

    private class LoginState extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                @Override
                public void visitJump(JumpNode jn) {
                    if (jn.opcode() == IF_ICMPNE) {
                        NumberNode nn = jn.firstNumber();
                        if (nn != null && nn.number() >= 3) {
                            FieldMemberNode fmn = (FieldMemberNode) jn.layer(IMUL, GETSTATIC);
                            if (fmn == null || !fmn.owner().equals(((FieldHook) getHooks().get("username")).getClazz()) || !fmn.desc().equals("I"))
                                return;
                            addHook(new FieldHook("loginState", fmn.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class Username extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitJump(JumpNode jn) {
                    NumberNode len = jn.firstNumber();
                    MethodMemberNode lenCall = jn.firstMethod();
                    if (len == null || len.number() != 320 || lenCall == null || !lenCall.name().equals("length"))
                        return;
                    FieldMemberNode fmn = lenCall.firstField();
                    if (fmn != null) {
                        addHook(new FieldHook("username", fmn.fin()));
                        lock.set(true);
                    }
                }
            });
        }
    }

    private class Password extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitJump(JumpNode jn) {
                    NumberNode len = jn.firstNumber();
                    MethodMemberNode lenCall = jn.firstMethod();
                    if (len == null || len.number() != 20 || lenCall == null || !lenCall.name().equals("length"))
                        return;
                    FieldMemberNode fmn = lenCall.firstField();
                    if (fmn != null) {
                        addHook(new FieldHook("password", fmn.fin()));
                        lock.set(true);
                    }
                }
            });
        }
    }

    private class ExperienceHooks extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            if (block.count(new InsnQuery(ISTORE)) == 4 && block.count(new InsnQuery(IASTORE)) == 3) {
                NodeTree root = block.tree();
                AbstractNode storeE = root.find(IASTORE, 0);
                if (storeE == null)
                    return;
                FieldMemberNode experiences = storeE.firstField();
                if (experiences == null || experiences.opcode() != GETSTATIC)
                    return;
                AbstractNode storeL = root.find(IASTORE, 1);
                if (storeL == null)
                    return;
                FieldMemberNode levels = storeL.firstField();
                if (levels == null || levels.opcode() != GETSTATIC)
                    return;
                AbstractNode storeRL = root.find(IASTORE, 2);
                if (storeRL == null)
                    return;
                FieldMemberNode realLevels = storeRL.firstField();
                if (realLevels == null || realLevels.opcode() != GETSTATIC)
                    return;
                addHook(new FieldHook("experiences", experiences.fin()));
                addHook(new FieldHook("levels", levels.fin()));
                addHook(new FieldHook("realLevels", realLevels.fin()));
                lock.set(true);
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
                @Override
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
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                @Override
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

        private static final int added = 0;

        @Override
        public boolean validate() {
            return added < 2;
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.opcode() == PUTSTATIC) {
                        NumberNode nn = (NumberNode) fmn.layer(IMUL, IAND, D2I, DMUL, LDC);
                        if (nn != null) {
                            int mul = nn.number();
                            nn = (NumberNode) fmn.layer(IMUL, IAND, SIPUSH);
                            if (nn != null && nn.number() == 0x07FF) {
                                String name = "camera" + (mul > 0 ? "Pitch" : "Yaw");
                                if (getHooks().containsKey(name)) return;
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
                @Override
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
                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.opcode() == PUTSTATIC && fmn.desc().equals("I")) {
                        if (fmn.layer(IMUL, IAND, IADD, IDIV) != null) {
                            getHooks().put("mapAngle", new FieldHook("mapAngle", fmn.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class HintPlayerIndex extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                @Override
                public void visitJump(JumpNode jn) {
                    if (jn.opcode() == IF_ICMPNE) {
                        FieldMemberNode fmn = (FieldMemberNode) jn.layer(IMUL, GETSTATIC);
                        if (fmn != null && fmn.opcode() == GETSTATIC && fmn.owner().equals("client")) {
                            FieldMemberNode array = (FieldMemberNode) jn.layer(IALOAD, GETSTATIC);
                            if (array != null && array.desc().equals("[I")) {
                                VariableNode vn = array.nextVariable();
                                if (vn != null && vn.var() == 1) {
                                    addHook(new FieldHook("hintPlayerIndex", fmn.fin()));
                                    lock.set(true);
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    private class HintNpcIndex extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                @Override
                public void visitJump(JumpNode jn) {
                    if (jn.opcode() == IF_ICMPNE) {
                        FieldMemberNode fmn = (FieldMemberNode) jn.layer(IMUL, GETSTATIC);
                        if (fmn != null && fmn.opcode() == GETSTATIC && fmn.owner().equals("client")) {
                            FieldMemberNode array = (FieldMemberNode) jn.layer(IALOAD, GETSTATIC);
                            if (array != null && array.desc().equals("[I")) {
                                AbstractNode an = array.next(ISUB);
                                if (an != null && an.layer(IMUL, GETSTATIC) != null) {
                                    addHook(new FieldHook("hintNpcIndex", fmn.fin()));
                                    lock.set(true);
                                }
                            }
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
                @Override
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
                                        getHooks().put("hintX", new FieldHook("hintX", x.fin()));
                                        getHooks().put("baseX", new FieldHook("baseX", baseX.fin()));
                                        getHooks().put("hintY", new FieldHook("hintY", y.fin()));
                                        getHooks().put("baseY", new FieldHook("baseY", baseY.fin()));
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
                @Override
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
                @Override
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
                            getHooks().put(itr.get(i), new FieldHook(itr.get(i), fields[i].fin()));
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
                @Override
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
                @Override
                public void visitVariable(VariableNode vn) {
                    FieldMemberNode fmn = (FieldMemberNode) vn.layer(ISUB, IALOAD, AALOAD, AALOAD, GETSTATIC);
                    if (fmn != null && fmn.desc().equals("[[[I")) {
                        getHooks().put("tileHeights", new FieldHook("tileHeights", fmn.fin()));
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
                @Override
                public void visitVariable(VariableNode vn) {
                    if (vn.opcode() == ASTORE) {
                        TypeNode tn = vn.firstType();
                        if (tn != null && tn.type().equals(clazz("WidgetNode"))) {
                            FieldMemberNode fmn = (FieldMemberNode) vn.layer(INVOKEVIRTUAL, GETSTATIC);
                            if (fmn != null && fmn.desc().equals(desc("HashTable"))) {
                                getHooks().put("widgetNodes", new FieldHook("widgetNodes", fmn.fin()));
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
                @Override
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

    private class PlayerIndices extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.parent() != null && fmn.parent().opcode() == AALOAD) {
                        if (fmn.desc().equals("[" + desc("Player"))) {
                            fmn = (FieldMemberNode) fmn.parent().layer(IALOAD, GETSTATIC);
                            if (fmn != null && fmn.desc().equals("[I")) {
                                addHook(new FieldHook("playerIndices", fmn.fin()));
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
                @Override
                public void visit(AbstractNode n) {
                    if (n.opcode() == AASTORE) {
                        FieldMemberNode fmn = (FieldMemberNode) n.layer(AALOAD, AALOAD, IMUL, GETSTATIC);
                        if (fmn != null && fmn.desc().equals("I")) {
                            getHooks().put("plane", new FieldHook("plane", fmn.fin()));
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
                @Override
                public void visitJump(JumpNode jn) {
                    if (jn.opcode() == IF_ICMPNE) {
                        NumberNode nn = jn.firstNumber();
                        if (nn != null && nn.number() == 1000) {
                            FieldMemberNode fmn = (FieldMemberNode) jn.layer(IMUL, GETSTATIC);
                            if (fmn != null && fmn.owner().equals("client") && fmn.desc().equals("I")) {
                                getHooks().put("gameState", new FieldHook("gameState", fmn.fin()));
                                lock.set(true);
                            }
                        }
                    }
                }
            });
        }
    }

    private class ScreenState extends BlockVisitor {

        private int added = 0;

        @Override
        public boolean validate() {
            return added < 3;
        }

        @Override
        public void visit(Block block) {
            if (block.count(new InsnQuery(ISTORE)) > 0) {
                block.tree().accept(new NodeVisitor(this) {
                    public void visitField(FieldMemberNode fmn) {
                        if (fmn.opcode() == GETSTATIC && fmn.desc().equals("I")) {
                            VariableNode vn = (VariableNode) fmn.preLayer(IMUL, ISTORE);
                            if (vn != null) {
                                String name = null;
                                int var = vn.var();
                                if (var == 21) {
                                    name = "screenState";
                                } else if (var == 22) {
                                    name = "screenWidth";
                                } else if (var == 23) {
                                    name = "screenHeight";
                                }
                                if (name == null || getHooks().containsKey(name))
                                    return;
                                added++;
                                addHook(new FieldHook(name, fmn.fin()));
                            }
                        }
                    }
                });
            }
        }
    }

    private class HoveredRegionTiles extends BlockVisitor {

        private int added = 0;

        @Override
        public boolean validate() {
            return added < 2;
        }

        @Override
        public void visit(Block block) {
            List<AbstractNode> layer = block.tree().layerAll(ISTORE, GETSTATIC);
            if (layer != null && layer.size() == 2) {
                VariableNode load = (VariableNode) block.tree().layer(INVOKEVIRTUAL, IADD, ILOAD);
                if (load != null) {
                    FieldMemberNode base = (FieldMemberNode) load.parent().layer(IMUL, GETSTATIC);
                    if (base != null) {
                        String loadTypeA = base.key().equals(getHookKey("baseX")) ? "hoveredRegionTileX" : "hoveredRegionTileY";
                        String loadTypeB = loadTypeA.equals("hoveredRegionTileX") ? "hoveredRegionTileY" : "hoveredRegionTileX";
                        block.tree().accept(new NodeVisitor(this) {
                            @Override
                            public void visitVariable(VariableNode vn) {
                                if (vn.opcode() == ISTORE) {
                                    FieldMemberNode fmn = vn.firstField();
                                    if (fmn != null) {
                                        String name;
                                        name = vn.var() == load.var() ? loadTypeA : loadTypeB;
                                        if (getHooks().containsKey(name))
                                            return;
                                        addHook(new FieldHook(name, fmn.fin()));
                                        added++;
                                    }
                                }
                            }
                        });
                    }
                }
            }
        }
    }
}