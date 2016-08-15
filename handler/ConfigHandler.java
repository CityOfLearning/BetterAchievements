package com.dyn.betterachievements.handler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.dyn.betterachievements.gui.GuiBetterAchievements;
import com.dyn.betterachievements.util.ColourHelper;

import net.minecraft.util.StatCollector;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.IConfigElement;

public class ConfigHandler {
	public static Configuration config;

	public static List<IConfigElement> getConfigElements() {
		List<IConfigElement> list = new ArrayList<IConfigElement>();
		list.addAll(new ConfigElement(config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements());
		return list;
	}

	public static void init(File file) {
		if (config == null) {
			config = new Configuration(file);
			loadConfig();
		}
	}

	private static void loadConfig() {
		Property prop;
		String colourCode;

		prop = config.get(Configuration.CATEGORY_GENERAL, "scrollButtons", true);
		prop.comment = StatCollector.translateToLocal("com.dyn.betterachievements.config.scrollButtons.desc");
		prop.setLanguageKey("com.dyn.betterachievements.config.scrollButtons");
		GuiBetterAchievements.scrollButtons = prop.getBoolean();

		prop = config.get(Configuration.CATEGORY_GENERAL, "cantUnlockArrowColour", "#000000");
		prop.comment = StatCollector.translateToLocal("com.dyn.betterachievements.config.cantUnlockArrowColour.desc");
		prop.setLanguageKey("com.dyn.betterachievements.config.cantUnlockArrowColour");
		colourCode = prop.getString();
		if (colourCode.startsWith("#")) {
			GuiBetterAchievements.colourCantUnlockRainbow = false;
			GuiBetterAchievements.colourCantUnlock = ColourHelper.RGB(colourCode);
		} else if (colourCode.startsWith("rainbow")) {
			GuiBetterAchievements.colourCantUnlockRainbow = true;
			GuiBetterAchievements.colourCantUnlockRainbowSettings = ColourHelper.getRainbowSettings(colourCode);
		}

		prop = config.get(Configuration.CATEGORY_GENERAL, "canUnlockArrowColour", "#00FF00");
		prop.comment = StatCollector.translateToLocal("com.dyn.betterachievements.config.canUnlockArrowColour.desc");
		prop.setLanguageKey("com.dyn.betterachievements.config.canUnlockArrowColour");
		colourCode = prop.getString();
		if (colourCode.startsWith("#")) {
			GuiBetterAchievements.colourCanUnlockRainbow = false;
			GuiBetterAchievements.colourCanUnlock = ColourHelper.RGB(colourCode);
		} else if (colourCode.startsWith("rainbow")) {
			GuiBetterAchievements.colourCanUnlockRainbow = true;
			GuiBetterAchievements.colourCanUnlockRainbowSettings = ColourHelper.getRainbowSettings(colourCode);
		}

		prop = config.get(Configuration.CATEGORY_GENERAL, "completeArrowColour", "#A0A0A0");
		prop.comment = StatCollector.translateToLocal("com.dyn.betterachievements.config.completeArrowColour.desc");
		prop.setLanguageKey("com.dyn.betterachievements.config.completeArrowColour");
		colourCode = prop.getString();
		if (colourCode.startsWith("#")) {
			GuiBetterAchievements.colourUnlockedRainbow = false;
			GuiBetterAchievements.colourUnlocked = ColourHelper.RGB(colourCode);
		} else if (colourCode.startsWith("rainbow")) {
			GuiBetterAchievements.colourUnlockedRainbow = true;
			GuiBetterAchievements.colourUnlockedRainbowSettings = ColourHelper.getRainbowSettings(colourCode);
		}

		prop = config.get(Configuration.CATEGORY_GENERAL, "userColourOverride", false);
		prop.comment = StatCollector.translateToLocal("com.dyn.betterachievements.config.userColourOverride.desc");
		prop.setLanguageKey("com.dyn.betterachievements.config.userColourOverride");
		GuiBetterAchievements.userColourOverride = prop.getBoolean();

		if (config.hasChanged()) {
			config.save();
		}
	}
}
