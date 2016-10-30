package org.powerbot.script.rt4;

import java.util.concurrent.Callable;

import org.powerbot.script.Condition;
import org.powerbot.script.StringUtils;

/**
 * Combat
 * A utility class with methods for parsing widget and varpbit values.
 */
public class Combat extends ClientAccessor {
	public Combat(final ClientContext ctx) {
		super(ctx);
	}

	/**
	 * Gets your health as a ratio / percentage out of 100
	 * @return the percentage of health you have out of 100 (0 - 100)
	 */
	public int healthRatio() {
		return (int) ((health() / (double) maxHealth()) * 100.0);
	}
	
	/**
	 * Gets your player's current hitpoints
	 * @return your current hitpoints corresponding to your hp orb
	 */
	public int health() {
		return ctx.skills.level(Constants.SKILLS_HITPOINTS);
	}
	
	/**
	 * Gets your player's max hitpoints as shown in the skills tab
	 * @return max hitpoints
	 */
	public int maxHealth() {
		return ctx.skills.realLevel(Constants.SKILLS_HITPOINTS);
	}

	public int specialPercentage() {
		return ctx.varpbits.varpbit(300) / 10;
	}

	public boolean specialAttack() {
		return ctx.varpbits.varpbit(301) == 1;
	}

	public boolean specialAttack(final boolean select) {
		if (specialAttack() == select) {
			return true;
		}

		Component c = null;
		for (final Component comp : ctx.widgets.widget(593).components()) {
			if (comp.text().contains("Special attack:")) {
				c = comp;
				break;
			}
		}

		final int current = specialPercentage();
		return c != null && ctx.game.tab(Game.Tab.ATTACK) && c.visible() && c.click() && Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return specialAttack() == select || specialPercentage() != current;
			}
		}, 300, 6);
	}
    
    	/**
	 * Gets the wilderness level that you're in
	 * @return your player's wilderness level, 0 if you're not in the wilderness
	 */
	public int wildernessLevel() {
		try {
			return Integer.parseInt(ctx.widgets.component(90, 33).text().replace("Level:", "").trim());
		} catch (NumberFormatException e) {
			return 0;
		}	
	}
	
	/**
	 * Checks if auto retaliate is on or not
	 * @return true = on, false = off
	 */
	public boolean autoRetaliate() {
		return ctx.varpbits.varpbit(172) == 0;
	}
	
	/**
	 * Attempts to switch your auto retaliate status
	 * @param true = enable auto retaliate, false = disable auto retaliate
	 * @return if the switch has been successful
	 */
	public boolean autoRetaliate(final boolean enable) {
		if (autoRetaliate() == enable)
			return true;
		Component c = ctx.widgets.component(593, 27);
		return c != null && ctx.game.tab(Tab.ATTACK) && c.visible() && c.click() && Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return autoRetaliate() == enable;
			}
		}, 300, 6);
	}
	
	/**
	 * Gets the name of your player's weapon
	 * @return wielded weapon's name
	 */
	public String weaponName() {
		return ctx.widgets.component(593, 1).text().trim();
	}
	
	/**
	 * Gets the available attack actions (reassembles attack options text)
	 * @return an array of actions, length is 0 if there are no actions
	 */
	public String[] attackActions() {
		ArrayList<String> arr = new ArrayList<String>();
		if (ctx.game.tab(Tab.ATTACK)) {
			Component c = null;
			for (int cid = 6; cid <= 18; cid += 4) {
				if (!(c = ctx.widgets.component(593, cid)).visible())
					continue;
				if (c.text().length() > 0)
					arr.add(c.text().trim());
			}
		}
		return arr.toArray(new String[arr.size()]);
	}
	
	/**
	 * Gets the selected combat style index
	 * @return combat style index
	 */
	public int styleIndex() {
		return ctx.varpbits.varpbit(43);
	}
	
	/**
	 * Changes your current attack style to said index
	 * @param index - index to select (0-3)
	 * @return index has been successfully changed
	 */
	public boolean styleIndex(final int index) {
		if (styleIndex() == index)
			return true;
		if (index > 3 || index < 0)
			return true;
		Component c = ctx.widgets.component(593, 4 + (index * 4));
		return c != null && ctx.game.tab(Tab.ATTACK) && c.visible() && c.click() && Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return styleIndex() == index;
			}
		}, 300, 6);
	}
    
}
