package com.minibot.mod;

import com.minibot.analysis.visitor.GraphVisitor;
import com.minibot.mod.hooks.Hook;
import org.objectweb.asm.tree.ClassNode;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Map;

/**
 * @author Tyler Sedlar
 */
public class ModScript {

    private static final int MAGIC = 0xFADFAD;
    private static final String ENCRYPTION_KEY = "Valid hook data";

    public static void write(String file, String hash, Collection<GraphVisitor> visitors) throws Exception {
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(file))) {
            out.writeInt(MAGIC);
            out.writeUTF(hash);
            out.writeInt(visitors.size());
            for (GraphVisitor gv : visitors) {
                ClassNode cn = gv.getCn();
                out.writeBoolean(cn != null);
                if (cn == null)
                    continue;
                out.writeUTF(Crypto.encrypt(cn.name));
                out.writeUTF(Crypto.encrypt(gv.id()));
                out.writeInt(gv.getHooks().size());
                for (Map.Entry<String, Hook> entry : gv.getHooks().entrySet()) {
                    Hook hook = entry.getValue();
                    hook.writeToEncryptedStream(out);
                }
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR WRITING MODSCRIPT");
        }
    }
}