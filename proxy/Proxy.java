package com.dyn.betterachievements.proxy;

import java.io.File;

public interface Proxy {
	public void initConfig(File file);

	public void registerHandlers();
}