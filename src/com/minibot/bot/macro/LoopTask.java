package com.minibot.bot.macro;

import com.minibot.api.util.Time;
import com.minibot.ui.MacroSelector;

/**
 * @author Tyler Sedlar
 * @since 7/21/2015
 */
public abstract class LoopTask extends Thread {

    public abstract int loop();

    @Override
    public final void run() {
        while (MacroSelector.current() != null) {
            try {
                int loop = loop();
                if (loop >= 0) {
                    Time.sleep(loop);
                } else {
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
