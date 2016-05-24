package com.dyn.betterachievements.util;

import com.dyn.DYNServerMod;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;

public class LogHelper {
	public static LogHelper instance() {
		return new LogHelper(Loader.instance().activeModContainer().getModId());
	}

	private boolean debug;

	public LogHelper(String id) {
		// DYNServerMod.logger = LogManager.getLogger(id);
	}

	public void crash(Exception e, String message) {
		FMLCommonHandler.instance().raiseException(e, message, true);
	}

	public void debug(Object obj) {
		if (debug) {
			DYNServerMod.logger.info(obj);
		} else {
			DYNServerMod.logger.debug(obj);
		}
	}

	public void error(Exception e, String message) {
		FMLCommonHandler.instance().raiseException(e, message, false);
	}

	public void info(Object obj) {
		DYNServerMod.logger.info(obj);
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void warn(Object obj) {
		DYNServerMod.logger.warn(obj);
	}
}
