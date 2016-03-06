package com.dyn.betterachievements.handler;

import com.dyn.betterachievements.registry.AchievementRegistry;

import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SaveHandler {
	public static String[] userSetIcons = new String[0];

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
		AchievementRegistry.instance().setUserSetIcons(userSetIcons);
	}

	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload event) {
		ConfigHandler.saveUserSetIcons();
	}
}
