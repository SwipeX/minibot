package com.minibot.bot.random;

import com.minibot.api.method.Widgets;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.WidgetComponent;
import com.minibot.util.Configuration;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Tyler Sedlar
 * @since 6/24/2015
 */
public class PinSolver extends RandomEvent {

    private static final String BANK_PIN_FILE = Configuration.CACHE + "bank.dat";

    private static final int BANK_PIN_PARENT = 213;
    private static final int BANK_PIN_CHILD_START = 16, BANK_PIN_CHILD_END = 34;

    private boolean atPin() {
        WidgetComponent comp = Widgets.get(BANK_PIN_PARENT, BANK_PIN_CHILD_START);
        return comp != null && comp.visible();
    }

    private boolean inputNumber(int number) {
        if (!atPin())
            return false;
        String numString = Integer.toString(number);
        for (int i = 0; i < (BANK_PIN_CHILD_END - BANK_PIN_CHILD_START); i += 2) {
            WidgetComponent container = Widgets.get(BANK_PIN_PARENT, BANK_PIN_CHILD_START + i);
            if (container != null && container.visible()) {
                WidgetComponent button = container.children()[0];
                WidgetComponent text = container.children()[1];
                if (text.text().equals(numString)) {
                    button.processAction("Select");
                    Time.sleep(1200, 1500);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean inputPin() {
        try (Stream<String> lines = Files.lines(Paths.get(BANK_PIN_FILE))) {
            Optional<String> first = lines.findFirst();
            if (first != null) {
                char[] chars = first.get().toCharArray();
                for (char c : chars) {
                    if (!inputNumber(Integer.parseInt(Character.toString(c))))
                        return false;
                }
                return true;
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean validate() {
        return atPin();
    }

    @Override
    public void run() {
        if (inputPin())
            Time.sleep(2000);
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.CYAN);
        g.drawString("Bank Pin Solver", 10, 300);
    }
}
