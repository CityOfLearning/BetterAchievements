package com.dyn.betterachievements.proxy;

import java.io.File;

import org.lwjgl.input.Keyboard;

import com.dyn.achievements.gui.Search;
import com.dyn.betterachievements.handler.ConfigHandler;
import com.dyn.betterachievements.handler.GuiOpenHandler;
import com.dyn.betterachievements.handler.SaveHandler;
import com.rabbit.gui.GuiFoundation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class Client implements Proxy {

	@Override
	public void initConfig(File file) {
		ConfigHandler.init(file);
		FMLCommonHandler.instance().bus().register(new ConfigHandler());
	}

	@Override
	public void registerHandlers() {
		MinecraftForge.EVENT_BUS.register(new GuiOpenHandler());
		MinecraftForge.EVENT_BUS.register(new SaveHandler());
	}
}