package com.dyn.betterachievements;

import com.dyn.betterachievements.proxy.Proxy;
import com.dyn.betterachievements.reference.MetaData;
import com.dyn.betterachievements.reference.Reference;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION_FULL, guiFactory = Reference.MOD_GUI_FACTORY, dependencies = "required-after:dyn|server")
public class BetterAchievements {

	@SidedProxy(clientSide = Reference.CLIENT_PROXY, serverSide = Reference.SERVER_PROXY)
	public static Proxy proxy;

	@Mod.Instance(Reference.MOD_ID)
	public static BetterAchievements instance;

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.registerHandlers();
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MetaData.init(event.getModMetadata());
		proxy.initConfig(event.getSuggestedConfigurationFile());
	}
}
