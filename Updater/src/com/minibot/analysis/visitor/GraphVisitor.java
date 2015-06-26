package com.minibot.analysis.visitor;

import com.minibot.analysis.Updater;
import com.minibot.mod.hooks.FieldHook;
import com.minibot.mod.hooks.Hook;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.graph.FlowGraph;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public abstract class GraphVisitor implements Opcodes {

    private final Map<String, Hook> hooks = new HashMap<>();

    private Updater updater;
    private ClassNode cn;
    private FlowGraph graph;

    private String id;

    public abstract boolean validate(ClassNode cn);

    public abstract void visit();

    public final String id() {
        return id != null ? id : (id = getClass().getSimpleName());
    }

    public String iface() {
        return updater.getAccessorPrefix() + id();
    }

    public String clazz(String visitor) {
        try {
            return updater.visitor(visitor).cn.name;
        } catch (NullPointerException e) {
            return "null";
        }
    }

    public String desc() {
        return desc(id());
    }

    public String desc(String visitor) {
        return "L" + clazz(visitor) + ";";
    }

    public String literalDesc() {
        return desc(id());
    }

    public String literalDesc(String visitor) {
        return "L" + updater.visitor(visitor).id() + ";";
    }

    public String key(String hook) {
        FieldHook fh = (FieldHook) hooks.get(hook);
        return fh != null ? fh.getClazz() + "." + fh.getField() : null;
    }

    public final void addHook(Hook hook) {
        if (hook.name == null)
            return;
        hooks.put(hook.name, hook);
    }

    public final void add(String name, FieldNode fn) {
        if (name == null || fn == null)
            return;
        hooks.put(name, new FieldHook(name, fn));
    }

    public final void add(String name, FieldNode fn, String returnDesc) {
        if (name == null || fn == null)
            return;
        hooks.put(name, new FieldHook(name, fn));
    }

    public final void visit(String visitor, BlockVisitor bv) {
        ClassNode cn = updater.visitor(visitor).cn;
        if (cn == null)
            return;
        for (FlowGraph graph : updater.graphs().get(cn).values()) {
            this.graph = graph;
            for (Block block : graph) {
                if (bv.validate()) {
                    bv.visit(block);
                }
            }
        }
    }

    public final void visit(BlockVisitor bv) {
        visit(id(), bv);
        bv.visitEnd();
    }

    public final void visitAll(BlockVisitor bv) {
        for (Map<MethodNode, FlowGraph> map : updater.graphs().values()) {
            for (FlowGraph graph : map.values()) {
                this.graph = graph;
                for (Block block : graph) {
                    if (bv.validate())
                        bv.visit(block);
                }
            }
        }
        bv.visitEnd();
    }

    public final void visit(MethodVisitor mv) {
        for (MethodNode mn : cn.methods)
            mn.accept(mv);
    }

    public final void visitIf(BlockVisitor bv, Predicate<Block> blockPredicate) {
        for (Map<MethodNode, FlowGraph> map : updater.graphs().values()) {
            for (FlowGraph graph : map.values()) {
                this.graph = graph;
                for (Block block : graph) {
                    if (bv.validate() && blockPredicate.test(block))
                        bv.visit(block);
                }
            }
        }
        bv.visitEnd();
    }

    public final void visitIfM(BlockVisitor bv, Predicate<MethodNode> methodPredicate) {
        for (Map<MethodNode, FlowGraph> map : updater.graphs().values()) {
            for (Map.Entry<MethodNode, FlowGraph> graph : map.entrySet()) {
                if (!methodPredicate.test(graph.getKey()))
                    continue;
                this.graph = graph.getValue();
                for (Block block : this.graph) {
                    if (bv.validate())
                        bv.visit(block);
                }
            }
        }
        bv.visitEnd();
    }

    public final void visitAll(MethodVisitor mv) {
        for (ClassNode cn : updater.getClassnodes().values()) {
            for (MethodNode mn : cn.methods)
                mn.accept(mv);
        }
    }

    public String getHookKey(String hook) {
        Hook h = hooks.get(hook);
        if (h == null)
            return null;
        FieldHook fh = (FieldHook) h;
        return fh.getClazz() + "." + fh.getField();
    }

    public Map<String, Hook> getHooks() {
        return hooks;
    }

    public Updater getUpdater() {
        return updater;
    }

    public void setUpdater(Updater updater) {
        this.updater = updater;
    }

    public ClassNode getCn() {
        return cn;
    }

    public void setCn(ClassNode cn) {
        this.cn = cn;
    }

    public FlowGraph getGraph() {
        return graph;
    }

    public void setGraph(FlowGraph graph) {
        this.graph = graph;
    }
}