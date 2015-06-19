package com.minibot.macros;

import com.minibot.Minibot;
import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.method.*;
import com.minibot.api.util.Condition;
import com.minibot.api.util.Random;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.locatable.GameObject;
import com.minibot.api.wrapper.locatable.GroundItem;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;
import com.minibot.client.natives.RSItemDefinition;
import com.minibot.client.natives.RSObjectDefinition;

import java.awt.*;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Calculations for red chins only.
 *
 * @author Tim Dekker
 * @since 5/11/15
 */
@Manifest(name = "BirdHunter", author = "Swipe", version = "1.0.0", description = "Hunts Birds")
public class BirdHunter extends Macro implements Renderable {

	private static final int POS_Y = 12;
	private static final int SKILL_HUNTER = 21;

	private Tile tile;
	private int startExperience;
	private long startTime;

	@Override
	public void run() {
		Minibot.instance().client().resetMouseIdleTime();
		if (tile == null) {
			tile = Players.local().location();
			startExperience = Game.experiences()[SKILL_HUNTER];
			startTime = System.currentTimeMillis();
		}
		Tile next = getNext();
		if (next != null) {
			Deque<GroundItem> items = Ground.findByFilter(groundItem -> {
				RSItemDefinition def = groundItem.definition();
				return groundItem.location().equals(next) && def != null && def.getName().equals("Bird snare");
			});
			GameObject obj = Objects.topAt(next);
			if (triggered(obj)) {
				if (Arrays.asList(obj.definition().getActions()).contains("Check")) {
					obj.processAction("Check");
				} else {
					obj.processAction("Dismantle");
				}
				Time.sleep(new Condition() {
					public boolean validate() {
						return Objects.topAt(next) == null && Players.local().animation() == -1;
					}
				}, Random.nextInt(1500, 2500));
			} else if (obj == null && (items == null || items.isEmpty())) {
				if (!Players.local().location().equals(next)) {
					Walking.walkTo(next);
					Time.sleep(new Condition() {
						public boolean validate() {
							return Players.local().location().equals(next);
						}
					}, Random.nextInt(2500, 4000));
				}
				Item snare = Inventory.first(item -> item.name().equals("Bird snare"));
				if (snare != null) {
					if (Players.local().location().equals(next)) {
						snare.processAction(ActionOpcodes.ITEM_ACTION_0, "Lay");
						Time.sleep(300, 400);
						if (Time.sleep(new Condition() {
							public boolean validate() {
								return Players.local().animation() == -1;
							}
						}, Random.nextInt(2750, 4000))) {
							Walking.walkTo(next.derive(0, 1));
						}
					}
				}
			} else if (items != null && !items.isEmpty()) {
				GroundItem item = items.getFirst();
				if (item != null) {
					item.processAction(ActionOpcodes.GROUND_ITEM_ACTION_3, "Lay");
					Time.sleep(new Condition() {
						public boolean validate() {
							return Objects.topAt(next) != null && Players.local().animation() == -1;
						}
					}, Random.nextInt(2750, 4000));
				}
			}
		} else {
			Inventory.dropAll(item -> {
                String name = item.name();
                return name != null && (name.equals("Raw bird meat") || name.equals("Bones"));
            });
			Time.sleep(50);
		}
	}

	private boolean triggered(GameObject obj) {
		if (obj == null)
			return false;
		RSObjectDefinition def = obj.definition();
		if (def == null) {
            return false;
        }
		String[] actions = def.getActions();
		if (actions == null) {
            return false;
        }
        List<String> act = Arrays.asList(actions);
        return act.contains("Check") || !act.contains("Investigate");
	}

	/**
	 * @return the maximum number of traps that can be used at current level.
	 */
	private int trapSize() {
		return Game.realLevels()[SKILL_HUNTER] / 20 + 1;
	}

	private Tile[] traps() {
		switch (trapSize()) {
			case 1: {
				return new Tile[]{tile};
			}
			case 2: {
				return new Tile[]{tile.derive(-1, 0), tile.derive(1, 0)};
			}
			case 3: {
				return new Tile[]{tile.derive(-1, 0), tile.derive(0, -1), tile.derive(1, 0)};
			}
			case 4: {
				return new Tile[]{tile.derive(-1, 0), tile.derive(0, -1), tile.derive(1, 0),
						tile.derive(0, 1)};
			}
			case 5: {
				return new Tile[]{tile.derive(-1, 1), tile.derive(-1, -1), tile,
						tile.derive(1, -1), tile.derive(1, 1),};
			}
		}
		return new Tile[]{};
	}

	public Tile getNext() {
		// No trap
        Tile[] traps = traps();
        for (Tile tile : traps) {
			GameObject obj = Objects.topAt(tile);
			if (obj == null) {
				return tile;
			}
		}
        // Triggered
		for (Tile tile : traps) {
			GameObject obj = Objects.topAt(tile);
			if (obj != null && triggered(obj)) {
				return tile;
			}
		}
        return null;
	}

	public int hourly(int val, long difference) {
		return (int) Math.ceil(val * 3600000D / difference);
	}

	public static String format(long millis) {
		return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
				TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
	}

	@Override
	public void render(Graphics2D g) {
		long diff = System.currentTimeMillis() - startTime;
		int gain = Game.experiences()[SKILL_HUNTER] - startExperience;
		g.drawString("Time: " + format(diff), 10, POS_Y);
		g.drawString("Exp: " + gain, 10, POS_Y + 15);
		g.drawString("Exp/H: " + hourly(gain, diff), 10, POS_Y + 30);
	}
}