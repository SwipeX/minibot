package com.minibot.bot.random;

import com.minibot.api.method.Bank;
import com.minibot.api.method.Widgets;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.WidgetComponent;
import com.minibot.util.Configuration;

import java.awt.Color;
import java.awt.Graphics2D;
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
        if (!atPin()) {
            return false;
        }
        String numString = Integer.toString(number);
        for (int i = 0; i < (BANK_PIN_CHILD_END - BANK_PIN_CHILD_START); i += 2) {
            WidgetComponent container = Widgets.get(BANK_PIN_PARENT, BANK_PIN_CHILD_START + i);
            if (container != null && container.visible()) {
                WidgetComponent button = container.children()[0];
                WidgetComponent text = container.children()[1];
                if (text.text().equals(numString)) {
                    button.processAction("Select");
                    Time.sleep(2200, 2500);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean inputPin(char... pin) {
        for (char c : pin) {
            if (!inputNumber(Integer.parseInt(Character.toString(c)))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean validate() {
        return atPin();
    }

    @Override
    public void run() {
        try (Stream<String> lines = Files.lines(Paths.get(BANK_PIN_FILE))) {
            Optional<String> first = lines.findFirst();
            if (first != null) {
                if (inputPin(first.get().toCharArray())) {
                    Time.sleep(Bank::viewing, 5000);
                }
            }
        } catch (IOException e) {
            System.err.println("Invalid bank pin file");
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.CYAN);
        g.drawString("Bank Pin Solver", 10, 300);
    }
}