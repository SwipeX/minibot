package com.minibot.internal.mod.hooks;

import com.minibot.internal.mod.ModScript;
import com.minibot.internal.mod.reflection.FieldValue;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
public interface Hookable {

    public default ReflectionData annotation() {
        return getClass().getAnnotation(ReflectionData.class);
    }

    public default FieldValue hook(String className, String hookName) {
        return ModScript.hook(className + "#" + hookName);
    }

    public default FieldValue hook(String hookName) {
        return hook(annotation().className(), hookName);
    }

    public default InvokeHook serveInvoke(String className, String hookName) {
        return ModScript.serveInvoke(className + "#" + hookName);
    }

    public default InvokeHook serveInvoke(String hookName) {
        return serveInvoke(annotation().className(), hookName);
    }
}
