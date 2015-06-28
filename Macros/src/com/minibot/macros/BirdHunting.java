package com.minibot.macros;

import com.minibot.Minibot;
import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.method.Game;
import com.minibot.api.method.Ground;
import com.minibot.api.method.Inventory;
import com.minibot.api.method.Objects;
import com.minibot.api.method.Players;
import com.minibot.api.method.Skills;
import com.minibot.api.method.Walking;
import com.minibot.api.util.Random;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.util.filter.Filter;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.locatable.GameObject;
import com.minibot.api.wrapper.locatable.GroundItem;
import com.minibot.api.wrapper.locatable.Player;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;
import com.minibot.client.natives.RSItemDefinition;
import com.minibot.client.natives.RSObjectDefinition;

import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

/**
 * Calculations for red chins only.
 *
 * @author Tim Dekker
 * @since 5/11/15
 */
@Manifest(name = "BirdHunter", author = "Swipe", version = "1.0.0", description = "Hunts Birds")
public class BirdHunting extends Macro implements Renderable {

	private static final int POS_Y = 12;
    private int startExperience;

	private Tile tile;

	public static final Filter<GameObject> SNARE_FILTER = o -> {
		String name = o.name();
		return name != null && name.equals("Bird snare");
	};

    @Override
    public void atStart() {
        if (!Game.playing()) {
            interrupt();
        }
        Player local = Players.local();
        if (local != null) {
            tile = local.location();
            startExperience = Game.experiences()[Skills.HUNTER];
        }
    }

	@Override
	public void run() {
		Minibot.instance().client().resetMouseIdleTime();
		Tile next = getNext();
		if (next != null) {
			Deque<GroundItem> items = Ground.findByFilter(groundItem -> {
				RSItemDefinition def = groundItem.definition();
				return groundItem.location().equals(next) && def != null && def.getName().equals("Bird snare");
			});
			GameObject obj = objectAt(next, SNARE_FILTER);
			if (triggered(obj)) {
				if (Arrays.asList(obj.definition().getActions()).contains("Check")) {
					obj.processAction("Check");
				} else {
					obj.processAction("Dismantle");
				}
				Time.sleep(() -> objectAt(next, SNARE_FILTER) == null && Players.local().animation() == -1,
						Random.nextInt(1500, 2500));
			} else if (obj == null && (items == null || items.isEmpty())) {
				if (!Players.local().location().equals(next)) {
					Walking.walkTo(next);
					Time.sleep(() -> {
                        return Players.local().location().equals(next);
                    }, Random.nextInt(2500, 4000));
				}
				Item snare = Inventory.first(item -> item.name().equals("Bird snare"));
				if (snare != null) {
					if (Players.local().location().equals(next)) {
						snare.processAction(ActionOpcodes.ITEM_ACTION_0, "Lay");
						Time.sleep(300, 400);
						if (Time.sleep(() -> Players.local().animation() == -1, Random.nextInt(2750, 4000))) {
							Walking.walkTo(next.derive(0, 1));
						}
					}
				}
			} else if (items != null && !items.isEmpty()) {
				GroundItem item = items.getFirst();
				if (item != null) {
					item.processAction(ActionOpcodes.GROUND_ITEM_ACTION_3, "Lay");
					Time.sleep(() -> Objects.topAt(next) != null && Players.local().animation() == -1, Random.nextInt(2750, 4000));
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
		return Game.realLevels()[Skills.HUNTER] / 20 + 1;
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

	private GameObject objectAt(Tile t, Filter<GameObject> filter) {
		GameObject[] objects = Objects.allAt(t);
		if (objects == null)
			return null;
		for (GameObject obj : objects) {
			if (obj != null && filter.accept(obj))
				return obj;
		}
		return null;
	}

	public Tile getNext() {
		// No trap
		Tile[] traps = traps();
        for (Tile tile : traps) {
			GameObject obj = objectAt(tile, SNARE_FILTER);
			if (obj == null) {
				return tile;
			}
		}
        // Triggered
		for (Tile tile : traps) {
			GameObject obj = objectAt(tile, SNARE_FILTER);
			if (obj != null && triggered(obj)) {
				return tile;
			}
		}
        return null;
	}

	@Override
	public void render(Graphics2D g) {
		int gained = Game.experiences()[Skills.HUNTER] - startExperience;
		g.drawString("Time: " + Time.format(runtime()), 10, POS_Y);
		g.drawString("Exp: " + gained, 10, POS_Y + 15);
		g.drawString("Exp/H: " + hourly(gained), 10, POS_Y + 30);
	}
}