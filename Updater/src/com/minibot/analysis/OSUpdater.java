package com.minibot.analysis;

import com.minibot.analysis.oldschool.*;
import com.minibot.analysis.oldschool.Character;
import com.minibot.analysis.visitor.GraphVisitor;
import com.minibot.util.Configuration;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.graph.FlowGraph;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.NumberNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.JarFile;

/**
 * @author Tyler Sedlar
 */
public class OSUpdater extends Updater {

    private static final boolean server = new File("/usr/share/nginx/html/data/").exists();

    private static GraphVisitor[] createVisitors() {
        return new GraphVisitor[]{
                new Node(), new CacheableNode(), new RenderableNode(), new HashTable(),
                new Cache(), new NodeDeque(), new Queue(), new Tile(), new Model(),
                new AnimationSequence(), new Character(), new NpcDefinition(), new Npc(),
                new Player(), new Item(),new ItemContainer(), new ItemDefinition(), new InteractableObject(),
                new WallDecoration(), new FloorDecoration(), new Boundary(),
                new ObjectDefinition(), new Region(), new Canvas(), new WidgetNode(),
                new Widget(), new Varpbits(), new Client()
        };
    }

    @Override
    public String getType() {
        return "Oldschool RuneScape";
    }

    @Override
    public String getHash() {
        try (JarFile jar = new JarFile(getFile())) {
            return Integer.toString(jar.getManifest().hashCode());
        } catch (IOException | NullPointerException e) {
            return getFile().getName().replace(".jar", "");
        }
    }

    @Override
    public String getAccessorPrefix() {
        return "com/minibot/internal/accessors/oldschool/RS";
    }

    @Override
    public String getWrapperPrefix() {
        return "com/minibot/api/wrapper/";
    }

    @Override
    public String getModscriptLocation() {
        return server ? "/usr/share/nginx/html/data/oldschool.dat" : Configuration.CACHE + "/oldschool.dat";
    }

    @Override
    public int getRevision(Map<ClassNode, Map<MethodNode, FlowGraph>> graphs) {
        ClassNode client = getClassnodes().get("client");
        MethodNode init = client.getMethodByName("init");
        FlowGraph graph = graphs.get(client).get(init);
        AtomicInteger revision = new AtomicInteger(0);
        for (Block block : graph) {
            new BlockVisitor() {
                @Override
                public boolean validate() {
                    return revision.get() == 0;
                }

                @Override
                public void visit(Block block) {
                    block.tree().accept(new NodeVisitor(this) {
                        @Override
                        public void visitNumber(NumberNode nn) {
                            if (nn != null && nn.opcode() == SIPUSH) {
                                if ((nn = nn.nextNumber()) != null && nn.opcode() == SIPUSH) {
                                    if ((nn = nn.nextNumber()) != null) {
                                        revision.set(nn.number());
                                    }
                                }
                            }
                        }
                    });
                }
            }.visit(block);
        }
        return revision.get();
    }

    static {
        Configuration.setup();
    }

    public OSUpdater(File file, GraphVisitor[] visitors, boolean closeOnOld) throws Exception {
        super(file, visitors, closeOnOld);
    }

    public OSUpdater(File file, boolean closeOnOld) throws Exception {
        this(file, createVisitors(), closeOnOld);
    }

    public static void main(String... args) throws Exception {
        Updater updater = new OSUpdater(null, false);
//        Updater updater = new OSUpdater(new File("79.jar"), false);
        updater.setPrint(true);
        updater.run();
    }
}