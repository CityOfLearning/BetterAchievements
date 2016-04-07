package com.dyn.betterachievements;

import java.util.Map;

import com.dyn.betterachievements.proxy.Proxy;
import com.dyn.betterachievements.reference.MetaData;
import com.dyn.betterachievements.reference.Reference;
import com.dyn.betterachievements.registry.AchievementRegistry;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION_FULL, guiFactory = Reference.MOD_GUI_FACTORY)
public class BetterAchievements {
	@SidedProxy(clientSide = Reference.CLIENT_PROXY, serverSide = Reference.SERVER_PROXY)
	public static Proxy proxy;

	@Mod.Instance(Reference.MOD_ID)
	public BetterAchievements instance;

	@Mod.Metadata(Reference.MOD_ID)
	public ModMetadata metadata;

	@Mod.EventHandler
	public void imcCallback(FMLInterModComms.IMCEvent event) {
		for (FMLInterModComms.IMCMessage message : event.getMessages()) {
			if (message.isItemStackMessage()) {
				AchievementRegistry.instance().registerIcon(message.key, message.getItemStackValue(), false);
			}
		}
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.registerHandlers();
	}

	@NetworkCheckHandler
	public final boolean networkCheck(Map<String, String> remoteVersions, Side side) {
		return true;
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		metadata = MetaData.init(metadata);
		proxy.initConfig(event.getSuggestedConfigurationFile());
	}
}
