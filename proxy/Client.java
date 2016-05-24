package com.dyn.betterachievements.proxy;

import java.io.File;

import com.dyn.betterachievements.handler.ConfigHandler;
import com.dyn.betterachievements.handler.GuiOpenHandler;
import com.dyn.betterachievements.handler.SaveHandler;

import net.minecraftforge.common.MinecraftForge;

public class Client implements Proxy {

	@Override
	public void initConfig(File file) {
		ConfigHandler.init(file);
		MinecraftForge.EVENT_BUS.register(new ConfigHandler());
	}

	@Override
	public void registerHandlers() {
		MinecraftForge.EVENT_BUS.register(new GuiOpenHandler());
		MinecraftForge.EVENT_BUS.register(new SaveHandler());
	}
}