package com.minibot.mod.hooks;

import com.minibot.mod.ModScript;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
public interface Hookable {

    public default InvokeHook serveInvoke(String className, String hookName) {
        return ModScript.serveInvoke(className + "#" + hookName);
    }
}
