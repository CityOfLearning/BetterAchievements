package com.dyn.betterachievements.proxy;

import java.io.File;

import com.dyn.betterachievements.handler.ConfigHandler;
import com.dyn.betterachievements.handler.GuiOpenHandler;

import net.minecraftforge.common.MinecraftForge;

public class Client implements Proxy {

	@Override
	public void initConfig(File file) {
		ConfigHandler.init(file);
	}

	@Override
	public void registerHandlers() {
		MinecraftForge.EVENT_BUS.register(new GuiOpenHandler());
	}
}