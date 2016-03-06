package com.dyn.betterachievements.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;

public class LogHelper {
	public static LogHelper instance() {
		return new LogHelper(Loader.instance().activeModContainer().getModId());
	}

	private Logger log;

	private boolean debug;

	public LogHelper(String id) {
		this.log = LogManager.getLogger(id);
	}

	public void crash(Exception e, String message) {
		FMLCommonHandler.instance().raiseException(e, message, true);
	}

	public void debug(Object obj) {
		if (this.debug) {
			this.log.info(obj);
		} else {
			this.log.debug(obj);
		}
	}

	public void error(Exception e, String message) {
		FMLCommonHandler.instance().raiseException(e, message, false);
	}

	public void info(Object obj) {
		this.log.info(obj);
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void warn(Object obj) {
		this.log.warn(obj);
	}
}
