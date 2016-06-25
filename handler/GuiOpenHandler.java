package com.dyn.betterachievements.handler;

import java.lang.reflect.Field;

import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class GuiOpenHandler {
	public static Field prevScreen, currentPage;

	static {
		try {
			prevScreen = ReflectionHelper.findField(GuiAchievements.class, "parentScreen", "field_146562_a");
			prevScreen.setAccessible(true);
			currentPage = ReflectionHelper.findField(GuiAchievements.class, "currentPage");
			currentPage.setAccessible(true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
