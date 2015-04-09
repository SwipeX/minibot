package com.minibot.mod;

import com.minibot.mod.hooks.*;

import java.io.*;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class ModScript {

    public static final Map<String, String> CLASS_MAP = new HashMap<>();
    public static final Map<String, FieldHook> FIELD_HOOK_MAP = new HashMap<>();
    public static final Map<String, InvokeHook> INVOKE_HOOK_MAP = new HashMap<>();

    private static final int MAGIC = 0xFADFAD;

    private static ClassLoader classloader;

    public static void setClassLoader(ClassLoader classloader) {
        ModScript.classloader = classloader;
    }

    public static String getClass(String definedName) {
        return CLASS_MAP.get(definedName);
    }

    public static FieldHook getFieldHook(String definedName) {
        return FIELD_HOOK_MAP.get(definedName);
    }

    public static InvokeHook getInvokeHook(String definedName) {
        return INVOKE_HOOK_MAP.get(definedName);
    }

    public static String getDefinedName(String key) {
        for (String definedName : CLASS_MAP.keySet()) {
            String internalName = CLASS_MAP.get(definedName);
            if (internalName != null && internalName.equals(key)) {
                return definedName;
            }
        }
        return null;
    }

    public static ClassLoader classes() {
        return classloader;
    }

    public static int multiplyValueByDecoder(int value, String hook) {
        try {
            FieldHook fh = FIELD_HOOK_MAP.get(hook);
            if (fh.multiplier == -1)
                return value;
            return value * fh.multiplier;
        } catch (Exception e) {
            return value;
        }
    }

    public static int multiplyValueByEncoder(int value, String hook) {
        try {
            FieldHook fh = FIELD_HOOK_MAP.get(hook);
            if (fh.multiplier == -1)
                return value;
            BigInteger num = BigInteger.valueOf(fh.multiplier);
            return value * num.modInverse(new BigInteger(String.valueOf(1L << 32))).intValue();
        } catch (Exception e) {
            return value;
        }
    }

    public static InvokeHook serveInvoke(String name) {
        try {
            return INVOKE_HOOK_MAP.get(name);
        } catch (Exception e) {
            System.out.println(name + " isn't a valid invoke hook");
            return null;
        }
    }

    public static void load(byte[] bytes, String hash) throws Exception {
        try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes))) {
            int magic = in.readInt();
            if (magic != MAGIC) {
                throw new IOException("Invalid modscript format");
            }
            String innerHash = in.readUTF();
            if (!innerHash.equals(hash)) {
                throw new IOException("Modscript is out-of-date");
            }
            try {
                int classSize = in.readInt();
                for (int i = 0; i < classSize; i++) {
                    boolean valid = in.readBoolean();
                    if (valid) {
                        String className = Crypto.crypt(in.readUTF());
                        String id = Crypto.crypt(in.readUTF());
                        CLASS_MAP.put(id, className);
                        int hookCount = in.readInt();
                        for (int j = 0; j < hookCount; j++) {
                            Hook hook = Hook.readDataStream(in);
                            if (hook == null)
                                continue;
                            if (hook instanceof FieldHook) {
                                FieldHook fh = (FieldHook) hook;
                                FIELD_HOOK_MAP.put(id + "#" + fh.name, fh);
                            } else if (hook instanceof InvokeHook) {
                                InvokeHook ih = (InvokeHook) hook;
                                INVOKE_HOOK_MAP.put(id + "#" + ih.name, ih);
                            }
                        }
                    }
                }
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }
    }
}